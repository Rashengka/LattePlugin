package cz.hqm.latte.plugin.test.version;

import org.junit.Test;
import cz.hqm.latte.plugin.test.LattePluginTestBase;
import cz.hqm.latte.plugin.version.LatteVersion;
import com.intellij.openapi.application.PathManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Tests for the LatteVersion class, particularly the version detection functionality.
 */
public class LatteVersionTest extends LattePluginTestBase {

    private String latte2xContent;
    private String latte3xContent;
    private String latte4xContent;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // Load test files
        latte2xContent = loadTestFile("latte2x_features.latte");
        latte3xContent = loadTestFile("latte3x_features.latte");
        latte4xContent = loadTestFile("latte4x_features.latte");
    }

    /**
     * Tests detection of Latte 2.x version from content.
     */
    @Test
    public void testDetectVersion2x() {
        LatteVersion version = LatteVersion.detectVersionFromContent(latte2xContent);
        assertEquals("Should detect Latte 2.x", LatteVersion.VERSION_2X, version);
    }

    /**
     * Tests detection of Latte 3.0+ version from content.
     */
    @Test
    public void testDetectVersion3x() {
        LatteVersion version = LatteVersion.detectVersionFromContent(latte3xContent);
        assertEquals("Should detect Latte 3.0+", LatteVersion.VERSION_3X, version);
    }

    /**
     * Tests detection of Latte 4.0+ version from content with version comment.
     */
    @Test
    public void testDetectVersion4xFromComment() {
        LatteVersion version = LatteVersion.detectVersionFromContent("{* Latte 4.0+ *}\n<p>Test</p>");
        assertEquals("Should detect Latte 4.0+ from comment", LatteVersion.VERSION_4X, version);
    }

    /**
     * Tests detection of Latte 4.0+ version from content with specific syntax.
     */
    @Test
    public void testDetectVersion4xFromSyntax() {
        LatteVersion version = LatteVersion.detectVersionFromContent("<p>{typeCheck}</p>");
        assertEquals("Should detect Latte 4.0+ from typeCheck macro", LatteVersion.VERSION_4X, version);
        
        version = LatteVersion.detectVersionFromContent("<p>{strictTypes}</p>");
        assertEquals("Should detect Latte 4.0+ from strictTypes macro", LatteVersion.VERSION_4X, version);
    }

    /**
     * Tests detection of Latte 4.0+ version from the test file.
     */
    @Test
    public void testDetectVersion4xFromTestFile() {
        LatteVersion version = LatteVersion.detectVersionFromContent(latte4xContent);
        assertEquals("Should detect Latte 4.0+ from test file", LatteVersion.VERSION_4X, version);
    }

    /**
     * Tests handling of null or empty content.
     */
    @Test
    public void testDetectVersionWithNullOrEmpty() {
        LatteVersion version = LatteVersion.detectVersionFromContent(null);
        assertNull("Should return null for null content", version);
        
        version = LatteVersion.detectVersionFromContent("");
        assertNull("Should return null for empty content", version);
    }

    /**
     * Tests handling of content with no version indicators.
     */
    @Test
    public void testDetectVersionWithNoIndicators() {
        LatteVersion version = LatteVersion.detectVersionFromContent("<p>Hello, world!</p>");
        assertNull("Should return null for content with no version indicators", version);
    }

    /**
     * Loads a test file from the testData/version directory.
     *
     * @param filename The name of the file to load
     * @return The content of the file as a string
     * @throws IOException If the file cannot be read
     */
    private String loadTestFile(String filename) throws IOException {
        String testDataPath = getTestDataPath() + "/version";
        return new String(Files.readAllBytes(Paths.get(testDataPath, filename)));
    }
}
