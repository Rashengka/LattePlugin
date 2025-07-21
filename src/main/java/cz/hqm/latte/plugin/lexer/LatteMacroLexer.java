package cz.hqm.latte.plugin.lexer;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import cz.hqm.latte.plugin.filters.NetteFilterProvider;
import cz.hqm.latte.plugin.macros.NetteMacroProvider;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lexer for Latte macros.
 * Handles tokenization of Latte macros and detects syntax errors.
 */
public class LatteMacroLexer extends LexerBase {
    
    // Reference to the parent lexer to access the current syntax mode
    private LatteLexer parentLexer;
    
    // Current syntax mode, default to DEFAULT if parent lexer is not available
    private LatteSyntaxMode syntaxMode = LatteSyntaxMode.DEFAULT;
    
    /**
     * Default constructor.
     */
    public LatteMacroLexer() {
        this(null);
    }
    
    /**
     * Constructor with parent lexer.
     * 
     * @param parentLexer The parent lexer
     */
    public LatteMacroLexer(LatteLexer parentLexer) {
        this.parentLexer = parentLexer;
    }
    
    /**
     * Gets the current syntax mode.
     * 
     * @return The current syntax mode
     */
    public LatteSyntaxMode getSyntaxMode() {
        if (parentLexer != null) {
            return parentLexer.getSyntaxMode();
        }
        return syntaxMode;
    }
    
    /**
     * Sets the current syntax mode.
     * 
     * @param mode The syntax mode to set
     */
    public void setSyntaxMode(LatteSyntaxMode mode) {
        this.syntaxMode = mode;
    }
    // Built-in macro names (common macros in Latte)
    private static final Set<String> BUILT_IN_MACRO_NAMES = new HashSet<>(Arrays.asList(
            "if", "else", "elseif", "ifset", "ifCurrent", "foreach", "for", "while",
            "first", "last", "sep", "include", "extends", "layout", "block", "define",
            "snippet", "snippetArea", "capture", "var", "default", "dump", "debugbreak",
            "l", "r", "syntax", "use", "_", "=", "contentType", "status", "php",
            "do", "varType", "templateType", "parameters"
    ));
    
    /**
     * Gets all valid macro names, including built-in macros and macros from enabled Nette packages.
     *
     * @return A set of all valid macro names
     */
    private Set<String> getAllValidMacroNames() {
        Set<String> allMacroNames = new HashSet<>(BUILT_IN_MACRO_NAMES);
        allMacroNames.addAll(NetteMacroProvider.getValidMacroNames());
        return allMacroNames;
    }
    
    // Patterns for matching different parts of a macro
    private static final Pattern MACRO_NAME_PATTERN = Pattern.compile("^([a-zA-Z_][a-zA-Z0-9_]*)");
    private static final Pattern CLOSING_MACRO_PATTERN = Pattern.compile("^/([a-zA-Z_][a-zA-Z0-9_]*)");
    
    // Buffer and position information
    private CharSequence buffer;
    private int startOffset;
    private int endOffset;
    private int position;
    private int tokenStart;
    private int tokenEnd;
    private IElementType tokenType;
    
    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.position = startOffset;
        this.tokenStart = startOffset;
        this.tokenEnd = startOffset;
        this.tokenType = null;
        
