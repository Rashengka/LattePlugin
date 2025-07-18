package org.latte.plugin.cache;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.latte.plugin.psi.LatteFile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Service for caching parsed Latte templates to improve performance.
 * This service provides methods for caching and retrieving parsed templates,
 * as well as managing the cache lifecycle.
 */
@Service(Service.Level.PROJECT)
public final class LatteCacheManager {

    // Maximum number of entries in the cache
    private static final int MAX_CACHE_SIZE = 100;
    
    // Maximum age of cache entries in milliseconds (30 minutes)
    private static final long MAX_CACHE_AGE = TimeUnit.MINUTES.toMillis(30);
    
    // The project this cache manager is associated with
    private final Project project;
    
    // Cache of parsed templates by file path
    private final Map<String, CacheEntry> templateCache;
    
    /**
     * Constructor that initializes the cache.
     *
     * @param project The project this cache manager is associated with
     */
    public LatteCacheManager(Project project) {
        this.project = project;
        this.templateCache = new ConcurrentHashMap<>();
    }
    
    /**
     * Gets the instance of this service for the given project.
     *
     * @param project The project to get the service for
     * @return The service instance
     */
    public static LatteCacheManager getInstance(@NotNull Project project) {
        return project.getService(LatteCacheManager.class);
    }
    
    /**
     * Gets a cached template for the given file, if available and valid.
     *
     * @param file The virtual file to get the cached template for
     * @return The cached template or null if not available or invalid
     */
    @Nullable
    public LatteFile getCachedTemplate(@NotNull VirtualFile file) {
        String filePath = file.getPath();
        CacheEntry entry = templateCache.get(filePath);
        
        // Check if entry exists and is valid
        if (entry != null && isEntryValid(entry, file)) {
            // Update last access time
            entry.lastAccessTime = System.currentTimeMillis();
            return entry.template;
        }
        
        return null;
    }
    
    /**
     * Caches a parsed template for the given file.
     *
     * @param file The virtual file the template is for
     * @param template The parsed template to cache
     */
    public void cacheTemplate(@NotNull VirtualFile file, @NotNull LatteFile template) {
        String filePath = file.getPath();
        long currentTime = System.currentTimeMillis();
        
        // Create new cache entry
        CacheEntry entry = new CacheEntry(template, file.getModificationStamp(), currentTime, currentTime);
        
        // Add to cache
        templateCache.put(filePath, entry);
        
        // Ensure cache doesn't exceed maximum size
        if (templateCache.size() > MAX_CACHE_SIZE) {
            // Remove least recently used entry
            String oldestKey = findOldestEntry();
            if (oldestKey != null) {
                templateCache.remove(oldestKey);
            }
        }
    }
    
    /**
     * Invalidates the cache entry for the given file.
     *
     * @param file The virtual file to invalidate the cache for
     */
    public void invalidateCache(@NotNull VirtualFile file) {
        templateCache.remove(file.getPath());
    }
    
    /**
     * Clears the entire cache.
     */
    public void clearCache() {
        templateCache.clear();
    }
    
    /**
     * Checks if a cache entry is valid for the given file.
     *
     * @param entry The cache entry to check
     * @param file The virtual file to check against
     * @return True if the entry is valid, false otherwise
     */
    private boolean isEntryValid(@NotNull CacheEntry entry, @NotNull VirtualFile file) {
        long currentTime = System.currentTimeMillis();
        
        // Check if entry is too old
        if (currentTime - entry.creationTime > MAX_CACHE_AGE) {
            return false;
        }
        
        // Check if file has been modified since entry was created
        return entry.modificationStamp == file.getModificationStamp();
    }
    
    /**
     * Finds the least recently used entry in the cache.
     *
     * @return The key of the least recently used entry, or null if the cache is empty
     */
    @Nullable
    private String findOldestEntry() {
        if (templateCache.isEmpty()) {
            return null;
        }
        
        String oldestKey = null;
        long oldestTime = Long.MAX_VALUE;
        
        for (Map.Entry<String, CacheEntry> entry : templateCache.entrySet()) {
            if (entry.getValue().lastAccessTime < oldestTime) {
                oldestTime = entry.getValue().lastAccessTime;
                oldestKey = entry.getKey();
            }
        }
        
        return oldestKey;
    }
    
    /**
     * Class representing a cache entry.
     */
    private static class CacheEntry {
        final LatteFile template;
        final long modificationStamp;
        final long creationTime;
        long lastAccessTime;
        
        CacheEntry(LatteFile template, long modificationStamp, long creationTime, long lastAccessTime) {
            this.template = template;
            this.modificationStamp = modificationStamp;
            this.creationTime = creationTime;
            this.lastAccessTime = lastAccessTime;
        }
    }
}