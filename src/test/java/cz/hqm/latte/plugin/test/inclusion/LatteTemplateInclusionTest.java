package cz.hqm.latte.plugin.test.inclusion;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import cz.hqm.latte.plugin.test.LattePluginTestBase;
import cz.hqm.latte.plugin.inclusion.LatteTemplateInclusionHandler;
import cz.hqm.latte.plugin.inclusion.LatteBlockTypeProvider;
import cz.hqm.latte.plugin.version.LatteVersion;
import cz.hqm.latte.plugin.version.LatteVersionManager;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.List;

/**
 * Tests for the LatteTemplateInclusionHandler class.
 * Verifies that template inclusion and inheritance are correctly handled.
 */
public class LatteTemplateInclusionTest extends LattePluginTestBase {

    private LatteVersion originalVersion;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // Save the original version
        originalVersion = LatteVersionManager.getCurrentVersion();
        
        // Set the version to 3.x to ensure all inclusion features are supported
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_3X);
    }
    
    @Override
    protected void tearDown() throws Exception {
        // Restore the original version
        LatteVersionManager.setCurrentVersion(originalVersion);
        
        super.tearDown();
    }

    /**
     * Tests that the findIncludeTargetFile method correctly finds the target file for an {include} tag.
     */
    @Test
    public void testFindIncludeTargetFile() {
        // Create a test file with an {include} tag
        createLatteFile("{include 'header.latte'}");
        
        // Create the target file
        myFixture.addFileToProject("header.latte", "<header>Header content</header>");
        
        // Find the target file
        PsiFile targetFile = LatteTemplateInclusionHandler.findIncludeTargetFile(
            getProject(), myFixture.getFile(), "{include 'header.latte'}"
        );
        
        // Verify that the target file is found
        assertNotNull("Target file should be found", targetFile);
        assertEquals("Target file should have the correct name", "header.latte", targetFile.getName());
    }
    
    /**
     * Tests that the findIncludeTargetBlock method correctly finds the target block for an {include #blockName} tag.
     */
    @Test
    public void testFindIncludeTargetBlock() {
        // Create a test file with a block and an {include #blockName} tag
        createLatteFile(
            "{block content}\n" +
            "Block content\n" +
            "{/block}\n" +
            "{include #content}"
        );
        
        // Find the target block
        PsiElement targetBlock = LatteTemplateInclusionHandler.findIncludeTargetBlock(
            getProject(), myFixture.getFile(), "{include #content}"
        );
        
        // Verify that the target block is found
        assertNotNull("Target block should be found", targetBlock);
        assertTrue("Target block should contain the block name", 
                  targetBlock.getText().contains("content"));
    }
    
    /**
     * Tests that the findIncludeBlockTargetFile method correctly finds the target file for an {includeBlock} tag.
     */
    @Test
    public void testFindIncludeBlockTargetFile() {
        // Create a test file with an {includeBlock} tag
        createLatteFile("{includeBlock 'blocks.latte'}");
        
        // Create the target file
        myFixture.addFileToProject("blocks.latte", 
            "{block content}Block content{/block}\n" +
            "{block sidebar}Sidebar content{/block}"
        );
        
        // Find the target file
        PsiFile targetFile = LatteTemplateInclusionHandler.findIncludeBlockTargetFile(
            getProject(), myFixture.getFile(), "{includeBlock 'blocks.latte'}"
        );
        
        // Verify that the target file is found
        assertNotNull("Target file should be found", targetFile);
        assertEquals("Target file should have the correct name", "blocks.latte", targetFile.getName());
    }
    
    /**
     * Tests that the findSandboxTargetFile method correctly finds the target file for a {sandbox} tag.
     */
    @Test
    public void testFindSandboxTargetFile() {
        // Create a test file with a {sandbox} tag
        createLatteFile("{sandbox 'untrusted.latte'}");
        
        // Create the target file
        myFixture.addFileToProject("untrusted.latte", "Untrusted content");
        
        // Find the target file
        PsiFile targetFile = LatteTemplateInclusionHandler.findSandboxTargetFile(
            getProject(), myFixture.getFile(), "{sandbox 'untrusted.latte'}"
        );
        
        // Verify that the target file is found
        assertNotNull("Target file should be found", targetFile);
        assertEquals("Target file should have the correct name", "untrusted.latte", targetFile.getName());
    }
    
    /**
     * Tests that the findBlockInFile method correctly finds a block in a file.
     */
    @Test
    public void testFindBlockInFile() {
        // Create a test file with blocks
        createLatteFile(
            "{block content}\n" +
            "Block content\n" +
            "{/block}\n" +
            "{block sidebar}\n" +
            "Sidebar content\n" +
            "{/block}\n" +
            "{define modal}\n" +
            "Modal content\n" +
            "{/define}"
        );
        
        // Find the blocks
        PsiElement contentBlock = LatteTemplateInclusionHandler.findBlockInFile(
            myFixture.getFile(), "content"
        );
        PsiElement sidebarBlock = LatteTemplateInclusionHandler.findBlockInFile(
            myFixture.getFile(), "sidebar"
        );
        PsiElement modalBlock = LatteTemplateInclusionHandler.findBlockInFile(
            myFixture.getFile(), "modal"
        );
        
        // Verify that the blocks are found
        assertNotNull("Content block should be found", contentBlock);
        assertNotNull("Sidebar block should be found", sidebarBlock);
        assertNotNull("Modal block should be found", modalBlock);
        
        assertTrue("Content block should contain the block name", 
                  contentBlock.getText().contains("content"));
        assertTrue("Sidebar block should contain the block name", 
                  sidebarBlock.getText().contains("sidebar"));
        assertTrue("Modal block should contain the block name", 
                  modalBlock.getText().contains("modal"));
    }
    
    /**
     * Tests that the findBlocksInFile method correctly finds all blocks in a file.
     */
    @Test
    public void testFindBlocksInFile() {
        // Create a test file with blocks
        createLatteFile(
            "{block content}\n" +
            "Block content\n" +
            "{/block}\n" +
            "{block sidebar}\n" +
            "Sidebar content\n" +
            "{/block}\n" +
            "{define modal}\n" +
            "Modal content\n" +
            "{/define}"
        );
        
        // Find all blocks
        List<String> blocks = LatteTemplateInclusionHandler.findBlocksInFile(myFixture.getFile());
        
        // Verify that all blocks are found
        assertNotNull("Blocks should not be null", blocks);
        assertEquals("There should be 3 blocks", 3, blocks.size());
        assertTrue("Blocks should contain 'content'", blocks.contains("content"));
        assertTrue("Blocks should contain 'sidebar'", blocks.contains("sidebar"));
        assertTrue("Blocks should contain 'modal'", blocks.contains("modal"));
    }
    
    /**
     * Tests that {includeBlock} is not supported in Latte 4.x.
     */
    @Test
    public void testIncludeBlockNotSupportedInLatte4x() {
        // Set the version to 4.x
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_4X);
        
        // Create a test file with an {includeBlock} tag
        createLatteFile("{includeBlock 'blocks.latte'}");
        
        // Create the target file
        myFixture.addFileToProject("blocks.latte", 
            "{block content}Block content{/block}\n" +
            "{block sidebar}Sidebar content{/block}"
        );
        
        // Find the target file
        PsiFile targetFile = LatteTemplateInclusionHandler.findIncludeBlockTargetFile(
            getProject(), myFixture.getFile(), "{includeBlock 'blocks.latte'}"
        );
        
        // Verify that the target file is not found because {includeBlock} is not supported in Latte 4.x
        assertNull("Target file should not be found in Latte 4.x", targetFile);
    }
}