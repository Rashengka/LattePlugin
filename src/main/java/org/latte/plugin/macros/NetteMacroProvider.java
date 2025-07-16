package org.latte.plugin.macros;

import org.latte.plugin.settings.LatteSettings;

import java.util.*;

/**
 * Provides Latte macros from Nette packages based on enabled settings.
 * This class is responsible for storing and providing macros from different Nette packages.
 */
public class NetteMacroProvider {

    // Macros from nette/application package
    private static final Set<NetteMacro> APPLICATION_MACROS = new HashSet<>(Arrays.asList(
            new NetteMacro("link", "Creates a link to a presenter/action", "nette/application"),
            new NetteMacro("plink", "Creates a permanent link", "nette/application"),
            new NetteMacro("control", "Renders a component", "nette/application"),
            new NetteMacro("snippet", "Defines a snippet for AJAX", "nette/application"),
            new NetteMacro("snippetArea", "Defines a snippet area", "nette/application")
    ));

    // Macros from nette/forms package
    private static final Set<NetteMacro> FORMS_MACROS = new HashSet<>(Arrays.asList(
            new NetteMacro("form", "Opens a form", "nette/forms"),
            new NetteMacro("/form", "Closes a form", "nette/forms"),
            new NetteMacro("input", "Renders a form input", "nette/forms"),
            new NetteMacro("label", "Renders a form label", "nette/forms"),
            new NetteMacro("inputError", "Renders an error message for an input", "nette/forms")
    ));

    // Macros from nette/assets package
    private static final Set<NetteMacro> ASSETS_MACROS = new HashSet<>(Arrays.asList(
            new NetteMacro("css", "Includes CSS files", "nette/assets"),
            new NetteMacro("js", "Includes JavaScript files", "nette/assets"),
            new NetteMacro("asset", "Includes an asset with proper versioning", "nette/assets")
    ));

    // N:attributes from nette/application package
    private static final Set<NetteMacro> APPLICATION_ATTRIBUTES = new HashSet<>(Arrays.asList(
            new NetteMacro("n:href", "Creates a link to a presenter/action", "nette/application"),
            new NetteMacro("n:snippet", "Defines a snippet for AJAX", "nette/application")
    ));

    // N:attributes from nette/forms package
    private static final Set<NetteMacro> FORMS_ATTRIBUTES = new HashSet<>(Arrays.asList(
            new NetteMacro("n:name", "Binds an input to a form control", "nette/forms"),
            new NetteMacro("n:validation", "Adds validation to a form control", "nette/forms")
    ));

    /**
     * Gets all valid macro names based on enabled settings.
     *
     * @return A set of valid macro names
     */
    public static Set<String> getValidMacroNames() {
        Set<String> macroNames = new HashSet<>();
        
        // Get settings
        LatteSettings settings = LatteSettings.getInstance();
        
        // Add macros based on enabled settings
        if (settings.isEnableNetteApplication()) {
            APPLICATION_MACROS.forEach(macro -> macroNames.add(macro.getName()));
        }
        
        if (settings.isEnableNetteForms()) {
            FORMS_MACROS.forEach(macro -> macroNames.add(macro.getName()));
        }
        
        if (settings.isEnableNetteAssets()) {
            ASSETS_MACROS.forEach(macro -> macroNames.add(macro.getName()));
        }
        
        return macroNames;
    }

    /**
     * Gets all macros based on enabled settings.
     *
     * @return A set of macros
     */
    public static Set<NetteMacro> getAllMacros() {
        Set<NetteMacro> macros = new HashSet<>();
        
        // Get settings
        LatteSettings settings = LatteSettings.getInstance();
        
        // Add macros based on enabled settings
        if (settings.isEnableNetteApplication()) {
            macros.addAll(APPLICATION_MACROS);
        }
        
        if (settings.isEnableNetteForms()) {
            macros.addAll(FORMS_MACROS);
        }
        
        if (settings.isEnableNetteAssets()) {
            macros.addAll(ASSETS_MACROS);
        }
        
        return macros;
    }

    /**
     * Gets all n:attributes based on enabled settings.
     *
     * @return A set of n:attributes
     */
    public static Set<NetteMacro> getAllAttributes() {
        Set<NetteMacro> attributes = new HashSet<>();
        
        // Get settings
        LatteSettings settings = LatteSettings.getInstance();
        
        // Add attributes based on enabled settings
        if (settings.isEnableNetteApplication()) {
            attributes.addAll(APPLICATION_ATTRIBUTES);
        }
        
        if (settings.isEnableNetteForms()) {
            attributes.addAll(FORMS_ATTRIBUTES);
        }
        
        return attributes;
    }
}