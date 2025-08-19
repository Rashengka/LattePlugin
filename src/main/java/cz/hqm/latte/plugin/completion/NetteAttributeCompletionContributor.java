package cz.hqm.latte.plugin.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import cz.hqm.latte.plugin.lang.LatteLanguage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides context-aware completion for Nette n: attributes in HTML/XML tags.
 * Only suggests n: attributes when inside an HTML/XML tag.
 */
public class NetteAttributeCompletionContributor extends CompletionContributor {

    // Pattern to detect if we're inside an HTML/XML tag (not after a closed tag)
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<([a-zA-Z][a-zA-Z0-9]*)[^>]*$");
    
    // Pattern to detect if we're after a closed HTML/XML tag
    private static final Pattern AFTER_CLOSED_TAG_PATTERN = Pattern.compile("<[^>]+>[^<]*$");
    
    // Pattern to detect if we're typing an n: attribute
    private static final Pattern N_ATTRIBUTE_PATTERN = Pattern.compile("n:[a-zA-Z0-9_:.\\-]*$");
    
    // Valid attribute prefixes (copied from LatteAttributeLexer)
    private static final Set<String> VALID_ATTRIBUTE_PREFIXES = new HashSet<>(Arrays.asList(
            "n:", "n:inner-", "n:tag-", "n:class-", "n:attr-", 
            // Support for prefixed n:attributes (e.g., n:class:hover)
            "n:class:", "n:attr:", "n:tag:", "n:data-"
    ));
    
    // Valid attribute names (copied from LatteAttributeLexer)
    private static final Set<String> VALID_ATTRIBUTE_NAMES = new HashSet<>(Arrays.asList(
            "n:if", "n:ifset", "n:foreach", "n:inner-foreach", "n:class", "n:attr", "n:tag",
            "n:snippet", "n:block", "n:include", "n:inner-if", "n:inner-ifset", "n:ifcontent",
            "n:href", "n:name", "n:nonce", "n:syntax"
    ));

    public NetteAttributeCompletionContributor() {
        // Register completion provider for n: attributes in HTML/XML tags
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(LatteLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                 @NotNull ProcessingContext context,
                                                 @NotNull CompletionResultSet result) {
                        PsiElement position = parameters.getPosition();
                        String posText = position.getText();
                        System.out.println("[DEBUG_LOG] NetteAttributeCompletionContributor: position text= " + posText);

                        int offset = parameters.getOffset();
                        String fileText = parameters.getOriginalFile().getText();

                        // Use a bounded window of text before the caret to avoid heavy regex over entire file
                        int window = Math.max(0, offset - 512);
                        String textBeforeCursor = fileText.substring(window, offset);

                        System.out.println("[DEBUG_LOG] Text window before cursor: '" + textBeforeCursor + "'");

                        // Fast reject: if after a closed tag, don't suggest
                        boolean isAfterClosedTag = AFTER_CLOSED_TAG_PATTERN.matcher(textBeforeCursor).find();
                        System.out.println("[DEBUG_LOG] AFTER_CLOSED_TAG_PATTERN matches: " + isAfterClosedTag);
                        if (isAfterClosedTag) {
                            return;
                        }

                        // Check if we're inside an HTML/XML tag
                        Matcher tagMatcher = HTML_TAG_PATTERN.matcher(textBeforeCursor);
                        boolean isInsideTag = tagMatcher.find();
                        System.out.println("[DEBUG_LOG] HTML_TAG_PATTERN matches: " + isInsideTag);

                        if (!isInsideTag) return;

                        // Always offer base n: attributes when inside a tag (even if user hasn't typed n: yet)
                        addBaseNAttributes(result);

                        // If the user is currently typing an n: attribute (e.g., "n:" or "n:cl"), also offer prefixes
                        boolean endsWithN = textBeforeCursor.endsWith("n:");
                        boolean matchesNPattern = N_ATTRIBUTE_PATTERN.matcher(textBeforeCursor).find();
                        System.out.println("[DEBUG_LOG] Text ends with 'n:': " + endsWithN);
                        System.out.println("[DEBUG_LOG] N_ATTRIBUTE_PATTERN matches: " + matchesNPattern);
                        if (endsWithN || matchesNPattern) {
                            // Add a special marker completion item for testing/debugging when prefix typed
                            result.addElement(LookupElementBuilder.create("__N_PREFIX_MARKER__")
                                    .withPresentableText("__N_PREFIX_MARKER__")
                                    .withTypeText("Test marker"));
                            addNAttributePrefixes(result);
                        }
                    }
                });
    }

    /**
     * Lightweight helper for tests: computes suggested n: attribute names from raw text and caret offset.
     */
    public static @NotNull Set<String> computeNAttributeSuggestionsFromText(@NotNull String fullText, int offset) {
        if (offset < 0) offset = 0;
        if (offset > fullText.length()) offset = fullText.length();
        int window = Math.max(0, offset - 512);
        String textBeforeCursor = fullText.substring(window, offset);

        // After closed tag? no suggestions
        if (AFTER_CLOSED_TAG_PATTERN.matcher(textBeforeCursor).find()) {
            return java.util.Collections.emptySet();
        }
        // Inside tag?
        Matcher tagMatcher = HTML_TAG_PATTERN.matcher(textBeforeCursor);
        boolean isInsideTag = tagMatcher.find();
        if (!isInsideTag) return java.util.Collections.emptySet();

        // Base suggestions always available inside tag
        Set<String> result = new java.util.HashSet<>(VALID_ATTRIBUTE_NAMES);

        // If typing n:*, add prefixes too
        boolean endsWithN = textBeforeCursor.endsWith("n:");
        boolean matchesNPattern = N_ATTRIBUTE_PATTERN.matcher(textBeforeCursor).find();
        if (endsWithN || matchesNPattern) {
            for (String prefix : VALID_ATTRIBUTE_PREFIXES) {
                if (!"n:".equals(prefix)) {
                    result.add(prefix);
                }
            }
        }
        return result;
    }

    /**
     * Adds completions for base Nette n: attribute names.
     */
    private void addBaseNAttributes(CompletionResultSet result) {
        System.out.println("[DEBUG_LOG] Adding base Nette n: attribute completions");
        for (String attrName : VALID_ATTRIBUTE_NAMES) {
            result.addElement(LookupElementBuilder.create(attrName)
                    .withPresentableText(attrName)
                    .withLookupString(attrName)
                    .withLookupString(attrName.substring(2))
                    .bold()
                    .withTypeText("Nette attribute"));
        }
    }

    /**
     * Adds completions for Nette n: attribute prefixes (n:inner- etc.).
     */
    private void addNAttributePrefixes(CompletionResultSet result) {
        for (String prefix : VALID_ATTRIBUTE_PREFIXES) {
            if (!prefix.equals("n:")) {
                result.addElement(LookupElementBuilder.create(prefix)
                        .withPresentableText(prefix)
                        .withLookupString(prefix)
                        .withLookupString(prefix.substring(2))
                        .withTypeText("Nette attribute prefix"));
            }
        }
    }
}