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
import com.opensymphony.xwork.xml.handlers.SetPropertyHandler;

import junit.framework.TestCase;


/**
 * SetPropertyHandlerTest
 * @author Jason Carreira
 * Created May 18, 2003 12:25:05 PM
 */
public class SetPropertyHandlerTest extends TestCase {
    //~ Instance fields ////////////////////////////////////////////////////////

    DelegatingHandler delegatingHandler;
    Mock delegatingHandlerMock;
    OgnlValueStack stack;
    Path fooPath = Path.getInstance("/Foo");

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setUp() {
        delegatingHandlerMock = new Mock(DelegatingHandler.class);
        delegatingHandler = (DelegatingHandler) delegatingHandlerMock.proxy();
        stack = new OgnlValueStack();
        delegatingHandlerMock.expectAndReturn("getValueStack", stack);
    }

    public void testAddsErrorOnEmptyStack() {
        final String propertyName = "name";
        SetPropertyHandler handler = buildHandler(propertyName);
        delegatingHandlerMock.expect("addError", C.ANY_ARGS);
        handler.handleContent("foo");
        delegatingHandlerMock.verify();
    }

    public void testSetInt() {
        SetPropertyHandler handler = buildHandler("count");
        stack.push(new TestBean());

        final String content = "12";
        handler.handleContent(content);
        assertEquals(12, ((TestBean) stack.peek()).getCount());
        delegatingHandlerMock.verify();
    }

    public void testSetString() {
        SetPropertyHandler handler = buildHandler("name");
        stack.push(new TestBean());

        final String content = "testSetString.name";
        handler.handleContent(content);
        assertEquals(content, ((TestBean) stack.peek()).getName());
        delegatingHandlerMock.verify();
    }

    private SetPropertyHandler buildHandler(String propertyName) {
        SetPropertyHandler handler = new SetPropertyHandler(fooPath, propertyName);
        delegatingHandlerMock.expect("addHandler", C.args(C.eq(fooPath), C.eq(handler)));
        handler.registerWith(delegatingHandler);

        return handler;
    }
}
