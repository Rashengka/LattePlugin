package cz.hqm.latte.plugin.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import cz.hqm.latte.plugin.completion.NetteDefaultVariablesProvider;
import cz.hqm.latte.plugin.filters.NetteFilterProvider;
import cz.hqm.latte.plugin.version.LatteVersion;

/**
 * Persistent settings for the Latte plugin.
 * Stores user preferences such as the selected Latte version and enabled Nette packages.
 */
@Service
@State(
    name = "cz.hqm.latte.plugin.settings.LatteSettings",
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
     * Whether to enable support for nette/database package.
     * Default is true.
     */
    private boolean enableNetteDatabase = true;
    
    /**
     * Whether to enable support for nette/security package.
     * Default is true.
     */
    private boolean enableNetteSecurity = true;
    
    /**
     * Whether to enable support for nette/mail package.
     * Default is true.
     */
    private boolean enableNetteMail = true;
    
    /**
     * Whether to enable support for nette/http package.
     * Default is true.
     */
    private boolean enableNetteHttp = true;
    
    /**
     * The selected nette/security version.
     * If null, the plugin will try to auto-detect the version from composer.json.
     */
    private String selectedNetteSecurityVersion;
    
    /**
     * The selected nette/mail version.
     * If null, the plugin will try to auto-detect the version from composer.json.
     */
    private String selectedNetteMailVersion;
    
    /**
     * The selected nette/http version.
     * If null, the plugin will try to auto-detect the version from composer.json.
     */
    private String selectedNetteHttpVersion;
    
    /**
     * Whether to override the detected nette/security version with the manually selected version.
     */
    private boolean overrideDetectedNetteSecurityVersion = false;
    
    /**
     * Whether to override the detected nette/mail version with the manually selected version.
     */
    private boolean overrideDetectedNetteMailVersion = false;
    
    /**
     * Whether to override the detected nette/http version with the manually selected version.
     */
    private boolean overrideDetectedNetteHttpVersion = false;
    
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
     * The selected nette/database version.
     * If null, the plugin will try to auto-detect the version from composer.json.
     */
    private String selectedNetteDatabaseVersion;
    
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
     * Whether to override the detected nette/database version with the manually selected version.
     */
    private boolean overrideDetectedNetteDatabaseVersion = false;
    
    /**
     * Gets the instance of the settings service.
     * In test environment, returns a default instance with default settings.
     *
     * @return The settings instance
     */
    public static LatteSettings getInstance() {
        var application = ApplicationManager.getApplication();
        if (application == null) {
            // We're in a test environment, return a default instance
            return new LatteSettings();
        }
        return application.getService(LatteSettings.class);
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
        // Invalidate caches when settings change
        NetteDefaultVariablesProvider.invalidateCache();
        NetteFilterProvider.invalidateCache();
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
        // Invalidate caches when settings change
        NetteDefaultVariablesProvider.invalidateCache();
        NetteFilterProvider.invalidateCache();
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
        // Invalidate the variables cache when settings change
        NetteDefaultVariablesProvider.invalidateCache();
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
        // Invalidate the variables cache when settings change
        NetteDefaultVariablesProvider.invalidateCache();
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
        // Invalidate the variables cache when settings change
        NetteDefaultVariablesProvider.invalidateCache();
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
        // Invalidate the variables cache when settings change
        NetteDefaultVariablesProvider.invalidateCache();
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
        // Invalidate the variables cache when settings change
        NetteDefaultVariablesProvider.invalidateCache();
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
        // Invalidate the variables cache when settings change
        NetteDefaultVariablesProvider.invalidateCache();
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
        // Invalidate the variables cache when settings change
        NetteDefaultVariablesProvider.invalidateCache();
    }
    
    /**
     * Gets whether support for nette/database package is enabled.
     *
     * @return True if nette/database support is enabled, false otherwise
     */
    public boolean isEnableNetteDatabase() {
        return enableNetteDatabase;
    }
    
    /**
     * Sets whether support for nette/database package is enabled.
     *
     * @param enableNetteDatabase True to enable nette/database support, false to disable
     */
    public void setEnableNetteDatabase(boolean enableNetteDatabase) {
        this.enableNetteDatabase = enableNetteDatabase;
        // Invalidate the variables cache when settings change
        NetteDefaultVariablesProvider.invalidateCache();
    }
    
    /**
     * Gets the selected nette/database version.
     *
     * @return The selected version or null if not set
     */
    @Nullable
    public String getSelectedNetteDatabaseVersion() {
        return selectedNetteDatabaseVersion;
    }
    
    /**
     * Sets the selected nette/database version.
     *
     * @param selectedNetteDatabaseVersion The version to set
     */
    public void setSelectedNetteDatabaseVersion(@Nullable String selectedNetteDatabaseVersion) {
        this.selectedNetteDatabaseVersion = selectedNetteDatabaseVersion;
        // Invalidate the variables cache when settings change
        NetteDefaultVariablesProvider.invalidateCache();
    }
    
    /**
     * Gets whether to override the detected nette/database version with the manually selected version.
     *
     * @return True if the manually selected version should be used, false otherwise
     */
    public boolean isOverrideDetectedNetteDatabaseVersion() {
        return overrideDetectedNetteDatabaseVersion;
    }
    
    /**
     * Sets whether to override the detected nette/database version with the manually selected version.
     *
     * @param overrideDetectedNetteDatabaseVersion True if the manually selected version should be used, false otherwise
     */
    public void setOverrideDetectedNetteDatabaseVersion(boolean overrideDetectedNetteDatabaseVersion) {
        this.overrideDetectedNetteDatabaseVersion = overrideDetectedNetteDatabaseVersion;
        // Invalidate the variables cache when settings change
        NetteDefaultVariablesProvider.invalidateCache();
    }
    
    /**
     * Gets whether support for nette/security package is enabled.
     *
     * @return True if nette/security support is enabled, false otherwise
     */
    public boolean isEnableNetteSecurity() {
        return enableNetteSecurity;
    }
    
    /**
     * Sets whether support for nette/security package is enabled.
     *
     * @param enableNetteSecurity True to enable nette/security support, false to disable
     */
    public void setEnableNetteSecurity(boolean enableNetteSecurity) {
        this.enableNetteSecurity = enableNetteSecurity;
        // Invalidate the variables cache when settings change
        NetteDefaultVariablesProvider.invalidateCache();
    }
    
    /**
     * Gets the selected nette/security version.
     *
     * @return The selected version or null if not set
     */
    @Nullable
    public String getSelectedNetteSecurityVersion() {
        return selectedNetteSecurityVersion;
    }
    
    /**
     * Sets the selected nette/security version.
     *
     * @param selectedNetteSecurityVersion The version to set
     */
    public void setSelectedNetteSecurityVersion(@Nullable String selectedNetteSecurityVersion) {
        this.selectedNetteSecurityVersion = selectedNetteSecurityVersion;
        // Invalidate the variables cache when settings change
        NetteDefaultVariablesProvider.invalidateCache();
    }
    
    /**
     * Gets whether to override the detected nette/security version with the manually selected version.
     *
     * @return True if the manually selected version should be used, false otherwise
     */
    public boolean isOverrideDetectedNetteSecurityVersion() {
        return overrideDetectedNetteSecurityVersion;
    }
    
    /**
     * Sets whether to override the detected nette/security version with the manually selected version.
     *
     * @param overrideDetectedNetteSecurityVersion True if the manually selected version should be used, false otherwise
     */
    public void setOverrideDetectedNetteSecurityVersion(boolean overrideDetectedNetteSecurityVersion) {
        this.overrideDetectedNetteSecurityVersion = overrideDetectedNetteSecurityVersion;
        // Invalidate the variables cache when settings change
        NetteDefaultVariablesProvider.invalidateCache();
    }
    
    /**
     * Gets whether support for nette/mail package is enabled.
     *
     * @return True if nette/mail support is enabled, false otherwise
     */
    public boolean isEnableNetteMail() {
        return enableNetteMail;
    }
    
    /**
     * Sets whether support for nette/mail package is enabled.
     *
     * @param enableNetteMail True to enable nette/mail support, false to disable
     */
    public void setEnableNetteMail(boolean enableNetteMail) {
        this.enableNetteMail = enableNetteMail;
        // Invalidate the variables cache when settings change
        NetteDefaultVariablesProvider.invalidateCache();
    }
    
    /**
     * Gets the selected nette/mail version.
     *
     * @return The selected version or null if not set
     */
    @Nullable
    public String getSelectedNetteMailVersion() {
        return selectedNetteMailVersion;
    }
    
    /**
     * Sets the selected nette/mail version.
     *
     * @param selectedNetteMailVersion The version to set
     */
    public void setSelectedNetteMailVersion(@Nullable String selectedNetteMailVersion) {
        this.selectedNetteMailVersion = selectedNetteMailVersion;
        // Invalidate the variables cache when settings change
        NetteDefaultVariablesProvider.invalidateCache();
    }
    
    /**
     * Gets whether to override the detected nette/mail version with the manually selected version.
     *
     * @return True if the manually selected version should be used, false otherwise
     */
    public boolean isOverrideDetectedNetteMailVersion() {
        return overrideDetectedNetteMailVersion;
    }
    
    /**
     * Sets whether to override the detected nette/mail version with the manually selected version.
     *
     * @param overrideDetectedNetteMailVersion True if the manually selected version should be used, false otherwise
     */
    public void setOverrideDetectedNetteMailVersion(boolean overrideDetectedNetteMailVersion) {
        this.overrideDetectedNetteMailVersion = overrideDetectedNetteMailVersion;
        // Invalidate the variables cache when settings change
        NetteDefaultVariablesProvider.invalidateCache();
    }
    
    /**
     * Gets whether support for nette/http package is enabled.
     *
     * @return True if nette/http support is enabled, false otherwise
     */
    public boolean isEnableNetteHttp() {
        return enableNetteHttp;
    }
    
    /**
     * Sets whether support for nette/http package is enabled.
     *
     * @param enableNetteHttp True to enable nette/http support, false to disable
     */
    public void setEnableNetteHttp(boolean enableNetteHttp) {
        this.enableNetteHttp = enableNetteHttp;
        // Invalidate the variables cache when settings change
        NetteDefaultVariablesProvider.invalidateCache();
    }
    
    /**
     * Gets the selected nette/http version.
     *
     * @return The selected version or null if not set
     */
    @Nullable
    public String getSelectedNetteHttpVersion() {
        return selectedNetteHttpVersion;
    }
    
    /**
     * Sets the selected nette/http version.
     *
     * @param selectedNetteHttpVersion The version to set
     */
    public void setSelectedNetteHttpVersion(@Nullable String selectedNetteHttpVersion) {
        this.selectedNetteHttpVersion = selectedNetteHttpVersion;
        // Invalidate the variables cache when settings change
        NetteDefaultVariablesProvider.invalidateCache();
    }
    
    /**
     * Gets whether to override the detected nette/http version with the manually selected version.
     *
     * @return True if the manually selected version should be used, false otherwise
     */
    public boolean isOverrideDetectedNetteHttpVersion() {
        return overrideDetectedNetteHttpVersion;
    }
    
    /**
     * Sets whether to override the detected nette/http version with the manually selected version.
     *
     * @param overrideDetectedNetteHttpVersion True if the manually selected version should be used, false otherwise
     */
    public void setOverrideDetectedNetteHttpVersion(boolean overrideDetectedNetteHttpVersion) {
        this.overrideDetectedNetteHttpVersion = overrideDetectedNetteHttpVersion;
        // Invalidate the variables cache when settings change
        NetteDefaultVariablesProvider.invalidateCache();
    }
}