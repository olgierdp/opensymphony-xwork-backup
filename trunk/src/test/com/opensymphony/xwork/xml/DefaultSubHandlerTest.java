/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.xml;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;

import com.opensymphony.xwork.xml.DefaultSubHandler;
import com.opensymphony.xwork.xml.DelegatingHandler;
import com.opensymphony.xwork.xml.Path;

import junit.framework.TestCase;


/**
 * DefaultSubHandlerTest
 * @author Jason Carreira
 * Created May 16, 2003 3:27:55 PM
 */
public class DefaultSubHandlerTest extends TestCase {
    //~ Instance fields ////////////////////////////////////////////////////////

    DefaultSubHandler handler;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setUp() {
        handler = new TestSubHandler("/Foo");
    }

    public void testRegisterWithAndUnregister() {
        Path path = Path.getInstance("/Foo");
        Mock delegatingHandlerMock = new Mock(DelegatingHandler.class);
        delegatingHandlerMock.expectVoid("addHandler", C.args(C.eq(path), C.eq(handler)));

        DelegatingHandler delegatingHandler = (DelegatingHandler) delegatingHandlerMock.proxy();
        handler.registerWith(delegatingHandler);
        assertEquals(delegatingHandler, handler.getRootHandler());
        delegatingHandlerMock.expectVoid("removeHandler", C.args(C.eq(path), C.eq(handler)));
        handler.unregisterWith(delegatingHandler);
        assertNull(handler.getRootHandler());
        delegatingHandlerMock.verify();
    }

    public void testXmlEncode() {
        String inString = "< & > " + '"';
        String outString = "&lt; &amp; &gt; &quot;";
        String result = handler.xmlEncode(inString).toString();
        assertEquals(outString, result);
    }
}
