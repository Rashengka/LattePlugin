package cz.hqm.latte.plugin.custom;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import cz.hqm.latte.plugin.settings.LatteProjectSettings;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides custom Latte tags from project settings.
 */
public class CustomTagsProvider {
    
    /**
     * Gets all custom tags for the specified project.
     *
     * @param project The project
     * @return A set of custom tags
     */
    @NotNull
    public static Set<CustomTag> getAllTags(@NotNull Project project) {
        LatteProjectSettings settings = LatteProjectSettings.getInstance(project);
        return new HashSet<>(settings.getCustomTags());
    }
    
    /**
     * Gets all custom tag names for the specified project.
     *
     * @param project The project
     * @return A set of custom tag names
     */
    @NotNull
    public static Set<String> getAllTagNames(@NotNull Project project) {
        Set<String> tagNames = new HashSet<>();
        for (CustomTag tag : getAllTags(project)) {
            tagNames.add(tag.getName());
        }
        return tagNames;
    }
    
    /**
     * Checks if a tag with the specified name exists.
     *
     * @param project The project
     * @param name The tag name
     * @return True if the tag exists, false otherwise
     */
    public static boolean tagExists(@NotNull Project project, @NotNull String name) {
        return getAllTagNames(project).contains(name);
    }
    
    /**
     * Gets a tag by name.
     *
     * @param project The project
     * @param name The tag name
     * @return The tag or null if not found
     */
    public static CustomTag getTagByName(@NotNull Project project, @NotNull String name) {
        for (CustomTag tag : getAllTags(project)) {
            if (tag.getName().equals(name)) {
                return tag;
            }
        }
        return null;
    }
    
    /**
     * Adds a new custom tag.
     *
     * @param project The project
     * @param name The tag name
     * @param description The tag description
     * @return The added tag
     */
    @NotNull
    public static CustomTag addTag(@NotNull Project project, @NotNull String name, String description) {
        LatteProjectSettings settings = LatteProjectSettings.getInstance(project);
        CustomTag tag = new CustomTag(name, description);
        settings.addCustomTag(tag);
        return tag;
    }
    
    /**
     * Removes a custom tag.
     *
     * @param project The project
     * @param name The tag name
     * @return True if the tag was removed, false otherwise
     */
    public static boolean removeTag(@NotNull Project project, @NotNull String name) {
        LatteProjectSettings settings = LatteProjectSettings.getInstance(project);
        CustomTag tag = getTagByName(project, name);
        if (tag != null) {
            settings.removeCustomTag(tag);
            return true;
        }
        return false;
    }
}