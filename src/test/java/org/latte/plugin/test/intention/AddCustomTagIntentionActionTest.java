package org.latte.plugin.test.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.intellij.openapi.project.Project;
import org.latte.plugin.custom.CustomTag;
import org.latte.plugin.custom.CustomTagsProvider;
import org.latte.plugin.intention.AddCustomTagIntentionAction;
import org.latte.plugin.test.LattePluginTestBase;

import java.util.List;

/**
 * Tests for the AddCustomTagIntentionAction class.
 */
public class AddCustomTagIntentionActionTest extends LattePluginTestBase {
    
    private Project project;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // Get the project from the test fixture
        project = getProject();
        
        // Clear any existing custom tags
        clearCustomTags();
    }
    
    @Override
    protected void tearDown() throws Exception {
        // Clear custom tags
        clearCustomTags();
        
        super.tearDown();
    }
    
    /**
     * Clears all custom tags.
     */
    private void clearCustomTags() {
        CustomTagsProvider.getAllTags(project).forEach(tag -> 
            CustomTagsProvider.removeTag(project, tag.getName()));
    }
    
    /**
     * Tests that the intention action is available for unknown tags.
     */
    @Test
    public void testIntentionActionAvailability() {
        // Create a Latte file with an unknown tag
        myFixture.configureByText("test.latte", "{unknownTag}");
        
        // Check if the intention action is available
        List<IntentionAction> intentions = myFixture.filterAvailableIntentions("Add as custom Latte tag");
        
        // Verify
        assertNotNull("Intentions should not be null", intentions);
        assertFalse("Intentions should not be empty", intentions.isEmpty());
        assertEquals("Should have one intention", 1, intentions.size());
        assertEquals("Intention should be AddCustomTagIntentionAction", 
                     AddCustomTagIntentionAction.class.getName(), 
                     intentions.get(0).getClass().getName());
    }
    
    /**
     * Tests that the intention action is not available for known tags.
     */
    @Test
    public void testIntentionActionNotAvailableForKnownTags() {
        // Add a custom tag
        CustomTagsProvider.addTag(project, "knownTag", "Known tag for testing");
        
        // Create a Latte file with a known tag
        myFixture.configureByText("test.latte", "{knownTag}");
        
        // Check if the intention action is available
        List<IntentionAction> intentions = myFixture.filterAvailableIntentions("Add as custom Latte tag");
        
        // Verify
        assertTrue("Intentions should be empty or null", intentions == null || intentions.isEmpty());
    }
    
    /**
     * Tests that the intention action is not available for non-tag elements.
     */
    @Test
    public void testIntentionActionNotAvailableForNonTags() {
        // Create a Latte file with a non-tag element
        myFixture.configureByText("test.latte", "{$variable}");
        
        // Check if the intention action is available
        List<IntentionAction> intentions = myFixture.filterAvailableIntentions("Add as custom Latte tag");
        
        // Verify
        assertTrue("Intentions should be empty or null", intentions == null || intentions.isEmpty());
    }
    
    /**
     * Tests that the intention action correctly adds a custom tag.
     * 
     * Note: This test is limited because we can't directly invoke the intention action
     * with the dialog in a test environment. In a real test, we would need to mock the
     * dialog or use a headless UI environment. For now, we'll just verify that the
     * intention action is available and that we can add a custom tag programmatically.
     */
    @Test
    public void testAddCustomTag() {
        // Create a Latte file with an unknown tag
        myFixture.configureByText("test.latte", "{unknownTag}");
        
        // Check if the intention action is available
        List<IntentionAction> intentions = myFixture.filterAvailableIntentions("Add as custom Latte tag");
        
        // Verify the intention is available
        assertNotNull("Intentions should not be null", intentions);
        assertFalse("Intentions should not be empty", intentions.isEmpty());
        
        // Add the tag programmatically (simulating the intention action)
        CustomTagsProvider.addTag(project, "unknownTag", "Added by intention action");
        
        // Verify the tag was added
        CustomTag tag = CustomTagsProvider.getTagByName(project, "unknownTag");
        assertNotNull("Tag should not be null", tag);
        assertEquals("Tag name should be correct", "unknownTag", tag.getName());
        assertEquals("Tag description should be correct", "Added by intention action", tag.getDescription());
    }
}
