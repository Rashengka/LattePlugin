package cz.hqm.latte.plugin.test.util;

import cz.hqm.latte.plugin.util.LatteLogger;
import com.intellij.openapi.diagnostic.Logger;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A JUnit 4 TestRule that measures the execution time of each test and logs it.
 *
 * Behavior:
 * - Prints per-test start/finish with duration to standard out (visible in Gradle/IDE output).
 * - Logs the same info to LatteLogger (goes into per-test log directory).
 * - Appends a concise timing line to the main test run log: log/test_TIMESTAMP/test_TIMESTAMP.log
 * - On JVM shutdown, writes a short timing summary (total tests, total duration) to the main test log.
 */
public class TestTimingRule implements TestRule {

    private static final String TEST_TIME_PREFIX = "[TEST_TIME]";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final SimpleDateFormat DF = new SimpleDateFormat(DATE_FORMAT);

    // Aggregation across the whole JVM (one Gradle fork per test class by config)
    private static final AtomicInteger TOTAL_TESTS = new AtomicInteger(0);
    private static final AtomicLong TOTAL_DURATION_MS = new AtomicLong(0);
    private static final ConcurrentHashMap<String, Long> PER_TEST_DURATIONS = new ConcurrentHashMap<>();

    static {
        // Ensure a shutdown summary is written at the end of the JVM run
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                String timestamp = System.getProperty("latte.plugin.test.timestamp");
                if (timestamp == null || timestamp.isEmpty()) {
                    // Not in test mode or not configured; skip writing the main file
                    return;
                }
                File mainLogFile = resolveMainTestLogFile(timestamp);

                // Compute totals by parsing existing [TEST_TIME] lines to avoid classloader counter issues
                long totalMs = 0L;
                int count = 0;
                try {
                    java.util.List<String> lines = java.nio.file.Files.readAllLines(mainLogFile.toPath());
                    for (String line : lines) {
                        // Expected format: [TEST_TIME] <FQN> = <ms> ms ...
                        if (line != null && line.startsWith(TEST_TIME_PREFIX)) {
                            int eqIdx = line.indexOf("=");
                            int msIdx = line.indexOf(" ms", eqIdx > 0 ? eqIdx : 0);
                            if (eqIdx > 0 && msIdx > eqIdx) {
                                String num = line.substring(eqIdx + 1, msIdx).trim();
                                try {
                                    long val = Long.parseLong(num);
                                    totalMs += val;
                                    count++;
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    // If reading fails, fall back to local counters (may be zero in some forks)
                    totalMs = TOTAL_DURATION_MS.get();
                    count = TOTAL_TESTS.get();
                }

                // If we saw no per-test timing lines, skip writing a misleading zero summary
                if (count > 0) {
                    appendToFile(mainLogFile,
                            String.format("%s Test timing summary at %s: total tests=%d, total duration=%d ms\n\n",
                                    TEST_TIME_PREFIX, DF.format(new Date()),
                                    count, totalMs));
                }
            } catch (Throwable t) {
                // Never fail the build because of logging
                System.out.println("[DEBUG_LOG] TestTimingRule shutdownHook error: " + t.getMessage());
            }
        }, "TestTimingRuleShutdown"));
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                String className = description.getClassName();
                String methodName = description.getMethodName();
                String fqn = className + "." + methodName;

                long start = System.nanoTime();
                String startMsg = String.format("%s START %s at %s", TEST_TIME_PREFIX, fqn, DF.format(new Date()));
                System.out.println(startMsg);
                LatteLogger.info(Logger.getInstance("TestTiming"), startMsg);
                // Ensure per-test debug log exists and record START
                TestPerTestLogWriter.append(description, startMsg);

                Throwable failure = null;
                try {
                    base.evaluate();
                } catch (Throwable t) {
                    failure = t;
                    throw t;
                } finally {
                    long end = System.nanoTime();
                    long durationMs = (end - start) / 1_000_000L;
                    TOTAL_TESTS.incrementAndGet();
                    TOTAL_DURATION_MS.addAndGet(durationMs);
                    PER_TEST_DURATIONS.put(fqn, durationMs);

                    String finishMsg = String.format("%s FINISH %s in %d ms", TEST_TIME_PREFIX, fqn, durationMs);
                    System.out.println(finishMsg);
                    if (failure != null) {
                        LatteLogger.warn(Logger.getInstance("TestTiming"), finishMsg + " (FAILED)" );
                    } else {
                        LatteLogger.info(Logger.getInstance("TestTiming"), finishMsg + " (OK)" );
                    }
                    // Ensure per-test debug log includes FINISH too
                    TestPerTestLogWriter.append(description, finishMsg + (failure != null ? " (FAILED)" : " (OK)"));

                    // Also append to the main test run log for easy scanning
                    writeToMainTestLog(fqn, durationMs, failure != null);

                    // Record into shared metrics registry and attempt to write the combined line
                    TestMetricsRegistry.recordDuration(fqn, durationMs);
                    TestMetricsRegistry.commitIfComplete(fqn, failure != null);
                }
            }
        };
    }

    private static void writeToMainTestLog(String fqn, long durationMs, boolean failed) {
        String timestamp = System.getProperty("latte.plugin.test.timestamp");
        if (timestamp == null || timestamp.isEmpty()) {
            return;
        }
        File file = resolveMainTestLogFile(timestamp);
        String line = String.format("%s %s = %d ms%s\n", TEST_TIME_PREFIX, fqn, durationMs, failed ? " [FAILED]" : "");
        appendToFile(file, line);
    }

    private static File resolveMainTestLogFile(String timestamp) {
        String projectRoot = System.getProperty("user.dir");
        File dir = new File(projectRoot + File.separator + "log" + File.separator + "test_" + timestamp);
        if (!dir.exists()) {
            // Best effort create
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
            System.out.println("[DEBUG_LOG] Failed to write test timing to main log: " + e.getMessage());
        }
    }
}
