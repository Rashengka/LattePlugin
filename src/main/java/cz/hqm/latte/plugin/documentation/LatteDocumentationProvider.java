package cz.hqm.latte.plugin.documentation;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.lang.documentation.DocumentationMarkup;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import cz.hqm.latte.plugin.macros.NetteMacro;
import cz.hqm.latte.plugin.macros.NetteMacroProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Documentation provider for Latte macros and attributes.
 * Provides documentation for Latte macros, n:attributes, and filters.
 */
public class LatteDocumentationProvider extends AbstractDocumentationProvider {
    
    private static final Map<String, String> MACRO_DOCS = new HashMap<>();
    private static final Map<String, String> ATTRIBUTE_DOCS = new HashMap<>();
    private static final Map<String, String> FILTER_DOCS = new HashMap<>();
    
    static {
        // Initialize documentation for common Latte macros
        MACRO_DOCS.put("if", "Conditional statement.<br><code>{if $condition}...{elseif $condition}...{else}...{/if}</code>");
        MACRO_DOCS.put("foreach", "Loop over an array or iterable object.<br><code>{foreach $items as $item}...{/foreach}</code>");
        MACRO_DOCS.put("include", "Include another template.<br><code>{include 'file.latte', param => value}</code>");
        MACRO_DOCS.put("block", "Define a block.<br><code>{block name}...{/block}</code>");
        MACRO_DOCS.put("define", "Define a block without printing it.<br><code>{define name}...{/define}</code>");
        MACRO_DOCS.put("var", "Define a variable.<br><code>{var $foo = 'bar'}</code>");
        MACRO_DOCS.put("capture", "Capture output to a variable.<br><code>{capture $var}...{/capture}</code>");
        MACRO_DOCS.put("_", "Translate a string.<br><code>{_'text to translate'}</code>");
        MACRO_DOCS.put("=", "Print a variable.<br><code>{=$variable}</code>");
        
        // Initialize documentation for common Latte n:attributes
        ATTRIBUTE_DOCS.put("n:if", "Conditional rendering of an element.<br><code>&lt;div n:if=\"$condition\"&gt;...&lt;/div&gt;</code>");
        ATTRIBUTE_DOCS.put("n:foreach", "Loop over an array or iterable object.<br><code>&lt;ul n:foreach=\"$items as $item\"&gt;...&lt;/ul&gt;</code>");
        ATTRIBUTE_DOCS.put("n:inner-foreach", "Loop over an array or iterable object, but only for the inner content.<br><code>&lt;ul n:inner-foreach=\"$items as $item\"&gt;&lt;li&gt;{$item}&lt;/li&gt;&lt;/ul&gt;</code>");
        ATTRIBUTE_DOCS.put("n:class", "Conditionally add classes to an element.<br><code>&lt;div n:class=\"$condition ? active\"&gt;...&lt;/div&gt;</code>");
        ATTRIBUTE_DOCS.put("n:attr", "Conditionally add attributes to an element.<br><code>&lt;div n:attr=\"title => $title, data-id => $id\"&gt;...&lt;/div&gt;</code>");
        ATTRIBUTE_DOCS.put("n:tag", "Conditionally change the tag name.<br><code>&lt;div n:tag=\"$isHeader ? h1\"&gt;...&lt;/div&gt;</code>");
        
        // Initialize documentation for common Latte filters
        FILTER_DOCS.put("capitalize", "Capitalizes the first letter of each word in a string.<br><code>{$string|capitalize}</code>");
        FILTER_DOCS.put("upper", "Converts a string to uppercase.<br><code>{$string|upper}</code>");
        FILTER_DOCS.put("lower", "Converts a string to lowercase.<br><code>{$string|lower}</code>");
        FILTER_DOCS.put("firstUpper", "Capitalizes the first letter of a string.<br><code>{$string|firstUpper}</code>");
        FILTER_DOCS.put("escape", "Escapes a string for safe output in HTML.<br><code>{$string|escape}</code>");
        FILTER_DOCS.put("escapeUrl", "Escapes a string for use in a URL.<br><code>{$string|escapeUrl}</code>");
        FILTER_DOCS.put("noescape", "Prevents escaping of a string.<br><code>{$string|noescape}</code>");
        FILTER_DOCS.put("date", "Formats a date.<br><code>{$date|date:'j. n. Y'}</code>");
        FILTER_DOCS.put("number", "Formats a number.<br><code>{$number|number:2}</code>");
    }
    
    /**
     * Gets documentation for a Nette package macro.
     *
     * @param macroName The name of the macro
     * @return The documentation for the macro, or null if not found
     */
    @Nullable
    private String getNetteMacroDocumentation(String macroName) {
        for (NetteMacro macro : NetteMacroProvider.getAllMacros()) {
            if (macro.getName().equals(macroName)) {
                return createDocumentation(
                        "Nette Macro: " + macroName + " (" + macro.getPackageName() + ")",
                        macro.getDescription() + "<br><br>Provided by: " + macro.getPackageName()
                );
            }
        }
        return null;
    }
    
