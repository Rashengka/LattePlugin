package cz.hqm.latte.plugin.validator;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.tree.IElementType;
import cz.hqm.latte.plugin.lexer.LatteTokenTypes;
import cz.hqm.latte.plugin.util.LatteLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Validator for Latte files.
 * Detects and reports syntax errors in Latte files.
 */
public class LatteValidator {
    private static final Logger LOG = Logger.getInstance(LatteValidator.class);
    
    /**
     * Maximum number of lines to display in element text logging.
     */
    private static final int MAX_ELEMENT_TEXT_LINES = 10;

    /**
     * Truncates element text to a maximum number of lines.
     * If the text is longer than MAX_ELEMENT_TEXT_LINES, it will be truncated and "..." will be added.
     * If the text is multi-line, it will be printed on a new line and this line won't count towards the limit.
     *
     * @param text The text to truncate
     * @return The truncated text
     */
    public static String truncateElementText(@Nullable String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        // Check if the text is multi-line
        boolean isMultiLine = text.contains("\n") || text.contains("\r");
        
        // Split the text into lines
        String[] lines = text.split("\\R", MAX_ELEMENT_TEXT_LINES + 1);
        
        // Prepare the result
        StringBuilder result = new StringBuilder();
        
        // If multi-line, add a newline character at the beginning
        if (isMultiLine) {
            result.append("\n");
        }
        
        // If the text has more than MAX_ELEMENT_TEXT_LINES lines, truncate it
        if (lines.length > MAX_ELEMENT_TEXT_LINES) {
            for (int i = 0; i < MAX_ELEMENT_TEXT_LINES; i++) {
                result.append(lines[i]).append("\n");
            }
            result.append("...");
            return result.toString();
        }
        
        // Otherwise, return the original text with a newline at the beginning if multi-line
        if (isMultiLine) {
            // For multi-line text, return a newline followed by the original text
            return "\n" + text;
        } else {
            // For single-line text, return the original text
            return text;
        }
    }

    // Built-in macro names (common macros in Latte)
    private static final Set<String> BUILT_IN_MACRO_NAMES = new HashSet<>(Arrays.asList(
            "if", "else", "elseif", "ifset", "ifCurrent", "foreach", "for", "while",
            "first", "last", "sep", "include", "extends", "layout", "block", "define",
            "snippet", "snippetArea", "capture", "var", "default", "dump", "debugbreak",
            "l", "r", "syntax", "use", "_", "=", "contentType", "status", "php",
            "do", "varType", "templateType", "parameters"
    ));

    // Valid attribute names
    private static final Set<String> VALID_ATTRIBUTE_NAMES = new HashSet<>(Arrays.asList(
            "n:if", "n:ifset", "n:foreach", "n:inner-foreach", "n:class", "n:attr", "n:tag",
            "n:snippet", "n:block", "n:include", "n:inner-if", "n:inner-ifset", "n:ifcontent",
            "n:href", "n:name", "n:nonce", "n:syntax"
    ));

    // Valid attribute prefixes
    private static final Set<String> VALID_ATTRIBUTE_PREFIXES = new HashSet<>(Arrays.asList(
            "n:", "n:inner-", "n:tag-", "n:class-", "n:attr-", 
            // Support for prefixed n:attributes (e.g., n:class:hover)
            "n:class:", "n:attr:", "n:tag:", "n:data-"
    ));

