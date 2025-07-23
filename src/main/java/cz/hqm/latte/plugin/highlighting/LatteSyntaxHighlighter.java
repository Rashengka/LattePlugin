package cz.hqm.latte.plugin.highlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlTokenType;
import org.jetbrains.annotations.NotNull;
import cz.hqm.latte.plugin.lexer.LatteLexerFactory;
import cz.hqm.latte.plugin.lexer.LatteTokenTypes;

import java.awt.Color;
import java.awt.Font;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * Syntax highlighter for Latte files.
 * Handles Latte-specific syntax highlighting.
 */
public class LatteSyntaxHighlighter extends SyntaxHighlighterBase {
    // Define custom text attributes for Latte syntax elements
    private static final TextAttributes MACRO_ATTRIBUTES = new TextAttributes(
            new Color(255, 204, 0), // Bright yellow for macro delimiters
            null, 
            null, 
            null, 
            Font.BOLD
    );
    
    private static final TextAttributes MACRO_NAME_ATTRIBUTES = new TextAttributes(
            new Color(102, 204, 255), // Light blue for macro names
            null, 
            null, 
            null, 
            Font.BOLD
    );
    
    private static final TextAttributes ATTRIBUTE_ATTRIBUTES = new TextAttributes(
            new Color(255, 153, 0), // Orange for attributes
            null, 
            null, 
            null, 
            Font.PLAIN
    );
    
    private static final TextAttributes FILTER_ATTRIBUTES = new TextAttributes(
            new Color(153, 204, 0), // Light green for filters
            null, 
            null, 
            null, 
            Font.PLAIN
    );
    
    private static final TextAttributes COMMENT_ATTRIBUTES = new TextAttributes(
            new Color(128, 128, 128), // Gray for comments
            null, 
            null, 
            null, 
            Font.ITALIC
    );
    
    // Define text attribute keys for Latte syntax elements
    public static final TextAttributesKey LATTE_MACRO =
            createTextAttributesKey("LATTE_MACRO", MACRO_ATTRIBUTES);
    public static final TextAttributesKey LATTE_MACRO_NAME =
            createTextAttributesKey("LATTE_MACRO_NAME", MACRO_NAME_ATTRIBUTES);
    public static final TextAttributesKey LATTE_ATTRIBUTE =
            createTextAttributesKey("LATTE_ATTRIBUTE", ATTRIBUTE_ATTRIBUTES);
    public static final TextAttributesKey LATTE_FILTER =
            createTextAttributesKey("LATTE_FILTER", FILTER_ATTRIBUTES);
    public static final TextAttributesKey LATTE_COMMENT =
            createTextAttributesKey("LATTE_COMMENT", COMMENT_ATTRIBUTES);
    
    // Define custom text attributes for error elements
    private static final TextAttributes ERROR_ATTRIBUTES = new TextAttributes(
            new Color(255, 0, 0), // Bright red for errors
            null, 
            null, 
            null, 
            Font.BOLD
    );
    
