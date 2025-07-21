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

### 6. Test Navigation to PHP Methods

1. Create a PHP file with a presenter class (e.g., `ProductPresenter.php`)
2. Add action methods like `actionDetail()` and signal handlers like `handleDelete()`
3. In your Latte file, add links to these methods:
   ```latte
   <a n:href="Product:detail">Detail</a>
   <a href="{link delete!}">Delete</a>
   <a href="{plink Product:detail}">Permanent Link</a>
   ```
4. Place the cursor on `Product:detail` or `delete!` and press Ctrl+B (or Cmd+B on Mac)
5. Verify that navigation to the corresponding PHP method works

### 7. Test Component Autocomplete and Navigation

1. Create a PHP file with a presenter class that has component factory methods:
   ```php
   protected function createComponentProductList() {
       return new \Nette\Application\UI\Control();
   }
   ```
2. In your Latte file, add a control macro:
   ```latte
   {control productList}
   ```
3. Type `{control` and press Ctrl+Space to verify component name completion
4. Place the cursor on `productList` and press Ctrl+B (or Cmd+B on Mac)
5. Verify that navigation to the component factory method works

### 8. Test Template Inclusion and Inheritance

1. Create multiple Latte files with blocks and includes:
   ```latte
   {* parent.latte *}
   {block content}{/block}

   {* child.latte *}
   {layout 'parent.latte'}
   {block content}Child content{/block}

   {* blocks.latte *}
   {define modal}Modal content{/define}

   {* main.latte *}
   {include 'blocks.latte'}
   {include #modal}
   ```
2. Place the cursor on file paths or block names and press Ctrl+B (or Cmd+B on Mac)
3. Verify that navigation between templates and blocks works

### 9. Test Type Macros and Type Checking

1. Add type macros to your Latte file:
   ```latte
   {varType $user: \App\Model\User}
   {templateType \App\Templates\ProductTemplate}
   {var $count = 5}
   ```
2. Place the cursor on type references and press Ctrl+B (or Cmd+B on Mac)
3. Verify that navigation to the referenced PHP classes works

### 10. Test Version-Specific Features

1. Create Latte files with version-specific features:
   ```latte
   {* Latte 2.x *}
   {syntax double}
   {{$variable}}

   {* Latte 3.x+ *}
   {switch $value}
     {case 1}One{/case}
     {case 2}Two{/case}
     {default}Other{/default}
   {/switch}
   ```
2. Verify that syntax highlighting and code completion work for version-specific features

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