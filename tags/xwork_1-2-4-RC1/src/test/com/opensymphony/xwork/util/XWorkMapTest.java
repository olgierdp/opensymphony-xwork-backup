/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.test.User;
import junit.framework.TestCase;

import java.util.Map;


/**
 * @author Mark Woon
 */
public class XWorkMapTest extends TestCase {

    public void testPutValue() {
        OgnlValueStack stack = new OgnlValueStack();
        Map stackContext = stack.getContext();
        stackContext.put(InstantiatingNullHandler.CREATE_NULL_OBJECTS, Boolean.TRUE);
        stackContext.put(XWorkMethodAccessor.DENY_METHOD_EXECUTION, Boolean.TRUE);
        stackContext.put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);

        User user = new User();
        stack.push(user);

        // indexed string
        stack.setValue("map['foo']", "asdf");
        assertNotNull(user.getMap());
        assertEquals(1, user.getMap().size());
        assertEquals(String.class, user.getMap().get("foo").getClass());
        assertEquals("asdf", user.getMap().get("foo"));

        // type conversion
        stack.setValue("map['bar']", new String[]{"qwerty","qwertz"});
        assertNotNull(user.getMap());
        assertEquals(2, user.getMap().size());
        assertEquals(String.class, user.getMap().get("bar").getClass());
        assertEquals("qwerty, qwertz", user.getMap().get("bar"));
    }

    public void testTypeConversion() {
        XWorkMap map = new XWorkMap(String.class);
        String[] test = new String[]{"qwerty"};
        map.put("foo", test);
        assertEquals(1, map.size());
        assertEquals("qwerty", map.get("foo"));
    }
}
