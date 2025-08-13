package cz.hqm.latte.plugin.test.util;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Captures all console output (stdout and stderr) produced by a single test method
 * and writes it to a per-method file inside the current test timestamp directory.
 *
 * Target structure:
 *   log/test_YYYY-MM-DD_HH-MM-SS/<SimpleClassName>/<methodName>.console.log
 *
 * Notes:
 * - Uses a tee stream so output still appears on the console while being written to file.
 * - Relies on system property latte.plugin.test.timestamp set by Gradle test task.
 * - Falls back to current time if the property is missing (e.g., running in IDE).
 */
public final class TestOutputCaptureRule implements TestRule {

    private static final String TS_PROP = "latte.plugin.test.timestamp";

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                // Determine timestamp directory
                String timestamp = System.getProperty(TS_PROP);
                if (timestamp == null || timestamp.isEmpty()) {
                    timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
                }

                // Build destination file path
                File destFile = resolvePerMethodConsoleFile(description, timestamp);
                // Ensure parent directories exist
                File parent = destFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    parent.mkdirs();
                }

                // Current System.out/err (may already be wrapped by other rules/handlers)
                PrintStream originalOut = System.out;
                PrintStream originalErr = System.err;

                // Create file output stream (append to support multiple wrappers if any)
                try (FileOutputStream fos = new FileOutputStream(destFile, true)) {
                    // Write header
                    String header = String.format("=== Console for %s.%s at %s ===%n",
                            safeSimpleClassName(description), safeMethodName(description),
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
                    fos.write(header.getBytes());
                    fos.flush();

                    // Tee both stdout and stderr into the same file (with prefixes)
                    PrintStream fileOut = new PrintStream(new PrefixingOutputStream(fos, "[STDOUT] "), true);
                    PrintStream fileErr = new PrintStream(new PrefixingOutputStream(fos, "[STDERR] "), true);

                    // Wrap originals with tees
                    TeePrintStream teeOut = new TeePrintStream(originalOut, fileOut);
                    TeePrintStream teeErr = new TeePrintStream(originalErr, fileErr);

                    // Install
                    System.setOut(teeOut);
                    System.setErr(teeErr);

                    Throwable failure = null;
                    try {
                        base.evaluate();
                    } catch (Throwable t) {
                        failure = t;
                        throw t;
                    } finally {
                        // Footer
                        String footer = String.format("=== End of console for %s.%s (%s) ===%n%n",
                                safeSimpleClassName(description), safeMethodName(description),
                                (failure == null ? "OK" : "FAILED"));
                        fos.write(footer.getBytes());
                        fos.flush();

                        // Restore original streams
                        System.setOut(originalOut);
                        System.setErr(originalErr);

                        // Ensure tees are flushed
                        teeOut.flush();
                        teeErr.flush();
                        fileOut.flush();
                        fileErr.flush();
                    }
                }
            }
        };
    }

    private static File resolvePerMethodConsoleFile(Description description, String timestamp) {
        String projectRoot = System.getProperty("user.dir");
        String simpleClass = safeSimpleClassName(description);
        String method = safeMethodName(description);
        File baseDir = new File(projectRoot + File.separator + "log" + File.separator + "test_" + timestamp);
        File classDir = new File(baseDir, simpleClass);
        return new File(classDir, method + ".console.log");
    }

    private static String safeSimpleClassName(Description description) {
        String className = description.getClassName();
        if (className == null || className.isEmpty()) className = "UnknownClass";
        int idx = className.lastIndexOf('.');
        if (idx >= 0) className = className.substring(idx + 1);
        // sanitize
        return className.replaceAll("[^a-zA-Z0-9_.-]", "_");
    }

    private static String safeMethodName(Description description) {
        String methodName = description.getMethodName();
        if (methodName == null || methodName.isEmpty()) methodName = "unknownMethod";
        return methodName.replaceAll("[^a-zA-Z0-9_.-]", "_");
    }

    /** OutputStream that prefixes each new line with a tag (e.g., [STDOUT] ). */
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

    /** PrintStream that tees writes to two underlying PrintStreams. */
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
            // do not close underlying streams here; they are managed externally
            flush();
        }
    }
}
