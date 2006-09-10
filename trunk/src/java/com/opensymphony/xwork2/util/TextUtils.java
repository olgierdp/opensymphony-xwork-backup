/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;


/**
 * Utilities for common String manipulations.
 *
 * This is a class contains static methods only and is not meant to be instantiated.
 * It was brought in from oscore trunk revision 147, and trimmed to only contain
 * methods used by XWork.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @author <a href="mailto:pkan@internet.com">Patrick Kan</a>
 * @author <a href="mailto:mcannon@internet.com">Mike Cannon-Brookes</a>
 * @author <a href="mailto:hani@fate.demon.co.uk">Hani Suleiman</a>
 * @author <a href="mailto:joeo@adjacency.org">Joseph B. Ottinger</a>
 * @author <a href="mailto:scott@atlassian.com">Scott Farquhar</a>
 *
 * @version $Revision: 147 $
 */
public class TextUtils {
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     * An array of HTML tags that, in HTML, don't require closing tags. Note that
     * XHTML doesn't work this way.
     */
    public final static String[] SINGLE_TAGS = {"br", "p", "hr"};

    /**
     * Join an Iteration of Strings together.
     *
     * <h5>Example</h5>
     *
     * <pre>
     *   // get Iterator of Strings ("abc","def","123");
     *   Iterator i = getIterator();
     *   out.print( TextUtils.join(", ",i) );
     *   // prints: "abc, def, 123"
     * </pre>
     *
     * @param glue Token to place between Strings.
     * @param pieces Iteration of Strings to join.
     * @return String presentation of joined Strings.
     */
    public final static String join(String glue, Iterator pieces) {
        StringBuffer s = new StringBuffer();

        while (pieces.hasNext()) {
            s.append(pieces.next().toString());

            if (pieces.hasNext()) {
                s.append(glue);
            }
        }

        return s.toString();
    }

    /**
     * Join an array of Strings together.
     *
     * @param glue Token to place between Strings.
     * @param pieces Array of Strings to join.
     * @return String presentation of joined Strings.
     *
     * @see #join(String, java.util.Iterator)
     */
    public final static String join(String glue, String[] pieces) {
        return join(glue, Arrays.asList(pieces).iterator());
    }

    /**
     * Join a Collection of Strings together.
     *
     * @param glue Token to place between Strings.
     * @param pieces Collection of Strings to join.
     * @return String presentation of joined Strings.
     *
     * @see #join(String, java.util.Iterator)
     */
    public final static String join(String glue, Collection pieces) {
        return join(glue, pieces.iterator());
    }

    /**
     * Return <code>string</code>, or <code>defaultString</code> if
     * <code>string</code> is <code>null</code> or <code>""</code>.
     * Never returns <code>null</code>.
     *
     * <p>Examples:</p>
     * <pre>
     * // prints "hello"
     * String s=null;
     * System.out.println(TextUtils.noNull(s,"hello");
     *
     * // prints "hello"
     * s="";
     * System.out.println(TextUtils.noNull(s,"hello");
     *
     * // prints "world"
     * s="world";
     * System.out.println(TextUtils.noNull(s, "hello");
     * </pre>
     *
     * @param string the String to check.
     * @param defaultString The default string to return if <code>string</code> is <code>null</code> or <code>""</code>
     * @return <code>string</code> if <code>string</code> is non-empty, and <code>defaultString</code> otherwise
     * @see #stringSet(java.lang.String)
     */
    public final static String noNull(String string, String defaultString) {
        return (stringSet(string)) ? string : defaultString;
    }

    /**
     * Return <code>string</code>, or <code>""</code> if <code>string</code>
     * is <code>null</code>. Never returns <code>null</code>.
     * <p>Examples:</p>
     * <pre>
     * // prints 0
     * String s=null;
     * System.out.println(TextUtils.noNull(s).length());
     *
     * // prints 1
     * s="a";
     * System.out.println(TextUtils.noNull(s).length());
     * </pre>
     * @param string the String to check
     * @return a valid (non-null) string reference
     */
    public final static String noNull(String string) {
        return noNull(string, "");
    }

    /**
     * Check whether <code>string</code> has been set to
     * something other than <code>""</code> or <code>null</code>.
     * @param string the <code>String</code> to check
     * @return a boolean indicating whether the string was non-empty (and non-null)
     */
    public final static boolean stringSet(String string) {
        return (string != null) && !"".equals(string);
    }

    /**
     * Verify That the given String is in valid URL format.
     * @param url The url string to verify.
     * @return a boolean indicating whether the URL seems to be incorrect.
     */
    public final static boolean verifyUrl(String url) {
        if (url == null) {
            return false;
        }

        if (url.startsWith("https://")) {
            // URL doesn't understand the https protocol, hack it
            url = "http://" + url.substring(8);
        }

        try {
            new URL(url);

            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
