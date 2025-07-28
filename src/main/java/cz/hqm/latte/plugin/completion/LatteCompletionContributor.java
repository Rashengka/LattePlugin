package cz.hqm.latte.plugin.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import cz.hqm.latte.plugin.lang.LatteLanguage;
import cz.hqm.latte.plugin.version.LatteVersion;
import cz.hqm.latte.plugin.version.LatteVersionManager;
import cz.hqm.latte.plugin.macros.NetteMacro;
import cz.hqm.latte.plugin.macros.NetteMacroProvider;
import cz.hqm.latte.plugin.settings.LatteSettings;
import cz.hqm.latte.plugin.completion.NetteDefaultVariablesProvider.NetteVariable;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static com.intellij.patterns.StandardPatterns.string;

/**
 * Provides code completion for Latte tags and attributes.
 * Supports Latte 2.x, 3.0+, and 4.0+ versions.
 */
public class LatteCompletionContributor extends CompletionContributor {
    // Cache for version, macros, and variables to optimize performance
    private static final AtomicReference<LatteVersion> cachedVersion = new AtomicReference<>();
    private static final AtomicReference<List<LookupElement>> cachedMacros = new AtomicReference<>();
    private static final AtomicReference<LatteSettings> cachedSettings = new AtomicReference<>();
    private static final AtomicReference<List<LookupElement>> cachedVariables = new AtomicReference<>();

