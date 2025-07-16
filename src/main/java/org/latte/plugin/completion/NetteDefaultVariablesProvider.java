package org.latte.plugin.completion;

import com.intellij.openapi.project.Project;
import org.latte.plugin.settings.LatteSettings;
import org.latte.plugin.version.LatteVersionManager;
import org.latte.plugin.version.NettePackageDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides default variables for Nette packages based on detected versions.
 */
public class NetteDefaultVariablesProvider {

    /**
     * Gets all default variables for the given project.
     *
     * @param project The project to get variables for
     * @return A list of default variables
     */
    public static List<NetteVariable> getAllVariables(Project project) {
        List<NetteVariable> variables = new ArrayList<>();
        
        // Add variables from Nette Application
        if (isNetteApplicationEnabled()) {
            variables.addAll(getNetteApplicationVariables(project));
        }
        
        // Add variables from Nette Forms
        if (isNetteFormsEnabled()) {
            variables.addAll(getNetteFormsVariables(project));
        }
        
        // Add variables from Nette Assets
        if (isNetteAssetsEnabled()) {
            variables.addAll(getNetteAssetsVariables(project));
        }
        
        return variables;
    }
    
    /**
     * Gets default variables for Nette Application.
     *
     * @param project The project to get variables for
     * @return A list of default variables
     */
    public static List<NetteVariable> getNetteApplicationVariables(Project project) {
        List<NetteVariable> variables = new ArrayList<>();
        
        // Get the version of Nette Application
        int version = getNetteApplicationVersion(project);
        
        // Add common variables (available in all versions)
        variables.add(new NetteVariable("basePath", "string", "Absolute URL path to the root directory"));
        variables.add(new NetteVariable("baseUrl", "string", "Absolute URL to the root directory"));
        variables.add(new NetteVariable("user", "Nette\\Security\\User", "Object representing the user"));
        variables.add(new NetteVariable("presenter", "Nette\\Application\\UI\\Presenter", "Current presenter"));
        variables.add(new NetteVariable("control", "Nette\\Application\\UI\\Control", "Current component or presenter"));
        variables.add(new NetteVariable("flashes", "array", "Array of messages sent by flashMessage()"));
        
        // Add version-specific variables
        if (version >= 4) {
            // Nette Application 4.x specific variables
            // No additional variables for now
        } else if (version >= 3) {
            // Nette Application 3.x specific variables
            // No additional variables for now
        } else {
            // Nette Application 2.x specific variables
            // No additional variables for now
        }
        
        return variables;
    }
    
    /**
     * Gets default variables for Nette Forms.
     *
     * @param project The project to get variables for
     * @return A list of default variables
     */
    public static List<NetteVariable> getNetteFormsVariables(Project project) {
        List<NetteVariable> variables = new ArrayList<>();
        
        // Get the version of Nette Forms
        int version = getNetteFormsVersion(project);
        
        // Add common variables (available in all versions)
        variables.add(new NetteVariable("form", "Nette\\Forms\\Form", "Form object (created by <form n:name> tag or {form} ... {/form} pair)"));
        
        // Add version-specific variables
        if (version >= 4) {
            // Nette Forms 4.x specific variables
            // No additional variables for now
        } else if (version >= 3) {
            // Nette Forms 3.x specific variables
            // No additional variables for now
        } else {
            // Nette Forms 2.x specific variables
            // No additional variables for now
        }
        
        return variables;
    }
    
    /**
     * Gets default variables for Nette Assets.
     *
     * @param project The project to get variables for
     * @return A list of default variables
     */
    public static List<NetteVariable> getNetteAssetsVariables(Project project) {
        List<NetteVariable> variables = new ArrayList<>();
        
        // Get the version of Nette Assets
        int version = getNetteAssetsVersion(project);
        
        // Add common variables (available in all versions)
        // No default variables for Nette Assets
        
        return variables;
    }
    
    /**
     * Checks if Nette Application support is enabled.
     *
     * @return True if Nette Application support is enabled, false otherwise
     */
    private static boolean isNetteApplicationEnabled() {
        return LatteSettings.getInstance().isEnableNetteApplication();
    }
    
    /**
     * Checks if Nette Forms support is enabled.
     *
     * @return True if Nette Forms support is enabled, false otherwise
     */
    private static boolean isNetteFormsEnabled() {
        return LatteSettings.getInstance().isEnableNetteForms();
    }
    
    /**
     * Checks if Nette Assets support is enabled.
     *
     * @return True if Nette Assets support is enabled, false otherwise
     */
    private static boolean isNetteAssetsEnabled() {
        return LatteSettings.getInstance().isEnableNetteAssets();
    }
    
    /**
     * Gets the version of Nette Application.
     *
     * @param project The project to get the version for
     * @return The major version number
     */
    private static int getNetteApplicationVersion(Project project) {
        LatteSettings settings = LatteSettings.getInstance();
        
        // If override is enabled, use the selected version
        if (settings.isOverrideDetectedNetteApplicationVersion() && settings.getSelectedNetteApplicationVersion() != null) {
            try {
                return Integer.parseInt(settings.getSelectedNetteApplicationVersion());
            } catch (NumberFormatException e) {
                // Ignore and use detected version
            }
        }
        
        // Otherwise, use the detected version
        return NettePackageDetector.getPackageVersion(project, NettePackageDetector.NETTE_APPLICATION);
    }
    
    /**
     * Gets the version of Nette Forms.
     *
     * @param project The project to get the version for
     * @return The major version number
     */
    private static int getNetteFormsVersion(Project project) {
        LatteSettings settings = LatteSettings.getInstance();
        
        // If override is enabled, use the selected version
        if (settings.isOverrideDetectedNetteFormsVersion() && settings.getSelectedNetteFormsVersion() != null) {
            try {
                return Integer.parseInt(settings.getSelectedNetteFormsVersion());
            } catch (NumberFormatException e) {
                // Ignore and use detected version
            }
        }
        
        // Otherwise, use the detected version
        return NettePackageDetector.getPackageVersion(project, NettePackageDetector.NETTE_FORMS);
    }
    
    /**
     * Gets the version of Nette Assets.
     *
     * @param project The project to get the version for
     * @return The major version number
     */
    private static int getNetteAssetsVersion(Project project) {
        LatteSettings settings = LatteSettings.getInstance();
        
        // If override is enabled, use the selected version
        if (settings.isOverrideDetectedNetteAssetsVersion() && settings.getSelectedNetteAssetsVersion() != null) {
            try {
                return Integer.parseInt(settings.getSelectedNetteAssetsVersion());
            } catch (NumberFormatException e) {
                // Ignore and use detected version
            }
        }
        
        // Otherwise, use the detected version
        return NettePackageDetector.getPackageVersion(project, NettePackageDetector.NETTE_ASSETS);
    }
    
    /**
     * Class representing a Nette variable.
     */
    public static class NetteVariable {
        private final String name;
        private final String type;
        private final String description;
        
        /**
         * Constructor for NetteVariable.
         *
         * @param name The name of the variable
         * @param type The type of the variable
         * @param description The description of the variable
         */
        public NetteVariable(String name, String type, String description) {
            this.name = name;
            this.type = type;
            this.description = description;
        }
        
        /**
         * Gets the name of the variable.
         *
         * @return The name
         */
        public String getName() {
            return name;
        }
        
        /**
         * Gets the type of the variable.
         *
         * @return The type
         */
        public String getType() {
            return type;
        }
        
        /**
         * Gets the description of the variable.
         *
         * @return The description
         */
        public String getDescription() {
            return description;
        }
    }
}