package org.latte.plugin.lang;

import com.intellij.lang.html.HTMLParserDefinition;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import org.jetbrains.annotations.NotNull;
import org.latte.plugin.lexer.LatteLexer;
import org.latte.plugin.psi.LatteFile;

/**
 * Parser definition for Latte files.
 * Extends HTMLParserDefinition to leverage HTML parsing capabilities
 * while adding support for Latte-specific syntax.
 */
public class LatteParserDefinition extends HTMLParserDefinition {
    public static final IFileElementType FILE = new IFileElementType(LatteLanguage.INSTANCE);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new LatteLexer();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new LatteFile(viewProvider);
    }
}