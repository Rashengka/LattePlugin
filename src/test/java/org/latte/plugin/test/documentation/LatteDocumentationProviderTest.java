package org.latte.plugin.test.documentation;

import com.intellij.psi.PsiElement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.latte.plugin.documentation.LatteDocumentationProvider;
import org.latte.plugin.macros.NetteMacro;
import org.latte.plugin.macros.NetteMacroProvider;
import org.latte.plugin.test.LattePluginTestBase;

/**
 * Tests for the LatteDocumentationProvider class.
 */
public class LatteDocumentationProviderTest extends LattePluginTestBase {
    
    private LatteDocumentationProvider documentationProvider;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        documentationProvider = new LatteDocumentationProvider();
    }
    
    /**
     * Tests that documentation is provided for built-in Latte macros.
     */
    @Test
    public void testBuiltInMacroDocumentation() {
        // Create a Latte file with an if macro
        myFixture.configureByText("test.latte", "{if $condition}content{/if}");
        
        // Get the PsiElement at the if macro
        PsiElement element = myFixture.getFile().findElementAt(1); // Position of 'i' in 'if'
        
        // Debug output
        System.out.println("[DEBUG_LOG] Element at position 1: " + (element != null ? element.getText() : "null"));
        System.out.println("[DEBUG_LOG] Element class: " + (element != null ? element.getClass().getName() : "null"));
        
        // Get documentation for the element
        String documentation = documentationProvider.generateDoc(element, null);
        
        // Debug output
        System.out.println("[DEBUG_LOG] Documentation: " + documentation);
        
        // Verify documentation
        assertNotNull("Documentation should not be null", documentation);
        assertTrue("Documentation should contain 'Latte Macro: if'", documentation.contains("Latte Macro: if"));
        assertTrue("Documentation should contain 'Conditional statement'", documentation.contains("Conditional statement"));
    }
    
    /**
     * Tests that documentation is provided for built-in Latte attributes.
     */
    @Test
    public void testBuiltInAttributeDocumentation() {
        // Create a Latte file with an n:if attribute
        myFixture.configureByText("test.latte", "<div n:if=\"$condition\">content</div>");
        
        // Get the PsiElement at the n:if attribute
        PsiElement element = myFixture.getFile().findElementAt(5); // Position of 'i' in 'n:if'
        
        // Debug output
        System.out.println("[DEBUG_LOG] Element at position 5: " + (element != null ? element.getText() : "null"));
        System.out.println("[DEBUG_LOG] Element class: " + (element != null ? element.getClass().getName() : "null"));
        
        // Get documentation for the element
        String documentation = documentationProvider.generateDoc(element, null);
        
        // Debug output
        System.out.println("[DEBUG_LOG] Documentation: " + documentation);
        
        // Verify documentation
        assertNotNull("Documentation should not be null", documentation);
        assertTrue("Documentation should contain 'Latte Attribute: n:if'", documentation.contains("Latte Attribute: n:if"));
        assertTrue("Documentation should contain 'Conditional rendering'", documentation.contains("Conditional rendering"));
    }
    
    /**
     * Tests that documentation is provided for built-in Latte filters.
     */
    @Test
    public void testBuiltInFilterDocumentation() {
        // Create a Latte file with an upper filter
        myFixture.configureByText("test.latte", "{$var|upper}");
        
        // Get the PsiElement at the upper filter
        PsiElement element = myFixture.getFile().findElementAt(6); // Position of 'u' in 'upper'
        
        // Debug output
        System.out.println("[DEBUG_LOG] Element at position 6: " + (element != null ? element.getText() : "null"));
        System.out.println("[DEBUG_LOG] Element class: " + (element != null ? element.getClass().getName() : "null"));
        
        // Get documentation for the element
        String documentation = documentationProvider.generateDoc(element, null);
        
        // Debug output
        System.out.println("[DEBUG_LOG] Documentation: " + documentation);
        
        // Verify documentation
        assertNotNull("Documentation should not be null", documentation);
        assertTrue("Documentation should contain 'Latte Filter: upper'", documentation.contains("Latte Filter: upper"));
        assertTrue("Documentation should contain 'Converts a string to uppercase'", documentation.contains("Converts a string to uppercase"));
    }
    
    /**
     * Tests that documentation is provided for Nette package macros.
     */
    @Test
    public void testNetteMacroDocumentation() {
        // Enable Nette packages
        enableNettePackages();
        
        // Create a Latte file with a link macro
        myFixture.configureByText("test.latte", "{link 'Homepage:default'}");
        
        // Get the PsiElement at the link macro
        PsiElement element = myFixture.getFile().findElementAt(1); // Position of 'l' in 'link'
        
        // Get documentation for the element
        String documentation = documentationProvider.generateDoc(element, null);
        
        // Verify documentation
        assertNotNull("Documentation should not be null", documentation);
        assertTrue("Documentation should contain 'Nette Macro: link'", documentation.contains("Nette Macro: link"));
        assertTrue("Documentation should contain 'nette/application'", documentation.contains("nette/application"));
    }
    
    /**
     * Tests that documentation is provided for Nette package attributes.
     */
    @Test
    public void testNetteAttributeDocumentation() {
        // Enable Nette packages
        enableNettePackages();
        
        // Create a Latte file with an n:href attribute
        myFixture.configureByText("test.latte", "<a n:href=\"Homepage:default\">Home</a>");
        
        // Get the PsiElement at the n:href attribute
        PsiElement element = myFixture.getFile().findElementAt(3); // Position of 'h' in 'n:href'
        
        // Debug output
        System.out.println("[DEBUG_LOG] Element at position 3: " + (element != null ? element.getText() : "null"));
        System.out.println("[DEBUG_LOG] Element class: " + (element != null ? element.getClass().getName() : "null"));
        
        // Get documentation for the element
        String documentation = documentationProvider.generateDoc(element, null);
        
        // Debug output
        System.out.println("[DEBUG_LOG] Documentation: " + documentation);
        
        // Verify documentation
        assertNotNull("Documentation should not be null", documentation);
        assertTrue("Documentation should contain 'Nette Attribute: n:href'", documentation.contains("Nette Attribute: n:href"));
        assertTrue("Documentation should contain 'nette/application'", documentation.contains("nette/application"));
    }
    
    /**
     * Tests that no documentation is provided for unknown elements.
     */
    @Test
    public void testUnknownElementDocumentation() {
        // Create a Latte file with an unknown macro
        myFixture.configureByText("test.latte", "{unknownMacro}");
        
        // Get the PsiElement at the unknown macro
        PsiElement element = myFixture.getFile().findElementAt(1); // Position of 'u' in 'unknownMacro'
        
        // Get documentation for the element
        String documentation = documentationProvider.generateDoc(element, null);
        
        // Verify documentation
        assertNull("Documentation should be null for unknown elements", documentation);
    }
    
    /**
     * Enables Nette packages for testing.
     */
    private void enableNettePackages() {
        // Add a Nette macro for testing if not already present
        boolean hasMacro = false;
        for (NetteMacro macro : NetteMacroProvider.getAllMacros()) {
            if (macro.getName().equals("link")) {
                hasMacro = true;
                break;
            }
        }
        
        if (!hasMacro) {
            // This is a workaround for testing since we can't directly add to NetteMacroProvider
            // In a real test, the Nette macros would be provided by the NetteMacroProvider
            // based on the enabled packages in LatteSettings
            // For this test, we're assuming the macros are already available
        }
    }
}
