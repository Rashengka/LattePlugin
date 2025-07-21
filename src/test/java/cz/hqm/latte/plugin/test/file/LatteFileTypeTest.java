package cz.hqm.latte.plugin.test.file;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import cz.hqm.latte.plugin.test.LattePluginTestBase;
import cz.hqm.latte.plugin.file.LatteFileType;

/**
 * Tests for Latte file type recognition.
 */
public class LatteFileTypeTest extends LattePluginTestBase {

    /**
     * Tests that files with .latte extension are recognized as Latte files.
     */
    @Test
    public void testLatteFileTypeRecognition() {
        FileType fileType = FileTypeManager.getInstance().getFileTypeByExtension("latte");
        assertEquals("Latte file type should be recognized by .latte extension", 
                     LatteFileType.INSTANCE, fileType);
    }
    
    /**
     * Tests that the Latte file type has the correct properties.
     */
    @Test
    public void testLatteFileTypeProperties() {
        assertEquals("Latte", LatteFileType.INSTANCE.getName());
        assertEquals("Latte template file", LatteFileType.INSTANCE.getDescription());
        assertEquals("latte", LatteFileType.INSTANCE.getDefaultExtension());
    }
    
    /**
     * Tests that a file with .latte extension can be created and is recognized as a Latte file.
     */
    @Test
    public void testCreateLatteFile() {
        myFixture.configureByText("test.latte", "{* Latte comment *}\n<p>{$variable}</p>");
        assertEquals("File should be recognized as Latte file",
                     LatteFileType.INSTANCE, myFixture.getFile().getFileType());
    }
}
