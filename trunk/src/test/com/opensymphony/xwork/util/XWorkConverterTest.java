/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import junit.framework.TestCase;

import ognl.Ognl;
import ognl.OgnlException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 *
 *
 * @author $Author$
 * @version $Revision$
 */
public class XWorkConverterTest extends TestCase {
    //~ Instance fields ////////////////////////////////////////////////////////

    Map context = Ognl.createDefaultContext(this);
    XWorkConverter converter;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void testStringArrayToCollection() {
        ArrayList list = new ArrayList();
        list.add("foo");
        list.add("bar");
        list.add("baz");
        assertEquals(list, converter.convertValue(context, new String[]{
            "foo", "bar", "baz"
        }, null, null, null, Collection.class));
    }

    public void testStringArrayToList() {
        ArrayList list = new ArrayList();
        list.add("foo");
        list.add("bar");
        list.add("baz");
        assertEquals(list, converter.convertValue(context, new String[]{
            "foo", "bar", "baz"
        }, null, null, null, List.class));
    }

    public void testStringArrayToPrimitiveWrappers() {
        Long[] longs = (Long[]) converter.convertValue(context, new String[]{
            "123", "456"
        }, null, null, null, Long[].class);
        assertNotNull(longs);
        assertTrue(Arrays.equals(new Long[]{new Long(123), new Long(456)}, longs));

        Integer[] ints = (Integer[]) converter.convertValue(context, new String[]{
            "123", "456"
        }, null, null, null, Integer[].class);
        assertNotNull(ints);
        assertTrue(Arrays.equals(new Integer[]{
            new Integer(123), new Integer(456)
        }, ints));

        Double[] doubles = (Double[]) converter.convertValue(context, new String[]{
            "123", "456"
        }, null, null, null, Double[].class);
        assertNotNull(doubles);
        assertTrue(Arrays.equals(new Double[]{new Double(123), new Double(456)}, doubles));
    }

    public void testStringArrayToPrimitives() throws OgnlException {
        long[] longs = (long[]) converter.convertValue(context, new String[]{
            "123", "456"
        }, null, null, null, long[].class);
        assertNotNull(longs);
        assertTrue(Arrays.equals(new long[]{123, 456}, longs));

        int[] ints = (int[]) converter.convertValue(context, new String[]{
            "123", "456"
        }, null, null, null, int[].class);
        assertNotNull(ints);
        assertTrue(Arrays.equals(new int[]{123, 456}, ints));

        double[] doubles = (double[]) converter.convertValue(context, new String[]{
            "123", "456"
        }, null, null, null, double[].class);
        assertNotNull(doubles);
        assertTrue(Arrays.equals(new double[]{123, 456}, doubles));
    }

    public void testStringToPrimitiveWrappers() {
        assertEquals(new Long(123), converter.convertValue(context, "123", null, null, null, Long.class));
        assertEquals(new Integer(123), converter.convertValue(context, "123", null, null, null, Integer.class));
        assertEquals(new Double(123.5), converter.convertValue(context, "123.5", null, null, null, Double.class));
    }

    public void testStringToPrimitives() {
        assertEquals(new Long(123), converter.convertValue(context, "123", null, null, null, long.class));
        assertEquals(new Integer(123), converter.convertValue(context, "123", null, null, null, int.class));
        assertEquals(new Double(123.5), converter.convertValue(context, "123.5", null, null, null, double.class));
    }

    protected void setUp() throws Exception {
        converter = XWorkConverter.getInstance();
    }

    protected void tearDown() throws Exception {
        XWorkConverter.resetInstance();
    }
}