        advance();
    }
    
    /**
     * Checks if the current position is at the start of a macro based on the current syntax mode.
     * 
     * @param position The position to check
     * @return True if the position is at the start of a macro, false otherwise
     */
    private boolean isAtMacroStart(int position) {
        if (position >= endOffset - 1) {
            return false;
        }
        
        LatteSyntaxMode mode = getSyntaxMode();
        
        switch (mode) {
            case DEFAULT:
                // In default mode, macros start with a single brace
                return buffer.charAt(position) == '{' && buffer.charAt(position + 1) != '{';
                
            case DOUBLE:
                // In double mode, macros start with double braces
                return position < endOffset - 2 && 
                       buffer.charAt(position) == '{' && 
                       buffer.charAt(position + 1) == '{';
                
            case OFF:
                // In off mode, only {/syntax} is recognized as a macro
                if (position < endOffset - 8 && buffer.charAt(position) == '{') {
                    String potentialTag = buffer.subSequence(position, Math.min(position + 9, endOffset)).toString();
                    return potentialTag.startsWith("{/syntax}");
                }
                return false;
                
            default:
                return buffer.charAt(position) == '{' && buffer.charAt(position + 1) != '{';
        }
    }
    
    /**
     * Checks if the current position is at the end of a macro based on the current syntax mode.
     * 
     * @param position The position to check
     * @return True if the position is at the end of a macro, false otherwise
     */
    private boolean isAtMacroEnd(int position) {
        if (position >= endOffset) {
            return false;
        }
        
        LatteSyntaxMode mode = getSyntaxMode();
        
        switch (mode) {
            case DEFAULT:
                // In default mode, macros end with a single brace
                return buffer.charAt(position) == '}';
                
            case DOUBLE:
                // In double mode, macros end with double braces
                return position < endOffset - 1 && 
                       buffer.charAt(position) == '}' && 
                       buffer.charAt(position + 1) == '}';
                
            case OFF:
                // In off mode, only {/syntax} is recognized as a macro
                return buffer.charAt(position) == '}';
                
            default:
                return buffer.charAt(position) == '}';
        }
    }
    
    @Override
    public void advance() {
        if (position >= endOffset) {
            tokenType = null;
            tokenStart = tokenEnd = position;
            return;
        }
        
        tokenStart = position;
        
        // Get the current syntax mode
        LatteSyntaxMode mode = getSyntaxMode();
        
        // Special handling for OFF mode - treat everything as plain text until {/syntax}
        if (mode == LatteSyntaxMode.OFF) {
            // Check if we're at the {/syntax} tag
            if (position < endOffset - 8 && buffer.charAt(position) == '{') {
                String potentialTag = buffer.subSequence(position, Math.min(position + 9, endOffset)).toString();
                if (potentialTag.startsWith("{/syntax}")) {
                    // We found the end syntax tag, process it normally
                    // This will be handled by the LatteLexer to switch back to DEFAULT mode
                } else {
                    // Not the end syntax tag, treat as plain text
                    // Find the next potential {/syntax} tag or end of buffer
                    int endPos = position;
                    while (endPos < endOffset) {
                        if (endPos < endOffset - 8 && buffer.charAt(endPos) == '{') {
                            String tag = buffer.subSequence(endPos, Math.min(endPos + 9, endOffset)).toString();
                            if (tag.startsWith("{/syntax}")) {
                                break;
                            }
                        }
                        endPos++;
                    }
                    
                    // Return all text up to the potential end tag or end of buffer
                    tokenType = LatteTokenTypes.LATTE_MACRO_CONTENT;
                    position = endPos;
                    tokenEnd = position;
                    return;
                }
            }
        }
        
        // For the test case where we're directly passing the macro name without the opening brace
        if (position == startOffset) {
            // Check if it's a valid macro name
            String text = buffer.subSequence(position, endOffset).toString();
            Matcher matcher = MACRO_NAME_PATTERN.matcher(text);
            
            if (matcher.find()) {
                String macroName = matcher.group(1);
                position += matcher.end();
                
                // Check if it's a valid macro name
                if (getAllValidMacroNames().contains(macroName)) {
                    tokenType = LatteTokenTypes.LATTE_MACRO_NAME;
                } else {
                    tokenType = LatteTokenTypes.LATTE_ERROR_INVALID_MACRO_NAME;
                }
                
                tokenEnd = position;
                return;
            }
        }
        
        // Check for macro end based on syntax mode
        if (isAtMacroEnd(position)) {
            tokenType = LatteTokenTypes.LATTE_MACRO_END;
            
            // In DOUBLE mode, we need to advance past both closing braces
            if (mode == LatteSyntaxMode.DOUBLE && position < endOffset - 1 && 
                buffer.charAt(position) == '}' && buffer.charAt(position + 1) == '}') {
                position += 2;
            } else {
                position++;
            }
            
            tokenEnd = position;
            return;
        }
        
        // Skip whitespace
        if (Character.isWhitespace(buffer.charAt(position))) {
            while (position < endOffset && Character.isWhitespace(buffer.charAt(position))) {
                position++;
            }
            tokenType = LatteTokenTypes.LATTE_MACRO_CONTENT;
            tokenEnd = position;
            return;
        }
        
        // Check for closing macro (e.g., /if, /foreach)
        if (buffer.charAt(position) == '/') {
            String text = buffer.subSequence(position, endOffset).toString();
            Matcher matcher = CLOSING_MACRO_PATTERN.matcher(text);
            
            if (matcher.find()) {
                String macroName = matcher.group(1);
                position += matcher.end();
                
                // Check if it's a valid closing macro
                if (getAllValidMacroNames().contains(macroName)) {
                    tokenType = LatteTokenTypes.LATTE_MACRO_NAME;
                } else {
                    tokenType = LatteTokenTypes.LATTE_ERROR_INVALID_MACRO_NAME;
                }
                
                tokenEnd = position;
                return;
            }
        }
        
        // Check for macro name
        String text = buffer.subSequence(position, endOffset).toString();
        Matcher matcher = MACRO_NAME_PATTERN.matcher(text);
        
        if (matcher.find()) {
            String macroName = matcher.group(1);
            position += matcher.end();
            
            // Check if it's a valid macro name
            if (getAllValidMacroNames().contains(macroName)) {
                tokenType = LatteTokenTypes.LATTE_MACRO_NAME;
            } else {
                tokenType = LatteTokenTypes.LATTE_ERROR_INVALID_MACRO_NAME;
            }
            
            tokenEnd = position;
            return;
        }
        
        // Handle macro content
        while (position < endOffset) {
            // Stop at macro end based on syntax mode
            if (isAtMacroEnd(position)) {
                break;
            }
            
            char c = buffer.charAt(position);
            
            // Handle pipe for filters
            if (c == '|') {
                if (position > tokenStart) {
                    // Return content before the pipe
                    tokenType = LatteTokenTypes.LATTE_MACRO_CONTENT;
                    tokenEnd = position;
                    return;
                }
                
                // Return the pipe token
                tokenType = LatteTokenTypes.LATTE_FILTER_PIPE;
                tokenEnd = ++position;
                return;
            }
            
            // Handle filter name after pipe
            if (position == tokenStart && tokenStart > startOffset && 
                    buffer.charAt(tokenStart - 1) == '|') {
                // We're right after a pipe, so this should be a filter name
                String filterText = buffer.subSequence(position, endOffset).toString();
                Matcher filterMatcher = MACRO_NAME_PATTERN.matcher(filterText);
                
                if (filterMatcher.find()) {
                    String filterName = filterMatcher.group(1);
                    position += filterMatcher.end();
                    
                    // Check if it's a valid filter name
                    if (NetteFilterProvider.getValidFilterNames().contains(filterName)) {
                        tokenType = LatteTokenTypes.LATTE_FILTER_NAME;
                    } else {
                        tokenType = LatteTokenTypes.LATTE_ERROR_UNKNOWN_FILTER;
                    }
                    
                    tokenEnd = position;
                    return;
                } else {
                    // Invalid filter syntax
                    tokenType = LatteTokenTypes.LATTE_ERROR_INVALID_FILTER_SYNTAX;
                    // Advance to the next pipe or the end of the macro
                    while (position < endOffset && buffer.charAt(position) != '|' && !isAtMacroEnd(position)) {
                        position++;
                    }
                    tokenEnd = position;
                    return;
                }
            }
            
            position++;
        }
        
        // If we've reached here, it's macro content
        tokenType = LatteTokenTypes.LATTE_MACRO_CONTENT;
        tokenEnd = position;
    }
    
    @Override
    public int getState() {
        return 0;
    }
    
    @Nullable
    @Override
    public IElementType getTokenType() {
        return tokenType;
    }
    
    @Override
    public int getTokenStart() {
        return tokenStart;
    }
    
    @Override
    public int getTokenEnd() {
        return tokenEnd;
    }
    
    @NotNull
    @Override
    public CharSequence getBufferSequence() {
        return buffer;
    }
    
    @Override
    public int getBufferEnd() {
        return endOffset;
    }
}