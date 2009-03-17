/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import org.apache.commons.lang.StringUtils;

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

    public final static String htmlEncode(String s) {
        return htmlEncode(s, true);
    }

    /**
     * Escape html entity characters and high characters (eg "curvy" Word quotes).
     * Note this method can also be used to encode XML.
     * @param s the String to escape.
     * @param encodeSpecialChars if true high characters will be encode other wise not.
     * @return the escaped string
     */
    public final static String htmlEncode(String s, boolean encodeSpecialChars) {
        s = StringUtils.defaultString(s);

        StringBuilder str = new StringBuilder();

        for (int j = 0; j < s.length(); j++) {
            char c = s.charAt(j);

            // encode standard ASCII characters into HTML entities where needed
            if (c < '\200') {
                switch (c) {
                case '"':
                    str.append("&quot;");

                    break;

                case '&':
                    str.append("&amp;");

                    break;

                case '<':
                    str.append("&lt;");

                    break;

                case '>':
                    str.append("&gt;");

                    break;

                default:
                    str.append(c);
                }
            }
            // encode 'ugly' characters (ie Word "curvy" quotes etc)
            else if (encodeSpecialChars && (c < '\377')) {
                String hexChars = "0123456789ABCDEF";
                int a = c % 16;
                int b = (c - a) / 16;
                String hex = "" + hexChars.charAt(b) + hexChars.charAt(a);
                str.append("&#x" + hex + ";");
            }
            //add other characters back in - to handle charactersets
            //other than ascii
            else {
                str.append(c);
            }
        }

        return str.toString();
    }

    /**
     * Escape a String into a JavaScript-compatible String.
     *
     * @param s the String to escape.
     * @return the escaped string
     */
    public final static String escapeJavaScript(String s) {
        s = StringUtils.defaultString(s);
        StringBuffer str = new StringBuffer();

        for (int j = 0; j < s.length(); j++) {
            char c = s.charAt(j);
            switch (c) {
                case '\t':
                    str.append("\\t");
                    break;
                case '\b':
                    str.append("\\b");
                    break;
                case '\n':
                    str.append("\\n");
                    break;
                case '\f':
                    str.append("\\f");
                    break;
                case '\r':
                    str.append("\\r");
                    break;
                case '\\':
                    str.append("\\\\");
                    break;
                case '"':
                    str.append("\\\"");
                    break;
                case '\'':
                    str.append("\\'");
                    break;
                case '/':
                    str.append("\\/");
                    break;
                default:
                    str.append(c);
            }
        }
        return str.toString();
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
