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
    public static final Pattern JAR_PATTERN = Pattern.compile("^(jar:|wsjar:|zip:)?(file:)?(.*?)(\\!/)(.*)");
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
            if (jarMatcher.matches()) {
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
}
