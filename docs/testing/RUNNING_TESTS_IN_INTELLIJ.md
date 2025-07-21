# Running Tests in IntelliJ IDEA

This guide explains how to run the Latte Plugin tests directly in IntelliJ IDEA, avoiding the font-related errors that commonly occur.

## Background

When running tests for the Latte Plugin, you may encounter font-related errors such as:
```
java.lang.NoSuchMethodError: 'java.lang.String sun.font.Font2D.getTypographicFamilyName()'
```

These errors occur when IntelliJ IDEA accesses internal JDK APIs that are not available in the current JDK. While these errors can be safely ignored (as they don't affect the actual test logic), they can cause tests to fail when run directly in IntelliJ IDEA.

The `test_runner.sh` script handles these errors gracefully, but when running tests directly in IntelliJ IDEA, additional configuration is needed.

## Solution: Configure IntelliJ IDEA Run Configuration

To run tests directly in IntelliJ IDEA without font-related errors, you need to create a run configuration with specific VM options:

1. Open IntelliJ IDEA and navigate to your Latte Plugin project
2. Click on "Run" → "Edit Configurations..."
3. Click the "+" button to create a new configuration
4. Select "JUnit"
5. Configure the test:
   - Name: `LatteSettingsTest` (or any other test name)
   - Test kind: `Class`
   - Class: `cz.hqm.latte.plugin.test.settings.LatteSettingsTest` (or the class you want to test)
   - VM options: Add the following VM options:

```
--add-exports=java.desktop/sun.font=ALL-UNNAMED
--add-opens=java.desktop/sun.font=ALL-UNNAMED
--add-opens=java.base/java.lang=ALL-UNNAMED
-Djava.util.logging.config.file=src/test/resources/test-log.properties
-Djava.util.logging.manager=java.util.logging.LogManager
-Dcom.intellij.openapi.vfs.newvfs.persistent.VfsLog.level=OFF
-Dcom.intellij.openapi.editor.impl.FontFamilyServiceImpl.level=OFF
-Dcom.intellij.ui.util.StyleSheetUtil.level=OFF
-Djava.util.logging.LogManager.level=SEVERE
-Didea.platform.prefix=PlatformTestCaseTest
-Didea.suppress.known.test.exceptions=true
-Didea.ignore.vfs.log.version.mismatch=true
-Didea.headless.enable.font.checking=false
-Didea.use.headless.ui=true
-Didea.ui.skip.css.missing.warning=true
-Didea.font.system.disable=true
-Djava.awt.headless=true
-Dsun.java2d.noddraw=true
-Dsun.java2d.d3d=false
-Dsun.java2d.opengl=false
-Dswing.bufferPerWindow=false
-Dlogback.configurationFile=logback-test.xml
-Didea.log.config.file=logback-test.xml
-Didea.use.mock.ui=true
-Didea.use.minimal.fonts=true
-Didea.tests.overwrite.temp.jdk=true
-Didea.font.system.disable=true
-Dsun.font.useJDKFontMetrics=false
-Didea.use.system.fonts=false
```

6. Click "Apply" and then "OK"
7. Run the test using this configuration

## Creating a Template Configuration

To avoid adding these VM options every time you create a new test configuration, you can create a template:

1. Follow steps 1-5 above to create a configuration with all the VM options
2. Instead of clicking "OK", click "Save as template..."
3. Name the template "Latte Plugin Test"
4. Click "OK"

Now, whenever you create a new test configuration, you can select "Latte Plugin Test" as the template, and all the VM options will be pre-filled.

## Alternative: Use the test_runner.sh Script

If you prefer not to configure IntelliJ IDEA, you can always use the `test_runner.sh` script to run tests:

```bash
./src/test/resources/test_runner.sh cz.hqm.latte.plugin.test.settings.LatteSettingsTest
```

This script handles the font-related errors gracefully and provides clear output about the test results.

## Troubleshooting

If you still encounter issues when running tests in IntelliJ IDEA:

1. Make sure you're using a compatible JDK version (8-19, preferably JDK 17)
2. Verify that all the VM options are correctly added to the run configuration
3. Try running the tests with the `test_runner.sh` script to confirm that the tests themselves are working correctly
4. Check the IntelliJ IDEA log files for any additional errors or warnings

Remember that font-related errors can be safely ignored as they don't affect the actual test logic.