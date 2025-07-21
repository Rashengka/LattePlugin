# Test Configuration for Latte Plugin

This document explains the configuration settings used for running tests in the Latte Plugin project.

## Font-Related Error Handling

When running tests with Gradle, you might encounter font-related errors like:

```
java.lang.NoSuchMethodError: 'java.lang.String sun.font.Font2D.getTypographicFamilyName()'
java.lang.NoSuchMethodError: 'java.lang.String sun.font.Font2D.getTypographicSubfamilyName()'
```

These errors occur because IntelliJ IDEA 2023.1.5 tries to access internal JDK APIs that might not be available or might have changed in certain JDK versions.

### Solution

To address these issues, the following system properties have been added to the check task in `build.gradle`:

```gradle
check {
    // Run tests in headless mode to avoid font-related errors
    systemProperty 'java.awt.headless', 'true'
    // Force IntelliJ to use core fonts instead of system fonts
    systemProperty 'idea.force.use.core.fonts', 'true'
    // Suppress warnings about missing font methods
    systemProperty 'idea.suppress.font.warnings', 'true'
    // Force the use of a headless toolkit
    systemProperty 'awt.toolkit', 'sun.awt.CToolkit'
    // Disable native file system access on Windows
    systemProperty 'idea.use.native.fs.for.win', 'false'
    // Disable font fallback mechanism
    systemProperty 'idea.font.fallback', 'false'
    // Set a fixed UI scale to avoid font scaling issues
    systemProperty 'idea.ui.scale', '1.0'
    // Disable font ligatures
    systemProperty 'editor.disable.ligatures', 'true'
    // Skip loading editor SDK fonts on first start
    systemProperty 'idea.ui.skip.editor.sdk.fonts.on.first.start', 'true'
    // Completely disable font subsystem initialization
    systemProperty 'idea.font.initialization.disabled', 'true'
}
```

### Known Issues

Despite the system properties above, you might still see several types of warnings and error messages in the test output. **All of these can be safely ignored** as they don't affect the test results. The tests will still pass successfully despite these messages.

#### Font-Related Warnings

```
WARN - #c.i.o.e.i.FontFamilyServiceImpl - no such method: sun.font.Font2D.getTypographicFamilyName()String/invokeVirtual
java.lang.NoSuchMethodException: no such method: sun.font.Font2D.getTypographicFamilyName()String/invokeVirtual
```

These warnings occur because:
1. The IntelliJ platform attempts to access internal JDK APIs during initialization
2. These APIs might not be available or might have changed in certain JDK versions
3. The warnings are logged at the initialization phase, before our system properties can fully take effect
4. The actual test execution is not affected by these warnings

#### Logging Configuration Warning

```
Configuration file for j.u.l.LogManager does not exist: /path/to/ideaIU/test-log.properties
```

This warning indicates that the Java Util Logging (j.u.l) system is looking for a configuration file that doesn't exist. This is normal during test execution and doesn't affect the test results.

#### VFS Log Version Warning

```
WARN - #c.i.o.v.n.p.l.VfsLog - VFS Log version differs from the implementation version: log null vs implementation -43
```

This warning is related to the IntelliJ platform's Virtual File System (VFS) and indicates a version mismatch between the log and the implementation. This is expected during test execution and doesn't affect the test results.

#### JVM Warning About Archived Classes

```
OpenJDK 64-Bit Server VM warning: Archived non-system classes are disabled because the java.system.class.loader property is specified
```

This warning is related to the JVM's class loading mechanism and is expected during test execution. It doesn't affect the test results.

### Attempts to Intercept and Format These Messages

We've attempted to intercept and format these messages to make them less verbose in the test output. However, due to the way the IntelliJ platform and the test runner interact, it's challenging to completely eliminate or reformat these messages.

The current approach is to:
1. Use system properties to suppress as many of these messages as possible
2. Document the remaining messages as known issues that can be safely ignored
3. Provide utility classes (`TestErrorHandler` and `ErrorFormattingExtension`) that can be used for future work on improving the test output

#### Explanation of Properties

1. **java.awt.headless=true**
   - Runs the tests in headless mode, which means that UI components won't try to access the actual display or graphics system.
   - This is a common approach for running tests that don't need UI interaction.

2. **idea.force.use.core.fonts=true**
   - Forces IntelliJ to use core fonts instead of trying to access system fonts.
   - This avoids calls to problematic methods in `sun.font.Font2D`.

3. **idea.suppress.font.warnings=true**
   - Suppresses warnings related to font issues.
   - Prevents the errors from being logged.

4. **awt.toolkit=sun.awt.CToolkit**
   - Forces the use of a headless toolkit implementation.
   - Helps avoid graphics-related errors in headless environments.

5. **idea.use.native.fs.for.win=false**
   - Disables native file system access on Windows.
   - Prevents potential issues with file system access during tests.

6. **idea.font.fallback=false**
   - Disables the font fallback mechanism.
   - Prevents IntelliJ from trying to find alternative fonts when a requested font is not available.

7. **idea.ui.scale=1.0**
   - Sets a fixed UI scale to avoid font scaling issues.
   - Ensures consistent font rendering across different environments.

8. **editor.disable.ligatures=true**
   - Disables font ligatures in the editor.
   - Simplifies font rendering and avoids potential issues with ligature processing.

9. **idea.ui.skip.editor.sdk.fonts.on.first.start=true**
   - Skips loading editor SDK fonts on first start.
   - Reduces font-related initialization that might cause errors.

10. **idea.font.initialization.disabled=true**
    - Completely disables font subsystem initialization.
    - Prevents the font-related code from running, which is the source of the warnings.

### JDK Compatibility

The Latte Plugin has the following Java requirements:

- **Recommended**: JDK 17 (LTS)
- **Compatible**: JDK 8 through JDK 19
- **Not Compatible**: JDK 20 or newer

These requirements are based on:
1. The plugin is compiled with Java 17 compatibility
2. Gradle 7.6 (required for building the plugin) is compatible with JDK 8-19, but not with JDK 20+

For more details on Java installation and configuration, see [JAVA_INSTALL.md](JAVA_INSTALL.md).

## Other Test Configuration

The check task is configured to use JUnit 4 and to log detailed information about test execution:

```gradle
check {
    useJUnit()
    testLogging {
        events "passed", "skipped", "failed", "standardOut", "standardError"
        showStandardStreams = true
        exceptionFormat = "full"
    }
}
```

This configuration ensures that:
- Tests are run using JUnit 4 (as of July 2025, the project was converted from JUnit 5 to JUnit 4 for better compatibility with the IntelliJ Platform)
- Detailed logs are generated for test execution, including standard output and standard error
- Full exception details are included in the logs

## Troubleshooting

If you encounter other issues when running tests, consider:

1. **Checking your JDK version**:
   ```bash
   java -version
   ```
   Ensure you're using a compatible JDK (8-19, preferably JDK 17).

2. **Clearing Gradle cache**:
   ```bash
   ./gradlew cleanBuildCache
   ```

3. **Running with debug logging**:
   ```bash
   ./gradlew check --debug
   ```

4. **Running specific tests**:
   ```bash
   ./gradlew check --tests "cz.hqm.latte.plugin.test.YourTestClass"
   ```