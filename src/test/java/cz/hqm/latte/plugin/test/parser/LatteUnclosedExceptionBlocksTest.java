package cz.hqm.latte.plugin.test.parser;

import org.junit.Test;

/**
 * Test for verifying that unclosed exception handling block directives at the end of a file
 * are automatically closed and don't trigger the "Top level element is not completed" error.
 * 
 * This test covers the following exception handling block directives:
 * - try
 * - catch
 */
public class LatteUnclosedExceptionBlocksTest extends LatteUnclosedBlocksTestBase {

    /**
     * Tests that a file with an unclosed try directive at the end doesn't have
     * any PsiErrorElements with the message "Top level element is not completed".
     * Also verifies that specific expected errors are found and no unexpected errors are present.
     * 
     * Note: Expected errors are based on analysis of the file structure and may need
     * to be adjusted based on actual runtime behavior.
     */
    @Test
    public void testUnclosedTryDirective() {
        doTestUnclosedBlock("macros/unclosed_try_directive.latte", 
            "unclosed try", // Expected error for unclosed try directive
            "div" // Expected error for unclosed div tag
        );
    }

    /**
     * Tests that a file with an unclosed catch directive at the end doesn't have
     * any PsiErrorElements with the message "Top level element is not completed".
     * Also verifies that specific expected errors are found and no unexpected errors are present.
     * 
     * Note: Expected errors are based on analysis of the file structure and may need
     * to be adjusted based on actual runtime behavior.
     */
    @Test
    public void testUnclosedCatchDirective() {
        doTestUnclosedBlock("macros/unclosed_catch_directive.latte", 
            "unclosed catch", // Expected error for unclosed catch directive
            "div" // Expected error for unclosed div tag
        );
    }
}