/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers.dsx;

import com.opensymphony.xwork.config.ConfigurationProvider;
import com.opensymphony.xwork.config.providers.XmlConfigurationProviderActionsTest;


/**
 * Created by IntelliJ IDEA.
 * User: Mike
 * Date: May 6, 2003
 * Time: 3:10:16 PM
 * To change this template use Options | File Templates.
 */
public class DelegatingSaxXmlProviderActionsTest extends XmlConfigurationProviderActionsTest {
    //~ Methods ////////////////////////////////////////////////////////////////

    protected ConfigurationProvider buildConfigurationProvider(String filename) {
        return new DelegatingSaxXmlProvider(filename);
    }
}
