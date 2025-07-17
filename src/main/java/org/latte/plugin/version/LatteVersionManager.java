package org.latte.plugin.version;

/**
 * Manages the Latte version used by the plugin.
 * This simplified implementation uses a static configuration approach.
 */
public class LatteVersionManager {

    // The currently selected version
    private static LatteVersion currentVersion = LatteVersion.getDefault();

    /**
     * Gets the current Latte version.
     *
     * @return The current version
     */
    public static LatteVersion getCurrentVersion() {
        return currentVersion;
    }

    /**
     * Sets the current Latte version.
     *
     * @param version The version to set
     */
    public static void setCurrentVersion(LatteVersion version) {
        if (version != null) {
            currentVersion = version;
        }
    }

    /**
     * Checks if the current version is Latte 2.x.
     *
     * @return True if the current version is 2.x, false otherwise
     */
    public static boolean isVersion2x() {
        return currentVersion == LatteVersion.VERSION_2X || 
               currentVersion == LatteVersion.VERSION_2_4 || 
               currentVersion == LatteVersion.VERSION_2_5;
    }
    
    /**
     * Checks if the current version is Latte 2.4.
     *
     * @return True if the current version is 2.4, false otherwise
     */
    public static boolean isVersion2_4() {
        return currentVersion == LatteVersion.VERSION_2_4;
    }
    
    /**
     * Checks if the current version is Latte 2.5.
     *
     * @return True if the current version is 2.5, false otherwise
     */
    public static boolean isVersion2_5() {
        return currentVersion == LatteVersion.VERSION_2_5;
    }

    /**
     * Checks if the current version is Latte 3.0+.
     *
     * @return True if the current version is 3.0+, false otherwise
     */
    public static boolean isVersion3x() {
        return currentVersion == LatteVersion.VERSION_3X || 
               currentVersion == LatteVersion.VERSION_3_0 || 
               currentVersion == LatteVersion.VERSION_3_1;
    }
    
    /**
     * Checks if the current version is Latte 3.0.
     *
     * @return True if the current version is 3.0, false otherwise
     */
    public static boolean isVersion3_0() {
        return currentVersion == LatteVersion.VERSION_3_0;
    }
    
    /**
     * Checks if the current version is Latte 3.1.
     *
     * @return True if the current version is 3.1, false otherwise
     */
    public static boolean isVersion3_1() {
        return currentVersion == LatteVersion.VERSION_3_1;
    }
    
    /**
     * Checks if the current version is Latte 4.0+.
     *
     * @return True if the current version is 4.0+, false otherwise
     */
    public static boolean isVersion4x() {
        return currentVersion == LatteVersion.VERSION_4X || 
               currentVersion == LatteVersion.VERSION_4_0;
    }
    
    /**
     * Checks if the current version is Latte 4.0.
     *
     * @return True if the current version is 4.0, false otherwise
     */
    public static boolean isVersion4_0() {
        return currentVersion == LatteVersion.VERSION_4_0;
    }
    
    /**
     * Checks if the current version is at least the specified version.
     *
     * @param version The version to check against
     * @return True if the current version is at least the specified version, false otherwise
     */
    public static boolean isAtLeastVersion(LatteVersion version) {
        if (version == LatteVersion.VERSION_2X || version == LatteVersion.VERSION_2_4 || version == LatteVersion.VERSION_2_5) {
            return true; // All versions are at least 2.x
        }
        
        if (version == LatteVersion.VERSION_3X || version == LatteVersion.VERSION_3_0 || version == LatteVersion.VERSION_3_1) {
            return isVersion3x() || isVersion4x(); // 3.x and 4.x are at least 3.x
        }
        
        if (version == LatteVersion.VERSION_4X || version == LatteVersion.VERSION_4_0) {
            return isVersion4x(); // Only 4.x is at least 4.x
        }
        
        return false;
    }

    /**
     * Gets the documentation URL for the current version.
     *
     * @return The documentation URL
     */
    public static String getDocumentationUrl() {
        return currentVersion.getDocumentationUrl();
    }

    /**
     * Gets the tags documentation URL for the current version.
     *
     * @return The tags documentation URL
     */
    public static String getTagsDocumentationUrl() {
        return currentVersion.getTagsDocumentationUrl();
    }

    /**
     * Gets the filters documentation URL for the current version.
     *
     * @return The filters documentation URL
     */
    public static String getFiltersDocumentationUrl() {
        return currentVersion.getFiltersDocumentationUrl();
    }

    /**
     * Gets the functions documentation URL for the current version.
     *
     * @return The functions documentation URL
     */
    public static String getFunctionsDocumentationUrl() {
        return currentVersion.getFunctionsDocumentationUrl();
    }
}