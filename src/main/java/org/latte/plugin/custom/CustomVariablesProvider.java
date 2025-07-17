package org.latte.plugin.custom;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.latte.plugin.settings.LatteProjectSettings;

import java.util.HashSet;
import java.util.Set;

/**
 * Provides custom Latte variables from project settings.
 */
public class CustomVariablesProvider {
    
    /**
     * Gets all custom variables for the specified project.
     *
     * @param project The project
     * @return A set of custom variables
     */
    @NotNull
    public static Set<CustomVariable> getAllVariables(@NotNull Project project) {
        LatteProjectSettings settings = LatteProjectSettings.getInstance(project);
        return new HashSet<>(settings.getCustomVariables());
    }
    
    /**
     * Gets all custom variable names for the specified project.
     *
     * @param project The project
     * @return A set of custom variable names
     */
    @NotNull
    public static Set<String> getAllVariableNames(@NotNull Project project) {
        Set<String> variableNames = new HashSet<>();
        for (CustomVariable variable : getAllVariables(project)) {
            variableNames.add(variable.getName());
        }
        return variableNames;
    }
    
    /**
     * Checks if a variable with the specified name exists.
     *
     * @param project The project
     * @param name The variable name
     * @return True if the variable exists, false otherwise
     */
    public static boolean variableExists(@NotNull Project project, @NotNull String name) {
        return getAllVariableNames(project).contains(name);
    }
    
    /**
     * Gets a variable by name.
     *
     * @param project The project
     * @param name The variable name
     * @return The variable or null if not found
     */
    public static CustomVariable getVariableByName(@NotNull Project project, @NotNull String name) {
        for (CustomVariable variable : getAllVariables(project)) {
            if (variable.getName().equals(name)) {
                return variable;
            }
        }
        return null;
    }
    
    /**
     * Adds a new custom variable.
     *
     * @param project The project
     * @param name The variable name
     * @param type The variable type
     * @param description The variable description
     * @return The added variable
     */
    @NotNull
    public static CustomVariable addVariable(@NotNull Project project, @NotNull String name, String type, String description) {
        LatteProjectSettings settings = LatteProjectSettings.getInstance(project);
        CustomVariable variable = new CustomVariable(name, type, description);
        settings.addCustomVariable(variable);
        return variable;
    }
    
    /**
     * Removes a custom variable.
     *
     * @param project The project
     * @param name The variable name
     * @return True if the variable was removed, false otherwise
     */
    public static boolean removeVariable(@NotNull Project project, @NotNull String name) {
        LatteProjectSettings settings = LatteProjectSettings.getInstance(project);
        CustomVariable variable = getVariableByName(project, name);
        if (variable != null) {
            settings.removeCustomVariable(variable);
            return true;
        }
        return false;
    }
}