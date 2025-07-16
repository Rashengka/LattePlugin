package org.latte.plugin.test.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.latte.plugin.settings.LatteSettings;

import java.util.List;

/**
 * Tests for Nette variable completion in Latte templates.
 */
public class NetteVariableCompletionTest extends BasePlatformTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // Enable all Nette packages for testing
        LatteSettings settings = LatteSettings.getInstance();
        settings.setEnableNetteApplication(true);
        settings.setEnableNetteForms(true);
        settings.setEnableNetteAssets(true);
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/resources/testData/core";
    }

    /**
     * Tests that Nette Application variables are suggested.
     */
    public void testNetteApplicationVariables() {
        myFixture.configureByText("test.latte", "{$<caret>}");
        myFixture.complete(CompletionType.BASIC);
        
        List<String> lookupElements = myFixture.getLookupElementStrings();
        assertNotNull("No completion variants", lookupElements);
        
        // Check for Nette Application variables
        assertTrue("Missing basePath variable", lookupElements.contains("basePath"));
        assertTrue("Missing baseUrl variable", lookupElements.contains("baseUrl"));
        assertTrue("Missing user variable", lookupElements.contains("user"));
        assertTrue("Missing presenter variable", lookupElements.contains("presenter"));
        assertTrue("Missing control variable", lookupElements.contains("control"));
        assertTrue("Missing flashes variable", lookupElements.contains("flashes"));
    }

    /**
     * Tests that Nette Forms variables are suggested.
     */
    public void testNetteFormsVariables() {
        myFixture.configureByText("test.latte", "{form testForm}\n{$<caret>}\n{/form}");
        myFixture.complete(CompletionType.BASIC);
        
        List<String> lookupElements = myFixture.getLookupElementStrings();
        assertNotNull("No completion variants", lookupElements);
        
        // Check for Nette Forms variables
        assertTrue("Missing form variable", lookupElements.contains("form"));
    }

    /**
     * Tests that variables are not suggested when packages are disabled.
     */
    public void testDisabledPackages() {
        // Disable all packages
        LatteSettings settings = LatteSettings.getInstance();
        settings.setEnableNetteApplication(false);
        settings.setEnableNetteForms(false);
        settings.setEnableNetteAssets(false);
        
        myFixture.configureByText("test.latte", "{$<caret>}");
        myFixture.complete(CompletionType.BASIC);
        
        List<String> lookupElements = myFixture.getLookupElementStrings();
        
        // If there are no lookup elements, that's fine
        if (lookupElements == null) {
            return;
        }
        
        // If there are lookup elements, make sure they don't include Nette variables
        assertFalse("basePath variable should not be suggested", lookupElements.contains("basePath"));
        assertFalse("baseUrl variable should not be suggested", lookupElements.contains("baseUrl"));
        assertFalse("user variable should not be suggested", lookupElements.contains("user"));
        assertFalse("presenter variable should not be suggested", lookupElements.contains("presenter"));
        assertFalse("control variable should not be suggested", lookupElements.contains("control"));
        assertFalse("flashes variable should not be suggested", lookupElements.contains("flashes"));
        assertFalse("form variable should not be suggested", lookupElements.contains("form"));
    }
}