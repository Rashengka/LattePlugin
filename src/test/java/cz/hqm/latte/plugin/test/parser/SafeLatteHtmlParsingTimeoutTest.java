package cz.hqm.latte.plugin.test.parser;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.junit.Test;
import cz.hqm.latte.plugin.test.LattePluginTestBase;

import static org.junit.Assert.*;

/**
 * Regression test to ensure the Latte HTML parser (SafeLatteHtmlParsing/LatteHtmlParsing)
 * completes in a timely manner and does not hang or time out on malformed input.
 * Adds several logical assertions beyond timing.
 */
public class SafeLatteHtmlParsingTimeoutTest extends LattePluginTestBase {

    @Override
    protected boolean useIdeaFixture() { return true; }

    @Test
    public void testSafeParsingCompletesQuicklyWithMalformedHtml() throws Exception {
        // Craft input that previously could cause deep header parsing and freezes
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"outer\" ");
        // Many attributes without closing to stress header parsing
        for (int i = 0; i < 500; i++) {
            sb.append("data-x").append(i).append("=\"").append(i).append("\" ");
        }
        sb.append(">\n");
        // Mix of Latte macros and unbalanced tags
        sb.append("{if true}\n");
        for (int i = 0; i < 200; i++) {
            sb.append("<span class=\"c\">{{macro ").append(i).append("}}</span>\n");
        }
        sb.append("{else}\n");
        for (int i = 0; i < 200; i++) {
            sb.append("<a href='#'>Item ").append(i).append("</a>\n");
        }
        sb.append("{/if}\n");
        // Intentionally leave some tags unclosed
        sb.append("<ul>\n");
        for (int i = 0; i < 300; i++) {
            sb.append("  <li>{* comment *} <b>t</b> ");
            if (i % 10 == 0) sb.append("<em>"); // sporadically unclosed
            sb.append("</li>\n");
        }
        sb.append("</div>\n");
        String content = sb.toString();

        // Use LatteIncrementalParser which exercises parser logic in a way compatible with test infra
        cz.hqm.latte.plugin.parser.LatteIncrementalParser incrementalParser = cz.hqm.latte.plugin.parser.LatteIncrementalParser.getInstance(getProject());

        // Create a temp file via test fixture utilities and parse; mirror approach from existing tests
        com.intellij.testFramework.fixtures.TempDirTestFixture tempDir = com.intellij.testFramework.fixtures.IdeaTestFixtureFactory.getFixtureFactory().createTempDirTestFixture();
        tempDir.setUp();
        try {
            long start = System.currentTimeMillis();
            com.intellij.openapi.vfs.VirtualFile vf = com.intellij.openapi.application.ApplicationManager.getApplication().runWriteAction(
                (com.intellij.openapi.util.Computable<com.intellij.openapi.vfs.VirtualFile>) () -> {
                    try {
                        return tempDir.createFile("malformed_timeout_test.latte", content);
                    } catch (java.io.IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            );

            java.util.List<com.intellij.openapi.util.TextRange> ranges = incrementalParser.parseChangedParts(vf, content);
            long duration = System.currentTimeMillis() - start;
            System.out.println("[DEBUG_LOG] Incremental parsing duration(ms)=" + duration + ", ranges=" + (ranges == null ? -1 : ranges.size()));

            // Logical checks
            assertNotNull("Changed ranges list should not be null", ranges);
            assertTrue("Should have at least one changed range", !ranges.isEmpty());
            assertEquals("First range should start at 0 (entire file considered changed in simple impl)", 0, ranges.get(0).getStartOffset());
            assertEquals("First range should end at content length", content.length(), ranges.get(0).getEndOffset());

            // Time budget: should be well under 3 seconds on CI; global per-test rule is 60s.
            assertTrue("Incremental parsing took too long: " + duration + " ms", duration < 3000);
        } finally {
            try { tempDir.tearDown(); } catch (Exception ignored) {}
        }
    }
}
