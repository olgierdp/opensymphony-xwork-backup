/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.xml.handlers;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;

import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.xml.DelegatingHandler;
import com.opensymphony.xwork.xml.Path;
import com.opensymphony.xwork.xml.handlers.ObjectCreateHandler;

import junit.framework.TestCase;

import org.xml.sax.Attributes;

import java.util.HashMap;
import java.util.Map;


/**
 * ObjectCreateHandlerTest
 * @author Jason Carreira
 * Created May 17, 2003 1:11:05 PM
 */
public class ObjectCreateHandlerTest extends TestCase {
    //~ Instance fields ////////////////////////////////////////////////////////

    DelegatingHandler delegatingHandler;
    Mock delegatingHandlerMock;
    ObjectCreateHandler handler;
    OgnlValueStack stack;
    Path fooPath = Path.getInstance("/Foo");

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setUp() {
        delegatingHandlerMock = new Mock(DelegatingHandler.class);
        delegatingHandler = (DelegatingHandler) delegatingHandlerMock.proxy();
        handler = new ObjectCreateHandler(fooPath, TestBean.class);
        delegatingHandlerMock.expect("addHandler", C.args(C.eq(fooPath), C.eq(handler)));
        handler.registerWith(delegatingHandler);
        stack = new OgnlValueStack();
        delegatingHandlerMock.expectAndReturn("getValueStack", stack);
    }

    public void testAttributeMapping() {
        final String key = "attributeKey";
        final String propertyKey = "name";
        final String value = "testCreateHandler.name";

        Map attributeMap = new HashMap();
        attributeMap.put(key, value);
        attributeMap.put("foo", "bar");

        handler.addAttributeMapping(key, propertyKey);

        Map propertyMap = handler.buildPropertyMap(attributeMap);
        assertTrue(propertyMap.containsKey("foo"));
        assertTrue(propertyMap.containsKey(propertyKey));
        assertEquals("bar", propertyMap.get("foo"));
        assertEquals(value, propertyMap.get(propertyKey));
    }

    public void testCreateHandler() {
        final String key = "name";
        final String value = "testCreateHandler.name";

        TestBean expected = new TestBean();
        expected.setName(value);

        Map attributeMap = new HashMap();
        attributeMap.put(key, value);

        handler.startingPath(fooPath, attributeMap);

        assertEquals(1, stack.size());
        assertEquals(expected, stack.peek());

        delegatingHandlerMock.verify();
    }
}
