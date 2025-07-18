package cz.hqm.latte.plugin.memory;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import cz.hqm.latte.plugin.psi.LatteFile;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for optimizing memory usage when working with large Latte templates.
 * This service provides methods for segmenting large templates and using memory-efficient
 * data structures to reduce memory usage.
 */
@Service(Service.Level.PROJECT)
public final class LatteMemoryOptimizer {

    // Maximum size of a template segment in characters
    private static final int MAX_SEGMENT_SIZE = 10000;
    
    // The project this optimizer is associated with
    private final Project project;
    
    // Map of file paths to their segmented content
    private final Map<String, SoftReference<TemplateSegments>> segmentCache;
    
    /**
     * Constructor that initializes the optimizer.
     *
     * @param project The project this optimizer is associated with
     */
    public LatteMemoryOptimizer(Project project) {
        this.project = project;
        this.segmentCache = new ConcurrentHashMap<>();
    }
    
    /**
     * Gets the instance of this service for the given project.
     *
     * @param project The project to get the service for
     * @return The service instance
     */
    public static LatteMemoryOptimizer getInstance(@NotNull Project project) {
        return project.getService(LatteMemoryOptimizer.class);
    }
    
    /**
     * Gets the segmented content for the given file.
     * If the file is small, it will be treated as a single segment.
     * If the file is large, it will be split into multiple segments.
     *
     * @param file The file to get the segmented content for
     * @param content The content of the file
     * @return The segmented content
     */
    @NotNull
    public TemplateSegments getSegmentedContent(@NotNull VirtualFile file, @NotNull String content) {
        String filePath = file.getPath();
        
        // Check if we have a cached version
        SoftReference<TemplateSegments> segmentsRef = segmentCache.get(filePath);
        TemplateSegments segments = segmentsRef != null ? segmentsRef.get() : null;
        
        // If no cached version or content has changed, create a new one
        if (segments == null || !segments.isValidFor(content)) {
            segments = segmentContent(content);
            segmentCache.put(filePath, new SoftReference<>(segments));
        }
        
        return segments;
    }
    
    /**
     * Clears the segment cache for the given file.
     *
     * @param file The file to clear the cache for
     */
    public void clearSegmentCache(@NotNull VirtualFile file) {
        segmentCache.remove(file.getPath());
    }
    
    /**
     * Clears the entire segment cache.
     */
    public void clearAllSegmentCache() {
        segmentCache.clear();
    }
    
    /**
     * Segments the given content into smaller parts.
     * This reduces memory usage for large templates by allowing parts of the template
     * to be garbage collected when not in use.
     *
     * @param content The content to segment
     * @return The segmented content
     */
    @NotNull
    private TemplateSegments segmentContent(@NotNull String content) {
        if (content.length() <= MAX_SEGMENT_SIZE) {
            // Small file, treat as a single segment
            return new TemplateSegments(content);
        }
        
        // Large file, split into segments
        int segmentCount = (content.length() + MAX_SEGMENT_SIZE - 1) / MAX_SEGMENT_SIZE;
        String[] segments = new String[segmentCount];
        
        for (int i = 0; i < segmentCount; i++) {
            int start = i * MAX_SEGMENT_SIZE;
            int end = Math.min(start + MAX_SEGMENT_SIZE, content.length());
            
            // Adjust segment boundaries to avoid splitting Latte macros
            if (i > 0) {
                start = adjustSegmentStart(content, start);
            }
            if (i < segmentCount - 1) {
                end = adjustSegmentEnd(content, end);
            }
            
            segments[i] = content.substring(start, end);
        }
        
        return new TemplateSegments(content, segments);
    }
    
    /**
     * Adjusts the start of a segment to avoid splitting Latte macros.
     *
     * @param content The content of the file
     * @param start The initial start position
     * @return The adjusted start position
     */
    private int adjustSegmentStart(@NotNull String content, int start) {
        // Search backward for a safe boundary (end of a line or end of a Latte macro)
        for (int i = start; i > Math.max(0, start - 100); i--) {
            if (content.charAt(i) == '\n') {
                return i + 1; // Start of a new line
            }
            if (i > 0 && content.charAt(i) == '}' && content.charAt(i - 1) != '}') {
                return i + 1; // End of a Latte macro
            }
        }
        
        // If no safe boundary found, use the original start
        return start;
    }
    
    /**
     * Adjusts the end of a segment to avoid splitting Latte macros.
     *
     * @param content The content of the file
     * @param end The initial end position
     * @return The adjusted end position
     */
    private int adjustSegmentEnd(@NotNull String content, int end) {
        // Search forward for a safe boundary (end of a line or end of a Latte macro)
        for (int i = end; i < Math.min(content.length(), end + 100); i++) {
            if (content.charAt(i) == '\n') {
                return i + 1; // End of a line
            }
            if (i > 0 && content.charAt(i) == '}' && content.charAt(i - 1) != '}') {
                return i + 1; // End of a Latte macro
            }
        }
        
        // If no safe boundary found, use the original end
        return end;
    }
    
    /**
     * Class representing segmented template content.
     */
    public static class TemplateSegments {
        private final String fullContent;
        private final String[] segments;
        private final int[] segmentOffsets;
        
        /**
         * Constructor for a single segment.
         *
         * @param content The content of the template
         */
        TemplateSegments(@NotNull String content) {
            this.fullContent = content;
            this.segments = new String[] { content };
            this.segmentOffsets = new int[] { 0 };
        }
        
        /**
         * Constructor for multiple segments.
         *
         * @param fullContent The full content of the template
         * @param segments The segments of the template
         */
        TemplateSegments(@NotNull String fullContent, @NotNull String[] segments) {
            this.fullContent = fullContent;
            this.segments = segments;
            this.segmentOffsets = new int[segments.length];
            
            // Calculate offsets for each segment
            int offset = 0;
            for (int i = 0; i < segments.length; i++) {
                segmentOffsets[i] = offset;
                offset += segments[i].length();
            }
        }
        
        /**
         * Gets the full content of the template.
         *
         * @return The full content
         */
        @NotNull
        public String getFullContent() {
            return fullContent;
        }
        
        /**
         * Gets the segment that contains the given offset.
         *
         * @param offset The offset in the full content
         * @return The segment containing the offset
         */
        @NotNull
        public String getSegmentForOffset(int offset) {
            int segmentIndex = getSegmentIndexForOffset(offset);
            return segments[segmentIndex];
        }
        
        /**
         * Gets the offset within a segment for the given offset in the full content.
         *
         * @param offset The offset in the full content
         * @return The offset within the segment
         */
        public int getOffsetWithinSegment(int offset) {
            int segmentIndex = getSegmentIndexForOffset(offset);
            return offset - segmentOffsets[segmentIndex];
        }
        
        /**
         * Gets the number of segments.
         *
         * @return The number of segments
         */
        public int getSegmentCount() {
            return segments.length;
        }
        
        /**
         * Checks if these segments are valid for the given content.
         *
         * @param content The content to check against
         * @return True if the segments are valid for the content, false otherwise
         */
        public boolean isValidFor(@NotNull String content) {
            return fullContent.equals(content);
        }
        
        /**
         * Gets the index of the segment that contains the given offset.
         *
         * @param offset The offset in the full content
         * @return The index of the segment
         */
        private int getSegmentIndexForOffset(int offset) {
            for (int i = 0; i < segmentOffsets.length - 1; i++) {
                if (offset < segmentOffsets[i + 1]) {
                    return i;
                }
            }
            return segmentOffsets.length - 1;
        }
    }
}