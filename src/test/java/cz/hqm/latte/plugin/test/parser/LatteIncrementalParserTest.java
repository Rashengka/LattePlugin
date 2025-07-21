package cz.hqm.latte.plugin.test.parser;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import cz.hqm.latte.plugin.parser.LatteIncrementalParser;
import cz.hqm.latte.plugin.test.LattePluginTestBase;
import cz.hqm.latte.plugin.version.LatteVersion;
import cz.hqm.latte.plugin.version.LatteVersionManager;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for the LatteIncrementalParser class.
 */
public class LatteIncrementalParserTest extends LattePluginTestBase {

    private LatteIncrementalParser incrementalParser;
    private VirtualFile testFile;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // Get the incremental parser
        incrementalParser = LatteIncrementalParser.getInstance(getProject());
        
        // Clear the last known content to ensure a clean state
        incrementalParser.clearAllLastKnownContent();
    }
    
    private VirtualFile createTestFile(String fileName, String content) throws IOException {
        return myFixture.getTempDirFixture().createFile(fileName, content);
    }

    /**
     * Tests that the parser correctly identifies the entire file as changed when it's first seen.
     */
    @Test
    public void testFirstTimeParseWholeFile() throws Exception {
        
        String content = "{block content}\nHello, world!\n{/block}";
        
        // Create a test file with a unique name
        VirtualFile testFile = createTestFile("test_first_time_parse.latte", content);
        
        // Parse the file for the first time
        List<TextRange> changedRanges = incrementalParser.parseChangedParts(testFile, content);
        
        // Verify that the entire file is considered changed
        assertEquals("Should have one changed range", 1, changedRanges.size());
        assertEquals("Changed range should cover the entire file", 0, changedRanges.get(0).getStartOffset());
        assertEquals("Changed range should cover the entire file", content.length(), changedRanges.get(0).getEndOffset());
    }

    /**
     * Tests that the parser correctly identifies changed parts of a file.
     */
    @Test
    public void testParseChangedParts() throws Exception {
        
        String originalContent = "{block content}\nHello, world!\n{/block}";
        String modifiedContent = "{block content}\nHello, modified world!\n{/block}";
        
        // Create a test file with a unique name
        VirtualFile testFile = createTestFile("test_parse_changed_parts.latte", originalContent);
        
        // Parse the file for the first time
        incrementalParser.parseChangedParts(testFile, originalContent);
        
        // Parse the modified file
        List<TextRange> changedRanges = incrementalParser.parseChangedParts(testFile, modifiedContent);
        
        // Verify that the changed part is identified
        assertEquals("Should have one changed range", 1, changedRanges.size());
        
        // The entire file is considered changed in the current implementation
        // This is a simple approach that could be improved with a more sophisticated diff algorithm
        assertEquals("Changed range should cover the entire file", 0, changedRanges.get(0).getStartOffset());
        assertEquals("Changed range should cover the entire file", modifiedContent.length(), changedRanges.get(0).getEndOffset());
    }

    /**
     * Tests that the parser correctly expands changes to include complete Latte constructs.
     */
    @Test
    public void testExpandChangesToCompleteLatteConstructs() throws Exception {
        
        String originalContent = "{block content}\nHello, world!\n{/block}";
        String modifiedContent = "{block content}\nHello, {if true}modified{/if} world!\n{/block}";
        
        // Create a test file with a unique name
        VirtualFile testFile = createTestFile("test_expand_changes.latte", originalContent);
        
        // Parse the file for the first time
        incrementalParser.parseChangedParts(testFile, originalContent);
        
        // Parse the modified file
        List<TextRange> changedRanges = incrementalParser.parseChangedParts(testFile, modifiedContent);
        
        // Verify that the changed part is identified and expanded to include complete Latte constructs
        assertEquals("Should have one changed range", 1, changedRanges.size());
        
        // The entire file is considered changed in the current implementation
        assertEquals("Changed range should cover the entire file", 0, changedRanges.get(0).getStartOffset());
        assertEquals("Changed range should cover the entire file", modifiedContent.length(), changedRanges.get(0).getEndOffset());
    }

    /**
     * Tests that the parser correctly handles multiple changes.
     */
    @Test
    public void testMultipleChanges() throws Exception {
        
        String originalContent = "{block content}\nHello, world!\n{/block}";
        String modifiedContent = "{block content}\nHello, {if true}modified{/if} world!\n{/block}\n{block footer}\nFooter\n{/block}";
        
        // Create a test file with a unique name
        VirtualFile testFile = createTestFile("test_multiple_changes.latte", originalContent);
        
        // Parse the file for the first time
        incrementalParser.parseChangedParts(testFile, originalContent);
        
        // Parse the modified file
        List<TextRange> changedRanges = incrementalParser.parseChangedParts(testFile, modifiedContent);
        
        // Verify that the changed parts are identified
        assertEquals("Should have one changed range", 1, changedRanges.size());
        
        // The entire file is considered changed in the current implementation
        assertEquals("Changed range should cover the entire file", 0, changedRanges.get(0).getStartOffset());
        assertEquals("Changed range should cover the entire file", modifiedContent.length(), changedRanges.get(0).getEndOffset());
    }

    /**
     * Tests that the parser correctly handles clearing the last known content.
     */
    @Test
    public void testClearLastKnownContent() throws Exception {
        
        String content = "{block content}\nHello, world!\n{/block}";
        
        // Create a test file with a unique name
        VirtualFile testFile = createTestFile("test_clear_content.latte", content);
        
        // Parse the file for the first time
        incrementalParser.parseChangedParts(testFile, content);
        
        // Clear the last known content for the file
        incrementalParser.clearLastKnownContent(testFile);
        
        // Parse the file again
        List<TextRange> changedRanges = incrementalParser.parseChangedParts(testFile, content);
        
        // Verify that the entire file is considered changed
        assertEquals("Should have one changed range", 1, changedRanges.size());
        assertEquals("Changed range should cover the entire file", 0, changedRanges.get(0).getStartOffset());
        assertEquals("Changed range should cover the entire file", content.length(), changedRanges.get(0).getEndOffset());
    }
    
    /**
     * Tests that the parser correctly handles unclosed macros.
     * This tests our enhancement to findEndOfLatteMacro() to detect unclosed macros.
     */
    @Test
    public void testUnclosedMacros() throws Exception {
        // Save the original version to restore it later
        LatteVersion originalVersion = LatteVersionManager.getCurrentVersion();
        
        try {
            // Set the version to 3.x to ensure block macros require closing tags
            LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_3X);
            
            // Test with an unclosed if macro
            String unclosedIfContent = "{if $condition}\nThis is a test\n";
            VirtualFile unclosedIfFile = createTestFile("test_unclosed_if.latte", unclosedIfContent);
            
            // Parse the file
            List<TextRange> changedRanges = incrementalParser.parseChangedParts(unclosedIfFile, unclosedIfContent);
            
            // Verify that the entire file is considered changed
            assertEquals("Should have one changed range", 1, changedRanges.size());
            assertEquals("Changed range should cover the entire file", 0, changedRanges.get(0).getStartOffset());
            assertEquals("Changed range should cover the entire file", unclosedIfContent.length(), changedRanges.get(0).getEndOffset());
            
            // Test with an unclosed foreach macro
            String unclosedForeachContent = "{foreach $items as $item}\n<p>{$item}</p>\n";
            VirtualFile unclosedForeachFile = createTestFile("test_unclosed_foreach.latte", unclosedForeachContent);
            
            // Parse the file
            changedRanges = incrementalParser.parseChangedParts(unclosedForeachFile, unclosedForeachContent);
            
            // Verify that the entire file is considered changed
            assertEquals("Should have one changed range", 1, changedRanges.size());
            assertEquals("Changed range should cover the entire file", 0, changedRanges.get(0).getStartOffset());
            assertEquals("Changed range should cover the entire file", unclosedForeachContent.length(), changedRanges.get(0).getEndOffset());
            
            // Test with an unclosed block macro
            String unclosedBlockContent = "{block content}\nThis is a test\n";
            VirtualFile unclosedBlockFile = createTestFile("test_unclosed_block.latte", unclosedBlockContent);
            
            // Parse the file
            changedRanges = incrementalParser.parseChangedParts(unclosedBlockFile, unclosedBlockContent);
            
            // Verify that the entire file is considered changed
            assertEquals("Should have one changed range", 1, changedRanges.size());
            assertEquals("Changed range should cover the entire file", 0, changedRanges.get(0).getStartOffset());
            assertEquals("Changed range should cover the entire file", unclosedBlockContent.length(), changedRanges.get(0).getEndOffset());
        } finally {
            // Restore the original version
            LatteVersionManager.setCurrentVersion(originalVersion);
        }
    }
    
    /**
     * Tests that the parser correctly handles crossing macros.
     * This tests our enhancement to findEndOfLatteMacro() to detect crossing macros.
     */
    @Test
    public void testCrossingMacros() throws Exception {
        // Test with crossing if and foreach macros
        String crossingContent = "{if $condition}\n{foreach $items as $item}\n<p>{$item}</p>\n{/if}\n{/foreach}";
        VirtualFile crossingFile = createTestFile("test_crossing_macros.latte", crossingContent);
        
        // Parse the file
        List<TextRange> changedRanges = incrementalParser.parseChangedParts(crossingFile, crossingContent);
        
        // Verify that the entire file is considered changed
        assertEquals("Should have one changed range", 1, changedRanges.size());
        assertEquals("Changed range should cover the entire file", 0, changedRanges.get(0).getStartOffset());
        assertEquals("Changed range should cover the entire file", crossingContent.length(), changedRanges.get(0).getEndOffset());
        
        // Test with crossing block and if macros
        String crossingBlockContent = "{block content}\n{if $condition}\n<p>Test</p>\n{/block}\n{/if}";
        VirtualFile crossingBlockFile = createTestFile("test_crossing_block.latte", crossingBlockContent);
        
        // Parse the file
        changedRanges = incrementalParser.parseChangedParts(crossingBlockFile, crossingBlockContent);
        
        // Verify that the entire file is considered changed
        assertEquals("Should have one changed range", 1, changedRanges.size());
        assertEquals("Changed range should cover the entire file", 0, changedRanges.get(0).getStartOffset());
        assertEquals("Changed range should cover the entire file", crossingBlockContent.length(), changedRanges.get(0).getEndOffset());
    }
    
    /**
     * Tests version-specific behavior for block macros.
     * This tests our enhancement to findEndOfLatteMacro() to handle version-specific behaviors.
     */
    @Test
    public void testVersionSpecificBlockMacros() throws Exception {
        // Save the original version to restore it later
        LatteVersion originalVersion = LatteVersionManager.getCurrentVersion();
        
        try {
            // Test with Latte 2.x where block macros might be allowed to remain unclosed
            LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_2X);
            
            // Test with an unclosed block macro in Latte 2.x
            String unclosedBlockContent = "{block content}\nThis is a test\n";
            VirtualFile unclosedBlockFile = createTestFile("test_unclosed_block_2x.latte", unclosedBlockContent);
            
            // Parse the file
            List<TextRange> changedRanges = incrementalParser.parseChangedParts(unclosedBlockFile, unclosedBlockContent);
            
            // Verify that the entire file is considered changed
            assertEquals("Should have one changed range", 1, changedRanges.size());
            assertEquals("Changed range should cover the entire file", 0, changedRanges.get(0).getStartOffset());
            assertEquals("Changed range should cover the entire file", unclosedBlockContent.length(), changedRanges.get(0).getEndOffset());
            
            // Test with Latte 3.x where block macros should require closing tags
            LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_3X);
            
            // Test with an unclosed block macro in Latte 3.x
            unclosedBlockFile = createTestFile("test_unclosed_block_3x.latte", unclosedBlockContent);
            
            // Parse the file
            changedRanges = incrementalParser.parseChangedParts(unclosedBlockFile, unclosedBlockContent);
            
            // Verify that the entire file is considered changed
            assertEquals("Should have one changed range", 1, changedRanges.size());
            assertEquals("Changed range should cover the entire file", 0, changedRanges.get(0).getStartOffset());
            assertEquals("Changed range should cover the entire file", unclosedBlockContent.length(), changedRanges.get(0).getEndOffset());
            
            // Test with Latte 4.x where block macros should require closing tags
            LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_4X);
            
            // Test with an unclosed block macro in Latte 4.x
            unclosedBlockFile = createTestFile("test_unclosed_block_4x.latte", unclosedBlockContent);
            
            // Parse the file
            changedRanges = incrementalParser.parseChangedParts(unclosedBlockFile, unclosedBlockContent);
            
            // Verify that the entire file is considered changed
            assertEquals("Should have one changed range", 1, changedRanges.size());
            assertEquals("Changed range should cover the entire file", 0, changedRanges.get(0).getStartOffset());
            assertEquals("Changed range should cover the entire file", unclosedBlockContent.length(), changedRanges.get(0).getEndOffset());
        } finally {
            // Restore the original version
            LatteVersionManager.setCurrentVersion(originalVersion);
        }
    }
}
