package cz.hqm.latte.plugin.util;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for conditional logging in the Latte plugin.
 * Logging is enabled only during development and disabled in distribution builds.
 * Logs are written to both IntelliJ's built-in logging system and to files in the project's log/ directory.
 *
 * When logging, the path to the file being processed is included in the log message.
 * If the file is within the open project, a relative path is logged.
 * If the file is outside the open project, an absolute path is logged.
 */
public class LatteLogger {

    /**
     * Flag indicating whether logging is enabled.
     * This is set to false in distribution builds.
     */
    private static final boolean IS_DEVELOPMENT_MODE = !Boolean.getBoolean("latte.plugin.production");

    /**
     * Flag indicating whether we're running in test mode.
     * This is set to true when running tests via Gradle.
     */
    private static final boolean IS_TEST_MODE = Boolean.getBoolean("latte.plugin.test.mode");

    /**
     * Directory for log files.
     */
    private static final String LOG_DIR_NAME = "log";

    /**
     * Absolute path to the log directory.
     */
    private static final String LOG_DIR_PATH;

    /**
     * Common prefix for all log files.
     */
    private static final String LOG_FILE_PREFIX = "latte_plugin";

    /**
     * Base name for validation errors log file.
     */
    private static final String VALIDATION_LOG_BASE = "validation_errors";

    /**
     * Base name for debug messages log file.
     */
    private static final String DEBUG_LOG_BASE = "latte_debug";

    /**
     * Date format for log entries (within the log file).
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * Date format for log file names, matching the format used in build.gradle.
     */
    private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    /**
     * Session timestamp - generated once when the class is loaded.
     * This ensures all logs during one editor session go to the same file.
     */
    private static final String SESSION_TIMESTAMP = FILE_DATE_FORMAT.format(new Date());

    /**
     * Test directory name prefix.
     */
    private static final String TEST_DIR_PREFIX = "test_";

    /**
     * Full path to the debug log file for this session.
     */
    private static final String DEBUG_LOG_FILE_PATH;

    /**
     * Full path to the validation log file for this session.
     */
    private static final String VALIDATION_LOG_FILE_PATH;
    
    /**
     * Base directory for test logs.
     */
    private static final String TEST_LOG_BASE_DIR;

    /**
     * Ensures the log directory exists.
     */
    static {
        // Get the log directory from the system property, or fall back to the project root directory
        String logDirPath = System.getProperty("latte.plugin.log.dir");
        if (logDirPath == null || logDirPath.isEmpty()) {
            // Fall back to the project root directory
            String projectRoot = System.getProperty("user.dir");
            logDirPath = projectRoot + File.separator + LOG_DIR_NAME;
            System.out.println("No latte.plugin.log.dir system property found, using: " + logDirPath);
        } else {
            System.out.println("Using log directory from system property: " + logDirPath);
        }
        LOG_DIR_PATH = logDirPath;

        // Create the log directory if it doesn't exist
        File logDir = new File(LOG_DIR_PATH);
        if (!logDir.exists()) {
            boolean created = logDir.mkdirs();
            if (!created) {
                System.err.println("Failed to create log directory at: " + logDir.getAbsolutePath());
            } else {
                System.out.println("Created log directory at: " + logDir.getAbsolutePath());
            }
        } else {
            System.out.println("Using existing log directory at: " + logDir.getAbsolutePath());
        }

        // Set up test log directory if in test mode
        if (IS_TEST_MODE) {
            // Create a test-specific directory with timestamp
            String testDirName = TEST_DIR_PREFIX + SESSION_TIMESTAMP;
            String testLogBaseDirPath = LOG_DIR_PATH + File.separator + testDirName;
            
            // Create the test log base directory
            File testLogBaseDir = new File(testLogBaseDirPath);
            if (!testLogBaseDir.exists()) {
                boolean created = testLogBaseDir.mkdirs();
                if (!created) {
                    System.err.println("Failed to create test log directory at: " + testLogBaseDir.getAbsolutePath());
                } else {
                    System.out.println("Created test log directory at: " + testLogBaseDir.getAbsolutePath());
                }
            }
            
            TEST_LOG_BASE_DIR = testLogBaseDirPath;
            
            // For test mode, we'll create the actual log files in test-specific subdirectories
            // when logging occurs, so we just set these to empty strings for now
            DEBUG_LOG_FILE_PATH = "";
            VALIDATION_LOG_FILE_PATH = "";
            
            System.out.println("Test mode detected. Logs will be organized in test-specific directories under: " + TEST_LOG_BASE_DIR);
        } else {
            // Normal mode (not test) - use the original behavior
            TEST_LOG_BASE_DIR = ""; // Not used in normal mode
            
            // Initialize the log file paths for this session
            // Format: latte_plugin_TIMESTAMP_TYPE.log
            String debugFileName = LOG_FILE_PREFIX + "_" + SESSION_TIMESTAMP + "_" + DEBUG_LOG_BASE + ".log";
            String validationFileName = LOG_FILE_PREFIX + "_" + SESSION_TIMESTAMP + "_" + VALIDATION_LOG_BASE + ".log";

            DEBUG_LOG_FILE_PATH = new File(LOG_DIR_PATH, debugFileName).getAbsolutePath();
            VALIDATION_LOG_FILE_PATH = new File(LOG_DIR_PATH, validationFileName).getAbsolutePath();

            System.out.println("Debug log file for this session: " + DEBUG_LOG_FILE_PATH);
            System.out.println("Validation log file for this session: " + VALIDATION_LOG_FILE_PATH);
        }
    }

