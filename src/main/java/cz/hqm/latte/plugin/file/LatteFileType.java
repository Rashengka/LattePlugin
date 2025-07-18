package cz.hqm.latte.plugin.file;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import cz.hqm.latte.plugin.lang.LatteLanguage;

import javax.swing.*;

/**
 * Defines the Latte file type (.latte extension)
 */
public class LatteFileType extends LanguageFileType {
    public static final LatteFileType INSTANCE = new LatteFileType();

    private LatteFileType() {
        super(LatteLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Latte";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Latte template file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "latte";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        // Return a custom icon for Latte files
        // For now, we'll return null and use the default icon
        return null;
    }
}