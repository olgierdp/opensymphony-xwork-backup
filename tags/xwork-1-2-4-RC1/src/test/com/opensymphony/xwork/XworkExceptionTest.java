/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.xwork.util.location.*;
import com.opensymphony.util.ClassLoaderUtil;
import junit.framework.TestCase;

import java.util.List;
import java.net.URL;

/**
 * XworkException tests
 */
public class XworkExceptionTest extends TestCase {

    public void testGetSnippet() throws Exception {
        URL url = ClassLoaderUtil.getResource("com/opensymphony/xwork/somefile.txt", getClass());
        Location loc = new LocationImpl("foo", url.toString(), 3, 2);
        XworkException ex = new XworkException("Some message", loc);
        
        List snippet = ex.getSnippet(1);
        assertNotNull(snippet);
        assertTrue("Wrong length: "+snippet.size(), 3 == snippet.size());
        
        assertTrue("is".equals(snippet.get(0)));
        assertTrue("a".equals(snippet.get(1)));
        assertTrue("file".equals(snippet.get(2)));
    }
    
    public void testGetSnippetNoPadding() throws Exception {
        URL url = ClassLoaderUtil.getResource("com/opensymphony/xwork/somefile.txt", getClass());
        Location loc = new LocationImpl("foo", url.toString(), 3, 2);
        XworkException ex = new XworkException("Some message", loc);
        
        List snippet = ex.getSnippet(0);
        assertNotNull(snippet);
        assertTrue("Wrong length: "+snippet.size(), 1 == snippet.size());
        
        assertTrue("a".equals(snippet.get(0)));
    }
}
