package org.latte.plugin.filters;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a filter from a Nette package.
 * Contains information about the filter name, description, and package.
 */
public class NetteFilter {
    private final String name;
    private final String description;
    private final String packageName;

    /**
     * Creates a new NetteFilter.
     *
     * @param name The name of the filter
     * @param description The description of the filter
     * @param packageName The name of the package that provides the filter
     */
    public NetteFilter(@NotNull String name, @NotNull String description, @NotNull String packageName) {
        this.name = name;
        this.description = description;
        this.packageName = packageName;
    }

    /**
     * Gets the name of the filter.
     *
     * @return The filter name
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the filter.
     *
     * @return The filter description
     */
    @NotNull
    public String getDescription() {
        return description;
    }

    /**
     * Gets the name of the package that provides the filter.
     *
     * @return The package name
     */
    @NotNull
    public String getPackageName() {
        return packageName;
    }

    /**
     * Gets the display text for the filter in completion items.
     *
     * @return The display text
     */
    @NotNull
    public String getDisplayText() {
        return name + " (" + packageName + ")";
    }

    /**
     * Gets the type text for the filter in completion items.
     *
     * @return The type text
     */
    @NotNull
    public String getTypeText() {
        return packageName + " filter";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetteFilter that = (NetteFilter) o;

        if (!name.equals(that.name)) return false;
        return packageName.equals(that.packageName);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + packageName.hashCode();
        return result;
    }
}