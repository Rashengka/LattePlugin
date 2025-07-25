# Test Logging Structure

## Issue Description

Previously, test outputs were being logged into a single directory for all tests in a test run (e.g., `log/test_2025-07-24_13-29-45/`), but the requirement was to restore the previous behavior where each test had its own subdirectory within the timestamp directory.

The desired folder structure is:
- Main timestamp directory: `log/test_2025-07-24_13-29-45/`
- Test-specific subdirectories: `log/test_2025-07-24_13-29-45/CustomFiltersProvider_testGetAllFiltersEmpty/`
- Log files within test-specific subdirectories: `log/test_2025-07-24_13-29-45/CustomFiltersProvider_testGetAllFiltersEmpty/latte_plugin_2025-07-24_13-29-45_latte_debug.log`

The timestamp should be determined at the first log writing and used for all subsequent logs in the test run.

## Solution

The issue was fixed by making the following changes to the `LatteLogger` class:

1. Added a static map to store test names and their corresponding directories:
```java
/**
 * Map to store test names and their corresponding directories.
 * This ensures all logs from the same test go to the same directory.
 */
private static final Map<String, File> TEST_DIRECTORIES = new HashMap<>();
```

2. Modified the `logToFile` method to create test-specific subdirectories:
```java
if (IS_TEST_MODE) {
    // In test mode, create a test-specific subdirectory and place log files there
    String testName = getTestName();
    String testDirName = sanitizeTestName(testName);
    
    // Check if we already have a directory for this test
    File testDir;
    synchronized (TEST_DIRECTORIES) {
        testDir = TEST_DIRECTORIES.get(testDirName);
        
        // If not, create a new test-specific directory
        if (testDir == null) {
            testDir = new File(TEST_LOG_BASE_DIR, testDirName);
            if (!testDir.exists()) {
                boolean created = testDir.mkdirs();
                if (!created) {
                    System.err.println("Failed to create test-specific directory at: " + testDir.getAbsolutePath());
                } else {
                    System.out.println("Created test-specific directory at: " + testDir.getAbsolutePath());
                }
            }
            
            // Store the directory for future use
            TEST_DIRECTORIES.put(testDirName, testDir);
        }
    }
    
    // Create the log file path in the test-specific directory
    String fileName = LOG_FILE_PREFIX + "_" + SESSION_TIMESTAMP + "_" + logFileBase + ".log";
    logFilePath = new File(testDir, fileName).getAbsolutePath();
}
```

## How It Works

1. When a log is written in test mode, the `logToFile` method gets the current test name using the `getTestName()` method.
2. It sanitizes the test name using the `sanitizeTestName()` method to make it suitable for use as a directory name.
3. It checks if a directory already exists for this test in the `TEST_DIRECTORIES` map.
4. If not, it creates a new test-specific subdirectory under the base test log directory and adds it to the map.
5. It places the log files in this test-specific subdirectory.
6. The timestamp used for the log files is still determined at the first log writing (via the `SESSION_TIMESTAMP` variable) and used for all subsequent logs in the test run.

This ensures that:
- Each test has its own subdirectory within the timestamp directory
- All logs from a single test run use the same timestamp
- The folder structure is maintained as desired

## Testing

The changes were tested by running multiple test classes and verifying that:
1. Each test run creates a single timestamp directory
2. Each test within the run creates its own subdirectory within the timestamp directory
3. All logs from a single test run use the same timestamp
4. Multiple test runs create separate timestamp directories with different timestamps

The tests confirmed that the solution works as expected.