    /**
     * Validates a Latte file and reports any errors.
     *
     * @param file The file to validate
     */
    public static void validateFile(@NotNull PsiFile file) {
        LatteLogger.debug(LOG, "Validating Latte file: " + file.getName());
        
        // Log a test validation error to confirm that the logging mechanism is working
        LatteLogger.logValidationError(LOG, "Starting validation of Latte file", 
                                     file.getName(), 0,
                                     file.getVirtualFile(), file.getProject());
        
        // Visit all elements in the file and validate them
        file.accept(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                validateElement(element);
                super.visitElement(element);
            }
        });
    }

    /**
     * Validates a single PsiElement and reports any errors.
     *
     * @param element The element to validate
     */
    private static void validateElement(@NotNull PsiElement element) {
        IElementType elementType = element.getNode().getElementType();
        
        // Log that we're validating this element
        LatteLogger.debug(LOG, "Validating element: " + truncateElementText(element.getText()) + " of type: " + elementType);
        
        // Check for error token types
        if (elementType.equals(LatteTokenTypes.LATTE_ERROR_UNCLOSED_MACRO)) {
            reportError("Unclosed macro", HighlightSeverity.ERROR, element);
        } else if (elementType.equals(LatteTokenTypes.LATTE_ERROR_INVALID_MACRO_NAME)) {
            reportError("Invalid macro name", HighlightSeverity.ERROR, element);
        } else if (elementType.equals(LatteTokenTypes.LATTE_ERROR_MISMATCHED_MACRO_END)) {
            reportError("Mismatched macro end", HighlightSeverity.ERROR, element);
        } else if (elementType.equals(LatteTokenTypes.LATTE_ERROR_UNEXPECTED_MACRO_END)) {
            reportError("Unexpected macro end", HighlightSeverity.ERROR, element);
        } else if (elementType.equals(LatteTokenTypes.LATTE_ERROR_INVALID_ATTRIBUTE_SYNTAX)) {
            reportError("Invalid attribute syntax", HighlightSeverity.ERROR, element);
        } else if (elementType.equals(LatteTokenTypes.LATTE_ERROR_UNCLOSED_ATTRIBUTE_QUOTES)) {
            reportError("Unclosed attribute quotes", HighlightSeverity.ERROR, element);
        } else if (elementType.equals(LatteTokenTypes.LATTE_ERROR_INVALID_FILTER_SYNTAX)) {
            reportError("Invalid filter syntax", HighlightSeverity.ERROR, element);
        } else if (elementType.equals(LatteTokenTypes.LATTE_ERROR_UNKNOWN_FILTER)) {
            reportError("Unknown filter", HighlightSeverity.WARNING, element);
        } else if (elementType.equals(LatteTokenTypes.LATTE_ERROR_UNEXPECTED_CHARACTER)) {
            reportError("Unexpected character", HighlightSeverity.ERROR, element);
        }
        
        // Additional validation for specific element types
        if (elementType.equals(LatteTokenTypes.LATTE_MACRO_NAME)) {
            validateMacroName(element);
        } else if (elementType.equals(LatteTokenTypes.LATTE_ATTRIBUTE_NAME)) {
            validateAttributeName(element);
        } else if (elementType.equals(LatteTokenTypes.LATTE_FILTER_NAME)) {
            validateFilterName(element);
        }
    }

    /**
     * Validates a macro name and reports any errors.
     *
     * @param element The element containing the macro name
     */
    private static void validateMacroName(@NotNull PsiElement element) {
        String macroName = element.getText();
        
        // Check if it's a valid macro name
        if (!BUILT_IN_MACRO_NAMES.contains(macroName)) {
            reportError("Unknown macro: " + macroName, HighlightSeverity.WARNING, element);
        }
        
        // Add validation for {else} and {elseif} tags
        if ("else".equals(macroName) || "elseif".equals(macroName)) {
            boolean isInsideIfBlock = checkIfInsideIfBlock(element);
            if (!isInsideIfBlock) {
                // Report error for standalone {else} or {elseif} tag
                reportError("Tag {" + macroName + "} must be inside an {if} block", HighlightSeverity.ERROR, element);
            }
        }
    }
    
    /**
     * Checks if the given element is inside an {if} block.
     * 
     * @param element The element to check
     * @return true if the element is inside an {if} block, false otherwise
     */
    private static boolean checkIfInsideIfBlock(@NotNull PsiElement element) {
        // Get the parent element
        PsiElement parent = element.getParent();
        
        // Track the nesting level of if blocks
        int ifLevel = 0;
        
        // Iterate through previous siblings to find an {if} tag
        PsiElement prevSibling = element.getPrevSibling();
        while (prevSibling != null) {
            // Check if this is a macro name element
            if (prevSibling.getNode().getElementType().equals(LatteTokenTypes.LATTE_MACRO_NAME)) {
                String macroName = prevSibling.getText();
                
                // Check for if/else tags
                if ("if".equals(macroName)) {
                    ifLevel++;
                } else if ("/if".equals(macroName)) {
                    ifLevel--;
                }
            }
            
            // If we found an open if block, return true
            if (ifLevel > 0) {
                return true;
            }
            
            // Move to the previous sibling
            prevSibling = prevSibling.getPrevSibling();
        }
        
        // If we didn't find an open if block, check parent elements
        while (parent != null) {
            // Check if this parent contains an {if} tag before our element
            PsiElement[] children = parent.getChildren();
            boolean foundOurElement = false;
            ifLevel = 0;
            
            for (PsiElement child : children) {
                // Once we reach our element, stop checking
                if (child == element) {
                    foundOurElement = true;
                    break;
                }
                
                // Check if this is a macro name element
                if (child.getNode().getElementType().equals(LatteTokenTypes.LATTE_MACRO_NAME)) {
                    String macroName = child.getText();
                    
                    // Check for if/else tags
                    if ("if".equals(macroName)) {
                        ifLevel++;
                    } else if ("/if".equals(macroName)) {
                        ifLevel--;
                    }
                }
            }
            
            // If we found an open if block, return true
            if (ifLevel > 0) {
                return true;
            }
            
            // Move up to the parent
            parent = parent.getParent();
        }
        
        // If we didn't find an open if block, return false
        return false;
    }

    /**
     * Validates an attribute name and reports any errors.
     *
     * @param element The element containing the attribute name
     */
    private static void validateAttributeName(@NotNull PsiElement element) {
        String attributeName = element.getText();
        
        // Check if it's a valid attribute name
        if (!VALID_ATTRIBUTE_NAMES.contains(attributeName) && 
                !VALID_ATTRIBUTE_PREFIXES.stream().anyMatch(prefix -> attributeName.startsWith(prefix))) {
            reportError("Unknown attribute: " + attributeName, HighlightSeverity.WARNING, element);
        }
    }

    /**
     * Validates a filter name and reports any errors.
     *
     * @param element The element containing the filter name
     */
    private static void validateFilterName(@NotNull PsiElement element) {
        String filterName = element.getText();
        
        // For now, we don't have a list of valid filter names
        // This could be enhanced to check against a list of known filters
    }

    /**
     * Reports an error for the given element.
     *
     * @param message The error message
     * @param severity The severity of the error
     * @param element The element with the error
     */
    private static void reportError(@NotNull String message, @NotNull HighlightSeverity severity, @NotNull PsiElement element) {
        // Log the error
        LatteLogger.debug(LOG, "Reporting error: " + message + " for element: " + truncateElementText(element.getText()));
        
        // Log the validation error
        LatteErrorReporter.logValidationError(message, element);
        
        // Get the virtual file and project for more detailed logging
        VirtualFile file = element.getContainingFile().getVirtualFile();
        Project project = element.getProject();
        
        // Log with file and project information
        LatteLogger.logValidationError(LOG, message, truncateElementText(element.getText()), element.getTextOffset(), file, project);
    }
}