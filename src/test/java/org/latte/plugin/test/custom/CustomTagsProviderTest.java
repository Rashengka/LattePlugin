package org.latte.plugin.test.custom;

import com.intellij.openapi.project.Project;
import org.latte.plugin.custom.CustomTag;
import org.latte.plugin.custom.CustomTagsProvider;
import org.latte.plugin.settings.LatteProjectSettings;
import org.latte.plugin.test.LattePluginTestBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Tests for the CustomTagsProvider class.
 */
public class CustomTagsProviderTest extends LattePluginTestBase {
    
    private Project project;
    private LatteProjectSettings settings;
    private List<CustomTag> testTags;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // Get the project from the test fixture
        project = getProject();
        
        // Get the settings instance for the project
        settings = LatteProjectSettings.getInstance(project);
        
        // Save the original tags
        testTags = new ArrayList<>(settings.getCustomTags());
        
        // Clear the tags for testing
        settings.setCustomTags(new ArrayList<>());
    }
    
    @Override
    protected void tearDown() throws Exception {
        // Restore the original tags
        settings.setCustomTags(testTags);
        
        super.tearDown();
    }
    
    /**
     * Tests getting all tags when there are no tags.
     */
    public void testGetAllTagsEmpty() {
        Set<CustomTag> tags = CustomTagsProvider.getAllTags(project);
        
        assertNotNull("Tags set should not be null", tags);
        assertEquals("Tags set should be empty", 0, tags.size());
    }
    
    /**
     * Tests getting all tag names when there are no tags.
     */
    public void testGetAllTagNamesEmpty() {
        Set<String> tagNames = CustomTagsProvider.getAllTagNames(project);
        
        assertNotNull("Tag names set should not be null", tagNames);
        assertEquals("Tag names set should be empty", 0, tagNames.size());
    }
    
    /**
     * Tests tag existence check when the tag doesn't exist.
     */
    public void testTagExistsFalse() {
        boolean exists = CustomTagsProvider.tagExists(project, "nonExistentTag");
        
        assertFalse("Tag should not exist", exists);
    }
    
    /**
     * Tests getting a tag by name when the tag doesn't exist.
     */
    public void testGetTagByNameNotFound() {
        CustomTag tag = CustomTagsProvider.getTagByName(project, "nonExistentTag");
        
        assertNull("Tag should not be found", tag);
    }
    
    /**
     * Tests adding a tag.
     */
    public void testAddTag() {
        CustomTag tag = CustomTagsProvider.addTag(project, "testTag", "testDescription");
        
        assertNotNull("Added tag should not be null", tag);
        assertEquals("Tag name should be set correctly", "testTag", tag.getName());
        assertEquals("Tag description should be set correctly", "testDescription", tag.getDescription());
        
        // Verify the tag was added to the settings
        List<CustomTag> tags = settings.getCustomTags();
        assertEquals("Settings should have 1 tag", 1, tags.size());
        assertEquals("Tag in settings should have the correct name", "testTag", tags.get(0).getName());
        assertEquals("Tag in settings should have the correct description", "testDescription", tags.get(0).getDescription());
        
        // Verify the tag can be retrieved
        assertTrue("Tag should exist", CustomTagsProvider.tagExists(project, "testTag"));
        CustomTag retrievedTag = CustomTagsProvider.getTagByName(project, "testTag");
        assertNotNull("Retrieved tag should not be null", retrievedTag);
        assertEquals("Retrieved tag should have the correct name", "testTag", retrievedTag.getName());
        assertEquals("Retrieved tag should have the correct description", "testDescription", retrievedTag.getDescription());
    }
    
    /**
     * Tests removing a tag.
     */
    public void testRemoveTag() {
        // Add a tag first
        CustomTagsProvider.addTag(project, "testTag", "testDescription");
        
        // Verify the tag exists
        assertTrue("Tag should exist before removal", CustomTagsProvider.tagExists(project, "testTag"));
        
        // Remove the tag
        boolean removed = CustomTagsProvider.removeTag(project, "testTag");
        
        assertTrue("Tag should be removed successfully", removed);
        assertFalse("Tag should not exist after removal", CustomTagsProvider.tagExists(project, "testTag"));
        assertEquals("Settings should have 0 tags", 0, settings.getCustomTags().size());
    }
    
    /**
     * Tests removing a non-existent tag.
     */
    public void testRemoveNonExistentTag() {
        boolean removed = CustomTagsProvider.removeTag(project, "nonExistentTag");
        
        assertFalse("Non-existent tag should not be removed", removed);
    }
    
    /**
     * Tests adding multiple tags and retrieving them.
     */
    public void testAddMultipleTags() {
        CustomTagsProvider.addTag(project, "tag1", "description1");
        CustomTagsProvider.addTag(project, "tag2", "description2");
        CustomTagsProvider.addTag(project, "tag3", "description3");
        
        Set<CustomTag> tags = CustomTagsProvider.getAllTags(project);
        assertEquals("Should have 3 tags", 3, tags.size());
        
        Set<String> tagNames = CustomTagsProvider.getAllTagNames(project);
        assertEquals("Should have 3 tag names", 3, tagNames.size());
        assertTrue("Should contain tag1", tagNames.contains("tag1"));
        assertTrue("Should contain tag2", tagNames.contains("tag2"));
        assertTrue("Should contain tag3", tagNames.contains("tag3"));
    }
    
    /**
     * Tests adding a duplicate tag.
     */
    public void testAddDuplicateTag() {
        CustomTagsProvider.addTag(project, "testTag", "description1");
        CustomTagsProvider.addTag(project, "testTag", "description2");
        
        Set<CustomTag> tags = CustomTagsProvider.getAllTags(project);
        assertEquals("Should have 1 tag (no duplicates)", 1, tags.size());
        
        // The first tag should be preserved
        CustomTag tag = CustomTagsProvider.getTagByName(project, "testTag");
        assertEquals("Description should be from the first tag", "description1", tag.getDescription());
    }
}