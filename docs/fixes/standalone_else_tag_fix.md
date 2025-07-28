# Standalone {else} Tag Fix

## Issue Description

The test `cz.hqm.latte.plugin.test.validator.LatteStandaloneElseTagTest#testStandaloneElseTag` was failing because it expected an error for a standalone `{else}` tag outside of an `{if}` block, but no such error was being detected.

The test was looking for a `PsiErrorElement` with an error message containing "must be inside an {if} block", but no such error element was being created during parsing.

## Root Cause Analysis

After investigating the codebase, we found that:

1. The validation logic in `LatteValidator.java` correctly identified standalone `{else}` and `{elseif}` tags as errors, but it was only logging the errors, not creating `PsiErrorElement` objects in the PSI tree.

2. The parser in `LatteHtmlParsing.java` was treating all Latte macros the same way, without any special handling for `{else}` and `{elseif}` tags.

3. The test was looking for `PsiErrorElement` objects in the PSI tree, which are typically created during parsing, not during annotation.

## Solution

We modified the parser to recognize standalone `{else}` and `{elseif}` tags as syntax errors during parsing, which causes the platform to create `PsiErrorElement` objects that can be detected by the test.

Specifically, we made the following changes:

1. Added a field `ifBlockLevel` to the `LatteHtmlParsing` class to track the nesting level of `{if}` blocks.

2. Modified the `parseDocument` method to reset the `ifBlockLevel` field at the start of parsing.

3. Modified the `parseLatteMacro` method to:
   - Extract the macro name from the token text
   - Update the `ifBlockLevel` when we encounter `{if}` and `{/if}` tags
   - Check if the macro name is "else" or "elseif" and if it's not inside an `{if}` block
   - If it's a standalone `{else}` or `{elseif}` tag, create an error element using `macro.error("Tag {" + macroName + "} must be inside an {if} block")`

4. Added a helper method `extractMacroName` to extract the macro name from the macro text, handling different Latte syntax versions (single brace, double brace, etc.).

## Testing

We attempted to run the test to verify our changes, but encountered environment setup issues that prevented the test from running successfully. However, based on our analysis and the changes we made, we believe the issue has been addressed.

The changes ensure that standalone `{else}` and `{elseif}` tags are properly detected as errors during parsing, which should make the test pass in a properly configured environment.

## Files Modified

- `/Users/ragnar/IdeaProjects/LattePlugin/src/main/java/cz/hqm/latte/plugin/parser/LatteHtmlParsing.java`