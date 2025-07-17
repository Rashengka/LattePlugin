package org.latte.plugin.filters;

import org.latte.plugin.settings.LatteSettings;

import java.util.*;

/**
 * Provides Latte filters from Nette packages based on enabled settings.
 * This class is responsible for storing and providing filters from different Nette packages.
 */
public class NetteFilterProvider {

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
            new NetteFilter("escapeUrl", "Escapes a string for use inside URL", "latte/core"),
            new NetteFilter("noescape", "Prints a variable without escaping", "latte/core"),
            new NetteFilter("truncate", "Shortens a string to the given maximum length", "latte/core"),
            new NetteFilter("substring", "Returns part of a string", "latte/core"),
            new NetteFilter("trim", "Strips whitespace from the beginning and end of a string", "latte/core"),
            new NetteFilter("padLeft", "Pads a string to a certain length with another string from the left", "latte/core"),
            new NetteFilter("padRight", "Pads a string to a certain length with another string from the right", "latte/core"),
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
     * Gets all valid filter names based on enabled settings.
     *
     * @return A set of valid filter names
     */
    public static Set<String> getValidFilterNames() {
        Set<String> filterNames = new HashSet<>();
        
        // Add core filters
        CORE_FILTERS.forEach(filter -> filterNames.add(filter.getName()));
        
        // Get settings
        LatteSettings settings = LatteSettings.getInstance();
        
        // Add filters based on enabled settings
        if (settings.isEnableNetteApplication()) {
            APPLICATION_FILTERS.forEach(filter -> filterNames.add(filter.getName()));
        }
        
        if (settings.isEnableNetteForms()) {
            FORMS_FILTERS.forEach(filter -> filterNames.add(filter.getName()));
        }
        
        if (settings.isEnableNetteAssets()) {
            ASSETS_FILTERS.forEach(filter -> filterNames.add(filter.getName()));
        }
        
        if (settings.isEnableNetteDatabase()) {
            DATABASE_FILTERS.forEach(filter -> filterNames.add(filter.getName()));
        }
        
        if (settings.isEnableNetteSecurity()) {
            SECURITY_FILTERS.forEach(filter -> filterNames.add(filter.getName()));
        }
        
        return filterNames;
    }

    /**
     * Gets all filters based on enabled settings.
     *
     * @return A set of filters
     */
    public static Set<NetteFilter> getAllFilters() {
        Set<NetteFilter> filters = new HashSet<>();
        
        // Add core filters
        filters.addAll(CORE_FILTERS);
        
        // Get settings
        LatteSettings settings = LatteSettings.getInstance();
        
        // Add filters based on enabled settings
        if (settings.isEnableNetteApplication()) {
            filters.addAll(APPLICATION_FILTERS);
        }
        
        if (settings.isEnableNetteForms()) {
            filters.addAll(FORMS_FILTERS);
        }
        
        if (settings.isEnableNetteAssets()) {
            filters.addAll(ASSETS_FILTERS);
        }
        
        if (settings.isEnableNetteDatabase()) {
            filters.addAll(DATABASE_FILTERS);
        }
        
        if (settings.isEnableNetteSecurity()) {
            filters.addAll(SECURITY_FILTERS);
        }
        
        return filters;
    }
}