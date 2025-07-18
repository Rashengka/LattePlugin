package cz.hqm.latte.plugin.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import cz.hqm.latte.plugin.file.LatteFileType;
import cz.hqm.latte.plugin.lang.LatteLanguage;

/**
 * PSI file implementation for Latte files.
 * Extends PsiFileBase to provide Latte-specific PSI functionality.
 */
public class LatteFile extends PsiFileBase {
    
    public LatteFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, LatteLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return LatteFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Latte File";
    }
}