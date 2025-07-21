package cz.hqm.latte.plugin.lexer;

import com.intellij.lexer.LayeredLexer;
import com.intellij.lexer.EmptyLexer;
import com.intellij.psi.tree.IElementType;
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
    private static final Pattern N_SYNTAX_PATTERN = Pattern.compile("n:syntax\\s*=\\s*[\"']?([a-zA-Z0-9_]+)[\"']?");
    
    public LatteLexer() {
        super(new EmptyLexer());
        
        System.out.println("DEBUG: Creating LatteLexer instance: " + this);
        
        // Register Latte macro lexer with a reference to this lexer
        registerSelfStoppingLayer(
            new LatteMacroLexer(this),
            new IElementType[] { LatteTokenTypes.LATTE_MACRO_START },
            new IElementType[] { LatteTokenTypes.LATTE_MACRO_END }
        );
        
        // Register Latte n:attribute lexer with a reference to this lexer
        LatteAttributeLexer attributeLexer = new LatteAttributeLexer(this);
        System.out.println("DEBUG: Created LatteAttributeLexer with parent: " + this);
        
        registerSelfStoppingLayer(
            attributeLexer,
            new IElementType[] { LatteTokenTypes.LATTE_ATTRIBUTE_START },
            new IElementType[] { LatteTokenTypes.LATTE_ATTRIBUTE_END }
        );
        System.out.println("DEBUG: Registered LatteAttributeLexer layer");
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
        
        System.out.println("DEBUG: Set syntax mode to " + syntaxMode + ", previous mode was " + 
                          (syntaxModeStack.isEmpty() ? LatteSyntaxMode.DEFAULT : syntaxModeStack.peek()));
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
        System.out.println("DEBUG: processSyntaxTags called with text: " + text);
        
        // Case 1: Check for {syntax off} or {syntax double} tag
        Matcher syntaxMatcher = SYNTAX_PARAM_PATTERN.matcher(text);
        if (syntaxMatcher.find()) {
            // Extract the parameter (off or double)
            String parameter = syntaxMatcher.group(1);
            // Set the syntax mode and store the previous mode
            setSyntaxMode(parameter);
            System.out.println("Setting syntax mode to: " + syntaxMode + " for parameter: " + parameter);
            return;
        }
        
        // Case 2: Check for {/syntax} tag
        Matcher endSyntaxMatcher = SYNTAX_END_PATTERN.matcher(text);
        if (endSyntaxMatcher.find()) {
            // Restore the previous syntax mode from the stack
            if (!syntaxModeStack.isEmpty()) {
                syntaxMode = syntaxModeStack.pop();
                System.out.println("DEBUG: Restoring syntax mode to previous mode: " + syntaxMode);
            } else {
                // If the stack is empty, default to DEFAULT mode
                syntaxMode = LatteSyntaxMode.DEFAULT;
                System.out.println("DEBUG: Stack is empty, restoring to DEFAULT mode");
            }
            return;
        }
        
        // Case 3: Check for n:syntax attribute in HTML
        Matcher nSyntaxMatcher = N_SYNTAX_PATTERN.matcher(text);
        if (nSyntaxMatcher.find()) {
            // Extract the parameter (off or double)
            String parameter = nSyntaxMatcher.group(1);
            System.out.println("DEBUG: Found n:syntax attribute with parameter: " + parameter);
            // Set the syntax mode and store the previous mode
            setSyntaxMode(parameter);
            System.out.println("Setting syntax mode to: " + syntaxMode + " from n:syntax attribute with parameter: " + parameter);
        } else {
            System.out.println("DEBUG: No n:syntax attribute found in text");
        }
    }
    
    @Nullable
    @Override
    public IElementType getTokenType() {
        return super.getTokenType();
    }
    
    @Override
    public void advance() {
        // Get the current token sequence
        CharSequence tokenSequence = getTokenSequence();
        if (tokenSequence != null && tokenSequence.length() > 0) {
            // Process the token sequence to update the syntax mode
            System.out.println("Processing token sequence: " + tokenSequence);
            processSyntaxTags(tokenSequence.toString());
        }
        
        super.advance();
    }
    
    @NotNull
    @Override
    public CharSequence getTokenSequence() {
        return super.getTokenSequence();
    }
}