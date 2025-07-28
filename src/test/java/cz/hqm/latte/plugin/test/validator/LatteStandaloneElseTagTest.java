package cz.hqm.latte.plugin.test.validator;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.junit.Test;

import java.util.Collection;

/**
 * Test for verifying that standalone {else} and {elseif} tags outside of {if} blocks
 * are properly validated and reported as errors.
 */
public class LatteStandaloneElseTagTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "/Users/ragnar/IdeaProjects/LattePlugin/src/test/resources/testData";
    }

    /**
     * Tests that a standalone {else} tag outside of an {if} block is properly
     * validated and reported as an error.
     */
    @Test
    public void testStandaloneElseTag() {
        // Load the test file with a standalone else tag
        PsiFile file = myFixture.configureByFile("macros/standalone_else_tag.latte");

        // Find all PsiErrorElements in the file
        Collection<PsiErrorElement> errorElements = PsiTreeUtil.findChildrenOfType(file, PsiErrorElement.class);

        // Check if there's an error for the standalone else tag
        boolean foundElseTagError = false;
        for (PsiErrorElement errorElement : errorElements) {
            if (errorElement.getErrorDescription().contains("must be inside an {if} block")) {
                foundElseTagError = true;
                break;
            }
        }

        // Assert that there is an error for the standalone else tag
        assertTrue("File should have an error for standalone {else} tag outside of {if} block", foundElseTagError);

        // Print the number of error elements found (for debugging)
        System.out.println("Number of error elements found: " + errorElements.size());
        for (PsiErrorElement errorElement : errorElements) {
            System.out.println("Error: " + errorElement.getErrorDescription());
        }
    }
}