package cz.hqm.latte.plugin.inclusion;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides type information for variables defined in included blocks.
 * This class parses {define} blocks to extract parameter types and provides
 * type hints for variables used in {include} tags.
 */
public class LatteBlockTypeProvider {

    // Pattern for extracting parameter types from {define} blocks
    private static final Pattern DEFINE_PARAMS_PATTERN = Pattern.compile("\\{define\\s+(\\w+)(?:\\s*,\\s*([^}]+))?\\}");
    
    // Pattern for extracting parameter type declarations
    private static final Pattern PARAM_TYPE_PATTERN = Pattern.compile("(\\w+)\\s*:\\s*(\\w+(?:\\|\\w+)*)");

    /**
     * Gets the type information for a block.
     * 
     * @param project The project
     * @param sourceFile The source file containing the {include} tag
     * @param blockName The name of the block
     * @return A map of parameter names to types, or null if the block is not found
     */
    @Nullable
    public static Map<String, String> getBlockTypeInfo(Project project, PsiFile sourceFile, String blockName) {
        // Find the block in the file
        PsiElement blockElement = LatteTemplateInclusionHandler.findBlockInFile(sourceFile, blockName);
        if (blockElement == null) {
            return null;
        }
        
        // Get the block text
        String blockText = getBlockText(blockElement);
        if (blockText == null) {
            return null;
        }
        
        // Extract parameter types from the block
        return extractParameterTypes(blockText);
    }
    
    /**
     * Gets the text of a block.
     * 
     * @param blockElement The block element
     * @return The block text, or null if not found
     */
    @Nullable
    private static String getBlockText(PsiElement blockElement) {
        // Get the parent element that contains the entire block
        PsiElement parent = blockElement.getParent();
        if (parent == null) {
            return null;
        }
        
        // Get the text of the parent element
        return parent.getText();
    }
    
    /**
     * Extracts parameter types from a block.
     * 
     * @param blockText The block text
     * @return A map of parameter names to types
     */
    @NotNull
    private static Map<String, String> extractParameterTypes(String blockText) {
        Map<String, String> parameterTypes = new HashMap<>();
        
        // Extract the parameter list from the {define} tag
        Matcher defineMatcher = DEFINE_PARAMS_PATTERN.matcher(blockText);
        if (defineMatcher.find() && defineMatcher.groupCount() >= 2) {
            String paramList = defineMatcher.group(2);
            if (paramList != null && !paramList.isEmpty()) {
                // Extract parameter types
                Matcher paramMatcher = PARAM_TYPE_PATTERN.matcher(paramList);
                while (paramMatcher.find()) {
                    String paramName = paramMatcher.group(1);
                    String paramType = paramMatcher.group(2);
                    parameterTypes.put(paramName, paramType);
                }
            }
        }
        
        return parameterTypes;
    }
    
    /**
     * Validates parameter values against their types.
     * 
     * @param parameterTypes The parameter types
     * @param parameterValues The parameter values
     * @return A map of validation errors, or an empty map if all parameters are valid
     */
    @NotNull
    public static Map<String, String> validateParameters(Map<String, String> parameterTypes, Map<String, String> parameterValues) {
        Map<String, String> validationErrors = new HashMap<>();
        
        for (Map.Entry<String, String> entry : parameterTypes.entrySet()) {
            String paramName = entry.getKey();
            String paramType = entry.getValue();
            
            // Check if the parameter is required (not nullable)
            boolean isRequired = !paramType.contains("?") && !paramType.contains("null");
            
            // Check if the parameter is provided
            if (isRequired && !parameterValues.containsKey(paramName)) {
                validationErrors.put(paramName, "Parameter is required but not provided");
                continue;
            }
            
            // Check if the parameter value matches the type
            String paramValue = parameterValues.get(paramName);
            if (paramValue != null && !isValueCompatibleWithType(paramValue, paramType)) {
                validationErrors.put(paramName, "Parameter value is not compatible with type " + paramType);
            }
        }
        
        return validationErrors;
    }
    
    /**
     * Checks if a value is compatible with a type.
     * 
     * @param value The value
     * @param type The type
     * @return True if the value is compatible with the type, false otherwise
     */
    private static boolean isValueCompatibleWithType(String value, String type) {
        // Handle union types
        if (type.contains("|")) {
            String[] types = type.split("\\|");
            for (String t : types) {
                if (isValueCompatibleWithType(value, t.trim())) {
                    return true;
                }
            }
            return false;
        }
        
        // Handle nullable types
        if (type.startsWith("?")) {
            return value.equals("null") || isValueCompatibleWithType(value, type.substring(1));
        }
        
        // Handle basic types
        switch (type.toLowerCase()) {
            case "string":
                return value.startsWith("'") || value.startsWith("\"");
            case "int":
            case "integer":
                return value.matches("\\d+");
            case "float":
            case "double":
                return value.matches("\\d+\\.\\d+");
            case "bool":
            case "boolean":
                return value.equals("true") || value.equals("false");
            case "array":
                return value.startsWith("[") || value.startsWith("array(");
            case "null":
                return value.equals("null");
            default:
                // For other types, we can't easily validate
                return true;
        }
    }
}