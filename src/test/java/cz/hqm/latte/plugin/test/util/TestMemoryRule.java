package cz.hqm.latte.plugin.test.util;

import com.intellij.openapi.diagnostic.Logger;
import cz.hqm.latte.plugin.util.LatteLogger;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A JUnit 4 TestRule that measures the memory usage of each test and logs it.
 *
 * Behavior:
 * - Prints per-test START/FINISH with used memory and delta to stdout.
 * - Logs the same info via LatteLogger into per-test debug logs.
 * - Appends concise [TEST_MEM] lines to the main test run log: log/test_TIMESTAMP/test_TIMESTAMP.log.
 * - On JVM shutdown, writes a short memory summary (tests, total delta, max delta) to the main test log.
 */
public class TestMemoryRule implements TestRule {

    private static final String TEST_MEM_PREFIX = "[TEST_MEM]";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final SimpleDateFormat DF = new SimpleDateFormat(DATE_FORMAT);

    private static final AtomicInteger TOTAL_TESTS = new AtomicInteger(0);
    private static final AtomicLong TOTAL_DELTA_BYTES = new AtomicLong(0);
    private static final AtomicLong MAX_DELTA_BYTES = new AtomicLong(0);

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                String timestamp = System.getProperty("latte.plugin.test.timestamp");
                if (timestamp == null || timestamp.isEmpty()) {
                    return;
                }
                File mainLogFile = resolveMainTestLogFile(timestamp);

                // Compute totals by parsing existing [TEST_MEM] lines to avoid classloader counter issues
                long totalDeltaKb = 0L;
                long maxDeltaKb = 0L;
                int count = 0;
                try {
                    java.util.List<String> lines = java.nio.file.Files.readAllLines(mainLogFile.toPath());
                    for (String line : lines) {
                        // Expected format: [TEST_MEM] <FQN> used=<KB> KB, delta=+<KB> KB ...
                        if (line != null && line.startsWith(TEST_MEM_PREFIX)) {
                            int dIdx = line.indexOf("delta=");
                            int kbIdx = line.indexOf(" KB", dIdx > 0 ? dIdx : 0);
                            if (dIdx > 0 && kbIdx > dIdx) {
                                String valStr = line.substring(dIdx + 6, kbIdx).trim();
                                // valStr may start with + or -
                                try {
                                    long val = Long.parseLong(valStr);
                                    long abs = Math.max(0L, val);
                                    totalDeltaKb += abs;
                                    if (abs > maxDeltaKb) maxDeltaKb = abs;
                                    count++;
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    // Fallback to local counters (may be zero)
                    totalDeltaKb = bytesToKb(TOTAL_DELTA_BYTES.get());
                    maxDeltaKb = bytesToKb(MAX_DELTA_BYTES.get());
                    count = TOTAL_TESTS.get();
                }

                if (count > 0) {
                    appendToFile(mainLogFile,
                            String.format("%s Memory summary at %s: tests=%d, totalDelta=%d KB, maxDelta=%d KB\n\n",
                                    TEST_MEM_PREFIX, DF.format(new Date()),
                                    count, totalDeltaKb, maxDeltaKb));
                }
            } catch (Throwable t) {
                System.out.println("[DEBUG_LOG] TestMemoryRule shutdownHook error: " + t.getMessage());
            }
        }, "TestMemoryRuleShutdown"));
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                String className = description.getClassName();
                String methodName = description.getMethodName();
                String fqn = className + "." + methodName;

                // GC best-effort stabilization before measuring
                long beforeBytes = measuredUsedBytesWithGc();
                String startMsg = String.format("%s START %s at %s used=%d KB",
                        TEST_MEM_PREFIX, fqn, DF.format(new Date()), bytesToKb(beforeBytes));
                System.out.println(startMsg);
                LatteLogger.info(Logger.getInstance("TestMemory"), startMsg);
                // Ensure per-test debug log exists and record START
                TestPerTestLogWriter.append(description, startMsg);

                Throwable failure = null;
                try {
                    base.evaluate();
                } catch (Throwable t) {
                    failure = t;
                    throw t;
                } finally {
                    long afterBytes = measuredUsedBytesWithGc();
                    long deltaBytes = afterBytes - beforeBytes;
                    TOTAL_TESTS.incrementAndGet();
                    TOTAL_DELTA_BYTES.addAndGet(Math.max(0, deltaBytes));
                    MAX_DELTA_BYTES.updateAndGet(prev -> Math.max(prev, Math.max(0, deltaBytes)));

                    String finishMsg = String.format("%s FINISH %s used=%d KB, delta=%+d KB",
                            TEST_MEM_PREFIX, fqn, bytesToKb(afterBytes), bytesToKb(deltaBytes));
                    System.out.println(finishMsg);
                    if (failure != null) {
                        LatteLogger.warn(Logger.getInstance("TestMemory"), finishMsg + " (FAILED)");
                    } else {
                        LatteLogger.info(Logger.getInstance("TestMemory"), finishMsg + " (OK)");
                    }
                    // Ensure per-test debug log includes FINISH too
                    TestPerTestLogWriter.append(description, finishMsg + (failure != null ? " (FAILED)" : " (OK)"));

                    appendMemLineToMain(fqn, afterBytes, deltaBytes, failure != null);

                    // Record into shared metrics registry and attempt to write the combined line
                    TestMetricsRegistry.recordMemory(fqn, afterBytes);
                    TestMetricsRegistry.commitIfComplete(fqn, failure != null);
                }
            }
        };
    }

    private static long measuredUsedBytesWithGc() {
        try {
            System.gc();
            Thread.sleep(10); // short pause to allow GC
        } catch (InterruptedException ignored) {}
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    private static void appendMemLineToMain(String fqn, long usedBytes, long deltaBytes, boolean failed) {
        String timestamp = System.getProperty("latte.plugin.test.timestamp");
        if (timestamp == null || timestamp.isEmpty()) {
            return;
        }
        File file = resolveMainTestLogFile(timestamp);
        String line = String.format("%s %s used=%d KB, delta=%+d KB%s\n",
                TEST_MEM_PREFIX, fqn, bytesToKb(usedBytes), bytesToKb(deltaBytes), failed ? " [FAILED]" : "");
        appendToFile(file, line);
    }

    private static int bytesToKb(long bytes) {
        return (int) Math.round(bytes / 1024.0);
    }

    private static File resolveMainTestLogFile(String timestamp) {
        String projectRoot = System.getProperty("user.dir");
        File dir = new File(projectRoot + File.separator + "log" + File.separator + "test_" + timestamp);
        if (!dir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
        return new File(dir, "test_" + timestamp + ".log");
    }

    private static void appendToFile(File file, String text) {
        try (FileWriter fw = new FileWriter(file, true); PrintWriter pw = new PrintWriter(fw)) {
            pw.print(text);
            pw.flush();
        } catch (IOException e) {
            System.out.println("[DEBUG_LOG] Failed to write test memory to main log: " + e.getMessage());
        }
    }
}
