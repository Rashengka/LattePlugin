package org.latte.plugin.test;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import java.io.File;

/**
 * Base class for all Latte plugin tests.
 * Extends BasePlatformTestCase which provides the necessary infrastructure for testing IntelliJ IDEA plugins.
 */
public abstract class LattePluginTestBase extends BasePlatformTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
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