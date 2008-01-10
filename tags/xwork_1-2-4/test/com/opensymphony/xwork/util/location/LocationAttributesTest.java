/*
 * Copyright (c) 2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util.location;

import org.w3c.dom.Element;
import org.xml.sax.Locator;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;

import junit.framework.TestCase;

public class LocationAttributesTest extends TestCase {
    
    public LocationAttributesTest(String name) {
        super(name);
    }
    
    public void testAddLocationAttributes() throws Exception {
        AttributesImpl attrs = new AttributesImpl();
        LocationAttributes.addLocationAttributes(new Locator() {
            public int getColumnNumber() { return 40; }
            public int getLineNumber() { return 1; }
            public String getSystemId() { return "path/to/file.xml"; }
            public String getPublicId() { return "path/to/file.xml"; }
        }, attrs);

        assertTrue("path/to/file.xml".equals(attrs.getValue("loc:src")));
        assertTrue("1".equals(attrs.getValue("loc:line")));
        assertTrue("40".equals(attrs.getValue("loc:column")));
    }
 
    public void testRecursiveRemove() throws Exception {
        Document doc = getDoc("xml-with-location.xml");

        Element root = doc.getDocumentElement();
        LocationAttributes.remove(root, true);

        assertNull(root.getAttributeNode("loc:line"));
        assertNull(root.getAttributeNode("loc:column"));
        assertNull(root.getAttributeNode("loc:src"));

        Element kid = (Element)doc.getElementsByTagName("bar").item(0);
        assertNull(kid.getAttributeNode("loc:line"));
        assertNull(kid.getAttributeNode("loc:column"));
        assertNull(kid.getAttributeNode("loc:src"));
    }    

    public void testNonRecursiveRemove() throws Exception {
        Document doc = getDoc("xml-with-location.xml");

        Element root = doc.getDocumentElement();
        LocationAttributes.remove(root, false);

        assertNull(root.getAttributeNode("loc:line"));
        assertNull(root.getAttributeNode("loc:column"));
        assertNull(root.getAttributeNode("loc:src"));

        Element kid = (Element)doc.getElementsByTagName("bar").item(0);
        assertNotNull(kid.getAttributeNode("loc:line"));
        assertNotNull(kid.getAttributeNode("loc:column"));
        assertNotNull(kid.getAttributeNode("loc:src"));
    }    

    private Document getDoc(String path) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(LocationAttributesTest.class.getResourceAsStream(path));


    }
}
