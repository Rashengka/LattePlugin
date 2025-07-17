# Future Enhancements for Latte Plugin

This document outlines potential future enhancements for the Latte Plugin to ensure comprehensive support for all Latte and Nette features across all supported versions.

## Overview

The Latte Plugin currently supports many features from Latte and Nette packages, including:
- Core Latte macros, filters, and n:attributes
- Nette/application macros, filters, and n:attributes
- Nette/forms macros, filters, and n:attributes
- Nette/assets macros and filters
- Version-specific features for Latte 2.x and 3.0+

However, there are opportunities for enhancement to ensure complete coverage of all features and versions.

## Potential Enhancements

### 1. Latte 4.0+ Support (Implemented)

The plugin now includes support for Latte 4.0+ features:

- **Completion Contributor**: LatteCompletionContributor has been updated to handle Latte 4.0+ specific macros and syntax, including typeCheck, strictTypes, asyncInclude, await, inject, and more.
- **Version Detection**: LatteVersionManager has been enhanced with isVersion4x() method and LatteVersion.detectVersionFromContent() now detects Latte 4.0+ specific syntax.
- **Documentation**: Documentation has been updated with Latte 4.0+ specific features in LATTE_4.0_SUPPORT.md.

See LATTE_4.0_SUPPORT.md for detailed information about the implementation.

### 2. Additional Nette Packages

Consider adding support for additional Nette packages:

- **nette/database**: Support for database-related macros and filters
- **nette/security**: Support for security-related macros and variables
- **nette/mail**: Support for email-related macros and templates
- **nette/http**: Support for HTTP-related variables and functions

### 3. Enhanced n:attributes Support

Improve support for n:attributes:

- **Dynamic n:attributes**: Better support for dynamically generated n:attributes
- **Prefixed n:attributes**: Support for prefixed n:attributes (e.g., n:class:hover)
- **Custom n:attributes**: Enhanced support for custom n:attributes

### 4. Advanced Filters

Add support for more advanced filters:

- **Custom Filter Chaining**: Better support for chaining custom filters
- **Filter Parameters**: Enhanced support for filter parameters
- **Filter Auto-completion**: Smarter auto-completion for filters based on context

### 5. Version-Specific Features

Implement more granular version-specific features:

- **Minor Version Differences**: Support for differences between minor versions (e.g., 3.1 vs 3.2)
- **Deprecated Features**: Warnings for deprecated features in newer versions
- **Version Migration**: Tools to help migrate between versions

### 6. Performance Optimizations

Optimize performance for large templates:

- **Caching**: Better caching of parsed templates
- **Incremental Parsing**: Support for incremental parsing of templates
- **Memory Usage**: Reduce memory usage for large templates

### 7. Integration with PHP

Improve integration with PHP code:

- **PHP Type Hints**: Better support for PHP type hints in templates
- **PHP Variables**: Smarter detection of PHP variables available in templates
- **PHP Functions**: Better support for PHP functions used in templates

### 8. Testing and Validation

Enhance testing and validation:

- **Test Coverage**: Increase test coverage for all features
- **Validation**: Better validation of templates against Latte syntax
- **Error Reporting**: Improved error reporting for template errors

## Implementation Priorities

1. **High Priority**:
   - Latte 4.0+ support
   - Enhanced n:attributes support
   - Version-specific features

2. **Medium Priority**:
   - Additional Nette packages
   - Advanced filters
   - Integration with PHP

3. **Low Priority**:
   - Performance optimizations
   - Testing and validation enhancements

## Conclusion

Implementing these enhancements will ensure the Latte Plugin provides comprehensive support for all Latte and Nette features across all supported versions, making it an even more valuable tool for developers working with Latte templates.