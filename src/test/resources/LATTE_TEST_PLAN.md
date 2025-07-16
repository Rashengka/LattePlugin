# Latte Template Language Test Plan

This document outlines a comprehensive test plan for the Latte template language features implemented in the Latte Plugin for JetBrains IDEs. It provides guidelines for testing all Latte features according to the official Latte documentation.

## Test Files

The following test files have been created to demonstrate and test Latte features:

1. **Core Syntax**
   - `testData/core/variables.latte` - Tests variable output and basic expressions

2. **Macros**
   - `testData/macros/conditionals.latte` - Tests conditional macros (if, else, elseif)
   - `testData/macros/loops.latte` - Tests loop macros (foreach, for, while)
   - `testData/macros/blocks.latte` - Tests block and include macros

3. **Attributes**
   - `testData/attributes/nattributes.latte` - Tests n:attributes

4. **Filters**
   - `testData/filters/filters.latte` - Tests filters for variable modification

## Test Cases

### 1. Core Syntax Tests

#### 1.1 Variable Output

| Test Case | Description | Expected Result |
|-----------|-------------|-----------------|
| Simple variable | `{$name}` | Variable value is output |
| Object property | `{$user->name}` | Object property value is output |
| Array access | `{$users[0]}` | Array element value is output |
| Method call | `{$user->getName()}` | Method return value is output |
| Complex expression | `{$user->isAdmin() ? 'Admin' : 'User'}` | Conditional expression result is output |
| Direct output | `{='Hello ' . $name}` | Concatenated string is output |
| HTML escaping | `{$html}` | HTML is properly escaped |

#### 1.2 Comments

| Test Case | Description | Expected Result |
|-----------|-------------|-----------------|
| Single-line comment | `{* This is a comment *}` | Comment is not rendered in output |
| Multi-line comment | `{* Multi-line\ncomment *}` | Comment is not rendered in output |

### 2. Macro Tests

#### 2.1 Conditional Macros

| Test Case | Description | Expected Result |
|-----------|-------------|-----------------|
| Simple if | `{if $condition}...{/if}` | Content is conditionally rendered |
| If-else | `{if $condition}...{else}...{/if}` | Appropriate branch is rendered |
| If-elseif-else | `{if $c1}...{elseif $c2}...{else}...{/if}` | Appropriate branch is rendered |
| Nested if | `{if $c1}{if $c2}...{/if}{/if}` | Nested conditions work correctly |
| Complex condition | `{if $c1 && ($c2 || $c3)}...{/if}` | Complex boolean logic works |

#### 2.2 Loop Macros

| Test Case | Description | Expected Result |
|-----------|-------------|-----------------|
| Simple foreach | `{foreach $items as $item}...{/foreach}` | Loop iterates over all items |
| Foreach with key | `{foreach $items as $key => $value}...{/foreach}` | Keys and values are accessible |
| Nested foreach | `{foreach $outer as $inner}{foreach $inner as $item}...{/foreach}{/foreach}` | Nested loops work correctly |
| Foreach with iterator | `{foreach $items as $item}{$iterator->counter}...{/foreach}` | Iterator variables work |
| For loop | `{for $i = 1; $i <= 5; $i++}...{/for}` | For loop works correctly |
| While loop | `{while $condition}...{/while}` | While loop works correctly |
| Break and continue | `{foreach $items as $item}{if $condition}{continue}{/if}...{/foreach}` | Break and continue work |

#### 2.3 Block and Include Macros

| Test Case | Description | Expected Result |
|-----------|-------------|-----------------|
| Define block | `{block content}...{/block}` | Block is defined and rendered |
| Include template | `{include 'file.latte'}` | Template is included |
| Include with params | `{include 'file.latte', param => value}` | Template is included with parameters |
| Define without printing | `{define sidebar}...{/define}` | Block is defined but not rendered |
| Include defined block | `{include sidebar}` | Defined block is rendered |
| Extend parent template | `{layout 'layout.latte'}` | Template inheritance works |
| Override parent block | `{block title}...{/block}` | Parent block is overridden |
| Capture output | `{capture $var}...{/capture}` | Output is captured to variable |

