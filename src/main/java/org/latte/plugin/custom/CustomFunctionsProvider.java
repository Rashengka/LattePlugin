package org.latte.plugin.custom;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.latte.plugin.settings.LatteProjectSettings;

import java.util.HashSet;
import java.util.Set;

/**
 * Provides custom Latte functions from project settings.
 */
public class CustomFunctionsProvider {
    
    /**
     * Gets all custom functions for the specified project.
     *
     * @param project The project
     * @return A set of custom functions
     */
    @NotNull
    public static Set<CustomFunction> getAllFunctions(@NotNull Project project) {
        LatteProjectSettings settings = LatteProjectSettings.getInstance(project);
        return new HashSet<>(settings.getCustomFunctions());
    }
    
    /**
     * Gets all custom function names for the specified project.
     *
     * @param project The project
     * @return A set of custom function names
     */
    @NotNull
    public static Set<String> getAllFunctionNames(@NotNull Project project) {
        Set<String> functionNames = new HashSet<>();
        for (CustomFunction function : getAllFunctions(project)) {
            functionNames.add(function.getName());
        }
        return functionNames;
    }
    
    /**
     * Checks if a function with the specified name exists.
     *
     * @param project The project
     * @param name The function name
     * @return True if the function exists, false otherwise
     */
    public static boolean functionExists(@NotNull Project project, @NotNull String name) {
        return getAllFunctionNames(project).contains(name);
    }
    
    /**
     * Gets a function by name.
     *
     * @param project The project
     * @param name The function name
     * @return The function or null if not found
     */
    public static CustomFunction getFunctionByName(@NotNull Project project, @NotNull String name) {
        for (CustomFunction function : getAllFunctions(project)) {
            if (function.getName().equals(name)) {
                return function;
            }
        }
        return null;
    }
    
    /**
     * Adds a new custom function.
     *
     * @param project The project
     * @param name The function name
     * @param description The function description
     * @return The added function
     */
    @NotNull
    public static CustomFunction addFunction(@NotNull Project project, @NotNull String name, String description) {
        LatteProjectSettings settings = LatteProjectSettings.getInstance(project);
        CustomFunction function = new CustomFunction(name, description);
        settings.addCustomFunction(function);
        return function;
    }
    
    /**
     * Removes a custom function.
     *
     * @param project The project
     * @param name The function name
     * @return True if the function was removed, false otherwise
     */
    public static boolean removeFunction(@NotNull Project project, @NotNull String name) {
        LatteProjectSettings settings = LatteProjectSettings.getInstance(project);
        CustomFunction function = getFunctionByName(project, name);
        if (function != null) {
            settings.removeCustomFunction(function);
            return true;
        }
        return false;
    }
}