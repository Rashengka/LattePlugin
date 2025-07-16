package org.latte.plugin.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.latte.plugin.version.LatteVersion;

/**
 * Persistent settings for the Latte plugin.
 * Stores user preferences such as the selected Latte version and enabled Nette packages.
 */
@Service
@State(
    name = "org.latte.plugin.settings.LatteSettings",
    storages = @Storage("LattePluginSettings.xml")
)
public final class LatteSettings implements PersistentStateComponent<LatteSettings> {
    
    /**
     * The selected Latte version.
     * If null, the plugin will try to auto-detect the version from composer.json.
     */
    private String selectedVersion;
    
    /**
     * Whether to override the detected version with the manually selected version.
     */
    private boolean overrideDetectedVersion = false;
    
    /**
     * Whether to enable support for nette/application package.
     * Default is true.
     */
    private boolean enableNetteApplication = true;
    
    /**
     * Whether to enable support for nette/forms package.
     * Default is true.
     */
    private boolean enableNetteForms = true;
    
    /**
     * Whether to enable support for nette/assets package.
     * Default is true.
     */
    private boolean enableNetteAssets = true;
    
    /**
     * The selected nette/application version.
     * If null, the plugin will try to auto-detect the version from composer.json.
     */
    private String selectedNetteApplicationVersion;
    
    /**
     * The selected nette/forms version.
     * If null, the plugin will try to auto-detect the version from composer.json.
     */
    private String selectedNetteFormsVersion;
    
    /**
     * The selected nette/assets version.
     * If null, the plugin will try to auto-detect the version from composer.json.
     */
    private String selectedNetteAssetsVersion;
    
    /**
     * Whether to override the detected nette/application version with the manually selected version.
     */
    private boolean overrideDetectedNetteApplicationVersion = false;
    
    /**
     * Whether to override the detected nette/forms version with the manually selected version.
     */
    private boolean overrideDetectedNetteFormsVersion = false;
    
    /**
     * Whether to override the detected nette/assets version with the manually selected version.
     */
    private boolean overrideDetectedNetteAssetsVersion = false;
    
    /**
     * Gets the instance of the settings service.
     *
     * @return The settings instance
     */
    public static LatteSettings getInstance() {
        return ApplicationManager.getApplication().getService(LatteSettings.class);
    }
    
    /**
     * Gets the selected Latte version.
     *
     * @return The selected version or null if not set
     */
    @Nullable
    public String getSelectedVersion() {
        return selectedVersion;
    }
    
    /**
     * Sets the selected Latte version.
     *
     * @param selectedVersion The version to set
     */
    public void setSelectedVersion(@Nullable String selectedVersion) {
        this.selectedVersion = selectedVersion;
    }
    
    /**
     * Gets whether to override the detected version with the manually selected version.
     *
     * @return True if the manually selected version should be used, false otherwise
     */
    public boolean isOverrideDetectedVersion() {
        return overrideDetectedVersion;
    }
    
    /**
     * Sets whether to override the detected version with the manually selected version.
     *
     * @param overrideDetectedVersion True if the manually selected version should be used, false otherwise
     */
    public void setOverrideDetectedVersion(boolean overrideDetectedVersion) {
        this.overrideDetectedVersion = overrideDetectedVersion;
    }
    
    /**
     * Gets the selected Latte version as an enum.
     *
     * @return The selected version as an enum or the default version if not set
     */
    public LatteVersion getSelectedVersionEnum() {
        if (selectedVersion == null) {
            return LatteVersion.getDefault();
        }
        
        return switch (selectedVersion) {
            case "2.x" -> LatteVersion.VERSION_2X;
            case "3.0+" -> LatteVersion.VERSION_3X;
            default -> LatteVersion.getDefault();
        };
    }
    
    /**
     * Sets the selected Latte version from an enum.
     *
     * @param version The version to set
     */
    public void setSelectedVersionEnum(LatteVersion version) {
        this.selectedVersion = version.getDisplayName();
    }
    
    @Nullable
    @Override
    public LatteSettings getState() {
        return this;
    }
    
