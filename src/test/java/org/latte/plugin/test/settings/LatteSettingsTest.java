package org.latte.plugin.test.settings;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.latte.plugin.completion.NetteDefaultVariablesProvider;
import org.latte.plugin.settings.LatteSettings;
import org.latte.plugin.version.LatteVersion;
import org.latte.plugin.version.LatteVersionManager;

/**
 * Tests for the LatteSettings class and version override functionality.
 */
public class LatteSettingsTest extends BasePlatformTestCase {

    private LatteSettings settings;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        settings = LatteSettings.getInstance();
        
        // Reset settings to defaults
        settings.setSelectedVersion(null);
        settings.setOverrideDetectedVersion(false);
        settings.setEnableNetteApplication(true);
        settings.setEnableNetteForms(true);
        settings.setEnableNetteAssets(true);
        settings.setSelectedNetteApplicationVersion(null);
        settings.setOverrideDetectedNetteApplicationVersion(false);
        settings.setSelectedNetteFormsVersion(null);
        settings.setOverrideDetectedNetteFormsVersion(false);
        settings.setSelectedNetteAssetsVersion(null);
        settings.setOverrideDetectedNetteAssetsVersion(false);
    }

    /**
     * Tests that the Latte version can be overridden.
     */
    public void testLatteVersionOverride() {
        // Set the current version to 2.x
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_2X);
        
        // Check that isVersion2x returns true and isVersion3x returns false
        assertTrue("Version should be 2.x", LatteVersionManager.isVersion2x());
        assertFalse("Version should not be 3.x", LatteVersionManager.isVersion3x());
        
        // Override the version to 3.0+
        settings.setSelectedVersion("3.0+");
        settings.setOverrideDetectedVersion(true);
        
        // Update the current version based on settings
        LatteVersionManager.setCurrentVersion(settings.getSelectedVersionEnum());
        
        // Check that isVersion2x returns false and isVersion3x returns true
        assertFalse("Version should not be 2.x", LatteVersionManager.isVersion2x());
        assertTrue("Version should be 3.x", LatteVersionManager.isVersion3x());
    }

    /**
     * Tests that the Nette package versions can be overridden.
     */
    public void testNettePackageVersionOverride() {
        // Set up the test
        settings.setSelectedNetteApplicationVersion("2");
        settings.setOverrideDetectedNetteApplicationVersion(true);
        settings.setSelectedNetteFormsVersion("4");
        settings.setOverrideDetectedNetteFormsVersion(true);
        
        // Get variables for the current project
        NetteDefaultVariablesProvider.NetteVariable[] variables = 
            NetteDefaultVariablesProvider.getAllVariables(getProject()).toArray(new NetteDefaultVariablesProvider.NetteVariable[0]);
        
        // Check that we have variables
        assertNotNull("No variables returned", variables);
        assertTrue("No variables returned", variables.length > 0);
        
        // Check that the variables include the expected ones
        boolean foundBasePath = false;
        boolean foundForm = false;
        
        for (NetteDefaultVariablesProvider.NetteVariable variable : variables) {
            if ("basePath".equals(variable.getName())) {
                foundBasePath = true;
            } else if ("form".equals(variable.getName())) {
                foundForm = true;
            }
        }
        
        assertTrue("basePath variable not found", foundBasePath);
        assertTrue("form variable not found", foundForm);
    }

    /**
     * Tests that Latte 4.0+ version can be detected and overridden.
     */
    public void testLatteVersion4xOverride() {
        // Set the current version to 4.0+
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_4X);
        
        // Check that isVersion4x returns true and other version checks return false
        assertTrue("Version should be 4.0+", LatteVersionManager.isVersion4x());
        assertFalse("Version should not be 2.x", LatteVersionManager.isVersion2x());
        assertFalse("Version should not be 3.x", LatteVersionManager.isVersion3x());
        
        // Override the version to 3.0+
        settings.setSelectedVersion("3.0+");
        settings.setOverrideDetectedVersion(true);
        
        // Update the current version based on settings
        LatteVersionManager.setCurrentVersion(settings.getSelectedVersionEnum());
        
        // Check that isVersion4x returns false and isVersion3x returns true
        assertFalse("Version should not be 4.0+", LatteVersionManager.isVersion4x());
        assertFalse("Version should not be 2.x", LatteVersionManager.isVersion2x());
        assertTrue("Version should be 3.x", LatteVersionManager.isVersion3x());
    }

    /**
     * Tests that package enable/disable settings work.
     */
    public void testPackageEnableDisable() {
        // Enable all packages
        settings.setEnableNetteApplication(true);
        settings.setEnableNetteForms(true);
        settings.setEnableNetteAssets(true);
        
        // Get variables for the current project
        int allEnabledCount = NetteDefaultVariablesProvider.getAllVariables(getProject()).size();
        
        // Disable nette/application
        settings.setEnableNetteApplication(false);
        
        // Get variables again
        int applicationDisabledCount = NetteDefaultVariablesProvider.getAllVariables(getProject()).size();
        
        // Check that we have fewer variables
        assertTrue("Disabling nette/application should reduce variable count", 
                  applicationDisabledCount < allEnabledCount);
        
        // Disable nette/forms
        settings.setEnableNetteForms(false);
        
        // Get variables again
        int formsDisabledCount = NetteDefaultVariablesProvider.getAllVariables(getProject()).size();
        
        // Check that we have even fewer variables
        assertTrue("Disabling nette/forms should reduce variable count further", 
                  formsDisabledCount < applicationDisabledCount);
        
        // Disable nette/assets
        settings.setEnableNetteAssets(false);
        
        // Get variables again
        int allDisabledCount = NetteDefaultVariablesProvider.getAllVariables(getProject()).size();
        
        // Check that we have no variables
        assertEquals("All packages disabled should result in 0 variables", 0, allDisabledCount);
    }
}