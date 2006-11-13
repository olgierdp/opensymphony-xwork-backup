/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.ObjectFactory;
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

    protected ConfigurationProvider buildConfigurationProvider(final String filename) {
        configuration = new MockConfiguration();
        container = configuration.getContainer();
        
        XmlConfigurationProvider prov = new XmlConfigurationProvider(filename, true);
        prov.setObjectFactory(new ObjectFactory());
        prov.init(configuration);
        prov.loadPackages();
        return prov;
    }
}
