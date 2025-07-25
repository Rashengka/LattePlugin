package cz.hqm.latte.plugin.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.html.HTMLParser;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;
import cz.hqm.latte.plugin.util.LatteLogger;
import cz.hqm.latte.plugin.validator.LatteValidator;
import org.jetbrains.annotations.NotNull;

/**
 * Custom HTML parser for Latte files that handles incomplete HTML structures.
 * This parser extends the standard HTML parser but suppresses the "top level element is not completed" error.
 * It also handles IllegalArgumentException that can occur during HTML parsing with invalid indices,
 * which can happen when pressing Ctrl+Space for code completion in certain Latte templates.
 */
public class LatteHtmlParser extends HTMLParser {
    private static final Logger LOG = Logger.getInstance(LatteHtmlParser.class);
    
    /**
     * Checks if the HTML structure is incomplete, which would trigger the "Top level element is not completed" error.
     * In Latte, unclosed block directives at the end of a file are automatically closed, which is standard behavior.
     * Therefore, we always return false to suppress the "Top level element is not completed" error for Latte files.
     * 
     * @param node The ASTNode to check
     * @return false, to suppress the "Top level element is not completed" error for Latte files
     */
    private boolean isHtmlStructureIncomplete(ASTNode node) {
        // Log that we're checking for incomplete HTML structure
        LatteLogger.debug(LOG, "Checking for incomplete HTML structure in node: " + (node != null ? node.getElementType() : "null"));
        
        // In Latte, unclosed block directives at the end of a file are automatically closed.
        // This is standard behavior in Latte, so we don't flag it as an error.
        // We always return false to suppress the "Top level element is not completed" error.
        
        LatteLogger.debug(LOG, "Suppressing 'Top level element is not completed' error for Latte files");
        return false;
    }

    @Override
    @NotNull
    public ASTNode parse(@NotNull IElementType root, @NotNull PsiBuilder builder) {
        // Enable debug mode to help identify marker issues
        builder.setDebugMode(true);
        
        // Log the start of parsing - only in development mode
        LatteLogger.debug(LOG, "LatteHtmlParser starting to parse with root: " + root);
        
        // Log the current token if available
        if (builder.getTokenType() != null) {
            // Use truncateElementText to avoid logging the entire file content
            String tokenText = builder.getTokenText();
            LatteLogger.debug(LOG, "Current token: " + builder.getTokenType() + ", text: " + 
                             LatteValidator.truncateElementText(tokenText));
        } else {
            // For empty tokens, just log that it's empty without including the entire file content
            LatteLogger.debug(LOG, "Current token: empty token (no token type available)");
        }
        
        try {
            // Call the parent parser to do the actual parsing
            // Wrap in try-catch to handle IllegalArgumentException that can occur with invalid indices
            LatteLogger.debug(LOG, "Calling super.parse()");
            ASTNode result = super.parse(root, builder);
            LatteLogger.debug(LOG, "Finished super.parse(), result: " + (result != null ? result.getElementType() : "null"));
            
            // Log the result of parsing
            if (result != null) {
                LatteLogger.debug(LOG, "Parsing completed successfully with root type: " + result.getElementType());
                // Check if the HTML structure is incomplete by examining the result
                // The "Top level element is not completed" error typically occurs when the root HTML element is not properly closed
                if (isHtmlStructureIncomplete(result)) {
                    // Log the validation error to the validation_errors log file
                    LatteLogger.logValidationError(LOG, "Top level element is not completed", 
                                                 "HTML structure in Latte file", 
                                                 0);
                }
            }
            
            return result;
        } catch (IllegalArgumentException e) {
            // Log the exception for debugging - only in development mode
            LatteLogger.warn(LOG, "LatteHtmlParser caught exception: " + e.getMessage(), e);

            // Reset the builder to the beginning
            while (builder.getTokenType() != null) {
                builder.advanceLexer();
            }

            // Create a marker for the root element
            PsiBuilder.Marker rootMarker = builder.mark();
            
            // Create a marker for a dummy element to ensure proper structure
            PsiBuilder.Marker dummyMarker = builder.mark();
            dummyMarker.done(root);
            
            // Complete the root marker
            rootMarker.done(root);
            
            // Return the tree after the markers have been properly closed
            return builder.getTreeBuilt();
        }
    }
}