package org.latte.plugin.test.custom;

import com.intellij.openapi.project.Project;
import org.latte.plugin.custom.CustomVariable;
import org.latte.plugin.custom.CustomVariablesProvider;
import org.latte.plugin.settings.LatteProjectSettings;
import org.latte.plugin.test.LattePluginTestBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Tests for the CustomVariablesProvider class.
 */
public class CustomVariablesProviderTest extends LattePluginTestBase {
    
    private Project project;
    private LatteProjectSettings settings;
    private List<CustomVariable> testVariables;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // Get the project from the test fixture
        project = getProject();
        
        // Get the settings instance for the project
        settings = LatteProjectSettings.getInstance(project);
        
        // Save the original variables
        testVariables = new ArrayList<>(settings.getCustomVariables());
        
        // Clear the variables for testing
        settings.setCustomVariables(new ArrayList<>());
    }
    
    @Override
    protected void tearDown() throws Exception {
        // Restore the original variables
        settings.setCustomVariables(testVariables);
        
        super.tearDown();
    }
    
    /**
     * Tests getting all variables when there are no variables.
     */
    public void testGetAllVariablesEmpty() {
        Set<CustomVariable> variables = CustomVariablesProvider.getAllVariables(project);
        
        assertNotNull("Variables set should not be null", variables);
        assertEquals("Variables set should be empty", 0, variables.size());
    }
    
    /**
     * Tests getting all variable names when there are no variables.
     */
    public void testGetAllVariableNamesEmpty() {
        Set<String> variableNames = CustomVariablesProvider.getAllVariableNames(project);
        
        assertNotNull("Variable names set should not be null", variableNames);
        assertEquals("Variable names set should be empty", 0, variableNames.size());
    }
    
    /**
     * Tests variable existence check when the variable doesn't exist.
     */
    public void testVariableExistsFalse() {
        boolean exists = CustomVariablesProvider.variableExists(project, "nonExistentVariable");
        
        assertFalse("Variable should not exist", exists);
    }
    
    /**
     * Tests getting a variable by name when the variable doesn't exist.
     */
    public void testGetVariableByNameNotFound() {
        CustomVariable variable = CustomVariablesProvider.getVariableByName(project, "nonExistentVariable");
        
        assertNull("Variable should not be found", variable);
    }
    
    /**
     * Tests adding a variable with a type.
     */
    public void testAddVariableWithType() {
        CustomVariable variable = CustomVariablesProvider.addVariable(project, "testVariable", "string", "testDescription");
        
        assertNotNull("Added variable should not be null", variable);
        assertEquals("Variable name should be set correctly", "testVariable", variable.getName());
        assertEquals("Variable type should be set correctly", "string", variable.getType());
        assertEquals("Variable description should be set correctly", "testDescription", variable.getDescription());
        
        // Verify the variable was added to the settings
        List<CustomVariable> variables = settings.getCustomVariables();
        assertEquals("Settings should have 1 variable", 1, variables.size());
        assertEquals("Variable in settings should have the correct name", "testVariable", variables.get(0).getName());
        assertEquals("Variable in settings should have the correct type", "string", variables.get(0).getType());
        assertEquals("Variable in settings should have the correct description", "testDescription", variables.get(0).getDescription());
        
        // Verify the variable can be retrieved
        assertTrue("Variable should exist", CustomVariablesProvider.variableExists(project, "testVariable"));
        CustomVariable retrievedVariable = CustomVariablesProvider.getVariableByName(project, "testVariable");
        assertNotNull("Retrieved variable should not be null", retrievedVariable);
        assertEquals("Retrieved variable should have the correct name", "testVariable", retrievedVariable.getName());
        assertEquals("Retrieved variable should have the correct type", "string", retrievedVariable.getType());
        assertEquals("Retrieved variable should have the correct description", "testDescription", retrievedVariable.getDescription());
    }
    
    /**
     * Tests adding a variable without a type.
     */
    public void testAddVariableWithoutType() {
        CustomVariable variable = CustomVariablesProvider.addVariable(project, "testVariable", null, "testDescription");
        
        assertNotNull("Added variable should not be null", variable);
        assertEquals("Variable name should be set correctly", "testVariable", variable.getName());
        assertNull("Variable type should be null", variable.getType());
        assertEquals("Variable description should be set correctly", "testDescription", variable.getDescription());
        
        // Verify the variable was added to the settings
        List<CustomVariable> variables = settings.getCustomVariables();
        assertEquals("Settings should have 1 variable", 1, variables.size());
        assertEquals("Variable in settings should have the correct name", "testVariable", variables.get(0).getName());
        assertNull("Variable in settings should have null type", variables.get(0).getType());
        assertEquals("Variable in settings should have the correct description", "testDescription", variables.get(0).getDescription());
        
        // Verify the variable can be retrieved
        assertTrue("Variable should exist", CustomVariablesProvider.variableExists(project, "testVariable"));
        CustomVariable retrievedVariable = CustomVariablesProvider.getVariableByName(project, "testVariable");
        assertNotNull("Retrieved variable should not be null", retrievedVariable);
        assertEquals("Retrieved variable should have the correct name", "testVariable", retrievedVariable.getName());
        assertNull("Retrieved variable should have null type", retrievedVariable.getType());
        assertEquals("Retrieved variable should have the correct description", "testDescription", retrievedVariable.getDescription());
    }
    
    /**
     * Tests removing a variable.
     */
    public void testRemoveVariable() {
        // Add a variable first
        CustomVariablesProvider.addVariable(project, "testVariable", "string", "testDescription");
        
        // Verify the variable exists
        assertTrue("Variable should exist before removal", CustomVariablesProvider.variableExists(project, "testVariable"));
        
        // Remove the variable
        boolean removed = CustomVariablesProvider.removeVariable(project, "testVariable");
        
        assertTrue("Variable should be removed successfully", removed);
        assertFalse("Variable should not exist after removal", CustomVariablesProvider.variableExists(project, "testVariable"));
        assertEquals("Settings should have 0 variables", 0, settings.getCustomVariables().size());
    }
    
    /**
     * Tests removing a non-existent variable.
     */
    public void testRemoveNonExistentVariable() {
        boolean removed = CustomVariablesProvider.removeVariable(project, "nonExistentVariable");
        
        assertFalse("Non-existent variable should not be removed", removed);
    }
    
    /**
     * Tests adding multiple variables and retrieving them.
     */
    public void testAddMultipleVariables() {
        CustomVariablesProvider.addVariable(project, "variable1", "string", "description1");
        CustomVariablesProvider.addVariable(project, "variable2", "int", "description2");
        CustomVariablesProvider.addVariable(project, "variable3", null, "description3");
        
        Set<CustomVariable> variables = CustomVariablesProvider.getAllVariables(project);
        assertEquals("Should have 3 variables", 3, variables.size());
        
        Set<String> variableNames = CustomVariablesProvider.getAllVariableNames(project);
        assertEquals("Should have 3 variable names", 3, variableNames.size());
        assertTrue("Should contain variable1", variableNames.contains("variable1"));
        assertTrue("Should contain variable2", variableNames.contains("variable2"));
        assertTrue("Should contain variable3", variableNames.contains("variable3"));
        
        // Verify types
        CustomVariable variable1 = CustomVariablesProvider.getVariableByName(project, "variable1");
        CustomVariable variable2 = CustomVariablesProvider.getVariableByName(project, "variable2");
        CustomVariable variable3 = CustomVariablesProvider.getVariableByName(project, "variable3");
        
        assertEquals("variable1 should have type 'string'", "string", variable1.getType());
        assertEquals("variable2 should have type 'int'", "int", variable2.getType());
        assertNull("variable3 should have null type", variable3.getType());
    }
    
    /**
     * Tests adding a duplicate variable.
     */
    public void testAddDuplicateVariable() {
        CustomVariablesProvider.addVariable(project, "testVariable", "string", "description1");
        CustomVariablesProvider.addVariable(project, "testVariable", "int", "description2");
        
        Set<CustomVariable> variables = CustomVariablesProvider.getAllVariables(project);
        assertEquals("Should have 1 variable (no duplicates)", 1, variables.size());
        
        // The first variable should be preserved
        CustomVariable variable = CustomVariablesProvider.getVariableByName(project, "testVariable");
        assertEquals("Type should be from the first variable", "string", variable.getType());
        assertEquals("Description should be from the first variable", "description1", variable.getDescription());
    }
}