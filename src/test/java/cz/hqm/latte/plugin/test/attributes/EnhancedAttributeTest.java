package cz.hqm.latte.plugin.test.attributes;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import cz.hqm.latte.plugin.custom.CustomAttribute;
import cz.hqm.latte.plugin.custom.CustomAttributesProvider;
import cz.hqm.latte.plugin.lexer.LatteAttributeLexer;
import cz.hqm.latte.plugin.lexer.LatteTokenTypes;
import cz.hqm.latte.plugin.test.LattePluginTestBase;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tests for the enhanced n:attributes support.
 */
public class EnhancedAttributeTest extends LattePluginTestBase {


    @Override
    protected void setUp() throws Exception {
        // Konfigurace Java Util Logging pro potlačení warnings
        configureLogging();

        // Nastavení systémových vlastností pro testovací prostředí
        System.setProperty("idea.test.mode", "true");
        System.setProperty("idea.is.unit.test", "true");
        System.setProperty("idea.platform.prefix", "Idea");

        // Potlačení JUL configuration warnings
        System.setProperty("java.util.logging.config.file", "");
        System.setProperty("java.util.logging.manager", "java.util.logging.LogManager");

        // Volání původní setUp metody z nadtřídy
        super.setUp();
    }

    /**
     * Konfiguruje Java Util Logging pro potlačení verbose výstupů během testů.
     */
    private void configureLogging() {
        // Získáme root logger
        java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");

        // Nastavíme úroveň na WARNING nebo vyšší
        rootLogger.setLevel(java.util.logging.Level.WARNING);

        // Nakonfigurujeme handlery
        java.util.logging.Handler[] handlers = rootLogger.getHandlers();
        for (java.util.logging.Handler handler : handlers) {
            handler.setLevel(java.util.logging.Level.WARNING);
            // Nastavíme jednoduchý formát pro méně verbose výstup
            if (handler instanceof java.util.logging.ConsoleHandler) {
                handler.setFormatter(new java.util.logging.SimpleFormatter());
            }
        }

        // Specifické loggery pro IntelliJ komponenty
        java.util.logging.Logger.getLogger("com.intellij").setLevel(java.util.logging.Level.WARNING);
        java.util.logging.Logger.getLogger("org.jetbrains").setLevel(java.util.logging.Level.WARNING);
        java.util.logging.Logger.getLogger("com.jetbrains").setLevel(java.util.logging.Level.WARNING);

        // Potlačíme verbose výstupy specifické pro testování
        java.util.logging.Logger.getLogger("com.intellij.testFramework").setLevel(java.util.logging.Level.WARNING);
        java.util.logging.Logger.getLogger("com.intellij.idea").setLevel(java.util.logging.Level.WARNING);
    }

    /**
     * Tests that dynamic n:attributes are supported.
     */
    @Test
    public void testDynamicAttributes() {
        // Create a test attribute lexer
        LatteAttributeLexer lexer = new LatteAttributeLexer();
        
        // Test dynamic attribute names
        String[] dynamicAttributes = {
            "n:dynamic-attribute",
            "n:custom_attribute",
            "n:attribute123",
            "n:attr-with-dashes",
            "n:attr_with_underscores"
        };
        
        for (String attribute : dynamicAttributes) {
            lexer.start(attribute, 0, attribute.length(), 0);
            assertEquals("Attribute should be recognized as LATTE_ATTRIBUTE_NAME", 
                    LatteTokenTypes.LATTE_ATTRIBUTE_NAME, lexer.getTokenType());
            assertEquals("Entire attribute should be tokenized", attribute.length(), lexer.getTokenEnd());
        }
    }
    
    /**
     * Tests that prefixed n:attributes are supported.
     */
    @Test
    public void testPrefixedAttributes() {
        // Create a test attribute lexer
        LatteAttributeLexer lexer = new LatteAttributeLexer();
        
        // Test prefixed attribute names
        String[] prefixedAttributes = {
            "n:class:hover",
            "n:attr:required",
            "n:tag:if",
            "n:data-custom"
        };
        
        for (String attribute : prefixedAttributes) {
            lexer.start(attribute, 0, attribute.length(), 0);
            assertEquals("Prefixed attribute should be recognized as LATTE_ATTRIBUTE_NAME", 
                    LatteTokenTypes.LATTE_ATTRIBUTE_NAME, lexer.getTokenType());
            assertEquals("Entire prefixed attribute should be tokenized", attribute.length(), lexer.getTokenEnd());
        }
    }
    
