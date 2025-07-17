package org.latte.plugin.test.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.latte.plugin.settings.LatteSettings;

import java.util.List;

/**
 * Tests for Nette Security variable completion in Latte templates.
 */
public class NetteSecurityVariableCompletionTest extends BasePlatformTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // Enable Nette Security package for testing
        LatteSettings settings = LatteSettings.getInstance();
        settings.setEnableNetteSecurity(true);
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/resources/testData/core";
    }

    /**
     * Tests that Nette Security variables are suggested.
     */
    public void testNetteSecurityVariables() {
        myFixture.configureByText("test.latte", "{$<caret>}");
        myFixture.complete(CompletionType.BASIC);
        
        List<String> lookupElements = myFixture.getLookupElementStrings();
        assertNotNull("No completion variants", lookupElements);
        
        // Check for Nette Security variables
        assertTrue("Missing user variable", lookupElements.contains("user"));
        assertTrue("Missing identity variable", lookupElements.contains("identity"));
        assertTrue("Missing roles variable", lookupElements.contains("roles"));
    }
    
    /**
     * Tests that version-specific Security variables are suggested.
     */
    public void testNetteSecurityVersionSpecificVariables() {
        // Set Security version to 3
        LatteSettings settings = LatteSettings.getInstance();
        settings.setSelectedNetteSecurityVersion("3");
        settings.setOverrideDetectedNetteSecurityVersion(true);
        
        myFixture.configureByText("test.latte", "{$<caret>}");
        myFixture.complete(CompletionType.BASIC);
        
        List<String> lookupElements = myFixture.getLookupElementStrings();
        assertNotNull("No completion variants", lookupElements);
        
        // Check for version 3 specific variables
        assertTrue("Missing authenticator variable", lookupElements.contains("authenticator"));
        assertTrue("Missing authorizator variable", lookupElements.contains("authorizator"));
        
        // Set Security version to 2
        settings.setSelectedNetteSecurityVersion("2");
        
        myFixture.configureByText("test.latte", "{$<caret>}");
        myFixture.complete(CompletionType.BASIC);
        
        lookupElements = myFixture.getLookupElementStrings();
        assertNotNull("No completion variants", lookupElements);
        
        // Version 2 has the same variable names but different types
        // We can't easily test the types in this test, so we'll just check that the variables are there
        assertTrue("Missing authenticator variable", lookupElements.contains("authenticator"));
        assertTrue("Missing authorizator variable", lookupElements.contains("authorizator"));
    }
    
    /**
     * Tests that variables are not suggested when Security package is disabled.
     */
    public void testDisabledSecurityPackage() {
        // Disable Security package
        LatteSettings settings = LatteSettings.getInstance();
        settings.setEnableNetteSecurity(false);
        
        myFixture.configureByText("test.latte", "{$<caret>}");
        myFixture.complete(CompletionType.BASIC);
        
        List<String> lookupElements = myFixture.getLookupElementStrings();
        
        // If there are no lookup elements, that's fine
        if (lookupElements == null) {
            return;
        }
        
        // If there are lookup elements, make sure they don't include Security variables
        // Note: user might still be present from nette/application, so we don't check for it
        assertFalse("identity variable should not be suggested", lookupElements.contains("identity"));
        assertFalse("roles variable should not be suggested", lookupElements.contains("roles"));
        assertFalse("authenticator variable should not be suggested", lookupElements.contains("authenticator"));
        assertFalse("authorizator variable should not be suggested", lookupElements.contains("authorizator"));
    }
}