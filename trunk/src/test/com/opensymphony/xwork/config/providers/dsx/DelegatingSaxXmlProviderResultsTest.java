/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers.dsx;

import com.opensymphony.xwork.config.ConfigurationProvider;
import com.opensymphony.xwork.config.providers.XmlConfigurationProviderResultsTest;


/**
 * DelegatingSaxXmlProviderResultsTest
 * @author Jason Carreira
 * Created May 28, 2003 8:33:48 AM
 */
public class DelegatingSaxXmlProviderResultsTest extends XmlConfigurationProviderResultsTest {
    //~ Methods ////////////////////////////////////////////////////////////////

    protected ConfigurationProvider buildConfigurationProvider(String filename) {
        return new DelegatingSaxXmlProvider(filename);
    }
}
