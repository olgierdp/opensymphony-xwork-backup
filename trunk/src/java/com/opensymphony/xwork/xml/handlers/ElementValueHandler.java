/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.xml.handlers;

import com.opensymphony.xwork.xml.DefaultSubHandler;
import com.opensymphony.xwork.xml.Path;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * ElementValueHandler
 *
 * Created Oct 23, 2002 8:38:01 PM
 * @author Jason Carreira
 * @version 1.0
 */
public abstract class ElementValueHandler extends DefaultSubHandler {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected StringBuffer buffer;
    protected StringBuffer characters;

    //~ Constructors ///////////////////////////////////////////////////////////

    public ElementValueHandler(Path path) {
        super(path);
        this.buffer = new StringBuffer(1024 * 256);
        this.characters = new StringBuffer(16);
    }

    public ElementValueHandler(String pathString) {
        this(Path.getInstance(pathString));
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public String getContent() {
        return buffer.toString();
    }

    public void characters(char[] chars, int start, int length) throws SAXException {
        characters.append(chars, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        String body = characters.toString();

        if (body != null) {
            buffer.append(xmlEncode(body.trim()).toString());
        }

        characters.setLength(0);
        handleContent(buffer.toString());
    }

    public abstract void handleContent(String content);

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        buffer.setLength(0);
        characters.setLength(0);
    }
}
