package cz.hqm.latte.plugin.test.version;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import cz.hqm.latte.plugin.test.LattePluginTestBase;
import cz.hqm.latte.plugin.version.DeprecatedFeatureDetector;
import cz.hqm.latte.plugin.version.LatteVersion;
import cz.hqm.latte.plugin.version.LatteVersionManager;

import java.util.List;

/**
 * Tests for the DeprecatedFeatureDetector class.
 */
public class DeprecatedFeatureDetectorTest extends LattePluginTestBase {

    // Save the original version to restore after tests
    private LatteVersion originalVersion;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        originalVersion = LatteVersionManager.getCurrentVersion();
    }

    @Override
    protected void tearDown() throws Exception {
        // Restore the original version
        LatteVersionManager.setCurrentVersion(originalVersion);
        super.tearDown();
    }

    /**
     * Tests detection of features deprecated in Latte 3.0+.
     */
    @Test
    public void testDetectDeprecatedFeaturesIn3x() {
        // Set current version to 3.0+
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_3X);
        
        // Test content with deprecated features
        String content = "{syntax double}\nThis is a {l}variable{r} in Latte template.\n{/syntax}";
        
        // Detect deprecated features
        List<DeprecatedFeatureDetector.DeprecatedFeatureWarning> warnings = 
                DeprecatedFeatureDetector.detectDeprecatedFeatures(content);
        
        // Verify that warnings were detected
        assertFalse("Deprecated features should be detected", warnings.isEmpty());
        assertEquals("Three deprecated features should be detected", 3, warnings.size());
        
        // Verify the specific warnings
        boolean foundSyntax = false;
        boolean foundL = false;
        boolean foundR = false;
        
        for (DeprecatedFeatureDetector.DeprecatedFeatureWarning warning : warnings) {
            if (warning.getText().contains("syntax")) {
                foundSyntax = true;
                assertTrue("Warning message should mention templateType", 
                        warning.getMessage().contains("templateType"));
            } else if (warning.getText().equals("{l}")) {
                foundL = true;
                assertTrue("Warning message should mention left", 
                        warning.getMessage().contains("left"));
            } else if (warning.getText().equals("{r}")) {
                foundR = true;
                assertTrue("Warning message should mention right", 
                        warning.getMessage().contains("right"));
            }
        }
        
        assertTrue("Warning for syntax macro should be detected", foundSyntax);
        assertTrue("Warning for l macro should be detected", foundL);
        assertTrue("Warning for r macro should be detected", foundR);
    }
    
    /**
     * Tests detection of features deprecated in Latte 4.0+.
     */
    @Test
    public void testDetectDeprecatedFeaturesIn4x() {
        // Set current version to 4.0+
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_4X);
        
        // Test content with deprecated features
        String content = "{ifCurrent Homepage:default}\nActive\n{/ifCurrent}\n{status 404}";
        
        // Detect deprecated features
        List<DeprecatedFeatureDetector.DeprecatedFeatureWarning> warnings = 
                DeprecatedFeatureDetector.detectDeprecatedFeatures(content);
        
        // Verify that warnings were detected
        assertFalse("Deprecated features should be detected", warnings.isEmpty());
        assertEquals("Two deprecated features should be detected", 2, warnings.size());
        
        // Verify the specific warnings
        boolean foundIfCurrent = false;
        boolean foundStatus = false;
        
        for (DeprecatedFeatureDetector.DeprecatedFeatureWarning warning : warnings) {
            if (warning.getText().contains("ifCurrent")) {
                foundIfCurrent = true;
                assertTrue("Warning message should mention isLinkCurrent", 
                        warning.getMessage().contains("isLinkCurrent"));
            } else if (warning.getText().contains("status")) {
                foundStatus = true;
                assertTrue("Warning message should mention http", 
                        warning.getMessage().contains("http"));
            }
        }
        
        assertTrue("Warning for ifCurrent macro should be detected", foundIfCurrent);
        assertTrue("Warning for status macro should be detected", foundStatus);
    }
    
    /**
     * Tests that features deprecated in Latte 3.0+ are also detected in Latte 4.0+.
     */
    @Test
    public void testDetectLatte3xDeprecatedFeaturesIn4x() {
        // Set current version to 4.0+
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_4X);
        
        // Test content with features deprecated in Latte 3.0+
        String content = "{syntax double}\nThis is a {l}variable{r} in Latte template.\n{/syntax}";
        
        // Detect deprecated features
        List<DeprecatedFeatureDetector.DeprecatedFeatureWarning> warnings = 
                DeprecatedFeatureDetector.detectDeprecatedFeatures(content);
        
        // Verify that warnings were detected
        assertFalse("Deprecated features should be detected", warnings.isEmpty());
        assertEquals("Three deprecated features should be detected", 3, warnings.size());
    }
    
    /**
     * Tests that no warnings are generated for non-deprecated features.
     */
    @Test
    public void testNoWarningsForNonDeprecatedFeatures() {
        // Set current version to 3.0+
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_3X);
        
        // Test content with no deprecated features
        String content = "{templateType double}\nThis is a {left}variable{right} in Latte template.\n{/templateType}";
        
        // Detect deprecated features
        List<DeprecatedFeatureDetector.DeprecatedFeatureWarning> warnings = 
                DeprecatedFeatureDetector.detectDeprecatedFeatures(content);
        
        // Verify that no warnings were detected
        assertTrue("No deprecated features should be detected", warnings.isEmpty());
    }
    
    /**
     * Tests detection with empty or null content.
     */
    @Test
    public void testDetectionWithEmptyContent() {
        // Set current version to 3.0+
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_3X);
        
        // Test with null content
        List<DeprecatedFeatureDetector.DeprecatedFeatureWarning> warnings = 
                DeprecatedFeatureDetector.detectDeprecatedFeatures(null);
        assertTrue("No warnings should be detected for null content", warnings.isEmpty());
        
        // Test with empty content
        warnings = DeprecatedFeatureDetector.detectDeprecatedFeatures("");
        assertTrue("No warnings should be detected for empty content", warnings.isEmpty());
    }
    
    /**
     * Tests that warning positions are correct.
     */
    @Test
    public void testWarningPositions() {
        // Set current version to 3.0+
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_3X);
        
        // Test content with deprecated features
        String content = "Start {syntax double} Middle {l}variable{r} End";
        
        // Detect deprecated features
        List<DeprecatedFeatureDetector.DeprecatedFeatureWarning> warnings = 
                DeprecatedFeatureDetector.detectDeprecatedFeatures(content);
        
        // Verify that warnings were detected
        assertFalse("Deprecated features should be detected", warnings.isEmpty());
        assertEquals("Three deprecated features should be detected", 3, warnings.size());
        
        // Verify the positions
        for (DeprecatedFeatureDetector.DeprecatedFeatureWarning warning : warnings) {
            if (warning.getText().contains("syntax")) {
                assertEquals("Syntax macro should start at position 6", 6, warning.getStartOffset());
                assertEquals("Syntax macro should end at position 21", 21, warning.getEndOffset());
            } else if (warning.getText().equals("{l}")) {
                assertEquals("l macro should start at position 29", 29, warning.getStartOffset());
                assertEquals("l macro should end at position 32", 32, warning.getEndOffset());
            } else if (warning.getText().equals("{r}")) {
                assertEquals("r macro should start at position 40", 40, warning.getStartOffset());
                assertEquals("r macro should end at position 43", 43, warning.getEndOffset());
            }
        }
    }
}
