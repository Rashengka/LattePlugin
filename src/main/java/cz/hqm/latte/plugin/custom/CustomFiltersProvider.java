package cz.hqm.latte.plugin.custom;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import cz.hqm.latte.plugin.settings.LatteProjectSettings;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides custom Latte filters from project settings.
 * Uses caching to improve performance.
 */
public class CustomFiltersProvider {
    // Cache for custom filters by project
    private static final Map<Project, Set<CustomFilter>> filtersCache = new ConcurrentHashMap<>();
    
    // Cache for custom filter names by project
    private static final Map<Project, Set<String>> filterNamesCache = new ConcurrentHashMap<>();
    
    /**
     * Initializes or updates the cache for the specified project.
     *
     * @param project The project
     */
    private static synchronized void updateCache(@NotNull Project project) {
        System.out.println("[DEBUG_LOG] Updating custom filters cache for project: " + project.getName());
        
        // Get custom filters from project settings
        LatteProjectSettings settings = LatteProjectSettings.getInstance(project);
        Set<CustomFilter> filters = new HashSet<>(settings.getCustomFilters());
        
        // Create filter names set
        Set<String> filterNames = new HashSet<>();
        for (CustomFilter filter : filters) {
            filterNames.add(filter.getName());
        }
        
        // Update caches
        filtersCache.put(project, filters);
        filterNamesCache.put(project, filterNames);
        
        System.out.println("[DEBUG_LOG] Custom filters cache updated with " + filters.size() + " filters");
    }
    
    /**
     * Invalidates the cache for the specified project.
     * This should be called when project settings change.
     *
     * @param project The project
     */
    public static synchronized void invalidateCache(@NotNull Project project) {
        System.out.println("[DEBUG_LOG] Invalidating custom filters cache for project: " + project.getName());
        filtersCache.remove(project);
        filterNamesCache.remove(project);
    }
    
    /**
     * Invalidates all caches.
     * This should be called when the plugin is unloaded.
     */
    public static synchronized void invalidateAllCaches() {
        System.out.println("[DEBUG_LOG] Invalidating all custom filters caches");
        filtersCache.clear();
        filterNamesCache.clear();
    }
    
    /**
     * Gets all custom filters for the specified project.
     * Uses caching to improve performance.
     *
     * @param project The project
     * @return A set of custom filters
     */
    @NotNull
    public static Set<CustomFilter> getAllFilters(@NotNull Project project) {
        // Check if we have a cache for this project
        if (!filtersCache.containsKey(project)) {
            updateCache(project);
        }
        
        // Return cached filters
        return new HashSet<>(filtersCache.get(project));
    }
    
    /**
     * Gets all custom filter names for the specified project.
     * Uses caching to improve performance.
     *
     * @param project The project
     * @return A set of custom filter names
     */
    @NotNull
    public static Set<String> getAllFilterNames(@NotNull Project project) {
        // Check if we have a cache for this project
        if (!filterNamesCache.containsKey(project)) {
            updateCache(project);
        }
        
        // Return cached filter names
        return new HashSet<>(filterNamesCache.get(project));
    }
    
    /**
     * Checks if a filter with the specified name exists.
     * Uses caching to improve performance.
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
     * Uses caching to improve performance.
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
        
        // Invalidate cache for this project
        invalidateCache(project);
        
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
            
            // Invalidate cache for this project
            invalidateCache(project);
            
            return true;
        }
        return false;
    }
}