package cz.hqm.latte.plugin.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.html.HtmlParsing;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.tree.ICustomParsingType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.ILazyParseableElementType;
import com.intellij.psi.xml.XmlElementType;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.util.Processor;
import com.intellij.util.ThreeState;
import com.intellij.util.containers.Stack;
import com.intellij.xml.psi.XmlPsiBundle;
import com.intellij.xml.util.HtmlUtil;
import cz.hqm.latte.plugin.util.LatteLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Custom HTML parsing for Latte templates that supports Latte syntax.
 * This class extends HtmlParsing to add support for Latte macros in different syntax versions:
 * - off: {* comment *}
 * - single: {macro}
 * - double: {{macro}}
 */
public class LatteHtmlParsing extends HtmlParsing {
    private static final Logger LOG = Logger.getInstance(LatteHtmlParsing.class);
    private static final String COMPLETION_NAME = StringUtil.toLowerCase("IntellijIdeaRulezzz");
    
    // Constants to prevent infinite loops
    private static final int MAX_MACRO_ITERATIONS = 10000; // Maximum iterations when parsing a single macro
    private static final int MAX_PARSING_DEPTH = 100; // Maximum recursion depth for parsing
    
    // Track current parsing depth to prevent stack overflow
    private int currentParsingDepth = 0;
    
    // Track the nesting level of if blocks
    private int ifBlockLevel = 0;
    
    /**
     * Checks if the current parsing depth exceeds the maximum allowed depth.
     * 
     * @return true if the maximum depth has been reached, false otherwise
     */
    private boolean isMaxDepthReached() {
        return currentParsingDepth >= MAX_PARSING_DEPTH;
    }
    
    /**
     * Increments the current parsing depth and checks if the maximum depth has been reached.
     * 
     * @return true if parsing can continue, false if the maximum depth has been reached
     */
    private boolean incrementDepth() {
        currentParsingDepth++;
        if (isMaxDepthReached()) {
            LatteLogger.warn(LOG, "Maximum parsing depth reached: " + MAX_PARSING_DEPTH + 
                           ". Stopping to prevent infinite recursion.");
            return false;
        }
        return true;
    }
    
    /**
     * Decrements the current parsing depth.
     */
    private void decrementDepth() {
        if (currentParsingDepth > 0) {
            currentParsingDepth--;
        }
    }

    /**
     * Creates a new instance of LatteHtmlParsing.
     *
     * @param builder The PsiBuilder to use for parsing
     */
    public LatteHtmlParsing(PsiBuilder builder) {
        super(builder);
    }

    /**
     * Overrides the parseDocument method to add depth tracking.
     * This is the main entry point for parsing.
     */
    @Override
    public void parseDocument() {
        // Reset counters at the start of document parsing
        currentParsingDepth = 0;
        ifBlockLevel = 0;
        
        // Call the parent implementation
        super.parseDocument();
    }
    
    /**
     * Overrides the parseTag method to add depth tracking.
     * This method is recursive when handling nested tags.
     */
    @Override
    public void parseTag() {
        // Check if we've reached the maximum parsing depth
        if (!incrementDepth()) {
            // If max depth reached, create a simple tag and return
            if (token() == XmlTokenType.XML_START_TAG_START) {
                PsiBuilder.Marker tag = mark();
                advance(); // consume start tag
                
                // Consume tag name if present
                if (token() == XmlTokenType.XML_NAME) {
                    advance();
                }
                
                // Consume until tag end
                while (!eof() && token() != XmlTokenType.XML_TAG_END && token() != XmlTokenType.XML_EMPTY_ELEMENT_END) {
                    advance();
                }
                
                // Consume tag end if present
                if (token() == XmlTokenType.XML_TAG_END || token() == XmlTokenType.XML_EMPTY_ELEMENT_END) {
                    advance();
                }
                
                tag.done(XmlElementType.HTML_TAG);
            }
            return;
        }
        
        try {
            // Call the parent implementation
            super.parseTag();
        } finally {
            // Always decrement the depth counter when exiting
            decrementDepth();
        }
    }
    
    @Override
    protected boolean hasCustomTopLevelContent() {
        // Allow Latte macros at the top level
        return true;
    }

    @Override
    protected PsiBuilder.@Nullable Marker parseCustomTopLevelContent(@Nullable PsiBuilder.@Nullable Marker error) {
        // Handle Latte macros at the top level
        if (isLatteMacro()) {
            error = flushError(error);
            parseLatteMacro();
            return error;
        }
        return super.parseCustomTopLevelContent(error);
    }
    
