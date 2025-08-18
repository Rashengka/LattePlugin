package cz.hqm.latte.plugin.test.completion;

import com.intellij.codeInsight.completion.CompletionType;
import org.junit.Test;
import cz.hqm.latte.plugin.test.LattePluginTestBase;
import cz.hqm.latte.plugin.completion.NetteDefaultVariablesProvider;

/**
 * Verifies that Latte variable completion does not freeze and respects a time budget watchdog.
 */
public class NetteVariableCompletionTimeoutTest extends LattePluginTestBase {

    @Override
    protected boolean useIdeaFixture() { return true; }

    @Test
    public void testVariableCompletionRespectsTimeout() {
        // Set a very small timeout to force fast cancellation if anything blocks
        NetteDefaultVariablesProvider.setCompletionTimeoutForTests(25L);
        try {
            createLatteFile("{$<caret>}");
            long start = System.currentTimeMillis();
            try {
                myFixture.complete(CompletionType.BASIC);
            } catch (com.intellij.openapi.progress.ProcessCanceledException ignored) {
                // Expected when timeout triggers; completion infra handles this gracefully
            }
            long duration = System.currentTimeMillis() - start;
            System.out.println("[DEBUG_LOG] Variable completion duration(ms)=" + duration);
            // Assert that completion returns fairly quickly (well under 2 seconds)
            assertTrue("Completion took too long: " + duration + " ms", duration < 2000);
        } finally {
            // Reset to default to not impact other tests
            NetteDefaultVariablesProvider.resetCompletionTimeoutToDefault();
        }
    }
}
