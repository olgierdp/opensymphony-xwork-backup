/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;


/**
 * Simple element path container.  Mostly used for path equallity checks.
 *
 * @author Jason Carreira
 */
public class Path {
    //~ Instance fields ////////////////////////////////////////////////////////

    /** The path elements. */
    private List path;

    /** The path's hash code.  It recalculated whenever the path changes. */
    private int hashCode;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
    * Default constructor.
    */
    private Path() {
        super();
        setPath(new ArrayList(1));
        setHashCode(0x0);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
    * Create a copy of this path.
    *
    * @return A path copy.
    */
    public Path getCopy() {
        return Path.getInstance(toString());
    }

    /**
    * Create a path from the given string representation.  The path elements
    * are created by tokenizing the string representation on
    * &quot;<tt>/</tt>&quot;.  Each token found in the string becomes an
    * element in the path.
    *
    * @param path The string representation.
    *
    * @return The new path.
    */
    public static Path getInstance(String path) {
        Path ret = new Path();
        StringTokenizer st = new StringTokenizer(path, "/");

        while (st.hasMoreTokens()) {
            String s = st.nextToken();

            if (s != null) {
                s = s.trim();

                if ((s != null) && (s.length() > 0)) {
                    ret.add(s);
                }
            }
        }

        return ret;
    }

    public static Path getInstance(Path path) {
        Path retPath = getInstance(path.toString());

        return retPath;
    }

    /**
    * Create an empty path.
    *
    * @return The root instance.
    */
    public static Path getRootInstance() {
        return new Path();
    }

    /**
    * Add an element to the end of this path.
    *
    * @param element The element to add.
    */
    public void add(String element) {
        if (element != null) {
            element = element.trim();

            if (!element.equals("")) {
                getPath().add(element);
                updateHashCode(element);
            }
        }
    }

    /**
    * Compare this object with <i>other</i> object for equallity.
    *
    * @param other The comparison object.
    * @return boolean <tt>true</tt> if this object and <i>other</i> are equal.
    *                 <tt>false</tt> otherwise.
    */
    public boolean equals(Object other) {
        boolean ret;

        if (other != null) {
            try {
                ret = equals((Path) other);
            } catch (ClassCastException ex) {
                ret = false;
            }
        } else {
            ret = false;
        }

        return ret;
    }

    /**
    * Compare this path with <i>other</i> path for equallity.
    *
    * @param other The comparison path.
    * @return boolean <tt>true</tt> if this path and <i>other</i> are equal.
    *                 <tt>false</tt> otherwise.
    */
    public boolean equals(Path other) {
        return (getPath().size() == other.getPath().size()) && (getPath().equals(other.getPath()));
    }

    public int hashCode() {
        return getHashCode();
    }

    /**
    * Remove the last element of this path.
    */
    public void remove() {
        if (!getPath().isEmpty()) {
            Object element = getPath().remove(getPath().size() - 1);
            updateHashCode(element);
        }
    }

    /**
    * Create a string representation for this path.
    *
    * @return String The string representation.
    */
    public String toString() {
        StringBuffer buff = new StringBuffer(32);
        Iterator iter = getPath().iterator();

        while (iter.hasNext()) {
            buff.append("/").append(iter.next());
        }

        return buff.toString();
    }

    private void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

    private int getHashCode() {
        return hashCode;
    }

    /**
    * Change the path list.
    */
    private void setPath(List path) {
        this.path = path;
    }

    /**
    * Access the path list.
    */
    private List getPath() {
        return path;
    }

    private void updateHashCode(Object element) {
        setHashCode(getHashCode() ^ element.hashCode());
    }
}
