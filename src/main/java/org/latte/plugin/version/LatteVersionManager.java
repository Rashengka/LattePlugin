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
        return currentVersion == LatteVersion.VERSION_2X;
    }

    /**
     * Checks if the current version is Latte 3.0+.
     *
     * @return True if the current version is 3.0+, false otherwise
     */
    public static boolean isVersion3x() {
        return currentVersion == LatteVersion.VERSION_3X;
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