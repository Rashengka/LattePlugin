package cz.hqm.latte.plugin.test.custom;

import cz.hqm.latte.plugin.custom.CustomTag;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import cz.hqm.latte.plugin.test.LattePluginTestBase;

/**
 * Tests for the CustomTag class.
 */
public class CustomTagTest extends LattePluginTestBase {
    
    /**
     * Tests the constructor and getters.
     */
    @Test
    public void testConstructorAndGetters() {
        CustomTag tag = new CustomTag("testTag", "testDescription");
        
        assertEquals("Name should be set correctly", "testTag", tag.getName());
        assertEquals("Description should be set correctly", "testDescription", tag.getDescription());
    }
    
    /**
     * Tests the default constructor and setters.
     */
    @Test
    public void testDefaultConstructorAndSetters() {
        CustomTag tag = new CustomTag();
        
        tag.setName("testTag");
        tag.setDescription("testDescription");
        
        assertEquals("Name should be set correctly", "testTag", tag.getName());
        assertEquals("Description should be set correctly", "testDescription", tag.getDescription());
    }
    
    /**
     * Tests the getTypeText method.
     */
    @Test
    public void testGetTypeText() {
        CustomTag tag = new CustomTag("testTag", "testDescription");
        
        assertEquals("Type text should be 'Custom tag'", "Custom tag", tag.getTypeText());
    }
    
    /**
     * Tests inheritance from CustomElement.
     */
    @Test
    public void testInheritance() {
        CustomTag tag = new CustomTag("testTag", "testDescription");
        
        // Test that CustomTag inherits getDisplayText from CustomElement
        assertEquals("Display text should be the name", "testTag", tag.getDisplayText());
    }
}
