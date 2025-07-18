package cz.hqm.latte.plugin.test.macros;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import cz.hqm.latte.plugin.test.LattePluginTestBase;
import cz.hqm.latte.plugin.macros.NetteMacro;
import cz.hqm.latte.plugin.macros.NetteMacroProvider;
import cz.hqm.latte.plugin.settings.LatteSettings;

import java.util.Set;

/**
 * Tests for the NetteMacro and NetteMacroProvider classes.
 */
public class NetteMacroTest extends LattePluginTestBase {

    /**
     * Tests that the NetteMacroProvider returns the correct macros based on enabled settings.
     */
    @Test
    public void testGetAllMacros() {
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
            
            // Get all macros
            Set<NetteMacro> allMacros = NetteMacroProvider.getAllMacros();
            
            // Verify that macros from all packages are included
            assertContainsMacro(allMacros, "link", "nette/application");
            assertContainsMacro(allMacros, "form", "nette/forms");
            assertContainsMacro(allMacros, "css", "nette/assets");
            assertContainsMacro(allMacros, "query", "nette/database");
            
            // Disable nette/application
            settings.setEnableNetteApplication(false);
            
            // Get all macros again
            allMacros = NetteMacroProvider.getAllMacros();
            
            // Verify that macros from nette/application are not included
            assertNotContainsMacro(allMacros, "link", "nette/application");
            assertContainsMacro(allMacros, "form", "nette/forms");
            assertContainsMacro(allMacros, "css", "nette/assets");
            
            // Disable nette/forms
            settings.setEnableNetteForms(false);
            
            // Get all macros again
            allMacros = NetteMacroProvider.getAllMacros();
            
            // Verify that macros from nette/application and nette/forms are not included
            assertNotContainsMacro(allMacros, "link", "nette/application");
            assertNotContainsMacro(allMacros, "form", "nette/forms");
            assertContainsMacro(allMacros, "css", "nette/assets");
            
            // Disable nette/assets
            settings.setEnableNetteAssets(false);
            
            // Get all macros again
            allMacros = NetteMacroProvider.getAllMacros();
            
            // Verify that macros from nette/database are still included
            assertNotContainsMacro(allMacros, "link", "nette/application");
            assertNotContainsMacro(allMacros, "form", "nette/forms");
            assertNotContainsMacro(allMacros, "css", "nette/assets");
            assertContainsMacro(allMacros, "query", "nette/database");
            
            // Disable nette/database
            settings.setEnableNetteDatabase(false);
            
            // Get all macros again
            allMacros = NetteMacroProvider.getAllMacros();
            
            // Verify that no macros are included
            assertTrue("No macros should be included when all packages are disabled", allMacros.isEmpty());
        } finally {
            // Restore original settings
            settings.setEnableNetteApplication(originalApplicationSetting);
            settings.setEnableNetteForms(originalFormsSetting);
            settings.setEnableNetteAssets(originalAssetsSetting);
            settings.setEnableNetteDatabase(originalDatabaseSetting);
        }
    }
    
    /**
     * Tests that the NetteMacroProvider returns the correct attributes based on enabled settings.
     */
    @Test
    public void testGetAllAttributes() {
        // Get settings
        LatteSettings settings = LatteSettings.getInstance();
        
        // Save original settings
        boolean originalApplicationSetting = settings.isEnableNetteApplication();
        boolean originalFormsSetting = settings.isEnableNetteForms();
        boolean originalDatabaseSetting = settings.isEnableNetteDatabase();
        
        try {
            // Enable all packages
            settings.setEnableNetteApplication(true);
            settings.setEnableNetteForms(true);
            settings.setEnableNetteDatabase(true);
            
            // Get all attributes
            Set<NetteMacro> allAttributes = NetteMacroProvider.getAllAttributes();
            
            // Verify that attributes from all packages are included
            assertContainsMacro(allAttributes, "n:href", "nette/application");
            assertContainsMacro(allAttributes, "n:name", "nette/forms");
            assertContainsMacro(allAttributes, "n:query", "nette/database");
            
            // Disable nette/application
            settings.setEnableNetteApplication(false);
            
            // Get all attributes again
            allAttributes = NetteMacroProvider.getAllAttributes();
            
            // Verify that attributes from nette/application are not included
            assertNotContainsMacro(allAttributes, "n:href", "nette/application");
            assertContainsMacro(allAttributes, "n:name", "nette/forms");
            
            // Disable nette/forms
            settings.setEnableNetteForms(false);
            
            // Get all attributes again
            allAttributes = NetteMacroProvider.getAllAttributes();
            
            // Verify that attributes from nette/database are still included
            assertNotContainsMacro(allAttributes, "n:href", "nette/application");
            assertNotContainsMacro(allAttributes, "n:name", "nette/forms");
            assertContainsMacro(allAttributes, "n:query", "nette/database");
            
            // Disable nette/database
            settings.setEnableNetteDatabase(false);
            
            // Get all attributes again
            allAttributes = NetteMacroProvider.getAllAttributes();
            
            // Verify that no attributes are included
            assertTrue("No attributes should be included when all packages are disabled", allAttributes.isEmpty());
        } finally {
            // Restore original settings
            settings.setEnableNetteApplication(originalApplicationSetting);
            settings.setEnableNetteForms(originalFormsSetting);
            settings.setEnableNetteDatabase(originalDatabaseSetting);
        }
    }
    
    /**
     * Tests that the NetteMacroProvider returns the correct macro names based on enabled settings.
     */
    @Test
    public void testGetValidMacroNames() {
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
            
            // Get all macro names
            Set<String> macroNames = NetteMacroProvider.getValidMacroNames();
            
            // Verify that macro names from all packages are included
            assertTrue("Macro names should include 'link'", macroNames.contains("link"));
            assertTrue("Macro names should include 'form'", macroNames.contains("form"));
            assertTrue("Macro names should include 'css'", macroNames.contains("css"));
            assertTrue("Macro names should include 'query'", macroNames.contains("query"));
            
            // Disable application, forms, and assets packages but keep database enabled
            settings.setEnableNetteApplication(false);
            settings.setEnableNetteForms(false);
            settings.setEnableNetteAssets(false);
            
            // Get all macro names again
            macroNames = NetteMacroProvider.getValidMacroNames();
            
            // Verify that only database macro names are included
            assertFalse("Macro names should not include 'link'", macroNames.contains("link"));
            assertFalse("Macro names should not include 'form'", macroNames.contains("form"));
            assertFalse("Macro names should not include 'css'", macroNames.contains("css"));
            assertTrue("Macro names should include 'query'", macroNames.contains("query"));
            
            // Disable database package
            settings.setEnableNetteDatabase(false);
            
            // Get all macro names again
            macroNames = NetteMacroProvider.getValidMacroNames();
            
            // Verify that no macro names are included
            assertTrue("No macro names should be included when all packages are disabled", macroNames.isEmpty());
        } finally {
            // Restore original settings
            settings.setEnableNetteApplication(originalApplicationSetting);
            settings.setEnableNetteForms(originalFormsSetting);
            settings.setEnableNetteAssets(originalAssetsSetting);
            settings.setEnableNetteDatabase(originalDatabaseSetting);
        }
    }
    
    /**
     * Asserts that the given set of macros contains a macro with the given name and package.
     *
     * @param macros The set of macros
     * @param name The macro name
     * @param packageName The package name
     */
    private void assertContainsMacro(Set<NetteMacro> macros, String name, String packageName) {
        boolean found = false;
        for (NetteMacro macro : macros) {
            if (macro.getName().equals(name) && macro.getPackageName().equals(packageName)) {
                found = true;
                break;
            }
        }
        assertTrue("Macros should contain '" + name + "' from '" + packageName + "'", found);
    }
    
    /**
     * Asserts that the given set of macros does not contain a macro with the given name and package.
     *
     * @param macros The set of macros
     * @param name The macro name
     * @param packageName The package name
     */
    private void assertNotContainsMacro(Set<NetteMacro> macros, String name, String packageName) {
        boolean found = false;
        for (NetteMacro macro : macros) {
            if (macro.getName().equals(name) && macro.getPackageName().equals(packageName)) {
                found = true;
                break;
            }
        }
        assertFalse("Macros should not contain '" + name + "' from '" + packageName + "'", found);
    }
}
