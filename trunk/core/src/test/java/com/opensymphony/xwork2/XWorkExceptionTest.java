/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.location.Location;

public class XWorkExceptionTest extends XWorkTestCase {

    public void testUnknown() throws Exception {
        XWorkException e = new XWorkException("testXXX", this);
        assertEquals(Location.UNKNOWN, e.getLocation());
    }

    public void testThrowable() {
        XWorkException e = new XWorkException("testThrowable", new IllegalArgumentException("Arg is null"));
        assertEquals("com/opensymphony/xwork2/XWorkExceptionTest.java", e.getLocation().getURI());
        String s = e.getLocation().toString();
        assertTrue(s.contains("Method: testThrowable"));
    }

    public void testCauseAndTarget() {
        XWorkException e = new XWorkException(new IllegalArgumentException("Arg is null"), this);
        assertEquals("com/opensymphony/xwork2/XWorkExceptionTest.java", e.getLocation().getURI());
        String s = e.getLocation().toString();
        assertTrue(s.contains("Method: testCauseAndTarget"));
    }

    public void testDefaultConstructor() {
        XWorkException e = new XWorkException();

        assertNull(e.getCause());
        assertNull(e.getThrowable());
        assertNull(e.getMessage());
        assertNull(e.getLocation());

        assertNull(e.toString()); // mo message so it returns null
    }

    public void testMessageOnly() {
        XWorkException e = new XWorkException("Hello World");

        assertNull(e.getCause());
        assertEquals("Hello World", e.getMessage());
        assertEquals(Location.UNKNOWN, e.getLocation());
    }

    public void testCauseOnly() {
        XWorkException e = new XWorkException(new IllegalArgumentException("Arg is null"));

        assertNotNull(e.getCause());
        assertNotNull(e.getLocation());
        assertEquals("com/opensymphony/xwork2/XWorkExceptionTest.java", e.getLocation().getURI());
        String s = e.getLocation().toString();
        assertTrue(s.contains("Method: testCauseOnly"));
        assertTrue(e.toString().contains("Arg is null"));
    }

    public void testCauseOnlyNoMessage() {
        XWorkException e = new XWorkException(new IllegalArgumentException());

        assertNotNull(e.getCause());
        assertNotNull(e.getLocation());
        assertEquals("com/opensymphony/xwork2/XWorkExceptionTest.java", e.getLocation().getURI());
        String s = e.getLocation().toString();
        assertTrue(s.contains("Method: testCauseOnly"));
        assertTrue(e.toString().contains("Method: testCauseOnly"));
    }

}
