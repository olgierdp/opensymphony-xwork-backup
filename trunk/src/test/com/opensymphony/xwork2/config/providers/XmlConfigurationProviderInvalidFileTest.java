/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;


/**
 * XmlConfigurationProviderInvalidFileTest
 *
 * @author Jason Carreira
 *         Created Sep 6, 2003 2:36:10 PM
 */
public class XmlConfigurationProviderInvalidFileTest extends ConfigurationTestBase {

    public void testInvalidFileThrowsException() {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-invalid-file.xml";

        try {
            ConfigurationProvider provider = buildConfigurationProvider(filename);
            fail();
        } catch (ConfigurationException e) {
            // this is what we expect
        }
    }
}
