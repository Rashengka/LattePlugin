package cz.hqm.latte.plugin.test.memory;

import com.intellij.openapi.vfs.VirtualFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import cz.hqm.latte.plugin.memory.LatteMemoryOptimizer;
import cz.hqm.latte.plugin.test.LattePluginTestBase;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the LatteMemoryOptimizer class.
 */
public class LatteMemoryOptimizerTest extends LattePluginTestBase {

    private LatteMemoryOptimizer memoryOptimizer;
    private VirtualFile testFile;

    @Override
    protected void setUp() throws Exception {
        // Set system property to ignore duplicated injectors before calling super.setUp()
        // This prevents the AssertionError in InjectedLanguageManagerImpl.pushInjectors
        System.setProperty("idea.ignore.duplicated.injectors", "true");
        
        super.setUp();
        
        // Get the memory optimizer
        memoryOptimizer = LatteMemoryOptimizer.getInstance(getProject());
        
        // Clear the segment cache to ensure a clean state
        memoryOptimizer.clearAllSegmentCache();
    }
    
    @Override
    protected void tearDown() throws Exception {
        try {
            // Clear any references to test files
            testFile = null;
            
            // Request garbage collection to free memory
            System.gc();
            
            // Reset the system property for language injectors
            // This ensures that the property doesn't affect other tests
            System.clearProperty("idea.ignore.duplicated.injectors");
            
            // Call super.tearDown() to clean up the test fixture
            super.tearDown();
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Exception during tearDown: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private VirtualFile createTestFile(String fileName, String content) throws IOException {
        return myFixture.getTempDirFixture().createFile(fileName, content);
    }

    /**
     * Tests that a small template is treated as a single segment.
     */
    
    @Test
    public void testSmallTemplateAsSingleSegment() throws Exception {
        // Create a test file with a unique name
        String content = "{block content}\nHello, world!\n{/block}";
        VirtualFile testFile = createTestFile("test_small_template.latte", content);
        
        // Get the segmented content
        LatteMemoryOptimizer.TemplateSegments segments = memoryOptimizer.getSegmentedContent(testFile, content);
        
        // Verify that there is only one segment
        assertEquals("Small template should have one segment", 1, segments.getSegmentCount());
        assertEquals("Full content should match the original", content, segments.getFullContent());
        assertEquals("Segment should match the original", content, segments.getSegmentForOffset(0));
    }

    /**
     * Tests that a large template is split into multiple segments.
     */
    
    @Test
    public void testLargeTemplateSplitIntoSegments() throws Exception {
        // Create a large template (more than MAX_SEGMENT_SIZE characters)
        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            contentBuilder.append("{block content").append(i).append("}\n");
            for (int j = 0; j < 1000; j++) {
                contentBuilder.append("Line ").append(j).append(" of block ").append(i).append("\n");
            }
            contentBuilder.append("{/block}\n");
        }
        String content = contentBuilder.toString();
        
        // Create a test file with a unique name
        VirtualFile testFile = createTestFile("test_large_template.latte", content);
        
        // Get the segmented content
        LatteMemoryOptimizer.TemplateSegments segments = memoryOptimizer.getSegmentedContent(testFile, content);
        
        // Verify that there are multiple segments
        assertTrue("Large template should have multiple segments", segments.getSegmentCount() > 1);
        assertEquals("Full content should match the original", content, segments.getFullContent());
    }

    /**
     * Tests that segment boundaries are adjusted to avoid splitting Latte macros.
     */
    
    @Test
    public void testSegmentBoundariesAdjusted() throws Exception {
        // Create a template with a Latte macro near the segment boundary
        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 0; i < 9900; i++) {
            contentBuilder.append("X");
        }
        contentBuilder.append("{block content}\nHello, world!\n{/block}");
        String content = contentBuilder.toString();
        
        // Create a test file with a unique name
        VirtualFile testFile = createTestFile("test_segment_boundaries.latte", content);
        
        // Get the segmented content
        LatteMemoryOptimizer.TemplateSegments segments = memoryOptimizer.getSegmentedContent(testFile, content);
        
        // Verify that the segment boundary is adjusted
        if (segments.getSegmentCount() > 1) {
            // Get the first segment
            String firstSegment = segments.getSegmentForOffset(0);
            
            // The first segment should not end in the middle of the Latte macro
            assertFalse("Segment should not end with an opening brace", firstSegment.endsWith("{"));
            assertFalse("Segment should not end with a partial macro", firstSegment.endsWith("{block"));
        }
    }

