package org.latte.plugin.test.version;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.latte.plugin.test.LattePluginTestBase;
import org.latte.plugin.version.LatteVersion;
import org.latte.plugin.version.VersionMigrationHelper;

import java.util.List;

/**
 * Tests for the VersionMigrationHelper class.
 */
public class VersionMigrationHelperTest extends LattePluginTestBase {

    /**
     * Tests migration from Latte 2.x to Latte 3.0+.
     */
    @Test
    public void testMigrationFrom2xTo3x() {
        // Test syntax macro migration
        String content = "{syntax double}\nHello World\n{/syntax}";
        String expected = "{templateType double}\nHello World\n{/syntax}";
        String result = VersionMigrationHelper.migrateContent(content, LatteVersion.VERSION_2X, LatteVersion.VERSION_3X);
        assertEquals("Syntax macro should be migrated to templateType", expected, result);
        
        // Test l and r macros migration
        content = "This is a {l}variable{r} in Latte 2.x";
        expected = "This is a {left}variable{right} in Latte 2.x";
        result = VersionMigrationHelper.migrateContent(content, LatteVersion.VERSION_2X, LatteVersion.VERSION_3X);
        assertEquals("l and r macros should be migrated to left and right", expected, result);
    }
    
    /**
     * Tests migration from Latte 3.x to Latte 4.0+.
     */
    @Test
    public void testMigrationFrom3xTo4x() {
        // Test ifCurrent macro migration
        String content = "{ifCurrent Homepage:default}\nActive\n{/ifCurrent}";
        String expected = "{if isLinkCurrent(Homepage:default)}\nActive\n{/if}";
        String result = VersionMigrationHelper.migrateContent(content, LatteVersion.VERSION_3X, LatteVersion.VERSION_4X);
        assertEquals("ifCurrent macro should be migrated to if isLinkCurrent", expected, result);
        
        // Test status macro migration
        content = "{status 404}";
        expected = "{http 404}";
        result = VersionMigrationHelper.migrateContent(content, LatteVersion.VERSION_3X, LatteVersion.VERSION_4X);
        assertEquals("status macro should be migrated to http", expected, result);
    }
    
    /**
     * Tests migration from Latte 2.x to Latte 4.0+.
     */
    @Test
    public void testMigrationFrom2xTo4x() {
        // Test combined migrations
        String content = "{syntax double}\n{ifCurrent Homepage:default}\n{l}variable{r}\n{status 404}\n{/ifCurrent}";
        String expected = "{templateType double}\n{if isLinkCurrent(Homepage:default)}\n{left}variable{right}\n{http 404}\n{/if}";
        String result = VersionMigrationHelper.migrateContent(content, LatteVersion.VERSION_2X, LatteVersion.VERSION_4X);
        assertEquals("All migrations should be applied when going from 2.x to 4.0+", expected, result);
    }
    
    /**
     * Tests getting migration rules.
     */
    @Test
    public void testGetMigrationRules() {
        // Test getting rules from 2.x to 3.x
        List<VersionMigrationHelper.MigrationRule> rules2xTo3x = 
                VersionMigrationHelper.getMigrationRules(LatteVersion.VERSION_2X, LatteVersion.VERSION_3X);
        assertFalse("There should be migration rules from 2.x to 3.x", rules2xTo3x.isEmpty());
        
        // Test getting rules from 3.x to 4.x
        List<VersionMigrationHelper.MigrationRule> rules3xTo4x = 
                VersionMigrationHelper.getMigrationRules(LatteVersion.VERSION_3X, LatteVersion.VERSION_4X);
        assertFalse("There should be migration rules from 3.x to 4.x", rules3xTo4x.isEmpty());
        
        // Test getting rules from 2.x to 4.x
        List<VersionMigrationHelper.MigrationRule> rules2xTo4x = 
                VersionMigrationHelper.getMigrationRules(LatteVersion.VERSION_2X, LatteVersion.VERSION_4X);
        assertFalse("There should be migration rules from 2.x to 4.x", rules2xTo4x.isEmpty());
        assertTrue("There should be more rules from 2.x to 4.x than from 2.x to 3.x", 
                rules2xTo4x.size() > rules2xTo3x.size());
        
        // Test getting rules for same version
        List<VersionMigrationHelper.MigrationRule> rulesSameVersion = 
                VersionMigrationHelper.getMigrationRules(LatteVersion.VERSION_3X, LatteVersion.VERSION_3X);
        assertTrue("There should be no migration rules for the same version", rulesSameVersion.isEmpty());
    }
    
    /**
     * Tests migration with empty or null content.
     */
    @Test
    public void testMigrationWithEmptyContent() {
        // Test with null content
        String result = VersionMigrationHelper.migrateContent(null, LatteVersion.VERSION_2X, LatteVersion.VERSION_3X);
        assertEquals("Migration with null content should return empty string", "", result);
        
        // Test with empty content
        result = VersionMigrationHelper.migrateContent("", LatteVersion.VERSION_2X, LatteVersion.VERSION_3X);
        assertEquals("Migration with empty content should return empty string", "", result);
    }
    
    /**
     * Tests migration with unsupported version pair.
     */
    @Test
    public void testMigrationWithUnsupportedVersionPair() {
        // Create a custom content
        String content = "{syntax double}\nHello World\n{/syntax}";
        
        // Test with unsupported version pair (same version)
        String result = VersionMigrationHelper.migrateContent(content, LatteVersion.VERSION_3X, LatteVersion.VERSION_3X);
        assertEquals("Migration with unsupported version pair should return original content", content, result);
    }
}
