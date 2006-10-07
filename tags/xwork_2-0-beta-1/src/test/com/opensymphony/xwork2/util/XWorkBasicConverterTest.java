/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import java.util.Date;
import java.util.HashMap;

import com.opensymphony.xwork2.XWorkException;

import junit.framework.TestCase;

/**
 * Test case for XWorkBasicConverter
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class XWorkBasicConverterTest extends TestCase {

	public void test() {
		// TODO: test for every possible conversion
		// take into account of empty string
		// primitive -> conversion error when empty string is passed
		// object -> return null when empty string is passed
	}
	
	
	// TODO: more test will come soon !!!
	
	// TEST DATE CONVERSION:-
	public void testDateConversionWithEmptyValue() {
		XWorkBasicConverter basicConverter = new XWorkBasicConverter();
		Object convertedObject = basicConverter.convertValue(new HashMap(), null, null, null, "", Date.class);
		// we must not get XWorkException as that will caused a conversion error
		assertNull(convertedObject); 
	}
	
	public void testDateConversionWithInvalidValue() {
		XWorkBasicConverter basicConverter = new XWorkBasicConverter();
		try {
			Object convertedObject = basicConverter.convertValue(new HashMap(), null, null, null, "asdsd", Date.class);
		}
		catch(XWorkException e) {
			// we MUST get this exception as this is a conversion error
			assertTrue(true);
			return;
		}
		fail("XWorkException expected - conversion error occurred");
	}
}
