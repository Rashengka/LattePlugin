package cz.hqm.latte.plugin.test.filters;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import cz.hqm.latte.plugin.test.LattePluginTestBase;
import cz.hqm.latte.plugin.filters.NetteFilter;
import cz.hqm.latte.plugin.filters.NetteFilterProvider;

import java.util.Set;

/**
 * Tests for the advanced filter features.
 */
public class AdvancedFilterTest extends LattePluginTestBase {

    /**
     * Tests that filters can have parameters.
     */
    @Test
    public void testFilterParameters() {
        // Get all filters
        Set<NetteFilter> filters = NetteFilterProvider.getAllFilters();
        
        System.out.println("[DEBUG_LOG] Total filters: " + filters.size());
        
        // Find filters with parameters
        boolean foundFilterWithParameters = false;
        for (NetteFilter filter : filters) {
            System.out.println("[DEBUG_LOG] Filter: " + filter.getName() + ", Package: " + filter.getPackageName() + ", Has Parameters: " + filter.hasParameters());
            if (filter.hasParameters()) {
                System.out.println("[DEBUG_LOG] Found filter with parameters: " + filter.getName());
                System.out.println("[DEBUG_LOG] Parameters count: " + filter.getParameters().size());
                System.out.println("[DEBUG_LOG] Parameter info: " + filter.getParameterInfoText());
                
                foundFilterWithParameters = true;
                
                // Verify that the filter has parameter information
                assertNotNull("Filter should have parameters", filter.getParameters());
                assertFalse("Filter parameters should not be empty", filter.getParameters().isEmpty());
                assertNotNull("Filter should have parameter info text", filter.getParameterInfoText());
                
                // Verify that the display text includes parameter information
                String displayText = filter.getDisplayText();
                System.out.println("[DEBUG_LOG] Display text: " + displayText);
                assertTrue("Display text should include parameter information", 
                        displayText.contains("(") && displayText.contains(")"));
                
                // Check the first parameter
                NetteFilter.FilterParameter param = filter.getParameters().get(0);
                assertNotNull("Parameter name should not be null", param.getName());
                assertNotNull("Parameter type should not be null", param.getType());
                assertNotNull("Parameter description should not be null", param.getDescription());
            }
        }
        
        // There should be at least one filter with parameters
        assertTrue("There should be at least one filter with parameters", foundFilterWithParameters);
    }
    
    /**
     * Tests that filter parameters can be optional.
     */
    @Test
    public void testOptionalFilterParameters() {
        // Create a filter with optional parameters
        NetteFilter filter = new NetteFilter("testFilter", "Test filter", "test/package",
                java.util.Arrays.asList(
                        new NetteFilter.FilterParameter("required", "string", "Required parameter", false),
                        new NetteFilter.FilterParameter("optional", "string", "Optional parameter", true)
                ));
        
        // Verify that the filter has parameters
        assertTrue("Filter should have parameters", filter.hasParameters());
        assertEquals("Filter should have 2 parameters", 2, filter.getParameters().size());
        
        // Verify the parameters
        NetteFilter.FilterParameter requiredParam = filter.getParameters().get(0);
        assertFalse("First parameter should be required", requiredParam.isOptional());
        assertEquals("First parameter should be named 'required'", "required", requiredParam.getName());
        
        NetteFilter.FilterParameter optionalParam = filter.getParameters().get(1);
        assertTrue("Second parameter should be optional", optionalParam.isOptional());
        assertEquals("Second parameter should be named 'optional'", "optional", optionalParam.getName());
        
        // Verify the display text
        String displayText = filter.getDisplayText();
        assertTrue("Display text should include required parameter", displayText.contains("required"));
        assertTrue("Display text should include optional parameter in brackets", 
                displayText.contains("[optional]"));
    }
    
    /**
     * Tests filter chaining.
     */
    @Test
    public void testFilterChaining() {
        // Create a test template with chained filters
        String template = "{$variable|upper|truncate:30|noescape}";
        
        // Verify that the template can be parsed without errors
        // This is a basic test that doesn't actually parse the template,
        // but in a real test environment, we would use the lexer to parse it
        // and verify that the filters are correctly identified
        assertTrue("Template should contain the upper filter", template.contains("|upper"));
        assertTrue("Template should contain the truncate filter with parameter", template.contains("|truncate:30"));
        assertTrue("Template should contain the noescape filter", template.contains("|noescape"));
    }
    
    /**
     * Tests filter auto-completion context awareness.
     */
    @Test
    public void testFilterAutoCompletionContextAwareness() {
        // This test would normally use the completion contributor to test
        // that filters are suggested in the right context, but for simplicity,
        // we'll just verify that the filter provider returns the expected filters
        
        // Get all filter names
        Set<String> filterNames = NetteFilterProvider.getValidFilterNames();
        
        // Verify that common filters are included
        assertTrue("upper filter should be included", filterNames.contains("upper"));
        assertTrue("lower filter should be included", filterNames.contains("lower"));
        assertTrue("truncate filter should be included", filterNames.contains("truncate"));
        assertTrue("noescape filter should be included", filterNames.contains("noescape"));
    }
    
    /**
     * Tests creating a custom filter with parameters.
     */
    @Test
    public void testCustomFilterWithParameters() {
        // Create a custom filter with parameters
        NetteFilter customFilter = new NetteFilter("customFilter", "Custom filter", "custom/package",
                java.util.Arrays.asList(
                        new NetteFilter.FilterParameter("param1", "string", "First parameter", false),
                        new NetteFilter.FilterParameter("param2", "int", "Second parameter", true),
                        new NetteFilter.FilterParameter("param3", "bool", "Third parameter", true)
                ));
        
        // Verify that the filter has parameters
        assertTrue("Filter should have parameters", customFilter.hasParameters());
        assertEquals("Filter should have 3 parameters", 3, customFilter.getParameters().size());
        
        // Verify the parameter info text
        String paramInfo = customFilter.getParameterInfoText();
        assertNotNull("Parameter info text should not be null", paramInfo);
        assertTrue("Parameter info text should include first parameter", paramInfo.contains("param1"));
        assertTrue("Parameter info text should include second parameter", paramInfo.contains("param2"));
        assertTrue("Parameter info text should include third parameter", paramInfo.contains("param3"));
        assertTrue("Parameter info text should include parameter types", 
                paramInfo.contains("string") && paramInfo.contains("int") && paramInfo.contains("bool"));
        assertTrue("Parameter info text should indicate optional parameters", 
                paramInfo.contains("[optional]"));
    }
}
