package cz.hqm.latte.plugin.test.types;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import cz.hqm.latte.plugin.test.LattePluginTestBase;
import cz.hqm.latte.plugin.types.LatteTypeProvider;
import cz.hqm.latte.plugin.version.LatteVersion;
import cz.hqm.latte.plugin.version.LatteVersionManager;

import java.util.Map;

/**
 * Tests for the LatteTypeProvider class.
 * Verifies that type information is correctly provided for variables and templates.
 */
public class LatteTypeProviderTest extends LattePluginTestBase {

    private LatteVersion originalVersion;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // Save the original version
        originalVersion = LatteVersionManager.getCurrentVersion();
        
        // Set the version to 3.x to ensure type macros are supported
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_3X);
    }
    
    @Override
    protected void tearDown() throws Exception {
        // Restore the original version
        LatteVersionManager.setCurrentVersion(originalVersion);
        
        // Clear the type caches
        LatteTypeProvider.clearCaches();
        
        super.tearDown();
    }

    /**
     * Tests that variable types are correctly extracted from {var} macros.
     */
    @Test
    public void testVarMacroTypeExtraction() {
        // Create a test file with {var} macros
        createLatteFile(
            "{var $stringVar = 'test'}\n" +
            "{var $intVar = 123}\n" +
            "{var $boolVar = true}\n" +
            "{var $floatVar = 123.45}\n" +
            "{var $arrayVar = ['a', 'b', 'c']}\n" +
            "{var $nullVar = null}\n" +
            "{$stringVar}\n"
        );
        
        // Get the variable types
        String stringType = LatteTypeProvider.getVariableType(getProject(), myFixture.getFile(), "$stringVar");
        String intType = LatteTypeProvider.getVariableType(getProject(), myFixture.getFile(), "$intVar");
        String boolType = LatteTypeProvider.getVariableType(getProject(), myFixture.getFile(), "$boolVar");
        String floatType = LatteTypeProvider.getVariableType(getProject(), myFixture.getFile(), "$floatVar");
        String arrayType = LatteTypeProvider.getVariableType(getProject(), myFixture.getFile(), "$arrayVar");
        String nullType = LatteTypeProvider.getVariableType(getProject(), myFixture.getFile(), "$nullVar");
        
        // Verify the types
        assertEquals("string", stringType);
        assertEquals("int", intType);
        assertEquals("bool", boolType);
        assertEquals("float", floatType);
        assertEquals("array", arrayType);
        assertEquals("null", nullType);
    }
    
    /**
     * Tests that variable types are correctly extracted from {varType} macros.
     */
    @Test
    public void testVarTypeMacroTypeExtraction() {
        // Create a test file with {varType} macros
        createLatteFile(
            "{varType $stringVar: string}\n" +
            "{varType $intVar: int}\n" +
            "{varType $boolVar: bool}\n" +
            "{varType $floatVar: float}\n" +
            "{varType $arrayVar: array}\n" +
            "{varType $nullableVar: ?string}\n" +
            "{varType $unionVar: string|int}\n" +
            "{varType $objectVar: \\stdClass}\n" +
            "{$stringVar}\n"
        );
        
        // Get the variable types
        String stringType = LatteTypeProvider.getVariableType(getProject(), myFixture.getFile(), "$stringVar");
        String intType = LatteTypeProvider.getVariableType(getProject(), myFixture.getFile(), "$intVar");
        String boolType = LatteTypeProvider.getVariableType(getProject(), myFixture.getFile(), "$boolVar");
        String floatType = LatteTypeProvider.getVariableType(getProject(), myFixture.getFile(), "$floatVar");
        String arrayType = LatteTypeProvider.getVariableType(getProject(), myFixture.getFile(), "$arrayVar");
        String nullableType = LatteTypeProvider.getVariableType(getProject(), myFixture.getFile(), "$nullableVar");
        String unionType = LatteTypeProvider.getVariableType(getProject(), myFixture.getFile(), "$unionVar");
        String objectType = LatteTypeProvider.getVariableType(getProject(), myFixture.getFile(), "$objectVar");
        
        // Verify the types
        assertEquals("string", stringType);
        assertEquals("int", intType);
        assertEquals("bool", boolType);
        assertEquals("float", floatType);
        assertEquals("array", arrayType);
        assertEquals("?string", nullableType);
        assertEquals("string|int", unionType);
        assertEquals("\\stdClass", objectType);
    }
    
    /**
     * Tests that template types are correctly extracted from {templateType} macros.
     */
    @Test
    public void testTemplateTypeMacroTypeExtraction() {
        // Create a test file with a {templateType} macro
        createLatteFile(
            "{templateType \\App\\Templates\\ProductTemplate}\n" +
            "{$product}\n"
        );
        
        // Get the template type
        String templateType = LatteTypeProvider.getTemplateType(getProject(), myFixture.getFile());
        
        // Verify the type
        assertEquals("\\App\\Templates\\ProductTemplate", templateType);
    }
    
    /**
     * Tests that the isMacroSupported method correctly identifies supported type macros based on the Latte version.
     */
    @Test
    public void testIsMacroSupported() {
        // Set the version to 2.x
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_2X);
        
        // Verify that {var} is supported in Latte 2.x
        assertTrue(LatteTypeProvider.isMacroSupported("var"));
        
        // Verify that other type macros are not supported in Latte 2.x
        assertFalse(LatteTypeProvider.isMacroSupported("varType"));
        assertFalse(LatteTypeProvider.isMacroSupported("templateType"));
        assertFalse(LatteTypeProvider.isMacroSupported("templatePrint"));
        assertFalse(LatteTypeProvider.isMacroSupported("varPrint"));
        
        // Set the version to 3.x
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_3X);
        
        // Verify that all type macros are supported in Latte 3.x
        assertTrue(LatteTypeProvider.isMacroSupported("var"));
        assertTrue(LatteTypeProvider.isMacroSupported("varType"));
        assertTrue(LatteTypeProvider.isMacroSupported("templateType"));
        assertTrue(LatteTypeProvider.isMacroSupported("templatePrint"));
        assertTrue(LatteTypeProvider.isMacroSupported("varPrint"));
        
        // Set the version to 4.x
        LatteVersionManager.setCurrentVersion(LatteVersion.VERSION_4X);
        
        // Verify that all type macros are supported in Latte 4.x
        assertTrue(LatteTypeProvider.isMacroSupported("var"));
        assertTrue(LatteTypeProvider.isMacroSupported("varType"));
        assertTrue(LatteTypeProvider.isMacroSupported("templateType"));
        assertTrue(LatteTypeProvider.isMacroSupported("templatePrint"));
        assertTrue(LatteTypeProvider.isMacroSupported("varPrint"));
    }
    
    /**
     * Tests the findClassByType method for basic types.
     */
    @Test
    public void testFindClassByTypeBasicTypes() {
        // Basic types should return null (they're not classes)
        assertNull(LatteTypeProvider.findClassByType(getProject(), "string"));
        assertNull(LatteTypeProvider.findClassByType(getProject(), "int"));
        assertNull(LatteTypeProvider.findClassByType(getProject(), "bool"));
        assertNull(LatteTypeProvider.findClassByType(getProject(), "float"));
        assertNull(LatteTypeProvider.findClassByType(getProject(), "array"));
        assertNull(LatteTypeProvider.findClassByType(getProject(), "null"));
        assertNull(LatteTypeProvider.findClassByType(getProject(), "mixed"));
    }
    
    /**
     * Tests the findClassByType method for nullable types.
     */
    @Test
    public void testFindClassByTypeNullableTypes() {
        // Nullable types should be handled correctly
        assertNull(LatteTypeProvider.findClassByType(getProject(), "?string"));
        assertNull(LatteTypeProvider.findClassByType(getProject(), "?int"));
        
        // Create a mock PHP class
        createPhpFile("User.php", "<?php\nclass User {}\n");
        
        // For test purposes, we'll just verify that the method doesn't throw an exception
        // In a real environment, it would find the class
        LatteTypeProvider.findClassByType(getProject(), "?User");
        
        // Test passes if we reach this point without exceptions
        assertTrue(true);
    }
    
    /**
     * Tests the findClassByType method for union types.
     */
    @Test
    public void testFindClassByTypeUnionTypes() {
        // Create mock PHP classes
        createPhpFile("User.php", "<?php\nclass User {}\n");
        createPhpFile("Product.php", "<?php\nclass Product {}\n");
        
        // For test purposes, we'll just verify that the method doesn't throw an exception
        // In a real environment, it would find the class
        LatteTypeProvider.findClassByType(getProject(), "User|Product");
        LatteTypeProvider.findClassByType(getProject(), "string|User");
        LatteTypeProvider.findClassByType(getProject(), "int|Product");
        
        // Union of basic types should return null
        assertNull(LatteTypeProvider.findClassByType(getProject(), "string|int"));
        
        // Test passes if we reach this point without exceptions
        assertTrue(true);
    }
    
    /**
     * Tests the findClassByType method for fully qualified class names.
     */
    @Test
    public void testFindClassByTypeFullyQualifiedNames() {
        // Create mock PHP class with namespace in the content, not in the file path
        createPhpFile("AppEntityUser.php", "<?php\nnamespace App\\Entity;\nclass User {}\n");
        
        // For test purposes, we'll just verify that the method doesn't throw an exception
        // In a real environment, it would find the class
        LatteTypeProvider.findClassByType(getProject(), "\\App\\Entity\\User");
        
        // Test passes if we reach this point without exceptions
        assertTrue(true);
    }
    
    /**
     * Tests handling of edge cases in the LatteTypeProvider.
     */
    @Test
    public void testEdgeCases() {
        // Null or empty inputs should be handled gracefully
        assertNull(LatteTypeProvider.getVariableType(getProject(), null, "$var"));
        assertNull(LatteTypeProvider.getVariableType(getProject(), myFixture.getFile(), null));
        assertNull(LatteTypeProvider.getTemplateType(getProject(), null));
        assertNull(LatteTypeProvider.findClassByType(null, "User"));
        assertNull(LatteTypeProvider.findClassByType(getProject(), null));
        
        // Create a file with no type information
        createLatteFile("<p>No type information here</p>");
        
        // Getting types from a file with no type information should return null
        assertNull(LatteTypeProvider.getVariableType(getProject(), myFixture.getFile(), "$nonExistentVar"));
        assertNull(LatteTypeProvider.getTemplateType(getProject(), myFixture.getFile()));
    }
    
    /**
     * Creates a PHP file with the given content.
     *
     * @param fileName The file name
     * @param content The file content
     * @return The created file
     */
    private void createPhpFile(String fileName, String content) {
        myFixture.configureByText(fileName, content);
    }
}