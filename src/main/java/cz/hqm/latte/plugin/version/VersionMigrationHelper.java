package cz.hqm.latte.plugin.version;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helps migrate Latte templates between different versions.
 * Provides tools to automatically fix deprecated features and adapt templates to newer versions.
 */
public class VersionMigrationHelper {
    
    // Map of migration rules by source and target versions
    private static final Map<VersionPair, List<MigrationRule>> MIGRATION_RULES = new HashMap<>();
    
    // Initialize the map with migration rules
    static {
        // Migration rules from Latte 2.x to Latte 3.0+
        List<MigrationRule> rules2xTo3x = new ArrayList<>();
        rules2xTo3x.add(new MigrationRule(
                "\\{syntax\\s+([^}]+)\\}",
                "{templateType $1}",
                "Replace {syntax ...} with {templateType ...}"));
        rules2xTo3x.add(new MigrationRule(
                "\\{l\\}",
                "{left}",
                "Replace {l} with {left}"));
        rules2xTo3x.add(new MigrationRule(
                "\\{r\\}",
                "{right}",
                "Replace {r} with {right}"));
        MIGRATION_RULES.put(new VersionPair(LatteVersion.VERSION_2X, LatteVersion.VERSION_3X), rules2xTo3x);
        MIGRATION_RULES.put(new VersionPair(LatteVersion.VERSION_2_4, LatteVersion.VERSION_3X), rules2xTo3x);
        MIGRATION_RULES.put(new VersionPair(LatteVersion.VERSION_2_5, LatteVersion.VERSION_3X), rules2xTo3x);
        MIGRATION_RULES.put(new VersionPair(LatteVersion.VERSION_2X, LatteVersion.VERSION_3_0), rules2xTo3x);
        MIGRATION_RULES.put(new VersionPair(LatteVersion.VERSION_2_4, LatteVersion.VERSION_3_0), rules2xTo3x);
        MIGRATION_RULES.put(new VersionPair(LatteVersion.VERSION_2_5, LatteVersion.VERSION_3_0), rules2xTo3x);
        MIGRATION_RULES.put(new VersionPair(LatteVersion.VERSION_2X, LatteVersion.VERSION_3_1), rules2xTo3x);
        MIGRATION_RULES.put(new VersionPair(LatteVersion.VERSION_2_4, LatteVersion.VERSION_3_1), rules2xTo3x);
        MIGRATION_RULES.put(new VersionPair(LatteVersion.VERSION_2_5, LatteVersion.VERSION_3_1), rules2xTo3x);
        
        // Migration rules from Latte 3.x to Latte 4.0+
        List<MigrationRule> rules3xTo4x = new ArrayList<>();
        rules3xTo4x.add(new MigrationRule(
                "\\{ifCurrent\\s+([^}]+)\\}",
                "{if isLinkCurrent($1)}",
                "Replace {ifCurrent ...} with {if isLinkCurrent(...)}"));
        rules3xTo4x.add(new MigrationRule(
                "\\{/ifCurrent\\}",
                "{/if}",
                "Replace {/ifCurrent} with {/if}"));
        rules3xTo4x.add(new MigrationRule(
                "\\{status\\s+([^}]+)\\}",
                "{http $1}",
                "Replace {status ...} with {http ...}"));
        MIGRATION_RULES.put(new VersionPair(LatteVersion.VERSION_3X, LatteVersion.VERSION_4X), rules3xTo4x);
        MIGRATION_RULES.put(new VersionPair(LatteVersion.VERSION_3_0, LatteVersion.VERSION_4X), rules3xTo4x);
        MIGRATION_RULES.put(new VersionPair(LatteVersion.VERSION_3_1, LatteVersion.VERSION_4X), rules3xTo4x);
        MIGRATION_RULES.put(new VersionPair(LatteVersion.VERSION_3X, LatteVersion.VERSION_4_0), rules3xTo4x);
        MIGRATION_RULES.put(new VersionPair(LatteVersion.VERSION_3_0, LatteVersion.VERSION_4_0), rules3xTo4x);
        MIGRATION_RULES.put(new VersionPair(LatteVersion.VERSION_3_1, LatteVersion.VERSION_4_0), rules3xTo4x);
        
        // Migration rules from Latte 2.x to Latte 4.0+
        List<MigrationRule> rules2xTo4x = new ArrayList<>(rules2xTo3x);
        rules2xTo4x.addAll(rules3xTo4x);
        MIGRATION_RULES.put(new VersionPair(LatteVersion.VERSION_2X, LatteVersion.VERSION_4X), rules2xTo4x);
        MIGRATION_RULES.put(new VersionPair(LatteVersion.VERSION_2_4, LatteVersion.VERSION_4X), rules2xTo4x);
        MIGRATION_RULES.put(new VersionPair(LatteVersion.VERSION_2_5, LatteVersion.VERSION_4X), rules2xTo4x);
        MIGRATION_RULES.put(new VersionPair(LatteVersion.VERSION_2X, LatteVersion.VERSION_4_0), rules2xTo4x);
        MIGRATION_RULES.put(new VersionPair(LatteVersion.VERSION_2_4, LatteVersion.VERSION_4_0), rules2xTo4x);
        MIGRATION_RULES.put(new VersionPair(LatteVersion.VERSION_2_5, LatteVersion.VERSION_4_0), rules2xTo4x);
    }
    
