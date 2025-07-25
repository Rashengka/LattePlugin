package cz.hqm.latte.plugin.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilderFactory;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.TreeUtil;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import cz.hqm.latte.plugin.lang.LatteLanguage;
import cz.hqm.latte.plugin.lexer.LatteLexer;
import cz.hqm.latte.plugin.lexer.LatteTokenTypes;
import cz.hqm.latte.plugin.version.LatteVersionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for incremental parsing of Latte templates.
 * This service provides methods for parsing only the changed parts of a template,
 * which improves performance for large templates.
 */
@Service(Service.Level.PROJECT)
public final class LatteIncrementalParser {

    // The project this parser is associated with
    private final Project project;
    
    // Map of file paths to their last known content
    private final Map<String, String> lastKnownContent;
    
    /**
     * Constructor that initializes the parser.
     *
     * @param project The project this parser is associated with
     */
    public LatteIncrementalParser(Project project) {
        this.project = project;
        this.lastKnownContent = new HashMap<>();
    }
    
    /**
     * Gets the instance of this service for the given project.
     *
     * @param project The project to get the service for
     * @return The service instance
     */
    public static LatteIncrementalParser getInstance(@NotNull Project project) {
        return project.getService(LatteIncrementalParser.class);
    }
    
    /**
     * Parses the changed parts of a template.
     *
     * @param file The file to parse
     * @param content The current content of the file
     * @return A list of text ranges that were reparsed
     */
    @NotNull
    public List<TextRange> parseChangedParts(@NotNull VirtualFile file, @NotNull String content) {
        String filePath = file.getPath();
        String oldContent = lastKnownContent.get(filePath);
        
        List<TextRange> changedRanges = new ArrayList<>();
        
        if (oldContent == null) {
            // First time seeing this file, parse the whole thing
            changedRanges.add(new TextRange(0, content.length()));
            lastKnownContent.put(filePath, content);
            return changedRanges;
        }
        
        // Find changed regions
        List<TextRange> changes = findChangedRegions(oldContent, content);
        
        // Expand changes to include complete Latte constructs
        List<TextRange> expandedChanges = expandChangesToCompleteLatteConstructs(content, changes);
        
        // Update last known content
        lastKnownContent.put(filePath, content);
        
        return expandedChanges;
    }
    
    /**
     * Clears the last known content for the given file.
     *
     * @param file The file to clear the content for
     */
    public void clearLastKnownContent(@NotNull VirtualFile file) {
        lastKnownContent.remove(file.getPath());
    }
    
    /**
     * Clears all last known content.
     */
    public void clearAllLastKnownContent() {
        lastKnownContent.clear();
    }
    
    /**
     * Finds the regions that have changed between two strings.
     * This is a simple implementation that could be improved with a more sophisticated diff algorithm.
     *
     * @param oldContent The old content
     * @param newContent The new content
     * @return A list of text ranges representing the changed regions
     */
    @NotNull
    private List<TextRange> findChangedRegions(@NotNull String oldContent, @NotNull String newContent) {
        List<TextRange> changes = new ArrayList<>();
        
        // Simple implementation: if the content has changed, consider the whole file changed
        // A more sophisticated implementation would use a diff algorithm to find specific changes
        if (!oldContent.equals(newContent)) {
            changes.add(new TextRange(0, newContent.length()));
        }
        
        return changes;
    }
    
    /**
     * Expands the changed regions to include complete Latte constructs.
     * This ensures that we don't parse partial Latte constructs, which could lead to errors.
     *
     * @param content The content of the file
     * @param changes The changed regions
     * @return A list of text ranges representing the expanded changed regions
     */
    @NotNull
    private List<TextRange> expandChangesToCompleteLatteConstructs(@NotNull String content, @NotNull List<TextRange> changes) {
        List<TextRange> expandedChanges = new ArrayList<>();
        
        for (TextRange change : changes) {
            int start = findStartOfLatteMacro(content, change.getStartOffset());
            int end = findEndOfLatteMacro(content, change.getEndOffset());
            
            expandedChanges.add(new TextRange(start, end));
        }
        
        // Merge overlapping ranges
        return mergeOverlappingRanges(expandedChanges);
    }
    
