/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.xml;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;

import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.xml.DefaultDelegatingHandler;
import com.opensymphony.xwork.xml.DelegatingHandler;
import com.opensymphony.xwork.xml.Path;
import com.opensymphony.xwork.xml.SubHandler;

import junit.framework.TestCase;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.List;
import java.util.Map;


/**
 * DefaultDelegatingHandlerTest
 * @author Jason Carreira
 * Created May 16, 2003 10:16:15 PM
 */
public class DefaultDelegatingHandlerTest extends TestCase {
    //~ Instance fields ////////////////////////////////////////////////////////

    private DefaultDelegatingHandler delegatingHandler;
    private Path fooPath = Path.getInstance("/Foo");

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setUp() {
        delegatingHandler = new DefaultDelegatingHandler();
    }

    public void testAddRemoveHandler() {
        TestSubHandler handler1 = new TestSubHandler(fooPath);
        TestSubHandler handler2 = new TestSubHandler(fooPath);
        delegatingHandler.addHandler(fooPath, handler1);

        List handlers = delegatingHandler.getHandlersForPath(fooPath);
        assertEquals(1, handlers.size());
        assertEquals(handler1, handlers.get(0));

        //make sure we can't add this more than once
        delegatingHandler.addHandler(fooPath, handler1);
        handlers = delegatingHandler.getHandlersForPath(fooPath);
        assertEquals(1, handlers.size());
        assertEquals(handler1, handlers.get(0));
        delegatingHandler.addHandler(fooPath, handler2);
        handlers = delegatingHandler.getHandlersForPath(fooPath);
        assertEquals(2, handlers.size());
        assertEquals(handler1, handlers.get(0));
        assertEquals(handler2, handlers.get(1));
        delegatingHandler.removeHandler(fooPath, handler1);
        handlers = delegatingHandler.getHandlersForPath(fooPath);
        assertEquals(1, handlers.size());
        assertEquals(handler2, handlers.get(0));
    }

    public void testBuildAttributeMap() {
        final String key = "testBuildAttributeMap.key";
        final String value = "testBuildAttributeMap.value";

        Mock attributeMock = new Mock(Attributes.class);
        attributeMock.expectAndReturn("getLength", new Integer(1));
        attributeMock.expectAndReturn("getQName", new Integer(0), key);
        attributeMock.expectAndReturn("getValue", new Integer(0), value);

        Attributes attributes = (Attributes) attributeMock.proxy();
        Map attrMap = delegatingHandler.buildAttributeMap(attributes);
        assertTrue(attrMap.containsKey(key));
        assertEquals(value, attrMap.get(key));

        attributeMock.verify();
    }

    public void testNestedDelegatingHandlers() {
        final Path barPath = Path.getInstance("/Bar");
        DelegatingHandler nestedDelegatingHandler = new DefaultDelegatingHandler(fooPath);
        nestedDelegatingHandler.registerWith(delegatingHandler);

        Mock subHandlerMock = new Mock(SubHandler.class);
        SubHandler subHandler = (SubHandler) subHandlerMock.proxy();
        subHandlerMock.expectAndReturn("hashCode", new Integer(234));
        subHandlerMock.expect("startingPath", C.args(C.eq(barPath), C.IS_NOT_NULL));
        subHandlerMock.expect("startElement", C.ANY_ARGS);
        nestedDelegatingHandler.addHandler(barPath, subHandler);

        try {
            delegatingHandler.startElement("", "", "Foo", null);
            delegatingHandler.startElement("", "", "Bar", null);
        } catch (SAXException e) {
            e.printStackTrace();
            fail();
        }

        subHandlerMock.verify();
    }

    public void testNestedValueStack() {
        DelegatingHandler nestedDelegatingHandler = new DefaultDelegatingHandler(fooPath);
        nestedDelegatingHandler.registerWith(delegatingHandler);

        OgnlValueStack stack = delegatingHandler.getValueStack();
        assertEquals(stack, nestedDelegatingHandler.getValueStack());
    }
}
