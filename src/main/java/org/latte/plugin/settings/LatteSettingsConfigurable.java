package org.latte.plugin.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import org.latte.plugin.version.LatteVersion;

import javax.swing.*;
import java.awt.*;

/**
 * Provides a settings UI for the Latte plugin.
 */
public class LatteSettingsConfigurable implements Configurable {
    private JPanel myMainPanel;
    
    // Latte version settings
    private ComboBox<String> versionComboBox;
    private JBCheckBox overrideDetectedVersionCheckBox;
    
    // Nette package enable/disable settings
    private JBCheckBox enableNetteApplicationCheckBox;
    private JBCheckBox enableNetteFormsCheckBox;
    private JBCheckBox enableNetteAssetsCheckBox;
    
    // Nette package version settings
    private ComboBox<String> netteApplicationVersionComboBox;
    private JBCheckBox overrideDetectedNetteApplicationVersionCheckBox;
    
    private ComboBox<String> netteFormsVersionComboBox;
    private JBCheckBox overrideDetectedNetteFormsVersionCheckBox;
    
    private ComboBox<String> netteAssetsVersionComboBox;
    private JBCheckBox overrideDetectedNetteAssetsVersionCheckBox;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Latte";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        myMainPanel = new JBPanel<>(new BorderLayout());
        
        // Version settings
        versionComboBox = new ComboBox<>(new String[]{"2.x", "3.0+", "4.0+"});
        overrideDetectedVersionCheckBox = new JBCheckBox("Override detected version");
        
        // Nette packages settings
        enableNetteApplicationCheckBox = new JBCheckBox("Enable nette/application support");
        enableNetteFormsCheckBox = new JBCheckBox("Enable nette/forms support");
        enableNetteAssetsCheckBox = new JBCheckBox("Enable nette/assets support");
        
        // Nette package version settings
        netteApplicationVersionComboBox = new ComboBox<>(new String[]{"2", "3", "4"});
        overrideDetectedNetteApplicationVersionCheckBox = new JBCheckBox("Override detected version");
        
        netteFormsVersionComboBox = new ComboBox<>(new String[]{"2", "3", "4"});
        overrideDetectedNetteFormsVersionCheckBox = new JBCheckBox("Override detected version");
        
        netteAssetsVersionComboBox = new ComboBox<>(new String[]{"1"});
        overrideDetectedNetteAssetsVersionCheckBox = new JBCheckBox("Override detected version");
        
        // Build the form
        FormBuilder formBuilder = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Latte Version:"), versionComboBox, 1, false)
                .addComponent(overrideDetectedVersionCheckBox, 1)
                .addSeparator(10)
                .addComponent(new JBLabel("Nette Packages:"), 1)
                .addComponent(enableNetteApplicationCheckBox, 1);
                
        // Only show version settings for enabled packages
        JPanel applicationVersionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        applicationVersionPanel.add(new JBLabel("Version:"));
        applicationVersionPanel.add(netteApplicationVersionComboBox);
        applicationVersionPanel.add(overrideDetectedNetteApplicationVersionCheckBox);
        
        formBuilder.addComponent(applicationVersionPanel, 1)
                .addComponent(enableNetteFormsCheckBox, 1);
                
        JPanel formsVersionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formsVersionPanel.add(new JBLabel("Version:"));
        formsVersionPanel.add(netteFormsVersionComboBox);
        formsVersionPanel.add(overrideDetectedNetteFormsVersionCheckBox);
        
        formBuilder.addComponent(formsVersionPanel, 1)
                .addComponent(enableNetteAssetsCheckBox, 1);
                
        JPanel assetsVersionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        assetsVersionPanel.add(new JBLabel("Version:"));
        assetsVersionPanel.add(netteAssetsVersionComboBox);
        assetsVersionPanel.add(overrideDetectedNetteAssetsVersionCheckBox);
        
        formBuilder.addComponent(assetsVersionPanel, 1);
        
