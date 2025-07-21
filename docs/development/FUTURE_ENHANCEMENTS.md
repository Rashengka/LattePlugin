# Future Enhancements for Latte Plugin

This document outlines potential future enhancements for the Latte Plugin to ensure comprehensive support for all Latte and Nette features across all supported versions.

## Overview

The Latte Plugin currently supports many features from Latte and Nette packages, including:
- Core Latte macros, filters, and n:attributes
- Nette/application macros, filters, and n:attributes
- Nette/forms macros, filters, and n:attributes
- Nette/assets macros and filters
- Nette/database macros, filters, and variables
- Nette/security macros, filters, and variables
- Nette/mail macros, filters, and variables
- Nette/http variables and functions
- Version-specific features for Latte 2.x, 3.0+, and 4.0+

However, there are opportunities for enhancement to ensure complete coverage of all features and versions.

## Potential Enhancements

### 1. Latte 4.0+ Support (Implemented)

The plugin now includes support for Latte 4.0+ features:

- **Completion Contributor**: LatteCompletionContributor has been updated to handle Latte 4.0+ specific macros and syntax, including typeCheck, strictTypes, asyncInclude, await, inject, and more.
- **Version Detection**: LatteVersionManager has been enhanced with isVersion4x() method and LatteVersion.detectVersionFromContent() now detects Latte 4.0+ specific syntax.
- **Documentation**: Documentation has been updated with Latte 4.0+ specific features in LATTE_4.0_SUPPORT.md.

See LATTE_4.0_SUPPORT.md for detailed information about the implementation.

### 2. Additional Nette Packages (Implemented)

The plugin now includes support for additional Nette packages:

- **nette/database**: Added support for database-related variables (`$database`, `$db`, `$row`), macros (`query`, `foreach`, `ifRow`), n:attributes (`n:query`, `n:ifRow`), and filters (`table`, `column`, `value`, `like`)
- **nette/security**: Added support for security-related variables (`$user`, `$identity`, `$roles`), macros (`ifLoggedIn`, `ifRole`, `ifAllowed`), n:attributes (`n:ifLoggedIn`, `n:ifRole`, `n:ifAllowed`), and filters (`isLoggedIn`, `isAllowed`, `hasRole`, `getRoles`)
- **nette/mail**: Added support for email-related variables (`$mail`, `$message`, `$attachment`, `$sender`), macros (`mail`, `subject`, `from`, `to`, `cc`, `bcc`, `attach`), n:attributes (`n:mail`, `n:subject`, `n:from`, `n:to`), and filters (`encodeEmail`, `formatEmail`, `attachFile`, `embedFile`)
- **nette/http**: Added support for HTTP-related variables (`$httpRequest`, `$httpResponse`, `$session`, `$url`, `$cookies`, `$headers`) and version-specific features

See NETTE_PACKAGE_SUPPORT.md for detailed information about the implementation.

### 3. Enhanced n:attributes Support (Implemented)

Improved support for n:attributes:

- **Dynamic n:attributes**: Added support for dynamically generated n:attributes by extending the ATTRIBUTE_NAME_PATTERN regex
- **Prefixed n:attributes**: Added support for prefixed n:attributes (e.g., n:class:hover) by adding more prefixes to VALID_ATTRIBUTE_PREFIXES
- **Custom n:attributes**: Added support for custom n:attributes by creating a CustomAttributesProvider

### 4. Advanced Filters (Implemented)

Added support for more advanced filters:

- **Custom Filter Chaining**: Added support for chaining custom filters by modifying the lexer and parser
- **Filter Parameters**: Added support for filter parameters by extending the NetteFilter class
- **Filter Auto-completion**: Enhanced filter auto-completion to be context-aware by modifying the LatteCompletionContributor

### 5. Version-Specific Features (Implemented)

Implemented more granular version-specific features:

- **Minor Version Differences**: Added support for differences between minor versions (e.g., 3.1 vs 3.2) by extending the LatteVersion enum and updating LatteVersionManager
- **Deprecated Features**: Added warnings for deprecated features in newer versions by creating a DeprecatedFeatureDetector
- **Version Migration**: Added tools to help migrate between versions by developing a VersionMigrationHelper

### 6. Performance Optimizations (Implemented)

Optimized performance for large templates:

- **Caching**: Implemented better caching of parsed templates in the LatteCacheManager class
- **Incremental Parsing**: Added support for incremental parsing of templates in the LatteIncrementalParser class
- **Memory Usage**: Reduced memory usage for large templates through the LatteMemoryOptimizer class