    // Define text attribute keys for Latte error elements
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("LATTE_BAD_CHARACTER", ERROR_ATTRIBUTES);
    public static final TextAttributesKey ERROR_MACRO =
            createTextAttributesKey("LATTE_ERROR_MACRO", ERROR_ATTRIBUTES);
    public static final TextAttributesKey ERROR_ATTRIBUTE =
            createTextAttributesKey("LATTE_ERROR_ATTRIBUTE", ERROR_ATTRIBUTES);
    public static final TextAttributesKey ERROR_FILTER =
            createTextAttributesKey("LATTE_ERROR_FILTER", ERROR_ATTRIBUTES);

    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];
    
    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] MACRO_KEYS = new TextAttributesKey[]{LATTE_MACRO};
    private static final TextAttributesKey[] MACRO_NAME_KEYS = new TextAttributesKey[]{LATTE_MACRO_NAME};
    private static final TextAttributesKey[] ATTRIBUTE_KEYS = new TextAttributesKey[]{LATTE_ATTRIBUTE};
    private static final TextAttributesKey[] FILTER_KEYS = new TextAttributesKey[]{LATTE_FILTER};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{LATTE_COMMENT};
    
    // Error highlighting keys
    private static final TextAttributesKey[] ERROR_MACRO_KEYS = new TextAttributesKey[]{ERROR_MACRO};
    private static final TextAttributesKey[] ERROR_ATTRIBUTE_KEYS = new TextAttributesKey[]{ERROR_ATTRIBUTE};
    private static final TextAttributesKey[] ERROR_FILTER_KEYS = new TextAttributesKey[]{ERROR_FILTER};

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return LatteLexerFactory.getInstance().getLexer();
    }

    // Define text attribute keys for HTML elements with LATTE_ prefix to avoid conflicts
    public static final TextAttributesKey HTML_TAG =
            createTextAttributesKey("LATTE_HTML_TAG", DefaultLanguageHighlighterColors.MARKUP_TAG);
    public static final TextAttributesKey HTML_TAG_NAME =
            createTextAttributesKey("LATTE_HTML_TAG_NAME", DefaultLanguageHighlighterColors.MARKUP_TAG);
    public static final TextAttributesKey HTML_ATTRIBUTE_NAME =
            createTextAttributesKey("LATTE_HTML_ATTRIBUTE_NAME", DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE);
    public static final TextAttributesKey HTML_ATTRIBUTE_VALUE =
            createTextAttributesKey("LATTE_HTML_ATTRIBUTE_VALUE", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey HTML_ENTITY =
            createTextAttributesKey("LATTE_HTML_ENTITY", DefaultLanguageHighlighterColors.MARKUP_ENTITY);
    public static final TextAttributesKey HTML_COMMENT =
            createTextAttributesKey("LATTE_HTML_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
            
    // Define arrays of text attribute keys for token highlighting
    private static final TextAttributesKey[] HTML_TAG_KEYS = new TextAttributesKey[]{HTML_TAG};
    private static final TextAttributesKey[] HTML_TAG_NAME_KEYS = new TextAttributesKey[]{HTML_TAG_NAME};
    private static final TextAttributesKey[] HTML_ATTRIBUTE_NAME_KEYS = new TextAttributesKey[]{HTML_ATTRIBUTE_NAME};
    private static final TextAttributesKey[] HTML_ATTRIBUTE_VALUE_KEYS = new TextAttributesKey[]{HTML_ATTRIBUTE_VALUE};
    private static final TextAttributesKey[] HTML_ENTITY_KEYS = new TextAttributesKey[]{HTML_ENTITY};
    private static final TextAttributesKey[] HTML_COMMENT_KEYS = new TextAttributesKey[]{HTML_COMMENT};

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        // Handle standard Latte token types
        if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else if (tokenType.equals(LatteTokenTypes.LATTE_MACRO_START) || 
                   tokenType.equals(LatteTokenTypes.LATTE_MACRO_END)) {
            return MACRO_KEYS;
        } else if (tokenType.equals(LatteTokenTypes.LATTE_MACRO_NAME)) {
            return MACRO_NAME_KEYS;
        } else if (tokenType.equals(LatteTokenTypes.LATTE_ATTRIBUTE_NAME) || 
                   tokenType.equals(LatteTokenTypes.LATTE_ATTRIBUTE_VALUE)) {
            return ATTRIBUTE_KEYS;
        } else if (tokenType.equals(LatteTokenTypes.LATTE_FILTER_PIPE) || 
                   tokenType.equals(LatteTokenTypes.LATTE_FILTER_NAME)) {
            return FILTER_KEYS;
        } else if (tokenType.equals(LatteTokenTypes.LATTE_COMMENT_START) || 
                   tokenType.equals(LatteTokenTypes.LATTE_COMMENT_END) || 
                   tokenType.equals(LatteTokenTypes.LATTE_COMMENT_CONTENT)) {
            return COMMENT_KEYS;
        }
        
        // Handle HTML token types
        else if (tokenType.equals(XmlTokenType.XML_START_TAG_START) || 
                 tokenType.equals(XmlTokenType.XML_END_TAG_START) ||
                 tokenType.equals(XmlTokenType.XML_TAG_END) ||
                 tokenType.equals(XmlTokenType.XML_EMPTY_ELEMENT_END)) {
            return HTML_TAG_KEYS;
        } else if (tokenType.equals(XmlTokenType.XML_TAG_NAME)) {
            return HTML_TAG_NAME_KEYS;
        } else if (tokenType.equals(XmlTokenType.XML_NAME)) {
            return HTML_ATTRIBUTE_NAME_KEYS;
        } else if (tokenType.equals(XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN) ||
                   tokenType.equals(XmlTokenType.XML_ATTRIBUTE_VALUE_START_DELIMITER) ||
                   tokenType.equals(XmlTokenType.XML_ATTRIBUTE_VALUE_END_DELIMITER)) {
            return HTML_ATTRIBUTE_VALUE_KEYS;
        } else if (tokenType.equals(XmlTokenType.XML_ENTITY_REF_TOKEN)) {
            return HTML_ENTITY_KEYS;
        } else if (tokenType.equals(XmlTokenType.XML_COMMENT_START) ||
                   tokenType.equals(XmlTokenType.XML_COMMENT_END) ||
                   tokenType.equals(XmlTokenType.XML_COMMENT_CHARACTERS)) {
            return HTML_COMMENT_KEYS;
        }
        
        // Handle error token types
        // Macro errors
        else if (tokenType.equals(LatteTokenTypes.LATTE_ERROR_UNCLOSED_MACRO) ||
                 tokenType.equals(LatteTokenTypes.LATTE_ERROR_INVALID_MACRO_NAME) ||
                 tokenType.equals(LatteTokenTypes.LATTE_ERROR_MISMATCHED_MACRO_END) ||
                 tokenType.equals(LatteTokenTypes.LATTE_ERROR_UNEXPECTED_MACRO_END)) {
            return ERROR_MACRO_KEYS;
        }
        // Attribute errors
        else if (tokenType.equals(LatteTokenTypes.LATTE_ERROR_INVALID_ATTRIBUTE_SYNTAX) ||
                 tokenType.equals(LatteTokenTypes.LATTE_ERROR_UNCLOSED_ATTRIBUTE_QUOTES)) {
            return ERROR_ATTRIBUTE_KEYS;
        }
        // Filter errors
        else if (tokenType.equals(LatteTokenTypes.LATTE_ERROR_INVALID_FILTER_SYNTAX) ||
                 tokenType.equals(LatteTokenTypes.LATTE_ERROR_UNKNOWN_FILTER)) {
            return ERROR_FILTER_KEYS;
        }
        // General errors
        else if (tokenType.equals(LatteTokenTypes.LATTE_ERROR_UNEXPECTED_CHARACTER)) {
            return BAD_CHAR_KEYS;
        }
        
        // Return empty keys for tokens we don't recognize
        return EMPTY_KEYS;
    }
}