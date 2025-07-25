package cz.hqm.latte.plugin.test.custom;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import cz.hqm.latte.plugin.custom.CustomFilter;
import cz.hqm.latte.plugin.custom.CustomFiltersProvider;
import cz.hqm.latte.plugin.settings.LatteProjectSettings;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Set;

/**
 * Tests for the caching mechanism in CustomFiltersProvider.
 */
public class CustomFilterCachingTest extends BasePlatformTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.out.println("[DEBUG_LOG] Setting up CustomFilterCachingTest");
        
        // Clear any existing custom filters
        LatteProjectSettings settings = LatteProjectSettings.getInstance(getProject());
        settings.setCustomFilters(new ArrayList<>());
        
        // Add some test filters
        CustomFiltersProvider.addFilter(getProject(), "testFilter1", "Test filter 1");
        CustomFiltersProvider.addFilter(getProject(), "testFilter2", "Test filter 2");
        
        // Invalidate the cache to start fresh
        CustomFiltersProvider.invalidateCache(getProject());
    }

    @Override
    protected void tearDown() throws Exception {
        // Clear custom filters after the test
        LatteProjectSettings settings = LatteProjectSettings.getInstance(getProject());
        settings.setCustomFilters(new ArrayList<>());
        
        super.tearDown();
    }

    /**
     * Tests that custom filter names are cached and not recreated on each request.
     */
    @Test
    public void testCustomFilterNamesCaching() {
        System.out.println("[DEBUG_LOG] Running testCustomFilterNamesCaching");
        
        // First request should initialize the cache
        System.out.println("[DEBUG_LOG] First request - should initialize cache");
        Set<String> filterNames1 = CustomFiltersProvider.getAllFilterNames(getProject());
        
        // Second request should use the cache
        System.out.println("[DEBUG_LOG] Second request - should use cache");
        Set<String> filterNames2 = CustomFiltersProvider.getAllFilterNames(getProject());
        
        // Verify that we have filter names
        assertFalse("Should have filter names", filterNames1.isEmpty());
        assertFalse("Should have filter names", filterNames2.isEmpty());
        
        // Verify that the same number of filter names are returned
        assertEquals("Should have the same number of filter names", filterNames1.size(), filterNames2.size());
        
        // Verify that the filter names are the same
        assertTrue("Should contain testFilter1", filterNames1.contains("testFilter1"));
        assertTrue("Should contain testFilter2", filterNames1.contains("testFilter2"));
    }

    /**
     * Tests that custom filters are cached and not recreated on each request.
     */
    @Test
    public void testCustomFiltersCaching() {
        System.out.println("[DEBUG_LOG] Running testCustomFiltersCaching");
        
        // First request should initialize the cache
        System.out.println("[DEBUG_LOG] First request - should initialize cache");
        Set<CustomFilter> filters1 = CustomFiltersProvider.getAllFilters(getProject());
        
        // Second request should use the cache
        System.out.println("[DEBUG_LOG] Second request - should use cache");
        Set<CustomFilter> filters2 = CustomFiltersProvider.getAllFilters(getProject());
        
        // Verify that we have filters
        assertFalse("Should have filters", filters1.isEmpty());
        assertFalse("Should have filters", filters2.isEmpty());
        
        // Verify that the same number of filters are returned
        assertEquals("Should have the same number of filters", filters1.size(), filters2.size());
    }

    /**
     * Tests that the cache is updated when filters are added.
     */
    @Test
    public void testCacheUpdateOnFilterAdd() {
        System.out.println("[DEBUG_LOG] Running testCacheUpdateOnFilterAdd");
        
        // First request with initial filters
        System.out.println("[DEBUG_LOG] First request with initial filters");
        Set<String> filterNames1 = CustomFiltersProvider.getAllFilterNames(getProject());
        
        // Add a new filter
        System.out.println("[DEBUG_LOG] Adding a new filter");
        CustomFiltersProvider.addFilter(getProject(), "testFilter3", "Test filter 3");
        
        // Second request should update the cache due to filter addition
        System.out.println("[DEBUG_LOG] Second request after filter addition");
        Set<String> filterNames2 = CustomFiltersProvider.getAllFilterNames(getProject());
        
        // Verify that we have filter names
        assertFalse("Should have filter names", filterNames1.isEmpty());
        assertFalse("Should have filter names", filterNames2.isEmpty());
        
        // Verify that the number of filter names has increased
        assertEquals("Should have one more filter name", filterNames1.size() + 1, filterNames2.size());
        
        // Verify that the new filter is included
        assertTrue("Should contain the new filter", filterNames2.contains("testFilter3"));
    }

    /**
     * Tests that the cache is updated when filters are removed.
     */
    @Test
    public void testCacheUpdateOnFilterRemove() {
        System.out.println("[DEBUG_LOG] Running testCacheUpdateOnFilterRemove");
        
        // First request with initial filters
        System.out.println("[DEBUG_LOG] First request with initial filters");
        Set<String> filterNames1 = CustomFiltersProvider.getAllFilterNames(getProject());
        
        // Remove a filter
        System.out.println("[DEBUG_LOG] Removing a filter");
        CustomFiltersProvider.removeFilter(getProject(), "testFilter1");
        
        // Second request should update the cache due to filter removal
        System.out.println("[DEBUG_LOG] Second request after filter removal");
        Set<String> filterNames2 = CustomFiltersProvider.getAllFilterNames(getProject());
        
        // Verify that we have filter names
        assertFalse("Should have filter names", filterNames1.isEmpty());
        assertFalse("Should have filter names", filterNames2.isEmpty());
        
        // Verify that the number of filter names has decreased
        assertEquals("Should have one less filter name", filterNames1.size() - 1, filterNames2.size());
        
        // Verify that the removed filter is not included
        assertFalse("Should not contain the removed filter", filterNames2.contains("testFilter1"));
    }
}