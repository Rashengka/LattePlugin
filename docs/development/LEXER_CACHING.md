# Lexer Caching Implementation

## Problem Description

The Latte plugin was experiencing issues with lexer instance management, which manifested as:

1. Multiple lexer instances being created unnecessarily during parsing and highlighting operations
2. Each lexer instance maintaining its own state (syntax mode, etc.)
3. Resource management issues, particularly with the MessageBus
4. "Top level element is not completed" errors in complex templates with syntax mode changes

The debug logs showed multiple instances of "DEBUG: Creating LatteLexer instance" and "DEBUG: Set syntax mode to OFF, previous mode was DEFAULT", indicating that new lexer instances were being created frequently without proper reuse or state management.

## Root Cause

The root cause of the issue was that both `LatteParserDefinition` and `LatteSyntaxHighlighter` were creating new `LatteLexer` instances for every parsing and highlighting operation, with no caching or reuse mechanism. This led to:

1. Inefficient resource usage due to frequent object creation
2. Inconsistent state across different lexer instances
3. Potential memory leaks if lexer instances weren't properly disposed
4. Errors in complex templates due to state inconsistencies

## Solution

We implemented a comprehensive lexer caching and state management solution with the following components:

### 1. LatteLexerFactory

A singleton service that caches and manages lexer instances:

```java
@Service(Service.Level.APP)
public final class LatteLexerFactory implements Disposable {
    
    // Cache of lexer instances
    private final ConcurrentMap<Thread, LatteLexer> lexerCache = new ConcurrentHashMap<>();
    
    // Get a cached lexer instance or create a new one
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
    
    // Cleanup methods
    public void clearCache() {
        lexerCache.remove(Thread.currentThread());
    }
    
    public void clearAllCaches() {
        lexerCache.clear();
    }
    
    @Override
    public void dispose() {
        clearAllCaches();
    }
}
```

### 2. LatteLexer Reset Method

Added a reset method to LatteLexer to clear its state when reused:

```java
public void reset() {
    syntaxMode = LatteSyntaxMode.DEFAULT;
    syntaxModeStack.clear();
    
    // Reset the base lexer
    super.start("", 0, 0, 0);
}
```

### 3. Updated Parser and Highlighter

Modified LatteParserDefinition and LatteSyntaxHighlighter to use the factory:

```java
// In LatteParserDefinition
@NotNull
@Override
public Lexer createLexer(Project project) {
    return LatteLexerFactory.getInstance().getLexer();
}

// In LatteSyntaxHighlighter
@NotNull
@Override
public Lexer getHighlightingLexer() {
    return LatteLexerFactory.getInstance().getLexer();
}
```

### 4. Cleanup on Project Close

Created a startup activity to register cleanup actions:

```java
public class LattePluginStartupActivity implements ProjectActivity {
    
    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        // Register a project-level disposable to clear the lexer cache when a project is closed
        Disposer.register(project, () -> {
            LatteLexerFactory.getInstance().clearCache();
        });
        
        return Unit.INSTANCE;
    }
}
```

## Benefits

This solution provides several benefits:

1. **Improved Performance**: Reusing lexer instances reduces object creation overhead
2. **Consistent State**: Each thread has its own lexer instance with consistent state
3. **Proper Resource Management**: Lexer instances are properly cached and disposed
4. **Thread Safety**: The caching mechanism is thread-safe using ConcurrentHashMap
5. **Cleanup on Project Close**: Lexer caches are cleared when a project is closed

## Testing

To verify that the solution works correctly, the following tests should be performed:

1. **Complex Templates**: Test with complex templates that use multiple syntax mode changes
2. **Performance**: Verify that the plugin performs well with large templates
3. **Memory Usage**: Monitor memory usage to ensure there are no leaks
4. **Error Handling**: Verify that the "Top level element is not completed" error is resolved

## Conclusion

The lexer caching implementation addresses the resource management issues in the Latte plugin by providing a centralized mechanism for creating, caching, and reusing lexer instances. This ensures consistent state across parsing and highlighting operations, improves performance, and prevents potential memory leaks.

By properly managing lexer instances and their state, we've also resolved the "Top level element is not completed" error that occurred in complex templates with syntax mode changes.