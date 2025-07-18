package cz.hqm.latte.plugin.macros;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a macro from a Nette package.
 * Contains information about the macro name, description, and package.
 */
public class NetteMacro {
    private final String name;
    private final String description;
    private final String packageName;

    /**
     * Creates a new NetteMacro.
     *
     * @param name The name of the macro
     * @param description The description of the macro
     * @param packageName The name of the package that provides the macro
     */
    public NetteMacro(@NotNull String name, @NotNull String description, @NotNull String packageName) {
        this.name = name;
        this.description = description;
        this.packageName = packageName;
    }

    /**
     * Gets the name of the macro.
     *
     * @return The macro name
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the macro.
     *
     * @return The macro description
     */
    @NotNull
    public String getDescription() {
        return description;
    }

    /**
     * Gets the name of the package that provides the macro.
     *
     * @return The package name
     */
    @NotNull
    public String getPackageName() {
        return packageName;
    }

    /**
     * Gets the display text for the macro in completion items.
     *
     * @return The display text
     */
    @NotNull
    public String getDisplayText() {
        return name + " (" + packageName + ")";
    }

    /**
     * Gets the type text for the macro in completion items.
     *
     * @return The type text
     */
    @NotNull
    public String getTypeText() {
        return packageName + " macro";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetteMacro that = (NetteMacro) o;

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