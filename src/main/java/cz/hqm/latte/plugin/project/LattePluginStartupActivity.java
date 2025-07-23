package cz.hqm.latte.plugin.project;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.util.Disposer;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import cz.hqm.latte.plugin.lexer.LatteLexerFactory;

/**
 * Startup activity for the Latte plugin.
 * This class is responsible for initializing plugin components
 * and registering them with the IntelliJ platform.
 */
public class LattePluginStartupActivity implements ProjectActivity {
    
    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        // Since LatteLexerFactory is an application-level service,
        // it should already be properly registered for disposal by the platform.
        // However, we can add additional cleanup logic here if needed.
        
        // For example, we can register a project-level disposable to clear
        // the lexer cache when a project is closed
        Disposer.register(project, () -> {
            // Clear the lexer cache for the current thread when the project is closed
            LatteLexerFactory.getInstance().clearCache();
        });
        
        return Unit.INSTANCE;
    }
}