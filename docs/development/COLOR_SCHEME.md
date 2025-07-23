# Latte Color Scheme

This document describes the color scheme implementation for Latte files in the IDE.

## Overview

The Latte plugin includes a custom color scheme for syntax highlighting of Latte files. The color scheme is designed to provide good contrast and readability, especially in dark themes, and follows common syntax highlighting patterns for template languages.

## HTML Integration

The Latte color scheme is an extension of the HTML color scheme. This means that HTML elements in Latte files are highlighted using the standard HTML colors, while Latte-specific elements are highlighted using custom colors. This approach provides a consistent and intuitive highlighting experience for users who are familiar with HTML.

The integration is implemented by:
1. Using the HTML token types from `XmlTokenType` to identify HTML elements
2. Using the default language highlighter colors from `DefaultLanguageHighlighterColors` for HTML elements
3. Using custom colors for Latte-specific elements

## Implementation

The color scheme is implemented in the `LatteSyntaxHighlighter` class, which defines custom `TextAttributes` objects with specific colors for different syntax elements:

- For Latte-specific elements, it uses custom colors defined in the class
- For HTML elements, it uses the default language highlighter colors from `DefaultLanguageHighlighterColors`

The `LatteColorSettingsPage` class provides a settings page that allows users to customize both HTML and Latte-specific colors.

## Color Definitions

### Latte-specific Elements

The following colors are used for Latte-specific syntax elements:

| Syntax Element | Color | RGB Value | Font Style |
|----------------|-------|-----------|------------|
| Macro delimiters | Bright Yellow | (255, 204, 0) | Bold |
| Macro names | Light Blue | (102, 204, 255) | Bold |
| Attributes | Orange | (255, 153, 0) | Plain |
| Filters | Light Green | (153, 204, 0) | Plain |
| Comments | Gray | (128, 128, 128) | Italic |
| Errors | Bright Red | (255, 0, 0) | Bold |

### HTML Elements

HTML elements use the default language highlighter colors from `DefaultLanguageHighlighterColors`:

| Syntax Element | Default Language Highlighter Color |
|----------------|-----------------------------------|
| Tags | `MARKUP_TAG` |
| Tag Names | `MARKUP_TAG` |
| Attribute Names | `MARKUP_ATTRIBUTE` |
| Attribute Values | `STRING` |
| Entities | `MARKUP_ENTITY` |
| Comments | `BLOCK_COMMENT` |

## Customization

Users can customize the colors through the IDE's color settings page. The Latte color settings page is accessible through:

Settings → Editor → Color Scheme → Latte

The settings page allows users to customize both HTML and Latte-specific colors.

## Future Enhancements

Potential future enhancements to the color scheme include:

1. Adding more syntax elements for highlighting (e.g., variables, strings)
2. Providing light and dark theme variants
3. Allowing users to import/export color schemes
4. Adding more HTML-specific syntax elements for highlighting

## References

- [IntelliJ Platform SDK Documentation: Syntax Highlighting and Error Highlighting](https://plugins.jetbrains.com/docs/intellij/syntax-highlighting-and-error-highlighting.html)
- [IntelliJ Platform SDK Documentation: Color Scheme Management](https://plugins.jetbrains.com/docs/intellij/color-scheme-management.html)