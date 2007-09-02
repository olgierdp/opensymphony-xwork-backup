/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import java.net.URL;
import java.util.Set;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.spring.SpringObjectFactory;

import junit.framework.TestCase;

public class ResolverUtilTest extends TestCase {

    public void testSimpleFind() throws Exception {
        ResolverUtil<ObjectFactory> resolver = new ResolverUtil<ObjectFactory>();
        resolver.findImplementations(ObjectFactory.class, "com");
        Set<Class<? extends ObjectFactory>> impls = resolver.getClasses();
        
        assertTrue(impls.contains(ObjectFactory.class));
        assertTrue(impls.contains(SpringObjectFactory.class));
    }
    
    public void testMissingSomeFind() throws Exception {
        ResolverUtil<ObjectFactory> resolver = new ResolverUtil<ObjectFactory>();
        resolver.findImplementations(ObjectFactory.class, "com.opensymphony.xwork2.spring");
        Set<Class<? extends ObjectFactory>> impls = resolver.getClasses();
        
        assertFalse(impls.contains(ObjectFactory.class));
        assertTrue(impls.contains(SpringObjectFactory.class));
    }
    
    public void testFindNamedResource() throws Exception {
        ResolverUtil resolver = new ResolverUtil();
        resolver.findNamedResource("xwork-default.xml", "");
        Set<URL> impls = resolver.getResources();
        
        assertTrue(impls.size() > 0);
    }
    
    public void testFindNamedResourceInDir() throws Exception {
        ResolverUtil resolver = new ResolverUtil();
        resolver.findNamedResource("SimpleAction.properties", "com/opensymphony");
        Set<URL> impls = resolver.getResources();
        
        assertTrue(impls.size() > 0);
    }

}
