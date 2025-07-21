# Latte Template Plugin for JetBrains IDEs

A plugin for JetBrains IDEs (IntelliJ IDEA, PhpStorm, WebStorm, etc.) that provides support for the Latte template language.

## Features

- **Syntax Highlighting**: Highlights Latte macros, attributes, and filters
- **Code Completion**: Provides code completion for Latte macros, n:attributes, and filters
- **Documentation**: Shows documentation for Latte macros, attributes, and filters
- **HTML Integration**: Extends HTML editing capabilities with Latte-specific features
- **Error Detection**: Identifies and highlights incorrect Latte syntax
- **Performance Optimizations**: Includes template caching, incremental parsing, and memory optimization for large templates

## Version Support

The Latte Plugin supports Latte 2.x, 3.0+, and 4.0+ versions, providing appropriate features and documentation for each version.

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

The plugin provides version-specific features for Latte 2.x, 3.0+, and 4.0+:

#### Latte 2.x Specific Features
- **Macros**: `{syntax}`, `{use}`, `{l}`, `{r}`
- **Attributes**: `n:ifcontent`, `n:href`
- **Filters**: `bytes`, `dataStream`, `url`

#### Latte 3.0+ Specific Features
- **Macros**: `{varType}`, `{templateType}`, `{php}`, `{do}`, `{parameters}`
- **Attributes**: `n:name`, `n:nonce`, `n:snippet-*`
- **Filters**: `slice`, `batch`, `spaceless`, `clamp`
- **Type Declarations**: Support for PHP type declarations in templates

#### Latte 4.0+ Specific Features
- **Macros**: `{typeCheck}`, `{strictTypes}`, `{asyncInclude}`, `{await}`, `{inject}`
- **Attributes**: Enhanced n:attributes syntax
- **Filters**: `json`, `base64`, `format`
- **Type System**: Enhanced type declarations with union types, intersection types, and generics
- **Asynchronous Processing**: Support for asynchronous template inclusion and processing

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
- `n:syntax` - Change syntax mode for a specific element (supports "double" and "off" values)

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

For detailed instructions on installing and managing Java versions, see [JAVA_INSTALL.md](docs/setup/JAVA_INSTALL.md).

For detailed instructions on installing and using Gradle 7.6, see [GRADLE_7.6_INSTALL.md](docs/setup/GRADLE_7.6_INSTALL.md).

### Checking Version Requirements

You can verify that your environment meets the required Java and Gradle versions by running the provided checker script:

```bash
# Make the script executable (if not already)
chmod +x check_versions.sh

# Run the checker script
./check_versions.sh
```

The script will check if your installed Java and Gradle versions are compatible with the LattePlugin project and provide appropriate guidance if any requirements are not met.

### Running the Plugin

1. Open the project in IntelliJ IDEA
2. Go to the Gradle tool window (View > Tool Windows > Gradle)
3. Navigate to Tasks > intellij > runIde
4. Double-click on `runIde` to start a new instance of IntelliJ IDEA with the plugin installed

### Running Tests

```
gradle test
```

For detailed instructions on building, testing, and debugging the plugin, see [BUILD_AND_TEST.md](docs/setup/BUILD_AND_TEST.md).

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

## Project Structure

The Latte Plugin is organized into several key components:

```
LattePlugin/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── cz/
│   │   │       └── hqm/
│   │   │           └── latte/
│   │   │               └── plugin/
│   │   │                   ├── cache/                # Template caching
│   │   │                   ├── completion/           # Code completion
│   │   │                   ├── custom/               # Custom elements support
│   │   │                   ├── documentation/        # Documentation
│   │   │                   ├── file/                 # File type
│   │   │                   ├── filters/              # Filters support
│   │   │                   ├── highlighting/         # Syntax highlighting
│   │   │                   ├── intention/            # Intention actions
│   │   │                   ├── lang/                 # Language definition
│   │   │                   ├── lexer/                # Lexical analysis
│   │   │                   ├── macros/               # Macros support
│   │   │                   ├── memory/               # Memory optimization
│   │   │                   ├── parser/               # Parser
│   │   │                   ├── project/              # Project services
│   │   │                   ├── psi/                  # Program Structure Interface
│   │   │                   ├── settings/             # Settings
│   │   │                   └── version/              # Version support
│   │   └── resources/
│   │       └── META-INF/
│   │           └── plugin.xml                        # Plugin configuration
│   └── test/
│       ├── java/                                     # Test classes
│       └── resources/
│           └── testData/                             # Test data files
└── docs/                                             # Documentation
    ├── user/                                         # User documentation
    ├── setup/                                        # Setup documentation
    ├── development/                                  # Developer documentation
    └── testing/                                      # Testing documentation
```

### Implementation Details

The plugin extends the HTML plugin with Latte-specific features:

- **Language Definition**: Custom language (LatteLanguage) associated with .latte files
- **Lexer and Parser**: Extended HTML lexer and parser to recognize Latte syntax
- **Syntax Highlighting**: Custom highlighting for Latte elements
- **Code Completion**: Suggestions for Latte macros, attributes, and filters
- **Documentation**: Quick documentation for Latte language elements
- **Version Support**: Support for different Latte versions (2.x, 3.0+, 4.0+)
- **Performance Optimizations**: Template caching, incremental parsing, and memory optimization

## Future Enhancements

Potential future enhancements for the Latte Plugin include:

1. **Integration with PHP**:
   - Better support for PHP type hints in templates
   - Smarter detection of PHP variables available in templates
   - Better support for PHP functions used in templates

2. **Performance Optimizations**:
   - Further improvements to caching of parsed templates
   - Enhanced incremental parsing of templates
   - Additional memory usage optimizations for large templates

3. **Testing and Validation**:
   - Increased test coverage for all features
   - Better validation of templates against Latte syntax
   - Improved error reporting for template errors

For more detailed information about the implementation and future plans, see the documentation in the `docs` directory.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Recent Changes

### July 2025 Updates

- **n:syntax Attribute Support**: Added support for the `n:syntax` attribute, which allows changing the syntax mode for specific HTML elements. See [NSYNTAX_ATTRIBUTE_SUPPORT.md](docs/development/NSYNTAX_ATTRIBUTE_SUPPORT.md) for details.
- **Latte 4.0+ Support**: Added support for Latte 4.0+ features, including new macros, enhanced type system, and asynchronous processing. See [LATTE_4.0_SUPPORT.md](docs/development/LATTE_4.0_SUPPORT.md) for details.
- **Performance Optimizations**: Implemented template caching, incremental parsing, and memory optimization for large templates. See [PERFORMANCE_OPTIMIZATIONS.md](docs/development/PERFORMANCE_OPTIMIZATIONS.md) for details.
- **Test Framework Conversion**: Converted all tests from JUnit 5 to JUnit 4 for better compatibility with the IntelliJ Platform.
- **Namespace Change**: Moved the entire project to the `cz.hqm.latte.plugin` namespace for better organization and consistency.
- **Bug Fixes**: Fixed several issues in the memory optimizer and other components to improve stability and performance.

## Acknowledgments

- [Latte](https://latte.nette.org/) - The template engine this plugin supports
- [JetBrains](https://www.jetbrains.com/) - For their excellent IDE platform