package cz.hqm.latte.plugin.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.lang.html.HtmlParsing;
import com.intellij.openapi.diagnostic.ControlFlowException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import cz.hqm.latte.plugin.parser.LatteHtmlParsing;
import cz.hqm.latte.plugin.util.LatteLogger;
import cz.hqm.latte.plugin.validator.LatteValidator;
import org.jetbrains.annotations.NotNull;

/**
 * Custom HTML parser for Latte files that handles incomplete HTML structures.
 * This parser implements PsiParser directly and suppresses the "top level element is not completed" error.
 * It also handles IllegalArgumentException that can occur during HTML parsing with invalid indices,
 * which can happen when pressing Ctrl+Space for code completion in certain Latte templates.
 */
public class LatteHtmlParser implements PsiParser {
    private static final Logger LOG = Logger.getInstance(LatteHtmlParser.class);
    
    // Maximum number of tokens to process before timing out
    private static final int MAX_TOKENS_TO_PROCESS = 100000;

    /**
     * Creates an instance of SafeLatteHtmlParsing for the given builder.
     * This method returns a SafeLatteHtmlParsing instance that adds token count limits
     * and timeout mechanisms to prevent infinite loops during parsing.
     * 
     * @param builder The PsiBuilder to create SafeLatteHtmlParsing for
     * @return A new SafeLatteHtmlParsing instance
     */
    protected @NotNull HtmlParsing createHtmlParsing(@NotNull PsiBuilder builder) {
        return new SafeLatteHtmlParsing(builder);
    }
    
    /**
     * Parses the input without building a tree.
     * 
     * @param root The root element type
     * @param builder The PsiBuilder to use
     */
    public void parseWithoutBuildingTree(@NotNull IElementType root, @NotNull PsiBuilder builder) {
        parseWithoutBuildingTree(root, builder, this.createHtmlParsing(builder));
    }
    
    /**
     * Parses the input without building a tree using the provided HtmlParsing.
     * This method ensures that the "Top level element is not completed" error is suppressed
     * by creating a proper tree structure with a parent-child relationship.
     * 
     * The actual parsing is delegated to the SafeLatteHtmlParsing class, which adds
     * token count limits and timeout mechanisms to prevent infinite loops during parsing.
     * 
     * @param root The root element type
     * @param builder The PsiBuilder to use
     * @param htmlParsing The HtmlParsing to use (should be an instance of SafeLatteHtmlParsing)
     */
    private static void parseWithoutBuildingTree(@NotNull IElementType root, @NotNull PsiBuilder builder, @NotNull HtmlParsing htmlParsing) {
        builder.enforceCommentTokens(TokenSet.EMPTY);
        
        // Create a marker for the root element
        PsiBuilder.Marker rootMarker = builder.mark();
        
        try {
            // Start a timer to measure parsing time
            long startTime = System.currentTimeMillis();
            
            // Parse the document using the SafeLatteHtmlParsing instance
            // which has built-in token count limits and timeout mechanisms
            htmlParsing.parseDocument();
            
            // Log the parsing time
            long endTime = System.currentTimeMillis();
            LatteLogger.debug(LOG, "Parsing completed in " + (endTime - startTime) + "ms");
            
            // Complete the root marker after successful parsing
            rootMarker.done(root);
        } catch (Exception e) {
            // Complete the root marker even when an exception occurs
            rootMarker.done(root);
            
            // Check if the exception is a control flow exception
            if (e instanceof ControlFlowException) {
                // Control flow exceptions should be propagated, not logged
                throw e;
            } else {
                // Log any other exceptions that might occur
                LatteLogger.warn(LOG, "Exception during parsing: " + e.getMessage(), e);
            }
        }
        
        // Note: rootMarker is now completed in all code paths above, so we don't need to do it here
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
            LatteLogger.debug(LOG, "Current token: empty token, text: " + 
                             (builder.getTokenText() != null ? LatteValidator.truncateElementText(builder.getTokenText()) : "{}"));
        }
        
        try {
            // Start a timer to measure parsing time
            long startTime = System.currentTimeMillis();
            
            // Do the actual parsing
            // Wrap in try-catch to handle IllegalArgumentException that can occur with invalid indices
            LatteLogger.debug(LOG, "Starting parse with SafeLatteHtmlParsing (timeout: " + 
                             SafeLatteHtmlParsing.MAX_PARSING_TIME_MS + "ms, max tokens: " + 
                             SafeLatteHtmlParsing.MAX_TOKENS_TO_PROCESS + ")");
            
            // Parse without building the tree
            parseWithoutBuildingTree(root, builder);
            
            // Log the parsing time
            long endTime = System.currentTimeMillis();
            LatteLogger.debug(LOG, "Parsing completed in " + (endTime - startTime) + "ms");
            
            // Get the built tree
            ASTNode result = builder.getTreeBuilt();
            
            LatteLogger.debug(LOG, "Finished parse, result: " + (result != null ? result.getElementType() : "null"));
            
            // Log the result of parsing
            if (result != null) {
                LatteLogger.debug(LOG, "Parsing completed successfully with root type: " + result.getElementType());
                // As per the issue description "neřeš teď top levele element is not completed"
                // (don't worry about the top level element is not completed error now),
                // we're not checking for incomplete HTML structure here
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
        } catch (Exception e) {
            // Check if the exception is a control flow exception
            if (e instanceof ControlFlowException) {
                // Control flow exceptions should be propagated, not logged
                // This allows the IDE to properly handle control flow
                throw e;
            }
            
            // Log any other exceptions that might occur
            LatteLogger.warn(LOG, "LatteHtmlParser caught unexpected exception: " + e.getMessage(), e);
            
            // Create a minimal valid tree structure
            PsiBuilder.Marker rootMarker = builder.mark();
            PsiBuilder.Marker dummyMarker = builder.mark();
            dummyMarker.done(root);
            rootMarker.done(root);
            
            // Return the tree after the markers have been properly closed
            return builder.getTreeBuilt();
        }
    }
}