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

1. **Completed**:
   - Latte 4.0+ support ✓
   - Enhanced n:attributes support ✓
   - Version-specific features ✓
   - Additional Nette packages ✓
   - Advanced filters ✓

2. **Medium Priority**:
   - Integration with PHP

3. **Low Priority**:
   - Performance optimizations
   - Testing and validation enhancements

## Conclusion

Implementing these enhancements will ensure the Latte Plugin provides comprehensive support for all Latte and Nette features across all supported versions, making it an even more valuable tool for developers working with Latte templates.