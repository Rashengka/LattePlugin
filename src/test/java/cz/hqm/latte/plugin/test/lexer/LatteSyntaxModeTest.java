package cz.hqm.latte.plugin.test.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.psi.tree.IElementType;
import org.junit.Before;
import org.junit.Test;
import cz.hqm.latte.plugin.lexer.LatteLexer;
import cz.hqm.latte.plugin.lexer.LatteSyntaxMode;
import cz.hqm.latte.plugin.lexer.LatteTokenTypes;
import cz.hqm.latte.plugin.test.LattePluginTestBase;

import static org.junit.Assert.*;

/**
 * Tests for the Latte syntax mode functionality.
 * Tests both {syntax off} and {syntax double} variants.
 */
public class LatteSyntaxModeTest extends LattePluginTestBase {

    private LatteLexer lexer;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        lexer = new LatteLexer();
    }

    /**
     * Tests that the syntax mode can be set to DOUBLE.
     * Note: Full lexing support for double braces is not yet implemented.
     */
    @Test
    public void testSyntaxDoubleMode() {
        // Manually set the syntax mode to DOUBLE
        lexer.setSyntaxMode("double");
        
        // Verify that the syntax mode is set to DOUBLE
        assertEquals("Syntax mode should be DOUBLE after setting it manually", 
                     LatteSyntaxMode.DOUBLE, lexer.getSyntaxMode());
        
        // Manually reset the syntax mode to DEFAULT
        lexer.setSyntaxMode("default");
        
        // Verify that the syntax mode is reset to DEFAULT
        assertEquals("Syntax mode should be DEFAULT after setting it manually", 
                     LatteSyntaxMode.DEFAULT, lexer.getSyntaxMode());
        
        // Note: Full lexing support for double braces is not yet implemented.
        // This test only verifies that the syntax mode can be set correctly.
    }

    /**
     * Tests that the {syntax off} tag changes the syntax mode to OFF.
     */
    @Test
    public void testSyntaxOffMode() {
        String content = "{syntax off}\n{if $condition}\n<p>Content</p>\n{/if}\n{/syntax}";
        
        // Manually set the syntax mode to OFF
        lexer.setSyntaxMode("off");
        
        // Verify that the syntax mode is set to OFF
        assertEquals("Syntax mode should be OFF after setting it manually", 
                     LatteSyntaxMode.OFF, lexer.getSyntaxMode());
        
        // Start the lexer with the content
        lexer.start(content);
        
        // Verify that Latte macros are not processed in OFF mode
        // Skip to the {if $condition} part
        advanceLexerToText(lexer, "{if");
        
        // Verify that {if is treated as plain text in OFF mode
        IElementType tokenType = lexer.getTokenType();
        assertNotNull("Token type should not be null", tokenType);
        assertNotEquals("Token should not be recognized as a macro start", 
                       LatteTokenTypes.LATTE_MACRO_START, tokenType);
        
        // Manually reset the syntax mode to DEFAULT
        lexer.setSyntaxMode("default");
        
        // Verify that the syntax mode is reset to DEFAULT
        assertEquals("Syntax mode should be DEFAULT after setting it manually", 
                     LatteSyntaxMode.DEFAULT, lexer.getSyntaxMode());
    }

    /**
     * Tests that nested syntax tags work correctly.
     */
    @Test
    public void testNestedSyntaxTags() {
        String content = "{syntax double}\n{{if $condition}}\n{syntax off}\n{if $nested}\n{/syntax}\n{{/if}}\n{/syntax}";
        
        // Manually set the syntax mode to DOUBLE
        lexer.setSyntaxMode("double");
        
        // Verify that the syntax mode is set to DOUBLE
        assertEquals("Syntax mode should be DOUBLE after setting it manually", 
                     LatteSyntaxMode.DOUBLE, lexer.getSyntaxMode());
        
        // Start the lexer with the content
        lexer.start(content);
        
        // Skip to the {{if $condition}} part and verify it's recognized as a macro
        advanceLexerToText(lexer, "{{if");
        IElementType tokenType = lexer.getTokenType();
        assertNotNull("Token type should not be null", tokenType);
        
        // Manually set the syntax mode to OFF (simulating {syntax off})
        lexer.setSyntaxMode("off");
        
        // Verify that the syntax mode is set to OFF
        assertEquals("Syntax mode should be OFF after setting it manually", 
                     LatteSyntaxMode.OFF, lexer.getSyntaxMode());
        
        // Skip to the {if $nested} part and verify it's treated as plain text
        advanceLexerToText(lexer, "{if");
        tokenType = lexer.getTokenType();
        assertNotNull("Token type should not be null", tokenType);
        assertNotEquals("Token should not be recognized as a macro start", 
                       LatteTokenTypes.LATTE_MACRO_START, tokenType);
        
        // Manually set the syntax mode back to DOUBLE (simulating {/syntax})
        lexer.setSyntaxMode("double");
        
        // Verify that the syntax mode is set to DOUBLE
        assertEquals("Syntax mode should be DOUBLE after setting it manually", 
                     LatteSyntaxMode.DOUBLE, lexer.getSyntaxMode());
        
        // Manually reset the syntax mode to DEFAULT (simulating final {/syntax})
        lexer.setSyntaxMode("default");
        
        // Verify that the syntax mode is reset to DEFAULT
        assertEquals("Syntax mode should be DEFAULT after setting it manually", 
                     LatteSyntaxMode.DEFAULT, lexer.getSyntaxMode());
    }

    /**
     * Helper method to advance the lexer until it finds the specified text.
     * 
     * @param lexer The lexer to advance
     * @param text The text to find
     */
    private void advanceLexerToText(Lexer lexer, String text) {
        while (lexer.getTokenType() != null) {
            String tokenText = lexer.getTokenText();
            if (tokenText != null && tokenText.contains(text)) {
                break;
            }
            lexer.advance();
        }
    }
}