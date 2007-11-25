/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers;

import com.opensymphony.xwork.XWorkTestCase;
import com.opensymphony.xwork.config.Configuration;
import com.opensymphony.xwork.config.ConfigurationProvider;
import com.opensymphony.xwork.mock.MockConfiguration;


/**
 * ConfigurationTestBase
 *
 * @author Jason Carreira
 * @version $Date$ $Id$
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