See PERFORMANCE_OPTIMIZATIONS.md for detailed information about the implementation.

### 7. Integration with PHP (Implemented)

Improved integration with PHP code:

- **Navigation to PHP Methods**: Added support for navigation from Latte templates to PHP methods in presenters and controls
- **Component Autocomplete**: Added support for autocomplete of component names in {control} macros
- **Component Navigation**: Added support for navigation from {control} macros to component factory methods
- **Template Inclusion and Inheritance**: Added support for template inclusion and inheritance with navigation between templates and blocks
- **Type Macros and Type Checking**: Added support for {varType}, {templateType}, and other type-related macros with navigation to PHP classes
- **PHP Type Hints**: Better support for PHP type hints in templates
- **PHP Variables**: Smarter detection of PHP variables available in templates
- **PHP Functions**: Better support for PHP functions used in templates

### 8. NEON File Support
``
Add support for NEON (Nette Object Notation) files:

- **Standalone NEON Support**: 
  - **Syntax Highlighting**: Add syntax highlighting for NEON files
  - **Code Completion**: Provide code completion for NEON syntax elements
  - **Error Detection**: Detect and highlight syntax errors in NEON files
  - **Formatting**: Add code formatting for NEON files
  - **Structure View**: Provide a structure view for NEON files

- **Nette Integration**:
  - **Configuration Navigation**: Add navigation from Latte templates to NEON configuration files
  - **Service Autocompletion**: Provide autocompletion for service names defined in NEON files
  - **Parameter Autocompletion**: Provide autocompletion for parameters defined in NEON files
  - **Presenter Mapping**: Support for presenter mapping defined in NEON files
  - **Route Definition**: Support for route definitions in NEON files

- **Advanced Features**:
  - **Refactoring Support**: Add refactoring support for NEON files (rename, move, etc.)
  - **Find Usages**: Add find usages support for elements defined in NEON files
  - **Documentation**: Provide documentation for NEON syntax and features
  - **Quick Fixes**: Add quick fixes for common NEON issues

### 9. Enhanced IDE Integration

Improve integration with IDE features:

- **Live Templates**: Add more live templates for common Latte patterns
- **Intentions**: Add more intention actions for common Latte operations
- **Inspections**: Add more inspections to detect potential issues in templates
- **Quick Documentation**: Enhance quick documentation for Latte macros, filters, and n:attributes
- **Parameter Info**: Improve parameter info for macros and filters
- **Code Folding**: Add code folding for Latte blocks and macros
- **Color Settings**: Add more customizable color settings for Latte syntax elements

### 10. Advanced Debugging Support

Enhance debugging capabilities:

- **Breakpoints**: Add support for breakpoints in Latte templates
- **Variable Inspection**: Add support for inspecting variables in templates during debugging
- **Step Through**: Add support for stepping through template execution
- **Conditional Breakpoints**: Add support for conditional breakpoints in templates
- **Watches**: Add support for watches in templates
- **Debug Information**: Add more debug information for template execution

### 11. Custom Template Engine Support

Add support for custom template engines based on Latte:

- **Custom Macro Detection**: Automatically detect custom macros from project code
- **Custom Filter Detection**: Automatically detect custom filters from project code
- **Custom n:attribute Detection**: Automatically detect custom n:attributes from project code
- **Extension Points**: Add more extension points for custom template engines
- **Configuration**: Add more configuration options for custom template engines

### 12. Testing and Validation

Enhance testing and validation:

- **Test Coverage**: Increase test coverage for all features
- **Validation**: Better validation of templates against Latte syntax
- **Error Reporting**: Improved error reporting for template errors
- **Test Templates**: Add support for testing templates
- **Validation Rules**: Add more validation rules for templates
- **Quick Fixes**: Add quick fixes for common template errors

## Implementation Priorities

1. **Completed**:
   - Latte 4.0+ support ✓
   - Enhanced n:attributes support ✓
   - Version-specific features ✓
   - Additional Nette packages ✓
   - Advanced filters ✓
   - Integration with PHP ✓
   - Performance optimizations ✓

2. **High Priority**:
   - NEON file support

3. **Medium Priority**:
   - Enhanced IDE integration
   - Advanced debugging support
   - Custom template engine support

4. **Low Priority**:
   - Testing and validation enhancements

## Conclusion

Implementing these enhancements will ensure the Latte Plugin provides comprehensive support for all Latte and Nette features across all supported versions, making it an even more valuable tool for developers working with Latte templates.