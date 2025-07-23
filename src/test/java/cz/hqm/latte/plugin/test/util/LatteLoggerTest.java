package cz.hqm.latte.plugin.test.util;

import com.intellij.openapi.diagnostic.Logger;
import cz.hqm.latte.plugin.util.LatteLogger;
import org.junit.Test;

/**
 * Test for the LatteLogger class.
 * This test is used to verify that the logging functionality works correctly,
 * especially the test-specific log directory structure.
 */
public class LatteLoggerTest {

    private static final Logger LOG = Logger.getInstance(LatteLoggerTest.class);

    /**
     * Test that debug logging works correctly.
     */
    @Test
    public void testDebugLogging() {
        LatteLogger.debug(LOG, "This is a test debug message from LatteLoggerTest");
    }

    /**
     * Test that info logging works correctly.
     */
    @Test
    public void testInfoLogging() {
        LatteLogger.info(LOG, "This is a test info message from LatteLoggerTest");
    }

    /**
     * Test that warning logging works correctly.
     */
    @Test
    public void testWarnLogging() {
        LatteLogger.warn(LOG, "This is a test warning message from LatteLoggerTest");
    }

    /**
     * Test that error logging works correctly.
     *
     * Note: The IntelliJ Logger.error() method is designed to throw a Throwable with the error message,
     * which is expected behavior. This test verifies that the exception is thrown correctly.
     */
    @Test(expected = Throwable.class)
    public void testErrorLogging() {
        LatteLogger.error(LOG, "This is a test error message from LatteLoggerTest");
    }

    /**
     * Test that validation error logging works correctly.
     */
    @Test
    public void testValidationErrorLogging() {
        LatteLogger.logValidationError(LOG, "This is a test validation error from LatteLoggerTest", "test element", 0);
    }
}