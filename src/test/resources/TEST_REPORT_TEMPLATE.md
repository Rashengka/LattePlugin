# Latte Plugin Test Report

## Test Information

- **Tester:** [Your Name]
- **Date:** [Test Date]
- **Plugin Version:** [Plugin Version]
- **IDE Version:** [IDE Version]
- **OS:** [Operating System]

## Test Summary

| Category | Total Tests | Passed | Failed | Skipped |
|----------|-------------|--------|--------|---------|
| Core Syntax | | | | |
| Conditional Macros | | | | |
| Loop Macros | | | | |
| Block Macros | | | | |
| Attributes | | | | |
| Filters | | | | |
| **Total** | | | | |

## Detailed Test Results

### 1. Core Syntax Tests

#### 1.1 Variable Output

| ID | Test Case | Expected Result | Actual Result | Status | Notes |
|----|-----------|-----------------|---------------|--------|-------|
| 1.1.1 | Simple variable | Variable value is output | | | |
| 1.1.2 | Object property | Object property value is output | | | |
| 1.1.3 | Array access | Array element value is output | | | |
| 1.1.4 | Method call | Method return value is output | | | |
| 1.1.5 | Complex expression | Conditional expression result is output | | | |
| 1.1.6 | Direct output | Concatenated string is output | | | |
| 1.1.7 | HTML escaping | HTML is properly escaped | | | |

#### 1.2 Comments

| ID | Test Case | Expected Result | Actual Result | Status | Notes |
|----|-----------|-----------------|---------------|--------|-------|
| 1.2.1 | Single-line comment | Comment is not rendered in output | | | |
| 1.2.2 | Multi-line comment | Comment is not rendered in output | | | |

### 2. Macro Tests

#### 2.1 Conditional Macros

| ID | Test Case | Expected Result | Actual Result | Status | Notes |
|----|-----------|-----------------|---------------|--------|-------|
| 2.1.1 | Simple if | Content is conditionally rendered | | | |
| 2.1.2 | If-else | Appropriate branch is rendered | | | |
| 2.1.3 | If-elseif-else | Appropriate branch is rendered | | | |
| 2.1.4 | Nested if | Nested conditions work correctly | | | |
| 2.1.5 | Complex condition | Complex boolean logic works | | | |

#### 2.2 Loop Macros

| ID | Test Case | Expected Result | Actual Result | Status | Notes |
|----|-----------|-----------------|---------------|--------|-------|
| 2.2.1 | Simple foreach | Loop iterates over all items | | | |
| 2.2.2 | Foreach with key | Keys and values are accessible | | | |
| 2.2.3 | Nested foreach | Nested loops work correctly | | | |
| 2.2.4 | Foreach with iterator | Iterator variables work | | | |
| 2.2.5 | For loop | For loop works correctly | | | |
| 2.2.6 | While loop | While loop works correctly | | | |
| 2.2.7 | Break and continue | Break and continue work | | | |

#### 2.3 Block and Include Macros

| ID | Test Case | Expected Result | Actual Result | Status | Notes |
|----|-----------|-----------------|---------------|--------|-------|
| 2.3.1 | Define block | Block is defined and rendered | | | |
| 2.3.2 | Include template | Template is included | | | |
| 2.3.3 | Include with params | Template is included with parameters | | | |
| 2.3.4 | Define without printing | Block is defined but not rendered | | | |
| 2.3.5 | Include defined block | Defined block is rendered | | | |
| 2.3.6 | Extend parent template | Template inheritance works | | | |
| 2.3.7 | Override parent block | Parent block is overridden | | | |
| 2.3.8 | Capture output | Output is captured to variable | | | |

### 3. Attribute Tests

#### 3.1 Conditional Attributes

| ID | Test Case | Expected Result | Actual Result | Status | Notes |
|----|-----------|-----------------|---------------|--------|-------|
| 3.1.1 | n:if | Element is conditionally rendered | | | |
| 3.1.2 | n:ifset | Element is rendered if variable is set | | | |
| 3.1.3 | n:inner-if | Inner content is conditionally rendered | | | |
| 3.1.4 | n:class | Class is conditionally added | | | |
| 3.1.5 | n:attr | Attribute is conditionally added | | | |
| 3.1.6 | n:tag | Tag name is conditionally changed | | | |

#### 3.2 Loop Attributes

| ID | Test Case | Expected Result | Actual Result | Status | Notes |
|----|-----------|-----------------|---------------|--------|-------|
| 3.2.1 | n:foreach | Element is repeated for each item | | | |
| 3.2.2 | n:inner-foreach | Inner content is repeated | | | |
| 3.2.3 | Combining attributes | Multiple attributes work together | | | |

