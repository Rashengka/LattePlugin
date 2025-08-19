package cz.hqm.latte.plugin.macros;

import cz.hqm.latte.plugin.version.LatteVersion;
import cz.hqm.latte.plugin.version.LatteVersionManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Provides version-specific macros for Latte templates.
 * Includes macros specific to Latte 2.x, Latte 3.x+, n-attributes, e-macros, form macros, and database integration macros.
 */
public class LatteVersionSpecificMacroProvider {

    /**
     * Gets all macros specific to Latte 2.x.
     *
     * @return A set of macros specific to Latte 2.x
     */
    @NotNull
    public static Set<NetteMacro> getLatte2xMacros() {
        Set<NetteMacro> macros = new HashSet<>();
        
        // Syntax macros
        macros.add(new NetteMacro("syntax", "Changes the syntax of macros (double, off, etc.)", "latte/latte"));
        macros.add(new NetteMacro("l", "Literal left delimiter", "latte/latte"));
        macros.add(new NetteMacro("r", "Literal right delimiter", "latte/latte"));
        
        // Custom macros
        macros.add(new NetteMacro("use", "Registers custom macros", "latte/latte"));
        
        // Filters
        macros.add(new NetteMacro("bytes", "Formats size in bytes", "latte/latte"));
        macros.add(new NetteMacro("dataStream", "Converts to data: URI scheme", "latte/latte"));
        macros.add(new NetteMacro("url", "Escapes parameter in URL", "latte/latte"));
        
        return macros;
    }
    
    /**
     * Gets all macros specific to Latte 3.x+.
     *
     * @return A set of macros specific to Latte 3.x+
     */
    @NotNull
    public static Set<NetteMacro> getLatte3xMacros() {
        Set<NetteMacro> macros = new HashSet<>();
        
        // Type-related macros introduced in Latte 3.x
        macros.add(new NetteMacro("varType", "Declares type of a variable", "latte/latte"));
        macros.add(new NetteMacro("templateType", "Declares type for the whole template", "latte/latte"));
        
        // PHP execution and parameters macros in 3.x
        macros.add(new NetteMacro("php", "Executes raw PHP code", "latte/latte"));
        macros.add(new NetteMacro("do", "Executes an expression without output", "latte/latte"));
        macros.add(new NetteMacro("parameters", "Declares template parameters", "latte/latte"));
        
        // Control flow macros
        macros.add(new NetteMacro("switch", "Switch statement", "latte/latte"));
        macros.add(new NetteMacro("case", "Case in switch statement", "latte/latte"));
        macros.add(new NetteMacro("default", "Default case in switch statement", "latte/latte"));
        
        // Output control macros
        macros.add(new NetteMacro("rollback", "Cancels the output of a capture block", "latte/latte"));
        macros.add(new NetteMacro("spaceless", "Removes whitespace", "latte/latte"));
        macros.add(new NetteMacro("skipIf", "Conditionally skips a block", "latte/latte"));
        
        // Content type macros
        macros.add(new NetteMacro("contentType", "Sets the content type", "latte/latte"));
        
        // Debugging helpers
        macros.add(new NetteMacro("debugbreak", "Breaks the script for debugging", "latte/latte"));
        macros.add(new NetteMacro("trace", "Displays tracing", "latte/latte"));
        
        // Exception handling macros
        macros.add(new NetteMacro("try", "Try block", "latte/latte"));
        macros.add(new NetteMacro("catch", "Catch block", "latte/latte"));
        macros.add(new NetteMacro("finally", "Finally block", "latte/latte"));
        
        // Import macros
        macros.add(new NetteMacro("import", "Imports blocks from other templates", "latte/latte"));
        
        return macros;
    }
    
    /**
     * Gets all n-attributes.
     *
     * @return A set of n-attributes
     */
    @NotNull
    public static Set<NetteMacro> getNAttributes() {
        Set<NetteMacro> attributes = new HashSet<>();
        
        // Basic n-attributes
        attributes.add(new NetteMacro("n:if", "Conditional rendering of an element", "latte/latte"));
        attributes.add(new NetteMacro("n:foreach", "Loop over an array or iterable object", "latte/latte"));
        attributes.add(new NetteMacro("n:inner-foreach", "Loop over an array or iterable object, but only for the inner content", "latte/latte"));
        attributes.add(new NetteMacro("n:class", "Conditionally add classes to an element", "latte/latte"));
        attributes.add(new NetteMacro("n:attr", "Conditionally add attributes to an element", "latte/latte"));
        attributes.add(new NetteMacro("n:tag", "Conditionally change the tag name", "latte/latte"));
        attributes.add(new NetteMacro("n:ifcontent", "Conditionally display an element only if it has content", "latte/latte"));
        
        // Form n-attributes
        attributes.add(new NetteMacro("n:name", "Binds an input to a form control", "nette/forms"));
        attributes.add(new NetteMacro("n:input", "Renders a form input", "nette/forms"));
        attributes.add(new NetteMacro("n:label", "Renders a form label", "nette/forms"));
        
        // Link n-attributes
        attributes.add(new NetteMacro("n:href", "Creates a link to a presenter action", "nette/application"));
        
        return attributes;
    }
    
