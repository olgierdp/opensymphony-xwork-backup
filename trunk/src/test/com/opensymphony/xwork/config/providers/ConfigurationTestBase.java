/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers;

import com.opensymphony.xwork.config.Configuration;
import com.opensymphony.xwork.config.ConfigurationProvider;
import com.opensymphony.xwork.config.impl.MockConfiguration;
import junit.framework.TestCase;


/**
 * ConfigurationTestBase
 *
 * @author Jason Carreira
 *         Created Jun 9, 2003 7:42:12 AM
 */
public abstract class ConfigurationTestBase extends TestCase {

    protected Configuration configuration;


    protected void setUp() throws Exception {
        super.setUp();
        configuration = new MockConfiguration();
    }

    protected ConfigurationProvider buildConfigurationProvider(final String filename) {
        return new XmlConfigurationProvider(filename);
    }
}
