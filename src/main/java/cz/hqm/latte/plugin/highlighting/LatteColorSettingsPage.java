package cz.hqm.latte.plugin.highlighting;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import cz.hqm.latte.plugin.file.LatteFileType;

import javax.swing.*;
import java.util.Map;

/**
 * Color settings page for Latte syntax highlighting.
 * Allows users to customize the colors used for Latte syntax elements.
 */
public class LatteColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Macro", LatteSyntaxHighlighter.LATTE_MACRO),
            new AttributesDescriptor("Macro Name", LatteSyntaxHighlighter.LATTE_MACRO_NAME),
            new AttributesDescriptor("Attribute", LatteSyntaxHighlighter.LATTE_ATTRIBUTE),
            new AttributesDescriptor("Filter", LatteSyntaxHighlighter.LATTE_FILTER),
            new AttributesDescriptor("Comment", LatteSyntaxHighlighter.LATTE_COMMENT),
            new AttributesDescriptor("Bad Character", LatteSyntaxHighlighter.BAD_CHARACTER)
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return LatteFileType.INSTANCE.getIcon();
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new LatteSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return """
               <!DOCTYPE html>
               <html>
               <head>
                   <title>{$title}</title>
               </head>
               <body>
                   {* This is a Latte comment *}
                   
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
               """;
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Latte";
    }
}