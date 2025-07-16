# Latte Version Support Implementation Guide

This document provides a comprehensive guide for implementing support for both Latte 2.x and 3.0+ versions in the Latte Plugin for JetBrains IDEs.

## Table of Contents

1. [Overview](#overview)
2. [Version Differences](#version-differences)
3. [Implementation Strategy](#implementation-strategy)
4. [Version Management](#version-management)
5. [Component Modifications](#component-modifications)
6. [Testing](#testing)
7. [Documentation](#documentation)

## Overview

The Latte template engine has two main versions with significant differences:
- **Latte 2.x** - The older version, still used in many projects
- **Latte 3.0+** - The newer version with additional features and syntax changes

This guide describes how to implement support for both versions in the Latte Plugin, allowing users to work with either version based on their project requirements.

## Version Differences

### Macros

#### Latte 3.0+ Specific Macros
- `{varType}` - Type declarations for variables
- `{templateType}` - Type declarations for templates
- `{php}` - PHP code execution
- `{do}` - Execute expressions without printing
- `{parameters}` - Define template parameters

#### Latte 2.x Specific Macros
- `{syntax}` - Change syntax delimiters
- `{use}` - Import macros from other files
- `{l}` - Left delimiter literal
- `{r}` - Right delimiter literal

### Attributes

#### Latte 3.0+ Specific Attributes
- `n:name` - Form field name
- `n:nonce` - Add nonce attribute for CSP
- `n:snippet-*` - Enhanced snippet attributes

#### Latte 2.x Specific Attributes
- `n:href` - Special link handling
- `n:ifcontent` - Conditional rendering based on content

### Filters

#### Latte 3.0+ Specific Filters
- `slice` - Extract a slice of an array
- `batch` - Split an array into chunks
- `spaceless` - Remove whitespace between HTML tags
- `clamp` - Clamp a value between min and max

#### Latte 2.x Specific Filters
- `bytes` - Format bytes to human-readable form
- `dataStream` - Convert to data URI scheme
- `url` - URL encoding

### Syntax Differences
- Latte 3.0+ uses strict typing with `{varType}` and `{templateType}`
- Latte 3.0+ has enhanced array syntax
- Latte 2.x uses different syntax for some macros

## Implementation Strategy

The implementation strategy involves:

1. Creating a version management mechanism
2. Making all plugin components version-aware
3. Adding version detection from project files
4. Providing a UI for manual version switching
5. Updating documentation to reflect version differences

## Version Management

### LatteVersion Class

Create a `LatteVersion` class to manage version information:

```java
package org.latte.plugin.version;

/**
 * Simple class to manage Latte version information.
 * Provides constants and methods for working with different Latte versions.
 */
public class LatteVersion {
    
    /**
     * Constant for Latte version 2.x
     */
    public static final String VERSION_2X = "2.x";
    
    /**
     * Constant for Latte version 3.0+
     */
    public static final String VERSION_3X = "3.0+";
    
    /**
     * The current Latte version being used.
     * Default is 3.0+.
     */
    private static String currentVersion = VERSION_3X;
    
    /**
     * Gets the current Latte version.
     *
     * @return The current version string
     */
    public static String getCurrentVersion() {
        return currentVersion;
    }
    
    /**
     * Sets the current Latte version.
     *
     * @param version The version to set (use VERSION_2X or VERSION_3X constants)
     */
    public static void setCurrentVersion(String version) {
        if (VERSION_2X.equals(version) || VERSION_3X.equals(version)) {
            currentVersion = version;
        }
    }
    
    /**
     * Checks if the current version is Latte 2.x.
     *
     * @return True if the current version is 2.x, false otherwise
     */
    public static boolean isVersion2x() {
        return VERSION_2X.equals(currentVersion);
    }
    
    /**
     * Checks if the current version is Latte 3.0+.
     *
     * @return True if the current version is 3.0+, false otherwise
     */
    public static boolean isVersion3x() {
        return VERSION_3X.equals(currentVersion);
    }
    
    /**
     * Gets the documentation URL for the current version.
     *
     * @return The documentation URL
     */
    public static String getDocumentationUrl() {
        return isVersion2x() 
            ? "https://latte.nette.org/en/syntax/2.x" 
            : "https://latte.nette.org/en/syntax";
    }
    
    /**
     * Gets the tags documentation URL for the current version.
     *
     * @return The tags documentation URL
     */
    public static String getTagsDocumentationUrl() {
        return isVersion2x() 
            ? "https://latte.nette.org/en/tags/2.x" 
            : "https://latte.nette.org/en/tags";
    }
    
    /**
     * Gets the filters documentation URL for the current version.
     *
     * @return The filters documentation URL
     */
    public static String getFiltersDocumentationUrl() {
        return isVersion2x() 
            ? "https://latte.nette.org/en/filters/2.x" 
            : "https://latte.nette.org/en/filters";
    }
    
    /**
     * Gets the functions documentation URL for the current version.
     *
     * @return The functions documentation URL
     */
    public static String getFunctionsDocumentationUrl() {
        return isVersion2x() 
            ? "https://latte.nette.org/en/functions/2.x" 
            : "https://latte.nette.org/en/functions";
    }
    
    /**
     * Attempts to detect the Latte version from the given content.
     * This is a simple heuristic based on version-specific syntax.
     *
     * @param content The Latte template content
     * @return The detected version or null if detection failed
     */
    public static String detectVersionFromContent(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }
        
        // Look for version-specific comment
        if (content.contains("{* Latte 2.x *}")) {
            return VERSION_2X;
        }
        if (content.contains("{* Latte 3.0+ *}")) {
            return VERSION_3X;
        }
        
        // Look for version-specific syntax patterns
        if (content.contains("{varType") || content.contains("{templateType")) {
            return VERSION_3X; // These are 3.0+ specific macros
        }
        
        // Default to null (couldn't detect)
        return null;
    }
}
```

### Version Detection from Composer

For more advanced version detection, implement a `LatteVersionDetector` class that can parse composer.json files to detect the Latte version:

```java
package org.latte.plugin.version;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for detecting Latte version from composer.json files.
 */
public class LatteVersionDetector {

    // Cache of detected versions by project
    private static final Map<String, String> versionCache = new HashMap<>();

    // Pattern to match version constraints like "^2.4", "~3.0", "3.*", etc.
    private static final Pattern VERSION_PATTERN = Pattern.compile("^[~^]?(\\d+)\\.");

    /**
     * Detects the Latte version for the given project by parsing composer.json.
     *
     * @param project The project to detect the version for
     * @return The detected version or null if not detected
     */
    public static String detectVersion(Project project) {
        if (project == null) {
            return null;
        }

        // Check cache first
        String projectPath = project.getBasePath();
        if (projectPath != null && versionCache.containsKey(projectPath)) {
            return versionCache.get(projectPath);
        }

        // Find composer.json file
        VirtualFile composerFile = findComposerFile(project);
        if (composerFile == null) {
            return null;
        }

        // Parse composer.json and extract Latte version
        String version = parseComposerJson(composerFile);

        // Cache the result
        if (projectPath != null && version != null) {
            versionCache.put(projectPath, version);
        }

        return version;
    }

    /**
     * Finds the composer.json file in the project.
     *
     * @param project The project to find the composer.json file in
     * @return The composer.json file or null if not found
     */
    private static VirtualFile findComposerFile(Project project) {
        String basePath = project.getBasePath();
        if (basePath == null) {
            return null;
        }

        String composerPath = basePath + "/composer.json";
        return VirtualFileManager.getInstance().findFileByUrl("file://" + composerPath);
    }

    /**
     * Parses the composer.json file to extract the Latte version.
     *
     * @param composerFile The composer.json file
     * @return The detected version or null if not detected
     */
    private static String parseComposerJson(VirtualFile composerFile) {
        try {
            // Parse JSON
            JSONParser parser = new JSONParser();
            JSONObject composerJson = (JSONObject) parser.parse(new FileReader(composerFile.getPath()));

            // Check require section
            JSONObject require = (JSONObject) composerJson.get("require");
            if (require != null) {
                // Check for latte/latte dependency
                Object latteVersion = require.get("latte/latte");
                if (latteVersion != null) {
                    return parseVersionConstraint(latteVersion.toString());
                }

                // Check for nette/latte dependency (older projects)
                Object netteLatteVersion = require.get("nette/latte");
                if (netteLatteVersion != null) {
                    return parseVersionConstraint(netteLatteVersion.toString());
                }
            }

            // Check require-dev section
            JSONObject requireDev = (JSONObject) composerJson.get("require-dev");
            if (requireDev != null) {
                // Check for latte/latte dependency
                Object latteVersion = requireDev.get("latte/latte");
                if (latteVersion != null) {
                    return parseVersionConstraint(latteVersion.toString());
                }

                // Check for nette/latte dependency (older projects)
                Object netteLatteVersion = requireDev.get("nette/latte");
                if (netteLatteVersion != null) {
                    return parseVersionConstraint(netteLatteVersion.toString());
                }
            }
        } catch (IOException | ParseException e) {
            // Log error or handle exception
            System.err.println("Error parsing composer.json: " + e.getMessage());
        }

        return null;
    }

    /**
     * Parses a version constraint string to determine the major version.
     *
     * @param versionConstraint The version constraint string (e.g., "^2.4", "~3.0", "3.*")
     * @return The corresponding version string or null if not recognized
     */
    private static String parseVersionConstraint(String versionConstraint) {
        Matcher matcher = VERSION_PATTERN.matcher(versionConstraint);
        if (matcher.find()) {
            String majorVersion = matcher.group(1);
            if ("2".equals(majorVersion)) {
                return LatteVersion.VERSION_2X;
            } else if ("3".equals(majorVersion)) {
                return LatteVersion.VERSION_3X;
            }
        }
        return null;
    }
}
```

## Component Modifications

### LatteCompletionContributor

Modify the `LatteCompletionContributor` class to provide version-specific completions:

```java
// Add import for LatteVersion
import org.latte.plugin.version.LatteVersion;

// In the addCompletions method for macros
protected void addCompletions(@NotNull CompletionParameters parameters,
                             @NotNull ProcessingContext context,
                             @NotNull CompletionResultSet result) {
    // Add common Latte macros (available in both 2.x and 3.0+)
    result.addElement(LookupElementBuilder.create("if").bold().withTypeText("Latte macro"));
    result.addElement(LookupElementBuilder.create("else").bold().withTypeText("Latte macro"));
    result.addElement(LookupElementBuilder.create("elseif").bold().withTypeText("Latte macro"));
    result.addElement(LookupElementBuilder.create("foreach").bold().withTypeText("Latte macro"));
    result.addElement(LookupElementBuilder.create("include").bold().withTypeText("Latte macro"));
    result.addElement(LookupElementBuilder.create("block").bold().withTypeText("Latte macro"));
    result.addElement(LookupElementBuilder.create("define").bold().withTypeText("Latte macro"));
    result.addElement(LookupElementBuilder.create("var").bold().withTypeText("Latte macro"));
    result.addElement(LookupElementBuilder.create("capture").bold().withTypeText("Latte macro"));
    result.addElement(LookupElementBuilder.create("snippet").bold().withTypeText("Latte macro"));
    result.addElement(LookupElementBuilder.create("_").bold().withTypeText("Latte macro"));
    result.addElement(LookupElementBuilder.create("=").bold().withTypeText("Latte macro"));
    
    // Add version-specific macros
    if (LatteVersion.isVersion3x()) {
        // Latte 3.0+ specific macros
        result.addElement(LookupElementBuilder.create("varType").bold().withTypeText("Latte 3.0+ macro"));
        result.addElement(LookupElementBuilder.create("templateType").bold().withTypeText("Latte 3.0+ macro"));
        result.addElement(LookupElementBuilder.create("php").bold().withTypeText("Latte 3.0+ macro"));
        result.addElement(LookupElementBuilder.create("do").bold().withTypeText("Latte 3.0+ macro"));
        result.addElement(LookupElementBuilder.create("parameters").bold().withTypeText("Latte 3.0+ macro"));
    } else {
        // Latte 2.x specific macros
        result.addElement(LookupElementBuilder.create("syntax").bold().withTypeText("Latte 2.x macro"));
        result.addElement(LookupElementBuilder.create("use").bold().withTypeText("Latte 2.x macro"));
        result.addElement(LookupElementBuilder.create("l").bold().withTypeText("Latte 2.x macro"));
        result.addElement(LookupElementBuilder.create("r").bold().withTypeText("Latte 2.x macro"));
    }
}

// Similarly, update the n:attributes completion provider
// ...

// And the filters completion provider
// ...
```

### LatteDocumentationProvider

Modify the `LatteDocumentationProvider` class to provide version-specific documentation:

```java
// Add import for LatteVersion
import org.latte.plugin.version.LatteVersion;

// Add separate maps for version-specific documentation
private static final Map<String, String> MACRO_DOCS_2X = new HashMap<>();
private static final Map<String, String> MACRO_DOCS_3X = new HashMap<>();
private static final Map<String, String> ATTRIBUTE_DOCS_2X = new HashMap<>();
private static final Map<String, String> ATTRIBUTE_DOCS_3X = new HashMap<>();
private static final Map<String, String> FILTER_DOCS_2X = new HashMap<>();
private static final Map<String, String> FILTER_DOCS_3X = new HashMap<>();

static {
    // Initialize common documentation (available in both versions)
    // ...
    
    // Initialize Latte 2.x specific documentation
    MACRO_DOCS_2X.put("syntax", "Change syntax delimiters.<br><code>{syntax double}</code>");
    MACRO_DOCS_2X.put("use", "Import macros from other files.<br><code>{use MyMacros}</code>");
    MACRO_DOCS_2X.put("l", "Left delimiter literal.<br><code>{l}Latte{r}</code>");
    MACRO_DOCS_2X.put("r", "Right delimiter literal.<br><code>{l}Latte{r}</code>");
    
    // Initialize Latte 3.0+ specific documentation
    MACRO_DOCS_3X.put("varType", "Type declarations for variables.<br><code>{varType string $name}</code>");
    MACRO_DOCS_3X.put("templateType", "Type declarations for templates.<br><code>{templateType App\\Template}</code>");
    MACRO_DOCS_3X.put("php", "PHP code execution.<br><code>{php $var = 123}</code>");
    MACRO_DOCS_3X.put("do", "Execute expressions without printing.<br><code>{do $form->render()}</code>");
    MACRO_DOCS_3X.put("parameters", "Define template parameters.<br><code>{parameters string $name}</code>");
    
    // Similarly for attributes and filters
    // ...
}

// Modify the generateDoc method to use the appropriate documentation map
@Nullable
@Override
public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
    if (element == null) {
        return null;
    }
    
    String text = element.getText();
    
    // Check if the element is a Latte macro
    if (text.startsWith("{") && text.endsWith("}")) {
        String macroName = text.substring(1, text.length() - 1).trim();
        
        // Get the appropriate documentation map based on the current version
        Map<String, String> macroDocsMap = LatteVersion.isVersion2x() ? MACRO_DOCS_2X : MACRO_DOCS_3X;
        
        // Check version-specific documentation first
        if (macroDocsMap.containsKey(macroName)) {
            return createDocumentation("Latte Macro: " + macroName, macroDocsMap.get(macroName));
        }
        
        // Check common documentation
        if (MACRO_DOCS.containsKey(macroName)) {
            return createDocumentation("Latte Macro: " + macroName, MACRO_DOCS.get(macroName));
        }
    }
    
    // Similarly for attributes and filters
    // ...
    
    return null;
}
```

### LatteSyntaxHighlighter

Update the `LatteSyntaxHighlighter` class to highlight version-specific syntax elements:

```java
// Add import for LatteVersion
import org.latte.plugin.version.LatteVersion;

// In the getTokenHighlights method, add version-specific highlighting
@NotNull
@Override
public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
    // Common highlighting logic
    // ...
    
    // Version-specific highlighting
    if (LatteVersion.isVersion3x()) {
        // Highlight Latte 3.0+ specific tokens
        // ...
    } else {
        // Highlight Latte 2.x specific tokens
        // ...
    }
    
    // Delegate to HTML syntax highlighter for HTML tokens
    return super.getTokenHighlights(tokenType);
}
```

## UI for Version Switching

Add a simple action to toggle between Latte versions:

```java
package org.latte.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.latte.plugin.version.LatteVersion;

/**
 * Action to toggle between Latte versions.
 */
public class LatteVersionToggleAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        
        // Get the current version
        String currentVersion = LatteVersion.getCurrentVersion();
        
        // Toggle to the other version
        String newVersion = LatteVersion.VERSION_2X.equals(currentVersion) 
            ? LatteVersion.VERSION_3X 
            : LatteVersion.VERSION_2X;
        
        // Set the new version
        LatteVersion.setCurrentVersion(newVersion);
        
        // Show a notification
        Messages.showInfoMessage(
            project,
            "Latte version switched to " + newVersion,
            "Latte Version"
        );
    }
}
```

Register the action in plugin.xml:

```xml
<actions>
    <action id="Latte.ToggleVersion"
            class="org.latte.plugin.actions.LatteVersionToggleAction"
            text="Toggle Latte Version"
            description="Toggle between Latte 2.x and 3.0+ versions">
        <add-to-group group-id="ToolsMenu" anchor="last"/>
    </action>
</actions>
```

## Testing

Create test files for both Latte versions:

### Latte 2.x Test File

```latte
{* Latte 2.x *}
<!DOCTYPE html>
<html>
<head>
    <title>{$title}</title>
</head>
<body>
    {syntax double}
    {{if $user->isLoggedIn()}}
        <h1>Welcome, {{$user->name|capitalize}}</h1>
    {{else}}
        <h1>Please log in</h1>
    {{/if}}
    {{syntax}}
    
    {l}This is a literal Latte syntax{r}
    
    {use MyMacros}
    
    <ul n:if="$items" n:ifcontent>
        {foreach $items as $item}
            <li>{$item|bytes}</li>
        {/foreach}
    </ul>
</body>
</html>
```

### Latte 3.0+ Test File

```latte
{* Latte 3.0+ *}
<!DOCTYPE html>
<html>
<head>
    <title>{$title}</title>
</head>
<body>
    {varType string $name}
    {templateType App\Template}
    
    {parameters
        string $title
        int $count = 0
    }
    
    {if $user->isLoggedIn()}
        <h1>Welcome, {$user->name|capitalize}</h1>
        {do $form->render()}
    {else}
        <h1>Please log in</h1>
    {/if}
    
    {php
        $items = ['apple', 'banana', 'orange'];
        $count = count($items);
    }
    
    <ul n:if="$items" n:nonce>
        {foreach $items as $item}
            <li>{$item|slice: 0, 3}</li>
        {/foreach}
    </ul>
</body>
</html>
```

## Documentation

Update the project documentation to reflect version support:

### README.md

Add a section about version support:

```markdown
## Version Support

The Latte Plugin supports both Latte 2.x and 3.0+ versions. You can switch between versions using the "Toggle Latte Version" action in the Tools menu.

### Version Detection

The plugin attempts to detect the Latte version from:
1. Composer dependencies (if available)
2. Version-specific syntax in the file
3. Version comments (`{* Latte 2.x *}` or `{* Latte 3.0+ *}`)

### Version-Specific Features

The plugin provides version-specific features:
- Code completion for version-specific macros, attributes, and filters
- Documentation for version-specific features
- Syntax highlighting for version-specific syntax
```

### SUMMARY.md

Update the summary to include version support:

```markdown
## Version Support

The Latte Plugin now supports both Latte 2.x and 3.0+ versions. This includes:

- Version detection from Composer dependencies
- Manual version switching
- Version-specific code completion
- Version-specific documentation
- Version-specific syntax highlighting
```

## Conclusion

Implementing support for both Latte 2.x and 3.0+ versions will significantly enhance the plugin's usability for developers working with different Latte versions. The version-aware approach ensures that users get the appropriate features and documentation for their specific Latte version.