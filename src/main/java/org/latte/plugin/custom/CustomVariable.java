package org.latte.plugin.custom;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a custom Latte variable.
 */
public class CustomVariable extends CustomElement {
    private String type;
    
    /**
     * Default constructor for serialization.
     */
    public CustomVariable() {
        super();
    }
    
    /**
     * Creates a new custom variable.
     *
     * @param name The name of the variable
     * @param type The type of the variable
     * @param description The description of the variable
     */
    public CustomVariable(@NotNull String name, @Nullable String type, @Nullable String description) {
        super(name, description);
        this.type = type;
    }
    
    /**
     * Gets the type of the variable.
     *
     * @return The variable type or null if not set
     */
    @Nullable
    public String getType() {
        return type;
    }
    
    /**
     * Sets the type of the variable.
     *
     * @param type The variable type
     */
    public void setType(@Nullable String type) {
        this.type = type;
    }
    
    /**
     * Gets the type text for the variable in completion items.
     *
     * @return The type text
     */
    @NotNull
    public String getTypeText() {
        return type != null ? type : "Custom variable";
    }
    
    @Override
    public String getDisplayText() {
        return "$" + getName();
    }
}