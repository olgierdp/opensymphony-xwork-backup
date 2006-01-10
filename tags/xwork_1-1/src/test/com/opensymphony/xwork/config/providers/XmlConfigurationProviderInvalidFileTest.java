/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers;

import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.config.ConfigurationProvider;


/**
 * XmlConfigurationProviderInvalidFileTest
 *
 * @author Jason Carreira
 *         Created Sep 6, 2003 2:36:10 PM
 */
public class XmlConfigurationProviderInvalidFileTest extends ConfigurationTestBase {

    public void testInvalidFileThrowsException() {
        final String filename = "com/opensymphony/xwork/config/providers/xwork-test-invalid-file.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        try {
            provider.init(configuration);
            fail();
        } catch (ConfigurationException e) {
            // this is what we expect
        }
    }
}