### 3. Attribute Tests

#### 3.1 Conditional Attributes

| Test Case | Description | Expected Result |
|-----------|-------------|-----------------|
| n:if | `<div n:if="$condition">...</div>` | Element is conditionally rendered |
| n:ifset | `<div n:ifset="$var">...</div>` | Element is rendered if variable is set |
| n:inner-if | `<div n:inner-if="$condition">...</div>` | Inner content is conditionally rendered |
| n:class | `<div n:class="$condition ? class">...</div>` | Class is conditionally added |
| n:attr | `<div n:attr="attr => $condition ? value">...</div>` | Attribute is conditionally added |
| n:tag | `<n:tag n:tag="$condition ? div : span">...</n:tag>` | Tag name is conditionally changed |

#### 3.2 Loop Attributes

| Test Case | Description | Expected Result |
|-----------|-------------|-----------------|
| n:foreach | `<ul n:foreach="$items as $item"><li>{$item}</li></ul>` | Element is repeated for each item |
| n:inner-foreach | `<ul n:inner-foreach="$items as $item"><li>{$item}</li></ul>` | Inner content is repeated |
| Combining attributes | `<div n:if="$c" n:foreach="$items as $item">...</div>` | Multiple attributes work together |

### 4. Filter Tests

#### 4.1 String Manipulation Filters

| Test Case | Description | Expected Result |
|-----------|-------------|-----------------|
| upper | `{$text|upper}` | Text is converted to uppercase |
| lower | `{$text|lower}` | Text is converted to lowercase |
| firstUpper | `{$text|firstUpper}` | First letter is capitalized |
| capitalize | `{$text|capitalize}` | First letter of each word is capitalized |
| truncate | `{$text|truncate:30}` | Text is truncated to specified length |
| substring | `{$text|substring:0:5}` | Substring is extracted |
| trim | `{$text|trim}` | Whitespace is trimmed |
| padLeft/padRight | `{$text|padLeft:10:'0'}` | Text is padded to specified length |
| replace | `{$text|replace:'old':'new'}` | Substring is replaced |
| stripHtml | `{$html|stripHtml}` | HTML tags are removed |

#### 4.2 Formatting Filters

| Test Case | Description | Expected Result |
|-----------|-------------|-----------------|
| date | `{$date|date:'j.n.Y'}` | Date is formatted according to format string |
| number | `{$number|number:2}` | Number is formatted with specified decimals |
| bytes | `{$bytes|bytes}` | Bytes are formatted as human-readable size |
| percent | `{$ratio|percent}` | Number is formatted as percentage |

#### 4.3 Escaping Filters

| Test Case | Description | Expected Result |
|-----------|-------------|-----------------|
| escape | `{$html|escape}` | HTML is escaped |
| escapeUrl | `{$url|escapeUrl}` | URL is escaped |
| noescape | `{$html|noescape}` | HTML is not escaped |

#### 4.4 Multiple Filters

| Test Case | Description | Expected Result |
|-----------|-------------|-----------------|
| Chained filters | `{$text|upper|truncate:20}` | Filters are applied in sequence |
| Filters with params | `{$text|replace:'old':'new'|truncate:30}` | Filters with parameters work in sequence |

## Test Execution

### Manual Testing

1. Open each test file in a JetBrains IDE with the Latte Plugin installed
2. Verify that syntax highlighting works correctly for all Latte elements
3. Verify that code completion works for macros, attributes, and filters
4. Verify that documentation is available for Latte elements
5. Check for any parsing errors or warnings

### Automated Testing

For automated testing, implement test classes that:

1. Verify file type recognition for .latte files
2. Test lexer and parser for correct token recognition
3. Test syntax highlighting for all Latte elements
4. Test code completion for macros, attributes, and filters
5. Test documentation provider for Latte elements

## Test Reporting

For each test case, record:

1. Test case ID and description
2. Expected result
3. Actual result
4. Pass/Fail status
5. Any issues or observations

## Conclusion

This test plan provides a comprehensive approach to testing all Latte template language features implemented in the Latte Plugin for JetBrains IDEs. By following this plan, you can ensure that the plugin correctly supports all Latte features according to the official documentation.