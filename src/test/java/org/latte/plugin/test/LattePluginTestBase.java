package org.latte.plugin.test;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.latte.plugin.test.util.TestErrorHandler;
import java.io.File;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Base class for all Latte plugin tests.
 * Extends BasePlatformTestCase which provides the necessary infrastructure for testing IntelliJ IDEA plugins.
 * Adapted to work with JUnit 5 annotations and assertions.
 */
public abstract class LattePluginTestBase extends BasePlatformTestCase {

    static {
        // Start redirecting standard error as early as possible
        TestErrorHandler.startRedirecting();
    }

    /**
     * JUnit 5 setup method that calls the JUnit 3 setUp method.
     * This allows tests to use either JUnit 3 or JUnit 5 style.
     */
    @BeforeEach
    public void setUpJUnit5(TestInfo testInfo) throws Exception {
        // Get the test method name from TestInfo
        String methodName = testInfo.getTestMethod().map(Method::getName).orElse(null);
        if (methodName != null) {
            // Set the name for JUnit 3 compatibility
            setName(methodName);
        }
        
        // Call the JUnit 3 setUp method
        setUp();
    }

    /**
     * JUnit 5 teardown method that calls the JUnit 3 tearDown method.
     * This allows tests to use either JUnit 3 or JUnit 5 style.
     */
    @AfterEach
    public void tearDownJUnit5() throws Exception {
        // Call the JUnit 3 tearDown method
        tearDown();
    }

    @Override
    protected void setUp() throws Exception {
        // Ensure error redirection is active
        TestErrorHandler.startRedirecting();
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        // We don't stop redirecting here to ensure errors during tearDown are also formatted
    }

    /**
     * Returns the path to the test data directory.
     * Test data files should be placed in this directory.
     */
    @Override
    protected String getTestDataPath() {
        return new File("src/test/resources/testData").getAbsolutePath();
    }

    /**
     * Creates a Latte file with the given content.
     *
     * @param content The content of the Latte file
     */
    protected void createLatteFile(String content) {
        myFixture.configureByText("test.latte", content);
    }
}
