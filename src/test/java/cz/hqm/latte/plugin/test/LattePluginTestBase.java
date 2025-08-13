package cz.hqm.latte.plugin.test;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.openapi.application.PathManager;
import cz.hqm.latte.plugin.test.util.TestErrorHandler;
import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.junit.rules.TestRule;
import cz.hqm.latte.plugin.test.util.TestTimingRule;
import cz.hqm.latte.plugin.test.util.TestMemoryRule;
import cz.hqm.latte.plugin.test.util.TestOutputCaptureRule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Base class for all Latte plugin tests.
 * Extends BasePlatformTestCase which provides the necessary infrastructure for testing IntelliJ IDEA plugins.
 * Uses JUnit 4 annotations and assertions.
 */
public abstract class LattePluginTestBase extends BasePlatformTestCase {

    private static final ThreadLocal<Boolean> OUTPUT_CAPTURE_ACTIVE = new ThreadLocal<>();

    // Enforce a maximum duration of 60 seconds per test to catch hangs/loops
    @Rule
    public Timeout globalTimeout = new Timeout(60, TimeUnit.SECONDS);

    // Capture full console output (stdout/stderr) for each test method into per-method files
    @Rule
    public TestRule outputCaptureRule = new TestOutputCaptureRule();

    // Per-test timing measurement and logging for all tests
    @Rule
    public TestRule timingRule = new TestTimingRule();

    // Per-test memory measurement and logging for all tests
    @Rule
    public TestRule memoryRule = new TestMemoryRule();

    /**
     * Override in subclasses that don't need IntelliJ IDEA fixture (Project, PSI, myFixture, etc.).
     * When false, BasePlatformTestCase#setUp() won't be called to avoid heavy initialization.
     */
    protected boolean useIdeaFixture() { return true; }

    static {
        // Set headless/font-safe properties as early as possible (before IDEA Application initialization)
        System.setProperty("java.awt.headless", "true");
        System.setProperty("idea.use.headless.ui", "true");
        System.setProperty("idea.force.use.core.fonts", "true");
        System.setProperty("idea.font.system.disable", "true");
        System.setProperty("idea.use.mock.ui", "true");
        System.setProperty("idea.use.minimal.fonts", "true");
        // Start redirecting standard error as early as possible
        TestErrorHandler.startRedirecting();
        System.out.println("[DEBUG_LOG] LattePluginTestBase.<clinit>: headless/font-safe properties set.");
    }

