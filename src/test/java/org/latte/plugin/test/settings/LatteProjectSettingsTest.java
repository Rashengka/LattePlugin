package org.latte.plugin.test.settings;

import com.intellij.openapi.project.Project;
import org.latte.plugin.custom.CustomTag;
import org.latte.plugin.custom.CustomFilter;
import org.latte.plugin.custom.CustomFunction;
import org.latte.plugin.custom.CustomVariable;
import org.latte.plugin.settings.LatteProjectSettings;
import org.latte.plugin.test.LattePluginTestBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the LatteProjectSettings class.
 */
public class LatteProjectSettingsTest extends LattePluginTestBase {
    
    private Project project;
    private LatteProjectSettings settings;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // Get the project from the test fixture
        project = getProject();
        
        // Get the settings instance for the project
        settings = LatteProjectSettings.getInstance(project);
        
        // Clear all custom elements for testing
        settings.setCustomTags(new ArrayList<>());
        settings.setCustomFilters(new ArrayList<>());
        settings.setCustomFunctions(new ArrayList<>());
        settings.setCustomVariables(new ArrayList<>());
    }
    
    /**
     * Tests getting and setting custom tags.
     */
    public void testCustomTags() {
        // Create test tags
        List<CustomTag> tags = new ArrayList<>();
        tags.add(new CustomTag("tag1", "description1"));
        tags.add(new CustomTag("tag2", "description2"));
        
        // Set tags
        settings.setCustomTags(tags);
        
        // Get tags
        List<CustomTag> retrievedTags = settings.getCustomTags();
        
        // Verify
        assertEquals("Should have 2 tags", 2, retrievedTags.size());
        assertEquals("First tag should have correct name", "tag1", retrievedTags.get(0).getName());
        assertEquals("First tag should have correct description", "description1", retrievedTags.get(0).getDescription());
        assertEquals("Second tag should have correct name", "tag2", retrievedTags.get(1).getName());
        assertEquals("Second tag should have correct description", "description2", retrievedTags.get(1).getDescription());
    }
    
    /**
     * Tests getting and setting custom filters.
     */
    public void testCustomFilters() {
        // Create test filters
        List<CustomFilter> filters = new ArrayList<>();
        filters.add(new CustomFilter("filter1", "description1"));
        filters.add(new CustomFilter("filter2", "description2"));
        
        // Set filters
        settings.setCustomFilters(filters);
        
        // Get filters
        List<CustomFilter> retrievedFilters = settings.getCustomFilters();
        
        // Verify
        assertEquals("Should have 2 filters", 2, retrievedFilters.size());
        assertEquals("First filter should have correct name", "filter1", retrievedFilters.get(0).getName());
        assertEquals("First filter should have correct description", "description1", retrievedFilters.get(0).getDescription());
        assertEquals("Second filter should have correct name", "filter2", retrievedFilters.get(1).getName());
        assertEquals("Second filter should have correct description", "description2", retrievedFilters.get(1).getDescription());
    }
    
    /**
     * Tests getting and setting custom functions.
     */
    public void testCustomFunctions() {
        // Create test functions
        List<CustomFunction> functions = new ArrayList<>();
        functions.add(new CustomFunction("function1", "description1"));
        functions.add(new CustomFunction("function2", "description2"));
        
        // Set functions
        settings.setCustomFunctions(functions);
        
        // Get functions
        List<CustomFunction> retrievedFunctions = settings.getCustomFunctions();
        
        // Verify
        assertEquals("Should have 2 functions", 2, retrievedFunctions.size());
        assertEquals("First function should have correct name", "function1", retrievedFunctions.get(0).getName());
        assertEquals("First function should have correct description", "description1", retrievedFunctions.get(0).getDescription());
        assertEquals("Second function should have correct name", "function2", retrievedFunctions.get(1).getName());
        assertEquals("Second function should have correct description", "description2", retrievedFunctions.get(1).getDescription());
    }
    
    /**
     * Tests getting and setting custom variables.
     */
    public void testCustomVariables() {
        // Create test variables
        List<CustomVariable> variables = new ArrayList<>();
        variables.add(new CustomVariable("variable1", "string", "description1"));
        variables.add(new CustomVariable("variable2", "int", "description2"));
        
        // Set variables
        settings.setCustomVariables(variables);
        
        // Get variables
        List<CustomVariable> retrievedVariables = settings.getCustomVariables();
        
        // Verify
        assertEquals("Should have 2 variables", 2, retrievedVariables.size());
        assertEquals("First variable should have correct name", "variable1", retrievedVariables.get(0).getName());
        assertEquals("First variable should have correct type", "string", retrievedVariables.get(0).getType());
        assertEquals("First variable should have correct description", "description1", retrievedVariables.get(0).getDescription());
        assertEquals("Second variable should have correct name", "variable2", retrievedVariables.get(1).getName());
        assertEquals("Second variable should have correct type", "int", retrievedVariables.get(1).getType());
        assertEquals("Second variable should have correct description", "description2", retrievedVariables.get(1).getDescription());
    }
    
    /**
     * Tests adding and removing custom tags.
     */
    public void testAddRemoveCustomTag() {
        // Add a tag
        CustomTag tag = new CustomTag("testTag", "testDescription");
        settings.addCustomTag(tag);
        
        // Verify it was added
        List<CustomTag> tags = settings.getCustomTags();
        assertEquals("Should have 1 tag", 1, tags.size());
        assertEquals("Tag should have correct name", "testTag", tags.get(0).getName());
        
        // Remove the tag
        settings.removeCustomTag(tag);
        
        // Verify it was removed
        tags = settings.getCustomTags();
        assertEquals("Should have 0 tags", 0, tags.size());
    }
    
    /**
     * Tests adding and removing custom filters.
     */
    public void testAddRemoveCustomFilter() {
        // Add a filter
        CustomFilter filter = new CustomFilter("testFilter", "testDescription");
        settings.addCustomFilter(filter);
        
        // Verify it was added
        List<CustomFilter> filters = settings.getCustomFilters();
        assertEquals("Should have 1 filter", 1, filters.size());
        assertEquals("Filter should have correct name", "testFilter", filters.get(0).getName());
        
        // Remove the filter
        settings.removeCustomFilter(filter);
        
        // Verify it was removed
        filters = settings.getCustomFilters();
        assertEquals("Should have 0 filters", 0, filters.size());
    }
    
    /**
     * Tests adding and removing custom functions.
     */
    public void testAddRemoveCustomFunction() {
        // Add a function
        CustomFunction function = new CustomFunction("testFunction", "testDescription");
        settings.addCustomFunction(function);
        
        // Verify it was added
        List<CustomFunction> functions = settings.getCustomFunctions();
        assertEquals("Should have 1 function", 1, functions.size());
        assertEquals("Function should have correct name", "testFunction", functions.get(0).getName());
        
        // Remove the function
        settings.removeCustomFunction(function);
        
        // Verify it was removed
        functions = settings.getCustomFunctions();
        assertEquals("Should have 0 functions", 0, functions.size());
    }
    
    /**
     * Tests adding and removing custom variables.
     */
    public void testAddRemoveCustomVariable() {
        // Add a variable
        CustomVariable variable = new CustomVariable("testVariable", "string", "testDescription");
        settings.addCustomVariable(variable);
        
        // Verify it was added
        List<CustomVariable> variables = settings.getCustomVariables();
        assertEquals("Should have 1 variable", 1, variables.size());
        assertEquals("Variable should have correct name", "testVariable", variables.get(0).getName());
        
        // Remove the variable
        settings.removeCustomVariable(variable);
        
        // Verify it was removed
        variables = settings.getCustomVariables();
        assertEquals("Should have 0 variables", 0, variables.size());
    }
}