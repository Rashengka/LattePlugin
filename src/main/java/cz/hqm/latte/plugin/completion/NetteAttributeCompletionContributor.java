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
                        String text = position.getText();
                        
                        System.out.println("[DEBUG_LOG] NetteAttributeCompletionContributor checking text: " + text);
                        
                        // Get the text before the cursor to check context
                        int offset = parameters.getOffset();
                        String fileText = parameters.getOriginalFile().getText();
                        String textBeforeCursor = fileText.substring(0, offset);
                        
                        System.out.println("[DEBUG_LOG] Text before cursor: " + textBeforeCursor);
                        
                        System.out.println("[DEBUG_LOG] NetteAttributeCompletionContributor is being triggered");
                        System.out.println("[DEBUG_LOG] Text before cursor for pattern matching: '" + textBeforeCursor + "'");
                        
                        // Check if we're after a closed HTML/XML tag - if so, don't suggest n: attributes
                        Matcher afterTagMatcher = AFTER_CLOSED_TAG_PATTERN.matcher(textBeforeCursor);
                        boolean isAfterClosedTag = afterTagMatcher.find();
                        System.out.println("[DEBUG_LOG] AFTER_CLOSED_TAG_PATTERN matches: " + isAfterClosedTag);
                        System.out.println("[DEBUG_LOG] Text before cursor for AFTER_CLOSED_TAG_PATTERN: '" + textBeforeCursor + "'");
                        System.out.println("[DEBUG_LOG] AFTER_CLOSED_TAG_PATTERN: '" + AFTER_CLOSED_TAG_PATTERN.pattern() + "'");
                        
                        if (isAfterClosedTag) {
                            System.out.println("[DEBUG_LOG] After closed HTML/XML tag, not suggesting n: attributes");
                            // We don't suggest n: attributes after a closed tag
                            return;
                        }
                        
                        // Check if we're inside an HTML/XML tag
                        Matcher tagMatcher = HTML_TAG_PATTERN.matcher(textBeforeCursor);
                        boolean isInsideTag = tagMatcher.find();
                        System.out.println("[DEBUG_LOG] HTML_TAG_PATTERN matches: " + isInsideTag);
                        
                        if (isInsideTag) {
                            String tagName = tagMatcher.group(1);
                            System.out.println("[DEBUG_LOG] Inside HTML/XML tag: " + tagName);
                            
                            // Check if we're typing an n: attribute or just after "n:"
                            boolean endsWithN = textBeforeCursor.endsWith("n:");
                            boolean matchesNPattern = N_ATTRIBUTE_PATTERN.matcher(textBeforeCursor).find();
                            System.out.println("[DEBUG_LOG] Text ends with 'n:': " + endsWithN);
                            System.out.println("[DEBUG_LOG] N_ATTRIBUTE_PATTERN matches: " + matchesNPattern);
                            
                            if (endsWithN || matchesNPattern) {
                                System.out.println("[DEBUG_LOG] Typing n: attribute, adding completions");
                                // Add a special marker completion item for testing
                                result.addElement(LookupElementBuilder.create("__N_PREFIX_MARKER__")
                                        .withPresentableText("__N_PREFIX_MARKER__")
                                        .withTypeText("Test marker"));
                                addNetteAttributeCompletions(result);
                            }
                        }
                    }
                });
    }
    
    /**
     * Adds completions for Nette n: attributes.
     *
     * @param result The completion result set
     */
    private void addNetteAttributeCompletions(CompletionResultSet result) {
        System.out.println("[DEBUG_LOG] Adding Nette n: attribute completions");
        
        // Add completions for specific attribute names
        for (String attrName : VALID_ATTRIBUTE_NAMES) {
            // Use the full attribute name (including "n:") as the primary lookup and presentation
            result.addElement(LookupElementBuilder.create(attrName)
                    .withPresentableText(attrName)
                    .withLookupString(attrName)
                    .withLookupString(attrName.substring(2)) // Allow matching without the prefix as well
                    .bold()
                    .withTypeText("Nette attribute"));
        }
        
        // Add completions for attribute prefixes (except "n:" itself which is already typed)
        for (String prefix : VALID_ATTRIBUTE_PREFIXES) {
            if (!prefix.equals("n:")) {
                // Use the full prefix (including "n:") as the primary lookup and presentation
                result.addElement(LookupElementBuilder.create(prefix)
                        .withPresentableText(prefix)
                        .withLookupString(prefix)
                        .withLookupString(prefix.substring(2)) // Allow matching without the prefix as well
                        .withTypeText("Nette attribute prefix"));
            }
        }
    }
}