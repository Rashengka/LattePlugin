package org.latte.plugin.test.file;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.latte.plugin.file.LatteFileType;

/**
 * Tests for Latte file type recognition.
 */
public class LatteFileTypeTest extends BasePlatformTestCase {

    /**
     * Tests that files with .latte extension are recognized as Latte files.
     */
    public void testLatteFileTypeRecognition() {
        FileType fileType = FileTypeManager.getInstance().getFileTypeByExtension("latte");
        assertEquals("Latte file type should be recognized by .latte extension", 
                     LatteFileType.INSTANCE, fileType);
    }
    
    /**
     * Tests that the Latte file type has the correct properties.
     */
    public void testLatteFileTypeProperties() {
        assertEquals("Latte", LatteFileType.INSTANCE.getName());
        assertEquals("Latte template file", LatteFileType.INSTANCE.getDescription());
        assertEquals("latte", LatteFileType.INSTANCE.getDefaultExtension());
    }
    
    /**
     * Tests that a file with .latte extension can be created and is recognized as a Latte file.
     */
    public void testCreateLatteFile() {
        myFixture.configureByText("test.latte", "{* Latte comment *}\n<p>{$variable}</p>");
        assertEquals("File should be recognized as Latte file",
                     LatteFileType.INSTANCE, myFixture.getFile().getFileType());
    }
}