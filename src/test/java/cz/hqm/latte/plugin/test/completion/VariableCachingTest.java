package cz.hqm.latte.plugin.test.completion;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import cz.hqm.latte.plugin.completion.NetteDefaultVariablesProvider;
import cz.hqm.latte.plugin.completion.NetteDefaultVariablesProvider.NetteVariable;
import cz.hqm.latte.plugin.settings.LatteSettings;
import org.junit.Test;

import java.util.List;

/**
 * Tests for the caching mechanism in NetteDefaultVariablesProvider.
 */
public class VariableCachingTest extends BasePlatformTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.out.println("[DEBUG_LOG] Setting up VariableCachingTest");
        
        // Ensure we have a clean state for each test
        LatteSettings settings = LatteSettings.getInstance();
        settings.setEnableNetteApplication(true);
        settings.setEnableNetteForms(true);
        settings.setEnableNetteDatabase(true);
        settings.setEnableNetteSecurity(true);
        settings.setEnableNetteHttp(true);
        settings.setEnableNetteMail(true);
        
        // Invalidate the cache to start fresh
        NetteDefaultVariablesProvider.invalidateCache();
    }

    /**
     * Tests that variables are cached and not recreated on each request.
     */
    @Test
    public void testVariableCaching() {
        System.out.println("[DEBUG_LOG] Running testVariableCaching");
        
        // First request should initialize the cache
        System.out.println("[DEBUG_LOG] First request - should initialize cache");
        List<NetteVariable> variables1 = NetteDefaultVariablesProvider.getAllVariables(getProject());
        
        // Second request should use the cache
        System.out.println("[DEBUG_LOG] Second request - should use cache");
        List<NetteVariable> variables2 = NetteDefaultVariablesProvider.getAllVariables(getProject());
        
        // Verify that we have variables
        assertFalse("Should have variables", variables1.isEmpty());
        assertFalse("Should have variables", variables2.isEmpty());
        
        // Verify that the same number of variables are returned
        assertEquals("Should have the same number of variables", variables1.size(), variables2.size());
    }

    /**
     * Tests that the cache is updated when settings change.
     */
    @Test
    public void testCacheUpdateOnSettingsChange() {
        System.out.println("[DEBUG_LOG] Running testCacheUpdateOnSettingsChange");
        
        // First request with all features enabled
        System.out.println("[DEBUG_LOG] First request with all features enabled");
        List<NetteVariable> variables1 = NetteDefaultVariablesProvider.getAllVariables(getProject());
        
        // Change settings
        System.out.println("[DEBUG_LOG] Changing settings");
        LatteSettings settings = LatteSettings.getInstance();
        settings.setEnableNetteHttp(false);
        
        // Second request should update the cache due to settings change
        System.out.println("[DEBUG_LOG] Second request after settings change");
        List<NetteVariable> variables2 = NetteDefaultVariablesProvider.getAllVariables(getProject());
        
        // Verify that we have variables
        assertFalse("Should have variables", variables1.isEmpty());
        assertFalse("Should have variables", variables2.isEmpty());
        
        // Verify that the number of variables has changed
        assertTrue("Should have fewer variables after disabling HTTP", variables2.size() < variables1.size());
        
        // Verify that HTTP variables are not included
        boolean hasHttpVariables = false;
        for (NetteVariable variable : variables2) {
            if (variable.getName().equals("httpRequest") || 
                variable.getName().equals("httpResponse") || 
                variable.getName().equals("session")) {
                hasHttpVariables = true;
                break;
            }
        }
        
        assertFalse("Should not have HTTP variables after disabling HTTP", hasHttpVariables);
    }
}