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
import cz.hqm.latte.plugin.custom.CustomFunction;
import cz.hqm.latte.plugin.custom.CustomFunctionsProvider;
import cz.hqm.latte.plugin.lexer.LatteTokenTypes;

import javax.swing.*;
import java.util.regex.Pattern;

/**
 * Intention action for adding unknown Latte functions as custom functions.
 */
public class AddCustomFunctionIntentionAction extends PsiElementBaseIntentionAction implements IntentionAction {
    
    // Pattern to match function calls (name followed by opening parenthesis)
    private static final Pattern FUNCTION_PATTERN = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*\\s*\\(");
    
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        String text = element.getText();
        
        // Extract function name from text (remove parentheses and arguments)
        String functionName = extractFunctionName(text);
        
        if (functionName.isEmpty()) {
            return;
        }
        
        // Check if the function already exists
        if (CustomFunctionsProvider.functionExists(project, functionName)) {
            return;
        }
        
        // Show dialog to add the function
        AddCustomFunctionDialog dialog = new AddCustomFunctionDialog(project, functionName);
        if (dialog.showAndGet()) {
            CustomFunctionsProvider.addFunction(project, dialog.getFunctionName(), dialog.getFunctionDescription());
        }
    }
    
    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        // Check if the element is a potential function
        if (element.getNode() != null && 
            element.getNode().getElementType() == LatteTokenTypes.LATTE_MACRO_CONTENT) {
            
            String text = element.getText();
            
            // Check if the text matches a function call pattern
            if (FUNCTION_PATTERN.matcher(text).find()) {
                String functionName = extractFunctionName(text);
                
                // Check if the function name is not empty and doesn't already exist
                return !functionName.isEmpty() && !CustomFunctionsProvider.functionExists(project, functionName);
            }
        }
        
        return false;
    }
    
    /**
     * Extracts the function name from a text that may contain a function call.
     *
     * @param text The text to extract from
     * @return The function name or empty string if not found
     */
    private String extractFunctionName(String text) {
        // Find the position of the opening parenthesis
        int parenIndex = text.indexOf('(');
        if (parenIndex > 0) {
            // Extract the name before the parenthesis and trim whitespace
            String name = text.substring(0, parenIndex).trim();
            
            // Validate that it's a valid function name
            if (name.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                return name;
            }
        }
        
        return "";
    }
    
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return "Add as custom Latte function";
    }
    
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "Latte";
    }
    
    /**
     * Dialog for adding a custom function.
     */
    private static class AddCustomFunctionDialog extends DialogWrapper {
        private final JBTextField nameField;
        private final JBTextField descriptionField;
        
        public AddCustomFunctionDialog(Project project, String functionName) {
            super(project);
            setTitle("Add Custom Function");
            
            nameField = new JBTextField(functionName);
            descriptionField = new JBTextField();
            
            init();
        }
        
        @Override
        protected @Nullable ValidationInfo doValidate() {
            if (nameField.getText().trim().isEmpty()) {
                return new ValidationInfo("Name cannot be empty", nameField);
            }
            
            // Validate function name format
            if (!nameField.getText().trim().matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                return new ValidationInfo("Invalid function name. Use only letters, numbers, and underscore. Start with a letter or underscore.", nameField);
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
        
        public String getFunctionName() {
            return nameField.getText().trim();
        }
        
        public String getFunctionDescription() {
            String description = descriptionField.getText().trim();
            return description.isEmpty() ? null : description;
        }
    }
}