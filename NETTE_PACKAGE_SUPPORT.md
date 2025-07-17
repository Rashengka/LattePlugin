# Nette Package Support in Latte Plugin

This document describes the support for Nette packages in the Latte Plugin, including default variables, version detection, and settings.

## Supported Nette Packages

The Latte Plugin supports the following Nette packages:

- **nette/application** - Provides default variables like `$basePath`, `$baseUrl`, `$user`, `$presenter`, `$control`, and `$flashes`, as well as macros and n:attributes for working with presenters, templates, and AJAX
- **nette/forms** - Provides the `$form` variable when using `<form n:name>` or `{form} ... {/form}`, as well as macros and n:attributes for working with forms
- **nette/assets** - Provides support for the `asset()` function and macros for including CSS and JavaScript files

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

## Macros and n:attributes

The plugin provides code completion for macros and n:attributes from Nette packages. These are special tags and attributes that provide additional functionality in Latte templates.

### Core Latte Macros

The following macros are part of the core Latte templating engine:

- `{if}...{/if}` - Conditional rendering
- `{else}` - Alternative for if
- `{elseif}` - Alternative for if with condition
- `{ifset}...{/ifset}` - Conditional rendering if variable is set
- `{foreach}...{/foreach}` - Loop through an array or collection
- `{for}...{/for}` - Traditional for loop
- `{while}...{/while}` - While loop
- `{var}` - Define a variable
- `{continue}` - Skip to the next iteration
- `{break}` - Exit the loop

### Core Latte n:attributes

The following n:attributes are part of the core Latte templating engine:

- `n:if` - Conditional rendering
- `n:ifset` - Conditional rendering if variable is set
- `n:foreach` - Loop through an array or collection
- `n:inner-foreach` - Inner loop
- `n:class` - Conditional class
- `n:attr` - Conditional attributes
- `n:tag` - Conditional tag
- `n:inner-if` - Conditional inner content
- `n:else` - Used with n:inner-if

### nette/application Macros

The following macros are available from the nette/application package:

- `{link}` - Creates a link to a presenter/action
- `{plink}` - Creates a permanent link
- `{control}` - Renders a component
- `{snippet}...{/snippet}` - Defines a snippet for AJAX
- `{snippetArea}...{/snippetArea}` - Defines a snippet area
- `{include}` - Includes a template
- `{layout}` - Extends a parent template
- `{block}...{/block}` - Defines a block
- `{define}...{/define}` - Defines a block without printing it
- `{capture}...{/capture}` - Captures output to a variable

### nette/application n:attributes

The following n:attributes are available from the nette/application package:

- `n:href` - Creates a link to a presenter/action
- `n:snippet` - Defines a snippet for AJAX
- `n:include` - Includes a template
- `n:block` - Defines a block

### nette/forms Macros

The following macros are available from the nette/forms package:

- `{form}...{/form}` - Opens and closes a form
- `{input}` - Renders a form input
- `{label}` - Renders a form label
- `{inputError}` - Renders an error message for an input

### nette/forms n:attributes

The following n:attributes are available from the nette/forms package:

- `n:name` - Binds an input to a form control
- `n:validation` - Adds validation to a form control

### nette/assets Macros

The following macros are available from the nette/assets package:

- `{css}` - Includes CSS files
- `{js}` - Includes JavaScript files
- `{asset}` - Includes an asset with proper versioning

## Filters

The plugin provides code completion for filters from Nette packages. Filters are used to modify variables in Latte templates.

### Core Latte Filters

The following filters are part of the core Latte templating engine:

- `upper` - Converts a value to uppercase
- `lower` - Converts a value to lowercase
- `firstUpper` - Converts the first character to uppercase
- `capitalize` - Converts the first character of each word to uppercase
- `escape` - Escapes a string for use inside HTML
- `escapeUrl` - Escapes a string for use inside URL
- `noescape` - Prints a variable without escaping
- `truncate` - Shortens a string to the given maximum length
- `substring` - Returns part of a string
- `trim` - Strips whitespace from the beginning and end of a string
- `padLeft` - Pads a string to a certain length with another string from the left
- `padRight` - Pads a string to a certain length with another string from the right
- `replace` - Replaces all occurrences of the search string with the replacement
- `stripHtml` - Removes HTML tags and converts HTML entities to text
- `strip` - Removes HTML tags
- `indent` - Indents a text from the left with specified number of tabs
- `reverse` - Reverses a string or array
- `length` - Returns the length of a string or array
- `date` - Formats a date according to the specified format
- `number` - Formats a number
- `bytes` - Formats a number of bytes
- `percent` - Formats a number as a percentage
- `join` - Joins an array with a string
- `implode` - Joins an array with a string
- `explode` - Splits a string by a string
- `sort` - Sorts an array
- `default` - Returns the value if it's not empty, otherwise returns a default value
- `checkEmpty` - Checks if a value is empty

### nette/application Filters

The following filters are available from the nette/application package:

- `escapeUrl` - Escapes parameter in URL
- `length` - Returns the length of a string or array
- `webalize` - Adjusts string for usage in URL

### nette/forms Filters

The following filters are available from the nette/forms package:

- `translate` - Translates a message
- `required` - Marks a form control as required

### nette/assets Filters

The following filters are available from the nette/assets package:

- `asset` - Adds version to asset URL

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