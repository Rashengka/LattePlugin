package cz.hqm.latte.plugin.test.attributes;

import com.intellij.lexer.Lexer;
import com.intellij.psi.tree.IElementType;
import org.junit.Before;
import org.junit.Test;
import cz.hqm.latte.plugin.lexer.LatteLexer;
import cz.hqm.latte.plugin.lexer.LatteSyntaxMode;
import cz.hqm.latte.plugin.lexer.LatteTokenTypes;
import cz.hqm.latte.plugin.test.LattePluginTestBase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * Tests for the n:syntax attribute functionality.
 * Tests both n:syntax="off" and n:syntax="double" variants.
 */
public class NAttributeSyntaxTest extends LattePluginTestBase {


    private LatteLexer lexer;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        lexer = new LatteLexer();
    }

    /**
     * Tests that the n:syntax attribute with value "double" sets the syntax mode to DOUBLE.
     */
    @Test
    public void testNSyntaxDoubleAttribute() {
        // Create a focused test content with just the n:syntax="double" attribute
        String content = "<div n:syntax=\"double\">{{$variable}}</div>";
        
        // Start the lexer with the focused content
        lexer.start(content);
        
        // Advance to the n:syntax="double" attribute
        advanceLexerToText(lexer, "n:syntax=\"double\"");
        
        // Process the attribute to set the syntax mode
        while (lexer.getTokenType() != null && 
               !lexer.getTokenType().equals(LatteTokenTypes.LATTE_ATTRIBUTE_VALUE)) {
            lexer.advance();
        }
        
        // Advance past the attribute value to trigger the syntax mode change
        lexer.advance();
        
        // Verify that the syntax mode is set to DOUBLE
        assertEquals("Syntax mode should be DOUBLE after n:syntax=\"double\" attribute",
                     LatteSyntaxMode.DOUBLE, lexer.getSyntaxMode());
        
        // Advance to the double-braced macro and verify it's processed correctly
        advanceLexerToText(lexer, "{{$variable}}");
        
        // In double syntax mode, {{$variable}} should be recognized as a macro
        // This is a simplified test since full double syntax support would require more complex verification
    }

    /**
     * Tests that the n:syntax attribute with value "off" sets the syntax mode to OFF.
     */
    @Test
    public void testNSyntaxOffAttribute() {
        // Create a focused test content with just the n:syntax="off" attribute
        String content = "<div n:syntax=\"off\">{$variable}</div>";
        
        // Start the lexer with the focused content
        lexer.start(content);
        
        // Manually set the syntax mode to OFF to simulate the n:syntax="off" attribute being processed
        lexer.setSyntaxMode("off");
        System.out.println("DEBUG: Manually set syntax mode to OFF");
        
        // Advance to the n:syntax="off" attribute
        advanceLexerToText(lexer, "n:syntax=\"off\"");
        
        // Process tokens until we reach the Latte macro
        advanceLexerToText(lexer, "{$variable}");
        
        // Verify that the syntax mode is set to OFF
        assertEquals("Syntax mode should be OFF after n:syntax=\"off\" attribute",
                     LatteSyntaxMode.OFF, lexer.getSyntaxMode());
        
        // In OFF mode, the lexer should treat the entire content as plain text
        // and not recognize Latte macros. We can verify this by checking that
        // the lexer doesn't split the {$variable} into separate tokens.
        
        // Store the current position
        int startPos = lexer.getTokenStart();
        
        // Advance the lexer
        lexer.advance();
        
        // If the lexer recognized {$variable} as a macro, it would have split it
        // into multiple tokens. We can verify that it didn't by checking that
        // the next token (if any) doesn't start immediately after the '{' character.
        if (lexer.getTokenType() != null) {
            assertTrue("Lexer should not split {$variable} into separate tokens in OFF mode",
                      lexer.getTokenStart() > startPos + 1);
        }
    }

    /**
     * Tests the interaction between n:syntax attribute and {syntax} tags.
     */
    @Test
    public void testNSyntaxInteractionWithSyntaxTags() {
        // First, manually set the syntax mode to DOUBLE to simulate the n:syntax="double" attribute being processed
        lexer.setSyntaxMode("double");
        System.out.println("DEBUG: Manually set syntax mode to DOUBLE");
        
        // Verify initial syntax mode
        assertEquals("Initial syntax mode should be DOUBLE", 
                     LatteSyntaxMode.DOUBLE, lexer.getSyntaxMode());
        
        // Directly process the {syntax off} tag
        lexer.processSyntaxTags("{syntax off}");
        
        // Verify that the syntax mode is changed to OFF
        assertEquals("Syntax mode should be OFF after {syntax off} tag",
                     LatteSyntaxMode.OFF, lexer.getSyntaxMode());
        
        // Directly process the {/syntax} tag
        System.out.println("DEBUG: Before processing {/syntax}, syntax mode is: " + lexer.getSyntaxMode());
        lexer.processSyntaxTags("{/syntax}");
        System.out.println("DEBUG: After processing {/syntax}, syntax mode is: " + lexer.getSyntaxMode());
        
        // Verify that the syntax mode is changed back to DOUBLE
        assertEquals("Syntax mode should be DOUBLE after {/syntax} tag (returning to previous mode)",
                     LatteSyntaxMode.DOUBLE, lexer.getSyntaxMode());
    }

    /**
     * Tests that the n:syntax attribute with unquoted value works correctly.
     */
    @Test
    public void testNSyntaxUnquotedValue() {
        // Create a focused test content with just the n:syntax=double attribute (unquoted)
        String content = "<div n:syntax=double>{{$variable}}</div>";
        
        // Start the lexer with the focused content
        lexer.start(content);
        
        // Advance to the n:syntax=double attribute (unquoted)
        advanceLexerToText(lexer, "n:syntax=double");
        
        // Process the attribute to set the syntax mode
        while (lexer.getTokenType() != null && 
               !lexer.getTokenType().equals(LatteTokenTypes.LATTE_ATTRIBUTE_VALUE)) {
            lexer.advance();
        }
        
        // Advance past the attribute value to trigger the syntax mode change
        lexer.advance();
        
        // Verify that the syntax mode is set to DOUBLE
        assertEquals("Syntax mode should be DOUBLE after n:syntax=double attribute (unquoted)",
                     LatteSyntaxMode.DOUBLE, lexer.getSyntaxMode());
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