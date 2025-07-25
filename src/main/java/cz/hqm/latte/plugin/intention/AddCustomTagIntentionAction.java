package cz.hqm.latte.plugin.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import cz.hqm.latte.plugin.custom.CustomTag;
import cz.hqm.latte.plugin.custom.CustomTagsProvider;
import cz.hqm.latte.plugin.lexer.LatteTokenTypes;

import javax.swing.*;

/**
 * Intention action for adding unknown Latte tags as custom tags.
 */
public class AddCustomTagIntentionAction extends PsiElementBaseIntentionAction implements IntentionAction {
    
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        String tagName = element.getText();
        
        // Remove any special characters (like {, /, etc.)
        tagName = tagName.replaceAll("[{}/]", "").trim();
        
        if (tagName.isEmpty()) {
            return;
        }
        
        // Check if the tag already exists
        if (CustomTagsProvider.tagExists(project, tagName)) {
            return;
        }
        
        // Show dialog to add the tag
        AddCustomTagDialog dialog = new AddCustomTagDialog(project, tagName);
        if (dialog.showAndGet()) {
            CustomTagsProvider.addTag(project, dialog.getTagName(), dialog.getTagDescription());
        }
    }
    
    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        // Get the element text
        String elementText = element.getText();
        
        // Check if the element text looks like a Latte tag (starts with { and doesn't contain $ or /)
        boolean looksLikeTag = elementText.startsWith("{") && 
                              !elementText.contains("$") && 
                              !elementText.contains("/") &&
                              !elementText.contains("#");
        
        // Check if the element is a potential tag
        if (looksLikeTag || 
            (element.getNode() != null && 
             (element.getNode().getElementType() == LatteTokenTypes.LATTE_MACRO_NAME ||
              element.getNode().getElementType() == LatteTokenTypes.LATTE_MACRO_CONTENT))) {
            
            String tagName = elementText.replaceAll("[{}/]", "").trim();
            
            // Check if the tag name is not empty and doesn't already exist
            boolean tagExists = CustomTagsProvider.tagExists(project, tagName);
            
            return !tagName.isEmpty() && !tagExists;
        }
        
        return false;
    }
    
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return "Add as custom Latte tag";
    }
    
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "Latte";
    }
    
    /**
     * Dialog for adding a custom tag.
     */
    private static class AddCustomTagDialog extends DialogWrapper {
        private final JBTextField nameField;
        private final JBTextField descriptionField;
        
        public AddCustomTagDialog(Project project, String tagName) {
            super(project);
            setTitle("Add Custom Tag");
            
            nameField = new JBTextField(tagName);
            descriptionField = new JBTextField();
            
            init();
        }
        
        @Override
        protected @Nullable ValidationInfo doValidate() {
            if (nameField.getText().trim().isEmpty()) {
                return new ValidationInfo("Name cannot be empty", nameField);
            }
            return null;
        }
        
        @Override
        protected @Nullable JComponent createCenterPanel() {
            return FormBuilder.createFormBuilder()
                    .addLabeledComponent(new JBLabel("Name:"), nameField)
                    .addLabeledComponent(new JBLabel("Description:"), descriptionField)
                    .getPanel();
        }
        
        public String getTagName() {
            return nameField.getText().trim();
        }
        
        public String getTagDescription() {
            String description = descriptionField.getText().trim();
            return description.isEmpty() ? null : description;
        }
    }
}