/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers;

import com.opensymphony.xwork.config.ConfigurationProvider;

/**
 * @author tmjee
 * @version $Date$ $Id$
 */
public class XmlConfigurationProviderParametersTest extends ConfigurationTestBase {

    public void testBasic() throws Exception {

        ConfigurationProvider provider = buildConfigurationProvider("com/opensymphony/xwork/config/providers/xwork-test-parameters.xml");

        provider.init(configuration);

        assertEquals(configuration.getParameter("param1"), "value1");
        assertEquals(configuration.getParameter("param2"), "value2");
        assertEquals(configuration.getParameter("noSuchParameter"), null);
        assertEquals(configuration.getParameter("noSuchParameterAgain"), null);
    }

    public void testOverride() throws Exception {

        ConfigurationProvider provider = buildConfigurationProvider("com/opensymphony/xwork/config/providers/xwork-test-parameters-override.xml");

        provider.init(configuration);

        assertEquals(configuration.getParameter("param1"), "value1");
        assertEquals(configuration.getParameter("param2"), "value2");
        assertEquals(configuration.getParameter("param3"), "overridenValue3");
        assertEquals(configuration.getParameter("param4"), "overridenValue4");
        assertEquals(configuration.getParameter("param5"), "value5");
        assertEquals(configuration.getParameter("param6"), null);
        assertEquals(configuration.getParameter("param7"), null);
    }
}

