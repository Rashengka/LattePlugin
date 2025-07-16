package org.latte.plugin.test.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.latte.plugin.settings.LatteSettings;

import java.util.List;

/**
 * Tests for completion of Nette package macros and attributes.
 */
public class NetteCompletionTest extends BasePlatformTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // Create a simple Latte file for testing
        myFixture.configureByText("test.latte", "{<caret>}");
    }
    
    /**
     * Tests that completion includes macros from nette/application when enabled.
     */
    public void testApplicationMacroCompletion() {
        // Get settings
        LatteSettings settings = LatteSettings.getInstance();
        
        // Save original settings
        boolean originalApplicationSetting = settings.isEnableNetteApplication();
        
        try {
            // Enable nette/application
            settings.setEnableNetteApplication(true);
            
            // Invoke completion
            myFixture.complete(CompletionType.BASIC);
            
            // Get lookup elements
            List<String> lookupElements = myFixture.getLookupElementStrings();
            
            // Verify that macros from nette/application are included
            assertNotNull("Lookup elements should not be null", lookupElements);
            assertTrue("Completion should include 'link'", lookupElements.contains("link"));
            assertTrue("Completion should include 'plink'", lookupElements.contains("plink"));
            assertTrue("Completion should include 'control'", lookupElements.contains("control"));
            
            // Disable nette/application
            settings.setEnableNetteApplication(false);
            
            // Invoke completion again
            myFixture.complete(CompletionType.BASIC);
            
            // Get lookup elements again
            lookupElements = myFixture.getLookupElementStrings();
            
            // Verify that macros from nette/application are not included
            assertNotNull("Lookup elements should not be null", lookupElements);
            assertFalse("Completion should not include 'link'", lookupElements.contains("link"));
            assertFalse("Completion should not include 'plink'", lookupElements.contains("plink"));
            assertFalse("Completion should not include 'control'", lookupElements.contains("control"));
        } finally {
            // Restore original settings
            settings.setEnableNetteApplication(originalApplicationSetting);
        }
    }
    
    /**
     * Tests that completion includes macros from nette/forms when enabled.
     */
    public void testFormsMacroCompletion() {
        // Get settings
        LatteSettings settings = LatteSettings.getInstance();
        
        // Save original settings
        boolean originalFormsSetting = settings.isEnableNetteForms();
        
        try {
            // Enable nette/forms
            settings.setEnableNetteForms(true);
            
            // Invoke completion
            myFixture.complete(CompletionType.BASIC);
            
            // Get lookup elements
            List<String> lookupElements = myFixture.getLookupElementStrings();
            
            // Verify that macros from nette/forms are included
            assertNotNull("Lookup elements should not be null", lookupElements);
            assertTrue("Completion should include 'form'", lookupElements.contains("form"));
            assertTrue("Completion should include 'input'", lookupElements.contains("input"));
            assertTrue("Completion should include 'label'", lookupElements.contains("label"));
            
            // Disable nette/forms
            settings.setEnableNetteForms(false);
            
            // Invoke completion again
            myFixture.complete(CompletionType.BASIC);
            
            // Get lookup elements again
            lookupElements = myFixture.getLookupElementStrings();
            
            // Verify that macros from nette/forms are not included
            assertNotNull("Lookup elements should not be null", lookupElements);
            assertFalse("Completion should not include 'form'", lookupElements.contains("form"));
            assertFalse("Completion should not include 'input'", lookupElements.contains("input"));
            assertFalse("Completion should not include 'label'", lookupElements.contains("label"));
        } finally {
            // Restore original settings
            settings.setEnableNetteForms(originalFormsSetting);
        }
    }
    
    /**
     * Tests that completion includes macros from nette/assets when enabled.
     */
    public void testAssetsMacroCompletion() {
        // Get settings
        LatteSettings settings = LatteSettings.getInstance();
        
        // Save original settings
        boolean originalAssetsSetting = settings.isEnableNetteAssets();
        
        try {
            // Enable nette/assets
            settings.setEnableNetteAssets(true);
            
            // Invoke completion
            myFixture.complete(CompletionType.BASIC);
            
            // Get lookup elements
            List<String> lookupElements = myFixture.getLookupElementStrings();
            
            // Verify that macros from nette/assets are included
            assertNotNull("Lookup elements should not be null", lookupElements);
            assertTrue("Completion should include 'css'", lookupElements.contains("css"));
            assertTrue("Completion should include 'js'", lookupElements.contains("js"));
            assertTrue("Completion should include 'asset'", lookupElements.contains("asset"));
            
            // Disable nette/assets
            settings.setEnableNetteAssets(false);
            
            // Invoke completion again
            myFixture.complete(CompletionType.BASIC);
            
            // Get lookup elements again
            lookupElements = myFixture.getLookupElementStrings();
            
            // Verify that macros from nette/assets are not included
            assertNotNull("Lookup elements should not be null", lookupElements);
            assertFalse("Completion should not include 'css'", lookupElements.contains("css"));
            assertFalse("Completion should not include 'js'", lookupElements.contains("js"));
            assertFalse("Completion should not include 'asset'", lookupElements.contains("asset"));
        } finally {
            // Restore original settings
            settings.setEnableNetteAssets(originalAssetsSetting);
        }
    }
    
    /**
     * Tests that completion includes attributes from nette/application when enabled.
     */
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
            
            // Invoke completion
            myFixture.complete(CompletionType.BASIC);
            
            // Get lookup elements
            List<String> lookupElements = myFixture.getLookupElementStrings();
            
            // Verify that attributes from nette/application are included
            assertNotNull("Lookup elements should not be null", lookupElements);
            assertTrue("Completion should include 'n:href'", lookupElements.contains("n:href"));
            assertTrue("Completion should include 'n:snippet'", lookupElements.contains("n:snippet"));
            
            // Disable nette/application
            settings.setEnableNetteApplication(false);
            
            // Invoke completion again
            myFixture.complete(CompletionType.BASIC);
            
            // Get lookup elements again
            lookupElements = myFixture.getLookupElementStrings();
            
            // Verify that attributes from nette/application are not included
            assertNotNull("Lookup elements should not be null", lookupElements);
            assertFalse("Completion should not include 'n:href'", lookupElements.contains("n:href"));
            assertFalse("Completion should not include 'n:snippet'", lookupElements.contains("n:snippet"));
        } finally {
            // Restore original settings
            settings.setEnableNetteApplication(originalApplicationSetting);
        }
    }
    
    /**
     * Tests that completion includes attributes from nette/forms when enabled.
     */
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
            
            // Invoke completion
            myFixture.complete(CompletionType.BASIC);
            
            // Get lookup elements
            List<String> lookupElements = myFixture.getLookupElementStrings();
            
            // Verify that attributes from nette/forms are included
            assertNotNull("Lookup elements should not be null", lookupElements);
            assertTrue("Completion should include 'n:name'", lookupElements.contains("n:name"));
            assertTrue("Completion should include 'n:validation'", lookupElements.contains("n:validation"));
            
            // Disable nette/forms
            settings.setEnableNetteForms(false);
            
            // Invoke completion again
            myFixture.complete(CompletionType.BASIC);
            
            // Get lookup elements again
            lookupElements = myFixture.getLookupElementStrings();
            
            // Verify that attributes from nette/forms are not included
            assertNotNull("Lookup elements should not be null", lookupElements);
            assertFalse("Completion should not include 'n:name'", lookupElements.contains("n:name"));
            assertFalse("Completion should not include 'n:validation'", lookupElements.contains("n:validation"));
        } finally {
            // Restore original settings
            settings.setEnableNetteForms(originalFormsSetting);
        }
    }
}