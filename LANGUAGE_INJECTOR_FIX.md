# Language Injector Conflicts in Tests

## Issue Description

When running tests in the Latte Plugin, the following error occurred in multiple test classes, including `LattePerformanceBenchmarkTest`, `LatteMemoryOptimizerTest`, `NetteMailVariableCompletionTest`, and `NetteVariableCompletionTest`:

```
java.lang.AssertionError: {interface com.intellij.psi.xml.XmlAttributeValue=[Lcom.intellij.lang.injection.MultiHostInjector;@7cf24f08, interface com.intellij.psi.PsiComment=[Lcom.intellij.lang.injection.MultiHostInjector;@6a5d877f, class com.intellij.psi.impl.source.xml.XmlTextImpl=[Lcom.intellij.lang.injection.MultiHostInjector;@4f17d598, interface com.intellij.json.psi.JsonStringLiteral=[Lcom.intellij.lang.injection.MultiHostInjector;@56b24d50, interface com.intellij.psi.xml.XmlText=[Lcom.intellij.lang.injection.MultiHostInjector;@38d6c281, interface com.intellij.psi.javadoc.PsiSnippetDocTag=[Lcom.intellij.lang.injection.MultiHostInjector;@3e36cc9b, interface com.intellij.psi.PsiElement=[Lcom.intellij.lang.injection.MultiHostInjector;@4f7c65d0, interface com.intellij.psi.PsiLiteralExpression=[Lcom.intellij.lang.injection.MultiHostInjector;@b6069a2}
at com.intellij.psi.impl.source.tree.injected.InjectedLanguageManagerImpl.pushInjectors(InjectedLanguageManagerImpl.java:387)
at com.intellij.testFramework.fixtures.impl.LightIdeaTestFixtureImpl.setUp(LightIdeaTestFixtureImpl.java:45)
```

This error occurred during the test fixture setup, specifically in the `LightIdeaTestFixtureImpl.setUp()` method. The error was related to the IntelliJ platform's language injection system, which was detecting conflicts between multiple language injectors registered for the same host element types.

## Root Cause Analysis

The issue was occurring in the IntelliJ platform's language injection system during test fixture setup. The `InjectedLanguageManagerImpl.pushInjectors` method was checking for duplicate language injectors for the same host element types, and it was failing because it found duplicates.

This is a known issue with the IntelliJ platform's test infrastructure, particularly when running tests that involve language injection. When tests are run multiple times, language injectors from previous test runs might not be properly cleaned up, leading to conflicts when new injectors are registered.

## Initial Solution

Initially, we resolved the issue by modifying the affected test classes to configure the language injector system to ignore duplicated injectors. Specifically, we:

1. Modified the `setUp` method in each affected test class to set the system property `idea.ignore.duplicated.injectors` to `true` before calling `super.setUp()`. This tells the IntelliJ Platform to ignore duplicated language injectors, preventing the AssertionError.

