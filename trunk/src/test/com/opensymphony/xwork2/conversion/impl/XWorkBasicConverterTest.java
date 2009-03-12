/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.XWorkException;
import junit.framework.TestCase;

import java.util.Date;
import java.util.HashMap;

/**
 * Test case for XWorkBasicConverter
 *
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class XWorkBasicConverterTest extends TestCase {

    // TODO: test for every possible conversion
    // take into account of empty string
    // primitive -> conversion error when empty string is passed
    // object -> return null when empty string is passed

    public void testDateConversionWithEmptyValue() {
        XWorkBasicConverter basicConverter = new XWorkBasicConverter();
        Object convertedObject = basicConverter.convertValue(new HashMap<String, Object>(), null, null, null, "", Date.class);
        // we must not get XWorkException as that will caused a conversion error
        assertNull(convertedObject);
    }

    public void testDateConversionWithInvalidValue() throws Exception {
        XWorkBasicConverter basicConverter = new XWorkBasicConverter();
        try {
            Object convertedObject = basicConverter.convertValue(new HashMap<String, Object>(), null, null, null, "asdsd", Date.class);
            fail("XWorkException expected - conversion error occurred");
        } catch (XWorkException e) {
            // we MUST get this exception as this is a conversion error
        }
    }

    public void testEmptyArrayConversion() throws Exception {
        XWorkBasicConverter basicConverter = new XWorkBasicConverter();
        Object convertedObject = basicConverter.convertValue(new HashMap<String, Object>(), null, null, null, new Object[]{}, Object[].class);
        // we must not get XWorkException as that will caused a conversion error
        assertEquals(Object[].class, convertedObject.getClass());
        Object[] obj = (Object[]) convertedObject;
        assertEquals(0, obj.length);
    }

    public void testNullArrayConversion() throws Exception {
        XWorkBasicConverter basicConverter = new XWorkBasicConverter();
        Object convertedObject = basicConverter.convertValue(new HashMap<String, Object>(), null, null, null, null, Object[].class);
        // we must not get XWorkException as that will caused a conversion error
        assertNull(convertedObject);
    }

    /* the code below has been disabled as it causes sideffects in Strtus2 (XW-512)
    public void testXW490ConvertStringToDouble() throws Exception {
        Locale locale = new Locale("DA"); // let's use a not common locale such as Denmark

        Map ctx = new HashMap();
        ctx.put(ActionContext.LOCALE, locale);

        XWorkBasicConverter conv = new XWorkBasicConverter();
        // decimal seperator is , in Denmark so we should write 123,99 as input
        Double value = (Double) conv.convertValue(ctx, null, null, null, "123,99", Double.class);
        assertNotNull(value);

        // output is as expected a real double value converted using Denmark as locale
        assertEquals(123.99d, value.doubleValue(), 0.001d);
    }

    public void testXW49ConvertDoubleToString() throws Exception {
        Locale locale = new Locale("DA"); // let's use a not common locale such as Denmark

        Map ctx = new HashMap();
        ctx.put(ActionContext.LOCALE, locale);

        XWorkBasicConverter conv = new XWorkBasicConverter();
        // decimal seperator is , in Denmark so we should write 123,99 as input
        String value = (String) conv.convertValue(ctx, null, null, null, new Double("123.99"), String.class);
        assertNotNull(value);

        // output should be formatted according to Danish locale using , as decimal seperator
        assertEquals("123,99", value);
    }    
    */

    public void testDoubleValues() {
        XWorkBasicConverter basicConverter = new XWorkBasicConverter();

        assertTrue(basicConverter.isInRange(-1.2, "-1.2", Double.class));
        assertTrue(basicConverter.isInRange(1.5, "1.5", Double.class));

        Object value = basicConverter.convertValue("-1.3", double.class);
        assertNotNull(value);
        assertEquals(-1.3, value);

        value = basicConverter.convertValue("1.8", double.class);
        assertNotNull(value);
        assertEquals(1.8, value);

        value = basicConverter.convertValue("-1.9", double.class);
        assertNotNull(value);
        assertEquals(-1.9, value);

        value = basicConverter.convertValue("1.7", Double.class);
        assertNotNull(value);
        assertEquals(1.7, value);

        value = basicConverter.convertValue("0.0", Double.class);
        assertNotNull(value);
        assertEquals(0.0, value);

        value = basicConverter.convertValue("0.0", double.class);
        assertNotNull(value);
        assertEquals(0.0, value);
    }

    public void testFloatValues() {
        XWorkBasicConverter basicConverter = new XWorkBasicConverter();

        assertTrue(basicConverter.isInRange(-1.65, "-1.65", Float.class));
        assertTrue(basicConverter.isInRange(1.9876, "1.9876", float.class));

        Float value = (Float) basicConverter.convertValue("-1.444401", Float.class);
        assertNotNull(value);
        assertEquals(Float.valueOf("-1.444401"), value);

        value = (Float) basicConverter.convertValue("1.46464989", Float.class);
        assertNotNull(value);
        assertEquals(Float.valueOf(1.46464989f), value);
    }

    public void testNegativeFloatValue() throws Exception {
        XWorkBasicConverter basicConverter = new XWorkBasicConverter();
        Object convertedObject = basicConverter.convertValue("-94.1231233", Float.class);
        assertTrue(convertedObject instanceof Float);
        assertEquals(-94.1231233f, ((Float) convertedObject).floatValue(), 0.0001);
    }

    public void testPositiveFloatValue() throws Exception {
        XWorkBasicConverter basicConverter = new XWorkBasicConverter();
        Object convertedObject = basicConverter.convertValue("94.1231233", Float.class);
        assertTrue(convertedObject instanceof Float);
        assertEquals(94.1231233f, ((Float) convertedObject).floatValue(), 0.0001);
    }


    public void testNegativeDoubleValue() throws Exception {
        XWorkBasicConverter basicConverter = new XWorkBasicConverter();
        Object convertedObject = basicConverter.convertValue("-94.1231233", Double.class);
        assertTrue(convertedObject instanceof Double);
        assertEquals(-94.1231233d, ((Double) convertedObject).doubleValue(), 0.0001);
    }

    public void testPositiveDoubleValue() throws Exception {
        XWorkBasicConverter basicConverter = new XWorkBasicConverter();
        Object convertedObject = basicConverter.convertValue("94.1231233", Double.class);
        assertTrue(convertedObject instanceof Double);
        assertEquals(94.1231233d, ((Double) convertedObject).doubleValue(), 0.0001);
    }

}