    @Override
    protected void setUp() throws Exception {
        // Ensure error redirection is active
        TestErrorHandler.startRedirecting();

        // Enable headless/core-font safe mode to avoid AWT/font initialization issues in tests
        // See docs/testing/RUNNING_TESTS_IN_INTELLIJ.md
        System.setProperty("java.awt.headless", "true");
        System.setProperty("idea.use.headless.ui", "true");
        System.setProperty("idea.force.use.core.fonts", "true");
        System.setProperty("idea.font.system.disable", "true");
        System.setProperty("idea.use.mock.ui", "true");
        System.setProperty("idea.use.minimal.fonts", "true");

        // Set multiple system properties to completely disable language injection
        // This prevents the AssertionError in InjectedLanguageManagerImpl.pushInjectors
        System.setProperty("idea.ignore.duplicated.injectors", "true");
        System.setProperty("idea.disable.language.injection", "true");
        System.setProperty("idea.injected.language.manager.disabled", "true");
        System.setProperty("idea.skip.injected.language.setup", "true");
        System.setProperty("idea.test.no.injected.language", "true");
        System.setProperty("idea.test.light.injected.language.manager", "true");
        System.setProperty("idea.test.disable.language.injection", "true");

        System.out.println("[DEBUG_LOG] LattePluginTestBase.setUp() completed with headless/font-safe properties.");
        
        if (useIdeaFixture()) {
            super.setUp();
        }
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            super.tearDown();
            // We don't stop redirecting here to ensure errors during tearDown are also formatted
            
            // Reset all system properties for language injectors
            // This ensures that the properties don't affect other tests
            System.clearProperty("idea.ignore.duplicated.injectors");
            System.clearProperty("idea.disable.language.injection");
            System.clearProperty("idea.injected.language.manager.disabled");
            System.clearProperty("idea.skip.injected.language.setup");
            System.clearProperty("idea.test.no.injected.language");
            System.clearProperty("idea.test.light.injected.language.manager");
            System.clearProperty("idea.test.disable.language.injection");
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Exception during tearDown: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Ensure per-method console capture even when JUnit Rules are not applied (fallback using JUnit4 @Before/@After)
    private PrintStream __origOut;
    private PrintStream __origErr;
    private FileOutputStream __fos;
    private PrintStream __fileOut;
    private PrintStream __fileErr;
    private TeePrintStream __teeOut;
    private TeePrintStream __teeErr;
    private String __className;
    private String __methodName;
    private String __timestamp;

    @Before
    public void __startOutputCapture() throws Exception {
        if (Boolean.TRUE.equals(OUTPUT_CAPTURE_ACTIVE.get())) {
            return;
        }
        OUTPUT_CAPTURE_ACTIVE.set(Boolean.TRUE);
        __origOut = System.out;
        __origErr = System.err;
        __timestamp = System.getProperty("latte.plugin.test.timestamp");
        if (__timestamp == null || __timestamp.isEmpty()) {
            __timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        }
        __className = getClass().getSimpleName();
        __methodName = getName();
        if (__className == null || __className.isEmpty()) __className = "UnknownClass";
        if (__methodName == null || __methodName.isEmpty()) __methodName = "unknownMethod";
        __className = __className.replaceAll("[^a-zA-Z0-9_.-]", "_");
        __methodName = __methodName.replaceAll("[^a-zA-Z0-9_.-]", "_");

        File baseDir = new File(System.getProperty("user.dir") + File.separator + "log" + File.separator + "test_" + __timestamp);
        File classDir = new File(baseDir, __className);
        if (!classDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            classDir.mkdirs();
        }
        File destFile = new File(classDir, __methodName + ".console.log");

        try {
            __fos = new FileOutputStream(destFile, true);
            String header = String.format("=== Console for %s.%s at %s ===%n",
                    __className, __methodName, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
            __fos.write(header.getBytes());
            __fos.flush();

            __fileOut = new PrintStream(new PrefixingOutputStream(__fos, "[STDOUT] "), true);
            __fileErr = new PrintStream(new PrefixingOutputStream(__fos, "[STDERR] "), true);
            __teeOut = new TeePrintStream(__origOut, __fileOut);
            __teeErr = new TeePrintStream(__origErr, __fileErr);

            System.setOut(__teeOut);
            System.setErr(__teeErr);
        } catch (IOException ioe) {
            System.out.println("[DEBUG_LOG] __startOutputCapture IO error: " + ioe.getMessage());
            OUTPUT_CAPTURE_ACTIVE.remove();
            // Keep originals in place; continue tests without file capture
        }
    }

    @After
    public void __stopOutputCapture() throws Exception {
        if (!Boolean.TRUE.equals(OUTPUT_CAPTURE_ACTIVE.get())) {
            return;
        }
        try {
            if (__fos != null) {
                String footer = String.format("=== End of console for %s.%s (END) ===%n%n", __className, __methodName);
                try { __fos.write(footer.getBytes()); __fos.flush(); } catch (IOException ignored) {}
            }
        } finally {
            if (__origOut != null) System.setOut(__origOut);
            if (__origErr != null) System.setErr(__origErr);
            try { if (__teeOut != null) __teeOut.flush(); } catch (Exception ignored) {}
            try { if (__teeErr != null) __teeErr.flush(); } catch (Exception ignored) {}
            try { if (__fileOut != null) __fileOut.flush(); } catch (Exception ignored) {}
            try { if (__fileErr != null) __fileErr.flush(); } catch (Exception ignored) {}
            try { if (__fileOut != null) __fileOut.close(); } catch (Exception ignored) {}
            try { if (__fileErr != null) __fileErr.close(); } catch (Exception ignored) {}
            try { if (__fos != null) __fos.close(); } catch (Exception ignored) {}
            OUTPUT_CAPTURE_ACTIVE.remove();
        }
    }

    /**
     * Returns the path to the test data directory.
     * Test data files should be placed in this directory.
     */
    @Override
    protected String getTestDataPath() {
        return System.getProperty("user.dir") + "/src/test/resources/testData";
    }

    /**
     * Creates a Latte file with the given content.
     *
     * @param content The content of the Latte file
     */
    protected void createLatteFile(String content) {
        myFixture.configureByText("test.latte", content);
    }

    // Minimal inner classes to support teeing streams without relying on JUnit Rules
    private static final class PrefixingOutputStream extends OutputStream {
        private final OutputStream delegate;
        private final byte[] prefixBytes;
        private boolean atLineStart = true;
        PrefixingOutputStream(OutputStream delegate, String prefix) {
            this.delegate = delegate;
            this.prefixBytes = prefix.getBytes();
        }
        @Override
        public synchronized void write(int b) throws IOException {
            if (atLineStart) {
                delegate.write(prefixBytes);
                atLineStart = false;
            }
            delegate.write(b);
            if (b == '\n') {
                atLineStart = true;
            }
        }
        @Override
        public synchronized void write(byte[] b, int off, int len) throws IOException {
            for (int i = 0; i < len; i++) {
                write(b[off + i]);
            }
        }
    }

    private static final class TeePrintStream extends PrintStream {
        private final PrintStream a;
        private final PrintStream b;
        TeePrintStream(PrintStream a, PrintStream b) {
            super(a);
            this.a = a;
            this.b = b;
        }
        @Override
        public void write(int b) {
            a.write(b);
            this.b.write(b);
        }
        @Override
        public void write(byte[] buf, int off, int len) {
            a.write(buf, off, len);
            b.write(buf, off, len);
        }
        @Override
        public void flush() {
            a.flush();
            b.flush();
        }
        @Override
        public void close() {
            flush();
        }
    }
}
