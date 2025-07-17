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
    private JBCheckBox enableNetteDatabaseCheckBox;
    private JBCheckBox enableNetteSecurityCheckBox;
    private JBCheckBox enableNetteMailCheckBox;
    private JBCheckBox enableNetteHttpCheckBox;
    
    // Nette package version settings
    private ComboBox<String> netteApplicationVersionComboBox;
    private JBCheckBox overrideDetectedNetteApplicationVersionCheckBox;
    
    private ComboBox<String> netteFormsVersionComboBox;
    private JBCheckBox overrideDetectedNetteFormsVersionCheckBox;
    
    private ComboBox<String> netteAssetsVersionComboBox;
    private JBCheckBox overrideDetectedNetteAssetsVersionCheckBox;
    
    private ComboBox<String> netteDatabaseVersionComboBox;
    private JBCheckBox overrideDetectedNetteDatabaseVersionCheckBox;
    
    private ComboBox<String> netteSecurityVersionComboBox;
    private JBCheckBox overrideDetectedNetteSecurityVersionCheckBox;
    
    private ComboBox<String> netteMailVersionComboBox;
    private JBCheckBox overrideDetectedNetteMailVersionCheckBox;
    
    private ComboBox<String> netteHttpVersionComboBox;
    private JBCheckBox overrideDetectedNetteHttpVersionCheckBox;

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
        enableNetteDatabaseCheckBox = new JBCheckBox("Enable nette/database support");
        enableNetteSecurityCheckBox = new JBCheckBox("Enable nette/security support");
        enableNetteMailCheckBox = new JBCheckBox("Enable nette/mail support");
        enableNetteHttpCheckBox = new JBCheckBox("Enable nette/http support");
        
        // Nette package version settings
        netteApplicationVersionComboBox = new ComboBox<>(new String[]{"2", "3", "4"});
        overrideDetectedNetteApplicationVersionCheckBox = new JBCheckBox("Override detected version");
        
        netteFormsVersionComboBox = new ComboBox<>(new String[]{"2", "3", "4"});
        overrideDetectedNetteFormsVersionCheckBox = new JBCheckBox("Override detected version");
        
        netteAssetsVersionComboBox = new ComboBox<>(new String[]{"1"});
        overrideDetectedNetteAssetsVersionCheckBox = new JBCheckBox("Override detected version");
        
        netteDatabaseVersionComboBox = new ComboBox<>(new String[]{"2", "3"});
        overrideDetectedNetteDatabaseVersionCheckBox = new JBCheckBox("Override detected version");
        
        netteSecurityVersionComboBox = new ComboBox<>(new String[]{"2", "3"});
        overrideDetectedNetteSecurityVersionCheckBox = new JBCheckBox("Override detected version");
        
        netteMailVersionComboBox = new ComboBox<>(new String[]{"2", "3"});
        overrideDetectedNetteMailVersionCheckBox = new JBCheckBox("Override detected version");
        
        netteHttpVersionComboBox = new ComboBox<>(new String[]{"2", "3"});
        overrideDetectedNetteHttpVersionCheckBox = new JBCheckBox("Override detected version");
        
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
        
        formBuilder.addComponent(assetsVersionPanel, 1)
                .addComponent(enableNetteDatabaseCheckBox, 1);
                
        JPanel databaseVersionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        databaseVersionPanel.add(new JBLabel("Version:"));
        databaseVersionPanel.add(netteDatabaseVersionComboBox);
        databaseVersionPanel.add(overrideDetectedNetteDatabaseVersionCheckBox);
        
        formBuilder.addComponent(databaseVersionPanel, 1)
                .addComponent(enableNetteSecurityCheckBox, 1);
                
        JPanel securityVersionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        securityVersionPanel.add(new JBLabel("Version:"));
        securityVersionPanel.add(netteSecurityVersionComboBox);
        securityVersionPanel.add(overrideDetectedNetteSecurityVersionCheckBox);
        
        formBuilder.addComponent(securityVersionPanel, 1)
                .addComponent(enableNetteMailCheckBox, 1);
                
        JPanel mailVersionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mailVersionPanel.add(new JBLabel("Version:"));
        mailVersionPanel.add(netteMailVersionComboBox);
        mailVersionPanel.add(overrideDetectedNetteMailVersionCheckBox);
        
        formBuilder.addComponent(mailVersionPanel, 1)
                .addComponent(enableNetteHttpCheckBox, 1);
                
        JPanel httpVersionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        httpVersionPanel.add(new JBLabel("Version:"));
        httpVersionPanel.add(netteHttpVersionComboBox);
        httpVersionPanel.add(overrideDetectedNetteHttpVersionCheckBox);
        
        formBuilder.addComponent(httpVersionPanel, 1);
        
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
        
        enableNetteDatabaseCheckBox.addActionListener(e -> {
            boolean enabled = enableNetteDatabaseCheckBox.isSelected();
            netteDatabaseVersionComboBox.setEnabled(enabled);
            overrideDetectedNetteDatabaseVersionCheckBox.setEnabled(enabled);
        });
        
        enableNetteSecurityCheckBox.addActionListener(e -> {
            boolean enabled = enableNetteSecurityCheckBox.isSelected();
            netteSecurityVersionComboBox.setEnabled(enabled);
            overrideDetectedNetteSecurityVersionCheckBox.setEnabled(enabled);
        });
        
        enableNetteMailCheckBox.addActionListener(e -> {
            boolean enabled = enableNetteMailCheckBox.isSelected();
            netteMailVersionComboBox.setEnabled(enabled);
            overrideDetectedNetteMailVersionCheckBox.setEnabled(enabled);
        });
        
        enableNetteHttpCheckBox.addActionListener(e -> {
            boolean enabled = enableNetteHttpCheckBox.isSelected();
            netteHttpVersionComboBox.setEnabled(enabled);
            overrideDetectedNetteHttpVersionCheckBox.setEnabled(enabled);
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
        modified |= enableNetteDatabaseCheckBox.isSelected() != settings.isEnableNetteDatabase();
        modified |= enableNetteSecurityCheckBox.isSelected() != settings.isEnableNetteSecurity();
        modified |= enableNetteMailCheckBox.isSelected() != settings.isEnableNetteMail();
        modified |= enableNetteHttpCheckBox.isSelected() != settings.isEnableNetteHttp();
        
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
        
        String selectedNetteDatabaseVersion = (String) netteDatabaseVersionComboBox.getSelectedItem();
        if (selectedNetteDatabaseVersion != null && settings.getSelectedNetteDatabaseVersion() != null) {
            modified |= !selectedNetteDatabaseVersion.equals(settings.getSelectedNetteDatabaseVersion());
        } else if (selectedNetteDatabaseVersion != null || settings.getSelectedNetteDatabaseVersion() != null) {
            modified = true;
        }
        modified |= overrideDetectedNetteDatabaseVersionCheckBox.isSelected() != settings.isOverrideDetectedNetteDatabaseVersion();
        
        String selectedNetteSecurityVersion = (String) netteSecurityVersionComboBox.getSelectedItem();
        if (selectedNetteSecurityVersion != null && settings.getSelectedNetteSecurityVersion() != null) {
            modified |= !selectedNetteSecurityVersion.equals(settings.getSelectedNetteSecurityVersion());
        } else if (selectedNetteSecurityVersion != null || settings.getSelectedNetteSecurityVersion() != null) {
            modified = true;
        }
        modified |= overrideDetectedNetteSecurityVersionCheckBox.isSelected() != settings.isOverrideDetectedNetteSecurityVersion();
        
        String selectedNetteMailVersion = (String) netteMailVersionComboBox.getSelectedItem();
        if (selectedNetteMailVersion != null && settings.getSelectedNetteMailVersion() != null) {
            modified |= !selectedNetteMailVersion.equals(settings.getSelectedNetteMailVersion());
        } else if (selectedNetteMailVersion != null || settings.getSelectedNetteMailVersion() != null) {
            modified = true;
        }
        modified |= overrideDetectedNetteMailVersionCheckBox.isSelected() != settings.isOverrideDetectedNetteMailVersion();
        
        String selectedNetteHttpVersion = (String) netteHttpVersionComboBox.getSelectedItem();
        if (selectedNetteHttpVersion != null && settings.getSelectedNetteHttpVersion() != null) {
            modified |= !selectedNetteHttpVersion.equals(settings.getSelectedNetteHttpVersion());
        } else if (selectedNetteHttpVersion != null || settings.getSelectedNetteHttpVersion() != null) {
            modified = true;
        }
        modified |= overrideDetectedNetteHttpVersionCheckBox.isSelected() != settings.isOverrideDetectedNetteHttpVersion();
        
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
        
        // Apply nette/database settings
        settings.setEnableNetteDatabase(enableNetteDatabaseCheckBox.isSelected());
        settings.setSelectedNetteDatabaseVersion((String) netteDatabaseVersionComboBox.getSelectedItem());
        settings.setOverrideDetectedNetteDatabaseVersion(overrideDetectedNetteDatabaseVersionCheckBox.isSelected());
        
        // Apply nette/security settings
        settings.setEnableNetteSecurity(enableNetteSecurityCheckBox.isSelected());
        settings.setSelectedNetteSecurityVersion((String) netteSecurityVersionComboBox.getSelectedItem());
        settings.setOverrideDetectedNetteSecurityVersion(overrideDetectedNetteSecurityVersionCheckBox.isSelected());
        
        // Apply nette/mail settings
        settings.setEnableNetteMail(enableNetteMailCheckBox.isSelected());
        settings.setSelectedNetteMailVersion((String) netteMailVersionComboBox.getSelectedItem());
        settings.setOverrideDetectedNetteMailVersion(overrideDetectedNetteMailVersionCheckBox.isSelected());
        
        // Apply nette/http settings
        settings.setEnableNetteHttp(enableNetteHttpCheckBox.isSelected());
        settings.setSelectedNetteHttpVersion((String) netteHttpVersionComboBox.getSelectedItem());
        settings.setOverrideDetectedNetteHttpVersion(overrideDetectedNetteHttpVersionCheckBox.isSelected());
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
        
        // Reset nette/database settings
        enableNetteDatabaseCheckBox.setSelected(settings.isEnableNetteDatabase());
        netteDatabaseVersionComboBox.setSelectedItem(settings.getSelectedNetteDatabaseVersion() != null ? 
                settings.getSelectedNetteDatabaseVersion() : "3");
        overrideDetectedNetteDatabaseVersionCheckBox.setSelected(settings.isOverrideDetectedNetteDatabaseVersion());
        
        // Update enabled state of nette/database version settings
        boolean databaseEnabled = enableNetteDatabaseCheckBox.isSelected();
        netteDatabaseVersionComboBox.setEnabled(databaseEnabled);
        overrideDetectedNetteDatabaseVersionCheckBox.setEnabled(databaseEnabled);
        
        // Reset nette/security settings
        enableNetteSecurityCheckBox.setSelected(settings.isEnableNetteSecurity());
        netteSecurityVersionComboBox.setSelectedItem(settings.getSelectedNetteSecurityVersion() != null ? 
                settings.getSelectedNetteSecurityVersion() : "3");
        overrideDetectedNetteSecurityVersionCheckBox.setSelected(settings.isOverrideDetectedNetteSecurityVersion());
        
        // Update enabled state of nette/security version settings
        boolean securityEnabled = enableNetteSecurityCheckBox.isSelected();
        netteSecurityVersionComboBox.setEnabled(securityEnabled);
        overrideDetectedNetteSecurityVersionCheckBox.setEnabled(securityEnabled);
        
        // Reset nette/mail settings
        enableNetteMailCheckBox.setSelected(settings.isEnableNetteMail());
        netteMailVersionComboBox.setSelectedItem(settings.getSelectedNetteMailVersion() != null ? 
                settings.getSelectedNetteMailVersion() : "3");
        overrideDetectedNetteMailVersionCheckBox.setSelected(settings.isOverrideDetectedNetteMailVersion());
        
        // Update enabled state of nette/mail version settings
        boolean mailEnabled = enableNetteMailCheckBox.isSelected();
        netteMailVersionComboBox.setEnabled(mailEnabled);
        overrideDetectedNetteMailVersionCheckBox.setEnabled(mailEnabled);
        
        // Reset nette/http settings
        enableNetteHttpCheckBox.setSelected(settings.isEnableNetteHttp());
        netteHttpVersionComboBox.setSelectedItem(settings.getSelectedNetteHttpVersion() != null ? 
                settings.getSelectedNetteHttpVersion() : "3");
        overrideDetectedNetteHttpVersionCheckBox.setSelected(settings.isOverrideDetectedNetteHttpVersion());
        
        // Update enabled state of nette/http version settings
        boolean httpEnabled = enableNetteHttpCheckBox.isSelected();
        netteHttpVersionComboBox.setEnabled(httpEnabled);
        overrideDetectedNetteHttpVersionCheckBox.setEnabled(httpEnabled);
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return versionComboBox;
    }
}