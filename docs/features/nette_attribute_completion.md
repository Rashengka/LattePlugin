# Nette Attribute Completion

This document describes the implementation of context-aware autocomplete for Nette n: attributes in HTML/XML tags.

## Feature Description

The plugin now provides context-aware autocomplete suggestions for Nette n: attributes when typing inside HTML/XML tags. This means that when you type `<div n:` or similar, the plugin will suggest appropriate n: attributes and prefixes.

## Implementation Details

The feature is implemented in the `NetteAttributeCompletionContributor` class, which:

1. Detects if the cursor is inside an HTML/XML tag using a regex pattern
2. Checks if the user is typing an n: attribute or has just typed "n:"
3. Provides autocomplete suggestions for valid n: attributes and prefixes

The valid n: attributes and prefixes are defined in the `LatteAttributeLexer` class and include:

### Valid Attribute Names
- n:if
- n:ifset
- n:foreach
- n:inner-foreach
- n:class
- n:attr
- n:tag
- n:snippet
- n:block
- n:include
- n:inner-if
- n:inner-ifset
- n:ifcontent
- n:href
- n:name
- n:nonce
- n:syntax

### Valid Attribute Prefixes
- n:
- n:inner-
- n:tag-
- n:class-
- n:attr-
- n:class:
- n:attr:
- n:tag:
- n:data-

## Usage Examples

The autocomplete will be triggered in contexts like:

```html
<div n:
```

After typing the above, the autocomplete will suggest attributes like `n:if`, `n:foreach`, etc.

It will also work with partially typed attributes:

```html
<div n:i
```

This will suggest attributes starting with "i", such as `n:if`, `n:ifset`, `n:ifcontent`, etc.

## Technical Implementation

The implementation uses the IntelliJ Platform's completion API:

1. A new completion contributor class `NetteAttributeCompletionContributor` extends `CompletionContributor`
2. The contributor registers a completion provider for elements in the Latte language
3. The provider checks the context using regex patterns to determine if it should provide suggestions
4. If the context is appropriate, it adds suggestions for n: attributes and prefixes

The contributor is registered in the plugin.xml file:

```xml
<!-- Nette attribute completion for n: attributes in HTML/XML tags -->
<completion.contributor language="Latte"
                        implementationClass="cz.hqm.latte.plugin.completion.NetteAttributeCompletionContributor"/>
```

## Related Files

- `cz.hqm.latte.plugin.completion.NetteAttributeCompletionContributor`: The main implementation of the feature
- `cz.hqm.latte.plugin.lexer.LatteAttributeLexer`: Contains the definitions of valid n: attributes and prefixes
- `src/main/resources/META-INF/plugin.xml`: Registration of the completion contributor