    /**
     * Writes a log entry to the specified log file for the current session.
     * Uses a single log file per editor session instead of creating a new file for each log entry.
     * In test mode, creates a test-specific subdirectory and places log files there.
     *
     * @param logFileBase The base name of the log file (without extension)
     * @param level The log level (DEBUG, INFO, WARN, ERROR)
     * @param loggerName The name of the logger
     * @param message The message to log
     * @param filePath The path of the file being processed, or empty string if none
     * @param t The exception to log, or null if none
     */
    private static void logToFile(String logFileBase, String level, String loggerName, String message,
                                 String filePath, Throwable t) {
        if (!IS_DEVELOPMENT_MODE) {
            return;
        }

        try {
            String logFilePath;
            
            if (IS_TEST_MODE) {
                // In test mode, create a test-specific subdirectory and place log files there
                String testName = getTestName();
                String testDirName = sanitizeTestName(testName);
                
                // Create the test-specific directory if it doesn't exist
                File testDir = new File(TEST_LOG_BASE_DIR, testDirName);
                if (!testDir.exists()) {
                    boolean created = testDir.mkdirs();
                    if (!created) {
                        System.err.println("Failed to create test-specific directory at: " + testDir.getAbsolutePath());
                    }
                }
                
                // Create the log file path in the test-specific directory
                String fileName = LOG_FILE_PREFIX + "_" + SESSION_TIMESTAMP + "_" + logFileBase + ".log";
                logFilePath = new File(testDir, fileName).getAbsolutePath();
            } else {
                // Normal mode (not test) - use the original behavior
                if (logFileBase.equals(DEBUG_LOG_BASE)) {
                    logFilePath = DEBUG_LOG_FILE_PATH;
                } else if (logFileBase.equals(VALIDATION_LOG_BASE)) {
                    logFilePath = VALIDATION_LOG_FILE_PATH;
                } else {
                    // Fallback for any other log file base (shouldn't happen)
                    // Use the same format: latte_plugin_TIMESTAMP_TYPE.log
                    String fileName = LOG_FILE_PREFIX + "_" + SESSION_TIMESTAMP + "_" + logFileBase + ".log";
                    logFilePath = new File(LOG_DIR_PATH, fileName).getAbsolutePath();
                }
            }

            File file = new File(logFilePath);
            boolean fileExists = file.exists();

            try (FileWriter fw = new FileWriter(file, true);
                 PrintWriter pw = new PrintWriter(fw)) {

                // Add a header if the file is new
                if (!fileExists) {
                    pw.println("=== Latte Plugin Log File ===");
                    pw.println("Created: " + DATE_FORMAT.format(new Date()));
                    pw.println("----------------------------------------");
                }

                // Write the log entry
                pw.print(DATE_FORMAT.format(new Date()));
                pw.print(" [");
                pw.print(level);
                pw.print("] ");
                pw.print(loggerName);

                // Include file path in the log message if available
                if (filePath != null && !filePath.isEmpty()) {
                    pw.print(" [File: ");
                    pw.print(filePath);
                    pw.print("]");
                }

                pw.print(" - ");
                pw.println(message);

                // Write the exception if present
                if (t != null) {
                    t.printStackTrace(pw);
                    pw.println();
                }

                pw.flush();

                // Log success message for debugging
                if (!fileExists) {
                    System.out.println("Created log file: " + file.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            // Don't use logger here to avoid infinite recursion
            System.err.println("Error writing to log file: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    /**
     * Formats a file path for logging.
     * If the file is within the project, returns a relative path.
     * If the file is outside the project, returns an absolute path.
     *
     * @param file The file to format the path for
     * @param project The project to check if the file is within
     * @return The formatted file path, or an empty string if the file is null
     */
    private static String formatFilePath(@Nullable VirtualFile file, @Nullable Project project) {
        if (file == null) {
            return "";
        }

        String absolutePath = file.getPath();

        // If project is null or has no base path, return absolute path
        if (project == null || project.getBasePath() == null) {
            return absolutePath;
        }

        String projectPath = project.getBasePath();

        // Check if the file is within the project
        if (absolutePath.startsWith(projectPath)) {
            // Return path relative to project
            return absolutePath.substring(projectPath.length());
        } else {
            // Return absolute path for files outside the project
            return absolutePath;
        }
    }

    /**
     * Logs a debug message if in development mode.
     *
     * @param logger The logger to use
     * @param message The message to log
     */
    public static void debug(@NotNull Logger logger, @NotNull String message) {
        if (IS_DEVELOPMENT_MODE) {
            logger.debug(message);
            logToFile(DEBUG_LOG_BASE, "DEBUG", logger.getClass().getName(), message, "", null);
        }
    }

    /**
     * Logs a debug message with file information if in development mode.
     *
     * @param logger The logger to use
     * @param message The message to log
     * @param file The file being processed
     * @param project The project the file belongs to
     */
    public static void debug(@NotNull Logger logger, @NotNull String message,
                            @Nullable VirtualFile file, @Nullable Project project) {
        if (IS_DEVELOPMENT_MODE) {
            logger.debug(message);
            logToFile(DEBUG_LOG_BASE, "DEBUG", logger.getClass().getName(), message,
                     formatFilePath(file, project), null);
        }
    }

    /**
     * Logs a debug message with an exception if in development mode.
     *
     * @param logger The logger to use
     * @param message The message to log
     * @param t The exception to log
     */
    public static void debug(@NotNull Logger logger, @NotNull String message, @Nullable Throwable t) {
        if (IS_DEVELOPMENT_MODE) {
            logger.debug(message, t);
            logToFile(DEBUG_LOG_BASE, "DEBUG", logger.getClass().getName(), message, "", t);
        }
    }

    /**
     * Logs a debug message with file information and an exception if in development mode.
     *
     * @param logger The logger to use
     * @param message The message to log
     * @param file The file being processed
     * @param project The project the file belongs to
     * @param t The exception to log
     */
    public static void debug(@NotNull Logger logger, @NotNull String message,
                            @Nullable VirtualFile file, @Nullable Project project, @Nullable Throwable t) {
        if (IS_DEVELOPMENT_MODE) {
            logger.debug(message, t);
            logToFile(DEBUG_LOG_BASE, "DEBUG", logger.getClass().getName(), message,
                     formatFilePath(file, project), t);
        }
    }

    /**
     * Logs an info message if in development mode.
     *
     * @param logger The logger to use
     * @param message The message to log
     */
    public static void info(@NotNull Logger logger, @NotNull String message) {
        if (IS_DEVELOPMENT_MODE) {
            logger.info(message);
            logToFile(DEBUG_LOG_BASE, "INFO", logger.getClass().getName(), message, "", null);
        }
    }

    /**
     * Logs an info message with file information if in development mode.
     *
     * @param logger The logger to use
     * @param message The message to log
     * @param file The file being processed
     * @param project The project the file belongs to
     */
    public static void info(@NotNull Logger logger, @NotNull String message,
                           @Nullable VirtualFile file, @Nullable Project project) {
        if (IS_DEVELOPMENT_MODE) {
            logger.info(message);
            logToFile(DEBUG_LOG_BASE, "INFO", logger.getClass().getName(), message,
                     formatFilePath(file, project), null);
        }
    }

    /**
     * Logs an info message with an exception if in development mode.
     *
     * @param logger The logger to use
     * @param message The message to log
     * @param t The exception to log
     */
    public static void info(@NotNull Logger logger, @NotNull String message, @Nullable Throwable t) {
        if (IS_DEVELOPMENT_MODE) {
            logger.info(message, t);
            logToFile(DEBUG_LOG_BASE, "INFO", logger.getClass().getName(), message, "", t);
        }
    }

    /**
     * Logs an info message with file information and an exception if in development mode.
     *
     * @param logger The logger to use
     * @param message The message to log
     * @param file The file being processed
     * @param project The project the file belongs to
     * @param t The exception to log
     */
    public static void info(@NotNull Logger logger, @NotNull String message,
                           @Nullable VirtualFile file, @Nullable Project project, @Nullable Throwable t) {
        if (IS_DEVELOPMENT_MODE) {
            logger.info(message, t);
            logToFile(DEBUG_LOG_BASE, "INFO", logger.getClass().getName(), message,
                     formatFilePath(file, project), t);
        }
    }

    /**
     * Logs a warning message if in development mode.
     *
     * @param logger The logger to use
     * @param message The message to log
     */
    public static void warn(@NotNull Logger logger, @NotNull String message) {
        if (IS_DEVELOPMENT_MODE) {
            logger.warn(message);
            logToFile(DEBUG_LOG_BASE, "WARN", logger.getClass().getName(), message, "", null);
        }
    }

    /**
     * Logs a warning message with file information if in development mode.
     *
     * @param logger The logger to use
     * @param message The message to log
     * @param file The file being processed
     * @param project The project the file belongs to
     */
    public static void warn(@NotNull Logger logger, @NotNull String message,
                           @Nullable VirtualFile file, @Nullable Project project) {
        if (IS_DEVELOPMENT_MODE) {
            logger.warn(message);
            logToFile(DEBUG_LOG_BASE, "WARN", logger.getClass().getName(), message,
                     formatFilePath(file, project), null);
        }
    }

    /**
     * Logs a warning message with an exception if in development mode.
     *
     * @param logger The logger to use
     * @param message The message to log
     * @param t The exception to log
     */
    public static void warn(@NotNull Logger logger, @NotNull String message, @Nullable Throwable t) {
        if (IS_DEVELOPMENT_MODE) {
            logger.warn(message, t);
            logToFile(DEBUG_LOG_BASE, "WARN", logger.getClass().getName(), message, "", t);
        }
    }

    /**
     * Logs a warning message with file information and an exception if in development mode.
     *
     * @param logger The logger to use
     * @param message The message to log
     * @param file The file being processed
     * @param project The project the file belongs to
     * @param t The exception to log
     */
    public static void warn(@NotNull Logger logger, @NotNull String message,
                           @Nullable VirtualFile file, @Nullable Project project, @Nullable Throwable t) {
        if (IS_DEVELOPMENT_MODE) {
            logger.warn(message, t);
            logToFile(DEBUG_LOG_BASE, "WARN", logger.getClass().getName(), message,
                     formatFilePath(file, project), t);
        }
    }

    /**
     * Logs an error message if in development mode.
     *
     * @param logger The logger to use
     * @param message The message to log
     */
    public static void error(@NotNull Logger logger, @NotNull String message) {
        if (IS_DEVELOPMENT_MODE) {
            logger.error(message);
            logToFile(DEBUG_LOG_BASE, "ERROR", logger.getClass().getName(), message, "", null);
        }
    }

    /**
     * Logs an error message with file information if in development mode.
     *
     * @param logger The logger to use
     * @param message The message to log
     * @param file The file being processed
     * @param project The project the file belongs to
     */
    public static void error(@NotNull Logger logger, @NotNull String message,
                            @Nullable VirtualFile file, @Nullable Project project) {
        if (IS_DEVELOPMENT_MODE) {
            logger.error(message);
            logToFile(DEBUG_LOG_BASE, "ERROR", logger.getClass().getName(), message,
                     formatFilePath(file, project), null);
        }
    }

    /**
     * Logs an error message with an exception if in development mode.
     *
     * @param logger The logger to use
     * @param message The message to log
     * @param t The exception to log
     */
    public static void error(@NotNull Logger logger, @NotNull String message, @Nullable Throwable t) {
        if (IS_DEVELOPMENT_MODE) {
            logger.error(message, t);
            logToFile(DEBUG_LOG_BASE, "ERROR", logger.getClass().getName(), message, "", t);
        }
    }

    /**
     * Logs an error message with file information and an exception if in development mode.
     *
     * @param logger The logger to use
     * @param message The message to log
     * @param file The file being processed
     * @param project The project the file belongs to
     * @param t The exception to log
     */
    public static void error(@NotNull Logger logger, @NotNull String message,
                            @Nullable VirtualFile file, @Nullable Project project, @Nullable Throwable t) {
        if (IS_DEVELOPMENT_MODE) {
            logger.error(message, t);
            logToFile(DEBUG_LOG_BASE, "ERROR", logger.getClass().getName(), message,
                     formatFilePath(file, project), t);
        }
    }

    /**
     * Logs a validation error if in development mode.
     * This method logs to both IntelliJ's built-in logging system and to a separate validation_errors.log file.
     *
     * @param logger The logger to use
     * @param message The error message
     * @param elementText The text of the element where the error occurred
     * @param offset The offset of the element where the error occurred
     */
    public static void logValidationError(@NotNull Logger logger, @NotNull String message,
                                         @Nullable String elementText, int offset) {
        if (IS_DEVELOPMENT_MODE) {
            String fullMessage = "Validation error: " + message;
            if (elementText != null) {
                fullMessage += " at element: " + elementText + " (offset: " + offset + ")";
            }

            logger.warn(fullMessage);
            logToFile(VALIDATION_LOG_BASE, "VALIDATION", logger.getClass().getName(), fullMessage, "", null);
        }
    }

    /**
     * Logs a validation error with file information if in development mode.
     * This method logs to both IntelliJ's built-in logging system and to a separate validation_errors.log file.
     *
     * @param logger The logger to use
     * @param message The error message
     * @param elementText The text of the element where the error occurred
     * @param offset The offset of the element where the error occurred
     * @param file The file being processed
     * @param project The project the file belongs to
     */
    public static void logValidationError(@NotNull Logger logger, @NotNull String message,
                                         @Nullable String elementText, int offset,
                                         @Nullable VirtualFile file, @Nullable Project project) {
        if (IS_DEVELOPMENT_MODE) {
            String fullMessage = "Validation error: " + message;
            if (elementText != null) {
                fullMessage += " at element: " + elementText + " (offset: " + offset + ")";
            }

            logger.warn(fullMessage);
            logToFile(VALIDATION_LOG_BASE, "VALIDATION", logger.getClass().getName(), fullMessage,
                     formatFilePath(file, project), null);
        }
    }

    /**
     * Logs a validation error with an exception if in development mode.
     * This method logs to both IntelliJ's built-in logging system and to a separate validation_errors.log file.
     *
     * @param logger The logger to use
     * @param message The error message
     * @param elementText The text of the element where the error occurred
     * @param offset The offset of the element where the error occurred
     * @param t The exception to log
     */
    public static void logValidationError(@NotNull Logger logger, @NotNull String message,
                                         @Nullable String elementText, int offset, @Nullable Throwable t) {
        if (IS_DEVELOPMENT_MODE) {
            String fullMessage = "Validation error: " + message;
            if (elementText != null) {
                fullMessage += " at element: " + elementText + " (offset: " + offset + ")";
            }

            logger.warn(fullMessage, t);
            logToFile(VALIDATION_LOG_BASE, "VALIDATION", logger.getClass().getName(), fullMessage, "", t);
        }
    }

    /**
     * Logs a validation error with file information and an exception if in development mode.
     * This method logs to both IntelliJ's built-in logging system and to a separate validation_errors.log file.
     *
     * @param logger The logger to use
     * @param message The error message
     * @param elementText The text of the element where the error occurred
     * @param offset The offset of the element where the error occurred
     * @param file The file being processed
     * @param project The project the file belongs to
     * @param t The exception to log
     */
    public static void logValidationError(@NotNull Logger logger, @NotNull String message,
                                         @Nullable String elementText, int offset,
                                         @Nullable VirtualFile file, @Nullable Project project, @Nullable Throwable t) {
        if (IS_DEVELOPMENT_MODE) {
            String fullMessage = "Validation error: " + message;
            if (elementText != null) {
                fullMessage += " at element: " + elementText + " (offset: " + offset + ")";
            }

            logger.warn(fullMessage, t);
            logToFile(VALIDATION_LOG_BASE, "VALIDATION", logger.getClass().getName(), fullMessage,
                     formatFilePath(file, project), t);
        }
    }

    /**
     * Checks if logging is enabled.
     *
     * @return true if logging is enabled, false otherwise
     */
    public static boolean isLoggingEnabled() {
        return IS_DEVELOPMENT_MODE;
    }

    /**
     * Logs a test message to verify that logging is working correctly.
     * This method should be called when the plugin is initialized.
     * Test messages are only logged when running in test mode.
     */
    public static void logTestMessage() {
        if (IS_DEVELOPMENT_MODE && IS_TEST_MODE) {
            System.out.println("LatteLogger: Testing logging system...");
            System.out.println("LatteLogger: Development mode is enabled: " + IS_DEVELOPMENT_MODE);
            System.out.println("LatteLogger: Test mode is enabled: " + IS_TEST_MODE);
            System.out.println("LatteLogger: Log directory is: " + LOG_DIR_PATH);

            // Create a test logger
            Logger testLogger = Logger.getInstance("LatteLogger.Test");

            // Log test messages at different levels
            debug(testLogger, "This is a test debug message");
            info(testLogger, "This is a test info message");
            warn(testLogger, "This is a test warning message");
            error(testLogger, "This is a test error message");
            logValidationError(testLogger, "This is a test validation error", "test element", 0);

            System.out.println("LatteLogger: Test messages logged successfully");
        }
    }
    
    /**
     * Gets the name of the current test from the stack trace.
     * This method looks for a method annotated with @Test or a class that extends TestCase.
     * If no test is found, returns "unknown_test".
     *
     * @return The name of the current test
     */
    private static String getTestName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        
        // Look for a method in a test class
        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            String methodName = element.getMethodName();
            
            // Check if this is likely a test class/method
            if ((className.contains("Test") || className.endsWith("Tests")) && 
                !className.contains("$") && // Exclude inner classes
                !methodName.equals("getTestName") && // Exclude this method
                !methodName.equals("logToFile") && // Exclude calling method
                !methodName.startsWith("access$")) { // Exclude synthetic accessor methods
                
                // Found a potential test method
                return className + "." + methodName;
            }
        }
        
        // If no test method found, return a default name
        return "unknown_test";
    }
    
    /**
     * Sanitizes a test name for use as a directory name.
     * Removes invalid characters, shortens if necessary, and makes it more human-readable.
     *
     * @param testName The full test name (className.methodName)
     * @return A sanitized version suitable for use as a directory name
     */
    private static String sanitizeTestName(String testName) {
        if (testName == null || testName.isEmpty()) {
            return "unknown_test";
        }
        
        // Split into class and method parts
        String[] parts = testName.split("\\.");
        String className = parts.length > 0 ? parts[parts.length - 2] : "";
        String methodName = parts.length > 0 ? parts[parts.length - 1] : "";
        
        // Extract just the simple class name (without package)
        if (className.contains(".")) {
            className = className.substring(className.lastIndexOf('.') + 1);
        }
        
        // Remove "Test" suffix from class name if present
        if (className.endsWith("Test")) {
            className = className.substring(0, className.length() - 4);
        }
        
        // Combine class and method names
        String sanitized = className + "_" + methodName;
        
        // Replace invalid characters with underscores
        sanitized = sanitized.replaceAll("[^a-zA-Z0-9_.-]", "_");
        
        // Limit length to 50 characters to avoid too long directory names
        if (sanitized.length() > 50) {
            sanitized = sanitized.substring(0, 50);
        }
        
        return sanitized;
    }
}