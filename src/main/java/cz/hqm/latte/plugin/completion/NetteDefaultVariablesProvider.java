package cz.hqm.latte.plugin.completion;

import com.intellij.openapi.project.Project;
import cz.hqm.latte.plugin.settings.LatteSettings;
import cz.hqm.latte.plugin.version.LatteVersionManager;
import cz.hqm.latte.plugin.version.NettePackageDetector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Provides default variables for Nette packages based on detected versions.
 * Uses caching to improve performance.
 */
public class NetteDefaultVariablesProvider {
    // Cache for variables by project
    private static final Map<Project, List<NetteVariable>> variablesCache = new ConcurrentHashMap<>();
    
    // Cache for settings to detect changes
    private static final AtomicReference<LatteSettings> cachedSettings = new AtomicReference<>();
    
    // Cache for package versions by project
    private static final Map<Project, Map<String, Integer>> versionCache = new ConcurrentHashMap<>();

    /**
     * Gets all default variables for the given project.
     * Uses caching to improve performance.
     *
     * @param project The project to get variables for
     * @return A list of default variables
     */
    public static List<NetteVariable> getAllVariables(Project project) {
        // Check if we need to update the cache
        if (isCacheValid(project)) {
            System.out.println("[DEBUG_LOG] Using cached variables for project: " + project.getName());
            return variablesCache.get(project);
        }
        
        System.out.println("[DEBUG_LOG] Cache invalid or not found, rebuilding variables for project: " + project.getName());
        
        // Cache is invalid or not found, rebuild it
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
        System.out.println("[DEBUG_LOG] All variables (" + variables.size() + "):");
        for (NetteVariable variable : variables) {
            System.out.println("[DEBUG_LOG] - " + variable.getName() + " (" + variable.getType() + ")");
        }
        
        // Update the cache
        updateCache(project, variables);
        
        return variables;
    }
    
    /**
     * Checks if the cache is valid for the given project.
     *
     * @param project The project to check
     * @return True if the cache is valid, false otherwise
     */
    private static synchronized boolean isCacheValid(Project project) {
        // Check if we have a cache for this project
        if (!variablesCache.containsKey(project)) {
            System.out.println("[DEBUG_LOG] No cache found for project: " + project.getName());
            return false;
        }
        
        // Check if settings have changed
        LatteSettings currentSettings = LatteSettings.getInstance();
        LatteSettings cachedSettingsValue = cachedSettings.get();
        
        if (cachedSettingsValue == null || !settingsEqual(cachedSettingsValue, currentSettings)) {
            System.out.println("[DEBUG_LOG] Settings have changed, cache invalid");
            return false;
        }
        
        // Check if versions have changed
        Map<String, Integer> cachedVersions = versionCache.get(project);
        if (cachedVersions == null) {
            System.out.println("[DEBUG_LOG] No version cache found, cache invalid");
            return false;
        }
        
        // Check each package version
        if (isNetteApplicationEnabled() && 
            !versionsEqual(cachedVersions, NettePackageDetector.NETTE_APPLICATION, getNetteApplicationVersion(project))) {
            System.out.println("[DEBUG_LOG] Nette Application version changed, cache invalid");
            return false;
        }
        
        if (isNetteFormsEnabled() && 
            !versionsEqual(cachedVersions, NettePackageDetector.NETTE_FORMS, getNetteFormsVersion(project))) {
            System.out.println("[DEBUG_LOG] Nette Forms version changed, cache invalid");
            return false;
        }
        
        if (isNetteAssetsEnabled() && 
            !versionsEqual(cachedVersions, NettePackageDetector.NETTE_ASSETS, getNetteAssetsVersion(project))) {
            System.out.println("[DEBUG_LOG] Nette Assets version changed, cache invalid");
            return false;
        }
        
        if (isNetteDatabaseEnabled() && 
            !versionsEqual(cachedVersions, NettePackageDetector.NETTE_DATABASE, getNetteDatabaseVersion(project))) {
            System.out.println("[DEBUG_LOG] Nette Database version changed, cache invalid");
            return false;
        }
        
        if (isNetteSecurityEnabled() && 
            !versionsEqual(cachedVersions, NettePackageDetector.NETTE_SECURITY, getNetteSecurityVersion(project))) {
            System.out.println("[DEBUG_LOG] Nette Security version changed, cache invalid");
            return false;
        }
        
        if (isNetteHttpEnabled() && 
            !versionsEqual(cachedVersions, NettePackageDetector.NETTE_HTTP, getNetteHttpVersion(project))) {
            System.out.println("[DEBUG_LOG] Nette HTTP version changed, cache invalid");
            return false;
        }
        
        if (isNetteMailEnabled() && 
            !versionsEqual(cachedVersions, NettePackageDetector.NETTE_MAIL, getNetteMailVersion(project))) {
            System.out.println("[DEBUG_LOG] Nette Mail version changed, cache invalid");
            return false;
        }
        
        // Cache is valid
        return true;
    }
    
