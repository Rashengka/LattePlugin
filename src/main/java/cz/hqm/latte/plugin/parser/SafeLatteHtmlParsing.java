package cz.hqm.latte.plugin.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.openapi.diagnostic.ControlFlowException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlElementType;
import com.intellij.psi.xml.XmlTokenType;
import cz.hqm.latte.plugin.util.LatteLogger;
import org.jetbrains.annotations.NotNull;

/**
 * A safe wrapper around LatteHtmlParsing that adds token count limits to prevent infinite loops.
 * This class extends LatteHtmlParsing and overrides the parseDocument method to add a token count limit.
 */
public class SafeLatteHtmlParsing extends LatteHtmlParsing {
    private static final Logger LOG = Logger.getInstance(SafeLatteHtmlParsing.class);
    
    // Maximum number of tokens to process before timing out
    public static final int MAX_TOKENS_TO_PROCESS = 100000;
    
    // Maximum time to spend parsing (in milliseconds)
    public static final long MAX_PARSING_TIME_MS = 5000; // 5 seconds
    
    // Track the number of tokens processed
    private int tokensProcessed = 0;
    
    // Track the start time of parsing
    private long startTime;
    
    /**
     * Creates a new instance of SafeLatteHtmlParsing.
     *
     * @param builder The PsiBuilder to use for parsing
     */
    public SafeLatteHtmlParsing(PsiBuilder builder) {
        super(builder);
    }
    
    /**
     * Overrides the parseDocument method to add a token count limit.
     * This method will break out of the parsing loop if the token count exceeds MAX_TOKENS_TO_PROCESS
     * or if the parsing time exceeds MAX_PARSING_TIME_MS.
     */
    @Override
    public void parseDocument() {
        // Reset counters
        tokensProcessed = 0;
        startTime = System.currentTimeMillis();
        
        // Log the start of parsing
        LatteLogger.debug(LOG, "Starting safe parsing with token limit: " + MAX_TOKENS_TO_PROCESS);
        
        try {
            // Call the parent implementation with safety checks
            parseDocumentSafely();
        } catch (Exception e) {
            // Check if the exception is a control flow exception
            if (e instanceof ControlFlowException) {
                // Control flow exceptions should be propagated, not logged
                throw e;
            } else {
                // Log any other exceptions that might occur
                LatteLogger.warn(LOG, "Exception during safe parsing: " + e.getMessage(), e);
                
                // Create a minimal valid document structure
                createMinimalDocument();
            }
        }
        
        // Log the end of parsing
        long endTime = System.currentTimeMillis();
        LatteLogger.debug(LOG, "Finished safe parsing in " + (endTime - startTime) + "ms, processed " + tokensProcessed + " tokens");
    }
    
    /**
     * Parses the document with safety checks to prevent infinite loops.
     * This method will break out of the parsing loop if the token count exceeds MAX_TOKENS_TO_PROCESS
     * or if the parsing time exceeds MAX_PARSING_TIME_MS.
     */
    private void parseDocumentSafely() {
        // Save the current position to detect if parsing is making progress
        int lastPosition = getBuilder().getCurrentOffset();
        int stuckCount = 0;
        
        // Create a marker for the document
        PsiBuilder.Marker document = mark();
        
        // Process tokens until we reach the end or exceed the limits
        while (!eof() && tokensProcessed < MAX_TOKENS_TO_PROCESS) {
            // Check if we've exceeded the maximum parsing time
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime > MAX_PARSING_TIME_MS) {
                LatteLogger.warn(LOG, "Maximum parsing time reached: " + MAX_PARSING_TIME_MS + "ms. Stopping parsing.");
                break;
            }
            
            // Check if we're making progress
            int currentPosition = getBuilder().getCurrentOffset();
            if (currentPosition == lastPosition) {
                stuckCount++;
                
                // If we're stuck for too long, break out of the loop
                if (stuckCount > 100) {
                    LatteLogger.warn(LOG, "Parser appears to be stuck at position " + currentPosition + ". Stopping parsing.");
                    break;
                }
            } else {
                // Reset the stuck count if we're making progress
                stuckCount = 0;
                lastPosition = currentPosition;
            }
            
            // Process the current token
            IElementType tokenType = token();
            
            // Log the current token every 1000 tokens
            if (tokensProcessed % 1000 == 0) {
                LatteLogger.debug(LOG, "Processing token " + tokensProcessed + ": " + tokenType);
            }
            
            // Process the token based on its type
            if (tokenType == null) {
                // End of file
                break;
            } else {
                // Save the current position to track how many tokens are processed
                int beforePosition = getBuilder().getCurrentOffset();
                
                // Process the token based on its type
                if (tokenType == XmlTokenType.XML_START_TAG_START) {
                    // Parse HTML tag
                    parseTag();
                } else if (hasCustomTopLevelContent()) {
                    // Try to parse custom top-level content (including Latte macros)
                    PsiBuilder.Marker error = null;
                    error = parseCustomTopLevelContent(error);
                    
                    // If parseCustomTopLevelContent returned the same error marker,
                    // it means it didn't process anything, so we need to advance manually
                    if (beforePosition == getBuilder().getCurrentOffset()) {
                        advance();
                    }
                } else {
                    // For other token types, just advance
                    advance();
                }
                
                // Calculate how many tokens were processed
                int afterPosition = getBuilder().getCurrentOffset();
                int tokensProcessedInThisIteration = Math.max(1, afterPosition - beforePosition);
                tokensProcessed += tokensProcessedInThisIteration;
                
                // Log every 1000 tokens
                if (tokensProcessed % 1000 == 0) {
                    LatteLogger.debug(LOG, "Processed " + tokensProcessed + " tokens so far");
                }
            }
        }
        
        // Check if we've exceeded the token limit
        if (tokensProcessed >= MAX_TOKENS_TO_PROCESS) {
            LatteLogger.warn(LOG, "Maximum token count reached: " + MAX_TOKENS_TO_PROCESS + ". Stopping parsing.");
        }
        
        // Complete the document marker
        document.done(XmlElementType.HTML_DOCUMENT);
    }
    
    /**
     * Creates a minimal valid document structure.
     * This method is called when an exception occurs during parsing.
     */
    private void createMinimalDocument() {
        LatteLogger.debug(LOG, "Creating minimal document structure");
        
        // Create a marker for the document
        PsiBuilder.Marker document = mark();
        
        // Complete the document marker
        document.done(XmlElementType.HTML_DOCUMENT);
    }
}