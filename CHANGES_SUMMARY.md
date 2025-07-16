# Changes Summary

This document summarizes the changes made to implement support for Nette default variables in the Latte plugin.

## 1. Version Detection

### Added support for Latte 4.x
- Added VERSION_4X to the LatteVersion enum
- Updated LatteVersionDetector to detect Latte 4.x from composer.json

### Enhanced NettePackageDetector
- Added support for detecting package versions, not just presence
- Added PackageInfo class to store both presence and version information
- Added isPackagePresent method to check if a package is present
- Added getPackageVersion method to get the major version of a package

## 2. Settings

### Extended LatteSettings
- Added fields for storing package versions:
  - selectedNetteApplicationVersion
  - selectedNetteFormsVersion
  - selectedNetteAssetsVersion
- Added fields for overriding detected package versions:
  - overrideDetectedNetteApplicationVersion
  - overrideDetectedNetteFormsVersion
  - overrideDetectedNetteAssetsVersion
- Added getter and setter methods for these fields

### Updated LatteSettingsConfigurable
- Added UI elements for selecting package versions
- Added UI elements for overriding detected package versions
- Added listeners to enable/disable version settings based on package enable/disable
- Updated isModified, apply, and reset methods to handle the new settings

## 3. Default Variables

### Created NetteDefaultVariablesProvider
- Added NetteVariable class to represent a variable with name, type, and description
- Added methods to get variables for each Nette package:
  - getNetteApplicationVariables
  - getNetteFormsVariables
  - getNetteAssetsVariables
- Added methods to get package versions, respecting the override settings
- Added methods to check if packages are enabled
- Added method to get all variables for a project

### Updated LatteCompletionContributor
- Added a completion provider for variables that uses NetteDefaultVariablesProvider
- Added code to suggest variables when the user types "$" in a Latte template

## 4. Documentation

### Created NETTE_PACKAGE_SUPPORT.md
- Described the supported Nette packages
- Listed the default variables provided by each package
- Explained the version detection mechanism
- Described the settings for configuring package support
- Provided usage instructions for code completion and version-specific features
- Added troubleshooting tips

## 5. Tests

### Created NettePackageDetectorTest
- Added tests for version detection
- Added tests for package presence detection
- Added tests for cache clearing

### Created NetteVariableCompletionTest
- Added tests for Nette Application variables
- Added tests for Nette Forms variables
- Added tests for disabled packages

### Created LatteSettingsTest
- Added tests for Latte version override
- Added tests for Nette package version override
- Added tests for package enable/disable settings

## Summary

The changes add support for default variables from Nette packages (nette/application, nette/forms, nette/assets) to the Latte plugin. The plugin now detects the versions of these packages from composer.json and provides appropriate code completion for default variables. Users can override the detected versions in the plugin settings.