/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import junit.framework.TestCase;

import ognl.Ognl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class OgnlUtilTest extends TestCase {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void testCopySameType() {
        Foo foo1 = new Foo();
        Foo foo2 = new Foo();

        Map context = Ognl.createDefaultContext(foo1);

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal.set(Calendar.DAY_OF_MONTH, 12);
        cal.set(Calendar.YEAR, 1982);

        foo1.setTitle("blah");
        foo1.setNumber(1);
        foo1.setPoints(new long[] {1, 2, 3});
        foo1.setBirthday(cal.getTime());
        foo1.setUseful(false);

        OgnlUtil.copy(foo1, foo2, context);

        assertEquals(foo1.getTitle(), foo2.getTitle());
        assertEquals(foo1.getNumber(), foo2.getNumber());
        assertEquals(foo1.getPoints(), foo2.getPoints());
        assertEquals(foo1.getBirthday(), foo2.getBirthday());
        assertEquals(foo1.isUseful(), foo2.isUseful());
    }

    public void testCopyUnevenObjects() {
        Foo foo = new Foo();
        Bar bar = new Bar();

        Map context = Ognl.createDefaultContext(foo);

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal.set(Calendar.DAY_OF_MONTH, 12);
        cal.set(Calendar.YEAR, 1982);

        foo.setTitle("blah");
        foo.setNumber(1);
        foo.setPoints(new long[] {1, 2, 3});
        foo.setBirthday(cal.getTime());
        foo.setUseful(false);

        OgnlUtil.copy(foo, bar, context);

        assertEquals(foo.getTitle(), bar.getTitle());
        assertEquals(0, bar.getSomethingElse());
    }

    public void testDeepSetting() {
        Foo foo = new Foo();
        foo.setBar(new Bar());

        Map context = Ognl.createDefaultContext(foo);

        HashMap props = new HashMap();
        props.put("bar.title", "i am barbaz");
        OgnlUtil.setProperties(props, foo, context);

        assertEquals(foo.getBar().getTitle(), "i am barbaz");
    }

    public void testSetBarAsString() {
        Foo foo = new Foo();

        Map context = Ognl.createDefaultContext(foo);

        HashMap props = new HashMap();
        props.put("bar", "bar:123");

        OgnlUtil.setProperties(props, foo, context);

        assertEquals("bar", foo.getBar().getTitle());
        assertEquals(123, foo.getBar().getSomethingElse());
    }

    public void testSetPropertiesBoolean() {
        Foo foo = new Foo();

        Map context = Ognl.createDefaultContext(foo);

        HashMap props = new HashMap();
        props.put("useful", "true");
        OgnlUtil.setProperties(props, foo, context);

        assertEquals(true, foo.isUseful());
    }

    public void testSetPropertiesDate() {
        Foo foo = new Foo();

        Map context = Ognl.createDefaultContext(foo);

        HashMap props = new HashMap();
        props.put("birthday", "02/12/1982");
        OgnlUtil.setProperties(props, foo, context);

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal.set(Calendar.DAY_OF_MONTH, 12);
        cal.set(Calendar.YEAR, 1982);

        assertEquals(cal.getTime(), foo.getBirthday());
    }

    public void testSetPropertiesInt() {
        Foo foo = new Foo();

        Map context = Ognl.createDefaultContext(foo);

        HashMap props = new HashMap();
        props.put("number", "2");
        OgnlUtil.setProperties(props, foo, context);

        assertEquals(foo.getNumber(), 2);
    }

    public void testSetPropertiesLongArray() {
        Foo foo = new Foo();

        Map context = Ognl.createDefaultContext(foo);

        HashMap props = new HashMap();
        props.put("points", new String[] {"1", "2"});
        OgnlUtil.setProperties(props, foo, context);

        assertNotNull(foo.getPoints());
        assertEquals(foo.getPoints().length, 2);
        assertEquals(foo.getPoints()[0], 1);
        assertEquals(foo.getPoints()[1], 2);
    }

    public void testSetPropertiesString() {
        Foo foo = new Foo();

        Map context = Ognl.createDefaultContext(foo);

        HashMap props = new HashMap();
        props.put("title", "this is a title");
        OgnlUtil.setProperties(props, foo, context);

        assertEquals(foo.getTitle(), "this is a title");
    }

    public void testSetProperty() {
        Foo foo = new Foo();
        Map context = Ognl.createDefaultContext(foo);
        assertFalse(123456 == foo.getNumber());
        OgnlUtil.setProperty("number", "123456", foo, context);
        assertTrue(123456 == foo.getNumber());
    }

    public void testStringToLong() {
        Foo foo = new Foo();

        Map context = Ognl.createDefaultContext(foo);

        HashMap props = new HashMap();
        props.put("aLong", "123");

        OgnlUtil.setProperties(props, foo, context);
        assertEquals(123, foo.getaLong());

        props.put("aLong", new String[] {"123"});

        foo.setaLong(0);
        OgnlUtil.setProperties(props, foo, context);
        assertEquals(123, foo.getaLong());
    }
}
