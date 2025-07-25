package cz.hqm.latte.plugin.test.parser;

import org.junit.Test;

/**
 * Test for verifying that unclosed loop block directives at the end of a file
 * are automatically closed and don't trigger the "Top level element is not completed" error.
 * 
 * This test covers the following loop block directives:
 * - foreach
 * - for
 * - while
 */
public class LatteUnclosedLoopBlocksTest extends LatteUnclosedBlocksTestBase {

    /**
     * Tests that a file with an unclosed foreach directive at the end doesn't have
     * any PsiErrorElements with the message "Top level element is not completed".
     * Also verifies that specific expected errors are found and no unexpected errors are present.
     * 
     * Note: Expected errors are based on analysis of the file structure and may need
     * to be adjusted based on actual runtime behavior.
     */
    @Test
    public void testUnclosedForeachDirective() {
        doTestUnclosedBlock("macros/unclosed_foreach_directive.latte", 
            "unclosed foreach", // Expected error for unclosed foreach directive
            "div" // Expected error for unclosed div tag
        );
    }

    /**
     * Tests that a file with an unclosed for directive at the end doesn't have
     * any PsiErrorElements with the message "Top level element is not completed".
     * Also verifies that specific expected errors are found and no unexpected errors are present.
     * 
     * Note: Expected errors are based on analysis of the file structure and may need
     * to be adjusted based on actual runtime behavior.
     */
    @Test
    public void testUnclosedForDirective() {
        doTestUnclosedBlock("macros/unclosed_for_directive.latte", 
            "unclosed for", // Expected error for unclosed for directive
            "div" // Expected error for unclosed div tag
        );
    }

    /**
     * Tests that a file with an unclosed while directive at the end doesn't have
     * any PsiErrorElements with the message "Top level element is not completed".
     * Also verifies that specific expected errors are found and no unexpected errors are present.
     * 
     * Note: Expected errors are based on analysis of the file structure and may need
     * to be adjusted based on actual runtime behavior.
     */
    @Test
    public void testUnclosedWhileDirective() {
        doTestUnclosedBlock("macros/unclosed_while_directive.latte", 
            "unclosed while", // Expected error for unclosed while directive
            "div" // Expected error for unclosed div tag
        );
    }
}