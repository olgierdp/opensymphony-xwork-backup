/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import junit.framework.TestCase;

import java.util.ArrayList;
import org.w3c.dom.*;
import org.xml.sax.*;
import java.io.*;
import com.opensymphony.xwork.util.location.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;


/**
 * Test cases for {@link DomHelper}.
 */
public class DomHelperTest extends TestCase {

    private String xml = "<!DOCTYPE foo [\n" +
                         "<!ELEMENT foo (bar)>\n" +
                         "<!ELEMENT bar (#PCDATA)>\n" +
                         "]>\n" +
                         "<foo>\n" +
                         " <bar/>\n" +
                         "</foo>\n";
    
    public void testParse() throws Exception {
        InputSource in = new InputSource(new StringReader(xml));
        in.setSystemId("foo://bar");
        
        Document doc = DomHelper.parse(in);
        assertNotNull(doc);
        assertTrue("Wrong root node",
            "foo".equals(doc.getDocumentElement().getNodeName()));
        
        NodeList nl = doc.getElementsByTagName("bar");
        assertTrue(nl.getLength() == 1);
        
        
        
    }
    
    public void testGetLocationObject() throws Exception {
        InputSource in = new InputSource(new StringReader(xml));
        in.setSystemId("foo://bar");
        
        Document doc = DomHelper.parse(in);
        
        NodeList nl = doc.getElementsByTagName("bar");
        
        Location loc = DomHelper.getLocationObject((Element)nl.item(0));
        
        assertNotNull(loc);
        assertTrue("Should be line 6, was "+loc.getLineNumber(), 
            6==loc.getLineNumber());
    }
}
