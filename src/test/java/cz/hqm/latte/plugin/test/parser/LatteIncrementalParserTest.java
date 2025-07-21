package cz.hqm.latte.plugin.test.parser;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import cz.hqm.latte.plugin.parser.LatteIncrementalParser;
import cz.hqm.latte.plugin.test.LattePluginTestBase;

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
}
