package cz.hqm.latte.plugin.intention;

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
import cz.hqm.latte.plugin.custom.CustomVariable;
import cz.hqm.latte.plugin.custom.CustomVariablesProvider;
import cz.hqm.latte.plugin.lexer.LatteTokenTypes;

import javax.swing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Intention action for adding unknown Latte variables as custom variables.
 */
public class AddCustomVariableIntentionAction extends PsiElementBaseIntentionAction implements IntentionAction {
    
    // Pattern to match variables ($ followed by a valid variable name)
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$(\\w+)");
    
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        String text = element.getText();
        
        // Extract variable name from text (remove $ prefix)
        String variableName = extractVariableName(text);
        
        if (variableName.isEmpty()) {
            return;
        }
        
        // Check if the variable already exists
        if (CustomVariablesProvider.variableExists(project, variableName)) {
            return;
        }
        
        // Show dialog to add the variable
        AddCustomVariableDialog dialog = new AddCustomVariableDialog(project, variableName);
        if (dialog.showAndGet()) {
            CustomVariablesProvider.addVariable(
                project, 
                dialog.getVariableName(), 
                dialog.getVariableType(), 
                dialog.getVariableDescription()
            );
        }
    }
    
    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        // Check if the element is a potential variable
        if (element.getNode() != null && 
            element.getNode().getElementType() == LatteTokenTypes.LATTE_MACRO_CONTENT) {
            
            String text = element.getText();
            
            // Check if the text contains a variable pattern
            Matcher matcher = VARIABLE_PATTERN.matcher(text);
            if (matcher.find()) {
                String variableName = matcher.group(1);
                
                // Check if the variable name is not empty and doesn't already exist
                return !variableName.isEmpty() && !CustomVariablesProvider.variableExists(project, variableName);
            }
        }
        
        return false;
    }
    
    /**
     * Extracts the variable name from a text that may contain a variable reference.
     *
     * @param text The text to extract from
     * @return The variable name or empty string if not found
     */
    private String extractVariableName(String text) {
        Matcher matcher = VARIABLE_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return "";
    }
    
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return "Add as custom Latte variable";
    }
    
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "Latte";
    }
    
    /**
     * Dialog for adding a custom variable.
     */
    private static class AddCustomVariableDialog extends DialogWrapper {
        private final JBTextField nameField;
        private final JBTextField typeField;
        private final JBTextField descriptionField;
        
        public AddCustomVariableDialog(Project project, String variableName) {
            super(project);
            setTitle("Add Custom Variable");
            
            nameField = new JBTextField(variableName);
            typeField = new JBTextField();
            descriptionField = new JBTextField();
            
            init();
        }
        
        @Override
        protected @Nullable ValidationInfo doValidate() {
            if (nameField.getText().trim().isEmpty()) {
                return new ValidationInfo("Name cannot be empty", nameField);
            }
            
            // Validate variable name format
            if (!nameField.getText().trim().matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                return new ValidationInfo("Invalid variable name. Use only letters, numbers, and underscore. Start with a letter or underscore.", nameField);
            }
            
            return null;
        }
        
        @Override
        protected @Nullable JComponent createCenterPanel() {
            return FormBuilder.createFormBuilder()
                    .addLabeledComponent(new JBLabel("Name:"), nameField)
                    .addLabeledComponent(new JBLabel("Type:"), typeField)
                    .addLabeledComponent(new JBLabel("Description:"), descriptionField)
                    .getPanel();
        }
        
        public String getVariableName() {
            return nameField.getText().trim();
        }
        
        public String getVariableType() {
            String type = typeField.getText().trim();
            return type.isEmpty() ? null : type;
        }
        
        public String getVariableDescription() {
            String description = descriptionField.getText().trim();
            return description.isEmpty() ? null : description;
        }
    }
}