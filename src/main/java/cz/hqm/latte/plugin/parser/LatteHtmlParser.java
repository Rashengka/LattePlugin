package cz.hqm.latte.plugin.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.html.HTMLParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

/**
 * Custom HTML parser for Latte files that handles incomplete HTML structures.
 * This parser extends the standard HTML parser but suppresses the "top level element is not completed" error.
 * It also handles IllegalArgumentException that can occur during HTML parsing with invalid indices,
 * which can happen when pressing Ctrl+Space for code completion in certain Latte templates.
 */
public class LatteHtmlParser extends HTMLParser {

    @Override
    @NotNull
    public ASTNode parse(@NotNull IElementType root, @NotNull PsiBuilder builder) {
        // Enable debug mode to help identify marker issues
        builder.setDebugMode(true);
        
        // Create a marker that will always be completed
        PsiBuilder.Marker marker = builder.mark();
        
        ASTNode result;
        try {
            // Call the parent parser to do the actual parsing
            // Wrap in try-catch to handle IllegalArgumentException that can occur with invalid indices
            result = super.parse(root, builder);
        } catch (IllegalArgumentException e) {
            // Log the exception for debugging
            System.err.println("LatteHtmlParser caught exception: " + e.getMessage());
            
            // Reset the builder to the beginning
            while (builder.getTokenType() != null) {
                builder.advanceLexer();
            }
            
            // Create a minimal valid tree
            PsiBuilder.Marker rootMarker = builder.mark();
            rootMarker.done(root);
            
            // Get the tree but don't return it yet
            result = builder.getTreeBuilt();
        } finally {
            // Always complete the marker to ensure proper structure
            marker.done(root);
        }
        
        // Return the result after the marker has been properly closed
        return result;
    }
}