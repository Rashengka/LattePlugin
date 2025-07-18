# Latte Plugin Test Suite

This directory contains a comprehensive test suite for the Latte Plugin for JetBrains IDEs. The test suite is designed to verify that the plugin correctly supports all Latte template language features according to the official Latte documentation.

## Directory Structure

```
src/test/
├── java/                           # Java test classes
│   └── cz/hqm/latte/test/          # Test package
│       ├── file/                   # File type tests
│       ├── lexer/                  # Lexer and parser tests
│       ├── highlighting/           # Syntax highlighting tests
│       ├── completion/             # Code completion tests
│       ├── documentation/          # Documentation tests
│       └── features/               # Feature-specific tests
└── resources/                      # Test resources
    ├── testData/                   # Test data files
    │   ├── core/                   # Core syntax test files
    │   ├── macros/                 # Macro test files
    │   ├── attributes/             # Attribute test files
    │   └── filters/                # Filter test files
    ├── LATTE_TEST_PLAN.md          # Comprehensive test plan
    ├── TEST_REPORT_TEMPLATE.md     # Template for test reports
    └── test_runner.sh              # Script for validating test files
```

## Test Files

The test suite includes the following test files:

1. **Core Syntax**
   - `testData/core/variables.latte` - Tests variable output and basic expressions

2. **Macros**
   - `testData/macros/conditionals.latte` - Tests conditional macros (if, else, elseif)
   - `testData/macros/loops.latte` - Tests loop macros (foreach, for, while)
   - `testData/macros/blocks.latte` - Tests block and include macros

3. **Attributes**
   - `testData/attributes/nattributes.latte` - Tests n:attributes

4. **Filters**
   - `testData/filters/filters.latte` - Tests filters for variable modification

## Running the Tests

### Manual Testing

1. Open the test files in a JetBrains IDE with the Latte Plugin installed
2. Follow the test plan in `LATTE_TEST_PLAN.md` to verify each feature
3. Use the `TEST_REPORT_TEMPLATE.md` to document your test results

### Automated Testing

#### Using the Test Runner Script

The `test_runner.sh` script can be used to validate the syntax of the test files:

```bash
cd /path/to/clone/Rashengka/LattePlugin
chmod +x src/test/resources/test_runner.sh
./src/test/resources/test_runner.sh
```

This script will:
- Check all .latte files in the testData directory for syntax errors
- Verify that braces and HTML tags are properly matched
- Generate a test report in `src/test/resources/test_results.log`

#### Running JUnit Tests

To run the JUnit tests (when implemented):

```bash
./gradlew test
```

## Test Plan

The `LATTE_TEST_PLAN.md` file contains a comprehensive test plan for the Latte Plugin. It includes:

1. A list of all test files
2. Detailed test cases for each Latte feature
3. Guidelines for test execution
4. Test reporting guidelines

## Test Report Template

The `TEST_REPORT_TEMPLATE.md` file provides a template for documenting test results. It includes:

1. Test information section
2. Test summary table
3. Detailed test results sections for each feature
4. Sections for recording issues, recommendations, and conclusions
5. Guidelines for interpreting test results

## Adding New Tests

To add new tests:

1. Create a new test file in the appropriate subdirectory of `testData/`
2. Add test cases that demonstrate the feature being tested
3. Update the test plan in `LATTE_TEST_PLAN.md` if necessary
4. If adding a new feature category, create a new subdirectory in `testData/`

## Guidelines for Writing Test Files

1. Each test file should focus on a specific feature or group of related features
2. Include comments that explain what each test case is testing
3. Try to cover both common and edge cases
4. Keep test files concise and focused
5. Ensure that test files are valid Latte templates

## Troubleshooting

If you encounter issues with the test suite:

1. Verify that the Latte Plugin is properly installed in your IDE
2. Check that the test files are in the correct location
3. Ensure that the test runner script has execute permissions
4. Check the test results log for any error messages

## Contributing

Contributions to the test suite are welcome! Please follow these guidelines:

1. Follow the existing structure and naming conventions
2. Document new test cases in the test plan
3. Ensure that new test files are valid Latte templates
4. Update this README if necessary

## License

This test suite is licensed under the same license as the Latte Plugin.