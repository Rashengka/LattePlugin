# Latte Plugin Test Coverage Report

## Overview

This document provides an overview of the test coverage for the Latte Plugin. It lists all the major features of the plugin and the corresponding test classes that cover them.

## Features and Test Coverage

### 1. Syntax Highlighting

**Feature Description**: Highlighting for Latte macros, attributes, filters, and comments.

**Test Classes**:
- `cz.hqm.latte.plugin.test.highlighting.LatteSyntaxHighlighterTest` - Tests the syntax highlighter's ability to correctly highlight different Latte elements.

**Coverage Status**: ✅ Covered

### 2. Lexer and Parser

**Feature Description**: Tokenization and parsing of Latte templates.

**Test Classes**:
- `cz.hqm.latte.plugin.test.lexer.LatteErrorDetectionTest` - Tests the lexer's ability to detect errors in Latte syntax.

**Coverage Status**: ✅ Covered

### 3. File Type Recognition

**Feature Description**: Recognition of Latte files by their extension.

**Test Classes**:
- `cz.hqm.latte.plugin.test.file.LatteFileTypeTest` - Tests the file type recognition for Latte files.

**Coverage Status**: ✅ Covered

### 4. Code Completion

**Feature Description**: Code completion for Latte macros, attributes, filters, and variables.

**Test Classes**:
- `cz.hqm.latte.plugin.test.completion.NetteCompletionTest` - Tests code completion for Nette macros.
- `cz.hqm.latte.plugin.test.completion.NetteVariableCompletionTest` - Tests code completion for Nette variables.
- `cz.hqm.latte.plugin.test.completion.CustomElementsCompletionTest` - Tests code completion for custom elements.

**Coverage Status**: ✅ Covered

### 5. Nette Package Integration

**Feature Description**: Integration with Nette packages (application, forms, assets).

**Test Classes**:
- `cz.hqm.latte.plugin.test.macros.NetteMacroTest` - Tests the Nette macro provider.
- `cz.hqm.latte.plugin.test.version.NettePackageDetectorTest` - Tests the Nette package detector.

**Coverage Status**: ✅ Covered

### 6. Settings Management

**Feature Description**: Management of plugin settings (Latte version, Nette packages).

**Test Classes**:
- `cz.hqm.latte.plugin.test.settings.LatteSettingsTest` - Tests the Latte settings.
- `cz.hqm.latte.plugin.test.settings.LatteProjectSettingsTest` - Tests the Latte project settings for custom elements.

**Coverage Status**: ✅ Covered

### 7. Custom Elements

**Feature Description**: Support for custom tags, filters, functions, and variables.

**Test Classes**:
- `cz.hqm.latte.plugin.test.custom.CustomElementTest` - Tests the base custom element class.
- `cz.hqm.latte.plugin.test.custom.CustomTagTest` - Tests the custom tag class.
- `cz.hqm.latte.plugin.test.custom.CustomFilterTest` - Tests the custom filter class.
- `cz.hqm.latte.plugin.test.custom.CustomFunctionTest` - Tests the custom function class.
- `cz.hqm.latte.plugin.test.custom.CustomVariableTest` - Tests the custom variable class.
- `cz.hqm.latte.plugin.test.custom.CustomTagsProviderTest` - Tests the custom tags provider.
- `cz.hqm.latte.plugin.test.custom.CustomFiltersProviderTest` - Tests the custom filters provider.
- `cz.hqm.latte.plugin.test.custom.CustomFunctionsProviderTest` - Tests the custom functions provider.
- `cz.hqm.latte.plugin.test.custom.CustomVariablesProviderTest` - Tests the custom variables provider.

**Coverage Status**: ✅ Covered

### 8. Documentation Provider

**Feature Description**: Documentation for Latte macros, attributes, and filters.

**Test Classes**:
- `cz.hqm.latte.plugin.test.documentation.LatteDocumentationProviderTest` - Tests the documentation provider.

**Coverage Status**: ✅ Covered

### 9. Intention Actions

**Feature Description**: Intention actions for adding custom elements.

**Test Classes**:
- `cz.hqm.latte.plugin.test.intention.AddCustomTagIntentionActionTest` - Tests the intention action for adding custom tags.

**Coverage Status**: ✅ Covered

## Summary

All major features of the Latte Plugin now have corresponding test classes. The tests cover the functionality of each feature and ensure that they work as expected.

## Notes

- Some tests may require additional configuration in the build system to be discovered by Gradle.
- The intention action tests are limited in their ability to test the dialog-based functionality, as this requires a UI environment.
- The tests for custom elements are comprehensive, covering all aspects of the feature from the model classes to the providers and integration with completion.