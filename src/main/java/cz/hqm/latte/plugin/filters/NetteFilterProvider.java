package cz.hqm.latte.plugin.filters;

import cz.hqm.latte.plugin.settings.LatteSettings;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Provides Latte filters from Nette packages based on enabled settings.
 * This class is responsible for storing and providing filters from different Nette packages.
 * Uses caching to improve performance.
 */
public class NetteFilterProvider {
    // Cache for filter names and filter objects
    private static final AtomicReference<Set<String>> cachedFilterNames = new AtomicReference<>();
    private static final AtomicReference<Set<NetteFilter>> cachedFilters = new AtomicReference<>();
    
    // Cache for settings to detect changes
    private static final AtomicReference<LatteSettings> cachedSettings = new AtomicReference<>();

    // Filters from nette/application package
    private static final Set<NetteFilter> APPLICATION_FILTERS = new HashSet<>(Arrays.asList(
            new NetteFilter("escapeUrl", "Escapes parameter in URL", "nette/application"),
            new NetteFilter("length", "Returns the length of a string or array", "nette/application"),
            new NetteFilter("webalize", "Adjusts string for usage in URL", "nette/application")
    ));

    // Filters from nette/forms package
    private static final Set<NetteFilter> FORMS_FILTERS = new HashSet<>(Arrays.asList(
            new NetteFilter("translate", "Translates a message", "nette/forms"),
            new NetteFilter("required", "Marks a form control as required", "nette/forms")
    ));

    // Filters from nette/assets package
    private static final Set<NetteFilter> ASSETS_FILTERS = new HashSet<>(Arrays.asList(
            new NetteFilter("asset", "Adds version to asset URL", "nette/assets")
    ));
    
    // Filters from nette/database package
    private static final Set<NetteFilter> DATABASE_FILTERS = new HashSet<>(Arrays.asList(
            new NetteFilter("table", "Formats a value as a database table name", "nette/database"),
            new NetteFilter("column", "Formats a value as a database column name", "nette/database"),
            new NetteFilter("value", "Formats a value for use in a database query", "nette/database"),
            new NetteFilter("like", "Escapes a value for use in a LIKE clause", "nette/database")
    ));
    
    // Filters from nette/security package
    private static final Set<NetteFilter> SECURITY_FILTERS = new HashSet<>(Arrays.asList(
            new NetteFilter("isLoggedIn", "Checks if user is logged in", "nette/security"),
            new NetteFilter("isAllowed", "Checks if user is allowed to perform an action", "nette/security"),
            new NetteFilter("hasRole", "Checks if user has a role", "nette/security"),
            new NetteFilter("getRoles", "Gets user roles", "nette/security")
    ));

    // Core Latte filters
    private static final Set<NetteFilter> CORE_FILTERS = new HashSet<>(Arrays.asList(
            new NetteFilter("upper", "Converts a value to uppercase", "latte/core"),
            new NetteFilter("lower", "Converts a value to lowercase", "latte/core"),
            new NetteFilter("firstUpper", "Converts the first character to uppercase", "latte/core"),
            new NetteFilter("capitalize", "Converts the first character of each word to uppercase", "latte/core"),
            new NetteFilter("escape", "Escapes a string for use inside HTML", "latte/core"),
            // Removed "escapeUrl" from core filters as it's part of the application package
            new NetteFilter("noescape", "Prints a variable without escaping", "latte/core"),
            // Added parameters to the following filters
            new NetteFilter("truncate", "Shortens a string to the given maximum length", "latte/core",
                    Arrays.asList(
                            new NetteFilter.FilterParameter("length", "int", "Maximum length", false),
                            new NetteFilter.FilterParameter("append", "string", "String to append if truncated", true)
                    )),
            new NetteFilter("substring", "Returns part of a string", "latte/core",
                    Arrays.asList(
                            new NetteFilter.FilterParameter("start", "int", "Start position", false),
                            new NetteFilter.FilterParameter("length", "int", "Length of substring", true)
                    )),
            new NetteFilter("trim", "Strips whitespace from the beginning and end of a string", "latte/core"),
            new NetteFilter("padLeft", "Pads a string to a certain length with another string from the left", "latte/core",
                    Arrays.asList(
                            new NetteFilter.FilterParameter("length", "int", "Target length", false),
                            new NetteFilter.FilterParameter("padString", "string", "String to pad with", true)
                    )),
            new NetteFilter("padRight", "Pads a string to a certain length with another string from the right", "latte/core",
                    Arrays.asList(
                            new NetteFilter.FilterParameter("length", "int", "Target length", false),
                            new NetteFilter.FilterParameter("padString", "string", "String to pad with", true)
                    )),
            new NetteFilter("replace", "Replaces all occurrences of the search string with the replacement", "latte/core"),
            new NetteFilter("stripHtml", "Removes HTML tags and converts HTML entities to text", "latte/core"),
            new NetteFilter("strip", "Removes HTML tags", "latte/core"),
            new NetteFilter("indent", "Indents a text from the left with specified number of tabs", "latte/core"),
            new NetteFilter("reverse", "Reverses a string or array", "latte/core"),
            new NetteFilter("length", "Returns the length of a string or array", "latte/core"),
            new NetteFilter("date", "Formats a date according to the specified format", "latte/core"),
            new NetteFilter("number", "Formats a number", "latte/core"),
            new NetteFilter("bytes", "Formats a number of bytes", "latte/core"),
            new NetteFilter("percent", "Formats a number as a percentage", "latte/core"),
            new NetteFilter("join", "Joins an array with a string", "latte/core"),
            new NetteFilter("implode", "Joins an array with a string", "latte/core"),
            new NetteFilter("explode", "Splits a string by a string", "latte/core"),
            new NetteFilter("sort", "Sorts an array", "latte/core"),
            new NetteFilter("default", "Returns the value if it's not empty, otherwise returns a default value", "latte/core"),
            new NetteFilter("checkEmpty", "Checks if a value is empty", "latte/core")
    ));

