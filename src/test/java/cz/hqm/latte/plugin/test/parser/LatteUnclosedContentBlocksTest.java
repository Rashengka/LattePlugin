package cz.hqm.latte.plugin.test.parser;

import org.junit.Test;

/**
 * Test for verifying that unclosed content block directives at the end of a file
 * are automatically closed and don't trigger the "Top level element is not completed" error.
 * 
 * This test covers the following content block directives:
 * - block
 * - define
 * - snippet
 * - snippetArea
 * - capture
 */
public class LatteUnclosedContentBlocksTest extends LatteUnclosedBlocksTestBase {

    /**
     * Tests that a file with an unclosed block directive at the end doesn't have
     * any PsiErrorElements with the message "Top level element is not completed".
     * Also verifies that specific expected errors are found and no unexpected errors are present.
     * 
     * Note: Expected errors are based on analysis of the file structure and may need
     * to be adjusted based on actual runtime behavior.
     */
    @Test
    public void testUnclosedBlockDirective() {
        doTestUnclosedBlock("macros/unclosed_block_directive.latte", 
            "unclosed block", // Expected error for unclosed block directive
            "div" // Expected error for unclosed div tag
        );
    }

    /**
     * Tests that a file with an unclosed define directive at the end doesn't have
     * any PsiErrorElements with the message "Top level element is not completed".
     * Also verifies that specific expected errors are found and no unexpected errors are present.
     * 
     * Note: Expected errors are based on analysis of the file structure and may need
     * to be adjusted based on actual runtime behavior.
     */
    @Test
    public void testUnclosedDefineDirective() {
        doTestUnclosedBlock("macros/unclosed_define_directive.latte", 
            "unclosed define", // Expected error for unclosed define directive
            "div" // Expected error for unclosed div tag
        );
    }

    /**
     * Tests that a file with an unclosed snippet directive at the end doesn't have
     * any PsiErrorElements with the message "Top level element is not completed".
     * Also verifies that specific expected errors are found and no unexpected errors are present.
     * 
     * Note: Expected errors are based on analysis of the file structure and may need
     * to be adjusted based on actual runtime behavior.
     */
    @Test
    public void testUnclosedSnippetDirective() {
        doTestUnclosedBlock("macros/unclosed_snippet_directive.latte", 
            "unclosed snippet", // Expected error for unclosed snippet directive
            "div" // Expected error for unclosed div tag
        );
    }

    /**
     * Tests that a file with an unclosed snippetArea directive at the end doesn't have
     * any PsiErrorElements with the message "Top level element is not completed".
     * Also verifies that specific expected errors are found and no unexpected errors are present.
     * 
     * Note: Expected errors are based on analysis of the file structure and may need
     * to be adjusted based on actual runtime behavior.
     */
    @Test
    public void testUnclosedSnippetAreaDirective() {
        doTestUnclosedBlock("macros/unclosed_snippetArea_directive.latte", 
            "unclosed snippetArea", // Expected error for unclosed snippetArea directive
            "div" // Expected error for unclosed div tag
        );
    }

    /**
     * Tests that a file with an unclosed capture directive at the end doesn't have
     * any PsiErrorElements with the message "Top level element is not completed".
     * Also verifies that specific expected errors are found and no unexpected errors are present.
     * 
     * Note: Expected errors are based on analysis of the file structure and may need
     * to be adjusted based on actual runtime behavior.
     */
    @Test
    public void testUnclosedCaptureDirective() {
        doTestUnclosedBlock("macros/unclosed_capture_directive.latte", 
            "unclosed capture", // Expected error for unclosed capture directive
            "div" // Expected error for unclosed div tag
        );
    }
}