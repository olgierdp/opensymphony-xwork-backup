/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.helpers.DefaultHandler;

import java.util.Map;


/**
 * DefaultSubHandler is the base class for SubHandler classes.
 *
 * Created Oct 21, 2002 6:42:28 PM
 * @author Jason Carreira
 * @version 1.0
 */
public abstract class DefaultSubHandler extends DefaultHandler implements SubHandler {
    //~ Static fields/initializers /////////////////////////////////////////////

    public static final String ESCAPE_LESS_THAN = "&lt;";
    public static final String ESCAPE_GREATER_THAN = "&gt;";
    public static final String ESCAPE_AMPERSAND = "&amp;";
    public static final String ESCAPE_QUOTE = "&quot;";

    //~ Instance fields ////////////////////////////////////////////////////////

    protected DelegatingHandler rootHandler;
    protected final Log LOG = LogFactory.getLog(this.getClass());
    protected Path path;

    //~ Constructors ///////////////////////////////////////////////////////////

    public DefaultSubHandler(Path path) {
        super();
        this.path = path;
    }

    public DefaultSubHandler(String pathString) {
        this(Path.getInstance(pathString));
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Path getPath() {
        return path;
    }

    public DelegatingHandler getRootHandler() {
        return rootHandler;
    }

    /**
    * Subclasses should override this method if they want to receive an event at the ending point of the path
    */
    public void endingPath(Path path) {
        // do nothing
    }

    public void registerWith(DelegatingHandler rootHandler) {
        setRootHandler(rootHandler);
        rootHandler.addHandler(path, this);
    }

    /**
    * Subclasses should override this method if they want to receive an event at the starting point of the path
    */
    public void startingPath(Path path, Map attributeMap) {
        // do nothing
    }

    public void unregisterWith(DelegatingHandler rootHandler) {
        rootHandler.removeHandler(path, this);
        this.rootHandler = null;
    }

    protected void setRootHandler(DelegatingHandler newHandler) {
        if ((rootHandler != null) && (newHandler != null)) {
            String s = "Attempting to register with a DefaultDelegatingHandler when there is a current DefaultDelegatingHandler.";
            LOG.error(s);
            throw new IllegalStateException(s);
        }

        rootHandler = newHandler;
    }

    /**
    * Encode any special characters found in <i>value</i> into XML entities.
    *
    * @param value The string to encode.
    *
    * @return The encoded string.
    */
    protected StringBuffer xmlEncode(String value) {
        return xmlEncode(value.toCharArray());
    }

    /**
    * Encode <i>ch</i>, if it's a special character, into an XML entities, and add it to the StringBuffer supplied.
    * If the character does not need to be encoded, it is added as is to the StringBuffer.
    *
    * @param ch The character to encode.
    */
    private void addEncoded(char ch, StringBuffer buffer) {
        switch (ch) {
        case '<':
            buffer.append(ESCAPE_LESS_THAN);

            break;

        case '>':
            buffer.append(ESCAPE_GREATER_THAN);

            break;

        case '&':
            buffer.append(ESCAPE_AMPERSAND);

            break;

        case '"':
            buffer.append(ESCAPE_QUOTE);

            break;

        default:
            buffer.append(ch);

            break;
        }
    }

    /**
    * Encode any special characters found in <i>ch</i> into XML entities.
    *
    * @param ch The character array to encode.
    *
    * @return The encoded string.
    */
    private StringBuffer xmlEncode(char[] ch) {
        return xmlEncode(ch, 0, ch.length);
    }

    /**
    * Encode any special characters found in <i>ch</i> into XML entities.  This
    * method encodes the characters <i>ch[start]</i> through
    * <i>ch[start + length - 1]</i>.
    *
    * @param ch The character array to encode.
    * @param start The starting index of <i>ch</i>.
    * @return The encoded string.
    */
    private StringBuffer xmlEncode(char[] ch, int start, int length) {
        StringBuffer ret = new StringBuffer(length * 2);

        if (length > 0) {
            int i;
            int j;

            for (i = 0, j = start; i < length; ++i, ++j) {
                addEncoded(ch[j], ret);
            }
        }

        return ret;
    }
}
