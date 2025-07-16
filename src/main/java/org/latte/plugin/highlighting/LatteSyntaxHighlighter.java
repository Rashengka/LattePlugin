package org.latte.plugin.highlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.latte.plugin.lexer.LatteLexer;
import org.latte.plugin.lexer.LatteTokenTypes;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/**
 * Syntax highlighter for Latte files.
 * Handles Latte-specific syntax highlighting.
 */
public class LatteSyntaxHighlighter extends SyntaxHighlighterBase {
    // Define text attribute keys for Latte syntax elements
    public static final TextAttributesKey LATTE_MACRO =
            createTextAttributesKey("LATTE_MACRO", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey LATTE_MACRO_NAME =
            createTextAttributesKey("LATTE_MACRO_NAME", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey LATTE_ATTRIBUTE =
            createTextAttributesKey("LATTE_ATTRIBUTE", DefaultLanguageHighlighterColors.METADATA);
    public static final TextAttributesKey LATTE_FILTER =
            createTextAttributesKey("LATTE_FILTER", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
    public static final TextAttributesKey LATTE_COMMENT =
            createTextAttributesKey("LATTE_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
    
    // Define text attribute keys for Latte error elements
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("LATTE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);
    public static final TextAttributesKey ERROR_MACRO =
            createTextAttributesKey("LATTE_ERROR_MACRO", HighlighterColors.BAD_CHARACTER);
    public static final TextAttributesKey ERROR_ATTRIBUTE =
            createTextAttributesKey("LATTE_ERROR_ATTRIBUTE", HighlighterColors.BAD_CHARACTER);
    public static final TextAttributesKey ERROR_FILTER =
            createTextAttributesKey("LATTE_ERROR_FILTER", HighlighterColors.BAD_CHARACTER);

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
        return new LatteLexer();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        // Handle standard token types
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