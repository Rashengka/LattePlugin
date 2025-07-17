package org.latte.plugin.test.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.latte.plugin.version.LatteVersion;
import org.latte.plugin.version.LatteVersionManager;

import java.util.List;

/**
 * Tests for Latte 4.0+ specific completion.
 */
public class Latte4xCompletionTest extends BasePlatformTestCase {

    private LatteVersion originalVersion;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // Save the original version
        originalVersion = LatteVersionManager.getCurrentVersion();
        
        // Create a simple Latte file for testing
        myFixture.configureByText("test.latte", "{<caret>}");
    }
    
    @Override
    protected void tearDown() throws Exception {
        // Restore the original version
        LatteVersionManager.setCurrentVersion(originalVersion);
        
        super.tearDown();
    }
    
    /**
     * Tests that completion includes Latte 4.0+ specific macros when the version is set to 4.0+.
     */
    public void testLatte4xMacroCompletion() {
        // Set the version to 4.0+
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_4X);
        
        // Invoke completion
        myFixture.complete(CompletionType.BASIC);
        
        // Get lookup elements
        List<String> lookupElements = myFixture.getLookupElementStrings();
        
        // Verify that Latte 4.0+ specific macros are included
        assertNotNull("Lookup elements should not be null", lookupElements);
        assertTrue("Completion should include 'typeCheck'", lookupElements.contains("typeCheck"));
        assertTrue("Completion should include 'strictTypes'", lookupElements.contains("strictTypes"));
        assertTrue("Completion should include 'asyncInclude'", lookupElements.contains("asyncInclude"));
        assertTrue("Completion should include 'await'", lookupElements.contains("await"));
        assertTrue("Completion should include 'inject'", lookupElements.contains("inject"));
        
        // Set the version to 3.0+
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_3X);
        
        // Invoke completion again
        myFixture.complete(CompletionType.BASIC);
        
        // Get lookup elements again
        lookupElements = myFixture.getLookupElementStrings();
        
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
    public void testLatte4xIncludesLatte3xMacros() {
        // Set the version to 4.0+
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_4X);
        
        // Invoke completion
        myFixture.complete(CompletionType.BASIC);
        
        // Get lookup elements
        List<String> lookupElements = myFixture.getLookupElementStrings();
        
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
    public void testLatte4xExcludesLatte2xMacros() {
        // Set the version to 4.0+
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_4X);
        
        // Invoke completion
        myFixture.complete(CompletionType.BASIC);
        
        // Get lookup elements
        List<String> lookupElements = myFixture.getLookupElementStrings();
        
        // Verify that Latte 2.x specific macros are not included
        assertNotNull("Lookup elements should not be null", lookupElements);
        assertFalse("Completion should not include 'syntax'", lookupElements.contains("syntax"));
        assertFalse("Completion should not include 'use'", lookupElements.contains("use"));
        assertFalse("Completion should not include 'l'", lookupElements.contains("l"));
        assertFalse("Completion should not include 'r'", lookupElements.contains("r"));
    }
}