        // Add listeners to enable/disable version settings based on package enable/disable
        enableNetteApplicationCheckBox.addActionListener(e -> {
            boolean enabled = enableNetteApplicationCheckBox.isSelected();
            netteApplicationVersionComboBox.setEnabled(enabled);
            overrideDetectedNetteApplicationVersionCheckBox.setEnabled(enabled);
        });
        
        enableNetteFormsCheckBox.addActionListener(e -> {
            boolean enabled = enableNetteFormsCheckBox.isSelected();
            netteFormsVersionComboBox.setEnabled(enabled);
            overrideDetectedNetteFormsVersionCheckBox.setEnabled(enabled);
        });
        
        enableNetteAssetsCheckBox.addActionListener(e -> {
            boolean enabled = enableNetteAssetsCheckBox.isSelected();
            netteAssetsVersionComboBox.setEnabled(enabled);
            overrideDetectedNetteAssetsVersionCheckBox.setEnabled(enabled);
        });
        
        myMainPanel.add(formBuilder.getPanel(), BorderLayout.NORTH);
        myMainPanel.setBorder(JBUI.Borders.empty(10));
        
        // Load settings
        reset();
        
        return myMainPanel;
    }

    @Override
    public boolean isModified() {
        LatteSettings settings = LatteSettings.getInstance();
        
        boolean modified = false;
        
        // Check if version settings are modified
        String selectedVersion = (String) versionComboBox.getSelectedItem();
        modified |= !selectedVersion.equals(settings.getSelectedVersion());
        modified |= overrideDetectedVersionCheckBox.isSelected() != settings.isOverrideDetectedVersion();
        
        // Check if Nette package settings are modified
        modified |= enableNetteApplicationCheckBox.isSelected() != settings.isEnableNetteApplication();
        modified |= enableNetteFormsCheckBox.isSelected() != settings.isEnableNetteForms();
        modified |= enableNetteAssetsCheckBox.isSelected() != settings.isEnableNetteAssets();
        
        // Check if Nette package version settings are modified
        String selectedNetteApplicationVersion = (String) netteApplicationVersionComboBox.getSelectedItem();
        if (selectedNetteApplicationVersion != null && settings.getSelectedNetteApplicationVersion() != null) {
            modified |= !selectedNetteApplicationVersion.equals(settings.getSelectedNetteApplicationVersion());
        } else if (selectedNetteApplicationVersion != null || settings.getSelectedNetteApplicationVersion() != null) {
            modified = true;
        }
        modified |= overrideDetectedNetteApplicationVersionCheckBox.isSelected() != settings.isOverrideDetectedNetteApplicationVersion();
        
        String selectedNetteFormsVersion = (String) netteFormsVersionComboBox.getSelectedItem();
        if (selectedNetteFormsVersion != null && settings.getSelectedNetteFormsVersion() != null) {
            modified |= !selectedNetteFormsVersion.equals(settings.getSelectedNetteFormsVersion());
        } else if (selectedNetteFormsVersion != null || settings.getSelectedNetteFormsVersion() != null) {
            modified = true;
        }
        modified |= overrideDetectedNetteFormsVersionCheckBox.isSelected() != settings.isOverrideDetectedNetteFormsVersion();
        
        String selectedNetteAssetsVersion = (String) netteAssetsVersionComboBox.getSelectedItem();
        if (selectedNetteAssetsVersion != null && settings.getSelectedNetteAssetsVersion() != null) {
            modified |= !selectedNetteAssetsVersion.equals(settings.getSelectedNetteAssetsVersion());
        } else if (selectedNetteAssetsVersion != null || settings.getSelectedNetteAssetsVersion() != null) {
            modified = true;
        }
        modified |= overrideDetectedNetteAssetsVersionCheckBox.isSelected() != settings.isOverrideDetectedNetteAssetsVersion();
        
        return modified;
    }

    @Override
    public void apply() throws ConfigurationException {
        LatteSettings settings = LatteSettings.getInstance();
        
        // Apply version settings
        settings.setSelectedVersion((String) versionComboBox.getSelectedItem());
        settings.setOverrideDetectedVersion(overrideDetectedVersionCheckBox.isSelected());
        
        // Apply Nette package settings
        settings.setEnableNetteApplication(enableNetteApplicationCheckBox.isSelected());
        settings.setEnableNetteForms(enableNetteFormsCheckBox.isSelected());
        settings.setEnableNetteAssets(enableNetteAssetsCheckBox.isSelected());
        
        // Apply Nette package version settings
        settings.setSelectedNetteApplicationVersion((String) netteApplicationVersionComboBox.getSelectedItem());
        settings.setOverrideDetectedNetteApplicationVersion(overrideDetectedNetteApplicationVersionCheckBox.isSelected());
        
        settings.setSelectedNetteFormsVersion((String) netteFormsVersionComboBox.getSelectedItem());
        settings.setOverrideDetectedNetteFormsVersion(overrideDetectedNetteFormsVersionCheckBox.isSelected());
        
        settings.setSelectedNetteAssetsVersion((String) netteAssetsVersionComboBox.getSelectedItem());
        settings.setOverrideDetectedNetteAssetsVersion(overrideDetectedNetteAssetsVersionCheckBox.isSelected());
    }

    @Override
    public void reset() {
        LatteSettings settings = LatteSettings.getInstance();
        
        // Reset version settings
        versionComboBox.setSelectedItem(settings.getSelectedVersion() != null ? 
                settings.getSelectedVersion() : LatteVersion.getDefault().getDisplayName());
        overrideDetectedVersionCheckBox.setSelected(settings.isOverrideDetectedVersion());
        
        // Reset Nette package settings
        enableNetteApplicationCheckBox.setSelected(settings.isEnableNetteApplication());
        enableNetteFormsCheckBox.setSelected(settings.isEnableNetteForms());
        enableNetteAssetsCheckBox.setSelected(settings.isEnableNetteAssets());
        
        // Reset Nette package version settings
        netteApplicationVersionComboBox.setSelectedItem(settings.getSelectedNetteApplicationVersion() != null ? 
                settings.getSelectedNetteApplicationVersion() : "3");
        overrideDetectedNetteApplicationVersionCheckBox.setSelected(settings.isOverrideDetectedNetteApplicationVersion());
        
        netteFormsVersionComboBox.setSelectedItem(settings.getSelectedNetteFormsVersion() != null ? 
                settings.getSelectedNetteFormsVersion() : "3");
        overrideDetectedNetteFormsVersionCheckBox.setSelected(settings.isOverrideDetectedNetteFormsVersion());
        
        netteAssetsVersionComboBox.setSelectedItem(settings.getSelectedNetteAssetsVersion() != null ? 
                settings.getSelectedNetteAssetsVersion() : "1");
        overrideDetectedNetteAssetsVersionCheckBox.setSelected(settings.isOverrideDetectedNetteAssetsVersion());
        
        // Update enabled state of version settings based on package enable/disable
        boolean applicationEnabled = enableNetteApplicationCheckBox.isSelected();
        netteApplicationVersionComboBox.setEnabled(applicationEnabled);
        overrideDetectedNetteApplicationVersionCheckBox.setEnabled(applicationEnabled);
        
        boolean formsEnabled = enableNetteFormsCheckBox.isSelected();
        netteFormsVersionComboBox.setEnabled(formsEnabled);
        overrideDetectedNetteFormsVersionCheckBox.setEnabled(formsEnabled);
        
        boolean assetsEnabled = enableNetteAssetsCheckBox.isSelected();
        netteAssetsVersionComboBox.setEnabled(assetsEnabled);
        overrideDetectedNetteAssetsVersionCheckBox.setEnabled(assetsEnabled);
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return versionComboBox;
    }
}