    /**
     * Tests that the attribute name pattern supports complex attributes.
     */
    @Test
    public void testAttributeNamePattern() {
        // Get the ATTRIBUTE_NAME_PATTERN from LatteAttributeLexer via reflection
        Pattern pattern;
        try {
            java.lang.reflect.Field field = LatteAttributeLexer.class.getDeclaredField("ATTRIBUTE_NAME_PATTERN");
            field.setAccessible(true);
            pattern = (Pattern) field.get(null);
        } catch (Exception e) {
            fail("Could not access ATTRIBUTE_NAME_PATTERN: " + e.getMessage());
            return;
        }
        
        // Test various attribute names
        String[] validAttributes = {
            "n:if",
            "n:foreach",
            "n:class:hover",
            "n:attr:required",
            "n:data-custom",
            "n:custom_attribute",
            "n:attribute123",
            "n:complex.attribute"
        };
        
        for (String attribute : validAttributes) {
            Matcher matcher = pattern.matcher(attribute);
            assertTrue("Pattern should match attribute: " + attribute, matcher.find());
            assertEquals("Pattern should match entire attribute", attribute, matcher.group(1));
        }
    }
    
    /**
     * Tests that custom attributes are supported.
     */
    @Test
    public void testCustomAttributes() {
        // Create a custom attribute
        CustomAttribute customAttribute = new CustomAttribute("n:custom-attr", "Custom attribute", "Example usage");
        
        // Verify the custom attribute properties
        assertEquals("Custom attribute name should be correct", "n:custom-attr", customAttribute.getName());
        assertEquals("Custom attribute description should be correct", "Custom attribute", customAttribute.getDescription());
        assertEquals("Custom attribute usage should be correct", "Example usage", customAttribute.getUsage());
        
        // Verify the display text and type text
        assertTrue("Display text should include the attribute name", 
                customAttribute.getDisplayText().contains("n:custom-attr"));
        assertEquals("Type text should be 'custom attribute'", "custom attribute", customAttribute.getTypeText());
    }
    
    /**
     * Tests that the CustomAttributesProvider works correctly.
     */
    @Test
    public void testCustomAttributesProvider() {
        // Get the project
        var project = getProject();
        
        // Initially there should be no custom attributes
        List<CustomAttribute> initialAttributes = CustomAttributesProvider.getAllAttributes(project);
        int initialCount = initialAttributes.size();
        
        // Create a custom attribute
        CustomAttribute customAttribute = new CustomAttribute("n:test-attr", "Test attribute", "Example usage");
        
        // Add the custom attribute
        boolean added = CustomAttributesProvider.addAttribute(project, customAttribute);
        assertTrue("Custom attribute should be added successfully", added);
        
        // Verify that the attribute was added
        List<CustomAttribute> attributes = CustomAttributesProvider.getAllAttributes(project);
        assertEquals("Custom attribute should be added to the list", initialCount + 1, attributes.size());
        
        // Verify that the attribute can be found in the list
        boolean found = false;
        for (CustomAttribute attr : attributes) {
            if (attr.getName().equals("n:test-attr")) {
                found = true;
                break;
            }
        }
        assertTrue("Custom attribute should be found in the list", found);
        
        // Remove the custom attribute
        boolean removed = CustomAttributesProvider.removeAttribute(project, "n:test-attr");
        assertTrue("Custom attribute should be removed successfully", removed);
        
        // Verify that the attribute was removed
        attributes = CustomAttributesProvider.getAllAttributes(project);
        assertEquals("Custom attribute should be removed from the list", initialCount, attributes.size());
    }
    
    /**
     * Tests that adding a duplicate attribute fails.
     */
    @Test
    public void testAddDuplicateAttribute() {
        // Get the project
        var project = getProject();
        
        // Create a custom attribute
        CustomAttribute customAttribute = new CustomAttribute("n:duplicate", "Duplicate attribute", "Example usage");
        
        // Add the custom attribute
        boolean added = CustomAttributesProvider.addAttribute(project, customAttribute);
        assertTrue("Custom attribute should be added successfully", added);
        
        // Try to add the same attribute again
        boolean addedAgain = CustomAttributesProvider.addAttribute(project, customAttribute);
        assertFalse("Adding duplicate attribute should fail", addedAgain);
        
        // Clean up
        CustomAttributesProvider.removeAttribute(project, "n:duplicate");
    }
}
