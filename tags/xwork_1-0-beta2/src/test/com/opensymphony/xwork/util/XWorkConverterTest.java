/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ModelDrivenAction;
import com.opensymphony.xwork.SimpleAction;
import com.opensymphony.xwork.TestBean;
import com.opensymphony.xwork.config.ConfigurationManager;

import junit.framework.TestCase;

import ognl.Ognl;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.*;


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

    public void testDateConversion() throws ParseException {
        java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
        assertEquals(sqlDate, converter.convertValue(context, null, null, null, sqlDate, Date.class));

        SimpleDateFormat format = new SimpleDateFormat("mm/dd/yyyy hh:mm:ss");
        Date date = format.parse("01/10/2001 00:00:00");
        String dateStr = (String) converter.convertValue(context, null, null, null, date, String.class);
        Date date2 = (Date) converter.convertValue(context, null, null, null, dateStr, Date.class);
        assertEquals(date, date2);
    }

    public void testFieldErrorMessageAddedForComplexProperty() {
        SimpleAction action = new SimpleAction();
        action.setBean(new TestBean());

        OgnlValueStack stack = new OgnlValueStack();
        stack.push(action);

        Map context = stack.getContext();
        context.put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        context.put(XWorkConverter.CONVERSION_PROPERTY_FULLNAME, "bean.birth");
        assertEquals("Conversion should have failed.", null, converter.convertValue(context, action.getBean(), null, "birth", new String[] {
                    "invalid date"
                }, Date.class));
        stack.pop();
        assertTrue(action.hasFieldErrors());
        assertNotNull(action.getFieldErrors().get("bean.birth"));
        assertEquals("Invalid field value for field \"bean.birth\".", ((List) action.getFieldErrors().get("bean.birth")).get(0));
    }

    public void testFieldErrorMessageAddedWhenConversionFails() {
        SimpleAction action = new SimpleAction();
        action.setDate(null);

        OgnlValueStack stack = new OgnlValueStack();
        stack.push(action);

        Map context = stack.getContext();
        context.put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        assertEquals("Conversion should have failed.", null, converter.convertValue(context, action, null, "date", new String[] {
                    "invalid date"
                }, Date.class));
        stack.pop();
        assertTrue(action.hasFieldErrors());
        assertNotNull(action.getFieldErrors().get("date"));
        assertEquals("Invalid field value for field \"date\".", ((List) action.getFieldErrors().get("date")).get(0));
    }

    public void testFieldErrorMessageAddedWhenConversionFailsOnModelDriven() {
        ModelDrivenAction action = new ModelDrivenAction();
        OgnlValueStack stack = new OgnlValueStack();
        stack.push(action);
        stack.push(action.getModel());

        Map context = stack.getContext();
        context.put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        assertEquals("Conversion should have failed.", null, converter.convertValue(context, action, null, "birth", new String[] {
                    "invalid date"
                }, Date.class));
        stack.pop();
        stack.pop();
        assertTrue(action.hasFieldErrors());
        assertNotNull(action.getFieldErrors().get("birth"));
        assertEquals("Invalid date for birth.", ((List) action.getFieldErrors().get("birth")).get(0));
    }

    public void testStringArrayToCollection() {
        ArrayList list = new ArrayList();
        list.add("foo");
        list.add("bar");
        list.add("baz");
        assertEquals(list, converter.convertValue(context, null, null, null, new String[] {
                    "foo", "bar", "baz"
                }, Collection.class));
    }

    public void testStringArrayToList() {
        ArrayList list = new ArrayList();
        list.add("foo");
        list.add("bar");
        list.add("baz");
        assertEquals(list, converter.convertValue(context, null, null, null, new String[] {
                    "foo", "bar", "baz"
                }, List.class));
    }

    public void testStringArrayToPrimitiveWrappers() {
        Long[] longs = (Long[]) converter.convertValue(context, null, null, null, new String[] {
                "123", "456"
            }, Long[].class);
        assertNotNull(longs);
        assertTrue(Arrays.equals(new Long[] {new Long(123), new Long(456)}, longs));

        Integer[] ints = (Integer[]) converter.convertValue(context, null, null, null, new String[] {
                "123", "456"
            }, Integer[].class);
        assertNotNull(ints);
        assertTrue(Arrays.equals(new Integer[] {
                    new Integer(123), new Integer(456)
                }, ints));

        Double[] doubles = (Double[]) converter.convertValue(context, null, null, null, new String[] {
                "123", "456"
            }, Double[].class);
        assertNotNull(doubles);
        assertTrue(Arrays.equals(new Double[] {new Double(123), new Double(456)}, doubles));

        Float[] floats = (Float[]) converter.convertValue(context, null, null, null, new String[] {
                "123", "456"
            }, Float[].class);
        assertNotNull(floats);
        assertTrue(Arrays.equals(new Float[] {new Float(123), new Float(456)}, floats));

        Boolean[] booleans = (Boolean[]) converter.convertValue(context, null, null, null, new String[] {
                "true", "false"
            }, Boolean[].class);
        assertNotNull(booleans);
        assertTrue(Arrays.equals(new Boolean[] {Boolean.TRUE, Boolean.FALSE}, booleans));
    }

    public void testStringArrayToPrimitives() throws OgnlException {
        long[] longs = (long[]) converter.convertValue(context, null, null, null, new String[] {
                "123", "456"
            }, long[].class);
        assertNotNull(longs);
        assertTrue(Arrays.equals(new long[] {123, 456}, longs));

        int[] ints = (int[]) converter.convertValue(context, null, null, null, new String[] {
                "123", "456"
            }, int[].class);
        assertNotNull(ints);
        assertTrue(Arrays.equals(new int[] {123, 456}, ints));

        double[] doubles = (double[]) converter.convertValue(context, null, null, null, new String[] {
                "123", "456"
            }, double[].class);
        assertNotNull(doubles);
        assertTrue(Arrays.equals(new double[] {123, 456}, doubles));

        float[] floats = (float[]) converter.convertValue(context, null, null, null, new String[] {
                "123", "456"
            }, float[].class);
        assertNotNull(floats);
        assertTrue(Arrays.equals(new float[] {123, 456}, floats));

        boolean[] booleans = (boolean[]) converter.convertValue(context, null, null, null, new String[] {
                "true", "false"
            }, boolean[].class);
        assertNotNull(booleans);
        assertTrue(Arrays.equals(new boolean[] {true, false}, booleans));
    }

    public void testStringArrayToSet() {
        HashSet list = new HashSet();
        list.add("foo");
        list.add("bar");
        list.add("baz");
        assertEquals(list, converter.convertValue(context, null, null, null, new String[] {
                    "foo", "bar", "bar", "baz"
                }, Set.class));
    }

    public void testStringToCustomTypeUsingCustomConverter() {
        // the converter needs to be registered as the Bar.class converter 
        // it won't be detected from the Foo-conversion.properties
        // because the Foo-conversion.properties file is only used when converting a property of Foo
        converter.registerConverter(Bar.class.getName(), new FooBarConverter());

        Bar bar = null;

        bar = (Bar) converter.convertValue(null, null, null, null, "blah:123", Bar.class);
        assertNotNull("conversion failed", bar);
        assertEquals(123, bar.getSomethingElse());
        assertEquals("blah", bar.getTitle());
    }

    public void testStringToPrimitiveWrappers() {
        assertEquals(new Long(123), converter.convertValue(context, null, null, null, "123", Long.class));
        assertEquals(new Integer(123), converter.convertValue(context, null, null, null, "123", Integer.class));
        assertEquals(new Double(123.5), converter.convertValue(context, null, null, null, "123.5", Double.class));
        assertEquals(new Float(123.5), converter.convertValue(context, null, null, null, "123.5", float.class));
        assertEquals(new Boolean(false), converter.convertValue(context, null, null, null, "false", Boolean.class));
        assertEquals(new Boolean(true), converter.convertValue(context, null, null, null, "true", Boolean.class));
    }

    public void testStringToPrimitives() {
        assertEquals(new Long(123), converter.convertValue(context, null, null, null, "123", long.class));
        assertEquals(new Integer(123), converter.convertValue(context, null, null, null, "123", int.class));
        assertEquals(new Double(123.5), converter.convertValue(context, null, null, null, "123.5", double.class));
        assertEquals(new Float(123.5), converter.convertValue(context, null, null, null, "123.5", float.class));
        assertEquals(new Boolean(false), converter.convertValue(context, null, null, null, "false", boolean.class));
        assertEquals(new Boolean(true), converter.convertValue(context, null, null, null, "true", boolean.class));
        assertEquals(new BigDecimal(123.5), converter.convertValue(context, null, null, null, "123.5", BigDecimal.class));
        assertEquals(new BigInteger("123"), converter.convertValue(context, null, null, null, "123", BigInteger.class));
    }

    protected void setUp() throws Exception {
        converter = XWorkConverter.getInstance();
        ConfigurationManager.destroyConfiguration();

        OgnlValueStack stack = new OgnlValueStack();
        ActionContext ac = new ActionContext(stack.getContext());
        ac.setLocale(Locale.US);
        ActionContext.setContext(ac);
    }

    protected void tearDown() throws Exception {
        XWorkConverter.resetInstance();
        ActionContext.setContext(null);
    }
}
