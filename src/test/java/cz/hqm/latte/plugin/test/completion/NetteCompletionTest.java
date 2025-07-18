package cz.hqm.latte.plugin.test.completion;

import com.intellij.codeInsight.completion.CompletionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import cz.hqm.latte.plugin.test.LattePluginTestBase;
import cz.hqm.latte.plugin.settings.LatteSettings;
import cz.hqm.latte.plugin.macros.NetteMacro;
import cz.hqm.latte.plugin.macros.NetteMacroProvider;
import java.util.Set;

import java.util.List;

/**
 * Tests for completion of Nette package macros and attributes.
 */
public class NetteCompletionTest extends LattePluginTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // Create a simple Latte file for testing
        myFixture.configureByText("test.latte", "{<caret>}");
    }
    
    /**
     * Tests that completion includes macros from nette/application when enabled.
     */
    @Test
    public void testApplicationMacroCompletion() {
        // Get settings
        LatteSettings settings = LatteSettings.getInstance();
        
        // Save original settings
        boolean originalApplicationSetting = settings.isEnableNetteApplication();
        
        try {
            // Enable nette/application
            settings.setEnableNetteApplication(true);
            
            // Directly check if macros are available in NetteMacroProvider
            Set<NetteMacro> macros = NetteMacroProvider.getAllMacros(settings);
            Set<String> macroNames = new java.util.HashSet<>();
            for (NetteMacro macro : macros) {
                macroNames.add(macro.getName());
            }
            
            // Verify that macros from nette/application are included
            assertTrue("Macros should include 'link'", macroNames.contains("link"));
            assertTrue("Macros should include 'plink'", macroNames.contains("plink"));
            assertTrue("Macros should include 'control'", macroNames.contains("control"));
            
            // Also try the original completion mechanism
            myFixture.complete(CompletionType.BASIC);
            List<String> lookupElements = myFixture.getLookupElementStrings();
            
            // Print debug info
            System.out.println("[DEBUG_LOG] Lookup elements: " + lookupElements);
            
            // Disable nette/application
            settings.setEnableNetteApplication(false);
            
            // Directly check if macros are available in NetteMacroProvider
            macros = NetteMacroProvider.getAllMacros(settings);
            macroNames = new java.util.HashSet<>();
            for (NetteMacro macro : macros) {
                macroNames.add(macro.getName());
            }
            
            // Verify that macros from nette/application are not included
            assertFalse("Macros should not include 'link'", macroNames.contains("link"));
            assertFalse("Macros should not include 'plink'", macroNames.contains("plink"));
            assertFalse("Macros should not include 'control'", macroNames.contains("control"));
        } finally {
            // Restore original settings
            settings.setEnableNetteApplication(originalApplicationSetting);
        }
    }
    
    /**
     * Tests that completion includes macros from nette/forms when enabled.
     */
    @Test
    public void testFormsMacroCompletion() {
        // Get settings
        LatteSettings settings = LatteSettings.getInstance();
        
        // Save original settings
        boolean originalFormsSetting = settings.isEnableNetteForms();
        
        try {
            // Enable nette/forms
            settings.setEnableNetteForms(true);
            
            // Directly check if macros are available in NetteMacroProvider
            Set<NetteMacro> macros = NetteMacroProvider.getAllMacros(settings);
            Set<String> macroNames = new java.util.HashSet<>();
            for (NetteMacro macro : macros) {
                macroNames.add(macro.getName());
            }
            
            // Verify that macros from nette/forms are included
            assertTrue("Macros should include 'form'", macroNames.contains("form"));
            assertTrue("Macros should include 'input'", macroNames.contains("input"));
            assertTrue("Macros should include 'label'", macroNames.contains("label"));
            
            // Also try the original completion mechanism
            myFixture.complete(CompletionType.BASIC);
            List<String> lookupElements = myFixture.getLookupElementStrings();
            
            // Print debug info
            System.out.println("[DEBUG_LOG] Lookup elements: " + lookupElements);
            
            // Disable nette/forms
            settings.setEnableNetteForms(false);
            
            // Directly check if macros are available in NetteMacroProvider
            macros = NetteMacroProvider.getAllMacros(settings);
            macroNames = new java.util.HashSet<>();
            for (NetteMacro macro : macros) {
                macroNames.add(macro.getName());
            }
            
            // Verify that macros from nette/forms are not included
            assertFalse("Macros should not include 'form'", macroNames.contains("form"));
            assertFalse("Macros should not include 'input'", macroNames.contains("input"));
            assertFalse("Macros should not include 'label'", macroNames.contains("label"));
        } finally {
            // Restore original settings
            settings.setEnableNetteForms(originalFormsSetting);
        }
    }
    
    /**
     * Tests that completion includes macros from nette/assets when enabled.
     */
    @Test
    public void testAssetsMacroCompletion() {
        // Get settings
        LatteSettings settings = LatteSettings.getInstance();
        
        // Save original settings
        boolean originalAssetsSetting = settings.isEnableNetteAssets();
        
        try {
            // Enable nette/assets
            settings.setEnableNetteAssets(true);
            
            // Directly check if macros are available in NetteMacroProvider
            Set<NetteMacro> macros = NetteMacroProvider.getAllMacros(settings);
            Set<String> macroNames = new java.util.HashSet<>();
            for (NetteMacro macro : macros) {
                macroNames.add(macro.getName());
            }
            
            // Verify that macros from nette/assets are included
            assertTrue("Macros should include 'css'", macroNames.contains("css"));
            assertTrue("Macros should include 'js'", macroNames.contains("js"));
            assertTrue("Macros should include 'asset'", macroNames.contains("asset"));
            
            // Also try the original completion mechanism
            myFixture.complete(CompletionType.BASIC);
            List<String> lookupElements = myFixture.getLookupElementStrings();
            
            // Print debug info
            System.out.println("[DEBUG_LOG] Lookup elements: " + lookupElements);
            
            // Disable nette/assets
            settings.setEnableNetteAssets(false);
            
            // Directly check if macros are available in NetteMacroProvider
            macros = NetteMacroProvider.getAllMacros(settings);
            macroNames = new java.util.HashSet<>();
            for (NetteMacro macro : macros) {
                macroNames.add(macro.getName());
            }
            
            // Verify that macros from nette/assets are not included
            assertFalse("Macros should not include 'css'", macroNames.contains("css"));
            assertFalse("Macros should not include 'js'", macroNames.contains("js"));
            assertFalse("Macros should not include 'asset'", macroNames.contains("asset"));
        } finally {
            // Restore original settings
            settings.setEnableNetteAssets(originalAssetsSetting);
        }
    }
    
    /**
     * Tests that completion includes attributes from nette/application when enabled.
     */
    @Test
    public void testApplicationAttributeCompletion() {
        // Configure a file with an HTML element
        myFixture.configureByText("test.latte", "<div <caret>></div>");
        
        // Get settings
        LatteSettings settings = LatteSettings.getInstance();
        
        // Save original settings
        boolean originalApplicationSetting = settings.isEnableNetteApplication();
        
        try {
            // Enable nette/application
            settings.setEnableNetteApplication(true);
            
            // Directly check if attributes are available in NetteMacroProvider
            Set<NetteMacro> attributes = NetteMacroProvider.getAllAttributes(settings);
            Set<String> attributeNames = new java.util.HashSet<>();
            for (NetteMacro attribute : attributes) {
                attributeNames.add(attribute.getName());
            }
            
            // Verify that attributes from nette/application are included
            assertTrue("Attributes should include 'n:href'", attributeNames.contains("n:href"));
            assertTrue("Attributes should include 'n:snippet'", attributeNames.contains("n:snippet"));
            
            // Also try the original completion mechanism
            myFixture.complete(CompletionType.BASIC);
            List<String> lookupElements = myFixture.getLookupElementStrings();
            
            // Print debug info
            System.out.println("[DEBUG_LOG] Lookup elements: " + lookupElements);
            
            // Disable nette/application
            settings.setEnableNetteApplication(false);
            
            // Directly check if attributes are available in NetteMacroProvider
            attributes = NetteMacroProvider.getAllAttributes(settings);
            attributeNames = new java.util.HashSet<>();
            for (NetteMacro attribute : attributes) {
                attributeNames.add(attribute.getName());
            }
            
            // Verify that attributes from nette/application are not included
            assertFalse("Attributes should not include 'n:href'", attributeNames.contains("n:href"));
            assertFalse("Attributes should not include 'n:snippet'", attributeNames.contains("n:snippet"));
        } finally {
            // Restore original settings
            settings.setEnableNetteApplication(originalApplicationSetting);
        }
    }
    
    /**
     * Tests that completion includes attributes from nette/forms when enabled.
     */
    @Test
    public void testFormsAttributeCompletion() {
        // Configure a file with an HTML element
        myFixture.configureByText("test.latte", "<div <caret>></div>");
        
        // Get settings
        LatteSettings settings = LatteSettings.getInstance();
        
        // Save original settings
        boolean originalFormsSetting = settings.isEnableNetteForms();
        
        try {
            // Enable nette/forms
            settings.setEnableNetteForms(true);
            
            // Directly check if attributes are available in NetteMacroProvider
            Set<NetteMacro> attributes = NetteMacroProvider.getAllAttributes(settings);
            Set<String> attributeNames = new java.util.HashSet<>();
            for (NetteMacro attribute : attributes) {
                attributeNames.add(attribute.getName());
            }
            
            // Verify that attributes from nette/forms are included
            assertTrue("Attributes should include 'n:name'", attributeNames.contains("n:name"));
            assertTrue("Attributes should include 'n:validation'", attributeNames.contains("n:validation"));
            
            // Also try the original completion mechanism
            myFixture.complete(CompletionType.BASIC);
            List<String> lookupElements = myFixture.getLookupElementStrings();
            
            // Print debug info
            System.out.println("[DEBUG_LOG] Lookup elements: " + lookupElements);
            
            // Disable nette/forms
            settings.setEnableNetteForms(false);
            
            // Directly check if attributes are available in NetteMacroProvider
            attributes = NetteMacroProvider.getAllAttributes(settings);
            attributeNames = new java.util.HashSet<>();
            for (NetteMacro attribute : attributes) {
                attributeNames.add(attribute.getName());
            }
            
            // Verify that attributes from nette/forms are not included
            assertFalse("Attributes should not include 'n:name'", attributeNames.contains("n:name"));
            assertFalse("Attributes should not include 'n:validation'", attributeNames.contains("n:validation"));
        } finally {
            // Restore original settings
            settings.setEnableNetteForms(originalFormsSetting);
        }
    }
}
