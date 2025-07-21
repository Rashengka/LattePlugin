package cz.hqm.latte.plugin.test.custom;

import cz.hqm.latte.plugin.custom.CustomFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import cz.hqm.latte.plugin.test.LattePluginTestBase;

/**
 * Tests for the CustomFilter class.
 */
public class CustomFilterTest extends LattePluginTestBase {
    
    /**
     * Tests the constructor and getters.
     */
    @Test
    public void testConstructorAndGetters() {
        CustomFilter filter = new CustomFilter("testFilter", "testDescription");
        
        assertEquals("Name should be set correctly", "testFilter", filter.getName());
        assertEquals("Description should be set correctly", "testDescription", filter.getDescription());
    }
    
    /**
     * Tests the default constructor and setters.
     */
    @Test
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
    @Test
    public void testGetTypeText() {
        CustomFilter filter = new CustomFilter("testFilter", "testDescription");
        
        assertEquals("Type text should be 'Custom filter'", "Custom filter", filter.getTypeText());
    }
    
    /**
     * Tests inheritance from CustomElement.
     */
    @Test
    public void testInheritance() {
        CustomFilter filter = new CustomFilter("testFilter", "testDescription");
        
        // Test that CustomFilter inherits getDisplayText from CustomElement
        assertEquals("Display text should be the name", "testFilter", filter.getDisplayText());
    }
}