    /**
     * Finds the start of the Latte macro that contains the given offset.
     * Handles both single-brace macros {macro} and double-brace macros {{macro}}
     * for compatibility with different syntax modes.
     * 
     * This method is designed to be robust and handle all possible syntax modes correctly,
     * without relying on the current syntax mode. It identifies both single-brace and
     * double-brace macros as potential macro starts, and also handles special cases like
     * {syntax} tags and JavaScript code with braces.
     *
     * @param content The content of the file
     * @param offset The offset to start searching from
     * @return The offset of the start of the Latte macro
     */
    private int findStartOfLatteMacro(@NotNull String content, int offset) {
        // Search backward for the start of a Latte macro
        boolean inString = false;
        char stringDelimiter = 0;
        boolean inComment = false;
        boolean inJavaScript = false;
        
        // Check if we're inside a JavaScript block
        for (int i = 0; i < offset; i++) {
            if (i + 8 < content.length() && content.substring(i, i + 9).equals("<script>")) {
                inJavaScript = true;
            } else if (i + 9 < content.length() && content.substring(i, i + 10).equals("</script>")) {
                inJavaScript = false;
            }
        }
        
        for (int i = offset; i >= 0; i--) {
            // Handle strings and comments
            if (i > 0) {
                char prevChar = content.charAt(i - 1);
                char currentChar = content.charAt(i);
                
                // Check for end of string
                if (inString && currentChar == stringDelimiter && prevChar != '\\') {
                    inString = false;
                    continue;
                }
                
                // Check for start of string
                if (!inString && !inComment && (currentChar == '"' || currentChar == '\'')) {
                    inString = true;
                    stringDelimiter = currentChar;
                    continue;
                }
                
                // Check for end of line comment
                if (inComment && currentChar == '\n') {
                    inComment = false;
                    continue;
                }
                
                // Check for start of line comment
                if (!inString && !inComment && i > 0 && prevChar == '/' && currentChar == '/') {
                    inComment = true;
                    continue;
                }
            }
            
            // Skip if we're in a string or comment
            if (inString || inComment) {
                continue;
            }
            
            // Special handling for JavaScript code
            if (inJavaScript) {
                // In JavaScript, only look for {syntax} and {/syntax} tags
                if (i + 8 < content.length() && content.substring(i, i + 9).equals("{/syntax}")) {
                    return i;
                }
                if (i + 12 < content.length() && content.substring(i, i + 13).equals("{syntax off}")) {
                    return i;
                }
                if (i + 14 < content.length() && content.substring(i, i + 15).equals("{syntax double}")) {
                    return i;
                }
                continue;
            }
            
            // Check for {syntax} tags first, as they have highest priority
            if (i + 8 < content.length() && content.substring(i, i + 9).equals("{/syntax}")) {
                return i;
            }
            if (i + 12 < content.length() && content.substring(i, i + 13).equals("{syntax off}")) {
                return i;
            }
            if (i + 14 < content.length() && content.substring(i, i + 15).equals("{syntax double}")) {
                return i;
            }
            
            // Check for double-brace macro {{macro}} (for {syntax double} mode)
            // This has higher priority than single-brace macros to avoid misidentifying {{macro}} as two separate macros
            if (i + 1 < content.length() && i + 2 < content.length() && 
                content.charAt(i) == '{' && content.charAt(i + 1) == '{') {
                // Found potential start of a double-brace macro
                return i;
            }
            
            // Check for single-brace macro {macro}
            if (i + 1 < content.length() && content.charAt(i) == '{' && content.charAt(i + 1) != '{') {
                // Found potential start of a single-brace macro
                return i;
            }
        }
        
        // If no start found, return the beginning of the file
        return 0;
    }
    
