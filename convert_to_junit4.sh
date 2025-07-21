#!/bin/bash

# Script to convert JUnit 5 tests to JUnit 4
# This script will:
# 1. Find all test files that import JUnit 5 annotations
# 2. Replace JUnit 5 imports with JUnit 4 imports
# 3. Replace JUnit 5 assertions import with JUnit 4 assertions import
# 4. Replace JUnit 5 annotations with JUnit 4 annotations
# 5. Change method visibility from protected to public for methods annotated with @Before or @After

# Find all test files that import JUnit 5 annotations
TEST_FILES=$(find src/test/java -name "*.java" -type f | xargs grep -l "org.junit.jupiter")

# Process each file
for file in $TEST_FILES; do
  echo "Converting $file to JUnit 4..."
  
  # Replace JUnit 5 imports with JUnit 4 imports
  sed -i '' 's/import org.junit.jupiter.api.Test;/import org.junit.Test;/g' "$file"
  sed -i '' 's/import org.junit.jupiter.api.BeforeEach;/import org.junit.Before;/g' "$file"
  sed -i '' 's/import org.junit.jupiter.api.AfterEach;/import org.junit.After;/g' "$file"
  
  # Remove TestInfo import if present
  sed -i '' '/import org.junit.jupiter.api.TestInfo;/d' "$file"
  
  # Replace JUnit 5 assertions import with JUnit 4 assertions import
  sed -i '' 's/import static org.junit.jupiter.api.Assertions.\*;/import static org.junit.Assert.\*;/g' "$file"
  
  # Replace JUnit 5 annotations with JUnit 4 annotations
  sed -i '' 's/@BeforeEach/@Before/g' "$file"
  sed -i '' 's/@AfterEach/@After/g' "$file"
  
  # Change method visibility from protected to public for methods annotated with @Before or @After
  sed -i '' 's/@Before\s*@Override\s*protected void setUp/@Before\n    @Override\n    public void setUp/g' "$file"
  sed -i '' 's/@After\s*@Override\s*protected void tearDown/@After\n    @Override\n    public void tearDown/g' "$file"
  
  # Remove TestInfo parameter from setUp method if present
  sed -i '' 's/public void setUp(TestInfo testInfo)/public void setUp()/g' "$file"
  sed -i '' 's/public void setUpJUnit5(TestInfo testInfo)/public void setUpJUnit5()/g' "$file"
  
  echo "Conversion complete for $file"
done

echo "All test files have been converted to JUnit 4."