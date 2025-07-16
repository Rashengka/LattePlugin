#!/bin/bash
# Latte Test Runner Script
# This script validates Latte test files and checks for parsing errors

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

# Test data directory
TEST_DATA_DIR="src/test/resources/testData"

# Log file
LOG_FILE="src/test/resources/test_results.log"

# Initialize log file
echo "Latte Test Runner - $(date)" > $LOG_FILE
echo "===============================" >> $LOG_FILE

# Function to validate a Latte file
validate_latte_file() {
    local file=$1
    echo -e "${YELLOW}Validating $file...${NC}"
    echo "Testing: $file" >> $LOG_FILE
    
    # Check if file exists
    if [ ! -f "$file" ]; then
        echo -e "${RED}Error: File not found${NC}"
        echo "  Error: File not found" >> $LOG_FILE
        return 1
    fi
    
    # Check for basic syntax errors (unmatched braces, etc.)
    local open_braces=$(grep -o "{" "$file" | wc -l)
    local close_braces=$(grep -o "}" "$file" | wc -l)
    
    if [ $open_braces -ne $close_braces ]; then
        echo -e "${RED}Error: Unmatched braces - $open_braces opening vs $close_braces closing${NC}"
        echo "  Error: Unmatched braces - $open_braces opening vs $close_braces closing" >> $LOG_FILE
        return 1
    fi
    
    # Check for unclosed macros
    local unclosed_macros=$(grep -E "{[a-zA-Z]" "$file" | grep -v -E "{/[a-zA-Z]" | grep -v -E "{[a-zA-Z][^}]*}" | wc -l)
    if [ $unclosed_macros -gt 0 ]; then
        echo -e "${RED}Warning: Possible unclosed macros detected${NC}"
        echo "  Warning: Possible unclosed macros detected" >> $LOG_FILE
    fi
    
    # Check for HTML validity (basic check)
    local open_tags=$(grep -o "<[a-zA-Z]" "$file" | wc -l)
    local close_tags=$(grep -o "</[a-zA-Z]" "$file" | wc -l)
    
    if [ $open_tags -ne $close_tags ]; then
        echo -e "${YELLOW}Warning: Possible unmatched HTML tags - $open_tags opening vs $close_tags closing${NC}"
        echo "  Warning: Possible unmatched HTML tags - $open_tags opening vs $close_tags closing" >> $LOG_FILE
    fi
    
    echo -e "${GREEN}Validation complete${NC}"
    echo "  Validation complete" >> $LOG_FILE
    return 0
}

# Function to run tests on all Latte files
run_tests() {
    echo -e "${YELLOW}Running tests on all Latte files...${NC}"
    echo "Running tests on all Latte files" >> $LOG_FILE
    
    local total_files=0
    local passed_files=0
    local failed_files=0
    
    # Find all .latte files in the test data directory
    for file in $(find $TEST_DATA_DIR -name "*.latte"); do
        total_files=$((total_files + 1))
        
        if validate_latte_file "$file"; then
            passed_files=$((passed_files + 1))
        else
            failed_files=$((failed_files + 1))
        fi
        
        echo "" # Empty line for readability
    done
    
    # Print summary
    echo -e "${YELLOW}Test Summary:${NC}"
    echo -e "  ${GREEN}Total files: $total_files${NC}"
    echo -e "  ${GREEN}Passed: $passed_files${NC}"
    echo -e "  ${RED}Failed: $failed_files${NC}"
    
    echo "Test Summary:" >> $LOG_FILE
    echo "  Total files: $total_files" >> $LOG_FILE
    echo "  Passed: $passed_files" >> $LOG_FILE
    echo "  Failed: $failed_files" >> $LOG_FILE
}

# Main function
main() {
    echo -e "${YELLOW}Latte Test Runner${NC}"
    echo -e "${YELLOW}================${NC}"
    
    # Check if test data directory exists
    if [ ! -d "$TEST_DATA_DIR" ]; then
        echo -e "${RED}Error: Test data directory not found: $TEST_DATA_DIR${NC}"
        exit 1
    fi
    
    # Run tests
    run_tests
    
    echo -e "${YELLOW}Test results saved to $LOG_FILE${NC}"
}

# Run the main function
main