    /**
     * Migrates the given content from the source version to the target version.
     *
     * @param content The Latte template content
     * @param sourceVersion The source version
     * @param targetVersion The target version
     * @return The migrated content
     */
    @NotNull
    public static String migrateContent(@Nullable String content, @NotNull LatteVersion sourceVersion, @NotNull LatteVersion targetVersion) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        
        // Get the migration rules for the source and target versions
        VersionPair versionPair = new VersionPair(sourceVersion, targetVersion);
        List<MigrationRule> rules = MIGRATION_RULES.get(versionPair);
        if (rules == null) {
            return content;
        }
        
        // Apply each migration rule
        String result = content;
        for (MigrationRule rule : rules) {
            Pattern pattern = Pattern.compile(rule.getPattern());
            Matcher matcher = pattern.matcher(result);
            result = matcher.replaceAll(rule.getReplacement());
        }
        
        return result;
    }
    
    /**
     * Gets the migration rules for the given source and target versions.
     *
     * @param sourceVersion The source version
     * @param targetVersion The target version
     * @return The migration rules
     */
    @NotNull
    public static List<MigrationRule> getMigrationRules(@NotNull LatteVersion sourceVersion, @NotNull LatteVersion targetVersion) {
        VersionPair versionPair = new VersionPair(sourceVersion, targetVersion);
        List<MigrationRule> rules = MIGRATION_RULES.get(versionPair);
        return rules != null ? rules : new ArrayList<>();
    }
    
    /**
     * Represents a pair of source and target versions.
     */
    private static class VersionPair {
        private final LatteVersion sourceVersion;
        private final LatteVersion targetVersion;
        
        /**
         * Creates a new VersionPair.
         *
         * @param sourceVersion The source version
         * @param targetVersion The target version
         */
        public VersionPair(@NotNull LatteVersion sourceVersion, @NotNull LatteVersion targetVersion) {
            this.sourceVersion = sourceVersion;
            this.targetVersion = targetVersion;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            
            VersionPair that = (VersionPair) o;
            
            if (sourceVersion != that.sourceVersion) return false;
            return targetVersion == that.targetVersion;
        }
        
        @Override
        public int hashCode() {
            int result = sourceVersion.hashCode();
            result = 31 * result + targetVersion.hashCode();
            return result;
        }
    }
    
    /**
     * Represents a migration rule for converting code from one version to another.
     */
    public static class MigrationRule {
        private final String pattern;
        private final String replacement;
        private final String description;
        
        /**
         * Creates a new MigrationRule.
         *
         * @param pattern The regex pattern to match
         * @param replacement The replacement string
         * @param description The description of the rule
         */
        public MigrationRule(@NotNull String pattern, @NotNull String replacement, @NotNull String description) {
            this.pattern = pattern;
            this.replacement = replacement;
            this.description = description;
        }
        
        /**
         * Gets the regex pattern to match.
         *
         * @return The pattern
         */
        @NotNull
        public String getPattern() {
            return pattern;
        }
        
        /**
         * Gets the replacement string.
         *
         * @return The replacement
         */
        @NotNull
        public String getReplacement() {
            return replacement;
        }
        
        /**
         * Gets the description of the rule.
         *
         * @return The description
         */
        @NotNull
        public String getDescription() {
            return description;
        }
    }
}