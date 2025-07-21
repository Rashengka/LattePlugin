#!/bin/bash

# run_tests.sh - A wrapper script for running tests with proper Gradle configuration
# This script addresses the "Test events were not received" problem by properly managing
# the Gradle daemon and system properties.

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Display header
echo -e "${YELLOW}==================================================${NC}"
echo -e "${YELLOW}  Latte Plugin Test Runner                        ${NC}"
echo -e "${YELLOW}==================================================${NC}"
echo -e "${YELLOW}This script helps prevent the \"Test events were not received\"${NC}"
echo -e "${YELLOW}error by properly managing the Gradle daemon and system properties.${NC}"
echo -e ""

# Check if any arguments were provided
if [ $# -eq 0 ]; then
  echo -e "${YELLOW}Usage:${NC}"
  echo -e "${BLUE}  ./run_tests.sh [test_class] [test_method] [options]${NC}"
  echo -e ""
  echo -e "${YELLOW}Examples:${NC}"
  echo -e "${BLUE}  ./run_tests.sh cz.hqm.latte.plugin.test.completion.CustomElementsCompletionTest${NC}"
  echo -e "${BLUE}  ./run_tests.sh cz.hqm.latte.plugin.test.completion.CustomElementsCompletionTest testCustomFunctionCompletion${NC}"
  echo -e "${BLUE}  ./run_tests.sh cz.hqm.latte.plugin.test.completion.CustomElementsCompletionTest --clean-environment${NC}"
  echo -e ""
  echo -e "${YELLOW}Options:${NC}"
  echo -e "${BLUE}  --no-daemon           ${NC}Run Gradle without daemon"
  echo -e "${BLUE}  --refresh-deps        ${NC}Clean build cache and refresh dependencies"
  echo -e "${BLUE}  --debug               ${NC}Show debug information (--info --stacktrace)"
  echo -e "${BLUE}  --restart-daemon      ${NC}Restart Gradle daemon before running tests (default: true)"
  echo -e "${BLUE}  --fix-injectors       ${NC}Fix language injector conflicts (default: true)"
  echo -e "${BLUE}  --no-build-cache      ${NC}Disable Gradle build cache"
  echo -e "${BLUE}  --no-config-cache     ${NC}Disable Gradle configuration cache"
  echo -e "${BLUE}  --clean-environment   ${NC}Run with clean environment (combines multiple options)"
  echo -e ""
  echo -e "${YELLOW}For running all tests:${NC}"
  echo -e "${BLUE}  ./gradlew --stop && ./gradlew clean check${NC}"
  echo -e ""
  exit 0
fi

# Make sure the test_runner.sh script is executable
chmod +x src/test/resources/test_runner.sh

# Forward all arguments to the test_runner.sh script
echo -e "${YELLOW}Forwarding request to test_runner.sh...${NC}"
echo -e ""
src/test/resources/test_runner.sh "$@"

# Display footer with helpful information
echo -e ""
echo -e "${YELLOW}==================================================${NC}"
echo -e "${YELLOW}  Test Run Complete                               ${NC}"
echo -e "${YELLOW}==================================================${NC}"
echo -e "${YELLOW}If you encounter \"Test events were not received\" errors:${NC}"
echo -e "${BLUE}1. Try running with the --clean-environment option:${NC}"
echo -e "${BLUE}   ./run_tests.sh [test_class] --clean-environment${NC}"
echo -e ""
echo -e "${BLUE}2. Or manually restart the Gradle daemon:${NC}"
echo -e "${BLUE}   ./gradlew --stop${NC}"
echo -e "${BLUE}   ./gradlew clean check${NC}"
echo -e ""
echo -e "${YELLOW}For more information, see LANGUAGE_INJECTOR_FIX.md${NC}"