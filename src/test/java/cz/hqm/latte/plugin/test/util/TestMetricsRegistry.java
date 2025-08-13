package cz.hqm.latte.plugin.test.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Shared registry to aggregate per-test metrics (time and memory) and write
 * a single combined line to the main test log file once both values are known.
 *
 * Output format (exact spacing and units to match guidelines):
 *   Test: <FQN> - SUCCESS [12.3456s] [128MB]
 */
public final class TestMetricsRegistry {
    private static final Map<String, Long> DURATIONS_MS = new ConcurrentHashMap<>();
    private static final Map<String, Long> USED_BYTES = new ConcurrentHashMap<>();
    private static final Map<String, Boolean> FAILED_MAP = new ConcurrentHashMap<>();
    private static final Set<String> WRITTEN = ConcurrentHashMap.newKeySet();

    private static final DecimalFormat SECONDS_FMT = new DecimalFormat("0.0000");

    private TestMetricsRegistry() {}

    public static void recordDuration(String fqn, long durationMs) {
        if (fqn == null) return;
        DURATIONS_MS.put(fqn, durationMs);
    }

    public static void recordMemory(String fqn, long usedBytes) {
        if (fqn == null) return;
        USED_BYTES.put(fqn, usedBytes);
    }

    public static void commitIfComplete(String fqn, boolean failed) {
        if (fqn == null) return;
        FAILED_MAP.merge(fqn, failed, (a, b) -> a || b);
        Long ms = DURATIONS_MS.get(fqn);
        Long used = USED_BYTES.get(fqn);
        if (ms == null || used == null) {
            return; // not complete yet
        }
        // Ensure we write only once
        if (!WRITTEN.add(fqn)) {
            return;
        }
        String line = buildCombinedLine(fqn, ms, used, FAILED_MAP.getOrDefault(fqn, false));
        appendToMainTestLog(line);
    }

    private static String buildCombinedLine(String fqn, long durationMs, long usedBytes, boolean failed) {
        double seconds = durationMs / 1000.0;
        String secs = SECONDS_FMT.format(seconds);
        long usedMb = Math.round(usedBytes / (1024.0 * 1024.0));
        String status = failed ? "FAILURE" : "SUCCESS";
        return String.format("Test: %s - %s [%ss] [%dMB]\n", fqn, status, secs, usedMb);
    }

    private static void appendToMainTestLog(String text) {
        String timestamp = System.getProperty("latte.plugin.test.timestamp");
        if (timestamp == null || timestamp.isEmpty()) {
            return;
        }
        String projectRoot = System.getProperty("user.dir");
        File dir = new File(projectRoot + File.separator + "log" + File.separator + "test_" + timestamp);
        if (!dir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
        File file = new File(dir, "test_" + timestamp + ".log");
        try (FileWriter fw = new FileWriter(file, true); PrintWriter pw = new PrintWriter(fw)) {
            pw.print(text);
            pw.flush();
        } catch (IOException e) {
            System.out.println("[DEBUG_LOG] Failed to write combined test metrics to main log: " + e.getMessage());
        }
    }
}
