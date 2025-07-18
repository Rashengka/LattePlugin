package org.latte.plugin.test.util;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.Test;

/**
 * JUnit 5 extension that formats specific error messages during test execution.
 * This extension redirects standard error to capture and format specific error messages
 * as brief, informative messages.
 */
public class ErrorFormattingExtension implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        // Start redirecting standard error before any tests are run
        TestErrorHandler.startRedirecting();
    }
}
