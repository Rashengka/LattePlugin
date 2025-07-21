package cz.hqm.latte.plugin.types;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import cz.hqm.latte.plugin.version.LatteVersion;
import cz.hqm.latte.plugin.version.LatteVersionManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides type information for Latte templates.
 * Supports {var}, {varType}, {templateType}, {templatePrint}, and {varPrint} macros.
 */
public class LatteTypeProvider {

    // Pattern for extracting variable declarations from {var} macro
    private static final Pattern VAR_PATTERN = Pattern.compile("\\{var\\s+(\\$\\w+)\\s*=\\s*([^}]+)\\}");
    
    // Pattern for extracting type declarations from {varType} macro
    private static final Pattern VAR_TYPE_PATTERN = Pattern.compile("\\{varType\\s+(\\$\\w+)\\s*:\\s*([\\?\\w\\|\\\\]+)\\}");
    
    // Pattern for extracting class name from {templateType} macro
    private static final Pattern TEMPLATE_TYPE_PATTERN = Pattern.compile("\\{templateType\\s+([\\w\\\\]+)\\}");
    
    // Pattern for extracting variable name from {templatePrint} macro
    private static final Pattern TEMPLATE_PRINT_PATTERN = Pattern.compile("\\{templatePrint\\s+(\\$\\w+)\\}");
    
    // Pattern for extracting variable name from {varPrint} macro
    private static final Pattern VAR_PRINT_PATTERN = Pattern.compile("\\{varPrint\\s+(\\$\\w+)\\}");
    
    // Cache of variable types by file
    private static final Map<String, Map<String, String>> variableTypesCache = new HashMap<>();
    
    // Cache of template types by file
    private static final Map<String, String> templateTypesCache = new HashMap<>();

    /**
     * Gets the type of a variable in a file.
     * 
     * @param project The project
     * @param file The file
     * @param variableName The variable name
     * @return The variable type, or null if not found
     */
    @Nullable
    public static String getVariableType(Project project, PsiFile file, String variableName) {
        if (file == null || variableName == null) {
            return null;
        }
        
        // Check cache
        String filePath = file.getVirtualFile().getPath();
        Map<String, String> fileVariableTypes = variableTypesCache.get(filePath);
        if (fileVariableTypes != null && fileVariableTypes.containsKey(variableName)) {
            return fileVariableTypes.get(variableName);
        }
        
        // Parse the file to extract variable types
        Map<String, String> variableTypes = parseVariableTypes(file);
        variableTypesCache.put(filePath, variableTypes);
        
        return variableTypes.get(variableName);
    }
    
    /**
     * Gets the template type for a file.
     * 
     * @param project The project
     * @param file The file
     * @return The template type, or null if not found
     */
    @Nullable
    public static String getTemplateType(Project project, PsiFile file) {
        if (file == null) {
            return null;
        }
        
        // Check cache
        String filePath = file.getVirtualFile().getPath();
        if (templateTypesCache.containsKey(filePath)) {
            return templateTypesCache.get(filePath);
        }
        
        // Parse the file to extract template type
        String templateType = parseTemplateType(file);
        if (templateType != null) {
            templateTypesCache.put(filePath, templateType);
        }
        
        return templateType;
    }
    
    /**
     * Finds the class referenced by a type.
     * 
     * @param project The project
     * @param type The type
     * @return The class element, or null if not found
     */
    @Nullable
    public static PsiElement findClassByType(Project project, String type) {
        if (project == null || type == null) {
            return null;
        }
        
        // Handle union types
        if (type.contains("|")) {
            String[] types = type.split("\\|");
            for (String t : types) {
                PsiElement classElement = findClassByType(project, t.trim());
                if (classElement != null) {
                    return classElement;
                }
            }
            return null;
        }
        
        // Handle nullable types
        if (type.startsWith("?")) {
            return findClassByType(project, type.substring(1));
        }
        
        // Handle basic types
        switch (type.toLowerCase()) {
            case "string":
            case "int":
            case "integer":
            case "float":
            case "double":
            case "bool":
            case "boolean":
            case "array":
            case "null":
            case "mixed":
                // These are primitive types, not classes
                return null;
            default:
                // Try to find the class
                PsiShortNamesCache cache = PsiShortNamesCache.getInstance(project);
                PsiElement[] classes = cache.getClassesByName(type, GlobalSearchScope.allScope(project));
                if (classes.length > 0) {
                    return classes[0];
                }
                
                // Try with fully qualified name
                classes = cache.getClassesByName(type.substring(type.lastIndexOf('\\') + 1), GlobalSearchScope.allScope(project));
                for (PsiElement classElement : classes) {
                    if (classElement.toString().contains(type)) {
                        return classElement;
                    }
                }
                
                return null;
        }
    }
    
