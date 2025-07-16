# Nette Package Support in Latte Plugin

This document describes the support for Nette packages in the Latte Plugin, including default variables, version detection, and settings.

## Supported Nette Packages

The Latte Plugin supports the following Nette packages:

- **nette/application** - Provides default variables like `$basePath`, `$baseUrl`, `$user`, `$presenter`, `$control`, and `$flashes`
- **nette/forms** - Provides the `$form` variable when using `<form n:name>` or `{form} ... {/form}`
- **nette/assets** - Provides support for the `asset()` function

## Default Variables

The plugin provides code completion for default variables from Nette packages. These variables are available in Latte templates without needing to be explicitly defined.

### nette/application Variables

The following variables are available from the nette/application package:

- `$basePath` - Absolute URL path to the root directory (e.g., `/eshop`)
- `$baseUrl` - Absolute URL to the root directory (e.g., `http://localhost/eshop`)
- `$user` - Object representing the user (Nette\Security\User)
- `$presenter` - Current presenter (Nette\Application\UI\Presenter)
- `$control` - Current component or presenter (Nette\Application\UI\Control)
- `$flashes` - Array of messages sent by the `flashMessage()` function

### nette/forms Variables

The following variables are available from the nette/forms package:

- `$form` - Form object created by `<form n:name>` tag or `{form} ... {/form}` pair (Nette\Forms\Form)

### nette/assets Variables

The nette/assets package does not provide any default variables, but it provides the `asset()` function for working with assets.

## Version Detection

The plugin automatically detects the versions of Nette packages from the composer.json file in your project. It supports the following versions:

- Latte: 2.x, 3.0+, 4.0+
- nette/application: 2, 3, 4
- nette/forms: 2, 3, 4
- nette/assets: 1

The detected versions are used to provide appropriate code completion and other features.

## Settings

You can configure the Nette package support in the plugin settings:

1. Open the IDE settings (File > Settings or Ctrl+Alt+S)
2. Navigate to Languages & Frameworks > Latte
3. Configure the following settings:

### Latte Version

- **Latte Version**: Select the Latte version (2.x, 3.0+, 4.0+)
- **Override detected version**: Enable to use the selected version instead of the detected version

### Nette Packages

- **Enable nette/application support**: Enable or disable support for nette/application
- **Version**: Select the nette/application version (2, 3, 4)
- **Override detected version**: Enable to use the selected version instead of the detected version

- **Enable nette/forms support**: Enable or disable support for nette/forms
- **Version**: Select the nette/forms version (2, 3, 4)
- **Override detected version**: Enable to use the selected version instead of the detected version

- **Enable nette/assets support**: Enable or disable support for nette/assets
- **Version**: Select the nette/assets version (only version 1 is available)
- **Override detected version**: Enable to use the selected version instead of the detected version

## Usage

### Code Completion

The plugin provides code completion for default variables from Nette packages. When you type `$` in a Latte template, the plugin will suggest the available variables based on the enabled packages and their versions.

### Version-Specific Features

Some features may be specific to certain versions of Nette packages. The plugin will provide appropriate code completion and other features based on the detected or selected versions.

## Troubleshooting

If the plugin is not detecting the correct versions of Nette packages, you can:

1. Check that your composer.json file includes the correct dependencies
2. Use the "Override detected version" option in the settings to manually select the correct version
3. Make sure the package is enabled in the settings