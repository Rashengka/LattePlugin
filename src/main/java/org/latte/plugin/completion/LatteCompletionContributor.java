package org.latte.plugin.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.latte.plugin.completion.NetteDefaultVariablesProvider.NetteVariable;
import org.latte.plugin.custom.*;
import org.latte.plugin.filters.NetteFilter;
import org.latte.plugin.filters.NetteFilterProvider;
import org.latte.plugin.lang.LatteLanguage;
import org.latte.plugin.lexer.LatteTokenTypes;
import org.latte.plugin.macros.NetteMacro;
import org.latte.plugin.macros.NetteMacroProvider;
import org.latte.plugin.version.LatteVersionManager;

/**
 * Provides code completion for Latte tags and attributes.
 * Supports Latte 2.x, 3.0+, and 4.0+ versions.
 */
public class LatteCompletionContributor extends CompletionContributor {

    public LatteCompletionContributor() {
        // Add completion for Latte macros
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(LatteLanguage.INSTANCE)
                        .afterLeaf("{"),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                 @NotNull ProcessingContext context,
                                                 @NotNull CompletionResultSet result) {
                        // Add version-specific macros
                        if (LatteVersionManager.isVersion4x()) {
                            // Latte 4.0+ specific macros
                            result.addElement(LookupElementBuilder.create("typeCheck").bold().withTypeText("Latte 4.0+ macro"));
                            result.addElement(LookupElementBuilder.create("strictTypes").bold().withTypeText("Latte 4.0+ macro"));
                            result.addElement(LookupElementBuilder.create("asyncInclude").bold().withTypeText("Latte 4.0+ macro"));
                            result.addElement(LookupElementBuilder.create("await").bold().withTypeText("Latte 4.0+ macro"));
                            result.addElement(LookupElementBuilder.create("inject").bold().withTypeText("Latte 4.0+ macro"));
                            result.addElement(LookupElementBuilder.create("_").bold().withTypeText("Latte 4.0+ macro"));
                            result.addElement(LookupElementBuilder.create("=").bold().withTypeText("Latte 4.0+ macro"));
                            
                            // Also include 3.0+ macros as they are likely still supported in 4.0+
                            result.addElement(LookupElementBuilder.create("varType").bold().withTypeText("Latte 3.0+ macro"));
                            result.addElement(LookupElementBuilder.create("templateType").bold().withTypeText("Latte 3.0+ macro"));
                            result.addElement(LookupElementBuilder.create("php").bold().withTypeText("Latte 3.0+ macro"));
                            result.addElement(LookupElementBuilder.create("do").bold().withTypeText("Latte 3.0+ macro"));
                            result.addElement(LookupElementBuilder.create("parameters").bold().withTypeText("Latte 3.0+ macro"));
                        } else if (LatteVersionManager.isVersion3x()) {
                            // Latte 3.0+ specific macros
                            result.addElement(LookupElementBuilder.create("varType").bold().withTypeText("Latte 3.0+ macro"));
                            result.addElement(LookupElementBuilder.create("templateType").bold().withTypeText("Latte 3.0+ macro"));
                            result.addElement(LookupElementBuilder.create("php").bold().withTypeText("Latte 3.0+ macro"));
                            result.addElement(LookupElementBuilder.create("do").bold().withTypeText("Latte 3.0+ macro"));
                            result.addElement(LookupElementBuilder.create("parameters").bold().withTypeText("Latte 3.0+ macro"));
                            result.addElement(LookupElementBuilder.create("_").bold().withTypeText("Latte 3.0+ macro"));
                            result.addElement(LookupElementBuilder.create("=").bold().withTypeText("Latte 3.0+ macro"));
                        } else {
                            // Latte 2.x specific macros
                            result.addElement(LookupElementBuilder.create("syntax").bold().withTypeText("Latte 2.x macro"));
                            result.addElement(LookupElementBuilder.create("use").bold().withTypeText("Latte 2.x macro"));
                            result.addElement(LookupElementBuilder.create("l").bold().withTypeText("Latte 2.x macro"));
                            result.addElement(LookupElementBuilder.create("r").bold().withTypeText("Latte 2.x macro"));
                            result.addElement(LookupElementBuilder.create("_").bold().withTypeText("Latte 2.x macro"));
                            result.addElement(LookupElementBuilder.create("=").bold().withTypeText("Latte 2.x macro"));
                        }
                        
                        // Add Nette package macros
                        for (NetteMacro macro : NetteMacroProvider.getAllMacros()) {
                            result.addElement(LookupElementBuilder.create(macro.getName())
                                    .bold()
                                    .withTypeText(macro.getTypeText())
                                    .withTailText(" - " + macro.getDescription(), true));
                        }
                        
                        // Add custom tags
                        for (CustomTag tag : CustomTagsProvider.getAllTags(parameters.getOriginalFile().getProject())) {
                            result.addElement(LookupElementBuilder.create(tag.getName())
                                    .bold()
                                    .withTypeText(tag.getTypeText())
                                    .withTailText(tag.getDescription() != null ? " - " + tag.getDescription() : "", true));
                        }
                    }
                });
                
        // Add completion for Nette default variables
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(LatteLanguage.INSTANCE)
                        .afterLeaf("$"),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                 @NotNull ProcessingContext context,
                                                 @NotNull CompletionResultSet result) {
                        // Get all default variables for the current project
                        for (NetteVariable variable : NetteDefaultVariablesProvider.getAllVariables(parameters.getOriginalFile().getProject())) {
                            result.addElement(LookupElementBuilder.create(variable.getName())
                                    .withTypeText(variable.getType())
                                    .withTailText(" - " + variable.getDescription(), true));
                        }
                        
                        // Add custom variables
                        for (CustomVariable variable : CustomVariablesProvider.getAllVariables(parameters.getOriginalFile().getProject())) {
                            result.addElement(LookupElementBuilder.create(variable.getName())
                                    .withTypeText(variable.getTypeText())
                                    .withTailText(variable.getDescription() != null ? " - " + variable.getDescription() : "", true));
                        }
                    }
                });

        // Add completion for Latte n:attributes
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(LatteLanguage.INSTANCE)
                        .inside(PlatformPatterns.psiElement().withLanguage(LatteLanguage.INSTANCE)),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                 @NotNull ProcessingContext context,
                                                 @NotNull CompletionResultSet result) {
                        // Add Nette package attributes
                        for (NetteMacro attribute : NetteMacroProvider.getAllAttributes()) {
                            result.addElement(LookupElementBuilder.create(attribute.getName())
                                    .bold()
                                    .withTypeText(attribute.getTypeText())
                                    .withTailText(" - " + attribute.getDescription(), true));
                        }
                    }
                });

        // Add completion for Latte filters
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(LatteLanguage.INSTANCE)
                        .afterLeaf("|"),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                 @NotNull ProcessingContext context,
                                                 @NotNull CompletionResultSet result) {
                        // Add Nette package filters
                        for (NetteFilter filter : NetteFilterProvider.getAllFilters()) {
                            result.addElement(LookupElementBuilder.create(filter.getName())
                                    .withTypeText(filter.getTypeText())
                                    .withTailText(" - " + filter.getDescription(), true));
                        }
                        
                        // Add custom filters
                        for (CustomFilter filter : CustomFiltersProvider.getAllFilters(parameters.getOriginalFile().getProject())) {
                            result.addElement(LookupElementBuilder.create(filter.getName())
                                    .withTypeText(filter.getTypeText())
                                    .withTailText(filter.getDescription() != null ? " - " + filter.getDescription() : "", true));
                        }
                    }
                });
                
        // Add completion for custom functions
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(LatteLanguage.INSTANCE),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                 @NotNull ProcessingContext context,
                                                 @NotNull CompletionResultSet result) {
                        // Get the text before the caret
                        String text = parameters.getPosition().getText();
                        int offset = parameters.getOffset() - parameters.getPosition().getTextRange().getStartOffset();
                        
                        // Only provide completions in appropriate contexts for functions
                        if (offset > 0 && !text.substring(0, offset).matches(".*[\\$\\|\\{\\}\\(\\)\\[\\]\\s]$")) {
                            // Add custom functions
                            for (CustomFunction function : CustomFunctionsProvider.getAllFunctions(parameters.getOriginalFile().getProject())) {
                                result.addElement(LookupElementBuilder.create(function.getName())
                                        .withTypeText(function.getTypeText())
                                        .withTailText(function.getDescription() != null ? " - " + function.getDescription() : "", true));
                            }
                        }
                    }
                });
    }
}