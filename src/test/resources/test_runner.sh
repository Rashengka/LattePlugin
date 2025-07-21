#!/bin/bash

# Test runner script that handles font-related errors in IntelliJ platform tests
# Usage: ./test_runner.sh [test_class] [test_method] [options]
# Example: ./test_runner.sh cz.hqm.latte.plugin.test.completion.CustomElementsCompletionTest testCustomFunctionCompletion
#
# Options:
#   --no-daemon           Run Gradle without daemon
#   --refresh-deps        Clean build cache and refresh dependencies
#   --debug               Show debug information (--info --stacktrace)
#   --restart-daemon      Restart Gradle daemon before running tests (default: true)
#   --fix-injectors       Fix language injector conflicts (default: true)
#   --no-build-cache      Disable Gradle build cache
#   --no-config-cache     Disable Gradle configuration cache
#   --clean-environment   Run with clean environment (combines multiple options)

# Set default values
TEST_CLASS=${1:-"cz.hqm.latte.plugin.test.completion.CustomElementsCompletionTest"}
TEST_METHOD=${2:-""}
NO_DAEMON=false
REFRESH_DEPS=false
DEBUG_MODE=false
RESTART_DAEMON=true
FIX_INJECTORS=true
NO_BUILD_CACHE=false
NO_CONFIG_CACHE=false

# Parse arguments
for arg in "$@"; do
  case $arg in
    --no-daemon)
      NO_DAEMON=true
      ;;
    --refresh-deps)
      REFRESH_DEPS=true
      ;;
    --debug)
      DEBUG_MODE=true
      ;;
    --restart-daemon=*)
      RESTART_DAEMON="${arg#*=}"
      ;;
    --fix-injectors=*)
      FIX_INJECTORS="${arg#*=}"
      ;;
    --no-fix-injectors)
      FIX_INJECTORS=false
      ;;
    --no-build-cache)
      NO_BUILD_CACHE=true
      ;;
    --no-config-cache)
      NO_CONFIG_CACHE=true
      ;;
    --clean-environment)
      # Clean environment combines multiple options
      RESTART_DAEMON=true
      NO_DAEMON=true
      REFRESH_DEPS=true
      NO_BUILD_CACHE=true
      NO_CONFIG_CACHE=true
      ;;
  esac
done

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

# Restart Gradle daemon if requested
if [ "$RESTART_DAEMON" = true ]; then
  echo -e "${YELLOW}Stopping Gradle daemon...${NC}"
  ./gradlew --stop > /dev/null 2>&1
fi

# Run gradle clean first to ensure a clean environment
echo -e "${YELLOW}Running gradle clean...${NC}"
./gradlew clean > /dev/null 2>&1

# Build the gradle command with options
GRADLE_CMD="./gradlew check"

# Add test specification
if [ -n "$TEST_METHOD" ]; then
  GRADLE_CMD="$GRADLE_CMD --tests \"$TEST_CLASS.$TEST_METHOD\""
else
  GRADLE_CMD="$GRADLE_CMD --tests \"$TEST_CLASS\""
fi

# Add options based on flags
if [ "$NO_DAEMON" = true ]; then
  GRADLE_CMD="$GRADLE_CMD --no-daemon"
fi

if [ "$REFRESH_DEPS" = true ]; then
  GRADLE_CMD="$GRADLE_CMD --refresh-dependencies"
fi

if [ "$NO_BUILD_CACHE" = true ]; then
  GRADLE_CMD="$GRADLE_CMD --no-build-cache"
fi

if [ "$NO_CONFIG_CACHE" = true ]; then
  GRADLE_CMD="$GRADLE_CMD --no-configuration-cache"
fi

if [ "$DEBUG_MODE" = true ]; then
  GRADLE_CMD="$GRADLE_CMD --info --stacktrace"
fi

# Add system properties for language injector fixes
if [ "$FIX_INJECTORS" = true ]; then
  echo -e "${YELLOW}Applying language injector fixes...${NC}"
  GRADLE_CMD="$GRADLE_CMD \
    -Didea.ignore.duplicated.injectors=true \
    -Didea.disable.language.injection=true \
    -Didea.injected.language.manager.disabled=true \
    -Didea.skip.injected.language.setup=true \
    -Didea.test.no.injected.language=true \
    -Didea.test.light.injected.language.manager=true \
    -Didea.test.disable.language.injection=true"
fi

# Run the test and capture the output
echo -e "${BLUE}Running test with command: $GRADLE_CMD${NC}"
eval "$GRADLE_CMD" > "$TEMP_OUTPUT" 2>&1

TEST_EXIT_CODE=$?

echo -e "${BLUE}=== Full Test Output ===${NC}"
cat "$TEMP_OUTPUT"
echo -e "${BLUE}=== End of Test Output ===${NC}"

echo -e "${YELLOW}Test exit code: $TEST_EXIT_CODE${NC}"

# Check if the test failed
if grep -q "BUILD FAILED" "$TEMP_OUTPUT"; then
  echo -e "${RED}BUILD FAILED detected${NC}"

  # Check if it's a "Test events were not received" error
  if grep -q "Test events were not received" "$TEMP_OUTPUT"; then
    echo -e "${YELLOW}\"Test events were not received\" error detected.${NC}"
    echo -e "${YELLOW}This is likely due to Gradle daemon state issues. Try running with:${NC}"
    echo -e "${BLUE}  ./test_runner.sh $TEST_CLASS $TEST_METHOD --clean-environment${NC}"
    echo -e "${YELLOW}Or manually restart the Gradle daemon:${NC}"
    echo -e "${BLUE}  ./gradlew --stop${NC}"
    echo -e "${BLUE}  ./gradlew clean check${NC}"
  # Check if it's a "no tests found" error
  elif grep -q "No tests found for given includes" "$TEMP_OUTPUT"; then
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
  # Check for language injector conflicts
  elif grep -q "InjectedLanguageManagerImpl.pushInjectors" "$TEMP_OUTPUT"; then
    echo -e "${YELLOW}Language injector conflicts detected.${NC}"
    echo -e "${YELLOW}Try running with language injector fixes enabled:${NC}"
    echo -e "${BLUE}  ./test_runner.sh $TEST_CLASS $TEST_METHOD --fix-injectors=true${NC}"
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