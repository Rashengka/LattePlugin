package org.latte.plugin.test.custom;

import org.latte.plugin.custom.CustomFilter;
import org.latte.plugin.test.LattePluginTestBase;

/**
 * Tests for the CustomFilter class.
 */
public class CustomFilterTest extends LattePluginTestBase {
    
    /**
     * Tests the constructor and getters.
     */
    public void testConstructorAndGetters() {
        CustomFilter filter = new CustomFilter("testFilter", "testDescription");
        
        assertEquals("Name should be set correctly", "testFilter", filter.getName());
        assertEquals("Description should be set correctly", "testDescription", filter.getDescription());
    }
    
    /**
     * Tests the default constructor and setters.
     */
    public void testDefaultConstructorAndSetters() {
        CustomFilter filter = new CustomFilter();
        
        filter.setName("testFilter");
        filter.setDescription("testDescription");
        
        assertEquals("Name should be set correctly", "testFilter", filter.getName());
        assertEquals("Description should be set correctly", "testDescription", filter.getDescription());
    }
    
    /**
     * Tests the getTypeText method.
     */
    public void testGetTypeText() {
        CustomFilter filter = new CustomFilter("testFilter", "testDescription");
        
        assertEquals("Type text should be 'Custom filter'", "Custom filter", filter.getTypeText());
    }
    
    /**
     * Tests inheritance from CustomElement.
     */
    public void testInheritance() {
        CustomFilter filter = new CustomFilter("testFilter", "testDescription");
        
        // Test that CustomFilter inherits getDisplayText from CustomElement
        assertEquals("Display text should be the name", "testFilter", filter.getDisplayText());
    }
}