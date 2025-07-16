#!/bin/bash

# check_versions.sh
# Script to check if the installed Java and Gradle versions meet the requirements
# for the LattePlugin project.

# Color codes for output formatting
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=== LattePlugin Version Checker ===${NC}"
echo "Checking installed Java and Gradle versions..."
echo

# Function to print error message and exit
error_exit() {
    echo -e "${RED}ERROR: $1${NC}"
    echo
    echo -e "Please refer to the following documentation files for installation instructions:"
    echo -e "- Java: ${BLUE}JAVA_INSTALL.md${NC}"
    echo -e "- Gradle: ${BLUE}GRADLE_7.6_INSTALL.md${NC}"
    echo
    exit 1
}

# Function to print warning message
warning() {
    echo -e "${YELLOW}WARNING: $1${NC}"
    echo
}

# Function to print success message
success() {
    echo -e "${GREEN}$1${NC}"
    echo
}

# Check if Java is installed
if ! command -v java &> /dev/null; then
    error_exit "Java is not installed or not in PATH."
fi

# Check Java version
java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
echo "Detected Java version: $java_version"

# Extract major version number
if [[ $java_version == 1.* ]]; then
    # Java 8 or earlier uses 1.x format
    java_major_version=$(echo $java_version | sed -E 's/1\.([0-9]+).*/\1/')
else
    # Java 9+ uses x format
    java_major_version=$(echo $java_version | sed -E 's/([0-9]+).*/\1/')
fi

# Check if Java version is compatible
if [[ $java_major_version -lt 8 || $java_major_version -gt 19 ]]; then
    error_exit "Java version $java_major_version is not compatible with LattePlugin. Compatible versions are JDK 8-19 (JDK 17 recommended)."
elif [[ $java_major_version -ne 17 ]]; then
    warning "Java version $java_major_version is compatible, but JDK 17 is recommended for optimal performance."
else
    success "Java version $java_major_version is compatible and recommended."
fi

# Check if Gradle is installed
if ! command -v gradle &> /dev/null; then
    error_exit "Gradle is not installed or not in PATH."
fi

# Check Gradle version
gradle_version=$(gradle --version | grep "Gradle " | awk '{print $2}')
echo "Detected Gradle version: $gradle_version"

# Extract major and minor version numbers
gradle_major_version=$(echo $gradle_version | cut -d. -f1)
gradle_minor_version=$(echo $gradle_version | cut -d. -f2)

# Check if Gradle version is compatible
if [[ $gradle_major_version -ne 7 || $gradle_minor_version -ne 6 ]]; then
    error_exit "Gradle version $gradle_version is not compatible with LattePlugin. Gradle 7.6 is required."
else
    success "Gradle version $gradle_version is compatible."
fi

# Check if the Gradle wrapper is present
if [[ ! -f "./gradlew" || ! -f "./gradlew.bat" ]]; then
    warning "Gradle wrapper files (gradlew, gradlew.bat) not found. It's recommended to use the Gradle wrapper for building the project."
    echo "You can create the wrapper by running: gradle wrapper"
    echo
fi

# All checks passed
echo -e "${GREEN}=== All version requirements are met! ===${NC}"
echo "You can proceed with building the LattePlugin project."
echo

exit 0