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
import org.latte.plugin.lang.LatteLanguage;
import org.latte.plugin.lexer.LatteTokenTypes;
import org.latte.plugin.macros.NetteMacro;
import org.latte.plugin.macros.NetteMacroProvider;
import org.latte.plugin.version.LatteVersionManager;

/**
 * Provides code completion for Latte tags and attributes.
 * Supports both Latte 2.x and 3.0+ versions.
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
                        // Add common Latte macros (available in both 2.x and 3.0+)
                        result.addElement(LookupElementBuilder.create("if").bold().withTypeText("Latte macro"));
                        result.addElement(LookupElementBuilder.create("else").bold().withTypeText("Latte macro"));
                        result.addElement(LookupElementBuilder.create("elseif").bold().withTypeText("Latte macro"));
                        result.addElement(LookupElementBuilder.create("foreach").bold().withTypeText("Latte macro"));
                        result.addElement(LookupElementBuilder.create("include").bold().withTypeText("Latte macro"));
                        result.addElement(LookupElementBuilder.create("block").bold().withTypeText("Latte macro"));
                        result.addElement(LookupElementBuilder.create("define").bold().withTypeText("Latte macro"));
                        result.addElement(LookupElementBuilder.create("var").bold().withTypeText("Latte macro"));
                        result.addElement(LookupElementBuilder.create("capture").bold().withTypeText("Latte macro"));
                        result.addElement(LookupElementBuilder.create("snippet").bold().withTypeText("Latte macro"));
                        result.addElement(LookupElementBuilder.create("_").bold().withTypeText("Latte macro"));
                        result.addElement(LookupElementBuilder.create("=").bold().withTypeText("Latte macro"));
                        
                        // Add version-specific macros
                        if (LatteVersionManager.isVersion3x()) {
                            // Latte 3.0+ specific macros
                            result.addElement(LookupElementBuilder.create("varType").bold().withTypeText("Latte 3.0+ macro"));
                            result.addElement(LookupElementBuilder.create("templateType").bold().withTypeText("Latte 3.0+ macro"));
                            result.addElement(LookupElementBuilder.create("php").bold().withTypeText("Latte 3.0+ macro"));
                            result.addElement(LookupElementBuilder.create("do").bold().withTypeText("Latte 3.0+ macro"));
                            result.addElement(LookupElementBuilder.create("parameters").bold().withTypeText("Latte 3.0+ macro"));
                        } else {
                            // Latte 2.x specific macros
                            result.addElement(LookupElementBuilder.create("syntax").bold().withTypeText("Latte 2.x macro"));
                            result.addElement(LookupElementBuilder.create("use").bold().withTypeText("Latte 2.x macro"));
                            result.addElement(LookupElementBuilder.create("l").bold().withTypeText("Latte 2.x macro"));
                            result.addElement(LookupElementBuilder.create("r").bold().withTypeText("Latte 2.x macro"));
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
                        // Add common Latte n:attributes
                        result.addElement(LookupElementBuilder.create("n:if").bold().withTypeText("Latte attribute"));
                        result.addElement(LookupElementBuilder.create("n:foreach").bold().withTypeText("Latte attribute"));
                        result.addElement(LookupElementBuilder.create("n:inner-foreach").bold().withTypeText("Latte attribute"));
                        result.addElement(LookupElementBuilder.create("n:class").bold().withTypeText("Latte attribute"));
                        result.addElement(LookupElementBuilder.create("n:attr").bold().withTypeText("Latte attribute"));
                        result.addElement(LookupElementBuilder.create("n:tag").bold().withTypeText("Latte attribute"));
                        
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
                        // Add common Latte filters
                        result.addElement(LookupElementBuilder.create("capitalize").withTypeText("Latte filter"));
                        result.addElement(LookupElementBuilder.create("upper").withTypeText("Latte filter"));
                        result.addElement(LookupElementBuilder.create("lower").withTypeText("Latte filter"));
                        result.addElement(LookupElementBuilder.create("firstUpper").withTypeText("Latte filter"));
                        result.addElement(LookupElementBuilder.create("escape").withTypeText("Latte filter"));
                        result.addElement(LookupElementBuilder.create("escapeUrl").withTypeText("Latte filter"));
                        result.addElement(LookupElementBuilder.create("noescape").withTypeText("Latte filter"));
                        result.addElement(LookupElementBuilder.create("date").withTypeText("Latte filter"));
                        result.addElement(LookupElementBuilder.create("number").withTypeText("Latte filter"));
                        result.addElement(LookupElementBuilder.create("bytes").withTypeText("Latte filter"));
                        result.addElement(LookupElementBuilder.create("dataStream").withTypeText("Latte filter"));
                        result.addElement(LookupElementBuilder.create("replace").withTypeText("Latte filter"));
                        result.addElement(LookupElementBuilder.create("trim").withTypeText("Latte filter"));
                        result.addElement(LookupElementBuilder.create("stripHtml").withTypeText("Latte filter"));
                        result.addElement(LookupElementBuilder.create("strip").withTypeText("Latte filter"));
                        result.addElement(LookupElementBuilder.create("indent").withTypeText("Latte filter"));
                        result.addElement(LookupElementBuilder.create("padLeft").withTypeText("Latte filter"));
                        result.addElement(LookupElementBuilder.create("padRight").withTypeText("Latte filter"));
                        
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