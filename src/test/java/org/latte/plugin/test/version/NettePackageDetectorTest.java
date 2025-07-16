package org.latte.plugin.test.version;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.latte.plugin.version.NettePackageDetector;

/**
 * Tests for the NettePackageDetector class.
 */
public class NettePackageDetectorTest extends BasePlatformTestCase {

    /**
     * Tests that the NettePackageDetector correctly detects package versions.
     */
    public void testPackageVersionDetection() {
        // Test default version when no project is provided
        assertEquals(3, NettePackageDetector.getPackageVersion(null, NettePackageDetector.NETTE_APPLICATION));
        assertEquals(3, NettePackageDetector.getPackageVersion(null, NettePackageDetector.NETTE_FORMS));
        assertEquals(3, NettePackageDetector.getPackageVersion(null, NettePackageDetector.NETTE_ASSETS));
    }

    /**
     * Tests that the NettePackageDetector correctly detects package presence.
     */
    public void testPackagePresenceDetection() {
        // Test default presence when no project is provided
        assertFalse(NettePackageDetector.isPackagePresent(null, NettePackageDetector.NETTE_APPLICATION));
        assertFalse(NettePackageDetector.isPackagePresent(null, NettePackageDetector.NETTE_FORMS));
        assertFalse(NettePackageDetector.isPackagePresent(null, NettePackageDetector.NETTE_ASSETS));
    }

    /**
     * Tests that the NettePackageDetector correctly handles cache clearing.
     */
    public void testCacheClearing() {
        // Test that clearing the cache doesn't cause errors
        NettePackageDetector.clearAllCache();
        NettePackageDetector.clearCache(null);
        NettePackageDetector.clearCache(getProject());
        
        // Test that we can still get versions after clearing the cache
        assertEquals(3, NettePackageDetector.getPackageVersion(null, NettePackageDetector.NETTE_APPLICATION));
    }
}