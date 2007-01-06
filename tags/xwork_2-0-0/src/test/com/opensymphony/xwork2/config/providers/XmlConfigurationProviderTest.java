/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.providers;

import java.io.File;

import com.opensymphony.xwork2.config.ConfigurationProvider;
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
}
