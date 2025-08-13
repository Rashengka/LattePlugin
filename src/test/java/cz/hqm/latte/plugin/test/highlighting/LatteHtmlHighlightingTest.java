package cz.hqm.latte.plugin.test.highlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlTokenType;
import cz.hqm.latte.plugin.highlighting.LatteSyntaxHighlighter;
import cz.hqm.latte.plugin.test.LattePluginTestBase;
import org.junit.Test;

/**
 * Tests for HTML syntax highlighting in Latte files.
 * This test verifies that HTML elements in Latte files are properly highlighted.
 */
public class LatteHtmlHighlightingTest extends LattePluginTestBase {

    private LatteSyntaxHighlighter highlighter;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        highlighter = new LatteSyntaxHighlighter();
    }

    /**
     * Tests that the highlighter returns the correct text attribute keys for HTML tag tokens.
     */
    @Test
    public void testHtmlTagHighlighting() {
        // Test HTML tag start highlighting
        TextAttributesKey[] tagStartKeys = highlighter.getTokenHighlights(XmlTokenType.XML_START_TAG_START);
        assertEquals("Should return one text attribute key for HTML tag start", 1, tagStartKeys.length);
        assertEquals("Should return HTML_TAG for HTML tag start", LatteSyntaxHighlighter.HTML_TAG, tagStartKeys[0]);

        // Test HTML tag end highlighting
        TextAttributesKey[] tagEndKeys = highlighter.getTokenHighlights(XmlTokenType.XML_TAG_END);
        assertEquals("Should return one text attribute key for HTML tag end", 1, tagEndKeys.length);
        assertEquals("Should return HTML_TAG for HTML tag end", LatteSyntaxHighlighter.HTML_TAG, tagEndKeys[0]);

        // Test HTML tag name highlighting
        TextAttributesKey[] tagNameKeys = highlighter.getTokenHighlights(XmlTokenType.XML_TAG_NAME);
        assertEquals("Should return one text attribute key for HTML tag name", 1, tagNameKeys.length);
        assertEquals("Should return HTML_TAG_NAME for HTML tag name", LatteSyntaxHighlighter.HTML_TAG_NAME, tagNameKeys[0]);
    }

    /**
     * Tests that the highlighter returns the correct text attribute keys for HTML attribute tokens.
     */
    @Test
    public void testHtmlAttributeHighlighting() {
        // Test HTML attribute name highlighting
        TextAttributesKey[] attributeNameKeys = highlighter.getTokenHighlights(XmlTokenType.XML_NAME);
        assertEquals("Should return one text attribute key for HTML attribute name", 1, attributeNameKeys.length);
        assertEquals("Should return HTML_ATTRIBUTE_NAME for HTML attribute name", LatteSyntaxHighlighter.HTML_ATTRIBUTE_NAME, attributeNameKeys[0]);

        // Test HTML attribute value highlighting
        TextAttributesKey[] attributeValueKeys = highlighter.getTokenHighlights(XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN);
        assertEquals("Should return one text attribute key for HTML attribute value", 1, attributeValueKeys.length);
        assertEquals("Should return HTML_ATTRIBUTE_VALUE for HTML attribute value", LatteSyntaxHighlighter.HTML_ATTRIBUTE_VALUE, attributeValueKeys[0]);
    }

    /**
     * Tests that the highlighter returns the correct text attribute keys for HTML entity tokens.
     */
    @Test
    public void testHtmlEntityHighlighting() {
        // Test HTML entity highlighting
        TextAttributesKey[] entityKeys = highlighter.getTokenHighlights(XmlTokenType.XML_ENTITY_REF_TOKEN);
        assertEquals("Should return one text attribute key for HTML entity", 1, entityKeys.length);
        assertEquals("Should return HTML_ENTITY for HTML entity", LatteSyntaxHighlighter.HTML_ENTITY, entityKeys[0]);
    }

    /**
     * Tests that the highlighter returns the correct text attribute keys for HTML comment tokens.
     */
    @Test
    public void testHtmlCommentHighlighting() {
        // Test HTML comment start highlighting
        TextAttributesKey[] commentStartKeys = highlighter.getTokenHighlights(XmlTokenType.XML_COMMENT_START);
        assertEquals("Should return one text attribute key for HTML comment start", 1, commentStartKeys.length);
        assertEquals("Should return HTML_COMMENT for HTML comment start", LatteSyntaxHighlighter.HTML_COMMENT, commentStartKeys[0]);

        // Test HTML comment end highlighting
        TextAttributesKey[] commentEndKeys = highlighter.getTokenHighlights(XmlTokenType.XML_COMMENT_END);
        assertEquals("Should return one text attribute key for HTML comment end", 1, commentEndKeys.length);
        assertEquals("Should return HTML_COMMENT for HTML comment end", LatteSyntaxHighlighter.HTML_COMMENT, commentEndKeys[0]);

        // Test HTML comment content highlighting
        TextAttributesKey[] commentContentKeys = highlighter.getTokenHighlights(XmlTokenType.XML_COMMENT_CHARACTERS);
        assertEquals("Should return one text attribute key for HTML comment content", 1, commentContentKeys.length);
        assertEquals("Should return HTML_COMMENT for HTML comment content", LatteSyntaxHighlighter.HTML_COMMENT, commentContentKeys[0]);
    }

    /**
     * Tests that the lexer correctly tokenizes HTML elements in a Latte file.
     */
    @Test
    public void testLexerTokenizesHtmlElements() {
        // Get the lexer from the highlighter
        Lexer lexer = highlighter.getHighlightingLexer();
        assertNotNull("Highlighting lexer should not be null", lexer);

        // Set the lexer to tokenize a simple HTML snippet
        String html = "<div class=\"container\">Hello, world!</div>";
        lexer.start(html);

        // Verify that the lexer tokenizes the HTML elements correctly
        assertEquals("First token should be XML_START_TAG_START", XmlTokenType.XML_START_TAG_START, lexer.getTokenType());
        lexer.advance();
        assertEquals("Second token should be XML_NAME", XmlTokenType.XML_NAME, lexer.getTokenType());
        lexer.advance();
        assertEquals("Third token should be XML_WHITE_SPACE", XmlTokenType.XML_WHITE_SPACE, lexer.getTokenType());
        lexer.advance();
        assertEquals("Fourth token should be XML_NAME", XmlTokenType.XML_NAME, lexer.getTokenType());
    }
}