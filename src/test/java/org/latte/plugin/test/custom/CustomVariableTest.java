package org.latte.plugin.test.custom;

import org.latte.plugin.custom.CustomVariable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.latte.plugin.test.LattePluginTestBase;

/**
 * Tests for the CustomVariable class.
 */
public class CustomVariableTest extends LattePluginTestBase {
    
    /**
     * Tests the constructor and getters.
     */
    @Test
    public void testConstructorAndGetters() {
        CustomVariable variable = new CustomVariable("testVariable", "string", "testDescription");
        
        assertEquals("Name should be set correctly", "testVariable", variable.getName());
        assertEquals("Type should be set correctly", "string", variable.getType());
        assertEquals("Description should be set correctly", "testDescription", variable.getDescription());
    }
    
    /**
     * Tests the default constructor and setters.
     */
    @Test
    public void testDefaultConstructorAndSetters() {
        CustomVariable variable = new CustomVariable();
        
        variable.setName("testVariable");
        variable.setType("string");
        variable.setDescription("testDescription");
        
        assertEquals("Name should be set correctly", "testVariable", variable.getName());
        assertEquals("Type should be set correctly", "string", variable.getType());
        assertEquals("Description should be set correctly", "testDescription", variable.getDescription());
    }
    
    /**
     * Tests the getTypeText method with type set.
     */
    @Test
    public void testGetTypeTextWithType() {
        CustomVariable variable = new CustomVariable("testVariable", "string", "testDescription");
        
        assertEquals("Type text should be the type", "string", variable.getTypeText());
    }
    
    /**
     * Tests the getTypeText method with null type.
     */
    @Test
    public void testGetTypeTextWithNullType() {
        CustomVariable variable = new CustomVariable("testVariable", null, "testDescription");
        
        assertEquals("Type text should be 'Custom variable' when type is null", "Custom variable", variable.getTypeText());
    }
    
    /**
     * Tests the getDisplayText method.
     */
    @Test
    public void testGetDisplayText() {
        CustomVariable variable = new CustomVariable("testVariable", "string", "testDescription");
        
        assertEquals("Display text should be '$' + name", "$testVariable", variable.getDisplayText());
    }
    
    /**
     * Tests inheritance from CustomElement.
     */
    @Test
    public void testInheritance() {
        CustomVariable variable = new CustomVariable("testVariable", "string", "testDescription");
        
        // Test that CustomVariable overrides getDisplayText from CustomElement
        assertFalse("Display text should not be just the name", variable.getName().equals(variable.getDisplayText()));
        assertEquals("Display text should be '$' + name", "$" + variable.getName(), variable.getDisplayText());
    }
}
