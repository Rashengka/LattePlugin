package org.latte.plugin.test.completion;

import com.intellij.codeInsight.completion.CompletionType;
import org.junit.jupiter.api.Test;
import org.latte.plugin.test.LattePluginTestBase;
import org.latte.plugin.version.LatteVersion;
import org.latte.plugin.version.LatteVersionManager;

import java.util.List;

/**
 * Tests for Latte 4.0+ specific completion.
 */
public class Latte4xCompletionTest extends LattePluginTestBase {

    private LatteVersion originalVersion;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Save the original version
        originalVersion = LatteVersionManager.getCurrentVersion();
        
        // Create a simple Latte file for testing
        myFixture.configureByText("test.latte", "{<caret>}");
        
        System.out.println("[DEBUG_LOG] setUp - Current version: " + LatteVersionManager.getCurrentVersion());
    }

    @Override
    protected void tearDown() throws Exception {
        // Restore the original version
        LatteVersionManager.setCurrentVersion(originalVersion);
        
        System.out.println("[DEBUG_LOG] tearDown - Restored version: " + LatteVersionManager.getCurrentVersion());

        super.tearDown();
    }

    /**
     * Tests that completion includes Latte 4.0+ specific macros when the version is set to 4.0+.
     */
    @Test
    public void testLatte4xMacroCompletion() {
        // Set the version to 4.0+
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_4X);
        System.out.println("[DEBUG_LOG] testLatte4xMacroCompletion - Set version to: " + LatteVersionManager.getCurrentVersion());

        // Invoke completion
        myFixture.complete(CompletionType.BASIC);

        // Get lookup elements
        List<String> lookupElements = myFixture.getLookupElementStrings();

        // Debug log
        System.out.println("[DEBUG_LOG] Lookup elements for 4.0+: " + lookupElements);

        // Verify that Latte 4.0+ specific macros are included
        assertNotNull("Lookup elements should not be null", lookupElements);
        assertTrue("Completion should include 'typeCheck'", lookupElements.contains("typeCheck"));
        assertTrue("Completion should include 'strictTypes'", lookupElements.contains("strictTypes"));
        assertTrue("Completion should include 'asyncInclude'", lookupElements.contains("asyncInclude"));
        assertTrue("Completion should include 'await'", lookupElements.contains("await"));
        assertTrue("Completion should include 'inject'", lookupElements.contains("inject"));

        // Set the version to 3.0+
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_3X);
        System.out.println("[DEBUG_LOG] testLatte4xMacroCompletion - Set version to: " + LatteVersionManager.getCurrentVersion());

        // Create a new Latte file with a macro start to reset the completion context
        myFixture.configureByText("test3x.latte", "{<caret>}");

        // Invoke completion again
        myFixture.complete(CompletionType.BASIC);

        // Get lookup elements again
        lookupElements = myFixture.getLookupElementStrings();

        // Debug log
        System.out.println("[DEBUG_LOG] Lookup elements for 3.0+: " + lookupElements);

        // Verify that Latte 4.0+ specific macros are not included
        assertNotNull("Lookup elements should not be null", lookupElements);
        assertFalse("Completion should not include 'typeCheck'", lookupElements.contains("typeCheck"));
        assertFalse("Completion should not include 'strictTypes'", lookupElements.contains("strictTypes"));
        assertFalse("Completion should not include 'asyncInclude'", lookupElements.contains("asyncInclude"));
        assertFalse("Completion should not include 'await'", lookupElements.contains("await"));
        assertFalse("Completion should not include 'inject'", lookupElements.contains("inject"));

        // Verify that Latte 3.0+ specific macros are included
        assertTrue("Completion should include 'varType'", lookupElements.contains("varType"));
        assertTrue("Completion should include 'templateType'", lookupElements.contains("templateType"));
        assertTrue("Completion should include 'php'", lookupElements.contains("php"));
        assertTrue("Completion should include 'do'", lookupElements.contains("do"));
        assertTrue("Completion should include 'parameters'", lookupElements.contains("parameters"));
    }

    /**
     * Tests that completion includes both Latte 4.0+ and 3.0+ macros when the version is set to 4.0+.
     */
    @Test
    public void testLatte4xIncludesLatte3xMacros() {
        // Set the version to 4.0+
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_4X);
        System.out.println("[DEBUG_LOG] testLatte4xIncludesLatte3xMacros - Set version to: " + LatteVersionManager.getCurrentVersion());

        // Create a new Latte file with a macro start to reset the completion context
        myFixture.configureByText("test_includes.latte", "{<caret>}");

        // Invoke completion
        myFixture.complete(CompletionType.BASIC);

        // Get lookup elements
        List<String> lookupElements = myFixture.getLookupElementStrings();

        // Debug log
        System.out.println("[DEBUG_LOG] Lookup elements for 4.0+ (including 3.0+): " + lookupElements);

        // Verify that Latte 4.0+ specific macros are included
        assertNotNull("Lookup elements should not be null", lookupElements);
        assertTrue("Completion should include 'typeCheck'", lookupElements.contains("typeCheck"));
        assertTrue("Completion should include 'strictTypes'", lookupElements.contains("strictTypes"));

        // Verify that Latte 3.0+ macros are also included
        assertTrue("Completion should include 'varType'", lookupElements.contains("varType"));
        assertTrue("Completion should include 'templateType'", lookupElements.contains("templateType"));
        assertTrue("Completion should include 'php'", lookupElements.contains("php"));
        assertTrue("Completion should include 'do'", lookupElements.contains("do"));
        assertTrue("Completion should include 'parameters'", lookupElements.contains("parameters"));
    }

    /**
     * Tests that completion does not include Latte 2.x specific macros when the version is set to 4.0+.
     */
    @Test
    public void testLatte4xExcludesLatte2xMacros() {
        // Set the version to 4.0+
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_4X);
        System.out.println("[DEBUG_LOG] testLatte4xExcludesLatte2xMacros - Set version to: " + LatteVersionManager.getCurrentVersion());

        // Create a new Latte file with a macro start to reset the completion context
        myFixture.configureByText("test_excludes.latte", "{<caret>}");

        // Invoke completion
        myFixture.complete(CompletionType.BASIC);

        // Get lookup elements
        List<String> lookupElements = myFixture.getLookupElementStrings();

        // Debug log
        System.out.println("[DEBUG_LOG] Lookup elements for 4.0+ (excluding 2.x): " + lookupElements);

        // Verify that Latte 2.x specific macros are not included
        assertNotNull("Lookup elements should not be null", lookupElements);
        assertFalse("Completion should not include 'syntax'", lookupElements.contains("syntax"));
        assertFalse("Completion should not include 'use'", lookupElements.contains("use"));
        assertFalse("Completion should not include 'l'", lookupElements.contains("l"));
        assertFalse("Completion should not include 'r'", lookupElements.contains("r"));
    }

    /**
     * Test version specific macro completion in different contexts.
     */
    @Test
    public void testVersionSpecificMacroCompletion() {
        // Test 2.x version
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_2X);
        System.out.println("[DEBUG_LOG] testVersionSpecificMacroCompletion - Set version to: " + LatteVersionManager.getCurrentVersion());
        
        // Create a new Latte file with a macro start to reset the completion context
        myFixture.configureByText("test2x_specific.latte", "{<caret>}");
        
        myFixture.complete(CompletionType.BASIC);
        List<String> lookupElements = myFixture.getLookupElementStrings();
        
        System.out.println("[DEBUG_LOG] Lookup elements for 2.x: " + lookupElements);
        
        assertNotNull("Lookup elements should not be null", lookupElements);
        assertTrue("Completion should include 'syntax'", lookupElements.contains("syntax"));
        assertTrue("Completion should include 'use'", lookupElements.contains("use"));
        assertFalse("Completion should not include 'varType'", lookupElements.contains("varType"));
        assertFalse("Completion should not include 'typeCheck'", lookupElements.contains("typeCheck"));
        
        // Test 3.x version
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_3X);
        System.out.println("[DEBUG_LOG] testVersionSpecificMacroCompletion - Set version to: " + LatteVersionManager.getCurrentVersion());
        
        // Create a new Latte file with a macro start to reset the completion context
        myFixture.configureByText("test3x_specific.latte", "{<caret>}");
        
        myFixture.complete(CompletionType.BASIC);
        lookupElements = myFixture.getLookupElementStrings();
        
        System.out.println("[DEBUG_LOG] Lookup elements for 3.x: " + lookupElements);
        
        assertNotNull("Lookup elements should not be null", lookupElements);
        assertTrue("Completion should include 'varType'", lookupElements.contains("varType"));
        assertTrue("Completion should include 'templateType'", lookupElements.contains("templateType"));
        assertFalse("Completion should not include 'syntax'", lookupElements.contains("syntax"));
        assertFalse("Completion should not include 'typeCheck'", lookupElements.contains("typeCheck"));
        
        // Test 4.x version
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_4X);
        System.out.println("[DEBUG_LOG] testVersionSpecificMacroCompletion - Set version to: " + LatteVersionManager.getCurrentVersion());
        
        // Create a new Latte file with a macro start to reset the completion context
        myFixture.configureByText("test4x_specific.latte", "{<caret>}");
        
        myFixture.complete(CompletionType.BASIC);
        lookupElements = myFixture.getLookupElementStrings();
        
        System.out.println("[DEBUG_LOG] Lookup elements for 4.x: " + lookupElements);
        
        assertNotNull("Lookup elements should not be null", lookupElements);
        assertTrue("Completion should include 'typeCheck'", lookupElements.contains("typeCheck"));
        assertTrue("Completion should include 'strictTypes'", lookupElements.contains("strictTypes"));
        assertTrue("Completion should include 'varType'", lookupElements.contains("varType")); // 4.x includes 3.x
        assertFalse("Completion should not include 'syntax'", lookupElements.contains("syntax"));
    }
}
