package org.latte.plugin.test.custom;

import com.intellij.openapi.project.Project;
import org.latte.plugin.custom.CustomFunction;
import org.latte.plugin.custom.CustomFunctionsProvider;
import org.latte.plugin.settings.LatteProjectSettings;
import org.latte.plugin.test.LattePluginTestBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Tests for the CustomFunctionsProvider class.
 */
public class CustomFunctionsProviderTest extends LattePluginTestBase {
    
    private Project project;
    private LatteProjectSettings settings;
    private List<CustomFunction> testFunctions;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // Get the project from the test fixture
        project = getProject();
        
        // Get the settings instance for the project
        settings = LatteProjectSettings.getInstance(project);
        
        // Save the original functions
        testFunctions = new ArrayList<>(settings.getCustomFunctions());
        
        // Clear the functions for testing
        settings.setCustomFunctions(new ArrayList<>());
    }
    
    @Override
    protected void tearDown() throws Exception {
        // Restore the original functions
        settings.setCustomFunctions(testFunctions);
        
        super.tearDown();
    }
    
    /**
     * Tests getting all functions when there are no functions.
     */
    public void testGetAllFunctionsEmpty() {
        Set<CustomFunction> functions = CustomFunctionsProvider.getAllFunctions(project);
        
        assertNotNull("Functions set should not be null", functions);
        assertEquals("Functions set should be empty", 0, functions.size());
    }
    
    /**
     * Tests getting all function names when there are no functions.
     */
    public void testGetAllFunctionNamesEmpty() {
        Set<String> functionNames = CustomFunctionsProvider.getAllFunctionNames(project);
        
        assertNotNull("Function names set should not be null", functionNames);
        assertEquals("Function names set should be empty", 0, functionNames.size());
    }
    
    /**
     * Tests function existence check when the function doesn't exist.
     */
    public void testFunctionExistsFalse() {
        boolean exists = CustomFunctionsProvider.functionExists(project, "nonExistentFunction");
        
        assertFalse("Function should not exist", exists);
    }
    
    /**
     * Tests getting a function by name when the function doesn't exist.
     */
    public void testGetFunctionByNameNotFound() {
        CustomFunction function = CustomFunctionsProvider.getFunctionByName(project, "nonExistentFunction");
        
        assertNull("Function should not be found", function);
    }
    
    /**
     * Tests adding a function.
     */
    public void testAddFunction() {
        CustomFunction function = CustomFunctionsProvider.addFunction(project, "testFunction", "testDescription");
        
        assertNotNull("Added function should not be null", function);
        assertEquals("Function name should be set correctly", "testFunction", function.getName());
        assertEquals("Function description should be set correctly", "testDescription", function.getDescription());
        
        // Verify the function was added to the settings
        List<CustomFunction> functions = settings.getCustomFunctions();
        assertEquals("Settings should have 1 function", 1, functions.size());
        assertEquals("Function in settings should have the correct name", "testFunction", functions.get(0).getName());
        assertEquals("Function in settings should have the correct description", "testDescription", functions.get(0).getDescription());
        
        // Verify the function can be retrieved
        assertTrue("Function should exist", CustomFunctionsProvider.functionExists(project, "testFunction"));
        CustomFunction retrievedFunction = CustomFunctionsProvider.getFunctionByName(project, "testFunction");
        assertNotNull("Retrieved function should not be null", retrievedFunction);
        assertEquals("Retrieved function should have the correct name", "testFunction", retrievedFunction.getName());
        assertEquals("Retrieved function should have the correct description", "testDescription", retrievedFunction.getDescription());
    }
    
    /**
     * Tests removing a function.
     */
    public void testRemoveFunction() {
        // Add a function first
        CustomFunctionsProvider.addFunction(project, "testFunction", "testDescription");
        
        // Verify the function exists
        assertTrue("Function should exist before removal", CustomFunctionsProvider.functionExists(project, "testFunction"));
        
        // Remove the function
        boolean removed = CustomFunctionsProvider.removeFunction(project, "testFunction");
        
        assertTrue("Function should be removed successfully", removed);
        assertFalse("Function should not exist after removal", CustomFunctionsProvider.functionExists(project, "testFunction"));
        assertEquals("Settings should have 0 functions", 0, settings.getCustomFunctions().size());
    }
    
    /**
     * Tests removing a non-existent function.
     */
    public void testRemoveNonExistentFunction() {
        boolean removed = CustomFunctionsProvider.removeFunction(project, "nonExistentFunction");
        
        assertFalse("Non-existent function should not be removed", removed);
    }
    
    /**
     * Tests adding multiple functions and retrieving them.
     */
    public void testAddMultipleFunctions() {
        CustomFunctionsProvider.addFunction(project, "function1", "description1");
        CustomFunctionsProvider.addFunction(project, "function2", "description2");
        CustomFunctionsProvider.addFunction(project, "function3", "description3");
        
        Set<CustomFunction> functions = CustomFunctionsProvider.getAllFunctions(project);
        assertEquals("Should have 3 functions", 3, functions.size());
        
        Set<String> functionNames = CustomFunctionsProvider.getAllFunctionNames(project);
        assertEquals("Should have 3 function names", 3, functionNames.size());
        assertTrue("Should contain function1", functionNames.contains("function1"));
        assertTrue("Should contain function2", functionNames.contains("function2"));
        assertTrue("Should contain function3", functionNames.contains("function3"));
    }
    
    /**
     * Tests adding a duplicate function.
     */
    public void testAddDuplicateFunction() {
        CustomFunctionsProvider.addFunction(project, "testFunction", "description1");
        CustomFunctionsProvider.addFunction(project, "testFunction", "description2");
        
        Set<CustomFunction> functions = CustomFunctionsProvider.getAllFunctions(project);
        assertEquals("Should have 1 function (no duplicates)", 1, functions.size());
        
        // The first function should be preserved
        CustomFunction function = CustomFunctionsProvider.getFunctionByName(project, "testFunction");
        assertEquals("Description should be from the first function", "description1", function.getDescription());
    }
}