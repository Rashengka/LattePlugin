package cz.hqm.latte.plugin.inclusion;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides navigation from Latte templates to included files and blocks.
 * Supports navigation from:
 * - {include} tags to target files
 * - {include #blockName} tags to target blocks
 * - {includeBlock} tags to target files
 * - {sandbox} tags to target files
 */
public class LatteTemplateInclusionNavigationProvider implements GotoDeclarationHandler {

    // Pattern for detecting {include} tags
    private static final Pattern INCLUDE_PATTERN = Pattern.compile("\\{include\\s+(['\"].*?['\"]|#\\w+)");
    
    // Pattern for detecting {includeBlock} tags
    private static final Pattern INCLUDE_BLOCK_PATTERN = Pattern.compile("\\{includeBlock\\s+['\"].*?['\"]");
    
    // Pattern for detecting {sandbox} tags
    private static final Pattern SANDBOX_PATTERN = Pattern.compile("\\{sandbox\\s+['\"].*?['\"]");

    @Nullable
    @Override
    public PsiElement[] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
        if (sourceElement == null) {
            return null;
        }

        Project project = sourceElement.getProject();
        String elementText = sourceElement.getText();
        PsiFile sourceFile = sourceElement.getContainingFile();

        // Check for {include} tag
        Matcher includeMatcher = INCLUDE_PATTERN.matcher(elementText);
        if (includeMatcher.find()) {
            String includeTarget = includeMatcher.group(1);
            
            // Check if it's a block include
            if (includeTarget.startsWith("#")) {
                // It's a block include
                PsiElement targetBlock = LatteTemplateInclusionHandler.findIncludeTargetBlock(project, sourceFile, elementText);
                if (targetBlock != null) {
                    return new PsiElement[] { targetBlock };
                }
            } else {
                // It's a file include
                PsiFile targetFile = LatteTemplateInclusionHandler.findIncludeTargetFile(project, sourceFile, elementText);
                if (targetFile != null) {
                    return new PsiElement[] { targetFile };
                }
            }
        }

        // Check for {includeBlock} tag
        Matcher includeBlockMatcher = INCLUDE_BLOCK_PATTERN.matcher(elementText);
        if (includeBlockMatcher.find()) {
            PsiFile targetFile = LatteTemplateInclusionHandler.findIncludeBlockTargetFile(project, sourceFile, elementText);
            if (targetFile != null) {
                return new PsiElement[] { targetFile };
            }
        }

        // Check for {sandbox} tag
        Matcher sandboxMatcher = SANDBOX_PATTERN.matcher(elementText);
        if (sandboxMatcher.find()) {
            PsiFile targetFile = LatteTemplateInclusionHandler.findSandboxTargetFile(project, sourceFile, elementText);
            if (targetFile != null) {
                return new PsiElement[] { targetFile };
            }
        }

        return null;
    }
}