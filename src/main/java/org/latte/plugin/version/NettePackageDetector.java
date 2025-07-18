package org.latte.plugin.version;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import org.latte.plugin.settings.LatteSettings;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for detecting Nette packages from composer.json files.
 * Detects the presence and versions of nette/application, nette/forms, and nette/assets packages.
 */
public class NettePackageDetector {

    // Cache of detected packages by project
    private static final Map<String, Map<String, PackageInfo>> packageCache = new HashMap<>();
    
    // Pattern to match version constraints like "^2.4", "~3.0", "3.*", etc.
    private static final Pattern VERSION_PATTERN = Pattern.compile("^[~^]?(\\d+)\\.");

    // Package names to detect
    public static final String NETTE_APPLICATION = "nette/application";
    public static final String NETTE_FORMS = "nette/forms";
    public static final String NETTE_ASSETS = "nette/assets";
    public static final String NETTE_DATABASE = "nette/database";
    public static final String NETTE_SECURITY = "nette/security";
    public static final String NETTE_MAIL = "nette/mail";
    public static final String NETTE_HTTP = "nette/http";
    
    // Default major versions for packages
    private static final int DEFAULT_VERSION = 3;
    
    /**
     * Class to store package information (presence and version)
     */
    public static class PackageInfo {
        private final boolean present;
        private final int majorVersion;
        
        public PackageInfo(boolean present, int majorVersion) {
            this.present = present;
            this.majorVersion = majorVersion;
        }
        
        public boolean isPresent() {
            return present;
        }
        
        public int getMajorVersion() {
            return majorVersion;
        }
    }

    /**
     * Detects the presence and versions of Nette packages for the given project by parsing composer.json.
     * Updates the settings based on the detected packages.
     *
     * @param project The project to detect packages for
     */
    public static void detectAndUpdateSettings(Project project) {
        if (project == null) {
            return;
        }

        // Get the settings
        LatteSettings settings = LatteSettings.getInstance();

        // Check cache first
        String projectPath = project.getBasePath();
        if (projectPath != null && packageCache.containsKey(projectPath)) {
            Map<String, PackageInfo> packages = packageCache.get(projectPath);
            
            // Update settings based on package presence
            settings.setEnableNetteApplication(packages.containsKey(NETTE_APPLICATION) && 
                                              packages.get(NETTE_APPLICATION).isPresent());
            settings.setEnableNetteForms(packages.containsKey(NETTE_FORMS) && 
                                        packages.get(NETTE_FORMS).isPresent());
            settings.setEnableNetteAssets(packages.containsKey(NETTE_ASSETS) && 
                                         packages.get(NETTE_ASSETS).isPresent());
            settings.setEnableNetteDatabase(packages.containsKey(NETTE_DATABASE) && 
                                          packages.get(NETTE_DATABASE).isPresent());
            settings.setEnableNetteSecurity(packages.containsKey(NETTE_SECURITY) && 
                                          packages.get(NETTE_SECURITY).isPresent());
            settings.setEnableNetteMail(packages.containsKey(NETTE_MAIL) && 
                                      packages.get(NETTE_MAIL).isPresent());
            settings.setEnableNetteHttp(packages.containsKey(NETTE_HTTP) && 
                                      packages.get(NETTE_HTTP).isPresent());
            return;
        }

        // Find composer.json file
        VirtualFile composerFile = findComposerFile(project);
        if (composerFile == null) {
            return;
        }

        // Parse composer.json and detect packages
        Map<String, PackageInfo> detectedPackages = parseComposerJson(composerFile);

        // Update settings based on package presence
        settings.setEnableNetteApplication(detectedPackages.containsKey(NETTE_APPLICATION) && 
                                          detectedPackages.get(NETTE_APPLICATION).isPresent());
        settings.setEnableNetteForms(detectedPackages.containsKey(NETTE_FORMS) && 
                                    detectedPackages.get(NETTE_FORMS).isPresent());
        settings.setEnableNetteAssets(detectedPackages.containsKey(NETTE_ASSETS) && 
                                     detectedPackages.get(NETTE_ASSETS).isPresent());
        settings.setEnableNetteDatabase(detectedPackages.containsKey(NETTE_DATABASE) && 
                                      detectedPackages.get(NETTE_DATABASE).isPresent());
        settings.setEnableNetteSecurity(detectedPackages.containsKey(NETTE_SECURITY) && 
                                      detectedPackages.get(NETTE_SECURITY).isPresent());
        settings.setEnableNetteMail(detectedPackages.containsKey(NETTE_MAIL) && 
                                  detectedPackages.get(NETTE_MAIL).isPresent());
        settings.setEnableNetteHttp(detectedPackages.containsKey(NETTE_HTTP) && 
                                  detectedPackages.get(NETTE_HTTP).isPresent());

        // Cache the result
        if (projectPath != null) {
            packageCache.put(projectPath, detectedPackages);
        }
    }

