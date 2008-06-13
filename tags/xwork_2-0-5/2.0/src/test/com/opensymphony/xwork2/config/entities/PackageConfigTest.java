/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.XWorkTestCase;

public class PackageConfigTest extends XWorkTestCase {

    public void testFullDefaultInterceptorRef() {
        PackageConfig cfg1 = new PackageConfig("pkg1");
        cfg1.setDefaultInterceptorRef("ref1");
        PackageConfig cfg2 = new PackageConfig("pkg2");
        cfg2.setDefaultInterceptorRef("ref2");
        PackageConfig cfg = new PackageConfig("pkg");
        cfg.addParent(cfg1);
        cfg.addParent(cfg2);
        
        assertEquals("ref2", cfg.getFullDefaultInterceptorRef());
    }
    
}