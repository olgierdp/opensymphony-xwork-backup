/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.util.location.LocationImpl;

/**
 * ActionConfigTest
 */
public class ActionConfigTest extends XWorkTestCase {

    public void testToString() {
        ActionConfig cfg = new ActionConfig.Builder("", "bob", "foo.Bar")
                .methodName("execute")
                .location(new LocationImpl(null, "foo/xwork.xml", 10, 12))
                .build();

        assertTrue("Wrong toString(): "+cfg.toString(), 
            "{ActionConfig bob (foo.Bar.execute()) - foo/xwork.xml:10:12}".equals(cfg.toString()));
    }
    
    public void testToStringWithNoMethod() {
        ActionConfig cfg = new ActionConfig.Builder("", "bob", "foo.Bar")
                .location(new LocationImpl(null, "foo/xwork.xml", 10, 12))
                .build();
        
        assertTrue("Wrong toString(): "+cfg.toString(),
            "{ActionConfig bob (foo.Bar) - foo/xwork.xml:10:12}".equals(cfg.toString()));
    }
}