    /**
     * Gets the major version of a package.
     *
     * @param project The project to get the package version for
     * @param packageName The name of the package
     * @return The major version of the package, or DEFAULT_VERSION if not detected
     */
    public static int getPackageVersion(Project project, String packageName) {
        if (project == null) {
            return DEFAULT_VERSION;
        }
        
        // Special case for nette/assets which only has version 1
        if (NETTE_ASSETS.equals(packageName)) {
            return 1;
        }

        // Check cache first
        String projectPath = project.getBasePath();
        if (projectPath != null && packageCache.containsKey(projectPath)) {
            Map<String, PackageInfo> packages = packageCache.get(projectPath);
            if (packages.containsKey(packageName)) {
                return packages.get(packageName).getMajorVersion();
            }
        }

        // If not in cache, detect packages
        VirtualFile composerFile = findComposerFile(project);
        if (composerFile == null) {
            return DEFAULT_VERSION;
        }

        Map<String, PackageInfo> detectedPackages = parseComposerJson(composerFile);
        
        // Cache the result
        if (projectPath != null) {
            packageCache.put(projectPath, detectedPackages);
        }

        // Return the version
        if (detectedPackages.containsKey(packageName)) {
            return detectedPackages.get(packageName).getMajorVersion();
        }
        
        return DEFAULT_VERSION;
    }
    
    /**
     * Checks if a package is present in the project.
     *
     * @param project The project to check for the package
     * @param packageName The name of the package
     * @return True if the package is present, false otherwise
     */
    public static boolean isPackagePresent(Project project, String packageName) {
        if (project == null) {
            return false;
        }

        // Check cache first
        String projectPath = project.getBasePath();
        if (projectPath != null && packageCache.containsKey(projectPath)) {
            Map<String, PackageInfo> packages = packageCache.get(projectPath);
            if (packages.containsKey(packageName)) {
                return packages.get(packageName).isPresent();
            }
        }

        // If not in cache, detect packages
        VirtualFile composerFile = findComposerFile(project);
        if (composerFile == null) {
            return false;
        }

        Map<String, PackageInfo> detectedPackages = parseComposerJson(composerFile);
        
        // Cache the result
        if (projectPath != null) {
            packageCache.put(projectPath, detectedPackages);
        }

        // Return whether the package is present
        return detectedPackages.containsKey(packageName) && detectedPackages.get(packageName).isPresent();
    }

    /**
     * Clears the package cache for the given project.
     *
     * @param project The project to clear the cache for
     */
    public static void clearCache(Project project) {
        if (project != null && project.getBasePath() != null) {
            packageCache.remove(project.getBasePath());
        }
    }

    /**
     * Clears the entire package cache.
     */
    public static void clearAllCache() {
        packageCache.clear();
    }

