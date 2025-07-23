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
import java.util.concurrent.atomic.AtomicInteger;

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
    private final ConcurrentHashMap<String, CacheEntry> templateCache;
    
    // Counter for LRU implementation
    private final AtomicInteger accessCounter = new AtomicInteger(0);
    
    /**
     * Constructor that initializes the cache.
     *
     * @param project The project this cache manager is associated with
     */
    public LatteCacheManager(Project project) {
        this.project = project;
        // Use ConcurrentHashMap for better thread safety and performance
        this.templateCache = new ConcurrentHashMap<>(16, 0.75f, 4);
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
        
        if (entry == null) {
            return null;
        }
        
        long currentTime = System.currentTimeMillis();
        
        // Update access counter and time atomically
        entry.lastAccessTime = currentTime;
        entry.accessCount = accessCounter.incrementAndGet();
        
        // Fast path: if the entry was recently validated, return it immediately
        if (currentTime - entry.lastValidityCheckTime <= MIN_CHECK_INTERVAL) {
            return entry.template;
        }
        
        // Slow path: check validity if enough time has passed since the last check
        if (!isEntryValid(entry, file)) {
            templateCache.remove(filePath, entry); // Only remove if it's still the same entry
            return null;
        }
        
        // Update validity check time
        entry.lastValidityCheckTime = currentTime;
        
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
        int currentAccessCount = accessCounter.incrementAndGet();
        
        // Create a new cache entry
        CacheEntry entry = new CacheEntry(template, file.getModificationStamp(), currentTime, currentTime);
        entry.accessCount = currentAccessCount;
        entry.lastValidityCheckTime = currentTime;
        
        // Add to cache - ConcurrentHashMap handles thread safety
        templateCache.put(filePath, entry);
        
        // Check if we need to evict entries (LRU policy)
        if (templateCache.size() > MAX_CACHE_SIZE) {
            evictOldestEntries();
        }
    }
    
    /**
     * Evicts the oldest entries from the cache based on access count.
     * This implements an LRU eviction policy.
     */
    private void evictOldestEntries() {
        // Find entries with the lowest access counts and remove them
        templateCache.entrySet().stream()
            .sorted((e1, e2) -> Integer.compare(e1.getValue().accessCount, e2.getValue().accessCount))
            .limit(templateCache.size() - MAX_CACHE_SIZE)
            .forEach(entry -> templateCache.remove(entry.getKey(), entry.getValue()));
    }
    
    /**
     * Invalidates the cache entry for the given file.
     *
     * @param file The virtual file to invalidate the cache for
     */
    public void invalidateCache(@NotNull VirtualFile file) {
        // ConcurrentHashMap handles thread safety
        templateCache.remove(file.getPath());
    }
    
    /**
     * Clears the entire cache.
     */
    public void clearCache() {
        // ConcurrentHashMap handles thread safety
        templateCache.clear();
        // Reset the access counter to avoid potential overflow after long-running sessions
        accessCounter.set(0);
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
        volatile long lastAccessTime;
        volatile long lastValidityCheckTime;
        volatile int accessCount;
        
        CacheEntry(LatteFile template, long modificationStamp, long creationTime, long lastAccessTime) {
            this.template = template;
            this.modificationStamp = modificationStamp;
            this.creationTime = creationTime;
            this.lastAccessTime = lastAccessTime;
            this.lastValidityCheckTime = lastAccessTime; // Initialize validity check time
            this.accessCount = 0;
        }
    }
}