package org.latte.plugin.version;

/**
 * Enum to represent Latte versions.
 * Provides constants and methods for working with different Latte versions.
 */
public enum LatteVersion {
    
    /**
     * Latte version 2.x
     */
    VERSION_2X("2.x", "https://latte.nette.org/en/syntax/2.x", "https://latte.nette.org/en/tags/2.x", 
               "https://latte.nette.org/en/filters/2.x", "https://latte.nette.org/en/functions/2.x"),
    
    /**
     * Latte version 3.0+
     */
    VERSION_3X("3.0+", "https://latte.nette.org/en/syntax", "https://latte.nette.org/en/tags", 
               "https://latte.nette.org/en/filters", "https://latte.nette.org/en/functions"),
               
    /**
     * Latte version 4.0+
     */
    VERSION_4X("4.0+", "https://latte.nette.org/en/syntax", "https://latte.nette.org/en/tags", 
               "https://latte.nette.org/en/filters", "https://latte.nette.org/en/functions");
    
    private final String displayName;
    private final String documentationUrl;
    private final String tagsDocumentationUrl;
    private final String filtersDocumentationUrl;
    private final String functionsDocumentationUrl;
    
    /**
     * Constructor for LatteVersion enum.
     *
     * @param displayName The display name of the version
     * @param documentationUrl The documentation URL for the version
     * @param tagsDocumentationUrl The tags documentation URL for the version
     * @param filtersDocumentationUrl The filters documentation URL for the version
     * @param functionsDocumentationUrl The functions documentation URL for the version
     */
    LatteVersion(String displayName, String documentationUrl, String tagsDocumentationUrl, 
                String filtersDocumentationUrl, String functionsDocumentationUrl) {
        this.displayName = displayName;
        this.documentationUrl = documentationUrl;
        this.tagsDocumentationUrl = tagsDocumentationUrl;
        this.filtersDocumentationUrl = filtersDocumentationUrl;
        this.functionsDocumentationUrl = functionsDocumentationUrl;
    }
    
    /**
     * Gets the display name of the version.
     *
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the documentation URL for the version.
     *
     * @return The documentation URL
     */
    public String getDocumentationUrl() {
        return documentationUrl;
    }
    
    /**
     * Gets the tags documentation URL for the version.
     *
     * @return The tags documentation URL
     */
    public String getTagsDocumentationUrl() {
        return tagsDocumentationUrl;
    }
    
    /**
     * Gets the filters documentation URL for the version.
     *
     * @return The filters documentation URL
     */
    public String getFiltersDocumentationUrl() {
        return filtersDocumentationUrl;
    }
    
    /**
     * Gets the functions documentation URL for the version.
     *
     * @return The functions documentation URL
     */
    public String getFunctionsDocumentationUrl() {
        return functionsDocumentationUrl;
    }
    
    /**
     * Gets the default Latte version.
     *
     * @return The default version (VERSION_3X)
     */
    public static LatteVersion getDefault() {
        return VERSION_3X;
    }
    
    /**
     * Attempts to detect the Latte version from the given content.
     * This is a simple heuristic based on version-specific syntax.
     *
     * @param content The Latte template content
     * @return The detected version or null if detection failed
     */
    public static LatteVersion detectVersionFromContent(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }
        
        // Look for version-specific comment
        if (content.contains("{* Latte 2.x *}")) {
            return VERSION_2X;
        }
        if (content.contains("{* Latte 3.0+ *}")) {
            return VERSION_3X;
        }
        
        // Look for version-specific syntax patterns
        // (These are simplified examples - real detection would be more sophisticated)
        if (content.contains("{varType") || content.contains("{templateType")) {
            return VERSION_3X; // These are 3.0+ specific macros
        }
        
        // Default to null (couldn't detect)
        return null;
    }
}