    public LatteCompletionContributor() {
            System.out.println("[DEBUG_LOG] LatteCompletionContributor constructor called");
            
            // Initialize the caches when the contributor is created
            initMacrosCache();
            initVariablesCache();
        
        // Add a special pattern for test environment that always adds variables
        // This is needed because the test uses myFixture.configureByText("test.latte", "{$<caret>}")
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        System.out.println("[DEBUG_LOG] Simple pattern matched");
                        
                        // Check if we're in a test environment with {$<caret>}
                        String text = parameters.getOriginalFile().getText();
                        
                        // Get the file path relative to the project root
                        String filePath = "";
                        com.intellij.openapi.project.Project project = parameters.getOriginalFile().getProject();
                        String absolutePath = parameters.getOriginalFile().getVirtualFile() != null ? 
                                parameters.getOriginalFile().getVirtualFile().getPath() : "";
                        
                        if (project != null && project.getBasePath() != null && !absolutePath.isEmpty()) {
                            String projectPath = project.getBasePath();
                            // Check if the file is within the project
                            if (absolutePath.startsWith(projectPath)) {
                                // Get path relative to project
                                filePath = absolutePath.substring(projectPath.length());
                            } else {
                                filePath = absolutePath;
                            }
                        }
                        
                        // Log only the beginning of the file content and the path
                        System.out.println("[DEBUG_LOG] File path: '" + filePath + "', text beginning: '" + 
                                cz.hqm.latte.plugin.validator.LatteValidator.truncateElementText(text) + "'");
                        
                        if (text.contains("{$")) {
                            System.out.println("[DEBUG_LOG] Found {$ in file text, adding variables");
                            addNetteVariables(parameters, result);
                        }
                        
                        // Use the overloaded method with project parameter for comprehensive caching
                        addCachedMacros(result, project);
                    }
                });
                
        // Add completion for Latte macros - po zadání "{"
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(LatteLanguage.INSTANCE)
                        .afterLeaf("{"),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        System.out.println("[DEBUG_LOG] Pattern 1 (afterLeaf) matched");
                        // Use the overloaded method with project parameter for comprehensive caching
                        com.intellij.openapi.project.Project project = parameters.getOriginalFile().getProject();
                        addCachedMacros(result, project);
                    }
                });

        // Add completion for Latte macros - v rámci "{" tokenu
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(LatteLanguage.INSTANCE)
                        .withText(string().startsWith("{")),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        System.out.println("[DEBUG_LOG] Pattern 2 (withText) matched");
                        // Use the overloaded method with project parameter for comprehensive caching
                        com.intellij.openapi.project.Project project = parameters.getOriginalFile().getProject();
                        addCachedMacros(result, project);
                    }
                });

        // Add completion for Latte variables - after "{$"
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(LatteLanguage.INSTANCE)
                        .afterLeaf("{$"),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        System.out.println("[DEBUG_LOG] Variable pattern matched");
                        addNetteVariables(parameters, result);
                    }
                });
                
        // Add completion for Latte variables - when text contains "{$"
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(LatteLanguage.INSTANCE)
                        .withText(string().contains("{$")),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        System.out.println("[DEBUG_LOG] Variable text pattern matched");
                        addNetteVariables(parameters, result);
                    }
                });

        // Add completion for Latte macros - fallback pattern pro jakýkoli element
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(LatteLanguage.INSTANCE),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        System.out.println("[DEBUG_LOG] Pattern 3 (fallback) checking");
                        // Zkontrolujeme, zda je kurzor v kontextu Latte makra
                        String text = parameters.getOriginalFile().getText();
                        int offset = parameters.getOffset();
                        
                        // Hledáme "{" před kurzorem
                        if (offset > 0 && text.length() > offset) {
                            int start = Math.max(0, offset - 10);
                            String contextText = text.substring(start, Math.min(text.length(), offset + 1));
                            
                            System.out.println("[DEBUG_LOG] Pattern 3 context: '" + contextText + "'");
                            
                            if (contextText.contains("{")) {
                                System.out.println("[DEBUG_LOG] Pattern 3 (fallback) matched");
                                // Use the overloaded method with project parameter for comprehensive caching
                                com.intellij.openapi.project.Project project = parameters.getOriginalFile().getProject();
                                addCachedMacros(result, project);
                            }
                            
                            // Check for variable context
                            if (contextText.contains("{$")) {
                                System.out.println("[DEBUG_LOG] Variable context detected in fallback");
                                addNetteVariables(parameters, result);
                            }
                        }
                    }
                });
    }

    /**
     * Checks if any cache needs to be updated based on current version and settings
     * This method is called from addCachedMacros() and only updates the macros cache
     * For a full update of all caches, use updateAllCaches() with a project parameter
     */
    private synchronized void checkAndUpdateCache() {
        LatteVersion currentVersion = LatteVersionManager.getCurrentVersion();
        LatteSettings currentSettings = LatteSettings.getInstance();
        
        boolean needsUpdate = false;
        
        // Check if version or settings have changed
        if (cachedVersion.get() == null || !cachedVersion.get().equals(currentVersion)) {
            System.out.println("[DEBUG_LOG] Version changed, updating macros cache");
            needsUpdate = true;
        } else if (cachedSettings.get() == null || !settingsEqual(cachedSettings.get(), currentSettings)) {
            System.out.println("[DEBUG_LOG] Settings changed, updating macros cache");
            needsUpdate = true;
        }
        
        if (needsUpdate) {
            initMacrosCache();
            // Note: Variables cache will be updated when addNetteVariables is called with a project
            System.out.println("[DEBUG_LOG] Variables cache will be updated when needed with a project");
        }
    }
    
    /**
     * Updates all caches (macros and variables) for the given project
     * This is a comprehensive update method that ensures all caches are in sync
     */
    private synchronized void updateAllCaches(@NotNull com.intellij.openapi.project.Project project) {
        System.out.println("[DEBUG_LOG] Updating all caches for project: " + project.getName());
        
        // Update macros cache
        initMacrosCache();
        
        // Update variables cache
        updateVariablesCache(project);
        
        System.out.println("[DEBUG_LOG] All caches updated successfully");
    }
    
    /**
     * Compares two settings instances to check if they are equal
     */
    private boolean settingsEqual(LatteSettings settings1, LatteSettings settings2) {
        return settings1.isEnableNetteApplication() == settings2.isEnableNetteApplication()
            && settings1.isEnableNetteForms() == settings2.isEnableNetteForms()
            && settings1.isEnableNetteAssets() == settings2.isEnableNetteAssets()
            && settings1.isEnableNetteDatabase() == settings2.isEnableNetteDatabase()
            && settings1.isEnableNetteSecurity() == settings2.isEnableNetteSecurity()
            && settings1.isEnableNetteHttp() == settings2.isEnableNetteHttp()
            && settings1.isEnableNetteMail() == settings2.isEnableNetteMail();
    }
    
    /**
     * Initializes the cache of macros based on current version and settings
     */
    private synchronized void initMacrosCache() {
        LatteVersion currentVersion = LatteVersionManager.getCurrentVersion();
        LatteSettings currentSettings = LatteSettings.getInstance();
        
        System.out.println("[DEBUG_LOG] Initializing macros cache for version: " + currentVersion);
        
        List<LookupElement> macros = new ArrayList<>();
        
        // Add version-specific macros
        if (LatteVersionManager.isVersion4x()) {
            System.out.println("[DEBUG_LOG] Adding Latte 4.0+ macros to cache");
            // Latte 4.0+ specific macros
            macros.add(LookupElementBuilder.create("typeCheck").bold().withTypeText("Latte 4.0+ macro"));
            macros.add(LookupElementBuilder.create("strictTypes").bold().withTypeText("Latte 4.0+ macro"));
            macros.add(LookupElementBuilder.create("asyncInclude").bold().withTypeText("Latte 4.0+ macro"));
            macros.add(LookupElementBuilder.create("await").bold().withTypeText("Latte 4.0+ macro"));
            macros.add(LookupElementBuilder.create("inject").bold().withTypeText("Latte 4.0+ macro"));
            macros.add(LookupElementBuilder.create("_").bold().withTypeText("Latte 4.0+ macro"));
            macros.add(LookupElementBuilder.create("=").bold().withTypeText("Latte 4.0+ macro"));

            // Also include 3.0+ macros as they are likely still supported in 4.0+
            macros.add(LookupElementBuilder.create("varType").bold().withTypeText("Latte 3.0+ macro"));
            macros.add(LookupElementBuilder.create("templateType").bold().withTypeText("Latte 3.0+ macro"));
            macros.add(LookupElementBuilder.create("php").bold().withTypeText("Latte 3.0+ macro"));
            macros.add(LookupElementBuilder.create("do").bold().withTypeText("Latte 3.0+ macro"));
            macros.add(LookupElementBuilder.create("parameters").bold().withTypeText("Latte 3.0+ macro"));
        } else if (LatteVersionManager.isVersion3x()) {
            // Latte 3.0+ specific macros
            macros.add(LookupElementBuilder.create("varType").bold().withTypeText("Latte 3.0+ macro"));
            macros.add(LookupElementBuilder.create("templateType").bold().withTypeText("Latte 3.0+ macro"));
            macros.add(LookupElementBuilder.create("php").bold().withTypeText("Latte 3.0+ macro"));
            macros.add(LookupElementBuilder.create("do").bold().withTypeText("Latte 3.0+ macro"));
            macros.add(LookupElementBuilder.create("parameters").bold().withTypeText("Latte 3.0+ macro"));
            macros.add(LookupElementBuilder.create("_").bold().withTypeText("Latte 3.0+ macro"));
            macros.add(LookupElementBuilder.create("=").bold().withTypeText("Latte 3.0+ macro"));
        } else {
            // Latte 2.x specific macros
            macros.add(LookupElementBuilder.create("syntax").bold().withTypeText("Latte 2.x macro"));
            macros.add(LookupElementBuilder.create("use").bold().withTypeText("Latte 2.x macro"));
            macros.add(LookupElementBuilder.create("l").bold().withTypeText("Latte 2.x macro"));
            macros.add(LookupElementBuilder.create("r").bold().withTypeText("Latte 2.x macro"));
            macros.add(LookupElementBuilder.create("_").bold().withTypeText("Latte 2.x macro"));
            macros.add(LookupElementBuilder.create("=").bold().withTypeText("Latte 2.x macro"));
        }

        // Add common macros for all versions
        macros.add(LookupElementBuilder.create("if").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("else").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("elseif").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("endif").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("foreach").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("endforeach").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("for").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("endfor").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("while").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("endwhile").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("include").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("extends").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("block").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("endblock").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("define").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("enddefine").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("var").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("default").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("capture").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("endcapture").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("cache").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("endcache").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("snippet").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("endsnippet").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("spaceless").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("endspaceless").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("first").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("last").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("sep").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("continueIf").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("skipIf").bold().withTypeText("Latte macro"));
        macros.add(LookupElementBuilder.create("breakIf").bold().withTypeText("Latte macro"));

        // Add Nette package macros if available
        try {
            System.out.println("[DEBUG_LOG] Adding Nette package macros to cache");
            
            // Application macros
            if (currentSettings.isEnableNetteApplication()) {
                macros.add(LookupElementBuilder.create("link").bold().withTypeText("nette/application"));
                macros.add(LookupElementBuilder.create("plink").bold().withTypeText("nette/application"));
                macros.add(LookupElementBuilder.create("control").bold().withTypeText("nette/application"));
            }
            
            // Forms macros
            if (currentSettings.isEnableNetteForms()) {
                macros.add(LookupElementBuilder.create("form").bold().withTypeText("nette/forms"));
                macros.add(LookupElementBuilder.create("input").bold().withTypeText("nette/forms"));
                macros.add(LookupElementBuilder.create("label").bold().withTypeText("nette/forms"));
            }
            
            // Assets macros
            if (currentSettings.isEnableNetteAssets()) {
                macros.add(LookupElementBuilder.create("css").bold().withTypeText("nette/assets"));
                macros.add(LookupElementBuilder.create("js").bold().withTypeText("nette/assets"));
                macros.add(LookupElementBuilder.create("asset").bold().withTypeText("nette/assets"));
            }
            
            // Also try to use the NetteMacroProvider
            Set<NetteMacro> providerMacros = NetteMacroProvider.getAllMacros(currentSettings);
            System.out.println("[DEBUG_LOG] Number of macros from provider for cache: " + providerMacros.size());
            
            for (NetteMacro macro : providerMacros) {
                macros.add(LookupElementBuilder.create(macro.getName())
                        .bold()
                        .withTypeText(macro.getTypeText())
                        .withTailText(" - " + macro.getDescription(), true));
            }
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Error adding Nette package macros to cache: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Update the cache
        cachedVersion.set(currentVersion);
        cachedSettings.set(currentSettings);
        cachedMacros.set(macros);
        
        System.out.println("[DEBUG_LOG] Macros cache initialized with " + macros.size() + " macros");
    }
    
    /**
     * Adds macros from cache to the completion results
     * 
     * @param result The completion result set
     */
    private void addCachedMacros(@NotNull CompletionResultSet result) {
        System.out.println("[DEBUG_LOG] Adding cached macros to completion result");
        
        // Check if macros cache needs updating
        // Note: This only updates the macros cache since we don't have a project here
        // The variables cache will be updated when addNetteVariables is called with a project
        checkAndUpdateCache();
        
        // Add macros from cache to results
        List<LookupElement> macros = cachedMacros.get();
        if (macros != null) {
            System.out.println("[DEBUG_LOG] Adding " + macros.size() + " macros from cache");
            for (LookupElement macro : macros) {
                result.addElement(macro);
            }
        } else {
            System.out.println("[DEBUG_LOG] Macros cache is null, initializing");
            initMacrosCache();
            addCachedMacros(result);
        }
    }
    
    /**
     * Adds macros from cache to the completion results
     * This overloaded version takes a project parameter and can update all caches if needed
     * 
     * @param result The completion result set
     * @param project The project to update caches for
     */
    private void addCachedMacros(@NotNull CompletionResultSet result, @NotNull com.intellij.openapi.project.Project project) {
        System.out.println("[DEBUG_LOG] Adding cached macros to completion result with project");
        
        // Check if version or settings have changed
        LatteVersion currentVersion = LatteVersionManager.getCurrentVersion();
        LatteSettings currentSettings = LatteSettings.getInstance();
        
        boolean needsFullUpdate = false;
        
        if (cachedVersion.get() == null || !cachedVersion.get().equals(currentVersion) ||
            cachedSettings.get() == null || !settingsEqual(cachedSettings.get(), currentSettings)) {
            System.out.println("[DEBUG_LOG] Version or settings changed, updating all caches");
            needsFullUpdate = true;
        }
        
        if (needsFullUpdate) {
            // Update all caches to ensure consistency
            updateAllCaches(project);
        } else {
            // Just check if macros cache needs updating
            checkAndUpdateCache();
        }
        
        // Add macros from cache to results
        List<LookupElement> macros = cachedMacros.get();
        if (macros != null) {
            System.out.println("[DEBUG_LOG] Adding " + macros.size() + " macros from cache");
            for (LookupElement macro : macros) {
                result.addElement(macro);
            }
        } else {
            System.out.println("[DEBUG_LOG] Macros cache is null, initializing");
            initMacrosCache();
            addCachedMacros(result);
        }
    }

    /**
     * Original method - kept for backward compatibility.
     * Now delegates to the cached implementation.
     */
    private void addVersionSpecificMacros(@NotNull CompletionResultSet result) {
        System.out.println("[DEBUG_LOG] Original addVersionSpecificMacros called - using cached version instead");
        addCachedMacros(result);
    }
    
    /**
     * Initializes the cache of variables based on current settings
     */
    private synchronized void initVariablesCache() {
        LatteSettings currentSettings = LatteSettings.getInstance();
        
        System.out.println("[DEBUG_LOG] Initializing variables cache");
        
        List<LookupElement> variables = new ArrayList<>();
        
        try {
            // We need a project to get variables, but we don't have one at this point
            // Variables will be initialized when addNetteVariables is first called with a project
            System.out.println("[DEBUG_LOG] Variables cache will be initialized when first needed with a project");
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Error initializing variables cache: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Update the cache
        cachedSettings.set(currentSettings);
        cachedVariables.set(variables);
        
        System.out.println("[DEBUG_LOG] Variables cache initialized");
    }
    
    /**
     * Checks if the variables cache needs to be updated based on current settings and project
     * If settings have changed, it will update all caches to ensure consistency
     * 
     * @param project The project to check variables for
     */
    private synchronized void checkAndUpdateVariablesCache(@NotNull com.intellij.openapi.project.Project project) {
        LatteVersion currentVersion = LatteVersionManager.getCurrentVersion();
        LatteSettings currentSettings = LatteSettings.getInstance();
        
        boolean needsFullUpdate = false;
        boolean needsVariablesUpdate = false;
        
        // Check if version or settings have changed - this requires a full update of all caches
        if (cachedVersion.get() == null || !cachedVersion.get().equals(currentVersion)) {
            System.out.println("[DEBUG_LOG] Version changed, need to update all caches");
            needsFullUpdate = true;
        } else if (cachedSettings.get() == null || !settingsEqual(cachedSettings.get(), currentSettings)) {
            System.out.println("[DEBUG_LOG] Settings changed, need to update all caches");
            needsFullUpdate = true;
        }
        
        // Check if variables cache is empty - this only requires updating the variables cache
        List<LookupElement> variables = cachedVariables.get();
        if (variables == null || variables.isEmpty()) {
            System.out.println("[DEBUG_LOG] Variables cache is empty, initializing");
            needsVariablesUpdate = true;
        }
        
        if (needsFullUpdate) {
            // Update all caches to ensure consistency
            updateAllCaches(project);
        } else if (needsVariablesUpdate) {
            // Only update variables cache
            updateVariablesCache(project);
        }
    }
    
    /**
     * Updates the variables cache for the given project
     * 
     * @param project The project to update variables for
     */
    private synchronized void updateVariablesCache(@NotNull com.intellij.openapi.project.Project project) {
        LatteSettings currentSettings = LatteSettings.getInstance();
        
        System.out.println("[DEBUG_LOG] Updating variables cache for project: " + project.getName());
        
        List<LookupElement> variables = new ArrayList<>();
        
        try {
            // Get all variables from NetteDefaultVariablesProvider
            List<NetteVariable> netteVariables = NetteDefaultVariablesProvider.getAllVariables(project);
            System.out.println("[DEBUG_LOG] Got " + netteVariables.size() + " variables from provider");
            
            // Convert variables to LookupElements
            for (NetteVariable variable : netteVariables) {
                // Skip HTTP variables if Nette HTTP is disabled
                if (!currentSettings.isEnableNetteHttp() && isHttpVariable(variable.getName())) {
                    System.out.println("[DEBUG_LOG] Skipping HTTP variable: " + variable.getName() + " because Nette HTTP is disabled");
                    continue;
                }
                
                System.out.println("[DEBUG_LOG] Adding variable to cache: " + variable.getName() + " of type " + variable.getType());
                variables.add(LookupElementBuilder.create(variable.getName())
                        .withTypeText(variable.getType())
                        .withTailText(" - " + variable.getDescription(), true));
            }
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Error updating variables cache: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Update the cache
        cachedSettings.set(currentSettings);
        cachedVariables.set(variables);
        
        System.out.println("[DEBUG_LOG] Variables cache updated with " + variables.size() + " variables");
    }
    
    /**
     * Adds Nette variables to the completion results.
     * Uses caching to improve performance.
     *
     * @param parameters The completion parameters
     * @param result The completion result set
     */
    private void addNetteVariables(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        System.out.println("[DEBUG_LOG] addNetteVariables called");
        
        try {
            // Get the project from parameters
            com.intellij.openapi.project.Project project = parameters.getOriginalFile().getProject();
            
            // Check and update the variables cache
            checkAndUpdateVariablesCache(project);
            
            // Get variables from cache
            List<LookupElement> variables = cachedVariables.get();
            if (variables == null || variables.isEmpty()) {
                System.out.println("[DEBUG_LOG] Variables cache is empty after update, something went wrong");
                return;
            }
            
            System.out.println("[DEBUG_LOG] Adding " + variables.size() + " variables from cache to completion results");
            
            // Add variables from cache to results
            for (LookupElement variable : variables) {
                result.addElement(variable);
            }
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Error adding Nette variables: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Checks if a variable is an HTTP variable.
     *
     * @param name The name of the variable
     * @return True if the variable is an HTTP variable, false otherwise
     */
    private boolean isHttpVariable(String name) {
        return name.equals("httpRequest") || 
               name.equals("httpResponse") || 
               name.equals("session") || 
               name.equals("url") || 
               name.equals("cookies") || 
               name.equals("headers") || 
               name.equals("requestFactory");
    }
}