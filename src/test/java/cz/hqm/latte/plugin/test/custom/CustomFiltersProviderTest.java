package cz.hqm.latte.plugin.test.custom;

import com.intellij.openapi.project.Project;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import cz.hqm.latte.plugin.custom.CustomFilter;
import cz.hqm.latte.plugin.custom.CustomFiltersProvider;
import cz.hqm.latte.plugin.settings.LatteProjectSettings;
import cz.hqm.latte.plugin.test.LattePluginTestBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Tests for the CustomFiltersProvider class.
 */
public class CustomFiltersProviderTest extends LattePluginTestBase {
    
    private Project project;
    private LatteProjectSettings settings;
    private List<CustomFilter> testFilters;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // Get the project from the test fixture
        project = getProject();
        
        // Get the settings instance for the project
        settings = LatteProjectSettings.getInstance(project);
        
        // Save the original filters
        testFilters = new ArrayList<>(settings.getCustomFilters());
        
        // Clear the filters for testing
        settings.setCustomFilters(new ArrayList<>());
        
        // Invalidate the cache to ensure it's cleared
        CustomFiltersProvider.invalidateCache(project);
    }
    
    @Override
    protected void tearDown() throws Exception {
        // Restore the original filters
        settings.setCustomFilters(testFilters);
        
        super.tearDown();
    }
    
    /**
     * Tests getting all filters when there are no filters.
     */
    @Test
    public void testGetAllFiltersEmpty() {
        Set<CustomFilter> filters = CustomFiltersProvider.getAllFilters(project);
        
        assertNotNull("Filters set should not be null", filters);
        assertEquals("Filters set should be empty", 0, filters.size());
    }
    
    /**
     * Tests getting all filter names when there are no filters.
     */
    @Test
    public void testGetAllFilterNamesEmpty() {
        Set<String> filterNames = CustomFiltersProvider.getAllFilterNames(project);
        
        assertNotNull("Filter names set should not be null", filterNames);
        assertEquals("Filter names set should be empty", 0, filterNames.size());
    }
    
    /**
     * Tests filter existence check when the filter doesn't exist.
     */
    @Test
    public void testFilterExistsFalse() {
        boolean exists = CustomFiltersProvider.filterExists(project, "nonExistentFilter");
        
        assertFalse("Filter should not exist", exists);
    }
    
    /**
     * Tests getting a filter by name when the filter doesn't exist.
     */
    @Test
    public void testGetFilterByNameNotFound() {
        CustomFilter filter = CustomFiltersProvider.getFilterByName(project, "nonExistentFilter");
        
        assertNull("Filter should not be found", filter);
    }
    
    /**
     * Tests adding a filter.
     */
    @Test
    public void testAddFilter() {
        CustomFilter filter = CustomFiltersProvider.addFilter(project, "testFilter", "testDescription");
        
        assertNotNull("Added filter should not be null", filter);
        assertEquals("Filter name should be set correctly", "testFilter", filter.getName());
        assertEquals("Filter description should be set correctly", "testDescription", filter.getDescription());
        
        // Verify the filter was added to the settings
        List<CustomFilter> filters = settings.getCustomFilters();
        assertEquals("Settings should have 1 filter", 1, filters.size());
        assertEquals("Filter in settings should have the correct name", "testFilter", filters.get(0).getName());
        assertEquals("Filter in settings should have the correct description", "testDescription", filters.get(0).getDescription());
        
        // Verify the filter can be retrieved
        assertTrue("Filter should exist", CustomFiltersProvider.filterExists(project, "testFilter"));
        CustomFilter retrievedFilter = CustomFiltersProvider.getFilterByName(project, "testFilter");
        assertNotNull("Retrieved filter should not be null", retrievedFilter);
        assertEquals("Retrieved filter should have the correct name", "testFilter", retrievedFilter.getName());
        assertEquals("Retrieved filter should have the correct description", "testDescription", retrievedFilter.getDescription());
    }
    
    /**
     * Tests removing a filter.
     */
    @Test
    public void testRemoveFilter() {
        // Add a filter first
        CustomFiltersProvider.addFilter(project, "testFilter", "testDescription");
        
        // Verify the filter exists
        assertTrue("Filter should exist before removal", CustomFiltersProvider.filterExists(project, "testFilter"));
        
        // Remove the filter
        boolean removed = CustomFiltersProvider.removeFilter(project, "testFilter");
        
        assertTrue("Filter should be removed successfully", removed);
        assertFalse("Filter should not exist after removal", CustomFiltersProvider.filterExists(project, "testFilter"));
        assertEquals("Settings should have 0 filters", 0, settings.getCustomFilters().size());
    }
    
    /**
     * Tests removing a non-existent filter.
     */
    @Test
    public void testRemoveNonExistentFilter() {
        boolean removed = CustomFiltersProvider.removeFilter(project, "nonExistentFilter");
        
        assertFalse("Non-existent filter should not be removed", removed);
    }
    
    /**
     * Tests adding multiple filters and retrieving them.
     */
    @Test
    public void testAddMultipleFilters() {
        CustomFiltersProvider.addFilter(project, "filter1", "description1");
        CustomFiltersProvider.addFilter(project, "filter2", "description2");
        CustomFiltersProvider.addFilter(project, "filter3", "description3");
        
        Set<CustomFilter> filters = CustomFiltersProvider.getAllFilters(project);
        assertEquals("Should have 3 filters", 3, filters.size());
        
        Set<String> filterNames = CustomFiltersProvider.getAllFilterNames(project);
        assertEquals("Should have 3 filter names", 3, filterNames.size());
        assertTrue("Should contain filter1", filterNames.contains("filter1"));
        assertTrue("Should contain filter2", filterNames.contains("filter2"));
        assertTrue("Should contain filter3", filterNames.contains("filter3"));
    }
    
    /**
     * Tests adding a duplicate filter.
     */
    @Test
    public void testAddDuplicateFilter() {
        CustomFiltersProvider.addFilter(project, "testFilter", "description1");
        CustomFiltersProvider.addFilter(project, "testFilter", "description2");
        
        Set<CustomFilter> filters = CustomFiltersProvider.getAllFilters(project);
        assertEquals("Should have 1 filter (no duplicates)", 1, filters.size());
        
        // The first filter should be preserved
        CustomFilter filter = CustomFiltersProvider.getFilterByName(project, "testFilter");
        assertEquals("Description should be from the first filter", "description1", filter.getDescription());
    }
}