    /**
     * Updates the cache for the given project.
     *
     * @param project The project to update the cache for
     * @param variables The variables to cache
     */
    private static synchronized void updateCache(Project project, List<NetteVariable> variables) {
        System.out.println("[DEBUG_LOG] Updating cache for project: " + project.getName());
        
        // Update variables cache
        variablesCache.put(project, new ArrayList<>(variables));
        
        // Update settings cache
        cachedSettings.set(LatteSettings.getInstance());
        
        // Update version cache
        Map<String, Integer> versions = new HashMap<>();
        
        if (isNetteApplicationEnabled()) {
            versions.put(NettePackageDetector.NETTE_APPLICATION, getNetteApplicationVersion(project));
        }
        
        if (isNetteFormsEnabled()) {
            versions.put(NettePackageDetector.NETTE_FORMS, getNetteFormsVersion(project));
        }
        
        if (isNetteAssetsEnabled()) {
            versions.put(NettePackageDetector.NETTE_ASSETS, getNetteAssetsVersion(project));
        }
        
        if (isNetteDatabaseEnabled()) {
            versions.put(NettePackageDetector.NETTE_DATABASE, getNetteDatabaseVersion(project));
        }
        
        if (isNetteSecurityEnabled()) {
            versions.put(NettePackageDetector.NETTE_SECURITY, getNetteSecurityVersion(project));
        }
        
        if (isNetteHttpEnabled()) {
            versions.put(NettePackageDetector.NETTE_HTTP, getNetteHttpVersion(project));
        }
        
        if (isNetteMailEnabled()) {
            versions.put(NettePackageDetector.NETTE_MAIL, getNetteMailVersion(project));
        }
        
        versionCache.put(project, versions);
        
        System.out.println("[DEBUG_LOG] Cache updated for project: " + project.getName());
    }
    
    /**
     * Compares two settings instances to check if they are equal.
     *
     * @param settings1 The first settings instance
     * @param settings2 The second settings instance
     * @return True if the settings are equal, false otherwise
     */
    private static boolean settingsEqual(LatteSettings settings1, LatteSettings settings2) {
        return settings1.isEnableNetteApplication() == settings2.isEnableNetteApplication()
            && settings1.isEnableNetteForms() == settings2.isEnableNetteForms()
            && settings1.isEnableNetteAssets() == settings2.isEnableNetteAssets()
            && settings1.isEnableNetteDatabase() == settings2.isEnableNetteDatabase()
            && settings1.isEnableNetteSecurity() == settings2.isEnableNetteSecurity()
            && settings1.isEnableNetteHttp() == settings2.isEnableNetteHttp()
            && settings1.isEnableNetteMail() == settings2.isEnableNetteMail();
    }
    
    /**
     * Compares two versions to check if they are equal.
     *
     * @param cachedVersions The cached versions
     * @param packageName The package name
     * @param currentVersion The current version
     * @return True if the versions are equal, false otherwise
     */
    private static boolean versionsEqual(Map<String, Integer> cachedVersions, String packageName, int currentVersion) {
        Integer cachedVersion = cachedVersions.get(packageName);
        return cachedVersion != null && cachedVersion == currentVersion;
    }
    
    /**
     * Invalidates the cache for all projects.
     * This should be called when settings change.
     */
    public static synchronized void invalidateCache() {
        System.out.println("[DEBUG_LOG] Invalidating all caches");
        variablesCache.clear();
        versionCache.clear();
        cachedSettings.set(null);
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
        boolean enabled = LatteSettings.getInstance().isEnableNetteHttp();
        System.out.println("[DEBUG_LOG] NetteDefaultVariablesProvider.isNetteHttpEnabled() = " + enabled);
        return enabled;
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
        
        // Double-check that Nette HTTP is enabled
        if (!isNetteHttpEnabled()) {
            System.out.println("[DEBUG_LOG] getNetteHttpVariables: Nette HTTP is disabled, returning empty list");
            return variables;
        }
        
        System.out.println("[DEBUG_LOG] getNetteHttpVariables: Nette HTTP is enabled, adding variables");
        
        // Get the version of Nette HTTP
        int version = getNetteHttpVersion(project);
        System.out.println("[DEBUG_LOG] getNetteHttpVariables: Nette HTTP version = " + version);
        
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
            System.out.println("[DEBUG_LOG] getNetteHttpVariables: Adding Nette HTTP 3.x specific variables");
            variables.add(new NetteVariable("requestFactory", "Nette\\Http\\RequestFactory", "HTTP request factory"));
        } else {
            // Nette HTTP 2.x specific variables
            System.out.println("[DEBUG_LOG] getNetteHttpVariables: No specific variables for Nette HTTP 2.x");
            // No specific variables for version 2.x
        }
        
        System.out.println("[DEBUG_LOG] getNetteHttpVariables: Returning " + variables.size() + " variables");
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