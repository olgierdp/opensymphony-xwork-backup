/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers.dsx;

import com.opensymphony.xwork.config.ConfigurationProvider;
import com.opensymphony.xwork.config.providers.XmlConfigurationProviderMultilevelTest;


/**
 * Verify that Interceptor inheritance is happy for multi-level package derivations
 *
 * @author $Author$
 * @version $Revision$
 */
public class DelegatingSaxXmlProviderMultilevelTest extends XmlConfigurationProviderMultilevelTest {
    //~ Methods ////////////////////////////////////////////////////////////////

    protected ConfigurationProvider buildConfigurationProvider(String filename) {
        return new DelegatingSaxXmlProvider(filename);
    }
}
