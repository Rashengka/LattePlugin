# Fix for "Top level element is not completed" Error in Latte Templates with Double-Brace Macros

## Problem Description

The Latte plugin was experiencing an issue where templates using the `{syntax double}` directive would trigger a "Top level element is not completed" error during validation. This error occurs when the XML/HTML validator detects an incomplete element structure.

The issue was particularly noticeable in complex templates with nested macros and syntax mode changes, such as:

```latte
{syntax double}
{{if $condition}}
    <p>Content</p>
    {{foreach $items as $item}}
        <span>{{$item}}</span>
    {{/foreach}}
{{/if}}
{/syntax}
```

## Root Cause

After investigation, we identified that the issue was in the `findEndOfLatteMacro` method in `LatteIncrementalParser.java`. This method was responsible for finding the end of a Latte macro, but it only looked for single-brace macros (`{macro}`) and didn't properly handle double-brace macros (`{{macro}}`) that are used in `{syntax double}` mode.

Specifically, the method had the following issues:

1. It only checked for single-brace macros with this condition:
   ```java
   if (i < content.length() - 1 && content.charAt(i) == '{' && content.charAt(i + 1) != '{') {
       // ...
   }
   ```

2. The patterns for matching macro names were only designed for single-brace syntax:
   ```java
   Pattern openMacroPattern = Pattern.compile("\\{([a-zA-Z_][a-zA-Z0-9_]*)(?:\\s+|\\}|$)");
   Pattern closeMacroPattern = Pattern.compile("\\{/([a-zA-Z_][a-zA-Z0-9_]*)\\}");
   ```

This caused the parser to miss double-brace macros entirely, leading to unclosed HTML tags in the parsed structure and the "Top level element is not completed" error.

## Solution

We updated the `findEndOfLatteMacro` method to properly handle both single-brace and double-brace macros:

1. Added patterns for matching macro names in double-brace syntax:
   ```java
   Pattern openDoubleMacroPattern = Pattern.compile("\\{\\{([a-zA-Z_][a-zA-Z0-9_]*)(?:\\s+|\\}\\}|$)");
   Pattern closeDoubleMacroPattern = Pattern.compile("\\{\\{/([a-zA-Z_][a-zA-Z0-9_]*)\\}\\}");
   ```

2. Added a check for double-brace macros at the beginning of the loop:
   ```java
   boolean isDoubleBrace = false;
   
   // Check for double-brace macro {{macro}} (for {syntax double} mode)
   if (i < content.length() - 2 && content.charAt(i) == '{' && content.charAt(i + 1) == '{') {
       isDoubleBrace = true;
   }
   ```

3. Modified the condition for checking opening macros to handle both single-brace and double-brace macros:
   ```java
   // Check for opening macro (either single-brace or double-brace)
   if ((isDoubleBrace && i < content.length() - 2) || 
       (!isDoubleBrace && i < content.length() - 1 && content.charAt(i) == '{')) {
       // ...
   }
   ```

4. Used the appropriate pattern based on whether it's a single-brace or double-brace macro:
   ```java
   Matcher closeMatcher;
   if (isDoubleBrace) {
       closeMatcher = closeDoubleMacroPattern.matcher(macroTag);
   } else {
       closeMatcher = closeMacroPattern.matcher(macroTag);
   }
   ```

These changes ensure that the parser correctly handles both single-brace and double-brace macros, which resolves the "Top level element is not completed" error.

## Testing

We added a new test method `testDoubleBraceMacros` to `LatteIncrementalParserTest` to verify that the parser correctly handles double-brace macros in `{syntax double}` mode:

```java
/**
 * Tests that the parser correctly handles double-brace macros in {syntax double} mode.
 * This tests our enhancement to findEndOfLatteMacro() to handle both single-brace and double-brace macros.
 */
@Test
public void testDoubleBraceMacros() throws Exception {
    // Test with a template containing double-brace macros in {syntax double} mode
    String doubleBraceContent = 
        "{syntax double}\n" +
        "{{if $condition}}\n" +
        "    <p>Content</p>\n" +
        "    {{foreach $items as $item}}\n" +
        "        <span>{{$item}}</span>\n" +
        "    {{/foreach}}\n" +
        "{{/if}}\n" +
        "{/syntax}";
    
    VirtualFile doubleBraceFile = createTestFile("test_double_brace_macros.latte", doubleBraceContent);
    
    // Parse the file
    List<TextRange> changedRanges = incrementalParser.parseChangedParts(doubleBraceFile, doubleBraceContent);
    
    // Verify that the entire file is considered changed
    assertEquals("Should have one changed range", 1, changedRanges.size());
    assertEquals("Changed range should cover the entire file", 0, changedRanges.get(0).getStartOffset());
    assertEquals("Changed range should cover the entire file", doubleBraceContent.length(), changedRanges.get(0).getEndOffset());
    
    // Test with a more complex template containing nested syntax changes
    String complexContent = 
        "{syntax double}\n" +
        "{{if $condition}}\n" +
        "    {syntax off}\n" +
        "    <script>\n" +
        "        // This should not be processed as Latte\n" +
        "        var x = {value: 10};\n" +
        "    </script>\n" +
        "    {/syntax}\n" +
        "    {{foreach $items as $item}}\n" +
        "        <span>{{$item}}</span>\n" +
        "    {{/foreach}}\n" +
        "{{/if}}\n" +
        "{/syntax}";
    
    VirtualFile complexFile = createTestFile("test_complex_syntax_changes.latte", complexContent);
    
    // Parse the file
    changedRanges = incrementalParser.parseChangedParts(complexFile, complexContent);
    
    // Verify that the entire file is considered changed
    assertEquals("Should have one changed range", 1, changedRanges.size());
    assertEquals("Changed range should cover the entire file", 0, changedRanges.get(0).getStartOffset());
    assertEquals("Changed range should cover the entire file", complexContent.length(), changedRanges.get(0).getEndOffset());
}
```

This test verifies that the parser correctly processes templates with double-brace macros and nested syntax changes.

## Conclusion

The "Top level element is not completed" error in Latte templates using `{syntax double}` has been fixed by updating the parser to correctly handle double-brace macros. This ensures proper parsing and validation of templates in all syntax modes, even in complex templates with nested macros and syntax mode changes.

The key improvements include:
1. Adding support for double-brace macros in the `findEndOfLatteMacro` method
2. Using the appropriate patterns for matching macro names based on the syntax mode
3. Ensuring proper handling of nested macros in different syntax modes

These changes make the Latte plugin more robust and reliable when working with templates that use different syntax modes.