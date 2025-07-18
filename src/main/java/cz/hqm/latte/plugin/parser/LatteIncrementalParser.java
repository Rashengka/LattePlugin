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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     *
     * @param content The content of the file
     * @param offset The offset to start searching from
     * @return The offset of the start of the Latte macro
     */
    private int findStartOfLatteMacro(@NotNull String content, int offset) {
        // Search backward for the start of a Latte macro
        for (int i = offset; i >= 0; i--) {
            if (i + 1 < content.length() && content.charAt(i) == '{' && content.charAt(i + 1) != '{') {
                // Found potential start of a Latte macro
                return i;
            }
        }
        
        // If no start found, return the beginning of the file
        return 0;
    }
    
    /**
     * Finds the end of the Latte macro that contains the given offset.
     *
     * @param content The content of the file
     * @param offset The offset to start searching from
     * @return The offset of the end of the Latte macro
     */
    private int findEndOfLatteMacro(@NotNull String content, int offset) {
        // Search forward for the end of a Latte macro
        for (int i = offset; i < content.length(); i++) {
            if (i > 0 && content.charAt(i) == '}' && content.charAt(i - 1) != '}') {
                // Found potential end of a Latte macro
                return i + 1;
            }
        }
        
        // If no end found, return the end of the file
        return content.length();
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