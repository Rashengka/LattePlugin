package cz.hqm.latte.plugin.test.attributes;

import org.junit.Test;
import cz.hqm.latte.plugin.custom.CustomAttribute;
import cz.hqm.latte.plugin.custom.CustomAttributesProvider;
import cz.hqm.latte.plugin.test.LattePluginTestBase;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests that require IntelliJ Project fixture for CustomAttributesProvider.
 */
public class EnhancedAttributeProviderTest extends LattePluginTestBase {

    @Test
    public void testCustomAttributesProvider() {
        var project = getProject();

        List<CustomAttribute> initialAttributes = CustomAttributesProvider.getAllAttributes(project);
        int initialCount = initialAttributes.size();

        CustomAttribute customAttribute = new CustomAttribute("n:test-attr", "Test attribute", "Example usage");

        boolean added = CustomAttributesProvider.addAttribute(project, customAttribute);
        assertTrue("Custom attribute should be added successfully", added);

        List<CustomAttribute> attributes = CustomAttributesProvider.getAllAttributes(project);
        assertEquals("Custom attribute should be added to the list", initialCount + 1, attributes.size());

        boolean found = false;
        for (CustomAttribute attr : attributes) {
            if (attr.getName().equals("n:test-attr")) {
                found = true;
                break;
            }
        }
        assertTrue("Custom attribute should be found in the list", found);

        boolean removed = CustomAttributesProvider.removeAttribute(project, "n:test-attr");
        assertTrue("Custom attribute should be removed successfully", removed);

        attributes = CustomAttributesProvider.getAllAttributes(project);
        assertEquals("Custom attribute should be removed from the list", initialCount, attributes.size());
    }
}
