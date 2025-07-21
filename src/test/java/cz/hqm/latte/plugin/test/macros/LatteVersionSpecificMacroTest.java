package cz.hqm.latte.plugin.test.macros;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import cz.hqm.latte.plugin.test.LattePluginTestBase;
import cz.hqm.latte.plugin.macros.LatteVersionSpecificMacroProvider;
import cz.hqm.latte.plugin.macros.NetteMacro;
import cz.hqm.latte.plugin.version.LatteVersion;
import cz.hqm.latte.plugin.version.LatteVersionManager;

import java.util.Set;

/**
 * Tests for the LatteVersionSpecificMacroProvider class.
 * Verifies that version-specific macros and attributes are correctly provided based on the Latte version.
 */
public class LatteVersionSpecificMacroTest extends LattePluginTestBase {

    private LatteVersion originalVersion;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // Save the original version
        originalVersion = LatteVersionManager.getCurrentVersion();
    }
    
    @Override
    protected void tearDown() throws Exception {
        // Restore the original version
        LatteVersionManager.setCurrentVersion(originalVersion);
        
        super.tearDown();
    }

    /**
     * Tests that Latte 2.x specific macros are provided when the version is set to 2.x.
     */
    @Test
    public void testLatte2xMacros() {
        // Set the version to 2.x
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_2X);
        
        // Get all macros for the current version
        Set<NetteMacro> macros = LatteVersionSpecificMacroProvider.getAllMacrosForCurrentVersion();
        
        // Verify that Latte 2.x specific macros are included
        assertContainsMacro(macros, "syntax");
        assertContainsMacro(macros, "l");
        assertContainsMacro(macros, "r");
        assertContainsMacro(macros, "use");
        
        // Verify that Latte 3.x+ specific macros are not included
        assertNotContainsMacro(macros, "switch");
        assertNotContainsMacro(macros, "case");
        assertNotContainsMacro(macros, "default");
        assertNotContainsMacro(macros, "rollback");
    }
    
    /**
     * Tests that Latte 3.x+ specific macros are provided when the version is set to 3.x.
     */
    @Test
    public void testLatte3xMacros() {
        // Set the version to 3.x
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_3X);
        
        // Get all macros for the current version
        Set<NetteMacro> macros = LatteVersionSpecificMacroProvider.getAllMacrosForCurrentVersion();
        
        // Verify that Latte 3.x+ specific macros are included
        assertContainsMacro(macros, "switch");
        assertContainsMacro(macros, "case");
        assertContainsMacro(macros, "default");
        assertContainsMacro(macros, "rollback");
        
        // Verify that Latte 2.x specific macros are not included
        assertNotContainsMacro(macros, "syntax");
        assertNotContainsMacro(macros, "l");
        assertNotContainsMacro(macros, "r");
        assertNotContainsMacro(macros, "use");
    }
    
    /**
     * Tests that n-attributes are provided regardless of the Latte version.
     */
    @Test
    public void testNAttributes() {
        // Set the version to 2.x
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_2X);
        
        // Get all macros for the current version
        Set<NetteMacro> macros = LatteVersionSpecificMacroProvider.getAllMacrosForCurrentVersion();
        
        // Verify that n-attributes are included
        assertContainsMacro(macros, "n:if");
        assertContainsMacro(macros, "n:foreach");
        assertContainsMacro(macros, "n:class");
        assertContainsMacro(macros, "n:attr");
        
        // Set the version to 3.x
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_3X);
        
        // Get all macros for the current version
        macros = LatteVersionSpecificMacroProvider.getAllMacrosForCurrentVersion();
        
        // Verify that n-attributes are still included
        assertContainsMacro(macros, "n:if");
        assertContainsMacro(macros, "n:foreach");
        assertContainsMacro(macros, "n:class");
        assertContainsMacro(macros, "n:attr");
    }
    
    /**
     * Tests that e-macros are provided regardless of the Latte version.
     */
    @Test
    public void testEMacros() {
        // Set the version to 2.x
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_2X);
        
        // Get all macros for the current version
        Set<NetteMacro> macros = LatteVersionSpecificMacroProvider.getAllMacrosForCurrentVersion();
        
        // Verify that e-macros are included
        assertContainsMacro(macros, "_");
        assertContainsMacro(macros, "translate");
        assertContainsMacro(macros, "noescape");
        assertContainsMacro(macros, "nocheck");
        
        // Set the version to 3.x
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_3X);
        
        // Get all macros for the current version
        macros = LatteVersionSpecificMacroProvider.getAllMacrosForCurrentVersion();
        
        // Verify that e-macros are still included
        assertContainsMacro(macros, "_");
        assertContainsMacro(macros, "translate");
        assertContainsMacro(macros, "noescape");
        assertContainsMacro(macros, "nocheck");
    }
    
    /**
     * Tests that form macros are provided regardless of the Latte version.
     */
    @Test
    public void testFormMacros() {
        // Set the version to 2.x
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_2X);
        
        // Get all macros for the current version
        Set<NetteMacro> macros = LatteVersionSpecificMacroProvider.getAllMacrosForCurrentVersion();
        
        // Verify that form macros are included
        assertContainsMacro(macros, "form");
        assertContainsMacro(macros, "input");
        assertContainsMacro(macros, "label");
        assertContainsMacro(macros, "formContainer");
        
        // Set the version to 3.x
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_3X);
        
        // Get all macros for the current version
        macros = LatteVersionSpecificMacroProvider.getAllMacrosForCurrentVersion();
        
        // Verify that form macros are still included
        assertContainsMacro(macros, "form");
        assertContainsMacro(macros, "input");
        assertContainsMacro(macros, "label");
        assertContainsMacro(macros, "formContainer");
    }
    
    /**
     * Tests that database macros are provided regardless of the Latte version.
     */
    @Test
    public void testDatabaseMacros() {
        // Set the version to 2.x
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_2X);
        
        // Get all macros for the current version
        Set<NetteMacro> macros = LatteVersionSpecificMacroProvider.getAllMacrosForCurrentVersion();
        
        // Verify that database macros are included
        assertContainsMacro(macros, "query");
        
        // Set the version to 3.x
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_3X);
        
        // Get all macros for the current version
        macros = LatteVersionSpecificMacroProvider.getAllMacrosForCurrentVersion();
        
        // Verify that database macros are still included
        assertContainsMacro(macros, "query");
    }
    
    /**
     * Tests that Latte 4.x+ specific macros are provided when the version is set to 4.x.
     */
    @Test
    public void testLatte4xMacros() {
        // Set the version to 4.x
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_4X);
        
        // Get all macros for the current version
        Set<NetteMacro> macros = LatteVersionSpecificMacroProvider.getAllMacrosForCurrentVersion();
        
        // Verify that Latte 3.x+ specific macros are included (4.x includes 3.x macros)
        assertContainsMacro(macros, "switch");
        assertContainsMacro(macros, "case");
        assertContainsMacro(macros, "default");
        assertContainsMacro(macros, "rollback");
        
        // Verify that Latte 2.x specific macros are not included
        assertNotContainsMacro(macros, "syntax");
        assertNotContainsMacro(macros, "l");
        assertNotContainsMacro(macros, "r");
        assertNotContainsMacro(macros, "use");
    }
    
    /**
     * Tests that the macro set is correctly updated when switching between versions.
     */
    @Test
    public void testVersionSwitching() {
        // Start with version 2.x
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_2X);
        
        // Get macros for 2.x
        Set<NetteMacro> macros2x = LatteVersionSpecificMacroProvider.getAllMacrosForCurrentVersion();
        
        // Verify 2.x specific macros are included
        assertContainsMacro(macros2x, "syntax");
        assertContainsMacro(macros2x, "l");
        assertContainsMacro(macros2x, "r");
        
        // Switch to version 3.x
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_3X);
        
        // Get macros for 3.x
        Set<NetteMacro> macros3x = LatteVersionSpecificMacroProvider.getAllMacrosForCurrentVersion();
        
        // Verify 3.x specific macros are included
        assertContainsMacro(macros3x, "switch");
        assertContainsMacro(macros3x, "case");
        assertContainsMacro(macros3x, "default");
        
        // Verify 2.x specific macros are not included
        assertNotContainsMacro(macros3x, "syntax");
        assertNotContainsMacro(macros3x, "l");
        assertNotContainsMacro(macros3x, "r");
        
        // Switch to version 4.x
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_4X);
        
        // Get macros for 4.x
        Set<NetteMacro> macros4x = LatteVersionSpecificMacroProvider.getAllMacrosForCurrentVersion();
        
        // Verify 3.x specific macros are still included (4.x includes 3.x macros)
        assertContainsMacro(macros4x, "switch");
        assertContainsMacro(macros4x, "case");
        assertContainsMacro(macros4x, "default");
        
        // Verify 2.x specific macros are not included
        assertNotContainsMacro(macros4x, "syntax");
        assertNotContainsMacro(macros4x, "l");
        assertNotContainsMacro(macros4x, "r");
        
        // Switch back to version 2.x
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_2X);
        
        // Get macros for 2.x again
        Set<NetteMacro> macros2xAgain = LatteVersionSpecificMacroProvider.getAllMacrosForCurrentVersion();
        
        // Verify 2.x specific macros are included again
        assertContainsMacro(macros2xAgain, "syntax");
        assertContainsMacro(macros2xAgain, "l");
        assertContainsMacro(macros2xAgain, "r");
        
        // Verify 3.x specific macros are not included
        assertNotContainsMacro(macros2xAgain, "switch");
        assertNotContainsMacro(macros2xAgain, "case");
        assertNotContainsMacro(macros2xAgain, "default");
    }

    /**
     * Tests that the isMacroSupported method correctly identifies supported macros based on the Latte version.
     */
    @Test
    public void testIsMacroSupported() {
        // Set the version to 2.x
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_2X);
        
        // Verify that Latte 2.x specific macros are supported
        assertTrue(LatteVersionSpecificMacroProvider.isMacroSupported("syntax"));
        assertTrue(LatteVersionSpecificMacroProvider.isMacroSupported("l"));
        assertTrue(LatteVersionSpecificMacroProvider.isMacroSupported("r"));
        assertTrue(LatteVersionSpecificMacroProvider.isMacroSupported("use"));
        
        // Verify that Latte 3.x+ specific macros are not supported
        assertFalse(LatteVersionSpecificMacroProvider.isMacroSupported("switch"));
        assertFalse(LatteVersionSpecificMacroProvider.isMacroSupported("case"));
        assertFalse(LatteVersionSpecificMacroProvider.isMacroSupported("default"));
        assertFalse(LatteVersionSpecificMacroProvider.isMacroSupported("rollback"));
        
        // Set the version to 3.x
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_3X);
        
        // Verify that Latte 3.x+ specific macros are supported
        assertTrue(LatteVersionSpecificMacroProvider.isMacroSupported("switch"));
        assertTrue(LatteVersionSpecificMacroProvider.isMacroSupported("case"));
        assertTrue(LatteVersionSpecificMacroProvider.isMacroSupported("default"));
        assertTrue(LatteVersionSpecificMacroProvider.isMacroSupported("rollback"));
        
        // Verify that Latte 2.x specific macros are not supported
        assertFalse(LatteVersionSpecificMacroProvider.isMacroSupported("syntax"));
        assertFalse(LatteVersionSpecificMacroProvider.isMacroSupported("l"));
        assertFalse(LatteVersionSpecificMacroProvider.isMacroSupported("r"));
        assertFalse(LatteVersionSpecificMacroProvider.isMacroSupported("use"));
        
        // Set the version to 4.x
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_4X);
        
        // Verify that Latte 3.x+ specific macros are supported in 4.x
        assertTrue(LatteVersionSpecificMacroProvider.isMacroSupported("switch"));
        assertTrue(LatteVersionSpecificMacroProvider.isMacroSupported("case"));
        assertTrue(LatteVersionSpecificMacroProvider.isMacroSupported("default"));
        assertTrue(LatteVersionSpecificMacroProvider.isMacroSupported("rollback"));
        
        // Verify that Latte 2.x specific macros are not supported in 4.x
        assertFalse(LatteVersionSpecificMacroProvider.isMacroSupported("syntax"));
        assertFalse(LatteVersionSpecificMacroProvider.isMacroSupported("l"));
        assertFalse(LatteVersionSpecificMacroProvider.isMacroSupported("r"));
        assertFalse(LatteVersionSpecificMacroProvider.isMacroSupported("use"));
        
        // Verify that common macros are supported in all versions
        assertTrue(LatteVersionSpecificMacroProvider.isMacroSupported("n:if"));
        assertTrue(LatteVersionSpecificMacroProvider.isMacroSupported("n:foreach"));
        assertTrue(LatteVersionSpecificMacroProvider.isMacroSupported("_"));
        assertTrue(LatteVersionSpecificMacroProvider.isMacroSupported("form"));
        assertTrue(LatteVersionSpecificMacroProvider.isMacroSupported("query"));
    }
    
    /**
     * Asserts that the given set of macros contains a macro with the specified name.
     *
     * @param macros The set of macros
     * @param name The macro name
     */
    private void assertContainsMacro(Set<NetteMacro> macros, String name) {
        boolean found = false;
        for (NetteMacro macro : macros) {
            if (macro.getName().equals(name)) {
                found = true;
                break;
            }
        }
        assertTrue("Macros should contain '" + name + "'", found);
    }
    
    /**
     * Asserts that the given set of macros does not contain a macro with the specified name.
     *
     * @param macros The set of macros
     * @param name The macro name
     */
    private void assertNotContainsMacro(Set<NetteMacro> macros, String name) {
        boolean found = false;
        for (NetteMacro macro : macros) {
            if (macro.getName().equals(name)) {
                found = true;
                break;
            }
        }
        assertFalse("Macros should not contain '" + name + "'", found);
    }
}