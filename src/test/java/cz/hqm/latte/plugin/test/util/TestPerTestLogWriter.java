package cz.hqm.latte.plugin.test.util;

import org.junit.runner.Description;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Ensures that per-test log directories and a debug log file exist for each executed test,
 * and appends provided lines into that file.
 *
 * This avoids reliance on stack-trace based test name detection and guarantees that
 * log/test_TIMESTAMP/<TestName_method>/latte_plugin_TIMESTAMP_latte_debug.log exists
 * for every test, which is important for later analysis.
 */
public final class TestPerTestLogWriter {

    private static final String FALLBACK_TIMESTAMP;
    static {
        String ts = System.getProperty("latte.plugin.test.timestamp");
        if (ts == null || ts.isEmpty()) {
            java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            ts = fmt.format(new java.util.Date());
        }
        FALLBACK_TIMESTAMP = ts;
    }

    private TestPerTestLogWriter() {}

    public static void append(Description description, String line) {
        String timestamp = System.getProperty("latte.plugin.test.timestamp");
        if (description == null) {
            return; // no description available
        }
        if (timestamp == null || timestamp.isEmpty()) {
            // Fallback for IDE/test-runner executions without Gradle wiring
            timestamp = FALLBACK_TIMESTAMP;
        }
        File perTestLog = resolvePerTestDebugLogFile(description, timestamp);
        perTestLog.getParentFile().mkdirs();
        try (FileWriter fw = new FileWriter(perTestLog, true); PrintWriter pw = new PrintWriter(fw)) {
            pw.println(line);
            pw.flush();
        } catch (IOException e) {
            System.out.println("[DEBUG_LOG] Failed to write per-test log: " + e.getMessage());
        }
    }

    private static File resolvePerTestDebugLogFile(Description description, String timestamp) {
        String projectRoot = System.getProperty("user.dir");
        File baseDir = new File(projectRoot + File.separator + "log" + File.separator + "test_" + timestamp);
        String dirName = buildPerTestDirName(description);
        File testDir = new File(baseDir, dirName);
        String fileName = String.format("latte_plugin_%s_latte_debug.log", timestamp);
        return new File(testDir, fileName);
    }

    private static String buildPerTestDirName(Description description) {
        String className = description.getClassName();
        String methodName = description.getMethodName();
        // simple class name
        if (className == null) className = "UnknownClass";
        int idx = className.lastIndexOf('.');
        if (idx >= 0) {
            className = className.substring(idx + 1);
        }
        // drop trailing "Test"
        if (className.endsWith("Test")) {
            className = className.substring(0, className.length() - 4);
        }
        if (methodName == null || methodName.isEmpty()) {
            methodName = "unknownMethod";
        }
        String combined = className + "_" + methodName;
        // sanitize to be filesystem-friendly
        combined = combined.replaceAll("[^a-zA-Z0-9_.-]", "_");
        if (combined.length() > 50) {
            combined = combined.substring(0, 50);
        }
        return combined;
    }
}