    /**
     * Overrides the parseAttribute method to add depth tracking.
     */
    @Override
    protected void parseAttribute() {
        // Check if we've reached the maximum parsing depth
        if (!incrementDepth()) {
            // If max depth reached, consume the attribute name and return
            if (token() == XmlTokenType.XML_NAME) {
                advance();
            }
            decrementDepth();
            return;
        }
        
        try {
            // Call the parent implementation
            super.parseAttribute();
        } finally {
            // Always decrement the depth counter when exiting
            decrementDepth();
        }
    }
    
    /**
     * Overrides the parseAttributeValue method to add depth tracking.
     */
    @Override
    protected void parseAttributeValue() {
        // Check if we've reached the maximum parsing depth
        if (!incrementDepth()) {
            // If max depth reached, consume until attribute value end and return
            PsiBuilder.Marker attValue = mark();
            
            if (token() == XmlTokenType.XML_ATTRIBUTE_VALUE_START_DELIMITER) {
                advance();
                while (!eof() && token() != XmlTokenType.XML_ATTRIBUTE_VALUE_END_DELIMITER) {
                    advance();
                }
                if (token() == XmlTokenType.XML_ATTRIBUTE_VALUE_END_DELIMITER) {
                    advance();
                }
            } else if (token() != XmlTokenType.XML_TAG_END && token() != XmlTokenType.XML_EMPTY_ELEMENT_END) {
                advance();
            }
            
            attValue.done(getHtmlAttributeValueElementType());
            decrementDepth();
            return;
        }
        
        try {
            // Call the parent implementation
            super.parseAttributeValue();
        } finally {
            // Always decrement the depth counter when exiting
            decrementDepth();
        }
    }

    @Override
    protected boolean hasCustomTagContent() {
        // Allow Latte macros inside tags
        return true;
    }

    @Override
    protected PsiBuilder.@Nullable Marker parseCustomTagContent(@Nullable PsiBuilder.@Nullable Marker xmlText) {
        // Check if we've reached the maximum parsing depth
        if (!incrementDepth()) {
            LatteLogger.warn(LOG, "Maximum parsing depth reached in parseCustomTagContent. Skipping custom content parsing.");
            decrementDepth();
            return xmlText;
        }
        
        try {
            // Handle Latte macros inside tags
            if (isLatteMacro()) {
                xmlText = terminateText(xmlText);
                parseLatteMacro();
                return xmlText;
            }
            return super.parseCustomTagContent(xmlText);
        } finally {
            // Always decrement the depth counter when exiting
            decrementDepth();
        }
    }

    @Override
    protected boolean hasCustomTagHeaderContent() {
        // Allow Latte macros in tag headers
        return true;
    }

    @Override
    protected void parseCustomTagHeaderContent() {
        // Check if we've reached the maximum parsing depth
        if (!incrementDepth()) {
            LatteLogger.warn(LOG, "Maximum parsing depth reached in parseCustomTagHeaderContent. Skipping custom header parsing.");
            decrementDepth();
            return;
        }
        
        try {
            // Handle Latte macros in tag headers
            if (isLatteMacro()) {
                parseLatteMacro();
            } else {
                super.parseCustomTagHeaderContent();
            }
        } finally {
            // Always decrement the depth counter when exiting
            decrementDepth();
        }
    }

    @Override
    protected boolean hasCustomAttributeValue() {
        // Allow Latte macros in attribute values
        return isLatteMacro() || super.hasCustomAttributeValue();
    }

    @Override
    protected void parseCustomAttributeValue() {
        // Check if we've reached the maximum parsing depth
        if (!incrementDepth()) {
            LatteLogger.warn(LOG, "Maximum parsing depth reached in parseCustomAttributeValue. Skipping custom attribute value parsing.");
            decrementDepth();
            return;
        }
        
        try {
            // Handle Latte macros in attribute values
            if (isLatteMacro()) {
                parseLatteMacro();
            } else {
                super.parseCustomAttributeValue();
            }
        } finally {
            // Always decrement the depth counter when exiting
            decrementDepth();
        }
    }

