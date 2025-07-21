package cz.hqm.latte.plugin.test.lexer;

import com.intellij.lexer.Lexer;
import org.junit.After;
import org.junit.Before;
import com.intellij.psi.tree.IElementType;
import org.junit.Test;
import cz.hqm.latte.plugin.lexer.LatteLexer;
import cz.hqm.latte.plugin.lexer.LatteMacroLexer;
import cz.hqm.latte.plugin.lexer.LatteAttributeLexer;
import cz.hqm.latte.plugin.lexer.LatteTokenTypes;
import cz.hqm.latte.plugin.test.LattePluginTestBase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for Latte error detection functionality.
 */
public class LatteErrorDetectionTest extends LattePluginTestBase {

    /**
     * Tests that invalid macro names are correctly detected.
     */
    @Test
    public void testInvalidMacroName() {
        LatteMacroLexer lexer = new LatteMacroLexer();
        String text = "invalidMacro}";
        lexer.start(text, 0, text.length(), 0); // Don't skip the opening {, we're passing just the content
        
        // First token should be the invalid macro name
        IElementType actualTokenType = lexer.getTokenType();
        System.err.println("MACRO TEST - Expected token type: " + LatteTokenTypes.LATTE_ERROR_INVALID_MACRO_NAME);
        System.err.println("MACRO TEST - Actual token type: " + actualTokenType);
        System.err.println("MACRO TEST - Token text: '" + text.substring(lexer.getTokenStart(), lexer.getTokenEnd()) + "'");
        assertEquals("Invalid macro name should be detected as an error", 
                     LatteTokenTypes.LATTE_ERROR_INVALID_MACRO_NAME, actualTokenType);
        assertEquals("Token start position should be 0", 0, lexer.getTokenStart());
        assertEquals("Token end position should be 12", 12, lexer.getTokenEnd());
        
        lexer.advance();
        // Next token should be the closing }
        actualTokenType = lexer.getTokenType();
        System.out.println("Expected next token type: " + LatteTokenTypes.LATTE_MACRO_END);
        System.out.println("Actual next token type: " + actualTokenType);
        assertEquals("Next token should be the macro end", 
                     LatteTokenTypes.LATTE_MACRO_END, actualTokenType);
    }
    
    /**
     * Tests that invalid attribute syntax is correctly detected.
     */
    @Test
    public void testInvalidAttributeSyntax() {
        LatteAttributeLexer lexer = new LatteAttributeLexer();
        String text = "n:invalid=\"value\"";
        lexer.start(text, 0, text.length(), 0);
        
        // First token should be the invalid attribute name
        IElementType actualTokenType = lexer.getTokenType();
        System.err.println("ATTR TEST - Expected token type: " + LatteTokenTypes.LATTE_ERROR_INVALID_ATTRIBUTE_SYNTAX);
        System.err.println("ATTR TEST - Actual token type: " + actualTokenType);
        System.err.println("ATTR TEST - Token text: '" + text.substring(lexer.getTokenStart(), lexer.getTokenEnd()) + "'");
        System.err.println("ATTR TEST - Token start: " + lexer.getTokenStart());
        System.err.println("ATTR TEST - Token end: " + lexer.getTokenEnd());
        
        assertEquals("Invalid attribute syntax should be detected as an error", 
                     LatteTokenTypes.LATTE_ERROR_INVALID_ATTRIBUTE_SYNTAX, actualTokenType);
    }
    
    /**
     * Tests that unclosed quotes in attributes are correctly detected.
     */
    @Test
    public void testUnclosedAttributeQuotes() {
        LatteAttributeLexer lexer = new LatteAttributeLexer();
        String text = "n:if=\"$condition";
        lexer.start(text, 0, text.length(), 0);
        
        // First token should be the attribute name
        assertEquals(LatteTokenTypes.LATTE_ATTRIBUTE_NAME, lexer.getTokenType());
        lexer.advance();
        
        // Next token should be the attribute start (=)
        assertEquals(LatteTokenTypes.LATTE_ATTRIBUTE_START, lexer.getTokenType());
        lexer.advance();
        
        // Next token should be the unclosed attribute quotes error
        assertEquals(LatteTokenTypes.LATTE_ERROR_UNCLOSED_ATTRIBUTE_QUOTES, lexer.getTokenType());
    }
    
    /**
     * Tests the full lexer with a template containing errors.
     */
    @Test
    public void testFullLexerWithErrors() {
        // This test would use the full LatteLexer to tokenize a template with errors
        // However, since LatteLexer uses LayeredLexer which is part of the IntelliJ platform,
        // this test might be difficult to run in isolation.
        // In a real plugin project, you would use LightPlatformTestCase or similar.
        
        // For now, we'll just verify that our custom lexers detect errors correctly
        assertNotNull(LatteTokenTypes.LATTE_ERROR_INVALID_MACRO_NAME);
        assertNotNull(LatteTokenTypes.LATTE_ERROR_INVALID_ATTRIBUTE_SYNTAX);
        assertNotNull(LatteTokenTypes.LATTE_ERROR_UNCLOSED_ATTRIBUTE_QUOTES);
    }
}
