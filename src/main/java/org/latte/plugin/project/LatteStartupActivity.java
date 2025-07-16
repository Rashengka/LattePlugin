package org.latte.plugin.project;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;
import org.latte.plugin.version.LatteVersionDetector;
import org.latte.plugin.version.NettePackageDetector;

/**
 * Startup activity that initializes version and package detection when a project is opened.
 */
public class LatteStartupActivity implements StartupActivity {

    /**
     * Called when a project is opened.
     * Initializes version and package detection.
     *
     * @param project The opened project
     */
    @Override
    public void runActivity(@NotNull Project project) {
        // Detect Latte version
        LatteVersionDetector.detectVersion(project);
        
        // Detect Nette packages
        NettePackageDetector.detectAndUpdateSettings(project);
    }
}