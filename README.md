# Latte Template Plugin for JetBrains IDEs

A plugin for JetBrains IDEs (IntelliJ IDEA, PhpStorm, WebStorm, etc.) that provides support for the Latte template language.

## Features

- **Syntax Highlighting**: Highlights Latte macros, attributes, and filters
- **Code Completion**: Provides code completion for Latte macros, n:attributes, and filters
- **Documentation**: Shows documentation for Latte macros, attributes, and filters
- **HTML Integration**: Extends HTML editing capabilities with Latte-specific features
- **Error Detection**: Identifies and highlights incorrect Latte syntax

## Version Support

The Latte Plugin supports both Latte 2.x and 3.0+ versions, providing appropriate features and documentation for each version.

### Version Detection

The plugin automatically detects the Latte version from:
1. **Composer Dependencies**: If your project has a composer.json file with latte/latte or nette/latte dependencies, the plugin will detect the version from there.
2. **Version Comments**: You can specify the version with a comment at the top of your file:
   - `{* Latte 2.x *}` - For Latte 2.x
   - `{* Latte 3.0+ *}` - For Latte 3.0+
3. **Version-Specific Syntax**: The plugin can detect the version based on version-specific syntax in your file (e.g., `{varType}` indicates Latte 3.0+).

### Manual Version Switching

You can manually switch between Latte versions using the "Toggle Latte Version" action in the Tools menu.

### Version-Specific Features

The plugin provides version-specific features for both Latte 2.x and 3.0+:

#### Latte 2.x Specific Features
- **Macros**: `{syntax}`, `{use}`, `{l}`, `{r}`
- **Attributes**: `n:ifcontent`, `n:href`
- **Filters**: `bytes`, `dataStream`, `url`

#### Latte 3.0+ Specific Features
- **Macros**: `{varType}`, `{templateType}`, `{php}`, `{do}`, `{parameters}`
- **Attributes**: `n:name`, `n:nonce`, `n:snippet-*`
- **Filters**: `slice`, `batch`, `spaceless`, `clamp`
- **Type Declarations**: Support for PHP type declarations in templates

## Supported Latte Features

### Macros

The plugin supports all standard Latte macros, including:

- `{if}`, `{else}`, `{elseif}` - Conditional statements
- `{foreach}` - Loop over arrays or iterable objects
- `{include}` - Include other templates
- `{block}`, `{define}` - Template blocks
- `{var}`, `{capture}` - Variable manipulation
- `{_}` - Translation
- `{=}` - Variable printing

### n:attributes

The plugin supports Latte n:attributes, including:

- `n:if` - Conditional rendering of elements
- `n:foreach` - Loop over arrays or iterable objects
- `n:inner-foreach` - Loop over arrays for inner content only
- `n:class` - Conditional class addition
- `n:attr` - Conditional attribute addition
- `n:tag` - Conditional tag name change

### Filters

The plugin supports Latte filters, including:

- `capitalize`, `upper`, `lower`, `firstUpper` - String case manipulation
- `escape`, `escapeUrl`, `noescape` - String escaping
- `date`, `number` - Formatting
- And many more...

### Error Detection

The plugin detects and highlights various types of incorrect Latte syntax:

#### Macro Errors
- **Invalid Macro Names**: Highlights macros that don't exist in Latte (e.g., `{invalidMacro}`)
- **Unclosed Macros**: Detects macros without closing braces (e.g., `{if $condition`)
- **Mismatched Closing Tags**: Identifies when opening and closing tags don't match (e.g., `{if}...{/foreach}`)
- **Unexpected Closing Tags**: Highlights closing tags without matching opening tags (e.g., `{/if}` without `{if}`)

#### Attribute Errors
- **Invalid Attribute Syntax**: Detects invalid n:attributes (e.g., `n:invalid`, `n:if=` without a value)
- **Unclosed Quotes**: Identifies attributes with unclosed quotes (e.g., `n:if="$condition`)

#### Filter Errors
- **Invalid Filter Syntax**: Highlights invalid filter usage (e.g., `{$var||}`, `{$var|invalidFilter}`)

This error detection helps you identify and fix issues in your Latte templates more quickly, improving code quality and reducing debugging time.

## Installation

### From JetBrains Marketplace

1. Open your JetBrains IDE
2. Go to Settings/Preferences > Plugins
3. Click "Browse repositories..."
4. Search for "Latte Template"
5. Click "Install"

### Manual Installation

1. Download the latest release from the [Releases](https://github.com/Rashengka/LattePlugin/releases) page
2. Open your JetBrains IDE
3. Go to Settings/Preferences > Plugins
4. Click "Install plugin from disk..."
5. Select the downloaded .jar file

## Building and Testing

### Quick Start

1. Clone the repository:
   ```
   git clone https://github.com/Rashengka/LattePlugin.git
   ```

2. Build the plugin:
   ```
   cd LattePlugin
   gradle buildPlugin
   ```

3. The plugin will be built to `build/distributions/LattePlugin-1.0-SNAPSHOT.zip`

### Prerequisites

- JDK 17 or later (JDK 8-19 are compatible, JDK 20+ are not compatible)
- Gradle 7.6 (specifically version 7.6, as the JetBrains Intellij plugin is not compatible with Gradle 8.x)
- IntelliJ IDEA (Community or Ultimate edition, version 2023.1.5 or compatible)

For detailed instructions on installing and managing Java versions, see [JAVA_INSTALL.md](JAVA_INSTALL.md).

For detailed instructions on installing and using Gradle 7.6, see [GRADLE_7.6_INSTALL.md](GRADLE_7.6_INSTALL.md).

### Running the Plugin

1. Open the project in IntelliJ IDEA
2. Go to the Gradle tool window (View > Tool Windows > Gradle)
3. Navigate to Tasks > intellij > runIde
4. Double-click on `runIde` to start a new instance of IntelliJ IDEA with the plugin installed

### Running Tests

```
gradle test
```

For detailed instructions on building, testing, and debugging the plugin, see [BUILD_AND_TEST.md](BUILD_AND_TEST.md).

## Usage

After installing the plugin, files with the `.latte` extension will automatically be recognized as Latte template files. You can also associate other file extensions with the Latte file type in the IDE settings.

### Creating a New Latte File

1. Right-click on a directory in the Project view
2. Select "New" > "File"
3. Enter a name with the `.latte` extension (e.g., `template.latte`)

### Example Latte Template

```latte
<!DOCTYPE html>
<html>
<head>
    <title>{$title}</title>
</head>
<body>
    {* This is a Latte comment *}
    
    {if $user->isLoggedIn()}
        <h1>Welcome, {$user->name|capitalize}</h1>
        <p n:if="$user->isAdmin">You are an administrator.</p>
    {else}
        <h1>Please log in</h1>
    {/if}
    
    <ul n:if="$items">
        {foreach $items as $item}
            <li>{$item}</li>
        {/foreach}
    </ul>
    
    {include 'footer.latte'}
</body>
</html>
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Latte](https://latte.nette.org/) - The template engine this plugin supports
- [JetBrains](https://www.jetbrains.com/) - For their excellent IDE platform