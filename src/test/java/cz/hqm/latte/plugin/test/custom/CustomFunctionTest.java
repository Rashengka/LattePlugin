package cz.hqm.latte.plugin.test.custom;

import cz.hqm.latte.plugin.custom.CustomFunction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import cz.hqm.latte.plugin.test.LattePluginTestBase;

/**
 * Tests for the CustomFunction class.
 */
public class CustomFunctionTest extends LattePluginTestBase {
    
    /**
     * Tests the constructor and getters.
     */
    @Test
    public void testConstructorAndGetters() {
        CustomFunction function = new CustomFunction("testFunction", "testDescription");
        
        assertEquals("Name should be set correctly", "testFunction", function.getName());
        assertEquals("Description should be set correctly", "testDescription", function.getDescription());
    }
    
    /**
     * Tests the default constructor and setters.
     */
    @Test
    public void testDefaultConstructorAndSetters() {
        CustomFunction function = new CustomFunction();
        
        function.setName("testFunction");
        function.setDescription("testDescription");
        
        assertEquals("Name should be set correctly", "testFunction", function.getName());
        assertEquals("Description should be set correctly", "testDescription", function.getDescription());
    }
    
    /**
     * Tests the getTypeText method.
     */
    @Test
    public void testGetTypeText() {
        CustomFunction function = new CustomFunction("testFunction", "testDescription");
        
        assertEquals("Type text should be 'Custom function'", "Custom function", function.getTypeText());
    }
    
    /**
     * Tests inheritance from CustomElement.
     */
    @Test
    public void testInheritance() {
        CustomFunction function = new CustomFunction("testFunction", "testDescription");
        
        // Test that CustomFunction inherits getDisplayText from CustomElement
        assertEquals("Display text should be the name", "testFunction", function.getDisplayText());
    }
}