### 4. Filter Tests

#### 4.1 String Manipulation Filters

| ID | Test Case | Expected Result | Actual Result | Status | Notes |
|----|-----------|-----------------|---------------|--------|-------|
| 4.1.1 | upper | Text is converted to uppercase | | | |
| 4.1.2 | lower | Text is converted to lowercase | | | |
| 4.1.3 | firstUpper | First letter is capitalized | | | |
| 4.1.4 | capitalize | First letter of each word is capitalized | | | |
| 4.1.5 | truncate | Text is truncated to specified length | | | |
| 4.1.6 | substring | Substring is extracted | | | |
| 4.1.7 | trim | Whitespace is trimmed | | | |
| 4.1.8 | padLeft/padRight | Text is padded to specified length | | | |
| 4.1.9 | replace | Substring is replaced | | | |
| 4.1.10 | stripHtml | HTML tags are removed | | | |

#### 4.2 Formatting Filters

| ID | Test Case | Expected Result | Actual Result | Status | Notes |
|----|-----------|-----------------|---------------|--------|-------|
| 4.2.1 | date | Date is formatted according to format string | | | |
| 4.2.2 | number | Number is formatted with specified decimals | | | |
| 4.2.3 | bytes | Bytes are formatted as human-readable size | | | |
| 4.2.4 | percent | Number is formatted as percentage | | | |

#### 4.3 Escaping Filters

| ID | Test Case | Expected Result | Actual Result | Status | Notes |
|----|-----------|-----------------|---------------|--------|-------|
| 4.3.1 | escape | HTML is escaped | | | |
| 4.3.2 | escapeUrl | URL is escaped | | | |
| 4.3.3 | noescape | HTML is not escaped | | | |

#### 4.4 Multiple Filters

| ID | Test Case | Expected Result | Actual Result | Status | Notes |
|----|-----------|-----------------|---------------|--------|-------|
| 4.4.1 | Chained filters | Filters are applied in sequence | | | |
| 4.4.2 | Filters with params | Filters with parameters work in sequence | | | |

## IDE Integration Tests

### 5.1 Syntax Highlighting

| ID | Test Case | Expected Result | Actual Result | Status | Notes |
|----|-----------|-----------------|---------------|--------|-------|
| 5.1.1 | Macro highlighting | Macros are highlighted correctly | | | |
| 5.1.2 | Variable highlighting | Variables are highlighted correctly | | | |
| 5.1.3 | Attribute highlighting | n:attributes are highlighted correctly | | | |
| 5.1.4 | Filter highlighting | Filters are highlighted correctly | | | |
| 5.1.5 | Comment highlighting | Comments are highlighted correctly | | | |

### 5.2 Code Completion

| ID | Test Case | Expected Result | Actual Result | Status | Notes |
|----|-----------|-----------------|---------------|--------|-------|
| 5.2.1 | Macro completion | Macro suggestions appear after { | | | |
| 5.2.2 | Attribute completion | n:attribute suggestions appear | | | |
| 5.2.3 | Filter completion | Filter suggestions appear after | | | | |

### 5.3 Documentation

| ID | Test Case | Expected Result | Actual Result | Status | Notes |
|----|-----------|-----------------|---------------|--------|-------|
| 5.3.1 | Macro documentation | Documentation appears for macros | | | |
| 5.3.2 | Attribute documentation | Documentation appears for n:attributes | | | |
| 5.3.3 | Filter documentation | Documentation appears for filters | | | |

## Issues Found

| ID | Description | Severity | Steps to Reproduce | Expected vs Actual |
|----|-------------|----------|-------------------|-------------------|
| | | | | |
| | | | | |

## Recommendations

[Provide any recommendations for improving the plugin based on the test results]

## Conclusion

[Provide an overall assessment of the plugin's quality and readiness]

---

### Guidelines for Interpreting Test Results

- **Status Values:**
  - **Pass**: The test case passed with the expected result
  - **Fail**: The test case failed to produce the expected result
  - **Partial**: The test case partially passed (some aspects worked, others didn't)
  - **Skipped**: The test case was not executed

- **Severity Levels for Issues:**
  - **Critical**: Prevents core functionality from working
  - **Major**: Significantly impacts usability but has workarounds
  - **Minor**: Causes inconvenience but doesn't impact core functionality
  - **Cosmetic**: Visual or aesthetic issues only

- **Testing Tips:**
  - Test each feature in isolation first, then test combinations
  - For each test case, try both valid and invalid inputs
  - Document any unexpected behavior, even if it's not clearly a bug
  - Include screenshots for visual issues
  - Test with different IDE themes to ensure proper highlighting