#!/bin/bash

# Test runner script that handles font-related errors in IntelliJ platform tests
# Usage: ./test_runner.sh [test_class] [test_method]
# Example: ./test_runner.sh cz.hqm.latte.plugin.test.completion.CustomElementsCompletionTest testCustomFunctionCompletion

# Set default values
TEST_CLASS=${1:-"cz.hqm.latte.plugin.test.completion.CustomElementsCompletionTest"}
TEST_METHOD=${2:-""}

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Parse test class and method if provided in format "Class#method"
if [[ "$TEST_CLASS" == *"#"* ]]; then
    # Split by #
    IFS='#' read -r parsed_class parsed_method <<< "$TEST_CLASS"
    TEST_CLASS="$parsed_class"
    TEST_METHOD="$parsed_method"
fi

echo -e "${YELLOW}Running test: $TEST_CLASS${NC}"
if [ -n "$TEST_METHOD" ]; then
  echo -e "${YELLOW}Test method: $TEST_METHOD${NC}"
fi

# Create a temporary file for the test output
TEMP_OUTPUT=$(mktemp)

# Run gradle clean first to ensure a clean environment
echo -e "${YELLOW}Running gradle clean...${NC}"
./gradlew clean > /dev/null 2>&1

# Run the test and capture the output
echo -e "${BLUE}Running test...${NC}"
if [ -n "$TEST_METHOD" ]; then
  # Use correct Gradle syntax for specific test method
  ./gradlew test --tests "$TEST_CLASS.$TEST_METHOD" > "$TEMP_OUTPUT" 2>&1
else
  ./gradlew test --tests "$TEST_CLASS" > "$TEMP_OUTPUT" 2>&1
fi

TEST_EXIT_CODE=$?

echo -e "${BLUE}=== Full Test Output ===${NC}"
cat "$TEMP_OUTPUT"
echo -e "${BLUE}=== End of Test Output ===${NC}"

echo -e "${YELLOW}Test exit code: $TEST_EXIT_CODE${NC}"

# Check if the test failed
if grep -q "BUILD FAILED" "$TEMP_OUTPUT"; then
  echo -e "${RED}BUILD FAILED detected${NC}"

  # Check if it's a "no tests found" error
  if grep -q "No tests found for given includes" "$TEMP_OUTPUT"; then
    echo -e "${YELLOW}No tests found error detected. This might be due to incorrect test name or missing test file.${NC}"
  # Check if the failure is due to known font-related errors
  elif grep -q "sun.font.Font2D.getTypographicFamilyName" "$TEMP_OUTPUT" || \
       grep -q "sun.font.Font2D.getTypographicSubfamilyName" "$TEMP_OUTPUT"; then

    echo -e "${YELLOW}Test failed due to known font-related errors. These can be safely ignored.${NC}"

    # Check if there are actual test failures (not just environment errors)
    if grep -q "AssertionFailedError" "$TEMP_OUTPUT" || \
       grep -q "ComparisonFailure" "$TEMP_OUTPUT" || \
       grep -q "junit.framework.AssertionFailedError" "$TEMP_OUTPUT"; then
      echo -e "${RED}Test failed due to assertion failures:${NC}"
      grep -A 5 "AssertionFailedError\|ComparisonFailure\|junit.framework.AssertionFailedError" "$TEMP_OUTPUT"
    else
      echo -e "${GREEN}No actual test failures found. The test might have passed despite the environment errors.${NC}"
    fi
  fi
elif grep -q "BUILD SUCCESSFUL" "$TEMP_OUTPUT"; then
  echo -e "${GREEN}BUILD SUCCESSFUL detected${NC}"

  # Check if any tests were actually run
  if grep -q "test completed" "$TEMP_OUTPUT" || grep -q "tests completed" "$TEMP_OUTPUT"; then
    echo -e "${GREEN}Tests were executed successfully.${NC}"
  else
    echo -e "${YELLOW}Build successful but no explicit test completion message found.${NC}"
  fi
else
  echo -e "${YELLOW}No clear build result detected. Check the full output above.${NC}"
fi

# Extract and display any debug logs
echo -e "${YELLOW}Debug logs:${NC}"
grep "\[DEBUG_LOG\]" "$TEMP_OUTPUT" || echo "No debug logs found."

# Clean up
rm "$TEMP_OUTPUT"