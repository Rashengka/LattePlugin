package cz.hqm.latte.plugin.project;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.util.Disposer;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import cz.hqm.latte.plugin.lexer.LatteLexerFactory;
import cz.hqm.latte.plugin.util.LatteLogger;

/**
 * Startup activity for the Latte plugin.
 * This class is responsible for initializing plugin components
 * and registering them with the IntelliJ platform.
 */
public class LattePluginStartupActivity implements ProjectActivity {
    private static final Logger LOG = Logger.getInstance(LattePluginStartupActivity.class);
    
    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        // Test the logging system to verify it's working correctly
        LatteLogger.logTestMessage();
        
        // Log a message about the plugin startup
        LatteLogger.info(LOG, "Latte Plugin started for project: " + project.getName());
        
        // Since LatteLexerFactory is an application-level service,
        // it should already be properly registered for disposal by the platform.
        // However, we can add additional cleanup logic here if needed.
        
        // For example, we can register a project-level disposable to clear
        // the lexer cache when a project is closed
        Disposer.register(project, () -> {
            // Clear the lexer cache for the current thread when the project is closed
            LatteLexerFactory.getInstance().clearCache();
            LatteLogger.debug(LOG, "Lexer cache cleared for project: " + project.getName());
        });
        
        return Unit.INSTANCE;
    }
}