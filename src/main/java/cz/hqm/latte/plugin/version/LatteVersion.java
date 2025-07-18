package cz.hqm.latte.plugin.version;

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
     * Latte version 2.4
     */
    VERSION_2_4("2.4", "https://latte.nette.org/en/syntax/2.x", "https://latte.nette.org/en/tags/2.x", 
                "https://latte.nette.org/en/filters/2.x", "https://latte.nette.org/en/functions/2.x"),
    
    /**
     * Latte version 2.5
     */
    VERSION_2_5("2.5", "https://latte.nette.org/en/syntax/2.x", "https://latte.nette.org/en/tags/2.x", 
                "https://latte.nette.org/en/filters/2.x", "https://latte.nette.org/en/functions/2.x"),
    
    /**
     * Latte version 3.0+
     */
    VERSION_3X("3.0+", "https://latte.nette.org/en/syntax", "https://latte.nette.org/en/tags", 
               "https://latte.nette.org/en/filters", "https://latte.nette.org/en/functions"),
    
    /**
     * Latte version 3.0
     */
    VERSION_3_0("3.0", "https://latte.nette.org/en/syntax", "https://latte.nette.org/en/tags", 
                "https://latte.nette.org/en/filters", "https://latte.nette.org/en/functions"),
    
    /**
     * Latte version 3.1
     */
    VERSION_3_1("3.1", "https://latte.nette.org/en/syntax", "https://latte.nette.org/en/tags", 
                "https://latte.nette.org/en/filters", "https://latte.nette.org/en/functions"),
               
    /**
     * Latte version 4.0+
     */
    VERSION_4X("4.0+", "https://latte.nette.org/en/syntax", "https://latte.nette.org/en/tags", 
               "https://latte.nette.org/en/filters", "https://latte.nette.org/en/functions"),
    
    /**
     * Latte version 4.0
     */
    VERSION_4_0("4.0", "https://latte.nette.org/en/syntax", "https://latte.nette.org/en/tags", 
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
        if (content.contains("{* Latte 2.x *}") || content.contains("{* Latte 2.x specific features")) {
            return VERSION_2X;
        }
        if (content.contains("{* Latte 3.0+ *}") || content.contains("{* Latte 3.0+ specific features")) {
            return VERSION_3X;
        }
        if (content.contains("{* Latte 4.0+ *}") || content.contains("{* Latte 4.0+ specific features")) {
            return VERSION_4X;
        }
        
        // Look for Latte 2.x specific syntax patterns
        if (content.contains("{syntax") || content.contains("{l}") || content.contains("{r}") || 
            content.contains("{use") || content.contains("n:ifcontent") || 
            content.contains("|bytes") || content.contains("|dataStream") || content.contains("|url")) {
            return VERSION_2X;
        }
        
        // Look for Latte 4.0+ specific syntax patterns
        if (content.contains("{typeCheck") || content.contains("{strictTypes") || 
            content.contains("{asyncInclude") || content.contains("{await") || 
            content.contains("{inject") || content.contains("|json") || 
            content.contains("|base64") || content.contains("|format:")) {
            return VERSION_4X;
        }
        
        // Look for Latte 3.0+ specific syntax patterns
        if (content.contains("{varType") || content.contains("{templateType") || 
            content.contains("{parameters") || content.contains("{php") || 
            content.contains("{do")) {
            return VERSION_3X;
        }
        
        // Default to null (couldn't detect)
        return null;
    }
}