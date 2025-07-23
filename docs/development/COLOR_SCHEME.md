# Latte Color Scheme

This document describes the color scheme implementation for Latte files in the IDE.

## Overview

The Latte plugin includes a custom color scheme for syntax highlighting of Latte files. The color scheme is designed to provide good contrast and readability, especially in dark themes, and follows common syntax highlighting patterns for template languages.

## Implementation

The color scheme is implemented in the `LatteSyntaxHighlighter` class, which defines custom `TextAttributes` objects with specific colors for different Latte syntax elements.

## Color Definitions

The following colors are used for different Latte syntax elements:

| Syntax Element | Color | RGB Value | Font Style |
|----------------|-------|-----------|------------|
| Macro delimiters | Bright Yellow | (255, 204, 0) | Bold |
| Macro names | Light Blue | (102, 204, 255) | Bold |
| Attributes | Orange | (255, 153, 0) | Plain |
| Filters | Light Green | (153, 204, 0) | Plain |
| Comments | Gray | (128, 128, 128) | Italic |
| Errors | Bright Red | (255, 0, 0) | Bold |

## Customization

Users can customize the colors through the IDE's color settings page. The Latte color settings page is accessible through:

Settings → Editor → Color Scheme → Latte

## Future Enhancements

Potential future enhancements to the color scheme include:

1. Adding more syntax elements for highlighting (e.g., variables, strings)
2. Providing light and dark theme variants
3. Allowing users to import/export color schemes

## References

- [IntelliJ Platform SDK Documentation: Syntax Highlighting and Error Highlighting](https://plugins.jetbrains.com/docs/intellij/syntax-highlighting-and-error-highlighting.html)
- [IntelliJ Platform SDK Documentation: Color Scheme Management](https://plugins.jetbrains.com/docs/intellij/color-scheme-management.html)