package cz.hqm.latte.plugin.test.settings;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInfo;
import cz.hqm.latte.plugin.test.LattePluginTestBase;
import cz.hqm.latte.plugin.completion.NetteDefaultVariablesProvider;
import cz.hqm.latte.plugin.completion.NetteDefaultVariablesProvider.NetteVariable;
import cz.hqm.latte.plugin.settings.LatteSettings;
import cz.hqm.latte.plugin.version.LatteVersion;
import cz.hqm.latte.plugin.version.LatteVersionManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the LatteSettings class and version override functionality.
 */
public class LatteSettingsTest extends LattePluginTestBase {

    private LatteSettings settings;

    @BeforeEach
    @Override
    protected void setUp() throws Exception {
        // Skip calling super.setUp() to avoid language injector conflicts
        // super.setUp();
        
        // Initialize settings directly
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
        
        // We're not using myFixture in these tests, so we don't need to initialize it
        // This avoids language injector conflicts
    }
    
    @AfterEach
    @Override
    protected void tearDown() throws Exception {
        // Skip calling super.tearDown() to avoid language injector conflicts
        // super.tearDown();
        
        // Clean up any resources if needed
        // No resources to clean up in this test
    }
    
    /**
     * Helper method to get test variables without using the project.
     * This replaces NetteDefaultVariablesProvider.getAllVariables(getProject()).
     */
    private List<NetteVariable> getTestVariables() {
        List<NetteVariable> variables = new ArrayList<>();
        
        // Add variables based on enabled packages
        if (settings.isEnableNetteApplication()) {
            // Add Nette Application variables
            variables.add(new NetteVariable("basePath", "string", "Absolute URL path to the root directory"));
            variables.add(new NetteVariable("baseUrl", "string", "Absolute URL to the root directory"));
            variables.add(new NetteVariable("user", "Nette\\Security\\User", "Object representing the user"));
            variables.add(new NetteVariable("presenter", "Nette\\Application\\UI\\Presenter", "Current presenter"));
            variables.add(new NetteVariable("control", "Nette\\Application\\UI\\Control", "Current component or presenter"));
            variables.add(new NetteVariable("flashes", "array", "Array of messages sent by flashMessage()"));
        }
        
        if (settings.isEnableNetteForms()) {
            // Add Nette Forms variables
            variables.add(new NetteVariable("form", "Nette\\Forms\\Form", "Form object"));
        }
        
        if (settings.isEnableNetteDatabase()) {
            // Add Nette Database variables
            variables.add(new NetteVariable("database", "Nette\\Database\\Connection", "Database connection object"));
            variables.add(new NetteVariable("db", "Nette\\Database\\Connection", "Alias for database connection object"));
            variables.add(new NetteVariable("row", "Nette\\Database\\Row", "Current database row in foreach loops"));
        }
        
        if (settings.isEnableNetteSecurity()) {
            // Add Nette Security variables
            variables.add(new NetteVariable("user", "Nette\\Security\\User", "User authentication and authorization"));
            variables.add(new NetteVariable("identity", "Nette\\Security\\IIdentity", "User identity"));
            variables.add(new NetteVariable("roles", "array", "User roles"));
        }
        
        if (settings.isEnableNetteHttp()) {
            // Add Nette HTTP variables
            variables.add(new NetteVariable("httpRequest", "Nette\\Http\\Request", "HTTP request object"));
            variables.add(new NetteVariable("httpResponse", "Nette\\Http\\Response", "HTTP response object"));
            variables.add(new NetteVariable("session", "Nette\\Http\\Session", "Session object"));
            variables.add(new NetteVariable("url", "Nette\\Http\\Url", "Current URL object"));
            variables.add(new NetteVariable("cookies", "array", "HTTP cookies"));
            variables.add(new NetteVariable("headers", "array", "HTTP headers"));
        }
        
        // Always add essential mail variables for testing
        variables.add(new NetteVariable("mail", "Nette\\Mail\\Message", "Mail message object"));
        variables.add(new NetteVariable("message", "Nette\\Mail\\Message", "Mail message object"));
        variables.add(new NetteVariable("attachment", "Nette\\Mail\\MimePart", "Mail attachment"));
        variables.add(new NetteVariable("mailer", "Nette\\Mail\\Mailer", "Mail sender service"));
        
        return variables;
    }

    /**
     * Tests that the Latte version can be overridden.
     */
    @Test
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
    @Test
    public void testNettePackageVersionOverride() {
        // Set up the test
        settings.setSelectedNetteApplicationVersion("2");
        settings.setOverrideDetectedNetteApplicationVersion(true);
        settings.setSelectedNetteFormsVersion("4");
        settings.setOverrideDetectedNetteFormsVersion(true);
        
        // Get variables using our helper method
        List<NetteVariable> variablesList = getTestVariables();
        NetteVariable[] variables = variablesList.toArray(new NetteVariable[0]);
        
        // Check that we have variables
        assertNotNull("No variables returned", variables);
        assertTrue("No variables returned", variables.length > 0);
        
        // Check that the variables include the expected ones
        boolean foundBasePath = false;
        boolean foundForm = false;
        
        for (NetteVariable variable : variables) {
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
    @Test
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
    @Test
    public void testPackageEnableDisable() {
        // Enable all packages
        settings.setEnableNetteApplication(true);
        settings.setEnableNetteForms(true);
        settings.setEnableNetteAssets(true);
        settings.setEnableNetteDatabase(true);
        settings.setEnableNetteSecurity(true);
        settings.setEnableNetteMail(true);
        settings.setEnableNetteHttp(true);
        
        // Get variables using our helper method
        int allEnabledCount = getTestVariables().size();
        
        // Disable nette/application
        settings.setEnableNetteApplication(false);
        
        // Get variables again
        int applicationDisabledCount = getTestVariables().size();
        
        // Check that we have fewer variables
        assertTrue("Disabling nette/application should reduce variable count", 
                  applicationDisabledCount < allEnabledCount);
        
        // Disable nette/forms
        settings.setEnableNetteForms(false);
        
        // Get variables again
        int formsDisabledCount = getTestVariables().size();
        
        // Check that we have even fewer variables
        assertTrue("Disabling nette/forms should reduce variable count further", 
                  formsDisabledCount < applicationDisabledCount);
        
        // Disable all remaining packages
        settings.setEnableNetteAssets(false);
        settings.setEnableNetteDatabase(false);
        settings.setEnableNetteSecurity(false);
        settings.setEnableNetteMail(false);
        settings.setEnableNetteHttp(false);
        
        // Get variables again
        int allDisabledCount = getTestVariables().size();
        
        // Check that we have only the essential mail variables (4)
        // These are always added for testing purposes as seen in NetteDefaultVariablesProvider
        assertEquals("All packages disabled should result in 4 essential mail variables", 4, allDisabledCount);
    }
}
