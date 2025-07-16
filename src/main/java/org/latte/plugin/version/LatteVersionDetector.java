package org.latte.plugin.version;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for detecting Latte version from composer.json files.
 */
public class LatteVersionDetector {

    // Cache of detected versions by project
    private static final Map<String, LatteVersion> versionCache = new HashMap<>();

    // Pattern to match version constraints like "^2.4", "~3.0", "3.*", etc.
    private static final Pattern VERSION_PATTERN = Pattern.compile("^[~^]?(\\d+)\\.");

    /**
     * Detects the Latte version for the given project by parsing composer.json.
     *
     * @param project The project to detect the version for
     * @return The detected version or the default version if not detected
     */
    public static LatteVersion detectVersion(Project project) {
        if (project == null) {
            return LatteVersion.getDefault();
        }

        // Check cache first
        String projectPath = project.getBasePath();
        if (projectPath != null && versionCache.containsKey(projectPath)) {
            return versionCache.get(projectPath);
        }

        // Find composer.json file
        VirtualFile composerFile = findComposerFile(project);
        if (composerFile == null) {
            return LatteVersion.getDefault();
        }

        // Parse composer.json and extract Latte version
        LatteVersion version = parseComposerJson(composerFile);

        // Cache the result
        if (projectPath != null) {
            versionCache.put(projectPath, version);
        }

        return version;
    }

    /**
     * Clears the version cache for the given project.
     *
     * @param project The project to clear the cache for
     */
    public static void clearCache(Project project) {
        if (project != null && project.getBasePath() != null) {
            versionCache.remove(project.getBasePath());
        }
    }

    /**
     * Clears the entire version cache.
     */
    public static void clearAllCache() {
        versionCache.clear();
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
     * Parses the composer.json file to extract the Latte version.
     *
     * @param composerFile The composer.json file
     * @return The detected version or the default version if not detected
     */
    private static LatteVersion parseComposerJson(VirtualFile composerFile) {
        try {
            // Parse JSON
            JSONParser parser = new JSONParser();
            JSONObject composerJson = (JSONObject) parser.parse(new FileReader(composerFile.getPath()));

            // Check require section
            JSONObject require = (JSONObject) composerJson.get("require");
            if (require != null) {
                // Check for latte/latte dependency
                Object latteVersion = require.get("latte/latte");
                if (latteVersion != null) {
                    return parseVersionConstraint(latteVersion.toString());
                }

                // Check for nette/latte dependency (older projects)
                Object netteLatteVersion = require.get("nette/latte");
                if (netteLatteVersion != null) {
                    return parseVersionConstraint(netteLatteVersion.toString());
                }
            }

            // Check require-dev section
            JSONObject requireDev = (JSONObject) composerJson.get("require-dev");
            if (requireDev != null) {
                // Check for latte/latte dependency
                Object latteVersion = requireDev.get("latte/latte");
                if (latteVersion != null) {
                    return parseVersionConstraint(latteVersion.toString());
                }

                // Check for nette/latte dependency (older projects)
                Object netteLatteVersion = requireDev.get("nette/latte");
                if (netteLatteVersion != null) {
                    return parseVersionConstraint(netteLatteVersion.toString());
                }
            }
        } catch (IOException | ParseException e) {
            // Log error or handle exception
            System.err.println("Error parsing composer.json: " + e.getMessage());
        }

        return LatteVersion.getDefault();
    }

    /**
     * Parses a version constraint string to determine the major version.
     *
     * @param versionConstraint The version constraint string (e.g., "^2.4", "~3.0", "3.*", "^4.0")
     * @return The corresponding LatteVersion
     */
    private static LatteVersion parseVersionConstraint(String versionConstraint) {
        Matcher matcher = VERSION_PATTERN.matcher(versionConstraint);
        if (matcher.find()) {
            String majorVersion = matcher.group(1);
            if ("2".equals(majorVersion)) {
                return LatteVersion.VERSION_2X;
            } else if ("3".equals(majorVersion)) {
                return LatteVersion.VERSION_3X;
            } else if ("4".equals(majorVersion)) {
                return LatteVersion.VERSION_4X;
            }
        }
        return LatteVersion.getDefault();
    }
}