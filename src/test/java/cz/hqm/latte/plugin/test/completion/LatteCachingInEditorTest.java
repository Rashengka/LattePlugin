package cz.hqm.latte.plugin.test.completion;

import com.intellij.codeInsight.completion.CompletionType;
import cz.hqm.latte.plugin.completion.LatteCompletionContributor;
import cz.hqm.latte.plugin.completion.NetteDefaultVariablesProvider;
import cz.hqm.latte.plugin.filters.NetteFilterProvider;
import cz.hqm.latte.plugin.settings.LatteSettings;
import cz.hqm.latte.plugin.test.LattePluginTestBase;
import cz.hqm.latte.plugin.version.LatteVersion;
import cz.hqm.latte.plugin.version.LatteVersionManager;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Tests for verifying that caching works correctly during autocomplete in the editor.
 * 
 * This test class verifies that the caching mechanisms implemented in the Latte plugin
 * are working correctly. It focuses on three main caching areas:
 * 
 * 1. Macro caching in LatteCompletionContributor
 * 2. Variable caching in NetteDefaultVariablesProvider
 * 3. Filter caching in NetteFilterProvider
 * 
 * The tests use reflection to directly check the cache state and verify that cached
 * elements are not reloaded on subsequent requests. This approach was chosen because:
 * 
 * - It allows direct verification of the cache state
 * - It's more reliable than capturing debug logs
 * - It can verify that the same instance is reused (not just that values are equal)
 * 
 * These tests are important because they ensure that the plugin doesn't unnecessarily
 * reload data on each autocomplete request, which would impact performance.
 */
