package com.opensymphony.xwork2.util;

import junit.framework.TestCase;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLStreamHandlerFactory;
import java.net.URLStreamHandler;
import java.net.URLConnection;
import java.io.IOException;

public class URLUtilTest extends TestCase {

    public void testSimpleFile() throws MalformedURLException {
        URL url = new URL("file:c:/somefile.txt");
        URL outputURL = URLUtil.normalizeToFileProtocol(url);

        assertNull(outputURL);
    }

    public void testJarFile() throws MalformedURLException {
        URL url = new URL("jar:file:/c:/somefile.jar!/");
        URL outputURL = URLUtil.normalizeToFileProtocol(url);

        assertNotNull(outputURL);
        assertEquals("file:/c:/somefile.jar", outputURL.toExternalForm());

        url = new URL("jar:file:/c:/somefile.jar!/somestuf/bla/bla");
        outputURL = URLUtil.normalizeToFileProtocol(url);
        assertEquals("file:/c:/somefile.jar", outputURL.toExternalForm());

        url = new URL("jar:file:c:/somefile.jar!/somestuf/bla/bla");
        outputURL = URLUtil.normalizeToFileProtocol(url);
        assertEquals("file:c:/somefile.jar", outputURL.toExternalForm());
    }

    public void testZipFile() throws MalformedURLException {
        URL url = new URL("zip:/c:/somefile.zip!/");
        URL outputURL = URLUtil.normalizeToFileProtocol(url);

        assertNotNull(outputURL);
        assertEquals("file:/c:/somefile.zip", outputURL.toExternalForm());

        url = new URL("zip:/c:/somefile.zip!/somestuf/bla/bla");
        outputURL = URLUtil.normalizeToFileProtocol(url);
        assertEquals("file:/c:/somefile.zip", outputURL.toExternalForm());

        url = new URL("zip:c:/somefile.zip!/somestuf/bla/bla");
        outputURL = URLUtil.normalizeToFileProtocol(url);
        assertEquals("file:c:/somefile.zip", outputURL.toExternalForm());
    }

    public void testWSJarFile() throws MalformedURLException {
        URL url = new URL("wsjar:file:/c:/somefile.jar!/");
        URL outputURL = URLUtil.normalizeToFileProtocol(url);

        assertNotNull(outputURL);
        assertEquals("file:/c:/somefile.jar", outputURL.toExternalForm());

        url = new URL("wsjar:file:/c:/somefile.jar!/somestuf/bla/bla");
        outputURL = URLUtil.normalizeToFileProtocol(url);
        assertEquals("file:/c:/somefile.jar", outputURL.toExternalForm());

        url = new URL("wsjar:file:c:/somefile.jar!/somestuf/bla/bla");
        outputURL = URLUtil.normalizeToFileProtocol(url);
        assertEquals("file:c:/somefile.jar", outputURL.toExternalForm());
    }

    protected void setUp() throws Exception {
        super.setUp();

        try {
            URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
                public URLStreamHandler createURLStreamHandler(String protocol) {
                    return new URLStreamHandler() {
                        protected URLConnection openConnection(URL u) throws IOException {
                            return null;
                        }
                    };
                }
            });
        } catch (Throwable e) {
            //the factory cant be set multiple times..just ignore exception no biggie
        }
    }
}