    /**
     * Finds the end of the Latte macro that contains the given offset.
     * This enhanced version tracks opening and closing macros to detect proper nesting,
     * unclosed macros, and crossing macros.
     * It handles both single-brace macros {macro} and double-brace macros {{macro}}
     * for compatibility with different syntax modes.
     *
     * @param content The content of the file
     * @param offset The offset to start searching from
     * @return The offset of the end of the Latte macro
     */
    private int findEndOfLatteMacro(@NotNull String content, int offset) {
        // Patterns for matching macro names in single-brace syntax
        Pattern openMacroPattern = Pattern.compile("\\{([a-zA-Z_][a-zA-Z0-9_]*)(?:\\s+|\\}|$)");
        Pattern closeMacroPattern = Pattern.compile("\\{/([a-zA-Z_][a-zA-Z0-9_]*)\\}");
        
        // Patterns for matching macro names in double-brace syntax
        Pattern openDoubleMacroPattern = Pattern.compile("\\{\\{([a-zA-Z_][a-zA-Z0-9_]*)(?:\\s+|\\}\\}|$)");
        Pattern closeDoubleMacroPattern = Pattern.compile("\\{\\{/([a-zA-Z_][a-zA-Z0-9_]*)\\}\\}");
        
        // Set of macros that don't require closing tags
        Set<String> selfClosingMacros = new HashSet<>(Arrays.asList(
            "var", "default", "dump", "debugbreak", "l", "r", "syntax", "use", "_", "=", 
            "contentType", "status", "php", "do", "varType", "templateType", "parameters",
            "include", "extends", "layout", "typeCheck", "strictTypes", "asyncInclude", "await", "inject"
        ));
        
        // Set of block macros that might be allowed to remain unclosed in some versions
        Set<String> blockMacros = new HashSet<>(Arrays.asList("block", "define", "snippet", "snippetArea", "capture"));
        
        // Stack to track opening macros
        Stack<MacroInfo> macroStack = new Stack<>();
        
        // Start from the given offset
        int i = offset;
        
        while (i < content.length()) {
            boolean isDoubleBrace = false;
            
            // Check for double-brace macro {{macro}} (for {syntax double} mode)
            if (i < content.length() - 2 && content.charAt(i) == '{' && content.charAt(i + 1) == '{') {
                isDoubleBrace = true;
            }
            
            // Check for opening macro (either single-brace or double-brace)
            if ((isDoubleBrace && i < content.length() - 2) || 
                (!isDoubleBrace && i < content.length() - 1 && content.charAt(i) == '{')) {
                
                // Find the end of this macro tag
                int macroEnd = findMacroTagEnd(content, i);
                if (macroEnd == -1) {
                    // Unclosed macro tag, return end of file
                    return content.length();
                }
                
                String macroTag = content.substring(i, macroEnd);
                
                // Check if it's a closing macro
                Matcher closeMatcher;
                if (isDoubleBrace) {
                    closeMatcher = closeDoubleMacroPattern.matcher(macroTag);
                } else {
                    closeMatcher = closeMacroPattern.matcher(macroTag);
                }
                
                if (closeMatcher.find()) {
                    String closingMacroName = closeMatcher.group(1);
                    
                    // Check if we have a matching opening macro
                    if (!macroStack.isEmpty()) {
                        MacroInfo lastMacro = macroStack.peek();
                        
                        if (lastMacro.name.equals(closingMacroName)) {
                            // Matching closing tag found
                            macroStack.pop();
                            
                            // If the stack is empty, we've found the end of the original macro
                            if (macroStack.isEmpty()) {
                                return macroEnd;
                            }
                        } else {
                            // Crossing macro detected - mismatched closing tag
                            // For this implementation, we'll continue parsing to find the proper end
                            // In a real error reporting system, this would be flagged as an error
                            
                            // Check if this closing tag matches any macro in the stack
                            boolean foundMatch = false;
                            for (MacroInfo macro : macroStack) {
                                if (macro.name.equals(closingMacroName)) {
                                    foundMatch = true;
                                    break;
                                }
                            }
                            
                            if (!foundMatch) {
                                // This is an unexpected closing tag, ignore it
                                i = macroEnd;
                                continue;
                            }
                            
                            // This is a crossing macro, but we'll continue parsing
                            // to find the proper end of the original macro
                        }
                    } else {
                        // Unexpected closing tag, ignore it
                        i = macroEnd;
                        continue;
                    }
                } else {
                    // Check if it's an opening macro
                    Matcher openMatcher;
                    if (isDoubleBrace) {
                        openMatcher = openDoubleMacroPattern.matcher(macroTag);
                    } else {
                        openMatcher = openMacroPattern.matcher(macroTag);
                    }
                    
                    if (openMatcher.find()) {
                        String openingMacroName = openMatcher.group(1);
                        
                        // If this is the first macro and we're at the original offset,
                        // add it to the stack
                        if (i == offset) {
                            macroStack.push(new MacroInfo(openingMacroName, i));
                        } else if (!selfClosingMacros.contains(openingMacroName)) {
                            // This is a nested macro that requires a closing tag
                            macroStack.push(new MacroInfo(openingMacroName, i));
                        }
                        // Self-closing macros don't need to be added to the stack
                    }
                }
                
                i = macroEnd;
            } else {
                i++;
            }
        }
        
        // If we've reached the end of the file and the stack is not empty,
        // we have unclosed macros
        if (!macroStack.isEmpty()) {
            // Check each macro in the stack to see if it's allowed to remain unclosed
            boolean allMacrosAllowedUnclosed = true;
            
            for (MacroInfo macro : macroStack) {
                if (!isBlockMacroAllowedUnclosed(macro.name)) {
                    allMacrosAllowedUnclosed = false;
                    break;
                }
            }
            
            if (allMacrosAllowedUnclosed) {
                // All unclosed macros are block directives that are automatically closed at EOF
                // This is standard behavior in Latte, so we don't flag it as an error
                return content.length();
            } else {
                // Some unclosed macros are not block directives that can be automatically closed
                // This is still an error, but we'll return the end of the file
                return content.length();
            }
        }
        
        // If we've reached here, we didn't find the end of the macro
        return content.length();
    }
    
