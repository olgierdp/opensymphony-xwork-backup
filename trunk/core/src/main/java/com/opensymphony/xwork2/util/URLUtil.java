/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Helper class to extract file paths from different urls
 */
public class URLUtil {

    /**
     * Prefix for Jar files in JBoss Virtual File System
     */
    public static final String JBOSS5_VFSZIP = "vfszip";

    private static final Pattern JAR_PATTERN = Pattern.compile("^(jar:|wsjar:|zip:|vfsfile:|code-source:)?(file:)?(.*?)(\\!/|.jar/)(.*)");
    private static final int JAR_FILE_PATH = 3;

    /**
     * Convert URLs to URLs with "file" protocol
     * @param url URL to convert to a jar url
     * @return a URL to a file, or null if the URL external form cannot be parsed
     */
    public static URL normalizeToFileProtocol(URL url) {
        String fileName = url.toExternalForm();
        Matcher jarMatcher = JAR_PATTERN.matcher(fileName);
        try {
            if (isJBoss5Url(url)){
                return new URL("file", null, fileName.substring(JBOSS5_VFSZIP.length() + 1));
            } else  if (jarMatcher.matches()) {
                String path = jarMatcher.group(JAR_FILE_PATH);
                return new URL("file", "", path);
            } else {
                //it is not a jar or zip file
                return null;
            }
        } catch (MalformedURLException e) {
            //can this ever happen?
            return null;
        }
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

    /**
     * Check if given URL is matching Jar pattern for different servers
     * @param fileUrl
     * @return
     */
    public static boolean isJarURL(URL fileUrl) {
        Matcher jarMatcher = URLUtil.JAR_PATTERN.matcher(fileUrl.getPath());
        return jarMatcher.matches(); 
    }

    /**
     * Check if given URL is pointing to JBoss 5 VFS resource
     * @param fileUrl
     * @return
     */
    public static boolean isJBoss5Url(URL fileUrl) {
        return JBOSS5_VFSZIP.equals(fileUrl.getProtocol());
    }

}