    /**
     * Parses variable types from a file.
     * 
     * @param file The file
     * @return A map of variable names to types
     */
    @NotNull
    private static Map<String, String> parseVariableTypes(PsiFile file) {
        Map<String, String> variableTypes = new HashMap<>();
        
        String fileContent = file.getText();
        
        // Extract types from {varType} macros
        Matcher varTypeMatcher = VAR_TYPE_PATTERN.matcher(fileContent);
        while (varTypeMatcher.find()) {
            String variableName = varTypeMatcher.group(1);
            String variableType = varTypeMatcher.group(2);
            variableTypes.put(variableName, variableType);
        }
        
        // Extract types from {var} macros (infer from value)
        Matcher varMatcher = VAR_PATTERN.matcher(fileContent);
        while (varMatcher.find()) {
            String variableName = varMatcher.group(1);
            String variableValue = varMatcher.group(2);
            
            // Only add if not already defined by {varType}
            if (!variableTypes.containsKey(variableName)) {
                String inferredType = inferTypeFromValue(variableValue);
                if (inferredType != null) {
                    variableTypes.put(variableName, inferredType);
                }
            }
        }
        
        return variableTypes;
    }
    
    /**
     * Parses the template type from a file.
     * 
     * @param file The file
     * @return The template type, or null if not found
     */
    @Nullable
    private static String parseTemplateType(PsiFile file) {
        String fileContent = file.getText();
        
        // Extract type from {templateType} macro
        Matcher templateTypeMatcher = TEMPLATE_TYPE_PATTERN.matcher(fileContent);
        if (templateTypeMatcher.find()) {
            return templateTypeMatcher.group(1);
        }
        
        return null;
    }
    
    /**
     * Infers the type of a value.
     * 
     * @param value The value
     * @return The inferred type, or null if not inferrable
     */
    @Nullable
    private static String inferTypeFromValue(String value) {
        if (value == null) {
            return null;
        }
        
        value = value.trim();
        
        if (value.startsWith("'") || value.startsWith("\"")) {
            return "string";
        } else if (value.equals("true") || value.equals("false")) {
            return "bool";
        } else if (value.matches("\\d+")) {
            return "int";
        } else if (value.matches("\\d+\\.\\d+")) {
            return "float";
        } else if (value.startsWith("[") || value.startsWith("array(")) {
            return "array";
        } else if (value.equals("null")) {
            return "null";
        } else if (value.startsWith("new ")) {
            // Extract class name from "new ClassName(...)"
            Pattern newPattern = Pattern.compile("new\\s+(\\w+(?:\\\\\\w+)*)");
            Matcher newMatcher = newPattern.matcher(value);
            if (newMatcher.find()) {
                return newMatcher.group(1);
            }
        }
        
        return null;
    }
    
    /**
     * Checks if a macro is supported in the current Latte version.
     * 
     * @param macroName The macro name
     * @return True if the macro is supported, false otherwise
     */
    public static boolean isMacroSupported(String macroName) {
        LatteVersion version = LatteVersionManager.getCurrentVersion();
        
        switch (macroName) {
            case "var":
                // {var} is supported in all versions
                return true;
            case "varType":
                // {varType} is supported in Latte 3.0+ and 4.0+
                return version == LatteVersion.VERSION_3X || 
                       version == LatteVersion.VERSION_3_0 || 
                       version == LatteVersion.VERSION_3_1 ||
                       version == LatteVersion.VERSION_4X || 
                       version == LatteVersion.VERSION_4_0;
            case "templateType":
                // {templateType} is supported in Latte 3.0+ and 4.0+
                return version == LatteVersion.VERSION_3X || 
                       version == LatteVersion.VERSION_3_0 || 
                       version == LatteVersion.VERSION_3_1 ||
                       version == LatteVersion.VERSION_4X || 
                       version == LatteVersion.VERSION_4_0;
            case "templatePrint":
                // {templatePrint} is supported in Latte 3.0+ and 4.0+
                return version == LatteVersion.VERSION_3X || 
                       version == LatteVersion.VERSION_3_0 || 
                       version == LatteVersion.VERSION_3_1 ||
                       version == LatteVersion.VERSION_4X || 
                       version == LatteVersion.VERSION_4_0;
            case "varPrint":
                // {varPrint} is supported in Latte 3.0+ and 4.0+
                return version == LatteVersion.VERSION_3X || 
                       version == LatteVersion.VERSION_3_0 || 
                       version == LatteVersion.VERSION_3_1 ||
                       version == LatteVersion.VERSION_4X || 
                       version == LatteVersion.VERSION_4_0;
            default:
                return false;
        }
    }
    
    /**
     * Clears the type caches.
     */
    public static void clearCaches() {
        variableTypesCache.clear();
        templateTypesCache.clear();
    }
}