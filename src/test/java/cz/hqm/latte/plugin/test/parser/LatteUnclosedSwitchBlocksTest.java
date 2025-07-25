package cz.hqm.latte.plugin.test.parser;

import org.junit.Test;

/**
 * Test for verifying that unclosed switch block directives at the end of a file
 * are automatically closed and don't trigger the "Top level element is not completed" error.
 * 
 * This test covers the following switch block directives:
 * - switch
 * - case
 * - default
 */
public class LatteUnclosedSwitchBlocksTest extends LatteUnclosedBlocksTestBase {

    /**
     * Tests that a file with an unclosed switch directive at the end doesn't have
     * any PsiErrorElements with the message "Top level element is not completed".
     * Also verifies that specific expected errors are found and no unexpected errors are present.
     * 
     * Note: Expected errors are based on analysis of the file structure and may need
     * to be adjusted based on actual runtime behavior.
     */
    @Test
    public void testUnclosedSwitchDirective() {
        doTestUnclosedBlock("macros/unclosed_switch_directive.latte", 
            "unclosed switch", // Expected error for unclosed switch directive
            "div" // Expected error for unclosed div tag
        );
    }

    /**
     * Tests that a file with an unclosed case directive at the end doesn't have
     * any PsiErrorElements with the message "Top level element is not completed".
     * Also verifies that specific expected errors are found and no unexpected errors are present.
     * 
     * Note: Expected errors are based on analysis of the file structure and may need
     * to be adjusted based on actual runtime behavior.
     */
    @Test
    public void testUnclosedCaseDirective() {
        doTestUnclosedBlock("macros/unclosed_case_directive.latte", 
            "unclosed case", // Expected error for unclosed case directive
            "div" // Expected error for unclosed div tag
        );
    }

    /**
     * Tests that a file with an unclosed default directive at the end doesn't have
     * any PsiErrorElements with the message "Top level element is not completed".
     * Also verifies that specific expected errors are found and no unexpected errors are present.
     * 
     * Note: Expected errors are based on analysis of the file structure and may need
     * to be adjusted based on actual runtime behavior.
     */
    @Test
    public void testUnclosedDefaultDirective() {
        doTestUnclosedBlock("macros/unclosed_default_directive.latte", 
            "unclosed default", // Expected error for unclosed default directive
            "div" // Expected error for unclosed div tag
        );
    }
}