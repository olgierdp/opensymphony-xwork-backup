/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.providers;

import java.io.File;
import java.util.List;

import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.util.FileManager;


public class XmlConfigurationProviderTest extends ConfigurationTestBase {

    public void testNeedsReload() throws Exception {
        FileManager.setReloadingConfigs(true);
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-actions.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);
        
        assertTrue(!provider.needsReload());
        
        File file = new File(getClass().getResource("/"+filename).getFile());
        assertTrue(file.exists());
        file.setLastModified(System.currentTimeMillis());
        
        assertTrue(provider.needsReload());
    }

    public void testInheritence() throws Exception {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-include-parent.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        provider.init(configuration);
        provider.loadPackages();

        // test expectations
        assertEquals(6, configuration.getPackageConfigs().size());

        
        PackageConfig defaultPackage = configuration.getPackageConfig("default");
        assertNotNull(defaultPackage);
        assertEquals("default", defaultPackage.getName());


        PackageConfig namespace1 = configuration.getPackageConfig("namespace1");
        assertNotNull(namespace1);
        assertEquals("namespace1", namespace1.getName());
        assertEquals(defaultPackage, namespace1.getParents().get(0));

        PackageConfig namespace2 = configuration.getPackageConfig("namespace2");
        assertNotNull(namespace2);
        assertEquals("namespace2", namespace2.getName());
        assertEquals(1, namespace2.getParents().size());
        assertEquals(namespace1, namespace2.getParents().get(0));

        
        PackageConfig namespace4 = configuration.getPackageConfig("namespace4");
        assertNotNull(namespace4);
        assertEquals("namespace4", namespace4.getName());
        assertEquals(1, namespace4.getParents().size());
        assertEquals(namespace1, namespace4.getParents().get(0));

        
        PackageConfig namespace5 = configuration.getPackageConfig("namespace5");
        assertNotNull(namespace5);
        assertEquals("namespace5", namespace5.getName());
        assertEquals(1, namespace5.getParents().size());
        assertEquals(namespace4, namespace5.getParents().get(0));

    }
}