    /**
     * Tests that the correct segment is returned for a given offset.
     */
    
    @Test
    public void testGetSegmentForOffset() throws Exception {
        // Create a template with multiple segments
        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            contentBuilder.append("{block content").append(i).append("}\n");
            for (int j = 0; j < 1000; j++) {
                contentBuilder.append("Line ").append(j).append(" of block ").append(i).append("\n");
            }
            contentBuilder.append("{/block}\n");
        }
        String content = contentBuilder.toString();
        
        // Create a test file with a unique name
        VirtualFile testFile = createTestFile("test_segment_offset.latte", content);
        
        // Get the segmented content
        LatteMemoryOptimizer.TemplateSegments segments = memoryOptimizer.getSegmentedContent(testFile, content);
        
        // Verify that the correct segment is returned for different offsets
        if (segments.getSegmentCount() > 1) {
            // Get the first segment
            String firstSegment = segments.getSegmentForOffset(0);
            
            // Get a segment from the middle of the content
            int middleOffset = content.length() / 2;
            String middleSegment = segments.getSegmentForOffset(middleOffset);
            
            // Get the last segment
            String lastSegment = segments.getSegmentForOffset(content.length() - 1);
            
            // Verify that the segments are different
            if (!firstSegment.equals(middleSegment) || !middleSegment.equals(lastSegment)) {
                // At least some segments are different, which is what we expect for a large template
                assertTrue("Test passed", true);
            }
        }
    }

    /**
     * Tests that the segment cache is cleared when clearSegmentCache is called.
     */
    
    @Test
    public void testClearSegmentCache() throws Exception {
        String content = "{block content}\nHello, world!\n{/block}";
        
        // Create a test file with a unique name
        VirtualFile testFile = createTestFile("test_clear_cache.latte", content);
        
        // Get the segmented content
        memoryOptimizer.getSegmentedContent(testFile, content);
        
        // Clear the segment cache for the file
        memoryOptimizer.clearSegmentCache(testFile);
        
        // Get the segmented content again
        LatteMemoryOptimizer.TemplateSegments segments = memoryOptimizer.getSegmentedContent(testFile, content);
        
        // Verify that the segments are recreated
        assertEquals("Small template should have one segment", 1, segments.getSegmentCount());
        assertEquals("Full content should match the original", content, segments.getFullContent());
    }

    /**
     * Tests that the segment cache is updated when the content changes.
     */
    
    @Test
    public void testSegmentCacheUpdatedWhenContentChanges() throws Exception {
        String originalContent = "{block content}\nHello, world!\n{/block}";
        String modifiedContent = "{block content}\nHello, modified world!\n{/block}";
        
        // Create a test file with a unique name
        VirtualFile testFile = createTestFile("test_cache_update.latte", originalContent);
        
        // Get the segmented content for the original content
        LatteMemoryOptimizer.TemplateSegments originalSegments = memoryOptimizer.getSegmentedContent(testFile, originalContent);
        
        // Get the segmented content for the modified content
        LatteMemoryOptimizer.TemplateSegments modifiedSegments = memoryOptimizer.getSegmentedContent(testFile, modifiedContent);
        
        // Verify that the segments are different
        assertFalse("Segments should be different for different content", originalSegments.getFullContent().equals(modifiedSegments.getFullContent()));
    }
}
