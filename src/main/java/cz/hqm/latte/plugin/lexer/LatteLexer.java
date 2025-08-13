package cz.hqm.latte.plugin.lexer;

import com.intellij.lexer.LayeredLexer;
import com.intellij.lexer.HtmlLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlTokenType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lexer for Latte template language.
 * Handles Latte-specific syntax like macros.
 */
public class LatteLexer extends LayeredLexer {
    
    // Current syntax mode
    private LatteSyntaxMode syntaxMode = LatteSyntaxMode.DEFAULT;
    
    // Stack of previous syntax modes (used to restore the mode after {/syntax})
    private Stack<LatteSyntaxMode> syntaxModeStack = new Stack<>();
    
    // Pattern for matching syntax tag parameters - {syntax double} or {syntax off}
    private static final Pattern SYNTAX_PARAM_PATTERN = Pattern.compile("\\{syntax\\s+([a-zA-Z0-9_]+)\\}");
    // Pattern for matching end syntax tag - {/syntax}
    private static final Pattern SYNTAX_END_PATTERN = Pattern.compile("\\{/syntax\\}");
    
    // Pattern for matching n:syntax attribute - n:syntax="double" or n:syntax="off" or n:syntax=double
    // This allows for both quoted and unquoted attribute values
    private static final Pattern N_SYNTAX_PATTERN = Pattern.compile("n:syntax\\s*=\\s*['\"]?([a-zA-Z0-9_]+)['\"]?");
    
    // Internal flag to track when we are at an n:syntax attribute name
    private boolean nSyntaxAttributeSeen = false;
    
    public LatteLexer() {
        super(new HtmlLexer());
        
        // Register Latte macro lexer with a reference to this lexer
        registerSelfStoppingLayer(
            new LatteMacroLexer(this),
            new IElementType[] { LatteTokenTypes.LATTE_MACRO_START },
            new IElementType[] { LatteTokenTypes.LATTE_MACRO_END }
        );
        
        // Keep registration of attribute layer for future use (it may be activated in environments
        // where custom tokens are provided). Tests relying on LatteLexer will use XML tokens path below.
        LatteAttributeLexer attributeLexer = new LatteAttributeLexer(this);
        registerSelfStoppingLayer(
            attributeLexer,
            new IElementType[] { LatteTokenTypes.LATTE_ATTRIBUTE_START },
            new IElementType[] { LatteTokenTypes.LATTE_ATTRIBUTE_END }
        );
    }
    
    /**
     * Gets the current syntax mode.
     *
     * @return The current syntax mode
     */
    public LatteSyntaxMode getSyntaxMode() {
        return syntaxMode;
    }
    
    /**
     * Sets the syntax mode based on the parameter and stores the previous mode.
     * This method is called when processing {syntax} tags or n:syntax attributes.
     * 
     * The syntax mode affects how the lexer processes Latte macros:
     * - DEFAULT: Macros are delimited by single braces {macro}
     * - DOUBLE: Macros are delimited by double braces {{macro}}
     * - OFF: Latte syntax processing is disabled (except for {/syntax})
     *
     * @param parameter The syntax mode parameter ("double", "off", or any other value for DEFAULT)
     */
    public void setSyntaxMode(String parameter) {
        // Push the current mode onto the stack before changing it
        // This allows us to restore the previous mode when {/syntax} is encountered
        syntaxModeStack.push(syntaxMode);
        
        if ("double".equalsIgnoreCase(parameter)) {
            // Double braces mode: {{macro}}
            syntaxMode = LatteSyntaxMode.DOUBLE;
        } else if ("off".equalsIgnoreCase(parameter)) {
            // Syntax off mode: Latte macros are treated as plain text
            syntaxMode = LatteSyntaxMode.OFF;
        } else {
            // Default to DEFAULT mode for any other parameter
            // Single braces mode: {macro}
            syntaxMode = LatteSyntaxMode.DEFAULT;
        }
    }
    
    /**
     * Processes the given text to update the syntax mode if a syntax tag or n:syntax attribute is found.
     * This method handles three different cases:
     * 1. {syntax off} or {syntax double} - Changes the syntax mode and stores the previous mode
     * 2. {/syntax} - Restores the previous syntax mode
     * 3. n:syntax="off" or n:syntax="double" - Changes the syntax mode for the HTML element
     *
     * @param text The text to process
     */
    public void processSyntaxTags(String text) {
        // Case 1: Check for {syntax off} or {syntax double} tag
        Matcher syntaxMatcher = SYNTAX_PARAM_PATTERN.matcher(text);
        if (syntaxMatcher.find()) {
            String parameter = syntaxMatcher.group(1);
            setSyntaxMode(parameter);
            return;
        }
        
        // Case 2: Check for {/syntax} tag
        Matcher endSyntaxMatcher = SYNTAX_END_PATTERN.matcher(text);
        if (endSyntaxMatcher.find()) {
            if (!syntaxModeStack.isEmpty()) {
                syntaxMode = syntaxModeStack.pop();
            } else {
                syntaxMode = LatteSyntaxMode.DEFAULT;
            }
            return;
        }
        
        // Case 3: Check for inline n:syntax attribute pattern in the same token
        Matcher nSyntaxMatcher = N_SYNTAX_PATTERN.matcher(text);
        if (nSyntaxMatcher.find()) {
            String parameter = nSyntaxMatcher.group(1);
            setSyntaxMode(parameter);
        }
    }
    
    @Nullable
    @Override
    public IElementType getTokenType() {
        return super.getTokenType();
    }
    
    @Override
    public void advance() {
        // Inspect current token for XML-based detection of n:syntax name/value
        IElementType type = super.getTokenType();
        CharSequence tokenSequence = getTokenSequence();
        if (type != null && tokenSequence != null) {
            // Detect the n:syntax attribute name
            if (type == XmlTokenType.XML_NAME && "n:syntax".contentEquals(tokenSequence)) {
                nSyntaxAttributeSeen = true;
            }
            // When we see the value token for an n:syntax attribute, set the mode
            else if (nSyntaxAttributeSeen && type == XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN) {
                String raw = tokenSequence.toString();
                String value = stripQuotes(raw).trim();
                if (!value.isEmpty()) {
                    setSyntaxMode(value);
                }
                nSyntaxAttributeSeen = false; // reset flag after processing value
            }
        }
        
        // Also process macro and inline patterns within the current token
        if (tokenSequence != null && tokenSequence.length() > 0) {
            processSyntaxTags(tokenSequence.toString());
        }
        
        super.advance();
    }
    
    private static String stripQuotes(String s) {
        if (s == null || s.length() < 2) return s == null ? "" : s;
        char first = s.charAt(0);
        char last = s.charAt(s.length() - 1);
        if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }
    
    @NotNull
    @Override
    public CharSequence getTokenSequence() {
        return super.getTokenSequence();
    }
    
    /**
     * Resets the lexer state.
     * This method is called when a cached lexer instance is reused.
     * It resets the syntax mode to DEFAULT and clears the syntax mode stack.
     */
    public void reset() {
        syntaxMode = LatteSyntaxMode.DEFAULT;
        syntaxModeStack.clear();
        nSyntaxAttributeSeen = false;
        
        // Reset the base lexer
        super.start("", 0, 0, 0);
    }
}