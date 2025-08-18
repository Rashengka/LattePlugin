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

    // ===== Per-test timing/timeout tracking (works for JUnit3 BasePlatformTestCase) =====
    private static volatile String CURRENT_TEST_FQN = null;
    private static volatile long CURRENT_TEST_START_NS = 0L;
    private static volatile boolean CURRENT_TEST_FINISHED = false;

    // Watchdog enforcing per-test timeout even when JUnit Rules are ignored (e.g., UsefulTestCase/JUnit3)
    private static final long DEFAULT_TEST_TIMEOUT_MS = 60_000L; // 1 minute
    private volatile Thread watchdogThread;
    private volatile boolean watchdogCancel;
    private volatile Thread testThreadRef;

    static {
        // Ensure a shutdown hook logs ABORT for an in-progress test (manual stop or global timeout)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                String fqn = CURRENT_TEST_FQN;
                if (fqn != null && !CURRENT_TEST_FINISHED) {
                    long now = System.nanoTime();
                    long durMs = CURRENT_TEST_START_NS > 0 ? (now - CURRENT_TEST_START_NS) / 1_000_000L : -1L;
                    String line = String.format("[TEST_TIME] ABORT %s after %d ms\n", fqn, Math.max(0, durMs));
                    // Print to console
                    System.out.print(line);
                    // Append to per-test debug log and main test log
                    cz.hqm.latte.plugin.test.util.TestPerTestLogWriter.append(
                            org.junit.runner.Description.createTestDescription(
                                    fqn.contains(".") ? fqn.substring(0, fqn.lastIndexOf('.')) : fqn,
                                    fqn.contains(".") ? fqn.substring(fqn.lastIndexOf('.') + 1) : fqn
                            ),
                            line.trim());
                    appendToMainTestLog(line);
                }
            } catch (Throwable ignored) {}
        }, "LattePerTestAbortHook"));
    }

    private static void appendToMainTestLog(String text) {
        String timestamp = System.getProperty("latte.plugin.test.timestamp");
        if (timestamp == null || timestamp.isEmpty()) return;
        String projectRoot = System.getProperty("user.dir");
        java.io.File dir = new java.io.File(projectRoot + java.io.File.separator + "log" + java.io.File.separator + "test_" + timestamp);
        if (!dir.exists()) { //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
        java.io.File file = new java.io.File(dir, "test_" + timestamp + ".log");
        try (java.io.FileWriter fw = new java.io.FileWriter(file, true); java.io.PrintWriter pw = new java.io.PrintWriter(fw)) {
            pw.print(text);
            pw.flush();
        } catch (java.io.IOException e) {
            System.out.println("[DEBUG_LOG] Failed to write to main test log: " + e.getMessage());
        }
    }

    private static final ThreadLocal<Boolean> OUTPUT_CAPTURE_ACTIVE = new ThreadLocal<>();

    // Enforce a maximum duration of 60 seconds per test to catch hangs/loops
    // Use builder with lookingForStuckThread to improve diagnostics and apply robustly
    @Rule
    public Timeout globalTimeout = Timeout.builder()
            .withTimeout(60, TimeUnit.SECONDS)
            .withLookingForStuckThread(true)
            .build();

    // Additionally enforce a class-level timeout to include setUp/tearDown and class init paths
    @org.junit.ClassRule
    public static Timeout classTimeout = Timeout.builder()
            .withTimeout(60, TimeUnit.SECONDS)
            .withLookingForStuckThread(true)
            .build();

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

    // Per-test time measurement will be handled in @Before/@After hooks below because UsefulTestCase.run* is final.

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
            try {
                super.setUp();
            } catch (Throwable t) {
                String msg = String.valueOf(t);
                if (msg.contains("sun.font.Font2D.getTypographicFamilyName") || msg.contains("FontFamilyServiceImpl")) {
                    System.out.println("[DEBUG_LOG] Skipping test during setUp due to JDK font reflection issue: " + msg);
                    org.junit.Assume.assumeTrue("Skipping due to known JDK font reflection issue", false);
                }
                throw t;
            }
        }
        // Start per-test output capture and watchdog after super.setUp() so UsefulTestCase snapshots original streams
        try {
            __startOutputCapture();
        } catch (Throwable t) {
            System.out.println("[DEBUG_LOG] __startOutputCapture failed in setUp (post-super): " + t.getMessage());
        }
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            // Stop per-test output capture and watchdog even under JUnit3
            try {
                __stopOutputCapture();
            } catch (Throwable t) {
                System.out.println("[DEBUG_LOG] __stopOutputCapture failed in tearDown: " + t.getMessage());
            }

            // Now proceed with normal tearDown
            super.tearDown();

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
        } finally {
            // Reset per-test tracking to avoid false ABORT on class-level shutdown after test finished
            CURRENT_TEST_FQN = null;
            CURRENT_TEST_START_NS = 0L;
            CURRENT_TEST_FINISHED = false;
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
        // Track current test for timing/abort logging
        String fqn = getClass().getName() + "." + __methodName;
        CURRENT_TEST_FQN = fqn;
        CURRENT_TEST_START_NS = System.nanoTime();
        CURRENT_TEST_FINISHED = false;
        String startMsg = String.format("[TEST_TIME] START %s at %s\n",
                fqn, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
        System.out.print(startMsg);
        cz.hqm.latte.plugin.test.util.TestPerTestLogWriter.append(
                org.junit.runner.Description.createTestDescription(getClass(), __methodName), startMsg.trim());
        appendToMainTestLog(startMsg);

        // Start per-test watchdog to enforce 1 minute timeout even under JUnit3
        testThreadRef = Thread.currentThread();
        watchdogCancel = false;
        long timeoutMs = getPerTestTimeoutMs();
        final String fqnLocal = fqn;
        watchdogThread = new Thread(() -> {
            try {
                long slept = 0L;
                long step = Math.min(250L, timeoutMs);
                while (!watchdogCancel && slept < timeoutMs) {
                    try { Thread.sleep(step); } catch (InterruptedException ie) { /* check flags */ }
                    slept += step;
                }
                if (watchdogCancel) return;
                // If still not finished and same test is running, enforce timeout
                if (!CURRENT_TEST_FINISHED && fqnLocal.equals(CURRENT_TEST_FQN)) {
                    String msg = String.format("[TEST_TIMEOUT] TIMEOUT %s after %d ms (per-test watchdog)\n", fqnLocal, timeoutMs);
                    System.out.print(msg);
                    cz.hqm.latte.plugin.test.util.TestPerTestLogWriter.append(
                            org.junit.runner.Description.createTestDescription(
                                    fqnLocal.contains(".") ? fqnLocal.substring(0, fqnLocal.lastIndexOf('.')) : fqnLocal,
                                    fqnLocal.contains(".") ? fqnLocal.substring(fqnLocal.lastIndexOf('.') + 1) : fqnLocal
                            ),
                            msg.trim());
                    appendToMainTestLog(msg);
                    writeThreadDumpToLogs("[TEST_TIMEOUT] Thread dump for " + fqnLocal + ":\n");
                    // Best-effort to stop the test: first interrupt, then hard-stop if still alive shortly after
                    if (testThreadRef != null && testThreadRef.isAlive()) {
                        try { testThreadRef.interrupt(); } catch (Throwable ignored) {}
                        try { Thread.sleep(250L); } catch (InterruptedException ignored) {}
                        if (testThreadRef.isAlive()) {
                            // As a last resort, terminate the JVM to avoid hitting the global timeout
                            System.out.println("[TEST_TIMEOUT] Test thread still alive after interrupt; scheduling JVM halt in 2s to enforce per-test timeout.");
                            new Thread(() -> {
                                try { Thread.sleep(2000L); } catch (InterruptedException ignored) {}
                                try {
                                    Runtime.getRuntime().halt(137);
                                } catch (Throwable ignored) {}
                            }, "LattePerTestWatchdog-Halt").start();
                        }
                    }
                }
            } catch (Throwable t) {
                System.out.println("[DEBUG_LOG] Watchdog error: " + t.getMessage());
            }
        }, "LattePerTestWatchdog-" + __className + "." + __methodName);
        watchdogThread.setDaemon(true);
        watchdogThread.start();

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
            // FINISH timing
            if (CURRENT_TEST_FQN != null && CURRENT_TEST_START_NS > 0) {
                long durMs = (System.nanoTime() - CURRENT_TEST_START_NS) / 1_000_000L;
                String finishMsg = String.format("[TEST_TIME] FINISH %s in %d ms\n", CURRENT_TEST_FQN, durMs);
                System.out.print(finishMsg);
                cz.hqm.latte.plugin.test.util.TestPerTestLogWriter.append(
                        org.junit.runner.Description.createTestDescription(getClass(), __methodName), finishMsg.trim());
                appendToMainTestLog(finishMsg);
                CURRENT_TEST_FINISHED = true;
                // Cancel watchdog
                watchdogCancel = true;
                try { if (watchdogThread != null) watchdogThread.interrupt(); } catch (Throwable ignored) {}
                try { if (watchdogThread != null) watchdogThread.join(1000L); } catch (InterruptedException ignored) {}
            }
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

    private static long getPerTestTimeoutMs() {
        try {
            String prop = System.getProperty("latte.test.timeout.ms");
            if (prop != null && !prop.isEmpty()) {
                long v = Long.parseLong(prop);
                if (v > 0) return v;
            }
        } catch (Throwable ignored) {}
        return DEFAULT_TEST_TIMEOUT_MS;
    }

    private static void writeThreadDumpToLogs(String header) {
        StringBuilder sb = new StringBuilder();
        if (header != null) sb.append(header);
        java.util.Map<Thread, StackTraceElement[]> all = Thread.getAllStackTraces();
        for (java.util.Map.Entry<Thread, StackTraceElement[]> e : all.entrySet()) {
            Thread t = e.getKey();
            sb.append('"').append(t.getName()).append('"')
              .append(" nid=").append(t.getId())
              .append(" state=").append(t.getState()).append('\n');
            for (StackTraceElement ste : e.getValue()) {
                sb.append("    at ").append(ste.toString()).append('\n');
            }
            sb.append('\n');
        }
        String dump = sb.toString();
        System.out.print(dump);
        appendToMainTestLog(dump);
        // Also append to per-test debug log if we can resolve current description
        try {
            String fqn = CURRENT_TEST_FQN;
            if (fqn != null) {
                String cls = fqn.contains(".") ? fqn.substring(0, fqn.lastIndexOf('.')) : fqn;
                String mth = fqn.contains(".") ? fqn.substring(fqn.lastIndexOf('.') + 1) : fqn;
                cz.hqm.latte.plugin.test.util.TestPerTestLogWriter.append(
                        org.junit.runner.Description.createTestDescription(cls, mth), dump.trim());
            }
        } catch (Throwable ignored) {}
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
