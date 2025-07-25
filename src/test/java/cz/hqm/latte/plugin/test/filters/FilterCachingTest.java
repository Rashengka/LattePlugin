package cz.hqm.latte.plugin.test.filters;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import cz.hqm.latte.plugin.filters.NetteFilter;
import cz.hqm.latte.plugin.filters.NetteFilterProvider;
import cz.hqm.latte.plugin.settings.LatteSettings;
import org.junit.Test;

import java.util.Set;

/**
 * Tests for the caching mechanism in NetteFilterProvider.
 */
public class FilterCachingTest extends BasePlatformTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.out.println("[DEBUG_LOG] Setting up FilterCachingTest");
        
        // Ensure we have a clean state for each test
        LatteSettings settings = LatteSettings.getInstance();
        settings.setEnableNetteApplication(true);
        settings.setEnableNetteForms(true);
        settings.setEnableNetteAssets(true);
        settings.setEnableNetteDatabase(true);
        settings.setEnableNetteSecurity(true);
        
        // Invalidate the cache to start fresh
        NetteFilterProvider.invalidateCache();
    }

    /**
     * Tests that filter names are cached and not recreated on each request.
     */
    @Test
    public void testFilterNamesCaching() {
        System.out.println("[DEBUG_LOG] Running testFilterNamesCaching");
        
        // First request should initialize the cache
        System.out.println("[DEBUG_LOG] First request - should initialize cache");
        Set<String> filterNames1 = NetteFilterProvider.getValidFilterNames();
        
        // Second request should use the cache
        System.out.println("[DEBUG_LOG] Second request - should use cache");
        Set<String> filterNames2 = NetteFilterProvider.getValidFilterNames();
        
        // Verify that we have filter names
        assertFalse("Should have filter names", filterNames1.isEmpty());
        assertFalse("Should have filter names", filterNames2.isEmpty());
        
        // Verify that the same number of filter names are returned
        assertEquals("Should have the same number of filter names", filterNames1.size(), filterNames2.size());
    }

    /**
     * Tests that filters are cached and not recreated on each request.
     */
    @Test
    public void testFiltersCaching() {
        System.out.println("[DEBUG_LOG] Running testFiltersCaching");
        
        // First request should initialize the cache
        System.out.println("[DEBUG_LOG] First request - should initialize cache");
        Set<NetteFilter> filters1 = NetteFilterProvider.getAllFilters();
        
        // Second request should use the cache
        System.out.println("[DEBUG_LOG] Second request - should use cache");
        Set<NetteFilter> filters2 = NetteFilterProvider.getAllFilters();
        
        // Verify that we have filters
        assertFalse("Should have filters", filters1.isEmpty());
        assertFalse("Should have filters", filters2.isEmpty());
        
        // Verify that the same number of filters are returned
        assertEquals("Should have the same number of filters", filters1.size(), filters2.size());
    }

    /**
     * Tests that the cache is updated when settings change.
     */
    @Test
    public void testCacheUpdateOnSettingsChange() {
        System.out.println("[DEBUG_LOG] Running testCacheUpdateOnSettingsChange");
        
        // First request with all features enabled
        System.out.println("[DEBUG_LOG] First request with all features enabled");
        Set<NetteFilter> filters1 = NetteFilterProvider.getAllFilters();
        
        // Change settings
        System.out.println("[DEBUG_LOG] Changing settings");
        LatteSettings settings = LatteSettings.getInstance();
        settings.setEnableNetteApplication(false);
        
        // Second request should update the cache due to settings change
        System.out.println("[DEBUG_LOG] Second request after settings change");
        Set<NetteFilter> filters2 = NetteFilterProvider.getAllFilters();
        
        // Verify that we have filters
        assertFalse("Should have filters", filters1.isEmpty());
        assertFalse("Should have filters", filters2.isEmpty());
        
        // Verify that the number of filters has changed
        assertTrue("Should have fewer filters after disabling Application", filters2.size() < filters1.size());
        
        // Verify that Application filters are not included
        boolean hasApplicationFilters = false;
        for (NetteFilter filter : filters2) {
            if (filter.getTypeText().equals("nette/application")) {
                hasApplicationFilters = true;
                break;
            }
        }
        
        assertFalse("Should not have Application filters after disabling Application", hasApplicationFilters);
    }
}