public class LatteCachingInEditorTest extends LattePluginTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.out.println("[DEBUG_LOG] Setting up LatteCachingInEditorTest");
        
        // Ensure we have a clean state for each test
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_3X);
        LatteSettings settings = LatteSettings.getInstance();
        settings.setEnableNetteApplication(true);
        settings.setEnableNetteForms(true);
        settings.setEnableNetteAssets(true);
        settings.setEnableNetteDatabase(true);
        settings.setEnableNetteHttp(true);
        settings.setEnableNetteMail(true);
    }
    
    /**
     * Gets the value of a private static field using reflection.
     * 
     * @param clazz The class containing the field
     * @param fieldName The name of the field
     * @return The value of the field
     * @throws Exception If the field cannot be accessed
     */
    private Object getPrivateStaticField(Class<?> clazz, String fieldName) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(null);
    }
    
    /**
     * Clears the cache in LatteCompletionContributor by setting the cached fields to null.
     * 
     * @throws Exception If the fields cannot be accessed
     */
    private void clearLatteCompletionCache() throws Exception {
        Field cachedVersionField = LatteCompletionContributor.class.getDeclaredField("cachedVersion");
        Field cachedMacrosField = LatteCompletionContributor.class.getDeclaredField("cachedMacros");
        Field cachedSettingsField = LatteCompletionContributor.class.getDeclaredField("cachedSettings");
        
        cachedVersionField.setAccessible(true);
        cachedMacrosField.setAccessible(true);
        cachedSettingsField.setAccessible(true);
        
        AtomicReference<?> cachedVersion = (AtomicReference<?>) cachedVersionField.get(null);
        AtomicReference<?> cachedMacros = (AtomicReference<?>) cachedMacrosField.get(null);
        AtomicReference<?> cachedSettings = (AtomicReference<?>) cachedSettingsField.get(null);
        
        cachedVersion.set(null);
        cachedMacros.set(null);
        cachedSettings.set(null);
    }
    
    /**
     * Tests that macros are cached and not recreated on each completion request.
     * 
     * This test verifies the caching mechanism in LatteCompletionContributor by:
     * 1. Clearing the cache to ensure a clean state
     * 2. Creating a Latte file with a macro and positioning the cursor inside it
     * 3. Triggering completion to initialize the cache
     * 4. Verifying that the cache is initialized after the first completion
     * 5. Triggering completion again
     * 6. Verifying that the same cache instance is used (not recreated)
     * 
     * This test is important because it ensures that macros are not reloaded on each
     * completion request, which would impact performance, especially for projects with
     * many macros or when the user frequently uses code completion.
     * 
     * @throws Exception If reflection fails
     */
    @Test
    public void testMacroCachingInEditor() throws Exception {
        // Clear the cache before starting
        clearLatteCompletionCache();
        
        // Create a Latte file with a macro
        createLatteFile("{<caret>}");
        
        // Get the cache state before completion
        AtomicReference<?> cachedMacrosBefore = (AtomicReference<?>) getPrivateStaticField(
                LatteCompletionContributor.class, "cachedMacros");
        
        // First completion request should initialize the cache
        myFixture.complete(CompletionType.BASIC);
        
        // Get the cache state after first completion
        AtomicReference<?> cachedMacrosAfterFirst = (AtomicReference<?>) getPrivateStaticField(
                LatteCompletionContributor.class, "cachedMacros");
        
        // Verify that the cache was initialized
        assertNotNull("Cache should be initialized after first completion", cachedMacrosAfterFirst.get());
        
        // Store the cached macros for comparison
        Object cachedMacrosValue = cachedMacrosAfterFirst.get();
        
        // Second completion request should use the cache
        myFixture.complete(CompletionType.BASIC);
        
        // Get the cache state after second completion
        AtomicReference<?> cachedMacrosAfterSecond = (AtomicReference<?>) getPrivateStaticField(
                LatteCompletionContributor.class, "cachedMacros");
        
        // Verify that the cache was not recreated (same instance)
        assertSame("Cache should not be recreated on second completion", 
                cachedMacrosValue, cachedMacrosAfterSecond.get());
        
        // Verify that we have completions
        assertTrue("Should have completion items", myFixture.getLookupElements().length > 0);
    }
    
    /**
     * Tests that variables are cached and not recreated on each completion request.
     * 
     * This test verifies the caching mechanism in NetteDefaultVariablesProvider by:
     * 1. Clearing the variable cache to ensure a clean state
     * 2. Creating a Latte file with a variable and positioning the cursor inside it
     * 3. Triggering completion to initialize the cache
     * 4. Verifying that variables are cached for the current project
     * 5. Triggering completion again
     * 6. Verifying that the same cache instance is used (not recreated)
     * 
     * This test is important because it ensures that variables are not reloaded on each
     * completion request, which would impact performance, especially for projects with
     * many variables or when the user frequently uses code completion for variables.
     * 
     * @throws Exception If reflection fails
     */
    @Test
    public void testVariableCachingInEditor() throws Exception {
        // Clear any existing variable cache
        NetteDefaultVariablesProvider.invalidateCache();
        
        // Create a Latte file with a variable
        createLatteFile("{$<caret>}");
        
        // First completion request should initialize the cache
        myFixture.complete(CompletionType.BASIC);
        
        // Get the project from the fixture
        com.intellij.openapi.project.Project project = myFixture.getProject();
        
        // Check if variables are cached for this project
        Field variablesCacheField = NetteDefaultVariablesProvider.class.getDeclaredField("variablesCache");
        variablesCacheField.setAccessible(true);
        
        @SuppressWarnings("unchecked")
        java.util.Map<com.intellij.openapi.project.Project, List<?>> variablesCache = 
                (java.util.Map<com.intellij.openapi.project.Project, List<?>>) variablesCacheField.get(null);
        
        // Verify that variables are cached for this project
        assertTrue("Variables should be cached for the project", variablesCache.containsKey(project));
        assertNotNull("Cached variables should not be null", variablesCache.get(project));
        assertFalse("Cached variables should not be empty", variablesCache.get(project).isEmpty());
        
        // Store the cached variables for comparison
        List<?> cachedVariables = variablesCache.get(project);
        
        // Second completion request should use the cache
        myFixture.complete(CompletionType.BASIC);
        
        // Get the updated cache
        @SuppressWarnings("unchecked")
        java.util.Map<com.intellij.openapi.project.Project, List<?>> updatedVariablesCache = 
                (java.util.Map<com.intellij.openapi.project.Project, List<?>>) variablesCacheField.get(null);
        
        // Verify that the cache was not recreated (same instance)
        assertSame("Variables cache should not be recreated on second completion", 
                cachedVariables, updatedVariablesCache.get(project));
        
        // Note: We don't verify completion items here because they might not be returned in the test environment,
        // but we've verified that the cache is working correctly.
    }
    
    /**
     * Tests that filters are cached and not recreated on each request.
     * 
     * This test verifies the caching mechanism in NetteFilterProvider by:
     * 1. Clearing the filter cache to ensure a clean state
     * 2. Verifying that the cache is not initialized before the first call
     * 3. Directly calling getValidFilterNames() to initialize the cache
     *    (instead of relying on completion, which might not trigger filter completion)
     * 4. Verifying that the cache is initialized after the first call
     * 5. Calling getValidFilterNames() again
     * 6. Verifying that the same cache instance is used (not recreated)
     * 
     * This test is important because it ensures that filters are not reloaded on each
     * request, which would impact performance, especially for projects with many filters
     * or when the user frequently uses filters in templates.
     * 
     * Note: This test uses a different approach than the other tests because filter
     * completion might not be triggered correctly in the test environment. Instead,
     * it directly calls the method that initializes the cache.
     * 
     * @throws Exception If reflection fails
     */
    @Test
    public void testFilterCachingInEditor() throws Exception {
        // Clear any existing filter cache
        NetteFilterProvider.invalidateCache();
        
        // Check if filters are cached before initialization
        Field cachedFilterNamesField = NetteFilterProvider.class.getDeclaredField("cachedFilterNames");
        cachedFilterNamesField.setAccessible(true);
        
        AtomicReference<?> cachedFilterNamesBefore = (AtomicReference<?>) cachedFilterNamesField.get(null);
        
        // Verify that the cache is not initialized
        assertNull("Filter names cache should not be initialized before first call", 
                cachedFilterNamesBefore.get());
        
        // First call to getValidFilterNames() should initialize the cache
        NetteFilterProvider.getValidFilterNames();
        
        // Get the cache state after first call
        AtomicReference<?> cachedFilterNamesAfterFirst = (AtomicReference<?>) cachedFilterNamesField.get(null);
        
        // Verify that the cache was initialized
        assertNotNull("Filter names cache should be initialized after first call", 
                cachedFilterNamesAfterFirst.get());
        
        // Store the cached filter names for comparison
        Object cachedFilterNamesValue = cachedFilterNamesAfterFirst.get();
        
        // Second call to getValidFilterNames() should use the cache
        NetteFilterProvider.getValidFilterNames();
        
        // Get the cache state after second call
        AtomicReference<?> cachedFilterNamesAfterSecond = (AtomicReference<?>) cachedFilterNamesField.get(null);
        
        // Verify that the cache was not recreated (same instance)
        assertSame("Filter names cache should not be recreated on second call", 
                cachedFilterNamesValue, cachedFilterNamesAfterSecond.get());
    }
}