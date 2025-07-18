package cz.hqm.latte.plugin.test.filters;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import cz.hqm.latte.plugin.test.LattePluginTestBase;
import cz.hqm.latte.plugin.filters.NetteFilter;
import cz.hqm.latte.plugin.filters.NetteFilterProvider;
import cz.hqm.latte.plugin.settings.LatteSettings;

import java.util.Set;

/**
 * Tests for the NetteFilter and NetteFilterProvider classes.
 */
public class NetteFilterTest extends LattePluginTestBase {

    /**
     * Tests that the NetteFilterProvider returns the correct filters based on enabled settings.
     */
    @Test
    public void testGetAllFilters() {
        // Get settings
        LatteSettings settings = LatteSettings.getInstance();
        
        // Save original settings
        boolean originalApplicationSetting = settings.isEnableNetteApplication();
        boolean originalFormsSetting = settings.isEnableNetteForms();
        boolean originalAssetsSetting = settings.isEnableNetteAssets();
        boolean originalDatabaseSetting = settings.isEnableNetteDatabase();
        
        try {
            // Enable all packages
            settings.setEnableNetteApplication(true);
            settings.setEnableNetteForms(true);
            settings.setEnableNetteAssets(true);
            settings.setEnableNetteDatabase(true);
            
            // Get all filters
            Set<NetteFilter> allFilters = NetteFilterProvider.getAllFilters();
            
            // Verify that filters from all packages are included
            assertContainsFilter(allFilters, "escapeUrl", "nette/application");
            assertContainsFilter(allFilters, "translate", "nette/forms");
            assertContainsFilter(allFilters, "asset", "nette/assets");
            assertContainsFilter(allFilters, "table", "nette/database");
            
            // Verify that core filters are included
            assertContainsFilter(allFilters, "upper", "latte/core");
            assertContainsFilter(allFilters, "lower", "latte/core");
            assertContainsFilter(allFilters, "escape", "latte/core");
            
            // Disable nette/application
            settings.setEnableNetteApplication(false);
            
            // Get all filters again
            allFilters = NetteFilterProvider.getAllFilters();
            
            // Verify that filters from nette/application are not included
            assertNotContainsFilter(allFilters, "escapeUrl", "nette/application");
            assertContainsFilter(allFilters, "translate", "nette/forms");
            assertContainsFilter(allFilters, "asset", "nette/assets");
            
            // Disable nette/forms
            settings.setEnableNetteForms(false);
            
            // Get all filters again
            allFilters = NetteFilterProvider.getAllFilters();
            
            // Verify that filters from nette/application and nette/forms are not included
            assertNotContainsFilter(allFilters, "escapeUrl", "nette/application");
            assertNotContainsFilter(allFilters, "translate", "nette/forms");
            assertContainsFilter(allFilters, "asset", "nette/assets");
            
            // Disable nette/assets
            settings.setEnableNetteAssets(false);
            
            // Get all filters again
            allFilters = NetteFilterProvider.getAllFilters();
            
            // Verify that filters from nette/database are still included
            assertNotContainsFilter(allFilters, "escapeUrl", "nette/application");
            assertNotContainsFilter(allFilters, "translate", "nette/forms");
            assertNotContainsFilter(allFilters, "asset", "nette/assets");
            assertContainsFilter(allFilters, "table", "nette/database");
            assertContainsFilter(allFilters, "upper", "latte/core");
            assertContainsFilter(allFilters, "lower", "latte/core");
            assertContainsFilter(allFilters, "escape", "latte/core");
            
            // Disable nette/database
            settings.setEnableNetteDatabase(false);
            
            // Get all filters again
            allFilters = NetteFilterProvider.getAllFilters();
            
            // Verify that only core filters are included
            assertNotContainsFilter(allFilters, "escapeUrl", "nette/application");
            assertNotContainsFilter(allFilters, "translate", "nette/forms");
            assertNotContainsFilter(allFilters, "asset", "nette/assets");
            assertNotContainsFilter(allFilters, "table", "nette/database");
            assertContainsFilter(allFilters, "upper", "latte/core");
            assertContainsFilter(allFilters, "lower", "latte/core");
            assertContainsFilter(allFilters, "escape", "latte/core");
        } finally {
            // Restore original settings
            settings.setEnableNetteApplication(originalApplicationSetting);
            settings.setEnableNetteForms(originalFormsSetting);
            settings.setEnableNetteAssets(originalAssetsSetting);
            settings.setEnableNetteDatabase(originalDatabaseSetting);
        }
    }
    
