package cz.hqm.latte.plugin.navigation;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manages Nette presenter mapping configuration.
 * Parses the mapping configuration from config files and provides methods for finding presenter classes.
 */
public class NettePresenterMappingManager {

    // Cache of mapping configurations by project
    private static final Map<String, List<PresenterMapping>> mappingCache = new HashMap<>();

    // Pattern for extracting mapping configuration from config files
    private static final Pattern MAPPING_PATTERN = Pattern.compile("mapping:\\s*\\{([^}]+)\\}");
    private static final Pattern MAPPING_ENTRY_PATTERN = Pattern.compile("([\\w*]+):\\s*\\[([^\\]]+)\\]");

    /**
     * Gets the presenter mapping for the given project.
     * 
     * @param project The project
     * @return The list of presenter mappings
     */
    public static List<PresenterMapping> getPresenterMapping(Project project) {
        if (project == null) {
            return Collections.emptyList();
        }

        // Check cache first
        String projectPath = project.getBasePath();
        if (projectPath != null && mappingCache.containsKey(projectPath)) {
            return mappingCache.get(projectPath);
        }

        // Find config files
        List<PresenterMapping> mappings = new ArrayList<>();
        Collection<VirtualFile> configFiles = findConfigFiles(project);

        for (VirtualFile configFile : configFiles) {
            try {
                mappings.addAll(parseConfigFile(configFile));
            } catch (IOException e) {
                // Log error or handle exception
                System.err.println("Error parsing config file: " + e.getMessage());
            }
        }

        // Add default mapping if no mappings found
        if (mappings.isEmpty()) {
            mappings.add(new PresenterMapping("*", Arrays.asList("", "*Module", "*Presenter")));
        }

        // Cache the result
        if (projectPath != null) {
            mappingCache.put(projectPath, mappings);
        }

        return mappings;
    }

    /**
     * Clears the mapping cache for the given project.
     * 
     * @param project The project
     */
    public static void clearCache(Project project) {
        if (project != null && project.getBasePath() != null) {
            mappingCache.remove(project.getBasePath());
        }
    }

    /**
     * Clears the entire mapping cache.
     */
    public static void clearAllCache() {
        mappingCache.clear();
    }

    /**
     * Finds config files in the project.
     * 
     * @param project The project
     * @return A collection of config files
     */
    private static Collection<VirtualFile> findConfigFiles(Project project) {
        List<VirtualFile> configFiles = new ArrayList<>();

        // Look for common Nette config file locations
        String[] configPaths = {
            "/app/config/config.neon",
            "/config/config.neon",
            "/app/config/config.local.neon",
            "/config/config.local.neon"
        };

        String basePath = project.getBasePath();
        if (basePath == null) {
            return configFiles;
        }

        for (String configPath : configPaths) {
            VirtualFile configFile = VirtualFileManager.getInstance().findFileByUrl("file://" + basePath + configPath);
            if (configFile != null && configFile.exists()) {
                configFiles.add(configFile);
            }
        }

        return configFiles;
    }

    /**
     * Parses a config file to extract presenter mappings.
     * 
     * @param configFile The config file
     * @return A list of presenter mappings
     * @throws IOException If an I/O error occurs
     */
    private static List<PresenterMapping> parseConfigFile(VirtualFile configFile) throws IOException {
        List<PresenterMapping> mappings = new ArrayList<>();

        // Read the config file
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile.getPath()))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            // Extract mapping configuration
            Matcher mappingMatcher = MAPPING_PATTERN.matcher(content);
            if (mappingMatcher.find()) {
                String mappingContent = mappingMatcher.group(1);
                Matcher entryMatcher = MAPPING_ENTRY_PATTERN.matcher(mappingContent);
                while (entryMatcher.find()) {
                    String module = entryMatcher.group(1);
                    String[] masks = entryMatcher.group(2).split(",");
                    List<String> maskList = new ArrayList<>();
                    for (String mask : masks) {
                        maskList.add(mask.trim());
                    }
                    mappings.add(new PresenterMapping(module, maskList));
                }
            }
        }

        return mappings;
    }

    /**
     * Finds the presenter class for the given presenter name.
     * 
     * @param project The project
     * @param presenterName The presenter name
     * @return The presenter class file, or null if not found
     */
    @Nullable
    public static PsiFile findPresenterClass(Project project, String presenterName) {
        List<PresenterMapping> mappings = getPresenterMapping(project);

        // Find the matching mapping
        PresenterMapping mapping = null;
        for (PresenterMapping m : mappings) {
            if (m.getModule().equals("*") || m.getModule().equals(presenterName)) {
                mapping = m;
                break;
            }
        }

        if (mapping == null) {
            return null;
        }

        // Apply the mapping masks to generate possible class names
        List<String> possibleClassNames = new ArrayList<>();
        for (String mask : mapping.getMasks()) {
            String className = mask.replace("*", presenterName);
            possibleClassNames.add(className);
        }

        // Find the presenter class
        for (String className : possibleClassNames) {
            // In a real plugin, we would use PHP-specific APIs to find the class
            // For now, we'll just look for files with the class name
            String fileName = className + ".php";
            PsiFile[] files = FilenameIndex.getFilesByName(project, fileName, GlobalSearchScope.projectScope(project));
            if (files.length > 0) {
                return files[0];
            }
        }

        return null;
    }

    /**
     * Represents a presenter mapping configuration.
     */
    public static class PresenterMapping {
        private final String module;
        private final List<String> masks;

        /**
         * Creates a new PresenterMapping.
         * 
         * @param module The module name
         * @param masks The mapping masks
         */
        public PresenterMapping(String module, List<String> masks) {
            this.module = module;
            this.masks = masks;
        }

        /**
         * Gets the module name.
         * 
         * @return The module name
         */
        public String getModule() {
            return module;
        }

        /**
         * Gets the mapping masks.
         * 
         * @return The mapping masks
         */
        public List<String> getMasks() {
            return masks;
        }
    }
}