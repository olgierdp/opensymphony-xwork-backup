/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created on 6/10/2003
 *
 */
package com.opensymphony.xwork.util;

import junit.framework.TestCase;

import ognl.Ognl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


/**
 * @author CameronBraid
 *
 */
public class SetPropertiesTest extends TestCase {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void testOgnlUtilEmptyStringAsLong() {
        Bar bar = new Bar();
        Map context = Ognl.createDefaultContext(bar);
        context.put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        bar.setId(null);

        HashMap props = new HashMap();
        props.put("id", "");

        OgnlUtil.setProperties(props, bar, context);
        assertNull(bar.getId());
        assertEquals(0, bar.getFieldErrors().size());

        props.put("id", new String[] {""});

        bar.setId(null);
        OgnlUtil.setProperties(props, bar, context);
        assertNull(bar.getId());
        assertEquals(0, bar.getFieldErrors().size());
    }

    public void testSetCollectionByConverterFromArray() {
        Foo foo = new Foo();
        OgnlValueStack vs = new OgnlValueStack();
        vs.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);

        XWorkConverter c = (XWorkConverter) Ognl.getTypeConverter(vs.getContext());
        c.registerConverter(Cat.class.getName(), new FooBarConverter());
        vs.push(foo);

        vs.setValue("cats", new String[] {"1", "2"});
        assertNotNull(foo.getCats());
        assertEquals(2, foo.getCats().size());
        assertEquals(Cat.class, foo.getCats().get(0).getClass());
        assertEquals(Cat.class, foo.getCats().get(1).getClass());
    }

    public void testSetCollectionByConverterFromCollection() {
        Foo foo = new Foo();
        OgnlValueStack vs = new OgnlValueStack();
        vs.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);

        XWorkConverter c = (XWorkConverter) Ognl.getTypeConverter(vs.getContext());
        c.registerConverter(Cat.class.getName(), new FooBarConverter());
        vs.push(foo);

        HashSet s = new HashSet();
        s.add("1");
        s.add("2");
        vs.setValue("cats", s);
        assertNotNull(foo.getCats());
        assertEquals(2, foo.getCats().size());
        assertEquals(Cat.class, foo.getCats().get(0).getClass());
        assertEquals(Cat.class, foo.getCats().get(1).getClass());
    }

    public void testValueStackSetValueEmptyStringAsLong() {
        Bar bar = new Bar();
        OgnlValueStack vs = new OgnlValueStack();
        vs.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        vs.push(bar);

        vs.setValue("id", "");
        assertNull(bar.getId());
        assertEquals(0, bar.getFieldErrors().size());

        bar.setId(null);

        vs.setValue("id", new String[] {""});
        assertNull(bar.getId());
        assertEquals(0, bar.getFieldErrors().size());
    }
}
