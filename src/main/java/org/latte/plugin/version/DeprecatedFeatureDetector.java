package org.latte.plugin.version;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detects deprecated features in Latte templates based on the current version.
 * Provides warnings for deprecated features to help users migrate to newer versions.
 */
public class DeprecatedFeatureDetector {
    
    // Map of deprecated features by version
    private static final Map<LatteVersion, List<DeprecatedFeature>> DEPRECATED_FEATURES = new HashMap<>();
    
    // Initialize the map with deprecated features
    static {
        // Features deprecated in Latte 3.0
        List<DeprecatedFeature> deprecatedIn3_0 = new ArrayList<>();
        deprecatedIn3_0.add(new DeprecatedFeature(
                "\\{syntax\\s+[^}]+\\}",
                "{syntax} macro is deprecated in Latte 3.0+. Use {templateType} instead.",
                "Replace {syntax ...} with {templateType ...}"));
        deprecatedIn3_0.add(new DeprecatedFeature(
                "\\{l\\}",
                "{l} macro is deprecated in Latte 3.0+. Use {left} instead.",
                "Replace {l} with {left}"));
        deprecatedIn3_0.add(new DeprecatedFeature(
                "\\{r\\}",
                "{r} macro is deprecated in Latte 3.0+. Use {right} instead.",
                "Replace {r} with {right}"));
        DEPRECATED_FEATURES.put(LatteVersion.VERSION_3_0, deprecatedIn3_0);
        DEPRECATED_FEATURES.put(LatteVersion.VERSION_3_1, deprecatedIn3_0);
        DEPRECATED_FEATURES.put(LatteVersion.VERSION_3X, deprecatedIn3_0);
        
        // Features deprecated in Latte 4.0
        List<DeprecatedFeature> deprecatedIn4_0 = new ArrayList<>(deprecatedIn3_0); // Include all features deprecated in 3.0
        deprecatedIn4_0.add(new DeprecatedFeature(
                "\\{ifCurrent\\s+[^}]+\\}",
                "{ifCurrent} macro is deprecated in Latte 4.0+. Use {if isLinkCurrent(...)} instead.",
                "Replace {ifCurrent ...} with {if isLinkCurrent(...)}"));
        deprecatedIn4_0.add(new DeprecatedFeature(
                "\\{status\\s+[^}]+\\}",
                "{status} macro is deprecated in Latte 4.0+. Use {http} instead.",
                "Replace {status ...} with {http ...}"));
        DEPRECATED_FEATURES.put(LatteVersion.VERSION_4_0, deprecatedIn4_0);
        DEPRECATED_FEATURES.put(LatteVersion.VERSION_4X, deprecatedIn4_0);
    }
    
    /**
     * Detects deprecated features in the given content based on the current version.
     *
     * @param content The Latte template content
     * @return A list of detected deprecated features
     */
    @NotNull
    public static List<DeprecatedFeatureWarning> detectDeprecatedFeatures(@Nullable String content) {
        if (content == null || content.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<DeprecatedFeatureWarning> warnings = new ArrayList<>();
        LatteVersion currentVersion = LatteVersionManager.getCurrentVersion();
        
        // Get the list of deprecated features for the current version
        List<DeprecatedFeature> deprecatedFeatures = DEPRECATED_FEATURES.get(currentVersion);
        if (deprecatedFeatures == null) {
            return warnings;
        }
        
        // Check for each deprecated feature
        for (DeprecatedFeature feature : deprecatedFeatures) {
            Pattern pattern = Pattern.compile(feature.getPattern());
            Matcher matcher = pattern.matcher(content);
            
            while (matcher.find()) {
                int startOffset = matcher.start();
                int endOffset = matcher.end();
                String matchedText = matcher.group();
                
                warnings.add(new DeprecatedFeatureWarning(
                        matchedText,
                        feature.getMessage(),
                        feature.getSuggestion(),
                        startOffset,
                        endOffset
                ));
            }
        }
        
        return warnings;
    }
    
    /**
     * Represents a deprecated feature in Latte.
     */
    private static class DeprecatedFeature {
        private final String pattern;
        private final String message;
        private final String suggestion;
        
        /**
         * Creates a new DeprecatedFeature.
         *
         * @param pattern The regex pattern to match the deprecated feature
         * @param message The warning message
         * @param suggestion The suggestion for fixing the deprecated feature
         */
        public DeprecatedFeature(@NotNull String pattern, @NotNull String message, @NotNull String suggestion) {
            this.pattern = pattern;
            this.message = message;
            this.suggestion = suggestion;
        }
        
        /**
         * Gets the regex pattern to match the deprecated feature.
         *
         * @return The pattern
         */
        @NotNull
        public String getPattern() {
            return pattern;
        }
        
        /**
         * Gets the warning message.
         *
         * @return The message
         */
        @NotNull
        public String getMessage() {
            return message;
        }
        
        /**
         * Gets the suggestion for fixing the deprecated feature.
         *
         * @return The suggestion
         */
        @NotNull
        public String getSuggestion() {
            return suggestion;
        }
    }
    
    /**
     * Represents a warning for a deprecated feature in Latte.
     */
    public static class DeprecatedFeatureWarning {
        private final String text;
        private final String message;
        private final String suggestion;
        private final int startOffset;
        private final int endOffset;
        
        /**
         * Creates a new DeprecatedFeatureWarning.
         *
         * @param text The deprecated feature text
         * @param message The warning message
         * @param suggestion The suggestion for fixing the deprecated feature
         * @param startOffset The start offset of the deprecated feature in the content
         * @param endOffset The end offset of the deprecated feature in the content
         */
        public DeprecatedFeatureWarning(@NotNull String text, @NotNull String message, @NotNull String suggestion,
                                        int startOffset, int endOffset) {
            this.text = text;
            this.message = message;
            this.suggestion = suggestion;
            this.startOffset = startOffset;
            this.endOffset = endOffset;
        }
        
        /**
         * Gets the deprecated feature text.
         *
         * @return The text
         */
        @NotNull
        public String getText() {
            return text;
        }
        
        /**
         * Gets the warning message.
         *
         * @return The message
         */
        @NotNull
        public String getMessage() {
            return message;
        }
        
        /**
         * Gets the suggestion for fixing the deprecated feature.
         *
         * @return The suggestion
         */
        @NotNull
        public String getSuggestion() {
            return suggestion;
        }
        
        /**
         * Gets the start offset of the deprecated feature in the content.
         *
         * @return The start offset
         */
        public int getStartOffset() {
            return startOffset;
        }
        
        /**
         * Gets the end offset of the deprecated feature in the content.
         *
         * @return The end offset
         */
        public int getEndOffset() {
            return endOffset;
        }
    }
}