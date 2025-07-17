package org.latte.plugin.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.psi.PsiElement;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.latte.plugin.custom.CustomFilter;
import org.latte.plugin.custom.CustomFiltersProvider;
import org.latte.plugin.lexer.LatteTokenTypes;

import javax.swing.*;

/**
 * Intention action for adding unknown Latte filters as custom filters.
 */
public class AddCustomFilterIntentionAction extends PsiElementBaseIntentionAction implements IntentionAction {
    
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        String filterName = element.getText();
        
        // Remove any special characters
        filterName = filterName.replaceAll("[|]", "").trim();
        
        if (filterName.isEmpty()) {
            return;
        }
        
        // Check if the filter already exists
        if (CustomFiltersProvider.filterExists(project, filterName)) {
            return;
        }
        
        // Show dialog to add the filter
        AddCustomFilterDialog dialog = new AddCustomFilterDialog(project, filterName);
        if (dialog.showAndGet()) {
            CustomFiltersProvider.addFilter(project, dialog.getFilterName(), dialog.getFilterDescription());
        }
    }
    
    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        // Check if the element is a potential filter
        if (element.getNode() != null && 
            element.getNode().getElementType() == LatteTokenTypes.LATTE_FILTER_NAME) {
            
            String filterName = element.getText().trim();
            
            // Check if the filter name is not empty and doesn't already exist
            return !filterName.isEmpty() && !CustomFiltersProvider.filterExists(project, filterName);
        }
        
        return false;
    }
    
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return "Add as custom Latte filter";
    }
    
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "Latte";
    }
    
    /**
     * Dialog for adding a custom filter.
     */
    private static class AddCustomFilterDialog extends DialogWrapper {
        private final JBTextField nameField;
        private final JBTextField descriptionField;
        
        public AddCustomFilterDialog(Project project, String filterName) {
            super(project);
            setTitle("Add Custom Filter");
            
            nameField = new JBTextField(filterName);
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
        
        public String getFilterName() {
            return nameField.getText().trim();
        }
        
        public String getFilterDescription() {
            String description = descriptionField.getText().trim();
            return description.isEmpty() ? null : description;
        }
    }
}