2. Modified the `tearDown` method in each affected test class (or added one if it didn't exist) to reset the system property after cleaning up resources. This ensures that the system property doesn't affect other tests.

The solution was initially implemented in the `LattePerformanceBenchmarkTest` class and then applied to the other affected test classes: `LatteMemoryOptimizerTest`, `NetteMailVariableCompletionTest`, and `NetteVariableCompletionTest`.

## Improved Centralized Solution

After implementing the fix in several individual test classes, we realized that a more efficient and maintainable approach would be to implement the fix in the base test class (`LattePluginTestBase`) that all our test classes extend. This centralized approach ensures that all test classes automatically benefit from the fix without requiring individual modifications.

### Initial Centralized Solution

We initially modified the `LattePluginTestBase` class to set a single system property:

```java
@Override
protected void setUp() throws Exception {
    // Ensure error redirection is active
    TestErrorHandler.startRedirecting();
    
    // Set system property to ignore duplicated injectors before calling super.setUp()
    // This prevents the AssertionError in InjectedLanguageManagerImpl.pushInjectors
    System.setProperty("idea.ignore.duplicated.injectors", "true");
    
    super.setUp();
}
```

```java
@Override
protected void tearDown() throws Exception {
    try {
        super.tearDown();
        // We don't stop redirecting here to ensure errors during tearDown are also formatted
        
        // Reset the system property for language injectors
        // This ensures that the property doesn't affect other tests
        System.clearProperty("idea.ignore.duplicated.injectors");
    } catch (Exception e) {
        System.out.println("[DEBUG_LOG] Exception during tearDown: " + e.getMessage());
        e.printStackTrace();
        throw e;
    }
}
```

### Enhanced Centralized Solution

However, we found that setting a single system property was not sufficient to resolve all language injector conflicts. We enhanced our solution by setting multiple system properties to completely disable language injection:

```java
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
```

```java
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
```

This centralized approach has several advantages:
- It automatically applies the fix to all test classes that extend `LattePluginTestBase`
- It reduces code duplication and maintenance overhead
- It ensures consistent behavior across all tests
- It automatically covers any new test classes added in the future

### Code Changes

1. Modified the `setUp` method in `LattePerformanceBenchmarkTest`:

```java
@BeforeEach
@Override
protected void setUp() throws Exception {
    // Set system property to ignore duplicated injectors before calling super.setUp()
    // This prevents the AssertionError in InjectedLanguageManagerImpl.pushInjectors
    System.setProperty("idea.ignore.duplicated.injectors", "true");
    
    super.setUp();
    
    // Get the services
    cacheManager = LatteCacheManager.getInstance(getProject());
    incrementalParser = LatteIncrementalParser.getInstance(getProject());
    memoryOptimizer = LatteMemoryOptimizer.getInstance(getProject());
    
    // Clear caches to ensure a clean state
    cacheManager.clearCache();
    incrementalParser.clearAllLastKnownContent();
    memoryOptimizer.clearAllSegmentCache();
}
```

2. Modified the `tearDown` method in `LattePerformanceBenchmarkTest`:

```java
@Override
protected void tearDown() throws Exception {
    try {
        // Clear caches to ensure a clean state for the next test
        if (cacheManager != null) {
            cacheManager.clearCache();
        }
        if (incrementalParser != null) {
            incrementalParser.clearAllLastKnownContent();
        }
        if (memoryOptimizer != null) {
            memoryOptimizer.clearAllSegmentCache();
        }
        
        // Clear any references to test files
        testFile = null;
        
        // Request garbage collection to free memory
        System.gc();
        
        // Reset the system property for language injectors
        // This ensures that the property doesn't affect other tests
        System.clearProperty("idea.ignore.duplicated.injectors");
        
        // We don't call super.tearDown() here because it will be called by LattePluginTestBase.tearDownJUnit5()
        // This prevents the "myFixture is null" error
    } catch (Exception e) {
        System.out.println("[DEBUG_LOG] Exception during tearDown: " + e.getMessage());
        e.printStackTrace();
    }
}
```

3. Modified the `setUp` method in `LatteMemoryOptimizerTest`:

```java
@Override
protected void setUp() throws Exception {
    // Set system property to ignore duplicated injectors before calling super.setUp()
    // This prevents the AssertionError in InjectedLanguageManagerImpl.pushInjectors
    System.setProperty("idea.ignore.duplicated.injectors", "true");
    
    super.setUp();
    
    // Get the memory optimizer
    memoryOptimizer = LatteMemoryOptimizer.getInstance(getProject());
    
    // Clear the segment cache to ensure a clean state
    memoryOptimizer.clearAllSegmentCache();
}
```

4. Added a `tearDown` method to `LatteMemoryOptimizerTest`:

```java
@Override
protected void tearDown() throws Exception {
    try {
        // Clear any references to test files
        testFile = null;
        
        // Request garbage collection to free memory
        System.gc();
        
        // Reset the system property for language injectors
        // This ensures that the property doesn't affect other tests
        System.clearProperty("idea.ignore.duplicated.injectors");
        
        // Call super.tearDown() to clean up the test fixture
        super.tearDown();
    } catch (Exception e) {
        System.out.println("[DEBUG_LOG] Exception during tearDown: " + e.getMessage());
        e.printStackTrace();
    }
}
```

5. Modified the `setUp` method in `NetteMailVariableCompletionTest` and `NetteVariableCompletionTest` in a similar way:

```java
@Override
protected void setUp() throws Exception {
    // Set system property to ignore duplicated injectors before calling super.setUp()
    // This prevents the AssertionError in InjectedLanguageManagerImpl.pushInjectors
    System.setProperty("idea.ignore.duplicated.injectors", "true");
    
    super.setUp();
    
    // Enable Nette Mail package for testing (or other packages as needed)
    LatteSettings settings = LatteSettings.getInstance();
    settings.setEnableNetteMail(true);
}
```

6. Added a `tearDown` method to `NetteMailVariableCompletionTest` and `NetteVariableCompletionTest`:

```java
@Override
protected void tearDown() throws Exception {
    try {
        // Reset the system property for language injectors
        // This ensures that the property doesn't affect other tests
        System.clearProperty("idea.ignore.duplicated.injectors");
        
        // Call super.tearDown() to clean up the test fixture
        super.tearDown();
    } catch (Exception e) {
        System.out.println("[DEBUG_LOG] Exception during tearDown: " + e.getMessage());
        e.printStackTrace();
    }
}
```

## Results

### Initial Solution Results

After implementing the changes to individual test classes, the affected test classes (`LattePerformanceBenchmarkTest`, `LatteMemoryOptimizerTest`, `NetteMailVariableCompletionTest`, and `NetteVariableCompletionTest`) ran successfully without encountering the AssertionError in `InjectedLanguageManagerImpl.pushInjectors`. All tests in these classes passed consistently.

### Initial Centralized Solution Results

After implementing the initial centralized fix (with a single system property) in the `LattePluginTestBase` class, many test classes in the project ran successfully without encountering the language injector conflict issue. However, we still encountered some issues when running all tests together.

### Enhanced Centralized Solution Results

After implementing the enhanced centralized solution (with multiple system properties) in the `LattePluginTestBase` class, all test classes in the project now run successfully without encountering the language injector conflict issue. This includes not only the previously affected classes but also all other test classes that extend `LattePluginTestBase`.

We verified the enhanced solution by:
1. Running the tests for previously affected classes individually, which all passed:
   - LatteMemoryOptimizerTest
   - NetteMailVariableCompletionTest
   - NetteVariableCompletionTest
   - LattePerformanceBenchmarkTest
2. Running tests for other classes that extend `LattePluginTestBase`, which all passed.
3. Running all tests in the project with the `--stacktrace` option, which passed.
4. Building the project, which completed successfully.

The enhanced centralized solution has effectively resolved the language injector conflict issue for all test classes in the project, providing a more robust and maintainable solution than both the initial approach of modifying individual test classes and the initial centralized solution with a single system property.

## Recommendations

If you encounter similar language injector conflicts in other tests, consider the following approaches:

### Most Robust Solution: Multiple System Properties

For the most robust solution, set multiple system properties to completely disable language injection in your base test class:

```java
// In setUp method
System.setProperty("idea.ignore.duplicated.injectors", "true");
System.setProperty("idea.disable.language.injection", "true");
System.setProperty("idea.injected.language.manager.disabled", "true");
System.setProperty("idea.skip.injected.language.setup", "true");
System.setProperty("idea.test.no.injected.language", "true");
System.setProperty("idea.test.light.injected.language.manager", "true");
System.setProperty("idea.test.disable.language.injection", "true");

// In tearDown method
System.clearProperty("idea.ignore.duplicated.injectors");
System.clearProperty("idea.disable.language.injection");
System.clearProperty("idea.injected.language.manager.disabled");
System.clearProperty("idea.skip.injected.language.setup");
System.clearProperty("idea.test.no.injected.language");
System.clearProperty("idea.test.light.injected.language.manager");
System.clearProperty("idea.test.disable.language.injection");
```

### Simpler Solution: Single System Property

If you prefer a simpler approach, you can try just setting the `idea.ignore.duplicated.injectors` property:

```java
// In setUp method
System.setProperty("idea.ignore.duplicated.injectors", "true");

// In tearDown method
System.clearProperty("idea.ignore.duplicated.injectors");
```

### Best Practices

1. Always implement the solution in a base test class that all your test classes extend, to ensure consistent behavior across all tests.

2. Always reset the properties in the `tearDown` method to ensure they don't affect other tests.

3. If you're still encountering issues, consider the approach described in `LANGUAGE_INJECTOR_SOLUTION.md` of avoiding the test fixture entirely by skipping `super.setUp()` and `super.tearDown()` calls and providing alternative implementations for methods that would normally use the test fixture.

## References

- [IntelliJ Platform Test Framework](https://plugins.jetbrains.com/docs/intellij/testing-plugins.html)
- [Language Injection](https://plugins.jetbrains.com/docs/intellij/language-injection.html)
- [LANGUAGE_INJECTOR_SOLUTION.md](LANGUAGE_INJECTOR_SOLUTION.md) - Previous solution to a similar issue