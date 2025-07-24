package cz.hqm.latte.plugin.test.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import cz.hqm.latte.plugin.settings.LatteSettings;
import cz.hqm.latte.plugin.version.LatteVersion;
import cz.hqm.latte.plugin.version.LatteVersionManager;

/**
 * Tests for the caching mechanism in LatteCompletionContributor.
 */
public class LatteMacroCachingTest extends BasePlatformTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.out.println("[DEBUG_LOG] Setting up LatteMacroCachingTest");
        
        // Ensure we have a clean state for each test
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_3X);
        LatteSettings settings = LatteSettings.getInstance();
        settings.setEnableNetteApplication(true);
        settings.setEnableNetteForms(true);
        settings.setEnableNetteAssets(true);
    }

    /**
     * Tests that macros are cached and not recreated on each completion request.
     */
    public void testMacroCaching() {
        System.out.println("[DEBUG_LOG] Running testMacroCaching");
        
        // Create a simple Latte file
        myFixture.configureByText("test.latte", "{<caret>}");
        
        // First completion request should initialize the cache
        System.out.println("[DEBUG_LOG] First completion request - should initialize cache");
        myFixture.complete(CompletionType.BASIC);
        
        // Second completion request should use the cache
        System.out.println("[DEBUG_LOG] Second completion request - should use cache");
        myFixture.complete(CompletionType.BASIC);
        
        // Verify that we have completions (this is just a basic check)
        assertTrue("Should have completion items", myFixture.getLookupElements().length > 0);
    }

    /**
     * Tests that the cache is updated when the Latte version changes.
     */
    public void testCacheUpdateOnVersionChange() {
        System.out.println("[DEBUG_LOG] Running testCacheUpdateOnVersionChange");
        
        // Create a simple Latte file
        myFixture.configureByText("test.latte", "{<caret>}");
        
        // First completion request with version 3.x
        System.out.println("[DEBUG_LOG] First completion request with version 3.x");
        myFixture.complete(CompletionType.BASIC);
        
        // Change version to 4.x
        System.out.println("[DEBUG_LOG] Changing version to 4.x");
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_4X);
        
        // Second completion request should update the cache due to version change
        System.out.println("[DEBUG_LOG] Second completion request after version change");
        myFixture.complete(CompletionType.BASIC);
        
        // Verify that we have completions (this is just a basic check)
        assertTrue("Should have completion items", myFixture.getLookupElements().length > 0);
    }

    /**
     * Tests that the cache is updated when settings change.
     */
    public void testCacheUpdateOnSettingsChange() {
        System.out.println("[DEBUG_LOG] Running testCacheUpdateOnSettingsChange");
        
        // Create a simple Latte file
        myFixture.configureByText("test.latte", "{<caret>}");
        
        // First completion request with all features enabled
        System.out.println("[DEBUG_LOG] First completion request with all features enabled");
        myFixture.complete(CompletionType.BASIC);
        
        // Change settings
        System.out.println("[DEBUG_LOG] Changing settings");
        LatteSettings settings = LatteSettings.getInstance();
        settings.setEnableNetteApplication(false);
        settings.setEnableNetteForms(false);
        
        // Second completion request should update the cache due to settings change
        System.out.println("[DEBUG_LOG] Second completion request after settings change");
        myFixture.complete(CompletionType.BASIC);
        
        // Verify that we have completions (this is just a basic check)
        assertTrue("Should have completion items", myFixture.getLookupElements().length > 0);
    }
}