    /**
     * Gets all e-macros (escaping).
     *
     * @return A set of e-macros
     */
    @NotNull
    public static Set<NetteMacro> getEMacros() {
        Set<NetteMacro> macros = new HashSet<>();
        
        // Translation macros
        macros.add(new NetteMacro("_", "Translates text", "latte/latte"));
        macros.add(new NetteMacro("translate", "Translates text", "latte/latte"));
        
        // Escaping filters
        macros.add(new NetteMacro("noescape", "Prevents escaping", "latte/latte"));
        macros.add(new NetteMacro("nocheck", "Prevents escaping check", "latte/latte"));
        
        return macros;
    }
    
    /**
     * Gets all form macros.
     *
     * @return A set of form macros
     */
    @NotNull
    public static Set<NetteMacro> getFormMacros() {
        Set<NetteMacro> macros = new HashSet<>();
        
        // Form macros
        macros.add(new NetteMacro("form", "Renders a form", "nette/forms"));
        macros.add(new NetteMacro("input", "Renders a form input", "nette/forms"));
        macros.add(new NetteMacro("label", "Renders a form label", "nette/forms"));
        macros.add(new NetteMacro("formContainer", "Renders a form container", "nette/forms"));
        
        return macros;
    }
    
    /**
     * Gets all database macros.
     *
     * @return A set of database macros
     */
    @NotNull
    public static Set<NetteMacro> getDatabaseMacros() {
        Set<NetteMacro> macros = new HashSet<>();
        
        // Database macros
        macros.add(new NetteMacro("query", "Executes a database query", "nette/database"));
        
        return macros;
    }
    
    /**
     * Gets all macros for the current Latte version.
     *
     * @return A set of macros for the current Latte version
     */
    @NotNull
    public static Set<NetteMacro> getAllMacrosForCurrentVersion() {
        Set<NetteMacro> macros = new HashSet<>();
        LatteVersion version = LatteVersionManager.getCurrentVersion();
        
        // Add common macros
        macros.addAll(getNAttributes());
        macros.addAll(getEMacros());
        macros.addAll(getFormMacros());
        macros.addAll(getDatabaseMacros());
        
        // Add version-specific macros
        if (version == LatteVersion.VERSION_2X || 
            version == LatteVersion.VERSION_2_4 || 
            version == LatteVersion.VERSION_2_5) {
            macros.addAll(getLatte2xMacros());
        }
        
        if (version == LatteVersion.VERSION_3X || 
            version == LatteVersion.VERSION_3_0 || 
            version == LatteVersion.VERSION_3_1 ||
            version == LatteVersion.VERSION_4X || 
            version == LatteVersion.VERSION_4_0) {
            macros.addAll(getLatte3xMacros());
        }
        
        return macros;
    }
    
    /**
     * Checks if a macro is supported in the current Latte version.
     *
     * @param macroName The macro name
     * @return True if the macro is supported, false otherwise
     */
    public static boolean isMacroSupported(String macroName) {
        LatteVersion version = LatteVersionManager.getCurrentVersion();
        
        // Check Latte 2.x specific macros
        if ((macroName.equals("syntax") || 
             macroName.equals("l") || 
             macroName.equals("r") || 
             macroName.equals("use") ||
             macroName.equals("bytes") ||
             macroName.equals("dataStream") ||
             macroName.equals("url")) &&
            (version == LatteVersion.VERSION_2X || 
             version == LatteVersion.VERSION_2_4 || 
             version == LatteVersion.VERSION_2_5)) {
            return true;
        }
        
        // Check Latte 3.x+ specific macros (also available in 4.x)
        if ((macroName.equals("varType") ||
             macroName.equals("templateType") ||
             macroName.equals("php") ||
             macroName.equals("parameters") ||
             macroName.equals("do") ||
             macroName.equals("switch") || 
             macroName.equals("case") || 
             macroName.equals("default") || 
             macroName.equals("rollback") ||
             macroName.equals("spaceless") ||
             macroName.equals("skipIf") ||
             macroName.equals("contentType") ||
             macroName.equals("debugbreak") ||
             macroName.equals("trace") ||
             macroName.equals("try") ||
             macroName.equals("catch") ||
             macroName.equals("finally") ||
             macroName.equals("import")) &&
            (version == LatteVersion.VERSION_3X || 
             version == LatteVersion.VERSION_3_0 || 
             version == LatteVersion.VERSION_3_1 ||
             version == LatteVersion.VERSION_4X || 
             version == LatteVersion.VERSION_4_0)) {
            return true;
        }
        
        // n-attributes, e-macros, form macros, and database macros are supported in all versions
        return macroName.startsWith("n:") ||
               macroName.equals("_") ||
               macroName.equals("translate") ||
               macroName.equals("noescape") ||
               macroName.equals("nocheck") ||
               macroName.equals("form") ||
               macroName.equals("input") ||
               macroName.equals("label") ||
               macroName.equals("formContainer") ||
               macroName.equals("query");
    }
}