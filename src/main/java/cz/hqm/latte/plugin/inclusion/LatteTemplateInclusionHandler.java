package cz.hqm.latte.plugin.inclusion;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import cz.hqm.latte.plugin.version.LatteVersion;
import cz.hqm.latte.plugin.version.LatteVersionManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles template inclusion and inheritance in Latte templates.
 * Supports {include}, {includeBlock}, {sandbox}, {define}, and {block} tags.
 */
public class LatteTemplateInclusionHandler {

    // Pattern for extracting file path from {include} tag
    private static final Pattern INCLUDE_FILE_PATTERN = Pattern.compile("\\{include\\s+['\"](.*?)['\"]");
    
    // Pattern for extracting block name from {include #blockName} tag
    private static final Pattern INCLUDE_BLOCK_PATTERN = Pattern.compile("\\{include\\s+#(\\w+)");
    
    // Pattern for extracting file path from {includeBlock} tag
    private static final Pattern INCLUDE_BLOCK_FILE_PATTERN = Pattern.compile("\\{includeBlock\\s+['\"](.*?)['\"]");
    
    // Pattern for extracting file path from {sandbox} tag
    private static final Pattern SANDBOX_FILE_PATTERN = Pattern.compile("\\{sandbox\\s+['\"](.*?)['\"]");
    
    // Pattern for extracting block name from {define} tag
    private static final Pattern DEFINE_BLOCK_PATTERN = Pattern.compile("\\{define\\s+(\\w+)");
    
    // Pattern for extracting block name from {block} tag
    private static final Pattern BLOCK_PATTERN = Pattern.compile("\\{block\\s+(\\w+)");

    /**
     * Finds the target file for an {include} tag.
     * 
     * @param project The project
     * @param sourceFile The source file containing the {include} tag
     * @param includeText The text of the {include} tag
     * @return The target file, or null if not found
     */
    @Nullable
    public static PsiFile findIncludeTargetFile(Project project, PsiFile sourceFile, String includeText) {
        Matcher matcher = INCLUDE_FILE_PATTERN.matcher(includeText);
        if (!matcher.find()) {
            return null;
        }
        
        String filePath = matcher.group(1);
        return findFileByRelativePath(project, sourceFile, filePath);
    }
    
    /**
     * Finds the target block for an {include #blockName} tag.
     * 
     * @param project The project
     * @param sourceFile The source file containing the {include} tag
     * @param includeText The text of the {include} tag
     * @return The target block element, or null if not found
     */
    @Nullable
    public static PsiElement findIncludeTargetBlock(Project project, PsiFile sourceFile, String includeText) {
        Matcher matcher = INCLUDE_BLOCK_PATTERN.matcher(includeText);
        if (!matcher.find()) {
            return null;
        }
        
        String blockName = matcher.group(1);
        return findBlockInFile(sourceFile, blockName);
    }
    
    /**
     * Finds the target file for an {includeBlock} tag.
     * 
     * @param project The project
     * @param sourceFile The source file containing the {includeBlock} tag
     * @param includeBlockText The text of the {includeBlock} tag
     * @return The target file, or null if not found
     */
    @Nullable
    public static PsiFile findIncludeBlockTargetFile(Project project, PsiFile sourceFile, String includeBlockText) {
        // Check if {includeBlock} is supported in the current Latte version
        LatteVersion version = LatteVersionManager.getCurrentVersion();
        if (version == LatteVersion.VERSION_4X || version == LatteVersion.VERSION_4_0) {
            // {includeBlock} is not supported in Latte 4.x
            return null;
        }
        
        Matcher matcher = INCLUDE_BLOCK_FILE_PATTERN.matcher(includeBlockText);
        if (!matcher.find()) {
            return null;
        }
        
        String filePath = matcher.group(1);
        return findFileByRelativePath(project, sourceFile, filePath);
    }
    
    /**
     * Finds the target file for a {sandbox} tag.
     * 
     * @param project The project
     * @param sourceFile The source file containing the {sandbox} tag
     * @param sandboxText The text of the {sandbox} tag
     * @return The target file, or null if not found
     */
    @Nullable
    public static PsiFile findSandboxTargetFile(Project project, PsiFile sourceFile, String sandboxText) {
        Matcher matcher = SANDBOX_FILE_PATTERN.matcher(sandboxText);
        if (!matcher.find()) {
            return null;
        }
        
        String filePath = matcher.group(1);
        return findFileByRelativePath(project, sourceFile, filePath);
    }
    
    /**
     * Finds a block in a file by name.
     * 
     * @param file The file to search in
     * @param blockName The name of the block to find
     * @return The block element, or null if not found
     */
    @Nullable
    public static PsiElement findBlockInFile(PsiFile file, String blockName) {
        if (file == null) {
            return null;
        }
        
        String fileContent = file.getText();
        
        // Look for {define blockName} tag
        Pattern definePattern = Pattern.compile("\\{define\\s+" + blockName + "\\b");
        Matcher defineMatcher = definePattern.matcher(fileContent);
        if (defineMatcher.find()) {
            int startOffset = defineMatcher.start();
            return file.findElementAt(startOffset);
        }
        
        // Look for {block blockName} tag
        Pattern blockPattern = Pattern.compile("\\{block\\s+" + blockName + "\\b");
        Matcher blockMatcher = blockPattern.matcher(fileContent);
        if (blockMatcher.find()) {
            int startOffset = blockMatcher.start();
            return file.findElementAt(startOffset);
        }
        
        return null;
    }
    
    /**
     * Finds all blocks defined in a file.
     * 
     * @param file The file to search in
     * @return A list of block names
     */
    @NotNull
    public static List<String> findBlocksInFile(PsiFile file) {
        List<String> blocks = new ArrayList<>();
        
        if (file == null) {
            return blocks;
        }
        
        String fileContent = file.getText();
        
        // Find all {define} blocks
        Matcher defineMatcher = DEFINE_BLOCK_PATTERN.matcher(fileContent);
        while (defineMatcher.find()) {
            blocks.add(defineMatcher.group(1));
        }
        
        // Find all {block} blocks
        Matcher blockMatcher = BLOCK_PATTERN.matcher(fileContent);
        while (blockMatcher.find()) {
            blocks.add(blockMatcher.group(1));
        }
        
        return blocks;
    }
    
    /**
     * Finds a file by its relative path from a source file.
     * 
     * @param project The project
     * @param sourceFile The source file
     * @param relativePath The relative path
     * @return The target file, or null if not found
     */
    @Nullable
    private static PsiFile findFileByRelativePath(Project project, PsiFile sourceFile, String relativePath) {
        if (sourceFile == null || relativePath == null || relativePath.isEmpty()) {
            return null;
        }
        
        // Get the directory of the source file
        VirtualFile sourceDir = sourceFile.getContainingDirectory().getVirtualFile();
        
        // Resolve the relative path
        File targetFile = new File(sourceDir.getPath(), relativePath);
        
        // Check if the file exists
        if (!targetFile.exists()) {
            // Try to find the file by name in the project
            String fileName = new File(relativePath).getName();
            PsiFile[] files = FilenameIndex.getFilesByName(project, fileName, GlobalSearchScope.projectScope(project));
            if (files.length > 0) {
                return files[0];
            }
            return null;
        }
        
        // Convert to VirtualFile
        VirtualFile targetVirtualFile = LocalFileSystem.getInstance().findFileByIoFile(targetFile);
        if (targetVirtualFile == null) {
            return null;
        }
        
        // Convert to PsiFile
        return PsiManager.getInstance(project).findFile(targetVirtualFile);
    }
}