package org.latte.plugin.custom;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a custom n:attribute defined by the user.
 * Contains information about the attribute name, description, and usage.
 */
public class CustomAttribute {
    private final String name;
    private final String description;
    private final String usage;

    /**
     * Creates a new CustomAttribute.
     *
     * @param name The name of the attribute (including the n: prefix)
     * @param description The description of the attribute
     * @param usage Example usage of the attribute
     */
    public CustomAttribute(@NotNull String name, @Nullable String description, @Nullable String usage) {
        this.name = name;
        this.description = description;
        this.usage = usage;
    }

    /**
     * Gets the name of the attribute.
     *
     * @return The attribute name
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the attribute.
     *
     * @return The attribute description or null if not provided
     */
    @Nullable
    public String getDescription() {
        return description;
    }

    /**
     * Gets the example usage of the attribute.
     *
     * @return The attribute usage or null if not provided
     */
    @Nullable
    public String getUsage() {
        return usage;
    }

    /**
     * Gets the display text for the attribute in completion items.
     *
     * @return The display text
     */
    @NotNull
    public String getDisplayText() {
        return name + " (custom)";
    }

    /**
     * Gets the type text for the attribute in completion items.
     *
     * @return The type text
     */
    @NotNull
    public String getTypeText() {
        return "custom attribute";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomAttribute that = (CustomAttribute) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}