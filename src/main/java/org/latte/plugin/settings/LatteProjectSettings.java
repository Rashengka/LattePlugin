package org.latte.plugin.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.latte.plugin.custom.CustomFilter;
import org.latte.plugin.custom.CustomFunction;
import org.latte.plugin.custom.CustomTag;
import org.latte.plugin.custom.CustomVariable;

import java.util.ArrayList;
import java.util.List;

/**
 * Project-level persistent settings for the Latte plugin.
 * Stores custom elements (tags, filters, functions, variables) for the project.
 */
@Service(Service.Level.PROJECT)
@State(
    name = "org.latte.plugin.settings.LatteProjectSettings",
    storages = @Storage("latteCustomElements.xml")
)
public final class LatteProjectSettings implements PersistentStateComponent<LatteProjectSettings> {
    
    /**
     * List of custom tags.
     */
    private List<CustomTag> customTags = new ArrayList<>();
    
    /**
     * List of custom filters.
     */
    private List<CustomFilter> customFilters = new ArrayList<>();
    
    /**
     * List of custom functions.
     */
    private List<CustomFunction> customFunctions = new ArrayList<>();
    
    /**
     * List of custom variables.
     */
    private List<CustomVariable> customVariables = new ArrayList<>();
    
    /**
     * Gets the instance of the settings service for the specified project.
     *
     * @param project The project
     * @return The settings instance
     */
    public static LatteProjectSettings getInstance(@NotNull Project project) {
        return project.getService(LatteProjectSettings.class);
    }
    
    /**
     * Gets the list of custom tags.
     *
     * @return The list of custom tags
     */
    @NotNull
    public List<CustomTag> getCustomTags() {
        return customTags;
    }
    
    /**
     * Sets the list of custom tags.
     *
     * @param customTags The list of custom tags
     */
    public void setCustomTags(@NotNull List<CustomTag> customTags) {
        this.customTags = customTags;
    }
    
    /**
     * Gets the list of custom filters.
     *
     * @return The list of custom filters
     */
    @NotNull
    public List<CustomFilter> getCustomFilters() {
        return customFilters;
    }
    
    /**
     * Sets the list of custom filters.
     *
     * @param customFilters The list of custom filters
     */
    public void setCustomFilters(@NotNull List<CustomFilter> customFilters) {
        this.customFilters = customFilters;
    }
    
    /**
     * Gets the list of custom functions.
     *
     * @return The list of custom functions
     */
    @NotNull
    public List<CustomFunction> getCustomFunctions() {
        return customFunctions;
    }
    
    /**
     * Sets the list of custom functions.
     *
     * @param customFunctions The list of custom functions
     */
    public void setCustomFunctions(@NotNull List<CustomFunction> customFunctions) {
        this.customFunctions = customFunctions;
    }
    
    /**
     * Gets the list of custom variables.
     *
     * @return The list of custom variables
     */
    @NotNull
    public List<CustomVariable> getCustomVariables() {
        return customVariables;
    }
    
    /**
     * Sets the list of custom variables.
     *
     * @param customVariables The list of custom variables
     */
    public void setCustomVariables(@NotNull List<CustomVariable> customVariables) {
        this.customVariables = customVariables;
    }
    
    /**
     * Adds a custom tag.
     *
     * @param tag The tag to add
     */
    public void addCustomTag(@NotNull CustomTag tag) {
        if (!customTags.contains(tag)) {
            customTags.add(tag);
        }
    }
    
    /**
     * Removes a custom tag.
     *
     * @param tag The tag to remove
     */
    public void removeCustomTag(@NotNull CustomTag tag) {
        customTags.remove(tag);
    }
    
    /**
     * Adds a custom filter.
     *
     * @param filter The filter to add
     */
    public void addCustomFilter(@NotNull CustomFilter filter) {
        if (!customFilters.contains(filter)) {
            customFilters.add(filter);
        }
    }
    
    /**
     * Removes a custom filter.
     *
     * @param filter The filter to remove
     */
    public void removeCustomFilter(@NotNull CustomFilter filter) {
        customFilters.remove(filter);
    }
    
    /**
     * Adds a custom function.
     *
     * @param function The function to add
     */
    public void addCustomFunction(@NotNull CustomFunction function) {
        if (!customFunctions.contains(function)) {
            customFunctions.add(function);
        }
    }
    
    /**
     * Removes a custom function.
     *
     * @param function The function to remove
     */
    public void removeCustomFunction(@NotNull CustomFunction function) {
        customFunctions.remove(function);
    }
    
    /**
     * Adds a custom variable.
     *
     * @param variable The variable to add
     */
    public void addCustomVariable(@NotNull CustomVariable variable) {
        if (!customVariables.contains(variable)) {
            customVariables.add(variable);
        }
    }
    
    /**
     * Removes a custom variable.
     *
     * @param variable The variable to remove
     */
    public void removeCustomVariable(@NotNull CustomVariable variable) {
        customVariables.remove(variable);
    }
    
    @Nullable
    @Override
    public LatteProjectSettings getState() {
        return this;
    }
    
    @Override
    public void loadState(@NotNull LatteProjectSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}