package org.latte.plugin.lexer;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.latte.plugin.macros.NetteMacroProvider;

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
    
    @Override
    public void advance() {
        if (position >= endOffset) {
            tokenType = null;
            tokenStart = tokenEnd = position;
            return;
        }
        
        tokenStart = position;
        
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
        
        // Check for macro end
        if (buffer.charAt(position) == '}') {
            tokenType = LatteTokenTypes.LATTE_MACRO_END;
            tokenEnd = ++position;
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
            char c = buffer.charAt(position);
            
            // Stop at macro end
            if (c == '}') {
                break;
            }
            
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