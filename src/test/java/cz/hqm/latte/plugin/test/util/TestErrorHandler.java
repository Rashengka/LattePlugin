package cz.hqm.latte.plugin.test.util;

import java.io.ByteArrayOutputStream;
import org.junit.Test;
import java.io.PrintStream;
import org.junit.Test;
import java.util.Arrays;
import org.junit.Test;
import java.util.List;
import org.junit.Test;
import java.util.regex.Matcher;
import org.junit.Test;
import java.util.regex.Pattern;
import org.junit.Test;

/**
 * Utility class for handling and formatting error messages during test execution.
 * This class redirects standard error, captures specific error messages, and formats them as brief info messages.
 */
public class TestErrorHandler {
    private static final PrintStream originalErr = System.err;
    private static ByteArrayOutputStream errContent;
    private static PrintStream customErr;
    private static boolean redirecting = false;

    // Patterns to match specific error messages
    private static final List<Pattern> ERROR_PATTERNS = Arrays.asList(
            // Configuration file for j.u.l.LogManager does not exist
            Pattern.compile("Configuration file for j\\.u\\.l\\.LogManager does not exist:.*"),
            // VFS Log version differs from the implementation version
            Pattern.compile("\\[\\s*\\d+\\]\\s+WARN\\s+-\\s+#c\\.i\\.o\\.v\\.n\\.p\\.l\\.VfsLog\\s+-\\s+VFS Log version differs from the implementation version:.*"),
            // Font-related errors
            Pattern.compile("\\[\\s*\\d+\\]\\s+WARN\\s+-\\s+#c\\.i\\.o\\.e\\.i\\.FontFamilyServiceImpl\\s+-\\s+no such method:.*"),
            Pattern.compile("java\\.lang\\.NoSuchMethodException: no such method: sun\\.font\\.Font2D\\.getTypographicFamilyName\\(\\)String/invokeVirtual.*"),
            Pattern.compile("java\\.lang\\.NoSuchMethodException: no such method: sun\\.font\\.Font2D\\.getTypographicSubfamilyName\\(\\)String/invokeVirtual.*"),
            Pattern.compile("Caused by: java\\.lang\\.NoSuchMethodError: 'java\\.lang\\.String sun\\.font\\.Font2D\\.getTypographicFamilyName\\(\\)'.*"),
            Pattern.compile("Caused by: java\\.lang\\.NoSuchMethodError: 'java\\.lang\\.String sun\\.font\\.Font2D\\.getTypographicSubfamilyName\\(\\)'.*"),
            // OpenJDK warning about archived non-system classes
            Pattern.compile("OpenJDK 64-Bit Server VM warning: Archived non-system classes are disabled because the java\\.system\\.class\\.loader property is specified.*")
    );

    /**
     * Starts redirecting standard error to capture and format specific error messages.
     */
    public static void startRedirecting() {
        if (redirecting) {
            return;
        }
        
        errContent = new ByteArrayOutputStream();
        customErr = new PrintStream(errContent) {
            @Override
            public void println(String x) {
                if (shouldFormatMessage(x)) {
                    // Format the message as a brief info message
                    originalErr.println("[INFO] Known test environment issue: " + getShortErrorDescription(x));
                } else {
                    // Pass through other messages unchanged
                    originalErr.println(x);
                }
            }
            
            @Override
            public void print(String s) {
                // For print (without newline), we need to buffer and check when a complete line is formed
                if (s.contains("\n")) {
                    String[] lines = s.split("\n", -1);
                    for (int i = 0; i < lines.length - 1; i++) {
                        String line = lines[i];
                        if (shouldFormatMessage(line)) {
                            originalErr.println("[INFO] Known test environment issue: " + getShortErrorDescription(line));
                        } else {
                            originalErr.println(line);
                        }
                    }
                    // Handle the last part (which might not end with a newline)
                    if (!lines[lines.length - 1].isEmpty()) {
                        originalErr.print(lines[lines.length - 1]);
                    }
                } else {
                    originalErr.print(s);
                }
            }
        };
        
        System.setErr(customErr);
        redirecting = true;
    }

    /**
     * Stops redirecting standard error.
     */
    public static void stopRedirecting() {
        if (!redirecting) {
            return;
        }
        
        System.setErr(originalErr);
        customErr.close();
        redirecting = false;
    }

    /**
     * Checks if a message should be formatted as a brief info message.
     */
    private static boolean shouldFormatMessage(String message) {
        if (message == null) {
            return false;
        }
        
        for (Pattern pattern : ERROR_PATTERNS) {
            if (pattern.matcher(message).matches()) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Gets a short description of an error message.
     */
    private static String getShortErrorDescription(String message) {
        if (message == null) {
            return "Unknown error";
        }
        
        // Configuration file for j.u.l.LogManager does not exist
        if (message.contains("Configuration file for j.u.l.LogManager does not exist")) {
            return "Missing LogManager configuration file";
        }
        
        // VFS Log version differs from the implementation version
        if (message.contains("VFS Log version differs from the implementation version")) {
            return "VFS Log version mismatch";
        }
        
        // Font-related errors
        if (message.contains("no such method: sun.font.Font2D.getTypographicFamilyName()")) {
            return "Font2D.getTypographicFamilyName() method not available";
        }
        
        if (message.contains("no such method: sun.font.Font2D.getTypographicSubfamilyName()")) {
            return "Font2D.getTypographicSubfamilyName() method not available";
        }
        
        // OpenJDK warning about archived non-system classes
        if (message.contains("OpenJDK 64-Bit Server VM warning: Archived non-system classes are disabled")) {
            return "Archived non-system classes are disabled";
        }
        
        // Default case
        return message.length() > 100 ? message.substring(0, 100) + "..." : message;
    }
}
