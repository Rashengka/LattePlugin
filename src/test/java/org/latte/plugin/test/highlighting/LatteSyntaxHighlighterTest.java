package org.latte.plugin.test.highlighting;

import com.intellij.lexer.Lexer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.tree.IElementType;
import org.latte.plugin.highlighting.LatteSyntaxHighlighter;
import org.latte.plugin.lexer.LatteTokenTypes;
import org.latte.plugin.test.LattePluginTestBase;

/**
 * Tests for Latte syntax highlighting functionality.
 */
public class LatteSyntaxHighlighterTest extends LattePluginTestBase {

    private LatteSyntaxHighlighter highlighter;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        highlighter = new LatteSyntaxHighlighter();
    }

    /**
     * Tests that the highlighter returns the correct lexer.
     */
    @Test
    public void testGetHighlightingLexer() {
        Lexer lexer = highlighter.getHighlightingLexer();
        assertNotNull("Highlighting lexer should not be null", lexer);
    }

    /**
     * Tests that the highlighter returns the correct text attribute keys for macro tokens.
     */
    @Test
    public void testMacroHighlighting() {
        // Test macro start/end highlighting
        TextAttributesKey[] macroStartKeys = highlighter.getTokenHighlights(LatteTokenTypes.LATTE_MACRO_START);
        assertEquals("Should return one text attribute key for macro start", 1, macroStartKeys.length);
        assertEquals("Should return LATTE_MACRO for macro start", LatteSyntaxHighlighter.LATTE_MACRO, macroStartKeys[0]);

        TextAttributesKey[] macroEndKeys = highlighter.getTokenHighlights(LatteTokenTypes.LATTE_MACRO_END);
        assertEquals("Should return one text attribute key for macro end", 1, macroEndKeys.length);
        assertEquals("Should return LATTE_MACRO for macro end", LatteSyntaxHighlighter.LATTE_MACRO, macroEndKeys[0]);

        // Test macro name highlighting
        TextAttributesKey[] macroNameKeys = highlighter.getTokenHighlights(LatteTokenTypes.LATTE_MACRO_NAME);
        assertEquals("Should return one text attribute key for macro name", 1, macroNameKeys.length);
        assertEquals("Should return LATTE_MACRO_NAME for macro name", LatteSyntaxHighlighter.LATTE_MACRO_NAME, macroNameKeys[0]);
    }

    /**
     * Tests that the highlighter returns the correct text attribute keys for attribute tokens.
     */
    @Test
    public void testAttributeHighlighting() {
        // Test attribute name highlighting
        TextAttributesKey[] attributeNameKeys = highlighter.getTokenHighlights(LatteTokenTypes.LATTE_ATTRIBUTE_NAME);
        assertEquals("Should return one text attribute key for attribute name", 1, attributeNameKeys.length);
        assertEquals("Should return LATTE_ATTRIBUTE for attribute name", LatteSyntaxHighlighter.LATTE_ATTRIBUTE, attributeNameKeys[0]);

        // Test attribute value highlighting
        TextAttributesKey[] attributeValueKeys = highlighter.getTokenHighlights(LatteTokenTypes.LATTE_ATTRIBUTE_VALUE);
        assertEquals("Should return one text attribute key for attribute value", 1, attributeValueKeys.length);
        assertEquals("Should return LATTE_ATTRIBUTE for attribute value", LatteSyntaxHighlighter.LATTE_ATTRIBUTE, attributeValueKeys[0]);
    }

    /**
     * Tests that the highlighter returns the correct text attribute keys for filter tokens.
     */
    @Test
    public void testFilterHighlighting() {
        // Test filter pipe highlighting
        TextAttributesKey[] filterPipeKeys = highlighter.getTokenHighlights(LatteTokenTypes.LATTE_FILTER_PIPE);
        assertEquals("Should return one text attribute key for filter pipe", 1, filterPipeKeys.length);
        assertEquals("Should return LATTE_FILTER for filter pipe", LatteSyntaxHighlighter.LATTE_FILTER, filterPipeKeys[0]);

        // Test filter name highlighting
        TextAttributesKey[] filterNameKeys = highlighter.getTokenHighlights(LatteTokenTypes.LATTE_FILTER_NAME);
        assertEquals("Should return one text attribute key for filter name", 1, filterNameKeys.length);
        assertEquals("Should return LATTE_FILTER for filter name", LatteSyntaxHighlighter.LATTE_FILTER, filterNameKeys[0]);
    }

    /**
     * Tests that the highlighter returns the correct text attribute keys for comment tokens.
     */
    @Test
    public void testCommentHighlighting() {
        // Test comment start highlighting
        TextAttributesKey[] commentStartKeys = highlighter.getTokenHighlights(LatteTokenTypes.LATTE_COMMENT_START);
        assertEquals("Should return one text attribute key for comment start", 1, commentStartKeys.length);
        assertEquals("Should return LATTE_COMMENT for comment start", LatteSyntaxHighlighter.LATTE_COMMENT, commentStartKeys[0]);

        // Test comment end highlighting
        TextAttributesKey[] commentEndKeys = highlighter.getTokenHighlights(LatteTokenTypes.LATTE_COMMENT_END);
        assertEquals("Should return one text attribute key for comment end", 1, commentEndKeys.length);
        assertEquals("Should return LATTE_COMMENT for comment end", LatteSyntaxHighlighter.LATTE_COMMENT, commentEndKeys[0]);

        // Test comment content highlighting
        TextAttributesKey[] commentContentKeys = highlighter.getTokenHighlights(LatteTokenTypes.LATTE_COMMENT_CONTENT);
        assertEquals("Should return one text attribute key for comment content", 1, commentContentKeys.length);
        assertEquals("Should return LATTE_COMMENT for comment content", LatteSyntaxHighlighter.LATTE_COMMENT, commentContentKeys[0]);
    }

    /**
     * Tests that the highlighter returns the correct text attribute keys for error tokens.
     */
    @Test
    public void testErrorHighlighting() {
        // Test macro error highlighting
        TextAttributesKey[] macroErrorKeys = highlighter.getTokenHighlights(LatteTokenTypes.LATTE_ERROR_INVALID_MACRO_NAME);
        assertEquals("Should return one text attribute key for macro error", 1, macroErrorKeys.length);
        assertEquals("Should return ERROR_MACRO for macro error", LatteSyntaxHighlighter.ERROR_MACRO, macroErrorKeys[0]);

        // Test attribute error highlighting
        TextAttributesKey[] attributeErrorKeys = highlighter.getTokenHighlights(LatteTokenTypes.LATTE_ERROR_INVALID_ATTRIBUTE_SYNTAX);
        assertEquals("Should return one text attribute key for attribute error", 1, attributeErrorKeys.length);
        assertEquals("Should return ERROR_ATTRIBUTE for attribute error", LatteSyntaxHighlighter.ERROR_ATTRIBUTE, attributeErrorKeys[0]);

        // Test filter error highlighting
        TextAttributesKey[] filterErrorKeys = highlighter.getTokenHighlights(LatteTokenTypes.LATTE_ERROR_INVALID_FILTER_SYNTAX);
        assertEquals("Should return one text attribute key for filter error", 1, filterErrorKeys.length);
        assertEquals("Should return ERROR_FILTER for filter error", LatteSyntaxHighlighter.ERROR_FILTER, filterErrorKeys[0]);

        // Test bad character highlighting
        TextAttributesKey[] badCharKeys = highlighter.getTokenHighlights(LatteTokenTypes.LATTE_ERROR_UNEXPECTED_CHARACTER);
        assertEquals("Should return one text attribute key for bad character", 1, badCharKeys.length);
        assertEquals("Should return BAD_CHARACTER for bad character", LatteSyntaxHighlighter.BAD_CHARACTER, badCharKeys[0]);
    }

    /**
     * Tests that the highlighter returns empty keys for unknown token types.
     */
    @Test
    public void testUnknownTokenHighlighting() {
        // Create a mock token type
        IElementType unknownType = new IElementType("UNKNOWN", null);
        
        // Test unknown token highlighting
        TextAttributesKey[] unknownKeys = highlighter.getTokenHighlights(unknownType);
        assertEquals("Should return empty keys for unknown token type", 0, unknownKeys.length);
    }
}
