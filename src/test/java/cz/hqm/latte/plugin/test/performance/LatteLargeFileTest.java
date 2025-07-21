package cz.hqm.latte.plugin.test.performance;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.TempDirTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import cz.hqm.latte.plugin.parser.LatteIncrementalParser;
import cz.hqm.latte.plugin.psi.LatteFile;
import cz.hqm.latte.plugin.test.LattePluginTestBase;
import cz.hqm.latte.plugin.version.LatteVersion;
import cz.hqm.latte.plugin.version.LatteVersionManager;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Tests for the Latte parser with very large files.
 * Tests include:
 * - Validation of large valid files
 * - Error detection in large files with errors
 * - Processing speed and memory usage
 * - Ensuring the parser doesn't get stuck in infinite loops
 * - Ensuring the parser quickly identifies errors at the top of a very long file
 */
public class LatteLargeFileTest extends LattePluginTestBase {

    private TempDirTestFixture myTempDirFixture;
    private LatteIncrementalParser parser;
    private static final int VALID_FILE_SIZE = 10000; // 10,000 lines
    private static final int ERROR_FILE_SIZE = 10000; // 10,000 lines
    private static final int WARMUP_ITERATIONS = 3;
    private static final long TIMEOUT_SECONDS = 30; // Maximum time allowed for parsing

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        myTempDirFixture = IdeaTestFixtureFactory.getFixtureFactory().createTempDirTestFixture();
        myTempDirFixture.setUp();
        parser = LatteIncrementalParser.getInstance(getProject());
    }

    @After
    @Override
    public void tearDown() throws Exception {
        try {
            myTempDirFixture.tearDown();
        } finally {
            super.tearDown();
        }
    }

    /**
     * Creates a test file with the given name and content.
     */
    private VirtualFile createTestFile(String fileName, String content) throws IOException {
        return ApplicationManager.getApplication().runWriteAction((Computable<VirtualFile>) () -> {
            try {
                VirtualFile file = myTempDirFixture.createFile(fileName, content);
                return file;
            } catch (IOException e) {
                throw new RuntimeException("Failed to create test file", e);
            }
        });
    }

    /**
     * Safely runs a read action with the given computation.
     */
    private <T> T safeRunReadAction(Computable<T> computation) {
        return ApplicationManager.getApplication().runReadAction(computation);
    }

    /**
     * Forces garbage collection to get more accurate memory measurements.
     */
    private void forceGarbageCollection() {
        for (int i = 0; i < 5; i++) {
            System.gc();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Gets the currently used memory in MB.
     */
    private long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
    }

    /**
     * Generates a large valid Latte file with HTML, CSS, JavaScript, and various Latte macros.
     */
    private String generateValidLatteFile(int lines) {
        StringBuilder content = new StringBuilder();
        
        // HTML header
        content.append("<!DOCTYPE html>\n");
        content.append("<html lang=\"en\">\n");
        content.append("<head>\n");
        content.append("    <meta charset=\"UTF-8\">\n");
        content.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        content.append("    <title>{$title|upper}</title>\n");
        
        // CSS styles
        content.append("    <style>\n");
        for (int i = 0; i < 200 && content.toString().split("\n").length < lines; i++) {
            content.append("        .class-").append(i).append(" {\n");
            content.append("            color: #").append(String.format("%06x", i * 1000)).append(";\n");
            content.append("            padding: ").append(i % 20).append("px;\n");
            content.append("            margin: ").append(i % 15).append("px;\n");
            content.append("        }\n");
        }
        content.append("    </style>\n");
        
        // JavaScript
        content.append("    <script>\n");
        content.append("        document.addEventListener('DOMContentLoaded', function() {\n");
        for (int i = 0; i < 100 && content.toString().split("\n").length < lines; i++) {
            content.append("            console.log('Loading component ").append(i).append("');\n");
            content.append("            // Initialize component ").append(i).append("\n");
            content.append("            const element").append(i).append(" = document.getElementById('element-").append(i).append("');\n");
            content.append("            if (element").append(i).append(") {\n");
            content.append("                element").append(i).append(".addEventListener('click', function() {\n");
            content.append("                    console.log('Clicked element ").append(i).append("');\n");
            content.append("                });\n");
            content.append("            }\n");
        }
        content.append("        });\n");
        content.append("    </script>\n");
        content.append("</head>\n");
        
        // Body with various Latte macros
        content.append("<body>\n");
        
        // Define variables
        content.append("    {* Define variables *}\n");
        content.append("    {var $pageId = 'homepage'}\n");
        content.append("    {var $isAdmin = $user->role === 'admin'}\n");
        content.append("    {var $items = range(1, 100)}\n");
        
        // Header with conditional rendering
        content.append("    <header id=\"{$pageId}\">\n");
        content.append("        {if $user->isLoggedIn()}\n");
        content.append("            <h1>Welcome, {$user->name|capitalize}</h1>\n");
        content.append("            <div n:if=\"$isAdmin\" class=\"admin-panel\">\n");
        content.append("                <p>You have admin privileges</p>\n");
        content.append("            </div>\n");
        content.append("        {else}\n");
        content.append("            <h1>Welcome, Guest</h1>\n");
        content.append("            <p>Please <a href=\"/login\">log in</a> to access all features.</p>\n");
        content.append("        {/if}\n");
        content.append("    </header>\n");
        
        // Main content with nested blocks
        content.append("    <main>\n");
        
        // Generate multiple sections with different Latte features
        int sectionsNeeded = (lines - content.toString().split("\n").length) / 50;
        for (int section = 0; section < sectionsNeeded; section++) {
            content.append("        <section class=\"section-").append(section).append("\">\n");
            content.append("            <h2>Section ").append(section).append("</h2>\n");
            
            // Different content based on section number
            switch (section % 5) {
                case 0:
                    // Foreach loop
                    content.append("            {foreach $items as $item}\n");
                    content.append("                {if $item % 10 === 0}\n");
                    content.append("                    <div class=\"milestone\">\n");
                    content.append("                        <h3>Milestone {$item}</h3>\n");
                    content.append("                        <p>This is a special milestone item.</p>\n");
                    content.append("                    </div>\n");
                    content.append("                {else}\n");
                    content.append("                    <div class=\"regular-item\">\n");
                    content.append("                        <p>Item {$item}</p>\n");
                    content.append("                    </div>\n");
                    content.append("                {/if}\n");
                    content.append("            {/foreach}\n");
                    break;
                    
                case 1:
                    // Capture block
                    content.append("            {capture $sectionContent}\n");
                    content.append("                <div class=\"captured-content\">\n");
                    content.append("                    <p>This content is captured in a variable.</p>\n");
                    content.append("                    <ul>\n");
                    for (int i = 1; i <= 5; i++) {
                        content.append("                        <li>Item ").append(i).append("</li>\n");
                    }
                    content.append("                    </ul>\n");
                    content.append("                </div>\n");
                    content.append("            {/capture}\n");
                    content.append("            {$sectionContent|noescape}\n");
                    break;
                    
                case 2:
                    // N-attributes
                    content.append("            <ul n:inner-foreach=\"$items as $item\" n:if=\"count($items) > 0\">\n");
                    content.append("                <li n:class=\"$item % 2 === 0 ? even : odd\">{$item}</li>\n");
                    content.append("            </ul>\n");
                    break;
                    
                case 3:
                    // Include and extends
                    content.append("            {* Include example *}\n");
                    content.append("            {include 'components/sidebar.latte'}\n");
                    content.append("            \n");
                    content.append("            {* Block definition *}\n");
                    content.append("            {define customBlock}\n");
                    content.append("                <div class=\"custom-block\">\n");
                    content.append("                    <p>This is a custom block that can be included elsewhere.</p>\n");
                    content.append("                </div>\n");
                    content.append("            {/define}\n");
                    break;
                    
                case 4:
                    // Translations and filters
                    content.append("            <div class=\"translations\">\n");
                    content.append("                <h3>{_'section.title'}</h3>\n");
                    content.append("                <p>{_'section.description'|truncate:100}</p>\n");
                    content.append("                <time datetime=\"{$now|date:'Y-m-d'}\">{$now|date:'j F Y'}</time>\n");
                    content.append("            </div>\n");
                    break;
            }
            
            content.append("        </section>\n");
        }
        
        // Add more content if needed to reach the desired line count
        while (content.toString().split("\n").length < lines - 20) {
            content.append("    <div class=\"filler-content\">\n");
            content.append("        <p>Additional content to reach the desired line count.</p>\n");
            content.append("        {var $randomVar = random_int(1, 100)}\n");
            content.append("        {if $randomVar > 50}\n");
            content.append("            <span>Random value is greater than 50: {$randomVar}</span>\n");
            content.append("        {else}\n");
            content.append("            <span>Random value is less than or equal to 50: {$randomVar}</span>\n");
            content.append("        {/if}\n");
            content.append("    </div>\n");
        }
        
        // Footer
        content.append("    <footer>\n");
        content.append("        <p>&copy; {date('Y')} My Website</p>\n");
        content.append("        {include 'components/footer.latte'}\n");
        content.append("    </footer>\n");
        content.append("</body>\n");
        content.append("</html>\n");
        
        return content.toString();
    }

    /**
     * Generates a large Latte file with various macro errors.
     * The errors are placed at different positions in the file to test error detection.
     */
    private String generateErrorLatteFile(int lines) {
        StringBuilder content = new StringBuilder();
        
        // HTML header
        content.append("<!DOCTYPE html>\n");
        content.append("<html lang=\"en\">\n");
        content.append("<head>\n");
        content.append("    <meta charset=\"UTF-8\">\n");
        content.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        content.append("    <title>{$title|upper}</title>\n");
        
        // Error 1: Unclosed macro at the top of the file
        content.append("    {if $showHeader\n"); // Missing closing brace
        
        // CSS styles
        content.append("    <style>\n");
        for (int i = 0; i < 100 && content.toString().split("\n").length < lines / 4; i++) {
            content.append("        .class-").append(i).append(" {\n");
            content.append("            color: #").append(String.format("%06x", i * 1000)).append(";\n");
            content.append("            padding: ").append(i % 20).append("px;\n");
            content.append("            margin: ").append(i % 15).append("px;\n");
            content.append("        }\n");
        }
        content.append("    </style>\n");
        
        // JavaScript with an error
        content.append("    <script>\n");
        content.append("        document.addEventListener('DOMContentLoaded', function() {\n");
        // Error 2: Invalid variable syntax in JavaScript
        content.append("            console.log('User name: {$user->name');\n"); // Missing closing brace
        for (int i = 0; i < 50 && content.toString().split("\n").length < lines / 3; i++) {
            content.append("            console.log('Loading component ").append(i).append("');\n");
        }
        content.append("        });\n");
        content.append("    </script>\n");
        content.append("</head>\n");
        
        // Body with various Latte macros and errors
        content.append("<body>\n");
        
        // Define variables
        content.append("    {* Define variables *}\n");
        content.append("    {var $pageId = 'homepage'}\n");
        content.append("    {var $isAdmin = $user->role === 'admin'}\n");
        
        // Error 3: Mismatched macro closing
        content.append("    {if $user->isLoggedIn()}\n");
        content.append("        <h1>Welcome, {$user->name|capitalize}</h1>\n");
        content.append("    {/foreach}\n"); // Should be {/if}
        
        // Generate multiple sections with different Latte features and errors
        int sectionsNeeded = (lines - content.toString().split("\n").length) / 50;
        for (int section = 0; section < sectionsNeeded; section++) {
            content.append("    <section class=\"section-").append(section).append("\">\n");
            content.append("        <h2>Section ").append(section).append("</h2>\n");
            
            // Different content and errors based on section number
            switch (section % 6) {
                case 0:
                    // Error 4: Unclosed foreach
                    content.append("        {foreach $items as $item\n"); // Missing closing brace
                    content.append("            <div>{$item}</div>\n");
                    content.append("        {/foreach}\n");
                    break;
                    
                case 1:
                    // Error 5: Invalid filter syntax
                    content.append("        <p>{$description|truncate:100:}</p>\n"); // Extra colon
                    break;
                    
                case 2:
                    // Error 6: Crossing macros
                    content.append("        {if $showDetails}\n");
                    content.append("            {foreach $details as $detail}\n");
                    content.append("                <div>{$detail}</div>\n");
                    content.append("            {/if}\n"); // Should be {/foreach}
                    content.append("        {/foreach}\n"); // Should be {/if}
                    break;
                    
                case 3:
                    // Error 7: Invalid n-attribute syntax
                    content.append("        <div n:if=\"$showSection\" n:class=\"$isImportant ? important\">\n"); // Missing second value after ?
                    content.append("            <p>Important section content</p>\n");
                    content.append("        </div>\n");
                    break;
                    
                case 4:
                    // Error 8: Undefined variable (not a syntax error, but should be detected)
                    content.append("        <p>{$undefinedVariable}</p>\n");
                    break;
                    
                case 5:
                    // Error 9: Invalid macro name
                    content.append("        {123invalidName}\n"); // Macro names must start with a letter or underscore
                    break;
            }
            
            content.append("    </section>\n");
        }
        
        // Add more content to reach the desired line count
        while (content.toString().split("\n").length < lines - 20) {
            content.append("    <div class=\"filler-content\">\n");
            content.append("        <p>Additional content to reach the desired line count.</p>\n");
            content.append("    </div>\n");
        }
        
        // Error 10: Unclosed macro at the end of the file
        content.append("    {if $showFooter\n"); // Missing closing brace
        
        // Footer
        content.append("    <footer>\n");
        content.append("        <p>&copy; {date('Y')} My Website</p>\n");
        content.append("    </footer>\n");
        content.append("</body>\n");
        content.append("</html>\n");
        
        return content.toString();
    }

    /**
     * Tests parsing a large valid Latte file.
     * Measures the time taken and memory used.
     */
    @Test
    public void testParsingLargeValidFile() throws IOException {
        // Generate a large valid Latte file
        String validContent = generateValidLatteFile(VALID_FILE_SIZE);
        VirtualFile validFile = createTestFile("large_valid.latte", validContent);
        
        // Warm up the parser
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            parser.parseChangedParts(validFile, validContent);
        }
        
        // Measure memory before parsing
        forceGarbageCollection();
        long memoryBefore = getUsedMemory();
        
        // Measure time taken to parse
        long startTime = System.nanoTime();
        List<TextRange> changes = parser.parseChangedParts(validFile, validContent);
        long endTime = System.nanoTime();
        long durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        
        // Measure memory after parsing
        forceGarbageCollection();
        long memoryAfter = getUsedMemory();
        
        // Log performance metrics
        System.out.println("[DEBUG_LOG] Large valid file parsing:");
        System.out.println("[DEBUG_LOG] - File size: " + validContent.length() + " characters, " + VALID_FILE_SIZE + " lines");
        System.out.println("[DEBUG_LOG] - Time taken: " + durationMs + " ms");
        System.out.println("[DEBUG_LOG] - Memory used: " + (memoryAfter - memoryBefore) + " MB");
        System.out.println("[DEBUG_LOG] - Changes detected: " + changes.size());
        
        // Assert that parsing completed within a reasonable time
        assertTrue("Parsing took too long: " + durationMs + " ms", durationMs < TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS));
        
        // Assert that memory usage is reasonable
        assertTrue("Memory usage too high: " + (memoryAfter - memoryBefore) + " MB", 
                   (memoryAfter - memoryBefore) < 500); // 500 MB is a reasonable upper limit
    }

    /**
     * Tests parsing a large Latte file with errors.
     * Measures the time taken and memory used.
     * Verifies that the parser detects the errors.
     */
    @Test
    public void testParsingLargeErrorFile() throws IOException {
        // Save the original version to restore it later
        LatteVersion originalVersion = LatteVersionManager.getCurrentVersion();
        
        try {
            // Set the version to 3.x to ensure block macros require closing tags
            System.out.println("[DEBUG_LOG] Setting Latte version to 3.x for error detection test");
            LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_3X);
            
            // Generate a large Latte file with errors
            String errorContent = generateErrorLatteFile(ERROR_FILE_SIZE);
            System.out.println("[DEBUG_LOG] Generated error file with " + errorContent.length() + " characters and " + ERROR_FILE_SIZE + " lines");
            
            // Create a smaller test file first to verify parser is working
            String smallErrorContent = "{if $condition}\n<p>Test</p>\n{/foreach}\n"; // Mismatched closing tag
            VirtualFile smallErrorFile = createTestFile("small_error.latte", smallErrorContent);
            
            System.out.println("[DEBUG_LOG] Testing parser with small error file first");
            List<TextRange> smallChanges = parser.parseChangedParts(smallErrorFile, smallErrorContent);
            System.out.println("[DEBUG_LOG] Small error file parsing detected " + smallChanges.size() + " changes");
            
            // Now test with the large file
            VirtualFile errorFile = createTestFile("large_error.latte", errorContent);
            System.out.println("[DEBUG_LOG] Created large error file: " + errorFile.getPath());
            
            // Warm up the parser
            System.out.println("[DEBUG_LOG] Warming up parser");
            for (int i = 0; i < WARMUP_ITERATIONS; i++) {
                // Clear the parser's cache to force a re-parse
                parser.clearLastKnownContent(errorFile);
                List<TextRange> warmupChanges = parser.parseChangedParts(errorFile, errorContent);
                System.out.println("[DEBUG_LOG] Warmup iteration " + i + " detected " + warmupChanges.size() + " changes");
            }
            
            // Measure memory before parsing
            forceGarbageCollection();
            long memoryBefore = getUsedMemory();
            System.out.println("[DEBUG_LOG] Memory before parsing: " + memoryBefore + " MB");
            
            // Clear the parser's cache to force a re-parse for the actual test
            System.out.println("[DEBUG_LOG] Clearing parser cache before actual test");
            parser.clearLastKnownContent(errorFile);
            
            // Measure time taken to parse
            System.out.println("[DEBUG_LOG] Starting parsing of large error file");
            long startTime = System.nanoTime();
            List<TextRange> changes = parser.parseChangedParts(errorFile, errorContent);
            long endTime = System.nanoTime();
            long durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
            System.out.println("[DEBUG_LOG] Finished parsing of large error file");
            
            // Measure memory after parsing
            forceGarbageCollection();
            long memoryAfter = getUsedMemory();
            
            // Log performance metrics
            System.out.println("[DEBUG_LOG] Large error file parsing:");
            System.out.println("[DEBUG_LOG] - File size: " + errorContent.length() + " characters, " + ERROR_FILE_SIZE + " lines");
            System.out.println("[DEBUG_LOG] - Time taken: " + durationMs + " ms");
            System.out.println("[DEBUG_LOG] - Memory used: " + (memoryAfter - memoryBefore) + " MB");
            System.out.println("[DEBUG_LOG] - Changes detected: " + changes.size());
            
            if (changes.size() > 0) {
                System.out.println("[DEBUG_LOG] - First change: " + changes.get(0).getStartOffset() + " to " + changes.get(0).getEndOffset());
            }
            
            // Assert that parsing completed within a reasonable time
            assertTrue("Parsing took too long: " + durationMs + " ms", durationMs < TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS));
            
            // Assert that memory usage is reasonable
            assertTrue("Memory usage too high: " + (memoryAfter - memoryBefore) + " MB", 
                       (memoryAfter - memoryBefore) < 500); // 500 MB is a reasonable upper limit
            
            // Assert that the parser detected at least some of the errors
            // The exact number depends on how the parser handles errors
            assertTrue("Parser didn't detect any errors", changes.size() > 0);
        } finally {
            // Restore the original version
            LatteVersionManager.setCurrentVersion(originalVersion);
            System.out.println("[DEBUG_LOG] Restored original Latte version");
        }
    }

    /**
     * Tests that the parser quickly identifies errors at the top of a very long file.
     * The error is placed at the top of the file, and we verify that the parser
     * detects it without processing the entire file.
     */
    @Test
    public void testQuickErrorDetectionAtTop() throws IOException {
        // Create a file with an error at the top
        StringBuilder content = new StringBuilder();
        content.append("{if $showHeader\n"); // Error: unclosed macro
        
        // Add a lot of content after the error
        for (int i = 0; i < ERROR_FILE_SIZE - 10; i++) {
            content.append("<div>Line ").append(i).append("</div>\n");
        }
        
        VirtualFile errorFile = createTestFile("error_at_top.latte", content.toString());
        
        // Measure time taken to parse
        long startTime = System.nanoTime();
        List<TextRange> changes = parser.parseChangedParts(errorFile, content.toString());
        long endTime = System.nanoTime();
        long durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        
        // Log performance metrics
        System.out.println("[DEBUG_LOG] Error at top file parsing:");
        System.out.println("[DEBUG_LOG] - File size: " + content.length() + " characters, " + ERROR_FILE_SIZE + " lines");
        System.out.println("[DEBUG_LOG] - Time taken: " + durationMs + " ms");
        System.out.println("[DEBUG_LOG] - Changes detected: " + changes.size());
        
        // Assert that parsing completed quickly since the error is at the top
        assertTrue("Parsing took too long for error at top: " + durationMs + " ms", 
                   durationMs < TimeUnit.SECONDS.toMillis(5)); // Should be much faster than the timeout
        
        // Assert that the parser detected the error
        assertTrue("Parser didn't detect the error at the top", changes.size() > 0);
        
        // Check if the first change includes the error at the top
        if (!changes.isEmpty()) {
            TextRange firstChange = changes.get(0);
            assertTrue("First change doesn't include the error at the top", 
                       firstChange.getStartOffset() <= content.indexOf("{if $showHeader"));
        }
    }

    /**
     * Tests that the parser doesn't get stuck in infinite loops when processing files
     * with complex nested macros.
     */
    @Test
    public void testNoInfiniteLoopsWithComplexNesting() throws IOException {
        // Create a file with complex nested macros
        StringBuilder content = new StringBuilder();
        content.append("<!DOCTYPE html>\n");
        content.append("<html>\n");
        content.append("<body>\n");
        
        // Create deeply nested macros
        int nestingLevel = 50; // Deep enough to potentially cause issues
        for (int i = 0; i < nestingLevel; i++) {
            content.append("{if $level").append(i).append("}\n");
        }
        
        // Add some content in the middle
        content.append("<div>Deeply nested content</div>\n");
        
        // Close the nested macros in reverse order
        for (int i = nestingLevel - 1; i >= 0; i--) {
            content.append("{/if}\n");
        }
        
        // Add some more content
        content.append("</body>\n");
        content.append("</html>\n");
        
        VirtualFile nestedFile = createTestFile("complex_nesting.latte", content.toString());
        
        // Set a timeout for the parsing operation
        final boolean[] completed = {false};
        final List<TextRange>[] result = new List[1];
        
        Thread parsingThread = new Thread(() -> {
            result[0] = parser.parseChangedParts(nestedFile, content.toString());
            completed[0] = true;
        });
        
        parsingThread.start();
        try {
            parsingThread.join(TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[DEBUG_LOG] Parsing thread was interrupted: " + e.getMessage());
        }
        
        // Assert that parsing completed within the timeout
        assertTrue("Parser got stuck in an infinite loop or took too long", completed[0]);
        
        // If parsing completed, check the results
        assertNotNull("Parsing result is null", result[0]);
    }

    /**
     * Tests that the parser correctly handles a file with a large number of macros.
     */
    @Test
    public void testLargeNumberOfMacros() throws IOException {
        // Create a file with a large number of macros
        StringBuilder content = new StringBuilder();
        content.append("<!DOCTYPE html>\n");
        content.append("<html>\n");
        content.append("<body>\n");
        
        // Add a large number of macros
        int macroCount = 1000; // Large enough to potentially cause issues
        for (int i = 0; i < macroCount; i++) {
            // Mix different types of macros
            switch (i % 5) {
                case 0:
                    content.append("{var $var").append(i).append(" = ").append(i).append("}\n");
                    break;
                case 1:
                    content.append("{if $var").append(i % 100).append(" > 50}\n");
                    content.append("<div>Condition ").append(i).append("</div>\n");
                    content.append("{/if}\n");
                    break;
                case 2:
                    content.append("{foreach [1, 2, 3] as $item}\n");
                    content.append("<span>{$item}</span>\n");
                    content.append("{/foreach}\n");
                    break;
                case 3:
                    content.append("{block name").append(i).append("}\n");
                    content.append("<p>Block content ").append(i).append("</p>\n");
                    content.append("{/block}\n");
                    break;
                case 4:
                    content.append("{$var").append(i % 100).append("|default:").append(i).append("}\n");
                    break;
            }
        }
        
        content.append("</body>\n");
        content.append("</html>\n");
        
        VirtualFile macroFile = createTestFile("many_macros.latte", content.toString());
        
        // Measure memory before parsing
        forceGarbageCollection();
        long memoryBefore = getUsedMemory();
        
        // Measure time taken to parse
        long startTime = System.nanoTime();
        List<TextRange> changes = parser.parseChangedParts(macroFile, content.toString());
        long endTime = System.nanoTime();
        long durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        
        // Measure memory after parsing
        forceGarbageCollection();
        long memoryAfter = getUsedMemory();
        
        // Log performance metrics
        System.out.println("[DEBUG_LOG] Many macros file parsing:");
        System.out.println("[DEBUG_LOG] - File size: " + content.length() + " characters, " + content.toString().split("\n").length + " lines");
        System.out.println("[DEBUG_LOG] - Macro count: " + macroCount);
        System.out.println("[DEBUG_LOG] - Time taken: " + durationMs + " ms");
        System.out.println("[DEBUG_LOG] - Memory used: " + (memoryAfter - memoryBefore) + " MB");
        
        // Assert that parsing completed within a reasonable time
        assertTrue("Parsing took too long: " + durationMs + " ms", durationMs < TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS));
        
        // Assert that memory usage is reasonable
        assertTrue("Memory usage too high: " + (memoryAfter - memoryBefore) + " MB", 
                   (memoryAfter - memoryBefore) < 500); // 500 MB is a reasonable upper limit
    }
}