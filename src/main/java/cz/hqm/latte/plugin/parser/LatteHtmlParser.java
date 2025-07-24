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
     * This is a simple implementation that checks if the ASTNode has certain characteristics that indicate an incomplete HTML structure.
     * 
     * @param node The ASTNode to check
     * @return true if the HTML structure is likely incomplete, false otherwise
     */
    private boolean isHtmlStructureIncomplete(ASTNode node) {
        // Log that we're checking for incomplete HTML structure
        LatteLogger.debug(LOG, "Checking for incomplete HTML structure in node: " + (node != null ? node.getElementType() : "null"));
        
        if (node == null) {
            LatteLogger.debug(LOG, "Node is null, structure is incomplete");
            return true; // If the node is null, the structure is definitely incomplete
        }
        
        // Check if the node has children
        ASTNode[] children = node.getChildren(null);
        LatteLogger.debug(LOG, "Node has " + children.length + " children");
        if (children.length == 0) {
            LatteLogger.debug(LOG, "Node has no children, structure is likely incomplete");
            return true; // If the node has no children, it's likely incomplete
        }
        
        // IMPORTANT: We're completely avoiding accessing the PSI element's containing file
        // as it can cause PsiInvalidElementAccessException with NULL_PSI_ELEMENT
        
        // If we have at least one child, assume the structure is complete
        LatteLogger.debug(LOG, "Node has children, structure is likely complete");
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