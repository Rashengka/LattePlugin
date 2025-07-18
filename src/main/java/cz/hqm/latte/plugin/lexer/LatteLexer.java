package cz.hqm.latte.plugin.lexer;

import com.intellij.lexer.LayeredLexer;
import com.intellij.lexer.EmptyLexer;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Lexer for Latte template language.
 * Handles Latte-specific syntax like macros.
 */
public class LatteLexer extends LayeredLexer {
    
    public LatteLexer() {
        super(new EmptyLexer());
        
        // Register Latte macro lexer
        registerSelfStoppingLayer(
            new LatteMacroLexer(),
            new IElementType[] { LatteTokenTypes.LATTE_MACRO_START },
            new IElementType[] { LatteTokenTypes.LATTE_MACRO_END }
        );
        
        // Register Latte n:attribute lexer
        registerSelfStoppingLayer(
            new LatteAttributeLexer(),
            new IElementType[] { LatteTokenTypes.LATTE_ATTRIBUTE_START },
            new IElementType[] { LatteTokenTypes.LATTE_ATTRIBUTE_END }
        );
    }
    
    @Nullable
    @Override
    public IElementType getTokenType() {
        return super.getTokenType();
    }
    
    @Override
    public void advance() {
        super.advance();
    }
    
    @NotNull
    @Override
    public CharSequence getTokenSequence() {
        return super.getTokenSequence();
    }
}