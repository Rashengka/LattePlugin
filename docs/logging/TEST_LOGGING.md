# Test Logging System

## Issue Description

Previously, test outputs were being logged into multiple folders in the `log` directory, with each folder having a timestamp like `log/test_2025-07-24_12-01-32`. This happened because tests run for several seconds, and each test or test method was creating its own log directory with a new timestamp. The requirement was to have all logs from a single test run go into a single directory with a timestamp determined at the first log writing.

## Solution

The issue was fixed by making the following changes:

### 1. In build.gradle

1. Added a shared timestamp variable at the script level:
```groovy
// Create a single timestamp for the entire build/test run
// This ensures all logs from a single run go to the same directory
ext.buildTimestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())
```

2. Modified the logging functions to use this shared timestamp:
```groovy
ext.logToFile = { String taskName, String output ->
    if (enableCommandLogging) {
        // Use the shared buildTimestamp instead of creating a new one
        def timestamp = buildTimestamp
        
        // Rest of the function...
    }
}

ext.logToProjectRoot = { String taskName, String output ->
    if (enableCommandLogging) {
        // Use the shared buildTimestamp instead of creating a new one
        def timestamp = buildTimestamp
        
        // Rest of the function...
    }
}
```

3. Passed the timestamp to the tests as a system property:
```groovy
systemProperties([
    // Latte Plugin Test Mode Flag
    'latte.plugin.test.mode': 'true',
    
    // Pass the build timestamp to the tests
    // This ensures all logs from a single test run use the same timestamp
    'latte.plugin.test.timestamp': buildTimestamp,
    
    // Other properties...
])
```

### 2. In LatteLogger.java

1. Modified the SESSION_TIMESTAMP to use the system property if available:
```java
/**
 * Session timestamp - either from system property or generated once when the class is loaded.
 * This ensures all logs during one editor session go to the same file.
 * In test mode, this will use the timestamp passed from build.gradle to ensure
 * all logs from a single test run use the same timestamp.
 */
private static final String SESSION_TIMESTAMP = System.getProperty("latte.plugin.test.timestamp") != null ?
        System.getProperty("latte.plugin.test.timestamp") : FILE_DATE_FORMAT.format(new Date());
```

2. Simplified the logToFile method to use the base test log directory directly:
```java
if (IS_TEST_MODE) {
    // In test mode, use the base test log directory directly
    // This ensures all logs from a single test run go to a single directory
    
    // Create the log file path in the base test log directory
    String fileName = LOG_FILE_PREFIX + "_" + SESSION_TIMESTAMP + "_" + logFileBase + ".log";
    logFilePath = new File(TEST_LOG_BASE_DIR, fileName).getAbsolutePath();
}
```

## How It Works

1. When the build script is loaded, it creates a single timestamp (`buildTimestamp`) that will be used for the entire build/test run.
2. This timestamp is passed to the tests as a system property (`latte.plugin.test.timestamp`).
3. The LatteLogger class uses this system property for its `SESSION_TIMESTAMP` instead of creating a new one.
4. All logs from a single test run use this same timestamp, ensuring they all go into the same directory.

## Testing

The changes were tested by running multiple test classes and verifying that:
1. Each test run creates a single directory with a timestamp.
2. All logs from a single test run go into that directory.
3. Multiple test runs create separate directories with different timestamps.

The tests confirmed that the solution works as expected.