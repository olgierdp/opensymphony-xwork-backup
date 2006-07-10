/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.impl.MockConfiguration;


/**
 * ConfigurationTestBase
 *
 * @author Jason Carreira
 *         Created Jun 9, 2003 7:42:12 AM
 */
public abstract class ConfigurationTestBase extends XWorkTestCase {

    protected Configuration configuration;


    protected void setUp() throws Exception {
        super.setUp();
        configuration = new MockConfiguration();
    }

    protected ConfigurationProvider buildConfigurationProvider(final String filename) {
        return new XmlConfigurationProvider(filename);
    }
}
