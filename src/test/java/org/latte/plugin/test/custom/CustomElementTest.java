package org.latte.plugin.test.custom;

import org.latte.plugin.custom.CustomElement;
import org.latte.plugin.test.LattePluginTestBase;

/**
 * Tests for the CustomElement base class.
 */
public class CustomElementTest extends LattePluginTestBase {
    
    /**
     * Tests the constructor and getters.
     */
    public void testConstructorAndGetters() {
        CustomElement element = new CustomElement("testName", "testDescription");
        
        assertEquals("Name should be set correctly", "testName", element.getName());
        assertEquals("Description should be set correctly", "testDescription", element.getDescription());
    }
    
    /**
     * Tests the default constructor and setters.
     */
    public void testDefaultConstructorAndSetters() {
        CustomElement element = new CustomElement();
        
        element.setName("testName");
        element.setDescription("testDescription");
        
        assertEquals("Name should be set correctly", "testName", element.getName());
        assertEquals("Description should be set correctly", "testDescription", element.getDescription());
    }
    
    /**
     * Tests the getDisplayText method.
     */
    public void testGetDisplayText() {
        CustomElement element = new CustomElement("testName", "testDescription");
        
        assertEquals("Display text should be the name", "testName", element.getDisplayText());
    }
    
    /**
     * Tests the equals method.
     */
    public void testEquals() {
        CustomElement element1 = new CustomElement("testName", "testDescription");
        CustomElement element2 = new CustomElement("testName", "differentDescription");
        CustomElement element3 = new CustomElement("differentName", "testDescription");
        
        assertTrue("Elements with same name should be equal", element1.equals(element2));
        assertFalse("Elements with different names should not be equal", element1.equals(element3));
        assertFalse("Element should not be equal to null", element1.equals(null));
        assertFalse("Element should not be equal to object of different class", element1.equals("testName"));
    }
    
    /**
     * Tests the hashCode method.
     */
    public void testHashCode() {
        CustomElement element1 = new CustomElement("testName", "testDescription");
        CustomElement element2 = new CustomElement("testName", "differentDescription");
        
        assertEquals("Elements with same name should have same hash code", element1.hashCode(), element2.hashCode());
    }
}