    /**
     * Finds the end of a macro tag (the closing brace).
     * Handles both single and double braces for compatibility with different syntax modes.
     *
     * @param content The content of the file
     * @param start The start offset of the macro tag
     * @return The offset of the end of the macro tag, or -1 if not found
     */
    private int findMacroTagEnd(@NotNull String content, int start) {
        boolean inString = false;
        char stringDelimiter = 0;
        boolean isDoubleBrace = false;
        
        // Check if this is a double-brace macro (for {syntax double} mode)
        if (start + 1 < content.length() && content.charAt(start) == '{' && content.charAt(start + 1) == '{') {
            isDoubleBrace = true;
        }
        
        for (int i = start + 1; i < content.length(); i++) {
            char c = content.charAt(i);
            
            if (inString) {
                if (c == stringDelimiter && (i == 0 || content.charAt(i - 1) != '\\')) {
                    inString = false;
                }
            } else {
                if (c == '"' || c == '\'') {
                    inString = true;
                    stringDelimiter = c;
                } else if (c == '}') {
                    if (isDoubleBrace) {
                        // For double-brace mode, we need to find two closing braces
                        if (i + 1 < content.length() && content.charAt(i + 1) == '}') {
                            return i + 2;
                        }
                    } else {
                        // For single-brace mode, one closing brace is enough
                        return i + 1;
                    }
                }
            }
        }
        
        return -1;
    }
    
    /**
     * Checks if a block macro is allowed to remain unclosed in the current Latte version.
     *
     * @param macroName The name of the block macro
     * @return True if the block macro is allowed to remain unclosed, false otherwise
     */
    private boolean isBlockMacroAllowedUnclosed(String macroName) {
        // According to Latte's standard behavior, all block directives are automatically closed
        // at the end of the file if they are not explicitly closed.
        
        // Set of block macros that are automatically closed at EOF
        Set<String> autoClosedMacros = new HashSet<>(Arrays.asList(
            "block", "define", "snippet", "snippetArea", "capture",
            "if", "elseif", "else",
            "foreach", "for", "while",
            "try", "catch",
            "switch", "case", "default"
        ));
        
        return autoClosedMacros.contains(macroName);
    }
    
    /**
     * Helper class to store information about a macro.
     */
    private static class MacroInfo {
        final String name;
        final int offset;
        
        MacroInfo(String name, int offset) {
            this.name = name;
            this.offset = offset;
        }
    }
    
    /**
     * Merges overlapping text ranges.
     *
     * @param ranges The ranges to merge
     * @return A list of merged ranges
     */
    @NotNull
    private List<TextRange> mergeOverlappingRanges(@NotNull List<TextRange> ranges) {
        if (ranges.isEmpty()) {
            return ranges;
        }
        
        // Sort ranges by start offset
        ranges.sort((r1, r2) -> r1.getStartOffset() - r2.getStartOffset());
        
        List<TextRange> mergedRanges = new ArrayList<>();
        TextRange current = ranges.get(0);
        
        for (int i = 1; i < ranges.size(); i++) {
            TextRange next = ranges.get(i);
            
            if (current.getEndOffset() >= next.getStartOffset()) {
                // Ranges overlap, merge them
                current = new TextRange(current.getStartOffset(), Math.max(current.getEndOffset(), next.getEndOffset()));
            } else {
                // Ranges don't overlap, add current to result and move to next
                mergedRanges.add(current);
                current = next;
            }
        }
        
        // Add the last range
        mergedRanges.add(current);
        
        return mergedRanges;
    }
}