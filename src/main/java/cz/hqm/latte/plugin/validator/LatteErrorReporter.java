package cz.hqm.latte.plugin.validator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import cz.hqm.latte.plugin.util.LatteLogger;
import org.jetbrains.annotations.NotNull;
// Explicit import for LatteValidator for better code clarity
// Not strictly necessary as both classes are in the same package

/**
 * Captures and logs validation errors displayed in Latte files.
 * Logging only occurs during development and is disabled in distribution builds.
 */
public class LatteErrorReporter {
    private static final Logger LOG = Logger.getInstance(LatteErrorReporter.class);

    /**
     * Logs a validation error.
     * 
     * @param message Error message
     * @param element PsiElement where the error occurred
     */
    public static void logValidationError(@NotNull String message, @NotNull PsiElement element) {
        // Use the specialized validation error logging method to ensure errors are logged to validation_errors.log
        // Use truncateElementText to format multi-line element text
        LatteLogger.logValidationError(LOG, message, LatteValidator.truncateElementText(element.getText()), element.getTextOffset());
    }

    /**
     * Logs an error from AnnotationHolder.
     * 
     * @param message Error message
     * @param severity Error severity
     * @param element PsiElement where the error occurred
     * @param holder AnnotationHolder
     */
    public static void logAndReportError(@NotNull String message, 
                                        @NotNull HighlightSeverity severity,
                                        @NotNull PsiElement element, 
                                        @NotNull AnnotationHolder holder) {
        // Use the specialized validation error logging method to ensure errors are logged to validation_errors.log
        // Use truncateElementText to format multi-line element text
        LatteLogger.logValidationError(LOG, message, LatteValidator.truncateElementText(element.getText()), element.getTextOffset());
        
        // Create the annotation in the editor
        holder.newAnnotation(severity, message)
              .range(element.getTextRange())
              .create();
    }
}