package org.latte.plugin.project;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.latte.plugin.version.LatteVersionDetector;
import org.latte.plugin.version.NettePackageDetector;

/**
 * Project service that initializes version and package detection when a project is opened.
 * This service is automatically instantiated when a project is opened.
 */
@Service(Service.Level.PROJECT)
public final class LatteProjectService {
    
    private final Project project;

    /**
     * Constructor that initializes version and package detection.
     *
     * @param project The project this service is associated with
     */
    public LatteProjectService(Project project) {
        this.project = project;
        
        // Initialize detection
        detectVersionAndPackages();
    }
    
    /**
     * Detects the Latte version and Nette packages for the current project.
     * This can be called manually to refresh the detection when the composer.json file changes.
     */
    public void detectVersionAndPackages() {
        // Detect Latte version
        LatteVersionDetector.detectVersion(project);
        
        // Detect Nette packages
        NettePackageDetector.detectAndUpdateSettings(project);
    }
    
    /**
     * Gets the instance of this service for the given project.
     *
     * @param project The project to get the service for
     * @return The service instance
     */
    public static LatteProjectService getInstance(Project project) {
        return project.getService(LatteProjectService.class);
    }
}