package cz.hqm.latte.plugin.custom;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a custom Latte function.
 */
public class CustomFunction extends CustomElement {
    
    /**
     * Default constructor for serialization.
     */
    public CustomFunction() {
        super();
    }
    
    /**
     * Creates a new custom function.
     *
     * @param name The name of the function
     * @param description The description of the function
     */
    public CustomFunction(@NotNull String name, @Nullable String description) {
        super(name, description);
    }
    
    /**
     * Gets the type text for the function in completion items.
     *
     * @return The type text
     */
    @NotNull
    public String getTypeText() {
        return "Custom function";
    }
}