    /**
     * Checks if the current token is the start of a Latte macro.
     *
     * @return true if the current token is the start of a Latte macro, false otherwise
     */
    private boolean isLatteMacro() {
        IElementType tokenType = token();
        if (tokenType == XmlTokenType.XML_DATA_CHARACTERS || tokenType == XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN) {
            String text = getBuilder().getTokenText();
            if (text != null) {
                // Check for different Latte syntax versions
                // Single brace syntax: {macro}
                if (text.startsWith("{") && !text.startsWith("{{") && !text.startsWith("{*")) {
                    return true;
                }
                // Double brace syntax: {{macro}}
                if (text.startsWith("{{")) {
                    return true;
                }
                // Comment syntax: {* comment *}
                if (text.startsWith("{*")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Parses a Latte macro.
     */
    private void parseLatteMacro() {
        PsiBuilder.Marker macro = mark();
        
        // Get the macro text before consuming it
        String macroText = getBuilder().getTokenText();
        
        // Extract the macro name from the text
        String macroName = extractMacroName(macroText);
        
        // Check if this is an else or elseif tag outside of an if block
        if ((macroName.equals("else") || macroName.equals("elseif")) && ifBlockLevel <= 0) {
            // Consume the macro content
            advance();
            
            // Continue consuming tokens until we find the end of the macro
            // Add iteration limit to prevent infinite loops
            int iterations = 0;
            while (!eof()) {
                // Check if we've exceeded the maximum number of iterations
                if (++iterations > MAX_MACRO_ITERATIONS) {
                    LatteLogger.warn(LOG, "Maximum iterations reached while parsing Latte macro: " + MAX_MACRO_ITERATIONS + 
                                   ". Stopping to prevent infinite loop.");
                    break;
                }
                
                IElementType tokenType = token();
                String text = getBuilder().getTokenText();
                
                if (text != null) {
                    // Check for the end of different Latte syntax versions
                    if (text.endsWith("}") || text.endsWith("}}") || text.endsWith("*}")) {
                        advance();
                        break;
                    }
                }
                
                advance();
            }
            
            // Mark the macro as an error element
            macro.error("Tag {" + macroName + "} must be inside an {if} block");
        } else {
            // Update the if block level if this is an if or /if tag
            if (macroName.equals("if")) {
                ifBlockLevel++;
            } else if (macroName.equals("/if")) {
                ifBlockLevel--;
                // Ensure ifBlockLevel doesn't go below 0
                if (ifBlockLevel < 0) {
                    ifBlockLevel = 0;
                }
            }
            
            // Consume the macro content
            advance();
            
            // Continue consuming tokens until we find the end of the macro
            // Add iteration limit to prevent infinite loops
            int iterations = 0;
            while (!eof()) {
                // Check if we've exceeded the maximum number of iterations
                if (++iterations > MAX_MACRO_ITERATIONS) {
                    LatteLogger.warn(LOG, "Maximum iterations reached while parsing Latte macro: " + MAX_MACRO_ITERATIONS + 
                                   ". Stopping to prevent infinite loop.");
                    break;
                }
                
                IElementType tokenType = token();
                String text = getBuilder().getTokenText();
                
                if (text != null) {
                    // Check for the end of different Latte syntax versions
                    if (text.endsWith("}") || text.endsWith("}}") || text.endsWith("*}")) {
                        advance();
                        break;
                    }
                }
                
                advance();
            }
            
            // Mark the macro as a custom element
            macro.done(XmlElementType.XML_TEXT);
        }
    }
    
    /**
     * Extracts the macro name from the macro text.
     * 
     * @param macroText The text of the macro
     * @return The name of the macro
     */
    private String extractMacroName(String macroText) {
        if (macroText == null || macroText.isEmpty()) {
            return "";
        }
        
        // Remove the opening brace(s)
        String content = macroText;
        if (content.startsWith("{{")) {
            content = content.substring(2);
        } else if (content.startsWith("{")) {
            content = content.substring(1);
        }
        
        // Remove the closing brace(s) if present
        if (content.endsWith("}}")) {
            content = content.substring(0, content.length() - 2);
        } else if (content.endsWith("}")) {
            content = content.substring(0, content.length() - 1);
        }
        
        // Extract the macro name (everything up to the first whitespace)
        int spaceIndex = content.indexOf(' ');
        if (spaceIndex > 0) {
            content = content.substring(0, spaceIndex);
        }
        
        // Trim any remaining whitespace
        content = content.trim();
        
        return content;
    }
}