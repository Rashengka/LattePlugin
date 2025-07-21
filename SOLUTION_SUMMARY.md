# JUnit 4 Migration Solution Summary

## Problem
The project was experiencing the "Test events were not received" error when running tests with Gradle. This error was caused by a mix of JUnit 4 and JUnit 5 annotations and configurations in the test files.

## Solution
The solution was to standardize all tests to use JUnit 4 consistently, as requested in the issue description: "prosím převeď všechny testy na jednotné JUnit 4" (please convert all tests to unified JUnit 4).

### Changes Made

1. **Updated build.gradle**:
   - Changed test configuration to use JUnit 4: `useJUnit()` instead of `useJUnitPlatform()`
   - Commented out JUnit 5 dependencies and kept only JUnit 4 dependency

2. **Updated LattePluginTestBase.java**:
   - Replaced JUnit 5 imports with JUnit 4 imports
   - Removed JUnit 5 bridge methods
   - Updated class documentation to reflect JUnit 4 usage

3. **Updated all test files**:
   - Created and ran a script (`convert_to_junit4.sh`) to convert all test files from JUnit 5 to JUnit 4
   - Replaced JUnit 5 imports with JUnit 4 imports
   - Replaced JUnit 5 annotations with JUnit 4 annotations
   - Changed method visibility from `protected` to `public` for methods annotated with `@Before` or `@After`
   - Removed `TestInfo` parameter from setup methods

4. **Fixed specific issues**:
   - Removed `ErrorFormattingExtension.java` which was a JUnit 5 extension not used in the codebase
   - Fixed JUnit 5 assertions imports in `LatteErrorDetectionTest.java`
   - Fixed method names in `LattePerformanceBenchmarkTest.java` to match JUnit 4 lifecycle methods

### Verification
All tests now run successfully with JUnit 4, and the "Test events were not received" error has been resolved.

## Benefits
1. **Consistency**: All tests now use JUnit 4 consistently, making the codebase more maintainable.
2. **Compatibility**: JUnit 4 is fully compatible with IntelliJ Platform testing framework, particularly `BasePlatformTestCase`.
3. **Reliability**: The "Test events were not received" error has been resolved, ensuring that test results are properly reported.

## Future Considerations
If there's a need to use JUnit 5 features in the future, a more comprehensive migration to JUnit 5 would be required, including:
1. Updating the IntelliJ Platform testing framework to support JUnit 5
2. Updating all test files to use JUnit 5 consistently
3. Updating the build.gradle file to use JUnit 5 platform

For now, JUnit 4 provides all the necessary functionality for testing the Latte plugin.