package cz.hqm.latte.plugin.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Factory for creating and managing LatteLexer instances.
 * This service ensures that lexer instances are properly cached and reused,
 * which improves performance and reduces resource usage.
 */
@Service(Service.Level.APP)
public final class LatteLexerFactory implements Disposable {
    
    // Cache of lexer instances
    private final ConcurrentMap<Thread, LatteLexer> lexerCache = new ConcurrentHashMap<>();
    
    /**
     * Gets the instance of this factory.
     *
     * @return The factory instance
     */
    private static final LatteLexerFactory FALLBACK_INSTANCE = new LatteLexerFactory();
    
    public static LatteLexerFactory getInstance() {
        try {
            var app = ApplicationManager.getApplication();
            if (app != null) {
                return app.getService(LatteLexerFactory.class);
            }
        } catch (Throwable ignored) {
            // In non-IDE test environments ApplicationManager may be unavailable
        }
        // Fallback for environments where the IntelliJ Application is not initialized
        return FALLBACK_INSTANCE;
    }
    
    /**
     * Gets a lexer instance for the current thread.
     * If a cached instance exists, it will be reset and returned.
     * Otherwise, a new instance will be created, cached, and returned.
     *
     * @return A lexer instance
     */
    @NotNull
    public synchronized Lexer getLexer() {
        Thread currentThread = Thread.currentThread();
        LatteLexer lexer = lexerCache.get(currentThread);
        
        if (lexer == null) {
            lexer = new LatteLexer();
            lexerCache.put(currentThread, lexer);
        } else {
            lexer.reset();
        }
        
        return lexer;
    }
    
    /**
     * Clears the lexer cache for the current thread.
     * This should be called when the lexer is no longer needed.
     */
    public void clearCache() {
        lexerCache.remove(Thread.currentThread());
    }
    
    /**
     * Clears the entire lexer cache.
     * This should be called when the plugin is being unloaded.
     */
    public void clearAllCaches() {
        lexerCache.clear();
    }
    
    /**
     * Disposes of this factory and clears all caches.
     * This is called by the IntelliJ platform when the plugin is being unloaded.
     */
    @Override
    public void dispose() {
        clearAllCaches();
    }
}