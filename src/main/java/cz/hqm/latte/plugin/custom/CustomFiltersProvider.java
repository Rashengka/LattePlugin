package cz.hqm.latte.plugin.custom;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import cz.hqm.latte.plugin.settings.LatteProjectSettings;

import java.util.HashSet;
import java.util.Set;

/**
 * Provides custom Latte filters from project settings.
 */
public class CustomFiltersProvider {
    
    /**
     * Gets all custom filters for the specified project.
     *
     * @param project The project
     * @return A set of custom filters
     */
    @NotNull
    public static Set<CustomFilter> getAllFilters(@NotNull Project project) {
        LatteProjectSettings settings = LatteProjectSettings.getInstance(project);
        return new HashSet<>(settings.getCustomFilters());
    }
    
    /**
     * Gets all custom filter names for the specified project.
     *
     * @param project The project
     * @return A set of custom filter names
     */
    @NotNull
    public static Set<String> getAllFilterNames(@NotNull Project project) {
        Set<String> filterNames = new HashSet<>();
        for (CustomFilter filter : getAllFilters(project)) {
            filterNames.add(filter.getName());
        }
        return filterNames;
    }
    
    /**
     * Checks if a filter with the specified name exists.
     *
     * @param project The project
     * @param name The filter name
     * @return True if the filter exists, false otherwise
     */
    public static boolean filterExists(@NotNull Project project, @NotNull String name) {
        return getAllFilterNames(project).contains(name);
    }
    
    /**
     * Gets a filter by name.
     *
     * @param project The project
     * @param name The filter name
     * @return The filter or null if not found
     */
    public static CustomFilter getFilterByName(@NotNull Project project, @NotNull String name) {
        for (CustomFilter filter : getAllFilters(project)) {
            if (filter.getName().equals(name)) {
                return filter;
            }
        }
        return null;
    }
    
    /**
     * Adds a new custom filter.
     *
     * @param project The project
     * @param name The filter name
     * @param description The filter description
     * @return The added filter
     */
    @NotNull
    public static CustomFilter addFilter(@NotNull Project project, @NotNull String name, String description) {
        LatteProjectSettings settings = LatteProjectSettings.getInstance(project);
        CustomFilter filter = new CustomFilter(name, description);
        settings.addCustomFilter(filter);
        return filter;
    }
    
    /**
     * Removes a custom filter.
     *
     * @param project The project
     * @param name The filter name
     * @return True if the filter was removed, false otherwise
     */
    public static boolean removeFilter(@NotNull Project project, @NotNull String name) {
        LatteProjectSettings settings = LatteProjectSettings.getInstance(project);
        CustomFilter filter = getFilterByName(project, name);
        if (filter != null) {
            settings.removeCustomFilter(filter);
            return true;
        }
        return false;
    }
}