# Version Support in Latte Plugin

This document describes the version support features in the Latte Plugin, including version detection, version-specific features, and migration tools.

## Supported Versions

The Latte Plugin supports the following Latte versions:

- **Latte 2.x** - Including specific support for versions 2.4 and 2.5
- **Latte 3.0+** - Including specific support for versions 3.0 and 3.1
- **Latte 4.0+** - Including specific support for version 4.0

## Version Detection

The plugin automatically detects the Latte version used in your project through several methods:

1. **Composer.json Analysis**: The plugin examines your project's composer.json file to determine the Latte version.
2. **Content Analysis**: The plugin can analyze template content to detect version-specific syntax.
3. **Manual Selection**: You can manually select the Latte version in the plugin settings.

### Content-Based Detection

The plugin uses the following heuristics to detect the Latte version from template content:

- **Version-Specific Comments**: Comments like `{* Latte 2.x *}`, `{* Latte 3.0+ *}`, or `{* Latte 4.0+ *}`
- **Version-Specific Syntax**:
  - Latte 3.0+ specific macros: `{varType}`, `{templateType}`, `{php}`, `{do}`, `{parameters}`
  - Latte 4.0+ specific macros: `{typeCheck}`, `{strictTypes}`, `{asyncInclude}`, `{await}`, `{inject}`

## Version-Specific Features

The plugin provides different features based on the detected or selected Latte version:

### Latte 2.x Features

- Support for `{syntax}` macro
- Support for `{l}` and `{r}` macros
- Support for older syntax patterns

### Latte 3.0+ Features

- Support for `{varType}` and `{templateType}` macros
- Support for `{php}` and `{do}` macros
- Support for `{parameters}` macro
- Support for `{left}` and `{right}` macros (replacements for `{l}` and `{r}`)

### Latte 4.0+ Features

- Support for `{typeCheck}` and `{strictTypes}` macros
- Support for `{asyncInclude}` and `{await}` macros
- Support for `{inject}` macro
- Support for `{if isLinkCurrent(...)}` (replacement for `{ifCurrent}`)
- Support for `{http}` macro (replacement for `{status}`)

## Deprecated Features Detection

The plugin can detect deprecated features in your templates based on the current version:

### Features Deprecated in Latte 3.0+

- `{syntax}` macro - Use `{templateType}` instead
- `{l}` macro - Use `{left}` instead
- `{r}` macro - Use `{right}` instead

### Features Deprecated in Latte 4.0+

- `{ifCurrent}` macro - Use `{if isLinkCurrent(...)}` instead
- `{status}` macro - Use `{http}` instead

When a deprecated feature is detected, the plugin will display a warning with a suggestion for how to update your code.

## Version Migration

The plugin provides tools to help migrate your templates between different Latte versions:

### Migration from Latte 2.x to Latte 3.0+

- Replace `{syntax ...}` with `{templateType ...}`
- Replace `{l}` with `{left}`
- Replace `{r}` with `{right}`

### Migration from Latte 3.x to Latte 4.0+

- Replace `{ifCurrent ...}` with `{if isLinkCurrent(...)}`
- Replace `{/ifCurrent}` with `{/if}`
- Replace `{status ...}` with `{http ...}`

### Migration from Latte 2.x to Latte 4.0+

- All migrations from 2.x to 3.0+
- All migrations from 3.x to 4.0+

## Version Manager API

The plugin provides a `LatteVersionManager` class with the following methods:

- `getCurrentVersion()` - Gets the current Latte version
- `setCurrentVersion(LatteVersion version)` - Sets the current Latte version
- `isVersion2x()` - Checks if the current version is Latte 2.x
- `isVersion3x()` - Checks if the current version is Latte 3.0+
- `isVersion4x()` - Checks if the current version is Latte 4.0+
- `isAtLeastVersion(LatteVersion version)` - Checks if the current version is at least the specified version

## Version-Specific Documentation

The plugin provides links to version-specific documentation:

- **Latte 2.x**: [https://latte.nette.org/en/syntax/2.x](https://latte.nette.org/en/syntax/2.x)
- **Latte 3.0+**: [https://latte.nette.org/en/syntax](https://latte.nette.org/en/syntax)
- **Latte 4.0+**: [https://latte.nette.org/en/syntax](https://latte.nette.org/en/syntax)

## Settings

You can configure the version support in the plugin settings:

1. Open the IDE settings (File > Settings or Ctrl+Alt+S)
2. Navigate to Languages & Frameworks > Latte
3. Configure the following settings:

- **Latte Version**: Select the Latte version (2.x, 3.0+, 4.0+)
- **Override detected version**: Enable to use the selected version instead of the detected version

## Conclusion

The Latte Plugin provides comprehensive support for different Latte versions, making it easier to work with templates regardless of the version used in your project. The plugin's version detection, version-specific features, and migration tools help ensure that your templates are always up-to-date and compatible with your project's Latte version.