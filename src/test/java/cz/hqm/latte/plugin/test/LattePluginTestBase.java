package cz.hqm.latte.plugin.test;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.openapi.application.PathManager;
import cz.hqm.latte.plugin.test.util.TestErrorHandler;
import org.junit.Before;
import org.junit.After;

/**
 * Base class for all Latte plugin tests.
 * Extends BasePlatformTestCase which provides the necessary infrastructure for testing IntelliJ IDEA plugins.
 * Uses JUnit 4 annotations and assertions.
 */
public abstract class LattePluginTestBase extends BasePlatformTestCase {

    static {
        // Start redirecting standard error as early as possible
        TestErrorHandler.startRedirecting();
    }

    @Override
    protected void setUp() throws Exception {
        // Ensure error redirection is active
        TestErrorHandler.startRedirecting();
        
        // Set multiple system properties to completely disable language injection
        // This prevents the AssertionError in InjectedLanguageManagerImpl.pushInjectors
        System.setProperty("idea.ignore.duplicated.injectors", "true");
        System.setProperty("idea.disable.language.injection", "true");
        System.setProperty("idea.injected.language.manager.disabled", "true");
        System.setProperty("idea.skip.injected.language.setup", "true");
        System.setProperty("idea.test.no.injected.language", "true");
        System.setProperty("idea.test.light.injected.language.manager", "true");
        System.setProperty("idea.test.disable.language.injection", "true");
        
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            super.tearDown();
            // We don't stop redirecting here to ensure errors during tearDown are also formatted
            
            // Reset all system properties for language injectors
            // This ensures that the properties don't affect other tests
            System.clearProperty("idea.ignore.duplicated.injectors");
            System.clearProperty("idea.disable.language.injection");
            System.clearProperty("idea.injected.language.manager.disabled");
            System.clearProperty("idea.skip.injected.language.setup");
            System.clearProperty("idea.test.no.injected.language");
            System.clearProperty("idea.test.light.injected.language.manager");
            System.clearProperty("idea.test.disable.language.injection");
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Exception during tearDown: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Returns the path to the test data directory.
     * Test data files should be placed in this directory.
     */
    @Override
    protected String getTestDataPath() {
        return System.getProperty("user.dir") + "/src/test/resources/testData";
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
