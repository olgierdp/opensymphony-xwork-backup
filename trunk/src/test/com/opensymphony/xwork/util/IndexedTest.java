package com.opensymphony.xwork.util;

import junit.framework.TestCase;

/**
 * @author mimo
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IndexedTest extends TestCase {




    public void testSettingSimpleIndexedProperties() {
        Indexed i = new Indexed();
        Integer one = new Integer(1);
        OgnlValueStack stack = new OgnlValueStack();
        stack.context.put( XWorkMethodAccessor.DENY_METHOD_EXECUTION, Boolean.TRUE);
        stack.push(i);
        stack.setValue("simple[0]", "0");
        stack.setValue("simple[1]", one);
        stack.setValue("top.simple[2]", "2");
        assertEquals("0",i.getSimple(0));
        assertEquals(one,i.getSimple(1));
        assertEquals("2",i.getSimple(2));
        assertEquals("0",stack.findValue("top.simple[0]"));
        assertEquals(one,stack.findValue("simple[1]"));
        assertEquals("2", stack.findValue("top.simple[2]"));

        assertEquals(stack.findValue("simple[2]"), "2");

    }

    public void testSettingObjectIndexedProperties() {
        Indexed i = new Indexed();
        Integer one = new Integer(1);
        OgnlValueStack stack = new OgnlValueStack();
        stack.context.put( XWorkMethodAccessor.DENY_METHOD_EXECUTION, Boolean.TRUE);
        stack.push(i);
        stack.setValue("integerMap['0']", "0");
        stack.setValue("integerMap['1']", one);
        stack.setValue("stringMap['2']", "two");
        assertEquals(i.getIntegerMap("0"), new Integer(0));
        assertEquals(i.getIntegerMap("1"), one);
        assertEquals(i.getStringMap("2"), "two");
        assertEquals(stack.findValue("integerMap['0']"), new Integer(0));
        assertEquals(stack.findValue("integerMap['1']"), one);
        assertEquals(stack.findValue("top.stringMap['2']"), "two");

        assertEquals(stack.findValue("stringMap['2']"), "two");

    }


}