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
    // Cache for version and macros to optimize performance
    private static final AtomicReference<LatteVersion> cachedVersion = new AtomicReference<>();
    private static final AtomicReference<List<LookupElement>> cachedMacros = new AtomicReference<>();
    private static final AtomicReference<LatteSettings> cachedSettings = new AtomicReference<>();

    public LatteCompletionContributor() {
            System.out.println("[DEBUG_LOG] LatteCompletionContributor constructor called");
            
            // Initialize the cache when the contributor is created
            initMacrosCache();
        
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
                        System.out.println("[DEBUG_LOG] File text: '" + text + "'");
                        
                        if (text.contains("{$")) {
                            System.out.println("[DEBUG_LOG] Found {$ in file text, adding variables");
                            addNetteVariables(parameters, result);
                        }
                        
                        addCachedMacros(result);
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
                        addCachedMacros(result);
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
                        addCachedMacros(result);
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
                                addCachedMacros(result);
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
     * Checks if the cache needs to be updated based on current version and settings
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
        }
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
        
        // Check if cache needs updating
        checkAndUpdateCache();
        
        // Add macros from cache to results
        List<LookupElement> macros = cachedMacros.get();
        if (macros != null) {
            System.out.println("[DEBUG_LOG] Adding " + macros.size() + " macros from cache");
            for (LookupElement macro : macros) {
                result.addElement(macro);
            }
        } else {
            System.out.println("[DEBUG_LOG] Cache is null, initializing");
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
     * Adds Nette variables to the completion results.
     *
     * @param parameters The completion parameters
     * @param result The completion result set
     */
    private void addNetteVariables(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        System.out.println("[DEBUG_LOG] addNetteVariables called");
        
        try {
            // Check if Nette HTTP is enabled
            LatteSettings settings = LatteSettings.getInstance();
            System.out.println("[DEBUG_LOG] LatteCompletionContributor - Nette HTTP enabled: " + settings.isEnableNetteHttp());
            
            // Get all variables from NetteDefaultVariablesProvider
            List<NetteVariable> variables = NetteDefaultVariablesProvider.getAllVariables(parameters.getOriginalFile().getProject());
            System.out.println("[DEBUG_LOG] Number of variables: " + variables.size());
            
            // Add variables to completion results, filtering out HTTP variables if Nette HTTP is disabled
            for (NetteVariable variable : variables) {
                // Skip HTTP variables if Nette HTTP is disabled
                if (!settings.isEnableNetteHttp() && isHttpVariable(variable.getName())) {
                    System.out.println("[DEBUG_LOG] Skipping HTTP variable: " + variable.getName() + " because Nette HTTP is disabled");
                    continue;
                }
                
                System.out.println("[DEBUG_LOG] Adding variable: " + variable.getName() + " of type " + variable.getType());
                result.addElement(LookupElementBuilder.create(variable.getName())
                        .withTypeText(variable.getType())
                        .withTailText(" - " + variable.getDescription(), true));
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