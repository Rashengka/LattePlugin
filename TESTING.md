# Testing the Latte Plugin

This document provides instructions for testing the Latte Plugin functionality after installation.

## Prerequisites

- JetBrains IDE (IntelliJ IDEA, PhpStorm, WebStorm, etc.)
- Latte Plugin installed

## Testing Steps

### 1. Create a New Latte File

1. Right-click on a directory in the Project view
2. Select "New" > "File"
3. Enter a name with the `.latte` extension (e.g., `test.latte`)
4. Verify that the file opens with the Latte file type icon

### 2. Test Syntax Highlighting

Copy and paste the following code into your Latte file:

```latte
{* This is a Latte comment *}
<!DOCTYPE html>
<html>
<head>
    <title>{$title}</title>
</head>
<body>
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

Verify that:
- Latte macros (`{if}`, `{foreach}`, etc.) are highlighted
- Latte n:attributes (`n:if`) are highlighted
- Latte filters (`|capitalize`) are highlighted
- Latte comments (`{* ... *}`) are highlighted

### 3. Test Code Completion

1. Type an opening brace `{` and press Ctrl+Space (or Cmd+Space on Mac)
2. Verify that Latte macros appear in the completion list
3. Type `n:` at the beginning of an HTML tag attribute and press Ctrl+Space
4. Verify that Latte n:attributes appear in the completion list
5. Type a pipe character `|` after a variable and press Ctrl+Space
6. Verify that Latte filters appear in the completion list

### 4. Test Documentation

1. Place the cursor on a Latte macro (e.g., `{if}`)
2. Press Ctrl+Q (or F1 on Mac) to view quick documentation
3. Verify that documentation for the macro appears
4. Repeat for n:attributes and filters

### 5. Test HTML Integration

1. Verify that HTML syntax highlighting works alongside Latte syntax
2. Verify that HTML tag completion works
3. Verify that HTML attribute completion works

## Reporting Issues

If you encounter any issues during testing, please report them on the [GitHub Issues page](https://github.com/Rashengka/LattePlugin/issues) with the following information:

1. Description of the issue
2. Steps to reproduce
3. Expected behavior
4. Actual behavior
5. Screenshots (if applicable)
6. IDE version and OS

## Advanced Testing

For more advanced testing, you can use the sample file provided in the `samples/example.latte` file, which demonstrates more complex Latte features.