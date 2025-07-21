package cz.hqm.latte.plugin.cache;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import cz.hqm.latte.plugin.psi.LatteFile;

import java.util.LinkedHashMap;
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
    
    // Minimum time between validity checks in milliseconds (1 second)
    private static final long MIN_CHECK_INTERVAL = TimeUnit.SECONDS.toMillis(1);
    
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
        // Use LinkedHashMap with access order to implement LRU cache efficiently
        this.templateCache = new LinkedHashMap<String, CacheEntry>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        };
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
        CacheEntry entry;
        
        // Use synchronized block only for the get operation to minimize contention
        synchronized (templateCache) {
            entry = templateCache.get(filePath);
        }
        
        if (entry == null) {
            return null;
        }
        
        long currentTime = System.currentTimeMillis();
        
        // Only check validity if enough time has passed since the last check
        // This reduces the overhead of frequent validity checks
        if (currentTime - entry.lastValidityCheckTime > MIN_CHECK_INTERVAL) {
            if (!isEntryValid(entry, file)) {
                synchronized (templateCache) {
                    templateCache.remove(filePath);
                }
                return null;
            }
            entry.lastValidityCheckTime = currentTime;
        }
        
        // The LinkedHashMap will automatically update access order
        return entry.template;
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
        
        // Add to cache with synchronization to ensure thread safety
        synchronized (templateCache) {
            templateCache.put(filePath, entry);
            // The LinkedHashMap will automatically handle LRU eviction
        }
    }
    
    /**
     * Invalidates the cache entry for the given file.
     *
     * @param file The virtual file to invalidate the cache for
     */
    public void invalidateCache(@NotNull VirtualFile file) {
        synchronized (templateCache) {
            templateCache.remove(file.getPath());
        }
    }
    
    /**
     * Clears the entire cache.
     */
    public void clearCache() {
        synchronized (templateCache) {
            templateCache.clear();
        }
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
     * Class representing a cache entry.
     */
    private static class CacheEntry {
        final LatteFile template;
        final long modificationStamp;
        final long creationTime;
        long lastAccessTime;
        long lastValidityCheckTime;
        
        CacheEntry(LatteFile template, long modificationStamp, long creationTime, long lastAccessTime) {
            this.template = template;
            this.modificationStamp = modificationStamp;
            this.creationTime = creationTime;
            this.lastAccessTime = lastAccessTime;
            this.lastValidityCheckTime = lastAccessTime; // Initialize validity check time
        }
    }
}