    /**
     * Tests that the NetteFilterProvider returns the correct filter names based on enabled settings.
     */
    @Test
    public void testGetValidFilterNames() {
        // Get settings
        LatteSettings settings = LatteSettings.getInstance();
        
        // Save original settings
        boolean originalApplicationSetting = settings.isEnableNetteApplication();
        boolean originalFormsSetting = settings.isEnableNetteForms();
        boolean originalAssetsSetting = settings.isEnableNetteAssets();
        boolean originalDatabaseSetting = settings.isEnableNetteDatabase();
        
        try {
            // Enable all packages
            settings.setEnableNetteApplication(true);
            settings.setEnableNetteForms(true);
            settings.setEnableNetteAssets(true);
            settings.setEnableNetteDatabase(true);
            
            // Get all filter names
            Set<String> filterNames = NetteFilterProvider.getValidFilterNames();
            
            // Verify that filter names from all packages are included
            assertTrue("Filter names should include 'escapeUrl'", filterNames.contains("escapeUrl"));
            assertTrue("Filter names should include 'translate'", filterNames.contains("translate"));
            assertTrue("Filter names should include 'asset'", filterNames.contains("asset"));
            assertTrue("Filter names should include 'table'", filterNames.contains("table"));
            
            // Verify that core filter names are included
            assertTrue("Filter names should include 'upper'", filterNames.contains("upper"));
            assertTrue("Filter names should include 'lower'", filterNames.contains("lower"));
            assertTrue("Filter names should include 'escape'", filterNames.contains("escape"));
            
            // Disable application, forms, and assets packages but keep database enabled
            settings.setEnableNetteApplication(false);
            settings.setEnableNetteForms(false);
            settings.setEnableNetteAssets(false);
            
            // Get all filter names again
            filterNames = NetteFilterProvider.getValidFilterNames();
            
            // Verify that database filter names are still included
            assertFalse("Filter names should not include 'escapeUrl'", filterNames.contains("escapeUrl"));
            assertFalse("Filter names should not include 'translate'", filterNames.contains("translate"));
            assertFalse("Filter names should not include 'asset'", filterNames.contains("asset"));
            assertTrue("Filter names should include 'table'", filterNames.contains("table"));
            assertTrue("Filter names should include 'upper'", filterNames.contains("upper"));
            assertTrue("Filter names should include 'lower'", filterNames.contains("lower"));
            assertTrue("Filter names should include 'escape'", filterNames.contains("escape"));
            
            // Disable database package
            settings.setEnableNetteDatabase(false);
            
            // Get all filter names again
            filterNames = NetteFilterProvider.getValidFilterNames();
            
            // Verify that only core filter names are included
            assertFalse("Filter names should not include 'escapeUrl'", filterNames.contains("escapeUrl"));
            assertFalse("Filter names should not include 'translate'", filterNames.contains("translate"));
            assertFalse("Filter names should not include 'asset'", filterNames.contains("asset"));
            assertFalse("Filter names should not include 'table'", filterNames.contains("table"));
            
            // Note: Core filters are always included, unlike macros and attributes
            assertTrue("Filter names should include 'upper'", filterNames.contains("upper"));
            assertTrue("Filter names should include 'lower'", filterNames.contains("lower"));
            assertTrue("Filter names should include 'escape'", filterNames.contains("escape"));
        } finally {
            // Restore original settings
            settings.setEnableNetteApplication(originalApplicationSetting);
            settings.setEnableNetteForms(originalFormsSetting);
            settings.setEnableNetteAssets(originalAssetsSetting);
            settings.setEnableNetteDatabase(originalDatabaseSetting);
        }
    }
    
    /**
     * Asserts that the given set of filters contains a filter with the given name and package.
     *
     * @param filters The set of filters
     * @param name The filter name
     * @param packageName The package name
     */
    private void assertContainsFilter(Set<NetteFilter> filters, String name, String packageName) {
        boolean found = false;
        for (NetteFilter filter : filters) {
            if (filter.getName().equals(name) && filter.getPackageName().equals(packageName)) {
                found = true;
                break;
            }
        }
        assertTrue("Filters should contain '" + name + "' from '" + packageName + "'", found);
    }
    
    /**
     * Asserts that the given set of filters does not contain a filter with the given name and package.
     *
     * @param filters The set of filters
     * @param name The filter name
     * @param packageName The package name
     */
    private void assertNotContainsFilter(Set<NetteFilter> filters, String name, String packageName) {
        boolean found = false;
        for (NetteFilter filter : filters) {
            if (filter.getName().equals(name) && filter.getPackageName().equals(packageName)) {
                found = true;
                break;
            }
        }
        assertFalse("Filters should not contain '" + name + "' from '" + packageName + "'", found);
    }
}
