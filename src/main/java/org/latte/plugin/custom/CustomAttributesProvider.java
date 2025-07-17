package org.latte.plugin.custom;

import com.intellij.openapi.project.Project;
import org.latte.plugin.settings.LatteProjectSettings;

import java.util.Collections;
import java.util.List;

/**
 * Provides custom n:attributes defined by the user.
 * This class is responsible for storing and providing custom attributes for a project.
 */
public class CustomAttributesProvider {

    /**
     * Gets all custom attributes for the given project.
     *
     * @param project The project to get attributes for
     * @return A list of custom attributes
     */
    public static List<CustomAttribute> getAllAttributes(Project project) {
        if (project == null) {
            return Collections.emptyList();
        }
        
        LatteProjectSettings settings = LatteProjectSettings.getInstance(project);
        if (settings == null) {
            return Collections.emptyList();
        }
        
        // Return the list of custom attributes from project settings
        return settings.getCustomAttributes();
    }
    
    /**
     * Adds a custom attribute to the project settings.
     *
     * @param project The project to add the attribute to
     * @param attribute The attribute to add
     * @return True if the attribute was added, false if it already exists
     */
    public static boolean addAttribute(Project project, CustomAttribute attribute) {
        if (project == null || attribute == null) {
            return false;
        }
        
        LatteProjectSettings settings = LatteProjectSettings.getInstance(project);
        if (settings == null) {
            return false;
        }
        
        // Check if the attribute already exists
        if (settings.getCustomAttributes().contains(attribute)) {
            return false;
        }
        
        // Add the attribute to the settings
        settings.addCustomAttribute(attribute);
        return true;
    }
    
    /**
     * Removes a custom attribute from the project settings.
     *
     * @param project The project to remove the attribute from
     * @param attributeName The name of the attribute to remove
     * @return True if the attribute was removed, false if it doesn't exist
     */
    public static boolean removeAttribute(Project project, String attributeName) {
        if (project == null || attributeName == null) {
            return false;
        }
        
        LatteProjectSettings settings = LatteProjectSettings.getInstance(project);
        if (settings == null) {
            return false;
        }
        
        // Find the attribute with the given name
        for (CustomAttribute attribute : settings.getCustomAttributes()) {
            if (attribute.getName().equals(attributeName)) {
                // Remove the attribute from the settings
                settings.removeCustomAttribute(attribute);
                return true;
            }
        }
        
        // Attribute not found
        return false;
    }
}