package org.latte.plugin.test.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.Project;
import org.latte.plugin.custom.*;
import org.latte.plugin.test.LattePluginTestBase;

import java.util.List;

/**
 * Tests for custom elements in code completion.
 */
public class CustomElementsCompletionTest extends LattePluginTestBase {
    
    private Project project;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // Get the project from the test fixture
        project = getProject();
        
        // Clear any existing custom elements
        clearCustomElements();
        
        // Add test custom elements
        addTestCustomElements();
    }
    
    @Override
    protected void tearDown() throws Exception {
        // Clear custom elements
        clearCustomElements();
        
        super.tearDown();
    }
    
    /**
     * Clears all custom elements.
     */
    private void clearCustomElements() {
        CustomTagsProvider.getAllTags(project).forEach(tag -> 
            CustomTagsProvider.removeTag(project, tag.getName()));
        
        CustomFiltersProvider.getAllFilters(project).forEach(filter -> 
            CustomFiltersProvider.removeFilter(project, filter.getName()));
        
        CustomFunctionsProvider.getAllFunctions(project).forEach(function -> 
            CustomFunctionsProvider.removeFunction(project, function.getName()));
        
        CustomVariablesProvider.getAllVariables(project).forEach(variable -> 
            CustomVariablesProvider.removeVariable(project, variable.getName()));
    }
    
    /**
     * Adds test custom elements.
     */
    private void addTestCustomElements() {
        // Add custom tags
        CustomTagsProvider.addTag(project, "customTag", "Custom tag for testing");
        CustomTagsProvider.addTag(project, "anotherTag", "Another custom tag");
        
        // Add custom filters
        CustomFiltersProvider.addFilter(project, "customFilter", "Custom filter for testing");
        CustomFiltersProvider.addFilter(project, "anotherFilter", "Another custom filter");
        
        // Add custom functions
        CustomFunctionsProvider.addFunction(project, "customFunction", "Custom function for testing");
        CustomFunctionsProvider.addFunction(project, "anotherFunction", "Another custom function");
        
        // Add custom variables
        CustomVariablesProvider.addVariable(project, "customVar", "string", "Custom variable for testing");
        CustomVariablesProvider.addVariable(project, "anotherVar", "int", "Another custom variable");
    }
    
    /**
     * Tests that custom tags are included in completion.
     */
    public void testCustomTagCompletion() {
        // Create a Latte file with a macro start
        myFixture.configureByText("test.latte", "{<caret>}");
        
        // Invoke completion
        myFixture.complete(CompletionType.BASIC);
        
        // Get the lookup elements
        List<String> lookupElementStrings = myFixture.getLookupElementStrings();
        
        // Verify that custom tags are included
        assertNotNull("Lookup elements should not be null", lookupElementStrings);
        assertTrue("Completion should include customTag", lookupElementStrings.contains("customTag"));
        assertTrue("Completion should include anotherTag", lookupElementStrings.contains("anotherTag"));
    }
    
    /**
     * Tests that custom filters are included in completion.
     */
    public void testCustomFilterCompletion() {
        // Create a Latte file with a filter pipe
        myFixture.configureByText("test.latte", "{$var|<caret>}");
        
        // Invoke completion
        myFixture.complete(CompletionType.BASIC);
        
        // Get the lookup elements
        List<String> lookupElementStrings = myFixture.getLookupElementStrings();
        
        // Verify that custom filters are included
        assertNotNull("Lookup elements should not be null", lookupElementStrings);
        assertTrue("Completion should include customFilter", lookupElementStrings.contains("customFilter"));
        assertTrue("Completion should include anotherFilter", lookupElementStrings.contains("anotherFilter"));
    }
    
    /**
     * Tests that custom functions are included in completion.
     */
    public void testCustomFunctionCompletion() {
        // Create a Latte file with a function context
        myFixture.configureByText("test.latte", "{= <caret>}");
        
        // Invoke completion
        myFixture.complete(CompletionType.BASIC);
        
        // Get the lookup elements
        List<String> lookupElementStrings = myFixture.getLookupElementStrings();
        
        // Verify that custom functions are included
        assertNotNull("Lookup elements should not be null", lookupElementStrings);
        assertTrue("Completion should include customFunction", lookupElementStrings.contains("customFunction"));
        assertTrue("Completion should include anotherFunction", lookupElementStrings.contains("anotherFunction"));
    }
    
    /**
     * Tests that custom variables are included in completion.
     */
    public void testCustomVariableCompletion() {
        // Create a Latte file with a variable context
        myFixture.configureByText("test.latte", "{$<caret>}");
        
        // Invoke completion
        myFixture.complete(CompletionType.BASIC);
        
        // Get the lookup elements
        List<String> lookupElementStrings = myFixture.getLookupElementStrings();
        
        // Verify that custom variables are included
        assertNotNull("Lookup elements should not be null", lookupElementStrings);
        assertTrue("Completion should include customVar", lookupElementStrings.contains("customVar"));
        assertTrue("Completion should include anotherVar", lookupElementStrings.contains("anotherVar"));
    }
}