# n:syntax Attribute Support

This document describes the implementation of support for the `n:syntax` attribute in the Latte Plugin.

## Overview

The Latte template language supports changing the syntax mode using the `{syntax}` tag. This implementation adds support for the `n:syntax` attribute, which allows changing the syntax mode for a specific HTML element.

## Syntax Modes

The Latte Plugin supports three syntax modes:

1. **DEFAULT**: Macros are delimited by single braces `{macro}`
2. **DOUBLE**: Macros are delimited by double braces `{{macro}}`
3. **OFF**: Latte syntax processing is disabled (except for `{/syntax}`)

## Implementation Details

### 1. LatteSyntaxMode Enum

The `LatteSyntaxMode` enum defines the three syntax modes:

```java
public enum LatteSyntaxMode {
    DEFAULT,  // Single braces: {macro}
    DOUBLE,   // Double braces: {{macro}}
    OFF       // Syntax processing disabled
}
```

### 2. LatteLexer Class

The `LatteLexer` class was enhanced to support the `n:syntax` attribute:

- Added a field to track the current syntax mode: `private LatteSyntaxMode syntaxMode = LatteSyntaxMode.DEFAULT;`
- Added a field to track the previous syntax mode: `private LatteSyntaxMode previousSyntaxMode = LatteSyntaxMode.DEFAULT;`
- Added a pattern to match the `n:syntax` attribute: `private static final Pattern N_SYNTAX_PATTERN = Pattern.compile("n:syntax\\s*=\\s*[\"']?([a-zA-Z0-9_]+)[\"']?");`
- Enhanced the `processSyntaxTags` method to handle both `{syntax}` tags and `n:syntax` attributes
- Enhanced the `setSyntaxMode` method to store the previous mode and set the new mode

### 3. LatteMacroLexer Class

The `LatteMacroLexer` class was enhanced to respect the current syntax mode:

- Added a reference to the parent lexer: `private LatteLexer parentLexer;`
- Added methods to get and set the syntax mode
- Added methods to check if the current position is at the start or end of a macro based on the syntax mode
- Enhanced the `advance` method to handle different syntax modes

### 4. LatteAttributeLexer Class

The `LatteAttributeLexer` class was enhanced to support the `n:syntax` attribute:

- Added a reference to the parent lexer: `private LatteLexer parentLexer;`
- Added a field to track the current attribute name: `private String currentAttributeName;`
- Enhanced the `handleInitialState` method to store the current attribute name
- Enhanced the `handleInValueState` method to update the syntax mode when processing the `n:syntax` attribute

### 5. LatteIncrementalParser Class

The `LatteIncrementalParser` class was enhanced to support different syntax modes:

- Enhanced the `findMacroTagEnd` method to handle both single and double braces

## Tests

The following tests were added to verify the implementation:

1. `NAttributeSyntaxTest`: Tests for the `n:syntax` attribute functionality
   - `testNSyntaxDoubleAttribute`: Tests that the `n:syntax="double"` attribute sets the syntax mode to DOUBLE
   - `testNSyntaxOffAttribute`: Tests that the `n:syntax="off"` attribute sets the syntax mode to OFF
   - `testNSyntaxInteractionWithSyntaxTags`: Tests the interaction between `n:syntax` attribute and `{syntax}` tags
   - `testNSyntaxUnquotedValue`: Tests that the `n:syntax` attribute with unquoted value works correctly

2. `LatteSyntaxModeTest`: Tests for the Latte syntax mode functionality
   - `testSyntaxDoubleMode`: Tests that the syntax mode can be set to DOUBLE
   - `testSyntaxOffMode`: Tests that the `{syntax off}` tag changes the syntax mode to OFF
   - `testNestedSyntaxTags`: Tests that nested syntax tags work correctly

## Usage Examples

### Using n:syntax Attribute

```html
<!-- Set syntax mode to DOUBLE for this div -->
<div n:syntax="double">
    <!-- Use double braces for macros -->
    <p>Inside double syntax mode: {{$variable}}</p>
    <p>Nested tags: {{if $condition}}Condition is true{{/if}}</p>
</div>

<!-- Set syntax mode to OFF for this div -->
<div n:syntax="off">
    <!-- Latte macros are treated as plain text -->
    <p>Inside off syntax mode: {$variable} is treated as plain text</p>
    <p>Latte macros like {if $condition}this{/if} are ignored</p>
</div>

<!-- Unquoted attribute value is also supported -->
<div n:syntax=double>
    <p>Unquoted double syntax: {{$variable}}</p>
</div>
```

### Interaction with {syntax} Tags

```html
<!-- Set syntax mode to DOUBLE for this div -->
<div n:syntax="double">
    <!-- Use double braces for macros -->
    <p>Starting with double syntax: {{$variable1}}</p>
    
    <!-- Switch to OFF syntax mode -->
    {syntax off}
    <p>Switched to off syntax: {$variable2} is plain text</p>
    {/syntax}
    
    <!-- Back to DOUBLE syntax mode -->
    <p>Back to double syntax: {{$variable3}}</p>
</div>
```

## Conclusion

The implementation of the `n:syntax` attribute enhances the Latte Plugin by allowing developers to change the syntax mode for specific HTML elements. This provides more flexibility and control over how Latte macros are processed in different parts of a template.