    /**
     * Gets documentation for a Nette package attribute.
     *
     * @param attrName The name of the attribute
     * @return The documentation for the attribute, or null if not found
     */
    @Nullable
    private String getNetteAttributeDocumentation(String attrName) {
        for (NetteMacro attribute : NetteMacroProvider.getAllAttributes()) {
            if (attribute.getName().equals(attrName)) {
                return createDocumentation(
                        "Nette Attribute: " + attrName + " (" + attribute.getPackageName() + ")",
                        attribute.getDescription() + "<br><br>Provided by: " + attribute.getPackageName()
                );
            }
        }
        return null;
    }
    
    @Nullable
    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (element == null) {
            return null;
        }
        
        String text = element.getText();
        
        // Check if the element is a Latte macro
        if (text.startsWith("{")) {
            // Extract the macro name - it could be the entire macro or just the name
            String macroContent = text;
            if (text.endsWith("}")) {
                macroContent = text.substring(1, text.length() - 1).trim();
            }
            
            // Extract just the macro name (without parameters or content)
            String macroName = macroContent.split("\\s+")[0].trim();
            if (macroName.isEmpty()) {
                return null;
            }
            
            // Check built-in macros
            if (MACRO_DOCS.containsKey(macroName)) {
                return createDocumentation("Latte Macro: " + macroName, MACRO_DOCS.get(macroName));
            }
            
            // Check Nette package macros
            String netteMacroDoc = getNetteMacroDocumentation(macroName);
            if (netteMacroDoc != null) {
                return netteMacroDoc;
            }
        }
        
        // Check if the element is a Latte n:attribute or contains an n:attribute
        if (text.contains("n:")) {
            // Extract the attribute name
            int nIndex = text.indexOf("n:");
            if (nIndex >= 0) {
                String afterN = text.substring(nIndex);
                String attrName;
                
                // Handle the case where the attribute is followed by "="
                int equalsIndex = afterN.indexOf("=");
                if (equalsIndex > 0) {
                    attrName = afterN.substring(0, equalsIndex).trim();
                } else {
                    // Handle the case where there's no "=" (e.g., just "n:if")
                    int spaceIndex = afterN.indexOf(" ");
                    int quoteIndex = afterN.indexOf("\"");
                    int endIndex = Math.min(
                        spaceIndex > 0 ? spaceIndex : Integer.MAX_VALUE,
                        quoteIndex > 0 ? quoteIndex : Integer.MAX_VALUE
                    );
                    
                    if (endIndex < Integer.MAX_VALUE) {
                        attrName = afterN.substring(0, endIndex).trim();
                    } else {
                        attrName = afterN.trim();
                    }
                }
                
                // Check built-in attributes
                if (ATTRIBUTE_DOCS.containsKey(attrName)) {
                    return createDocumentation("Latte Attribute: " + attrName, ATTRIBUTE_DOCS.get(attrName));
                }
                
                // Check Nette package attributes
                String netteAttrDoc = getNetteAttributeDocumentation(attrName);
                if (netteAttrDoc != null) {
                    return netteAttrDoc;
                }
            }
        }
        
        // Check if the element is a Latte filter or contains a filter
        if (text.contains("|")) {
            // Extract the filter name
            int pipeIndex = text.indexOf("|");
            if (pipeIndex >= 0) {
                String afterPipe = text.substring(pipeIndex + 1);
                String filterName;
                
                // Handle the case where there are multiple filters or parameters
                int nextPipeIndex = afterPipe.indexOf("|");
                int spaceIndex = afterPipe.indexOf(" ");
                int bracketIndex = afterPipe.indexOf("}");
                int endIndex = Math.min(
                    nextPipeIndex > 0 ? nextPipeIndex : Integer.MAX_VALUE,
                    Math.min(
                        spaceIndex > 0 ? spaceIndex : Integer.MAX_VALUE,
                        bracketIndex > 0 ? bracketIndex : Integer.MAX_VALUE
                    )
                );
                
                if (endIndex < Integer.MAX_VALUE) {
                    filterName = afterPipe.substring(0, endIndex).trim();
                } else {
                    filterName = afterPipe.trim();
                }
                
                if (FILTER_DOCS.containsKey(filterName)) {
                    return createDocumentation("Latte Filter: " + filterName, FILTER_DOCS.get(filterName));
                }
            }
        }
        
        return null;
    }
    
    @NotNull
    private String createDocumentation(@NotNull String name, @NotNull String description) {
        return DocumentationMarkup.DEFINITION_START +
               name +
               DocumentationMarkup.DEFINITION_END +
               DocumentationMarkup.CONTENT_START +
               description +
               DocumentationMarkup.CONTENT_END;
    }
}