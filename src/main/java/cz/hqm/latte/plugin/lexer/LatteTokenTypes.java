package cz.hqm.latte.plugin.lexer;

import com.intellij.psi.tree.IElementType;
import cz.hqm.latte.plugin.lang.LatteLanguage;

/**
 * Token types for Latte template language.
 * Defines the token types for Latte-specific syntax elements like macros and attributes.
 */
public interface LatteTokenTypes {
    // Latte macro tokens
    IElementType LATTE_MACRO_START = new LatteElementType("LATTE_MACRO_START");
    IElementType LATTE_MACRO_END = new LatteElementType("LATTE_MACRO_END");
    IElementType LATTE_MACRO_NAME = new LatteElementType("LATTE_MACRO_NAME");
    IElementType LATTE_MACRO_CONTENT = new LatteElementType("LATTE_MACRO_CONTENT");
    
    // Latte attribute tokens
    IElementType LATTE_ATTRIBUTE_START = new LatteElementType("LATTE_ATTRIBUTE_START");
    IElementType LATTE_ATTRIBUTE_END = new LatteElementType("LATTE_ATTRIBUTE_END");
    IElementType LATTE_ATTRIBUTE_NAME = new LatteElementType("LATTE_ATTRIBUTE_NAME");
    IElementType LATTE_ATTRIBUTE_VALUE = new LatteElementType("LATTE_ATTRIBUTE_VALUE");
    
    // Latte filter tokens
    IElementType LATTE_FILTER_PIPE = new LatteElementType("LATTE_FILTER_PIPE");
    IElementType LATTE_FILTER_NAME = new LatteElementType("LATTE_FILTER_NAME");
    
    // Latte comment tokens
    IElementType LATTE_COMMENT_START = new LatteElementType("LATTE_COMMENT_START");
    IElementType LATTE_COMMENT_END = new LatteElementType("LATTE_COMMENT_END");
    IElementType LATTE_COMMENT_CONTENT = new LatteElementType("LATTE_COMMENT_CONTENT");
    
    // Latte error tokens - for incorrect syntax detection
    // Macro errors
    IElementType LATTE_ERROR_UNCLOSED_MACRO = new LatteElementType("LATTE_ERROR_UNCLOSED_MACRO");
    IElementType LATTE_ERROR_INVALID_MACRO_NAME = new LatteElementType("LATTE_ERROR_INVALID_MACRO_NAME");
    IElementType LATTE_ERROR_MISMATCHED_MACRO_END = new LatteElementType("LATTE_ERROR_MISMATCHED_MACRO_END");
    IElementType LATTE_ERROR_UNEXPECTED_MACRO_END = new LatteElementType("LATTE_ERROR_UNEXPECTED_MACRO_END");
    
    // Attribute errors
    IElementType LATTE_ERROR_INVALID_ATTRIBUTE_SYNTAX = new LatteElementType("LATTE_ERROR_INVALID_ATTRIBUTE_SYNTAX");
    IElementType LATTE_ERROR_UNCLOSED_ATTRIBUTE_QUOTES = new LatteElementType("LATTE_ERROR_UNCLOSED_ATTRIBUTE_QUOTES");
    
    // Filter errors
    IElementType LATTE_ERROR_INVALID_FILTER_SYNTAX = new LatteElementType("LATTE_ERROR_INVALID_FILTER_SYNTAX");
    IElementType LATTE_ERROR_UNKNOWN_FILTER = new LatteElementType("LATTE_ERROR_UNKNOWN_FILTER");
    
    // General errors
    IElementType LATTE_ERROR_UNEXPECTED_CHARACTER = new LatteElementType("LATTE_ERROR_UNEXPECTED_CHARACTER");
    
    /**
     * Custom element type for Latte tokens.
     */
    class LatteElementType extends IElementType {
        public LatteElementType(String debugName) {
            super(debugName, LatteLanguage.INSTANCE);
        }
    }
}