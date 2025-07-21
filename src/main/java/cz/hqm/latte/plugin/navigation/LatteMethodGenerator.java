package cz.hqm.latte.plugin.navigation;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generates PHP methods for Nette presenters and components.
 */
public class LatteMethodGenerator {

    // Pattern for extracting parameters from link macros
    private static final Pattern PARAMS_PATTERN = Pattern.compile("(?:a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z)\\s*=\\s*([^,}]+)");

    /**
     * Generates a presenter method for the given action or signal.
     * 
     * @param project The project
     * @param presenterFile The presenter file
     * @param methodName The method name
     * @param parameters The method parameters
     * @return The generated method element, or null if generation failed
     */
    @Nullable
    public static PsiElement generatePresenterMethod(Project project, PsiFile presenterFile, String methodName, Map<String, String> parameters) {
        if (presenterFile == null) {
            return null;
        }

        // Find the class definition in the presenter file
        String presenterContent = presenterFile.getText();
        int classPos = presenterContent.indexOf("class");
        if (classPos == -1) {
            return null;
        }

        // Find the end of the class
        int classEndPos = presenterContent.lastIndexOf("}");
        if (classEndPos == -1) {
            return null;
        }

        // Generate the method code
        StringBuilder methodCode = new StringBuilder();
        methodCode.append("\n\t/**\n");
        methodCode.append("\t * ").append(methodName).append(" method.\n");
        
        // Add parameter documentation
        for (Map.Entry<String, String> param : parameters.entrySet()) {
            methodCode.append("\t * @param ").append(getPhpType(param.getValue())).append(" $").append(param.getKey()).append("\n");
        }
        
        methodCode.append("\t */\n");
        methodCode.append("\tpublic function ").append(methodName).append("(");
        
        // Add method parameters
        List<String> paramList = new ArrayList<>();
        for (Map.Entry<String, String> param : parameters.entrySet()) {
            paramList.add(getPhpType(param.getValue()) + " $" + param.getKey());
        }
        methodCode.append(String.join(", ", paramList));
        
        methodCode.append(")\n\t{\n");
        methodCode.append("\t\t// TODO: Implement ").append(methodName).append(" method\n");
        methodCode.append("\t}\n");

        // Insert the method into the file
        final Document document = PsiDocumentManager.getInstance(project).getDocument(presenterFile);
        if (document == null) {
            return null;
        }

        final int insertPosition = classEndPos;
        final String finalMethodCode = methodCode.toString();

        WriteCommandAction.runWriteCommandAction(project, () -> {
            document.insertString(insertPosition, finalMethodCode);
            PsiDocumentManager.getInstance(project).commitDocument(document);
        });

        // Return the presenter file as the target element
        return presenterFile;
    }

    /**
     * Generates a component factory method.
     * 
     * @param project The project
     * @param presenterFile The presenter file
     * @param componentName The component name
     * @return The generated method element, or null if generation failed
     */
    @Nullable
    public static PsiElement generateComponentMethod(Project project, PsiFile presenterFile, String componentName) {
        if (presenterFile == null) {
            return null;
        }

        // Find the class definition in the presenter file
        String presenterContent = presenterFile.getText();
        int classPos = presenterContent.indexOf("class");
        if (classPos == -1) {
            return null;
        }

        // Find the end of the class
        int classEndPos = presenterContent.lastIndexOf("}");
        if (classEndPos == -1) {
            return null;
        }

        // Generate the method code
        StringBuilder methodCode = new StringBuilder();
        methodCode.append("\n\t/**\n");
        methodCode.append("\t * Creates the ").append(componentName).append(" component.\n");
        methodCode.append("\t *\n");
        methodCode.append("\t * @return \\Nette\\Application\\UI\\Control\n");
        methodCode.append("\t */\n");
        methodCode.append("\tprotected function createComponent").append(capitalizeFirst(componentName)).append("()\n");
        methodCode.append("\t{\n");
        methodCode.append("\t\t// TODO: Implement component factory\n");
        methodCode.append("\t\treturn new \\Nette\\Application\\UI\\Control();\n");
        methodCode.append("\t}\n");

        // Insert the method into the file
        final Document document = PsiDocumentManager.getInstance(project).getDocument(presenterFile);
        if (document == null) {
            return null;
        }

        final int insertPosition = classEndPos;
        final String finalMethodCode = methodCode.toString();

        WriteCommandAction.runWriteCommandAction(project, () -> {
            document.insertString(insertPosition, finalMethodCode);
            PsiDocumentManager.getInstance(project).commitDocument(document);
        });

        // Return the presenter file as the target element
        return presenterFile;
    }

    /**
     * Extracts parameters from a link macro.
     * 
     * @param linkText The link macro text
     * @return A map of parameter names to parameter values
     */
    @NotNull
    public static Map<String, String> extractParameters(String linkText) {
        Map<String, String> parameters = new java.util.HashMap<>();
        
        Matcher matcher = PARAMS_PATTERN.matcher(linkText);
        while (matcher.find()) {
            String paramName = matcher.group(1);
            String paramValue = matcher.group(2);
            parameters.put(paramName, paramValue);
        }
        
        return parameters;
    }

    /**
     * Gets the PHP type for a parameter value.
     * 
     * @param value The parameter value
     * @return The PHP type
     */
    @NotNull
    private static String getPhpType(String value) {
        if (value == null) {
            return "mixed";
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
        } else if (value.startsWith("null")) {
            return "?mixed";
        } else {
            return "mixed";
        }
    }

    /**
     * Capitalizes the first letter of a string.
     * 
     * @param str The string to capitalize
     * @return The capitalized string
     */
    @NotNull
    private static String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}