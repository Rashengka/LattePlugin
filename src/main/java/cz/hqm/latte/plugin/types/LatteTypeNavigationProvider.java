package cz.hqm.latte.plugin.types;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides navigation from type references in Latte templates to the corresponding classes.
 * Supports navigation from:
 * - {varType} macros to the referenced classes
 * - {templateType} macros to the referenced classes
 */
public class LatteTypeNavigationProvider implements GotoDeclarationHandler {

    // Pattern for extracting type from {varType} macro
    private static final Pattern VAR_TYPE_PATTERN = Pattern.compile("\\{varType\\s+\\$\\w+\\s*:\\s*(\\w+(?:\\|\\w+)*)\\}");
    
    // Pattern for extracting class name from {templateType} macro
    private static final Pattern TEMPLATE_TYPE_PATTERN = Pattern.compile("\\{templateType\\s+(\\w+(?:\\\\\\w+)*)\\}");

    @Nullable
    @Override
    public PsiElement[] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
        if (sourceElement == null) {
            return null;
        }

        Project project = sourceElement.getProject();
        String elementText = sourceElement.getText();
        PsiFile sourceFile = sourceElement.getContainingFile();

        // Check for {varType} macro
        Matcher varTypeMatcher = VAR_TYPE_PATTERN.matcher(elementText);
        if (varTypeMatcher.find()) {
            String type = varTypeMatcher.group(1);
            PsiElement classElement = LatteTypeProvider.findClassByType(project, type);
            if (classElement != null) {
                return new PsiElement[] { classElement };
            }
        }

        // Check for {templateType} macro
        Matcher templateTypeMatcher = TEMPLATE_TYPE_PATTERN.matcher(elementText);
        if (templateTypeMatcher.find()) {
            String className = templateTypeMatcher.group(1);
            PsiElement classElement = LatteTypeProvider.findClassByType(project, className);
            if (classElement != null) {
                return new PsiElement[] { classElement };
            }
        }

        return null;
    }
}