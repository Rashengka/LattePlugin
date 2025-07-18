package cz.hqm.latte.plugin.custom;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a custom Latte filter.
 */
public class CustomFilter extends CustomElement {
    
    /**
     * Default constructor for serialization.
     */
    public CustomFilter() {
        super();
    }
    
    /**
     * Creates a new custom filter.
     *
     * @param name The name of the filter
     * @param description The description of the filter
     */
    public CustomFilter(@NotNull String name, @Nullable String description) {
        super(name, description);
    }
    
    /**
     * Gets the type text for the filter in completion items.
     *
     * @return The type text
     */
    @NotNull
    public String getTypeText() {
        return "Custom filter";
    }
}