    /**
     * Finds the composer.json file in the project.
     *
     * @param project The project to find the composer.json file in
     * @return The composer.json file or null if not found
     */
    private static VirtualFile findComposerFile(Project project) {
        String basePath = project.getBasePath();
        if (basePath == null) {
            return null;
        }

        String composerPath = basePath + "/composer.json";
        return VirtualFileManager.getInstance().findFileByUrl("file://" + composerPath);
    }

    /**
     * Parses the composer.json file to detect Nette packages and their versions.
     *
     * @param composerFile The composer.json file
     * @return A map of package names to PackageInfo objects
     */
    private static Map<String, PackageInfo> parseComposerJson(VirtualFile composerFile) {
        Map<String, PackageInfo> packages = new HashMap<>();
        
        try {
            // Parse JSON
            JsonObject composerJson = JsonParser.parseReader(new FileReader(composerFile.getPath())).getAsJsonObject();

            // Check require section
            if (composerJson.has("require")) {
                JsonObject require = composerJson.getAsJsonObject("require");
                // Check for each Nette package
                checkPackage(packages, require, NETTE_APPLICATION);
                checkPackage(packages, require, NETTE_FORMS);
                checkPackage(packages, require, NETTE_ASSETS);
                checkPackage(packages, require, NETTE_DATABASE);
                checkPackage(packages, require, NETTE_SECURITY);
                checkPackage(packages, require, NETTE_MAIL);
                checkPackage(packages, require, NETTE_HTTP);
            }

            // Check require-dev section
            if (composerJson.has("require-dev")) {
                JsonObject requireDev = composerJson.getAsJsonObject("require-dev");
                // If a package is not found in require, check in require-dev
                if (!packages.containsKey(NETTE_APPLICATION)) {
                    checkPackage(packages, requireDev, NETTE_APPLICATION);
                }
                if (!packages.containsKey(NETTE_FORMS)) {
                    checkPackage(packages, requireDev, NETTE_FORMS);
                }
                if (!packages.containsKey(NETTE_ASSETS)) {
                    checkPackage(packages, requireDev, NETTE_ASSETS);
                }
                if (!packages.containsKey(NETTE_DATABASE)) {
                    checkPackage(packages, requireDev, NETTE_DATABASE);
                }
                if (!packages.containsKey(NETTE_SECURITY)) {
                    checkPackage(packages, requireDev, NETTE_SECURITY);
                }
                if (!packages.containsKey(NETTE_MAIL)) {
                    checkPackage(packages, requireDev, NETTE_MAIL);
                }
                if (!packages.containsKey(NETTE_HTTP)) {
                    checkPackage(packages, requireDev, NETTE_HTTP);
                }
            }
        } catch (IOException | JsonSyntaxException e) {
            // Log error or handle exception
            System.err.println("Error parsing composer.json: " + e.getMessage());
        }

        return packages;
    }
    
    /**
     * Checks for a package in the given JSON object and adds it to the packages map.
     *
     * @param packages The map to add the package to
     * @param jsonObject The JSON object to check for the package
     * @param packageName The name of the package to check for
     */
    private static void checkPackage(Map<String, PackageInfo> packages, JsonObject jsonObject, String packageName) {
        if (jsonObject.has(packageName)) {
            JsonElement versionConstraint = jsonObject.get(packageName);
            int majorVersion = parseMajorVersion(versionConstraint.getAsString());
            packages.put(packageName, new PackageInfo(true, majorVersion));
        } else {
            // Package not found, add with default version and not present
            packages.put(packageName, new PackageInfo(false, DEFAULT_VERSION));
        }
    }
    
    /**
     * Parses a version constraint string to determine the major version.
     *
     * @param versionConstraint The version constraint string (e.g., "^2.4", "~3.0", "3.*")
     * @return The major version number, or DEFAULT_VERSION if not detected
     */
    private static int parseMajorVersion(String versionConstraint) {
        Matcher matcher = VERSION_PATTERN.matcher(versionConstraint);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                // Ignore and return default
            }
        }
        return DEFAULT_VERSION;
    }
}