    /**
     * Checks if the cache needs to be updated based on current settings.
     * 
     * @return True if the cache is valid, false if it needs to be updated
     */
    private static synchronized boolean isCacheValid() {
        // Check if we have a cache
        if (cachedFilterNames.get() == null || cachedFilters.get() == null) {
            System.out.println("[DEBUG_LOG] Filter cache not initialized");
            return false;
        }
        
        // Check if settings have changed
        LatteSettings currentSettings = LatteSettings.getInstance();
        LatteSettings cachedSettingsValue = cachedSettings.get();
        
        if (cachedSettingsValue == null || !settingsEqual(cachedSettingsValue, currentSettings)) {
            System.out.println("[DEBUG_LOG] Filter settings have changed, cache invalid");
            return false;
        }
        
        // Cache is valid
        return true;
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
            && settings1.isEnableNetteSecurity() == settings2.isEnableNetteSecurity();
    }
    
    /**
     * Initializes the cache with current settings.
     */
    private static synchronized void initCache() {
        System.out.println("[DEBUG_LOG] Initializing filter cache");
        
        // Get current settings
        LatteSettings settings = LatteSettings.getInstance();
        
        // Create filter names set
        Set<String> filterNames = new HashSet<>();
        
        // Create filters set
        Set<NetteFilter> filters = new HashSet<>();
        
        // Add core filters
        for (NetteFilter filter : CORE_FILTERS) {
            filterNames.add(filter.getName());
            filters.add(filter);
        }
        
        // Add filters based on enabled settings
        if (settings.isEnableNetteApplication()) {
            for (NetteFilter filter : APPLICATION_FILTERS) {
                filterNames.add(filter.getName());
                filters.add(filter);
            }
        }
        
        if (settings.isEnableNetteForms()) {
            for (NetteFilter filter : FORMS_FILTERS) {
                filterNames.add(filter.getName());
                filters.add(filter);
            }
        }
        
        if (settings.isEnableNetteAssets()) {
            for (NetteFilter filter : ASSETS_FILTERS) {
                filterNames.add(filter.getName());
                filters.add(filter);
            }
        }
        
        if (settings.isEnableNetteDatabase()) {
            for (NetteFilter filter : DATABASE_FILTERS) {
                filterNames.add(filter.getName());
                filters.add(filter);
            }
        }
        
        if (settings.isEnableNetteSecurity()) {
            for (NetteFilter filter : SECURITY_FILTERS) {
                filterNames.add(filter.getName());
                filters.add(filter);
            }
        }
        
        // Update cache
        cachedFilterNames.set(filterNames);
        cachedFilters.set(filters);
        cachedSettings.set(settings);
        
        System.out.println("[DEBUG_LOG] Filter cache initialized with " + filterNames.size() + " filter names and " + filters.size() + " filters");
    }
    
    /**
     * Invalidates the cache.
     * This should be called when settings change.
     */
    public static synchronized void invalidateCache() {
        System.out.println("[DEBUG_LOG] Invalidating filter cache");
        cachedFilterNames.set(null);
        cachedFilters.set(null);
        cachedSettings.set(null);
    }

    /**
     * Gets all valid filter names based on enabled settings.
     * Uses caching to improve performance.
     *
     * @return A set of valid filter names
     */
    public static Set<String> getValidFilterNames() {
        // Check if cache is valid
        if (!isCacheValid()) {
            initCache();
        }
        
        // Return cached filter names, ensuring we don't return null
        Set<String> filterNames = cachedFilterNames.get();
        return filterNames != null ? new HashSet<>(filterNames) : new HashSet<>();
    }

    /**
     * Gets all filters based on enabled settings.
     * Uses caching to improve performance.
     *
     * @return A set of filters
     */
    public static Set<NetteFilter> getAllFilters() {
        // Check if cache is valid
        if (!isCacheValid()) {
            initCache();
        }
        
        // Return cached filters, ensuring we don't return null
        Set<NetteFilter> filters = cachedFilters.get();
        return filters != null ? new HashSet<>(filters) : new HashSet<>();
    }
}