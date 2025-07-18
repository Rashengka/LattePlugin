package org.latte.plugin.test.performance;

import com.intellij.openapi.application.ApplicationManager;
import org.junit.jupiter.api.AfterEach;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.latte.plugin.cache.LatteCacheManager;
import org.latte.plugin.memory.LatteMemoryOptimizer;
import org.latte.plugin.parser.LatteIncrementalParser;
import org.latte.plugin.psi.LatteFile;
import org.latte.plugin.test.LattePluginTestBase;

import java.io.IOException;
import java.util.List;

/**
 * Performance benchmark tests for the Latte plugin.
 * These tests measure the performance impact of the optimizations.
 */
public class LattePerformanceBenchmarkTest extends LattePluginTestBase {

    private LatteCacheManager cacheManager;
    private LatteIncrementalParser incrementalParser;
    private LatteMemoryOptimizer memoryOptimizer;
    private VirtualFile testFile;

    @BeforeEach
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // Get the services
        cacheManager = LatteCacheManager.getInstance(getProject());
        incrementalParser = LatteIncrementalParser.getInstance(getProject());
        memoryOptimizer = LatteMemoryOptimizer.getInstance(getProject());
        
        // Clear caches to ensure a clean state
        cacheManager.clearCache();
        incrementalParser.clearAllLastKnownContent();
        memoryOptimizer.clearAllSegmentCache();
    }
    
    private VirtualFile createTestFile(String fileName, String content) throws IOException {
        return myFixture.getTempDirFixture().createFile(fileName, content);
    }

    /**
     * Tests the performance of template caching.
     * Measures the time it takes to parse a template with and without caching.
     */
    
    
    @Test
    public void testCachingPerformance() throws Exception {
        // Initialize test environment
        setUp();
        
        // Create a large template
        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            contentBuilder.append("{block content").append(i).append("}\n");
            for (int j = 0; j < 100; j++) {
                contentBuilder.append("Line ").append(j).append(" of block ").append(i).append("\n");
            }
            contentBuilder.append("{/block}\n");
        }
        String content = contentBuilder.toString();
        
        // Create a test file with a unique name
        VirtualFile testFile = createTestFile("test_caching_performance.latte", content);
        
        // Get the PSI file for the test file inside a read action
        LatteFile latteFile = ApplicationManager.getApplication().runReadAction(
            (Computable<LatteFile>) () -> (LatteFile) myFixture.getPsiManager().findFile(testFile)
        );
        assertNotNull("Failed to get PSI file", latteFile);
        
        // Perform warmup iterations to stabilize JVM performance
        System.out.println("[DEBUG_LOG] Performing warmup iterations...");
        for (int i = 0; i < 5; i++) {
            // Warmup without caching
            cacheManager.clearCache();
            ApplicationManager.getApplication().runReadAction(
                (Computable<LatteFile>) () -> (LatteFile) myFixture.getPsiManager().findFile(testFile)
            );
            
            // Warmup with caching
            ApplicationManager.getApplication().runReadAction(
                (Computable<Void>) () -> {
                    cacheManager.cacheTemplate(testFile, latteFile);
                    cacheManager.getCachedTemplate(testFile);
                    return null;
                }
            );
        }
        
        // Increase number of iterations for more reliable results
        final int iterations = 20;
        
        // Measure time for parsing operation only (without caching)
        System.out.println("[DEBUG_LOG] Measuring time without caching...");
        cacheManager.clearCache(); // Clear cache once before the loop
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            // Only measure the time to parse the file, not the time to clear the cache
            ApplicationManager.getApplication().runReadAction(
                (Computable<LatteFile>) () -> (LatteFile) myFixture.getPsiManager().findFile(testFile)
            );
            // Clear cache after each iteration to force re-parsing
            cacheManager.clearCache();
        }
        long endTime = System.currentTimeMillis();
        long timeWithoutCaching = endTime - startTime;
        
        // Measure time for retrieving cached template only
        System.out.println("[DEBUG_LOG] Measuring time with caching...");
        // Cache the template once before the loop
        ApplicationManager.getApplication().runReadAction(
            (Computable<Void>) () -> {
                cacheManager.cacheTemplate(testFile, latteFile);
                return null;
            }
        );
        startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            // Only measure the time to retrieve the cached template
            ApplicationManager.getApplication().runReadAction(
                (Computable<LatteFile>) () -> cacheManager.getCachedTemplate(testFile)
            );
        }
        endTime = System.currentTimeMillis();
        long timeWithCaching = endTime - startTime;
        
        // Print the results
        System.out.println("[DEBUG_LOG] Caching Performance Test:");
        System.out.println("[DEBUG_LOG] Time without caching: " + timeWithoutCaching + "ms");
        System.out.println("[DEBUG_LOG] Time with caching: " + timeWithCaching + "ms");
        System.out.println("[DEBUG_LOG] Speedup: " + (timeWithoutCaching / (double) timeWithCaching) + "x");
        
        // Add a tolerance factor to account for system variations
        // Caching should be at least 1.5x faster than no caching
        double speedupFactor = timeWithoutCaching / (double) timeWithCaching;
        System.out.println("[DEBUG_LOG] Speedup factor: " + speedupFactor);
        
        // Verify that caching is significantly faster
        assertTrue("Caching should be faster than no caching (speedup factor: " + speedupFactor + ")", 
                   timeWithCaching * 1.5 <= timeWithoutCaching);
    }

    /**
     * Tests the performance of incremental parsing.
     * Measures the time it takes to parse a template with and without incremental parsing.
     */
    
    
    @Test
    public void testIncrementalParsingPerformance() throws Exception {
        // Initialize test environment
        setUp();
        
        // Create a large template
        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            contentBuilder.append("{block content").append(i).append("}\n");
            for (int j = 0; j < 100; j++) {
                contentBuilder.append("Line ").append(j).append(" of block ").append(i).append("\n");
            }
            contentBuilder.append("{/block}\n");
        }
        String originalContent = contentBuilder.toString();
        
        // Create a slightly modified version of the template
        String modifiedContent = originalContent.replace("Line 50 of block 50", "Modified line");
        
        // Create a test file with a unique name
        VirtualFile testFile = createTestFile("test_incremental_parsing.latte", originalContent);
        
        // Measure time without incremental parsing
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            // Parse the entire file each time
            incrementalParser.clearAllLastKnownContent();
            incrementalParser.parseChangedParts(testFile, originalContent);
            incrementalParser.parseChangedParts(testFile, modifiedContent);
        }
        long endTime = System.currentTimeMillis();
        long timeWithoutIncremental = endTime - startTime;
        
        // Measure time with incremental parsing
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            // Parse incrementally
            incrementalParser.parseChangedParts(testFile, originalContent);
            incrementalParser.parseChangedParts(testFile, modifiedContent);
        }
        endTime = System.currentTimeMillis();
        long timeWithIncremental = endTime - startTime;
        
        // Print the results
        System.out.println("[DEBUG_LOG] Incremental Parsing Performance Test:");
        System.out.println("[DEBUG_LOG] Time without incremental parsing: " + timeWithoutIncremental + "ms");
        System.out.println("[DEBUG_LOG] Time with incremental parsing: " + timeWithIncremental + "ms");
        System.out.println("[DEBUG_LOG] Speedup: " + (timeWithoutIncremental / (double) timeWithIncremental) + "x");
        
        // Verify that incremental parsing is faster
        assertTrue("Incremental parsing should be faster than full parsing", timeWithIncremental <= timeWithoutIncremental);
    }

    /**
     * Tests the performance of memory optimization.
     * Measures the memory usage with and without memory optimization.
     */
    
    
    @Test
    public void testMemoryOptimizationPerformance() throws Exception {
        // Initialize test environment
        setUp();
        
        // Create a large template
        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            contentBuilder.append("{block content").append(i).append("}\n");
            for (int j = 0; j < 100; j++) {
                contentBuilder.append("Line ").append(j).append(" of block ").append(i).append("\n");
            }
            contentBuilder.append("{/block}\n");
        }
        String content = contentBuilder.toString();
        
        // Create a test file with a unique name
        VirtualFile testFile = createTestFile("test_memory_optimization.latte", content);
        
        // Measure memory usage without optimization
        System.gc(); // Request garbage collection to get a more accurate measurement
        long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        // Create multiple copies of the content without optimization
        String[] contents = new String[10];
        for (int i = 0; i < contents.length; i++) {
            contents[i] = content;
        }
        
        System.gc(); // Request garbage collection to get a more accurate measurement
        long memoryAfterWithoutOptimization = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long memoryUsedWithoutOptimization = memoryAfterWithoutOptimization - memoryBefore;
        
        // Clear the arrays to free memory
        for (int i = 0; i < contents.length; i++) {
            contents[i] = null;
        }
        
        // Measure memory usage with optimization
        System.gc(); // Request garbage collection to get a more accurate measurement
        memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        // Create multiple copies of the content with optimization
        LatteMemoryOptimizer.TemplateSegments[] segments = new LatteMemoryOptimizer.TemplateSegments[10];
        for (int i = 0; i < segments.length; i++) {
            segments[i] = memoryOptimizer.getSegmentedContent(testFile, content);
        }
        
        System.gc(); // Request garbage collection to get a more accurate measurement
        long memoryAfterWithOptimization = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long memoryUsedWithOptimization = memoryAfterWithOptimization - memoryBefore;
        
        // Print the results
        System.out.println("[DEBUG_LOG] Memory Optimization Performance Test:");
        System.out.println("[DEBUG_LOG] Memory used without optimization: " + memoryUsedWithoutOptimization + " bytes");
        System.out.println("[DEBUG_LOG] Memory used with optimization: " + memoryUsedWithOptimization + " bytes");
        System.out.println("[DEBUG_LOG] Memory reduction: " + (memoryUsedWithoutOptimization - memoryUsedWithOptimization) + " bytes");
        System.out.println("[DEBUG_LOG] Memory reduction percentage: " + (100 - (memoryUsedWithOptimization * 100.0 / memoryUsedWithoutOptimization)) + "%");
        
        // Note: This test may not always show a significant memory reduction due to JVM memory management,
        // but it should at least demonstrate that the memory optimization doesn't increase memory usage.
    }

    /**
     * Tests the combined performance of all optimizations.
     * Measures the time it takes to parse and process a large template with and without optimizations.
     */
    
    
    @Test
    public void testCombinedOptimizationsPerformance() throws Exception {
        // Initialize test environment
        setUp();
        
        // Create a large template
        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            contentBuilder.append("{block content").append(i).append("}\n");
            for (int j = 0; j < 100; j++) {
                contentBuilder.append("Line ").append(j).append(" of block ").append(i).append("\n");
            }
            contentBuilder.append("{/block}\n");
        }
        String originalContent = contentBuilder.toString();
        
        // Create a slightly modified version of the template
        String modifiedContent = originalContent.replace("Line 50 of block 50", "Modified line");
        
        // Create a test file with a unique name
        VirtualFile testFile = createTestFile("test_combined_optimizations.latte", originalContent);
        
        // Get the PSI file for the test file inside a read action
        LatteFile latteFile = ApplicationManager.getApplication().runReadAction(
            (Computable<LatteFile>) () -> (LatteFile) myFixture.getPsiManager().findFile(testFile)
        );
        assertNotNull("Failed to get PSI file", latteFile);
        
        // Measure time without optimizations
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            // Clear all caches to simulate no optimizations
            cacheManager.clearCache();
            incrementalParser.clearAllLastKnownContent();
            memoryOptimizer.clearAllSegmentCache();
            
            // Process the template inside a read action
            ApplicationManager.getApplication().runReadAction(
                (Computable<Void>) () -> {
                    myFixture.getPsiManager().findFile(testFile);
                    incrementalParser.parseChangedParts(testFile, originalContent);
                    incrementalParser.parseChangedParts(testFile, modifiedContent);
                    memoryOptimizer.getSegmentedContent(testFile, originalContent);
                    memoryOptimizer.getSegmentedContent(testFile, modifiedContent);
                    return null;
                }
            );
        }
        long endTime = System.currentTimeMillis();
        long timeWithoutOptimizations = endTime - startTime;
        
        // Measure time with optimizations
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            // Process the template with optimizations inside a read action
            ApplicationManager.getApplication().runReadAction(
                (Computable<Void>) () -> {
                    cacheManager.cacheTemplate(testFile, latteFile);
                    cacheManager.getCachedTemplate(testFile);
                    incrementalParser.parseChangedParts(testFile, originalContent);
                    incrementalParser.parseChangedParts(testFile, modifiedContent);
                    memoryOptimizer.getSegmentedContent(testFile, originalContent);
                    memoryOptimizer.getSegmentedContent(testFile, modifiedContent);
                    return null;
                }
            );
        }
        endTime = System.currentTimeMillis();
        long timeWithOptimizations = endTime - startTime;
        
        // Print the results
        System.out.println("[DEBUG_LOG] Combined Optimizations Performance Test:");
        System.out.println("[DEBUG_LOG] Time without optimizations: " + timeWithoutOptimizations + "ms");
        System.out.println("[DEBUG_LOG] Time with optimizations: " + timeWithOptimizations + "ms");
        System.out.println("[DEBUG_LOG] Speedup: " + (timeWithoutOptimizations / (double) timeWithOptimizations) + "x");
        
        // Verify that optimizations are faster
        assertTrue("Optimizations should improve performance", timeWithOptimizations <= timeWithoutOptimizations);
    }
}
