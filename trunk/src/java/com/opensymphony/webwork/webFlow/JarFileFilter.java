/*
 * Created on Aug 13, 2004 by mgreer
 */
package com.opensymphony.webwork.webFlow;

import java.io.File;
import java.io.FileFilter;


/**
 * TODO Describe JarFileFilter
 */
public class JarFileFilter implements FileFilter {
    private String regex = ".*";

    /**
     *
     */
    public JarFileFilter() {
        super();
    }

    public JarFileFilter(String regex) {
        super();
        this.regex = regex;
    }

    /* (non-Javadoc)
     * @see java.io.FileFilter#accept(java.io.File)
     */
    public boolean accept(File pathname) {
        return pathname.getName().matches(regex);
    }
}
