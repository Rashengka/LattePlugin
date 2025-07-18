package cz.hqm.latte.plugin.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import cz.hqm.latte.plugin.custom.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Configurable for custom Latte elements (tags, filters, functions, variables).
 */
public class LatteCustomElementsConfigurable implements Configurable {
    private final Project project;
    private JBTabbedPane tabbedPane;
    private CustomElementsPanel<CustomTag> tagsPanel;
    private CustomElementsPanel<CustomFilter> filtersPanel;
    private CustomElementsPanel<CustomFunction> functionsPanel;
    private CustomElementsPanel<CustomVariable> variablesPanel;
    
    public LatteCustomElementsConfigurable(Project project) {
        this.project = project;
    }
    
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Latte Custom Elements";
    }
    
    @Nullable
    @Override
    public JComponent createComponent() {
        tabbedPane = new JBTabbedPane();
        
        // Create panels for each type of custom element
        tagsPanel = new CustomElementsPanel<>(
            "Tags",
            LatteProjectSettings.getInstance(project).getCustomTags(),
            this::createTagDialog
        );
        
        filtersPanel = new CustomElementsPanel<>(
            "Filters",
            LatteProjectSettings.getInstance(project).getCustomFilters(),
            this::createFilterDialog
        );
        
        functionsPanel = new CustomElementsPanel<>(
            "Functions",
            LatteProjectSettings.getInstance(project).getCustomFunctions(),
            this::createFunctionDialog
        );
        
        variablesPanel = new CustomElementsPanel<>(
            "Variables",
            LatteProjectSettings.getInstance(project).getCustomVariables(),
            this::createVariableDialog
        );
        
        // Add panels to tabbed pane
        tabbedPane.addTab("Tags", tagsPanel);
        tabbedPane.addTab("Filters", filtersPanel);
        tabbedPane.addTab("Functions", functionsPanel);
        tabbedPane.addTab("Variables", variablesPanel);
        
        // Create main panel
        JBPanel<?> mainPanel = new JBPanel<>(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Add description label
        JBLabel descriptionLabel = new JBLabel("Configure custom Latte elements for your project.");
        descriptionLabel.setBorder(JBUI.Borders.empty(0, 0, 10, 0));
        mainPanel.add(descriptionLabel, BorderLayout.NORTH);
        
        return mainPanel;
    }
    
    @Override
    public boolean isModified() {
        return tagsPanel.isModified() || 
               filtersPanel.isModified() || 
               functionsPanel.isModified() || 
               variablesPanel.isModified();
    }
    
    @Override
    public void apply() throws ConfigurationException {
        LatteProjectSettings settings = LatteProjectSettings.getInstance(project);
        
        settings.setCustomTags(new ArrayList<>(tagsPanel.getElements()));
        settings.setCustomFilters(new ArrayList<>(filtersPanel.getElements()));
        settings.setCustomFunctions(new ArrayList<>(functionsPanel.getElements()));
        settings.setCustomVariables(new ArrayList<>(variablesPanel.getElements()));
        
        tagsPanel.resetModified();
        filtersPanel.resetModified();
        functionsPanel.resetModified();
        variablesPanel.resetModified();
    }
    
    @Override
    public void reset() {
        LatteProjectSettings settings = LatteProjectSettings.getInstance(project);
        
        tagsPanel.setElements(new ArrayList<>(settings.getCustomTags()));
        filtersPanel.setElements(new ArrayList<>(settings.getCustomFilters()));
        functionsPanel.setElements(new ArrayList<>(settings.getCustomFunctions()));
        variablesPanel.setElements(new ArrayList<>(settings.getCustomVariables()));
        
        tagsPanel.resetModified();
        filtersPanel.resetModified();
        functionsPanel.resetModified();
        variablesPanel.resetModified();
    }
    
    @Override
    public void disposeUIResources() {
        tabbedPane = null;
        tagsPanel = null;
        filtersPanel = null;
        functionsPanel = null;
        variablesPanel = null;
    }
    
    private CustomElementDialog<CustomTag> createTagDialog(@Nullable CustomTag tag) {
        return new CustomTagDialog(tag);
    }
    
    private CustomElementDialog<CustomFilter> createFilterDialog(@Nullable CustomFilter filter) {
        return new CustomFilterDialog(filter);
    }
    
    private CustomElementDialog<CustomFunction> createFunctionDialog(@Nullable CustomFunction function) {
        return new CustomFunctionDialog(function);
    }
    
    private CustomElementDialog<CustomVariable> createVariableDialog(@Nullable CustomVariable variable) {
        return new CustomVariableDialog(variable);
    }
    
    /**
     * Panel for managing custom elements.
     */
    private class CustomElementsPanel<T extends CustomElement> extends JBPanel<CustomElementsPanel<T>> {
        private final String title;
        private final CollectionListModel<T> listModel;
        private final JBList<T> elementsList;
        private final ElementDialogFactory<T> dialogFactory;
        private boolean modified = false;
        
        public CustomElementsPanel(String title, List<T> elements, ElementDialogFactory<T> dialogFactory) {
            super(new BorderLayout());
            this.title = title;
            this.dialogFactory = dialogFactory;
            
            // Create list model and list
            listModel = new CollectionListModel<>(new ArrayList<>(elements));
            elementsList = new JBList<>(listModel);
            elementsList.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof CustomElement) {
                        CustomElement element = (CustomElement) value;
                        String text = element.getName();
                        
                        if (element instanceof CustomVariable) {
                            CustomVariable variable = (CustomVariable) element;
                            if (variable.getType() != null) {
                                text += " (" + variable.getType() + ")";
                            }
                        }
                        
                        if (element.getDescription() != null) {
                            text += " - " + element.getDescription();
                        }
                        
                        setText(text);
                    }
                    return component;
                }
            });
            
            // Create toolbar decorator
            ToolbarDecorator decorator = ToolbarDecorator.createDecorator(elementsList)
                .setAddAction(button -> addElement())
                .setEditAction(button -> editElement())
                .setRemoveAction(button -> removeElement())
                .disableUpDownActions();
            
            // Add to panel
            add(decorator.createPanel(), BorderLayout.CENTER);
            add(new JBLabel(title), BorderLayout.NORTH);
        }
        
        public List<T> getElements() {
            return listModel.getItems();
        }
        
        public void setElements(List<T> elements) {
            listModel.removeAll();
            listModel.addAll(0, elements);
        }
        
        public boolean isModified() {
            return modified;
        }
        
        public void resetModified() {
            modified = false;
        }
        
        private void addElement() {
            CustomElementDialog<T> dialog = dialogFactory.createDialog(null);
            if (dialog.showAndGet()) {
                T element = dialog.getElement();
                listModel.add(element);
                modified = true;
            }
        }
        
        private void editElement() {
            int selectedIndex = elementsList.getSelectedIndex();
            if (selectedIndex >= 0) {
                T element = listModel.getElementAt(selectedIndex);
                CustomElementDialog<T> dialog = dialogFactory.createDialog(element);
                if (dialog.showAndGet()) {
                    T newElement = dialog.getElement();
                    listModel.setElementAt(newElement, selectedIndex);
                    modified = true;
                }
            }
        }
        
        private void removeElement() {
            int selectedIndex = elementsList.getSelectedIndex();
            if (selectedIndex >= 0) {
                listModel.remove(selectedIndex);
                modified = true;
            }
        }
    }
    
    /**
     * Factory interface for creating element dialogs.
     */
    private interface ElementDialogFactory<T extends CustomElement> {
        CustomElementDialog<T> createDialog(@Nullable T element);
    }
    
    /**
     * Base dialog for custom elements.
     */
    private abstract class CustomElementDialog<T extends CustomElement> extends DialogWrapper {
        protected JBTextField nameField;
        protected JBTextField descriptionField;
        
        public CustomElementDialog(String title) {
            super(project);
            setTitle(title);
            init();
        }
        
        @Override
        protected @Nullable ValidationInfo doValidate() {
            if (nameField.getText().trim().isEmpty()) {
                return new ValidationInfo("Name cannot be empty", nameField);
            }
            return null;
        }
        
        public abstract T getElement();
    }
    
    /**
     * Dialog for adding/editing custom tags.
     */
    private class CustomTagDialog extends CustomElementDialog<CustomTag> {
        private final CustomTag originalTag;
        
        public CustomTagDialog(@Nullable CustomTag tag) {
            super("Custom Tag");
            this.originalTag = tag;
        }
        
        @Override
        protected @Nullable JComponent createCenterPanel() {
            nameField = new JBTextField(originalTag != null ? originalTag.getName() : "");
            descriptionField = new JBTextField(originalTag != null && originalTag.getDescription() != null ? originalTag.getDescription() : "");
            
            return FormBuilder.createFormBuilder()
                    .addLabeledComponent("Name:", nameField)
                    .addLabeledComponent("Description:", descriptionField)
                    .getPanel();
        }
        
        @Override
        public CustomTag getElement() {
            return new CustomTag(
                nameField.getText().trim(),
                descriptionField.getText().trim().isEmpty() ? null : descriptionField.getText().trim()
            );
        }
    }
    
    /**
     * Dialog for adding/editing custom filters.
     */
    private class CustomFilterDialog extends CustomElementDialog<CustomFilter> {
        private final CustomFilter originalFilter;
        
        public CustomFilterDialog(@Nullable CustomFilter filter) {
            super("Custom Filter");
            this.originalFilter = filter;
        }
        
        @Override
        protected @Nullable JComponent createCenterPanel() {
            nameField = new JBTextField(originalFilter != null ? originalFilter.getName() : "");
            descriptionField = new JBTextField(originalFilter != null && originalFilter.getDescription() != null ? originalFilter.getDescription() : "");
            
            return FormBuilder.createFormBuilder()
                    .addLabeledComponent("Name:", nameField)
                    .addLabeledComponent("Description:", descriptionField)
                    .getPanel();
        }
        
        @Override
        public CustomFilter getElement() {
            return new CustomFilter(
                nameField.getText().trim(),
                descriptionField.getText().trim().isEmpty() ? null : descriptionField.getText().trim()
            );
        }
    }
    
    /**
     * Dialog for adding/editing custom functions.
     */
    private class CustomFunctionDialog extends CustomElementDialog<CustomFunction> {
        private final CustomFunction originalFunction;
        
        public CustomFunctionDialog(@Nullable CustomFunction function) {
            super("Custom Function");
            this.originalFunction = function;
        }
        
        @Override
        protected @Nullable JComponent createCenterPanel() {
            nameField = new JBTextField(originalFunction != null ? originalFunction.getName() : "");
            descriptionField = new JBTextField(originalFunction != null && originalFunction.getDescription() != null ? originalFunction.getDescription() : "");
            
            return FormBuilder.createFormBuilder()
                    .addLabeledComponent("Name:", nameField)
                    .addLabeledComponent("Description:", descriptionField)
                    .getPanel();
        }
        
        @Override
        public CustomFunction getElement() {
            return new CustomFunction(
                nameField.getText().trim(),
                descriptionField.getText().trim().isEmpty() ? null : descriptionField.getText().trim()
            );
        }
    }
    
    /**
     * Dialog for adding/editing custom variables.
     */
    private class CustomVariableDialog extends CustomElementDialog<CustomVariable> {
        private final CustomVariable originalVariable;
        private JBTextField typeField;
        
        public CustomVariableDialog(@Nullable CustomVariable variable) {
            super("Custom Variable");
            this.originalVariable = variable;
        }
        
        @Override
        protected @Nullable JComponent createCenterPanel() {
            nameField = new JBTextField(originalVariable != null ? originalVariable.getName() : "");
            typeField = new JBTextField(originalVariable != null && originalVariable.getType() != null ? originalVariable.getType() : "");
            descriptionField = new JBTextField(originalVariable != null && originalVariable.getDescription() != null ? originalVariable.getDescription() : "");
            
            return FormBuilder.createFormBuilder()
                    .addLabeledComponent("Name:", nameField)
                    .addLabeledComponent("Type:", typeField)
                    .addLabeledComponent("Description:", descriptionField)
                    .getPanel();
        }
        
        @Override
        public CustomVariable getElement() {
            return new CustomVariable(
                nameField.getText().trim(),
                typeField.getText().trim().isEmpty() ? null : typeField.getText().trim(),
                descriptionField.getText().trim().isEmpty() ? null : descriptionField.getText().trim()
            );
        }
    }
}