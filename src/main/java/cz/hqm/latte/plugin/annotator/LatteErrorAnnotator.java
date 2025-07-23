package cz.hqm.latte.plugin.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import cz.hqm.latte.plugin.util.LatteLogger;
import org.jetbrains.annotations.NotNull;

/**
 * Captures and logs annotations displayed in Latte files.
 * Logging only occurs during development and is disabled in distribution builds.
 */
public class LatteErrorAnnotator implements Annotator {
    private static final Logger LOG = Logger.getInstance(LatteErrorAnnotator.class);

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        // This is a simple implementation that doesn't modify the annotations
        // but logs that the annotator was called for debugging purposes
        LatteLogger.debug(LOG, "LatteErrorAnnotator called for element: " + element.getText());
        
        // Additional logic can be added here to check for specific error conditions
        // and log them using LatteLogger
    }
}