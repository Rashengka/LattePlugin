package cz.hqm.latte.plugin.annotator;

import com.intellij.lang.annotation.AnnotationBuilder;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import cz.hqm.latte.plugin.file.LatteFileType;
import cz.hqm.latte.plugin.lexer.LatteTokenTypes;
import cz.hqm.latte.plugin.util.LatteLogger;
import cz.hqm.latte.plugin.validator.LatteErrorReporter;
import cz.hqm.latte.plugin.validator.LatteValidator;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Captures and logs annotations displayed in Latte files.
 * Logging only occurs during development and is disabled in distribution builds.
 */
public class LatteErrorAnnotator implements Annotator {
    private static final Logger LOG = Logger.getInstance(LatteErrorAnnotator.class);
    
    /**
     * Flag indicating whether logging is enabled.
     * This is set to false in distribution builds.
     */
    private static final boolean IS_DEVELOPMENT_MODE = !Boolean.getBoolean("latte.plugin.production");

    /**
     * Proxy for AnnotationHolder that intercepts all annotation creation calls to log them.
     * This ensures that all validation errors, including those from HTML validator, are logged.
     */
    private static class AnnotationHolderProxy implements InvocationHandler {
        private final AnnotationHolder originalHolder;
        private final PsiElement element;

        public AnnotationHolderProxy(AnnotationHolder originalHolder, PsiElement element) {
            this.originalHolder = originalHolder;
            this.element = element;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Intercept the newAnnotation method to log errors
            if (method.getName().equals("newAnnotation") && args.length >= 2) {
                HighlightSeverity severity = (HighlightSeverity) args[0];
                String message = (String) args[1];
                
                // Log the validation error
                LatteLogger.logValidationError(LOG, message, element.getText(), element.getTextOffset());
                
                // Call the original method
                return method.invoke(originalHolder, args);
            }
            
            // For all other methods, delegate to the original holder
            return method.invoke(originalHolder, args);
        }
    }

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        // Log that the annotator was called for debugging purposes
        LatteLogger.debug(LOG, "LatteErrorAnnotator called for element: " + element.getText());
        
        // Determine which holder to use based on development mode
        AnnotationHolder holderToUse;
        
        // In development mode, create a proxy to intercept and log all annotations
        // In production mode, use the original holder without logging
        if (IS_DEVELOPMENT_MODE) {
            // Create a proxy for the annotation holder to intercept all annotation creation calls
            holderToUse = (AnnotationHolder) Proxy.newProxyInstance(
                AnnotationHolder.class.getClassLoader(),
                new Class<?>[] { AnnotationHolder.class },
                new AnnotationHolderProxy(holder, element)
            );
        } else {
            // In production mode, use the original holder without logging
            holderToUse = holder;
        }
        
        // Check if this is a Latte file and validate it
        if (element instanceof PsiFile) {
            PsiFile file = (PsiFile) element;
            if (file.getFileType() instanceof LatteFileType) {
                LatteLogger.debug(LOG, "Validating Latte file: " + file.getName());
                LatteValidator.validateFile(file);
            }
        }
        
        // Get the element type
        IElementType elementType = element.getNode().getElementType();
        
        // Check for error token types and log validation errors
        
        // Macro errors
        if (elementType.equals(LatteTokenTypes.LATTE_ERROR_UNCLOSED_MACRO)) {
            LatteErrorReporter.logAndReportError("Unclosed macro", HighlightSeverity.ERROR, element, holderToUse);
        } else if (elementType.equals(LatteTokenTypes.LATTE_ERROR_INVALID_MACRO_NAME)) {
            LatteErrorReporter.logAndReportError("Invalid macro name", HighlightSeverity.ERROR, element, holderToUse);
        } else if (elementType.equals(LatteTokenTypes.LATTE_ERROR_MISMATCHED_MACRO_END)) {
            LatteErrorReporter.logAndReportError("Mismatched macro end", HighlightSeverity.ERROR, element, holderToUse);
        } else if (elementType.equals(LatteTokenTypes.LATTE_ERROR_UNEXPECTED_MACRO_END)) {
            LatteErrorReporter.logAndReportError("Unexpected macro end", HighlightSeverity.ERROR, element, holderToUse);
        }
        
        // Attribute errors
        else if (elementType.equals(LatteTokenTypes.LATTE_ERROR_INVALID_ATTRIBUTE_SYNTAX)) {
            LatteErrorReporter.logAndReportError("Invalid attribute syntax", HighlightSeverity.ERROR, element, holderToUse);
        } else if (elementType.equals(LatteTokenTypes.LATTE_ERROR_UNCLOSED_ATTRIBUTE_QUOTES)) {
            LatteErrorReporter.logAndReportError("Unclosed attribute quotes", HighlightSeverity.ERROR, element, holderToUse);
        }
        
        // Filter errors
        else if (elementType.equals(LatteTokenTypes.LATTE_ERROR_INVALID_FILTER_SYNTAX)) {
            LatteErrorReporter.logAndReportError("Invalid filter syntax", HighlightSeverity.ERROR, element, holderToUse);
        } else if (elementType.equals(LatteTokenTypes.LATTE_ERROR_UNKNOWN_FILTER)) {
            LatteErrorReporter.logAndReportError("Unknown filter", HighlightSeverity.WARNING, element, holderToUse);
        }
        
        // General errors
        else if (elementType.equals(LatteTokenTypes.LATTE_ERROR_UNEXPECTED_CHARACTER)) {
            LatteErrorReporter.logAndReportError("Unexpected character", HighlightSeverity.ERROR, element, holderToUse);
        }
        
        // For all other elements, holderToUse is used to ensure all annotations are properly handled
        // In development mode, this will log all annotations, including those from HTML validator
        // In production mode, this will simply create annotations without logging
    }
}