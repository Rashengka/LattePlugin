package cz.hqm.latte.plugin.test.parser;

import org.junit.Test;

/**
 * Test for verifying that unclosed conditional block directives at the end of a file
 * are automatically closed and don't trigger the "Top level element is not completed" error.
 * 
 * This test covers the following conditional block directives:
 * - if
 * - elseif
 * - else
 */
public class LatteUnclosedConditionalBlocksTest extends LatteUnclosedBlocksTestBase {

    /**
     * Tests that a file with an unclosed if directive at the end doesn't have
     * any PsiErrorElements with the message "Top level element is not completed".
     * Also verifies that specific expected errors are found and no unexpected errors are present.
     * 
     * Note: Expected errors are based on analysis of the file structure and may need
     * to be adjusted based on actual runtime behavior.
     */
    @Test
    public void testUnclosedIfDirective() {
        doTestUnclosedBlock("macros/unclosed_if_directive.latte", 
            "unclosed if", // Expected error for unclosed if directive
            "div" // Expected error for unclosed div tag
        );
    }

    /**
     * Tests that a file with an unclosed elseif directive at the end doesn't have
     * any PsiErrorElements with the message "Top level element is not completed".
     * Also verifies that specific expected errors are found and no unexpected errors are present.
     * 
     * Note: Expected errors are based on analysis of the file structure and may need
     * to be adjusted based on actual runtime behavior.
     */
    @Test
    public void testUnclosedElseifDirective() {
        doTestUnclosedBlock("macros/unclosed_elseif_directive.latte", 
            "unclosed elseif", // Expected error for unclosed elseif directive
            "div" // Expected error for unclosed div tag
        );
    }

    /**
     * Tests that a file with an unclosed else directive at the end doesn't have
     * any PsiErrorElements with the message "Top level element is not completed".
     * Also verifies that specific expected errors are found and no unexpected errors are present.
     * 
     * Note: Expected errors are based on analysis of the file structure and may need
     * to be adjusted based on actual runtime behavior.
     */
    @Test
    public void testUnclosedElseDirective() {
        doTestUnclosedBlock("macros/unclosed_else_directive.latte", 
            "unclosed else", // Expected error for unclosed else directive
            "div" // Expected error for unclosed div tag
        );
    }
}