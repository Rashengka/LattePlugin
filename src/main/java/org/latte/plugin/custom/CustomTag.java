package org.latte.plugin.custom;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a custom Latte tag/macro.
 */
public class CustomTag extends CustomElement {
    
    /**
     * Default constructor for serialization.
     */
    public CustomTag() {
        super();
    }
    
    /**
     * Creates a new custom tag.
     *
     * @param name The name of the tag
     * @param description The description of the tag
     */
    public CustomTag(@NotNull String name, @Nullable String description) {
        super(name, description);
    }
    
    /**
     * Gets the type text for the tag in completion items.
     *
     * @return The type text
     */
    @NotNull
    public String getTypeText() {
        return "Custom tag";
    }
}