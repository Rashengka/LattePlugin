package cz.hqm.latte.plugin.test.version;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import cz.hqm.latte.plugin.test.LattePluginTestBase;
import cz.hqm.latte.plugin.version.NettePackageDetector;

/**
 * Tests for the NettePackageDetector class.
 */
public class NettePackageDetectorTest extends LattePluginTestBase {

    /**
     * Tests that the NettePackageDetector correctly detects package versions.
     */
    @Test
    public void testPackageVersionDetection() {
        // Test default version when no project is provided
        assertEquals(3, NettePackageDetector.getPackageVersion(null, NettePackageDetector.NETTE_APPLICATION));
        assertEquals(3, NettePackageDetector.getPackageVersion(null, NettePackageDetector.NETTE_FORMS));
        assertEquals(3, NettePackageDetector.getPackageVersion(null, NettePackageDetector.NETTE_ASSETS));
        assertEquals(3, NettePackageDetector.getPackageVersion(null, NettePackageDetector.NETTE_DATABASE));
    }

    /**
     * Tests that the NettePackageDetector correctly detects package presence.
     */
    @Test
    public void testPackagePresenceDetection() {
        // Test default presence when no project is provided
        assertFalse(NettePackageDetector.isPackagePresent(null, NettePackageDetector.NETTE_APPLICATION));
        assertFalse(NettePackageDetector.isPackagePresent(null, NettePackageDetector.NETTE_FORMS));
        assertFalse(NettePackageDetector.isPackagePresent(null, NettePackageDetector.NETTE_ASSETS));
        assertFalse(NettePackageDetector.isPackagePresent(null, NettePackageDetector.NETTE_DATABASE));
    }

    /**
     * Tests that the NettePackageDetector correctly handles cache clearing.
     */
    @Test
    public void testCacheClearing() {
        // Test that clearing the cache doesn't cause errors
        NettePackageDetector.clearAllCache();
        NettePackageDetector.clearCache(null);
        NettePackageDetector.clearCache(getProject());
        
        // Test that we can still get versions after clearing the cache
        assertEquals(3, NettePackageDetector.getPackageVersion(null, NettePackageDetector.NETTE_APPLICATION));
    }
}
