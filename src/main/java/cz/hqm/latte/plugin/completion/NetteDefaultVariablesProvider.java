package cz.hqm.latte.plugin.completion;

import com.intellij.openapi.project.Project;
import cz.hqm.latte.plugin.settings.LatteSettings;
import cz.hqm.latte.plugin.version.LatteVersionManager;
import cz.hqm.latte.plugin.version.NettePackageDetector;

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
        
        // Add variables from Nette Database
        if (isNetteDatabaseEnabled()) {
            variables.addAll(getNetteDatabaseVariables(project));
        }
        
        // Add variables from Nette Security
        if (isNetteSecurityEnabled()) {
            variables.addAll(getNetteSecurityVariables(project));
        }
        
        // Add variables from Nette HTTP
        if (isNetteHttpEnabled()) {
            variables.addAll(getNetteHttpVariables(project));
        }
        
        // Add variables from Nette Mail
        if (isNetteMailEnabled()) {
            variables.addAll(getNetteMailVariables(project));
        }
        
        // Log all variables for debugging
        System.out.println("[DEBUG_LOG] All variables:");
        for (NetteVariable variable : variables) {
            System.out.println("[DEBUG_LOG] - " + variable.getName() + " (" + variable.getType() + ")");
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
     * Gets default variables for Nette Database.
     *
     * @param project The project to get variables for
     * @return A list of default variables
     */
    public static List<NetteVariable> getNetteDatabaseVariables(Project project) {
        List<NetteVariable> variables = new ArrayList<>();
        
        // Get the version of Nette Database
        int version = getNetteDatabaseVersion(project);
        System.out.println("[DEBUG_LOG] Nette Database version: " + version);
        
        // Add common variables (available in all versions)
        System.out.println("[DEBUG_LOG] Adding common database variables");
        variables.add(new NetteVariable("database", "Nette\\Database\\Connection", "Database connection object"));
        variables.add(new NetteVariable("db", "Nette\\Database\\Connection", "Alias for database connection object"));
        variables.add(new NetteVariable("row", "Nette\\Database\\Row", "Current database row in foreach loops"));
        
        // Add version-specific variables
        if (version >= 3) {
            // Nette Database 3.x specific variables
            System.out.println("[DEBUG_LOG] Adding Nette Database 3.x specific variables");
            variables.add(new NetteVariable("explorer", "Nette\\Database\\Explorer", "Database explorer object"));
        } else {
            // Nette Database 2.x specific variables
            System.out.println("[DEBUG_LOG] Adding Nette Database 2.x specific variables");
            variables.add(new NetteVariable("context", "Nette\\Database\\Context", "Database context object"));
        }
        
        // Log all variables for debugging
        System.out.println("[DEBUG_LOG] Database variables:");
        for (NetteVariable variable : variables) {
            System.out.println("[DEBUG_LOG] - " + variable.getName() + " (" + variable.getType() + ")");
        }
        
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
     * Checks if Nette Database support is enabled.
     *
     * @return True if Nette Database support is enabled, false otherwise
     */
    private static boolean isNetteDatabaseEnabled() {
        return LatteSettings.getInstance().isEnableNetteDatabase();
    }
    
    /**
     * Checks if Nette Security support is enabled.
     *
     * @return True if Nette Security support is enabled, false otherwise
     */
    private static boolean isNetteSecurityEnabled() {
        return LatteSettings.getInstance().isEnableNetteSecurity();
    }
    
    /**
     * Checks if Nette HTTP support is enabled.
     *
     * @return True if Nette HTTP support is enabled, false otherwise
     */
    private static boolean isNetteHttpEnabled() {
        return LatteSettings.getInstance().isEnableNetteHttp();
    }
    
    /**
     * Checks if Nette Mail support is enabled.
     *
     * @return True if Nette Mail support is enabled, false otherwise
     */
    private static boolean isNetteMailEnabled() {
        return LatteSettings.getInstance().isEnableNetteMail();
    }
    
    /**
     * Gets default variables for Nette HTTP.
     *
     * @param project The project to get variables for
     * @return A list of default variables
     */
    public static List<NetteVariable> getNetteHttpVariables(Project project) {
        List<NetteVariable> variables = new ArrayList<>();
        
        // Get the version of Nette HTTP
        int version = getNetteHttpVersion(project);
        
        // Add common variables (available in all versions)
        variables.add(new NetteVariable("httpRequest", "Nette\\Http\\Request", "HTTP request object"));
        variables.add(new NetteVariable("httpResponse", "Nette\\Http\\Response", "HTTP response object"));
        variables.add(new NetteVariable("session", "Nette\\Http\\Session", "Session object"));
        variables.add(new NetteVariable("url", "Nette\\Http\\Url", "Current URL object"));
        variables.add(new NetteVariable("cookies", "array", "HTTP cookies"));
        variables.add(new NetteVariable("headers", "array", "HTTP headers"));
        
        // Add version-specific variables
        if (version >= 3) {
            // Nette HTTP 3.x specific variables
            variables.add(new NetteVariable("requestFactory", "Nette\\Http\\RequestFactory", "HTTP request factory"));
        } else {
            // Nette HTTP 2.x specific variables
            // No specific variables for version 2.x
        }
        
        return variables;
    }
    
    /**
     * Gets default variables for Nette Security.
     *
     * @param project The project to get variables for
     * @return A list of default variables
     */
    public static List<NetteVariable> getNetteSecurityVariables(Project project) {
        List<NetteVariable> variables = new ArrayList<>();
        
        // Get the version of Nette Security
        int version = getNetteSecurityVersion(project);
        
        // Add common variables (available in all versions)
        variables.add(new NetteVariable("user", "Nette\\Security\\User", "User authentication and authorization"));
        variables.add(new NetteVariable("identity", "Nette\\Security\\IIdentity", "User identity"));
        variables.add(new NetteVariable("roles", "array", "User roles"));
        
        // Add version-specific variables
        if (version >= 3) {
            // Nette Security 3.x specific variables
            variables.add(new NetteVariable("authenticator", "Nette\\Security\\Authenticator", "User authentication service"));
            variables.add(new NetteVariable("authorizator", "Nette\\Security\\Authorizator", "User authorization service"));
        } else {
            // Nette Security 2.x specific variables
            variables.add(new NetteVariable("authenticator", "Nette\\Security\\IAuthenticator", "User authentication service"));
            variables.add(new NetteVariable("authorizator", "Nette\\Security\\IAuthorizator", "User authorization service"));
        }
        
        return variables;
    }
    
    /**
     * Gets default variables for Nette Mail.
     *
     * @param project The project to get variables for
     * @return A list of default variables
     */
    public static List<NetteVariable> getNetteMailVariables(Project project) {
        List<NetteVariable> variables = new ArrayList<>();
        
        // Get the version of Nette Mail
        int version = getNetteMailVersion(project);
        
        // Add common variables (available in all versions)
        variables.add(new NetteVariable("mail", "Nette\\Mail\\Message", "Mail message object"));
        variables.add(new NetteVariable("message", "Nette\\Mail\\Message", "Mail message object"));
        variables.add(new NetteVariable("attachment", "Nette\\Mail\\MimePart", "Mail attachment"));
        variables.add(new NetteVariable("sender", "Nette\\Mail\\SendmailMailer", "Mail sender service"));
        variables.add(new NetteVariable("mailer", "Nette\\Mail\\Mailer", "Mail sender service"));
        
        // Add version-specific variables
        if (version >= 3) {
            // Nette Mail 3.x specific variables
            variables.add(new NetteVariable("mailFactory", "Nette\\Mail\\MailFactory", "Mail factory service"));
        } else {
            // Nette Mail 2.x specific variables
            // No specific variables for version 2.x
        }
        
        return variables;
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
     * Gets the version of Nette Database.
     *
     * @param project The project to get the version for
     * @return The major version number
     */
    private static int getNetteDatabaseVersion(Project project) {
        LatteSettings settings = LatteSettings.getInstance();
        
        // If override is enabled, use the selected version
        if (settings.isOverrideDetectedNetteDatabaseVersion() && settings.getSelectedNetteDatabaseVersion() != null) {
            try {
                return Integer.parseInt(settings.getSelectedNetteDatabaseVersion());
            } catch (NumberFormatException e) {
                // Ignore and use detected version
            }
        }
        
        // Otherwise, use the detected version
        return NettePackageDetector.getPackageVersion(project, NettePackageDetector.NETTE_DATABASE);
    }
    
    /**
     * Gets the version of Nette Security.
     *
     * @param project The project to get the version for
     * @return The major version number
     */
    private static int getNetteSecurityVersion(Project project) {
        LatteSettings settings = LatteSettings.getInstance();
        
        // If override is enabled, use the selected version
        if (settings.isOverrideDetectedNetteSecurityVersion() && settings.getSelectedNetteSecurityVersion() != null) {
            try {
                return Integer.parseInt(settings.getSelectedNetteSecurityVersion());
            } catch (NumberFormatException e) {
                // Ignore and use detected version
            }
        }
        
        // Otherwise, use the detected version
        return NettePackageDetector.getPackageVersion(project, NettePackageDetector.NETTE_SECURITY);
    }
    
    /**
     * Gets the version of Nette HTTP.
     *
     * @param project The project to get the version for
     * @return The major version number
     */
    private static int getNetteHttpVersion(Project project) {
        LatteSettings settings = LatteSettings.getInstance();
        
        // If override is enabled, use the selected version
        if (settings.isOverrideDetectedNetteHttpVersion() && settings.getSelectedNetteHttpVersion() != null) {
            try {
                return Integer.parseInt(settings.getSelectedNetteHttpVersion());
            } catch (NumberFormatException e) {
                // Ignore and use detected version
            }
        }
        
        // Otherwise, use the detected version
        return NettePackageDetector.getPackageVersion(project, NettePackageDetector.NETTE_HTTP);
    }
    
    /**
     * Gets the version of Nette Mail.
     *
     * @param project The project to get the version for
     * @return The major version number
     */
    private static int getNetteMailVersion(Project project) {
        LatteSettings settings = LatteSettings.getInstance();
        
        // If override is enabled, use the selected version
        if (settings.isOverrideDetectedNetteMailVersion() && settings.getSelectedNetteMailVersion() != null) {
            try {
                return Integer.parseInt(settings.getSelectedNetteMailVersion());
            } catch (NumberFormatException e) {
                // Ignore and use detected version
            }
        }
        
        // Otherwise, use the detected version
        return NettePackageDetector.getPackageVersion(project, NettePackageDetector.NETTE_MAIL);
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