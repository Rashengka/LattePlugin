package cz.hqm.latte.plugin.test.parser;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.openapi.application.PathManager;
import org.junit.Test;

import java.util.Collection;

/**
 * Base test class for verifying that unclosed block directives at the end of a file
 * are automatically closed and don't trigger the "Top level element is not completed" error.
 */
public abstract class LatteUnclosedBlocksTestBase extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "/Users/ragnar/IdeaProjects/LattePlugin/src/test/resources/testData";
    }

    /**
     * Tests that a file with unclosed block directives at the end doesn't have
     * any PsiErrorElements with the message "Top level element is not completed".
     * Also verifies that all expected errors are found and no unexpected errors are present.
     *
     * @param testFilePath The path to the test file relative to the test data path
     * @param expectedErrors Array of expected error descriptions that should be found in the file
     */
    protected void doTestUnclosedBlock(String testFilePath, String... expectedErrors) {
        // Load the test file with unclosed blocks
        PsiFile file = myFixture.configureByFile(testFilePath);

        // Find all PsiErrorElements in the file
        Collection<PsiErrorElement> errorElements = PsiTreeUtil.findChildrenOfType(file, PsiErrorElement.class);

        // Check if any error element has the message "Top level element is not completed"
        boolean hasTopLevelElementNotCompletedError = false;
        for (PsiErrorElement errorElement : errorElements) {
            if (errorElement.getErrorDescription().contains("Top level element is not completed")) {
                hasTopLevelElementNotCompletedError = true;
                break;
            }
        }

        // Assert that there is no "Top level element is not completed" error
        assertFalse("File should not have 'Top level element is not completed' error", hasTopLevelElementNotCompletedError);

        // Verify that all expected errors are found
        for (String expectedError : expectedErrors) {
            boolean found = false;
            for (PsiErrorElement errorElement : errorElements) {
                if (errorElement.getErrorDescription().contains(expectedError)) {
                    found = true;
                    break;
                }
            }
            assertTrue("Expected error not found: " + expectedError, found);
        }

        // Verify that no unexpected errors are present
        for (PsiErrorElement errorElement : errorElements) {
            boolean expected = false;
            for (String expectedError : expectedErrors) {
                if (errorElement.getErrorDescription().contains(expectedError)) {
                    expected = true;
                    break;
                }
            }
            assertTrue("Unexpected error found: " + errorElement.getErrorDescription(), expected);
        }

        // Print the number of error elements found (for debugging)
        System.out.println("Number of error elements found: " + errorElements.size());
        for (PsiErrorElement errorElement : errorElements) {
            System.out.println("Error: " + errorElement.getErrorDescription());
        }
    }
    
    /**
     * Overloaded method for backward compatibility.
     * Tests that a file with unclosed block directives at the end doesn't have
     * any PsiErrorElements with the message "Top level element is not completed".
     *
     * @param testFilePath The path to the test file relative to the test data path
     */
    protected void doTestUnclosedBlock(String testFilePath) {
        doTestUnclosedBlock(testFilePath, new String[0]);
    }
}