package org.latte.plugin.custom;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Base class for all custom elements (tags, filters, functions, variables).
 * Contains common properties like name and description.
 */
public class CustomElement {
    private String name;
    private String description;

    /**
     * Default constructor for serialization.
     */
    public CustomElement() {
    }

    /**
     * Creates a new custom element.
     *
     * @param name The name of the element
     * @param description The description of the element
     */
    public CustomElement(@NotNull String name, @Nullable String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Gets the name of the element.
     *
     * @return The element name
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the element.
     *
     * @param name The element name
     */
    public void setName(@NotNull String name) {
        this.name = name;
    }

    /**
     * Gets the description of the element.
     *
     * @return The element description or null if not set
     */
    @Nullable
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the element.
     *
     * @param description The element description
     */
    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    /**
     * Gets the display text for the element in completion items.
     *
     * @return The display text
     */
    @NotNull
    public String getDisplayText() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomElement that = (CustomElement) o;

        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}