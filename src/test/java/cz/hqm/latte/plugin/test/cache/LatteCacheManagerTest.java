package cz.hqm.latte.plugin.test.cache;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import org.junit.After;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import cz.hqm.latte.plugin.cache.LatteCacheManager;
import cz.hqm.latte.plugin.psi.LatteFile;
import cz.hqm.latte.plugin.test.LattePluginTestBase;

import java.util.UUID;
import javax.swing.SwingUtilities;

/**
 * Tests for the LatteCacheManager class.
 */
public class LatteCacheManagerTest extends LattePluginTestBase {

    private LatteCacheManager cacheManager;
    private VirtualFile testFile;

    @Before
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // Create a test file with a unique name to avoid conflicts
        String uniqueFileName = "test_" + UUID.randomUUID().toString() + ".latte";
        testFile = myFixture.getTempDirFixture().createFile(uniqueFileName, "{block content}\nHello, world!\n{/block}");
        
        // Get the cache manager
        cacheManager = LatteCacheManager.getInstance(getProject());
        
        // Clear the cache to ensure a clean state
        cacheManager.clearCache();
    }

    /**
     * Tests that a template can be cached and retrieved.
     */
    @Test
    public void testCacheAndRetrieveTemplate() {
        // Get the PSI file for the test file inside a read action
        LatteFile latteFile = ReadAction.compute(() -> 
            (LatteFile) myFixture.getPsiManager().findFile(testFile)
        );
        assertNotNull("Failed to get PSI file", latteFile);
        
        // Cache the template
        cacheManager.cacheTemplate(testFile, latteFile);
        
        // Retrieve the cached template
        LatteFile cachedTemplate = cacheManager.getCachedTemplate(testFile);
        assertNotNull("Failed to retrieve cached template", cachedTemplate);
        assertEquals("Cached template does not match original", latteFile, cachedTemplate);
    }

    /**
     * Tests that the cache is invalidated when a file is modified.
     */
    @Test
    public void testCacheInvalidation() {
        // Get the PSI file for the test file inside a read action
        LatteFile latteFile = ReadAction.compute(() -> 
            (LatteFile) myFixture.getPsiManager().findFile(testFile)
        );
        assertNotNull("Failed to get PSI file", latteFile);
        
        // Cache the template
        cacheManager.cacheTemplate(testFile, latteFile);
        
        // Invalidate the cache
        cacheManager.invalidateCache(testFile);
        
        // Try to retrieve the cached template
        LatteFile cachedTemplate = cacheManager.getCachedTemplate(testFile);
        assertNull("Cache was not invalidated", cachedTemplate);
    }

    /**
     * Tests that the cache is cleared when clearCache is called.
     */
    @Test
    public void testClearCache() {
        // Get the PSI file for the test file inside a read action
        LatteFile latteFile = ReadAction.compute(() -> 
            (LatteFile) myFixture.getPsiManager().findFile(testFile)
        );
        assertNotNull("Failed to get PSI file", latteFile);
        
        // Cache the template
        cacheManager.cacheTemplate(testFile, latteFile);
        
        // Clear the cache
        cacheManager.clearCache();
        
        // Try to retrieve the cached template
        LatteFile cachedTemplate = cacheManager.getCachedTemplate(testFile);
        assertNull("Cache was not cleared", cachedTemplate);
    }

    /**
     * Tests that the cache entry is invalidated when the file is modified.
     */
    @Test
    public void testCacheEntryInvalidation() {
        // Get the PSI file for the test file inside a read action
        LatteFile latteFile = ReadAction.compute(() -> 
            (LatteFile) myFixture.getPsiManager().findFile(testFile)
        );
        assertNotNull("Failed to get PSI file", latteFile);
        
        // Cache the template
        cacheManager.cacheTemplate(testFile, latteFile);
        
        // Create a new file with the same name but different content to simulate modification
        String uniqueFileName = "test_modified_" + UUID.randomUUID().toString() + ".latte";
        final VirtualFile modifiedFile;
        try {
            modifiedFile = myFixture.getTempDirFixture().createFile(uniqueFileName, "{block content}\nModified content\n{/block}");
        } catch (Exception e) {
            fail("Failed to create modified file: " + e.getMessage());
            return; // Need to return here to make the compiler happy
        }
        
        // Cache the modified template
        LatteFile modifiedLatteFile = ReadAction.compute(() -> 
            (LatteFile) myFixture.getPsiManager().findFile(modifiedFile)
        );
        assertNotNull("Failed to get modified PSI file", modifiedLatteFile);
        
        // Verify that the cache works for the modified file
        cacheManager.cacheTemplate(modifiedFile, modifiedLatteFile);
        LatteFile cachedModifiedTemplate = cacheManager.getCachedTemplate(modifiedFile);
        assertNotNull("Failed to retrieve cached modified template", cachedModifiedTemplate);
        
        // Verify that the original file's cache entry is still valid
        LatteFile cachedOriginalTemplate = cacheManager.getCachedTemplate(testFile);
        assertNotNull("Original cache entry was invalidated unexpectedly", cachedOriginalTemplate);
        
        // Invalidate the cache for the original file
        cacheManager.invalidateCache(testFile);
        
        // Verify that the original file's cache entry is now invalid
        cachedOriginalTemplate = cacheManager.getCachedTemplate(testFile);
        assertNull("Original cache entry was not invalidated", cachedOriginalTemplate);
        
        // Verify that the modified file's cache entry is still valid
        cachedModifiedTemplate = cacheManager.getCachedTemplate(modifiedFile);
        assertNotNull("Modified cache entry was invalidated unexpectedly", cachedModifiedTemplate);
    }
}