    @Override
    public void loadState(@NotNull LatteSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
    
    /**
     * Gets whether support for nette/application package is enabled.
     *
     * @return True if nette/application support is enabled, false otherwise
     */
    public boolean isEnableNetteApplication() {
        return enableNetteApplication;
    }
    
    /**
     * Sets whether support for nette/application package is enabled.
     *
     * @param enableNetteApplication True to enable nette/application support, false to disable
     */
    public void setEnableNetteApplication(boolean enableNetteApplication) {
        this.enableNetteApplication = enableNetteApplication;
    }
    
    /**
     * Gets whether support for nette/forms package is enabled.
     *
     * @return True if nette/forms support is enabled, false otherwise
     */
    public boolean isEnableNetteForms() {
        return enableNetteForms;
    }
    
    /**
     * Sets whether support for nette/forms package is enabled.
     *
     * @param enableNetteForms True to enable nette/forms support, false to disable
     */
    public void setEnableNetteForms(boolean enableNetteForms) {
        this.enableNetteForms = enableNetteForms;
    }
    
    /**
     * Gets whether support for nette/assets package is enabled.
     *
     * @return True if nette/assets support is enabled, false otherwise
     */
    public boolean isEnableNetteAssets() {
        return enableNetteAssets;
    }
    
    /**
     * Sets whether support for nette/assets package is enabled.
     *
     * @param enableNetteAssets True to enable nette/assets support, false to disable
     */
    public void setEnableNetteAssets(boolean enableNetteAssets) {
        this.enableNetteAssets = enableNetteAssets;
    }
    
    /**
     * Gets the selected nette/application version.
     *
     * @return The selected version or null if not set
     */
    @Nullable
    public String getSelectedNetteApplicationVersion() {
        return selectedNetteApplicationVersion;
    }
    
    /**
     * Sets the selected nette/application version.
     *
     * @param selectedNetteApplicationVersion The version to set
     */
    public void setSelectedNetteApplicationVersion(@Nullable String selectedNetteApplicationVersion) {
        this.selectedNetteApplicationVersion = selectedNetteApplicationVersion;
    }
    
    /**
     * Gets whether to override the detected nette/application version with the manually selected version.
     *
     * @return True if the manually selected version should be used, false otherwise
     */
    public boolean isOverrideDetectedNetteApplicationVersion() {
        return overrideDetectedNetteApplicationVersion;
    }
    
    /**
     * Sets whether to override the detected nette/application version with the manually selected version.
     *
     * @param overrideDetectedNetteApplicationVersion True if the manually selected version should be used, false otherwise
     */
    public void setOverrideDetectedNetteApplicationVersion(boolean overrideDetectedNetteApplicationVersion) {
        this.overrideDetectedNetteApplicationVersion = overrideDetectedNetteApplicationVersion;
    }
    
    /**
     * Gets the selected nette/forms version.
     *
     * @return The selected version or null if not set
     */
    @Nullable
    public String getSelectedNetteFormsVersion() {
        return selectedNetteFormsVersion;
    }
    
    /**
     * Sets the selected nette/forms version.
     *
     * @param selectedNetteFormsVersion The version to set
     */
    public void setSelectedNetteFormsVersion(@Nullable String selectedNetteFormsVersion) {
        this.selectedNetteFormsVersion = selectedNetteFormsVersion;
    }
    
    /**
     * Gets whether to override the detected nette/forms version with the manually selected version.
     *
     * @return True if the manually selected version should be used, false otherwise
     */
    public boolean isOverrideDetectedNetteFormsVersion() {
        return overrideDetectedNetteFormsVersion;
    }
    
    /**
     * Sets whether to override the detected nette/forms version with the manually selected version.
     *
     * @param overrideDetectedNetteFormsVersion True if the manually selected version should be used, false otherwise
     */
    public void setOverrideDetectedNetteFormsVersion(boolean overrideDetectedNetteFormsVersion) {
        this.overrideDetectedNetteFormsVersion = overrideDetectedNetteFormsVersion;
    }
    
    /**
     * Gets the selected nette/assets version.
     *
     * @return The selected version or null if not set
     */
    @Nullable
    public String getSelectedNetteAssetsVersion() {
        return selectedNetteAssetsVersion;
    }
    
    /**
     * Sets the selected nette/assets version.
     *
     * @param selectedNetteAssetsVersion The version to set
     */
    public void setSelectedNetteAssetsVersion(@Nullable String selectedNetteAssetsVersion) {
        this.selectedNetteAssetsVersion = selectedNetteAssetsVersion;
    }
    
    /**
     * Gets whether to override the detected nette/assets version with the manually selected version.
     *
     * @return True if the manually selected version should be used, false otherwise
     */
    public boolean isOverrideDetectedNetteAssetsVersion() {
        return overrideDetectedNetteAssetsVersion;
    }
    
    /**
     * Sets whether to override the detected nette/assets version with the manually selected version.
     *
     * @param overrideDetectedNetteAssetsVersion True if the manually selected version should be used, false otherwise
     */
    public void setOverrideDetectedNetteAssetsVersion(boolean overrideDetectedNetteAssetsVersion) {
        this.overrideDetectedNetteAssetsVersion = overrideDetectedNetteAssetsVersion;
    }
}