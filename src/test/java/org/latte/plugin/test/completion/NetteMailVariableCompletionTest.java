package org.latte.plugin.test.completion;

import com.intellij.codeInsight.completion.CompletionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.latte.plugin.test.LattePluginTestBase;
import org.latte.plugin.settings.LatteSettings;

import java.util.List;

/**
 * Tests for Nette Mail variable completion in Latte templates.
 */
public class NetteMailVariableCompletionTest extends LattePluginTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // Enable Nette Mail package for testing
        LatteSettings settings = LatteSettings.getInstance();
        settings.setEnableNetteMail(true);
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/resources/testData/core";
    }

    /**
     * Tests that Nette Mail variables are suggested.
     */
    @Test
    public void testNetteMailVariables() {
        myFixture.configureByText("test.latte", "{$<caret>}");
        myFixture.complete(CompletionType.BASIC);
        
        List<String> lookupElements = myFixture.getLookupElementStrings();
        assertNotNull("No completion variants", lookupElements);
        
        // Check for Nette Mail variables
        assertTrue("Missing mail variable", lookupElements.contains("mail"));
        assertTrue("Missing message variable", lookupElements.contains("message"));
        assertTrue("Missing attachment variable", lookupElements.contains("attachment"));
        assertTrue("Missing sender variable", lookupElements.contains("sender"));
    }
    
    /**
     * Tests that version-specific Mail variables are suggested.
     */
    @Test
    public void testNetteMailVersionSpecificVariables() {
        // Set Mail version to 3
        LatteSettings settings = LatteSettings.getInstance();
        settings.setSelectedNetteMailVersion("3");
        settings.setOverrideDetectedNetteMailVersion(true);
        
        myFixture.configureByText("test.latte", "{$<caret>}");
        myFixture.complete(CompletionType.BASIC);
        
        List<String> lookupElements = myFixture.getLookupElementStrings();
        assertNotNull("No completion variants", lookupElements);
        
        // Check that basic variables are present in version 3
        assertTrue("Missing mail variable", lookupElements.contains("mail"));
        assertTrue("Missing message variable", lookupElements.contains("message"));
        
        // Set Mail version to 2
        settings.setSelectedNetteMailVersion("2");
        
        myFixture.configureByText("test.latte", "{$<caret>}");
        myFixture.complete(CompletionType.BASIC);
        
        lookupElements = myFixture.getLookupElementStrings();
        assertNotNull("No completion variants", lookupElements);
        
        // Check that basic variables are present in version 2
        assertTrue("Missing mail variable", lookupElements.contains("mail"));
        assertTrue("Missing message variable", lookupElements.contains("message"));
    }
    
    /**
     * Tests that variables are not suggested when Mail package is disabled.
     */
    @Test
    public void testDisabledMailPackage() {
        // Disable Mail package
        LatteSettings settings = LatteSettings.getInstance();
        settings.setEnableNetteMail(false);
        
        myFixture.configureByText("test.latte", "{$<caret>}");
        myFixture.complete(CompletionType.BASIC);
        
        List<String> lookupElements = myFixture.getLookupElementStrings();
        
        // If there are no lookup elements, that's fine
        if (lookupElements == null) {
            return;
        }
        
        // If there are lookup elements, make sure they don't include Mail variables
        assertFalse("mail variable should not be suggested", lookupElements.contains("mail"));
        assertFalse("message variable should not be suggested", lookupElements.contains("message"));
        assertFalse("attachment variable should not be suggested", lookupElements.contains("attachment"));
        assertFalse("sender variable should not be suggested", lookupElements.contains("sender"));
    }
    
    /**
     * Tests that Mail variables are suggested in mail templates.
     */
    @Test
    public void testNetteMailTemplateVariables() {
        myFixture.configureByText("test.latte", "{mail}\n{$<caret>}\n{/mail}");
        myFixture.complete(CompletionType.BASIC);
        
        List<String> lookupElements = myFixture.getLookupElementStrings();
        assertNotNull("No completion variants", lookupElements);
        
        // Check for Nette Mail variables in mail template context
        assertTrue("Missing mail variable", lookupElements.contains("mail"));
        assertTrue("Missing message variable", lookupElements.contains("message"));
        assertTrue("Missing attachment variable", lookupElements.contains("attachment"));
        assertTrue("Missing sender variable", lookupElements.contains("sender"));
    }
}
