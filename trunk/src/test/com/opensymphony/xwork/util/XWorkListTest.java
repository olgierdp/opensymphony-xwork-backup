/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.test.User;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Map;


/**
 * Test cases for {@link XWorkList}.
 *
 * @author Mark Woon
 */
public class XWorkListTest extends TestCase {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void testAddAllIndex() {
        XWorkList xworkList = new XWorkList(String.class);
        xworkList.add(new String[] {"a"});
        xworkList.add("b");

        ArrayList addList = new ArrayList();
        addList.add(new String[] {"1"});
        addList.add(new String[] {"2"});
        addList.add(new String[] {"3"});

        // trim
        xworkList.addAll(3, addList);
        assertEquals(6, xworkList.size());
        assertEquals("a", xworkList.get(0));
        assertEquals("b", xworkList.get(1));
        assertEquals("", xworkList.get(2));
        assertEquals("1", xworkList.get(3));
        assertEquals("2", xworkList.get(4));
        assertEquals("3", xworkList.get(5));

        // take 2, no trim
        xworkList = new XWorkList(String.class);
        xworkList.add(new String[] {"a"});
        xworkList.add("b");

        addList = new ArrayList();
        addList.add(new String[] {"1"});
        addList.add(new String[] {"2"});
        addList.add(new String[] {"3"});

        xworkList.addAll(2, addList);
        assertEquals(5, xworkList.size());
        assertEquals("a", xworkList.get(0));
        assertEquals("b", xworkList.get(1));
        assertEquals("1", xworkList.get(2));
        assertEquals("2", xworkList.get(3));
        assertEquals("3", xworkList.get(4));

        // take 3, insert
        xworkList = new XWorkList(String.class);
        xworkList.add(new String[] {"a"});
        xworkList.add("b");

        addList = new ArrayList();
        addList.add(new String[] {"1"});
        addList.add(new String[] {"2"});
        addList.add(new String[] {"3"});

        xworkList.addAll(1, addList);
        assertEquals(5, xworkList.size());
        assertEquals("a", xworkList.get(0));
        assertEquals("1", xworkList.get(1));
        assertEquals("2", xworkList.get(2));
        assertEquals("3", xworkList.get(3));
        assertEquals("b", xworkList.get(4));
    }

    public void testSetValue() {
        OgnlValueStack stack = new OgnlValueStack();
        Map stackContext = stack.getContext();
        stackContext.put(InstantiatingNullHandler.CREATE_NULL_OBJECTS, Boolean.TRUE);
        stackContext.put(XWorkMethodAccessor.DENY_METHOD_EXECUTION, Boolean.TRUE);
        stackContext.put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);

        User user = new User();
        stack.push(user);

        // indexed string
        stack.setValue("list[1]", "asdf");
        assertNotNull(user.getList());
        assertEquals(2, user.getList().size());
        assertEquals(String.class, user.getList().get(0).getClass());
        assertEquals("asdf", user.getList().get(1));
    }
}
