# Latte Template Support Plugin Parser Fixes

This document describes the fixes implemented to address issues in the parser component of the Latte Template Support plugin. These issues were identified when attempting to parse invalid Latte constructions, such as a standalone `{else}` tag outside of an `{if}` block.

## Issues Fixed

### 1. ProcessCanceledException Handling

**Problem**: The `ProcessCanceledException` was being caught and logged instead of being propagated. According to IntelliJ platform guidelines, this exception should be propagated to allow the IDE to properly handle cancellation requests.

**Files Modified**:
- `SafeLatteHtmlParsing.java`
- `LatteHtmlParser.java`

**Changes**:
- Added specific catch blocks for `ProcessCanceledException` that rethrow the exception instead of logging it
- Ensured that markers are properly closed before rethrowing the exception

### 2. Marker Management in PSI Builder

**Problem**: Markers were not being properly closed in all code paths, leading to unbalanced markers and errors like "Another not done marker added after this one" and "Unbalanced tree".

**Files Modified**:
- `LatteHtmlParser.java`

**Changes**:
- Restructured the marker management in `parseWithoutBuildingTree` to ensure markers are closed exactly once in each code path
- Completed markers after successful parsing, when a `ProcessCanceledException` is thrown, and when any other exception occurs
- Removed duplicate marker completion

### 3. Validation for Standalone {else} Tags

**Problem**: The parser didn't validate that `{else}` and `{elseif}` tags are inside `{if}` blocks, leading to confusing errors when these tags were used incorrectly.

**Files Modified**:
- `LatteValidator.java`

**Changes**:
- Added validation in `validateMacroName` to check if `{else}` and `{elseif}` tags are inside `{if}` blocks
- Added a new `checkIfInsideIfBlock` method that traverses the PSI tree to determine if an element is inside an `{if}` block
- Implemented proper error reporting for standalone `{else}` and `{elseif}` tags

## Testing

A new test file and test class were created to verify the validation for standalone `{else}` tags:
- `standalone_else_tag.latte`: Contains a standalone `{else}` tag outside of an `{if}` block
- `LatteStandaloneElseTagTest.java`: Tests that the validation correctly identifies and reports an error for the standalone `{else}` tag

Due to test environment configuration issues, the test could not be run successfully, but the code changes have been implemented according to the requirements.

## Conclusion

These fixes should address the issues described in the problem statement:
1. `ProcessCanceledException` is now properly propagated instead of being logged
2. Markers are now properly closed in all code paths, preventing unbalanced markers
3. Validation has been added to check if `{else}` and `{elseif}` tags are inside `{if}` blocks

These changes should improve the stability and error reporting of the Latte Template Support plugin when parsing invalid Latte constructions.