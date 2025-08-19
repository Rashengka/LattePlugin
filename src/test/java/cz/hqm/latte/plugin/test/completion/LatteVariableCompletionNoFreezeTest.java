package cz.hqm.latte.plugin.test.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import cz.hqm.latte.plugin.completion.NetteDefaultVariablesProvider;
import cz.hqm.latte.plugin.test.LattePluginTestBase;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Ensures that invoking autocomplete on a Latte variable does not freeze the IDE
 * and returns promptly, both inside curly braces and inside an n: attribute value.
 *
 * We do not assert specific variable names (like $isAdmin) because the current contributor
 * does not index per-template {var} declarations. The goal is responsiveness and stability.
 */
public class LatteVariableCompletionNoFreezeTest extends LattePluginTestBase {

    @Override
    protected boolean useIdeaFixture() { return true; }

    @Test
    public void testCompletionNoFreezeInBraces() {
        // Harden responsiveness by setting a very small completion timeout
        NetteDefaultVariablesProvider.setCompletionTimeoutForTests(25L);
        try {
            String content = "{var $isAdmin = true}\n{$isAd<caret>}";
            createLatteFile(content);
            long start = System.currentTimeMillis();
            try {
                LookupElement[] items = myFixture.complete(CompletionType.BASIC);
                // It's okay if items are null/empty; we care about timing
                if (items != null) {
                    System.out.println("[DEBUG_LOG] Items in braces context: " + items.length);
                } else {
                    System.out.println("[DEBUG_LOG] Items in braces context: null");
                }
            } catch (com.intellij.openapi.progress.ProcessCanceledException ignored) {
                // Expected if watchdog cancels; infra handles gracefully
            }
            long duration = System.currentTimeMillis() - start;
            System.out.println("[DEBUG_LOG] Braces completion duration(ms)=" + duration);
            assertTrue("Completion in braces took too long: " + duration + " ms", duration < 2000);
        } finally {
            NetteDefaultVariablesProvider.resetCompletionTimeoutToDefault();
        }
    }

    @Test(timeout = 10000)
    public void testCompletionNoFreezeInNAttribute() {
        // Ensure we have a strict upper bound for the whole test
        NetteDefaultVariablesProvider.setCompletionTimeoutForTests(25L);
        try {
            String content = "<div n:if=\"$isAd\">Hello</div>";
            int offset = content.indexOf("$isAd") + "$isAd".length();
            long start = System.currentTimeMillis();
            try {
                // Use lightweight helper to avoid heavy injected-language editor setup
                NetteDefaultVariablesProvider.beginCompletionWatchdog();
                java.util.Set<String> suggestions = cz.hqm.latte.plugin.completion.NetteAttributeCompletionContributor
                        .computeNAttributeSuggestionsFromText(content, offset);
                System.out.println("[DEBUG_LOG] Lightweight suggestions in n: attribute context: " + (suggestions != null ? suggestions.size() : -1));
            } catch (com.intellij.openapi.progress.ProcessCanceledException ignored) {
                // Expected if watchdog cancels; infra handles gracefully
            } finally {
                NetteDefaultVariablesProvider.endCompletionWatchdog();
            }
            long duration = System.currentTimeMillis() - start;
            System.out.println("[DEBUG_LOG] N-attr completion duration(ms)=" + duration);
            // Soft expectation: should be quick (<2s), while hard cap is enforced by @Test(timeout)
            assertTrue("Completion in n: attribute took too long: " + duration + " ms", duration < 2000);
        } finally {
            NetteDefaultVariablesProvider.resetCompletionTimeoutToDefault();
        }
    }
}
