/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */

/**
 * PathLocationEnum
 * @author Jason Carreira
 * Created May 29, 2003 12:47:09 PM
 */
package com.opensymphony.xwork.xml.handlers;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class PathLocationEnum {
    //~ Static fields/initializers /////////////////////////////////////////////

    public static final PathLocationEnum START = new PathLocationEnum("START");
    public static final PathLocationEnum END = new PathLocationEnum("END");
    public static final PathLocationEnum BOTH = new PathLocationEnum("BOTH");

    //~ Instance fields ////////////////////////////////////////////////////////

    private final String myName; // for debug only

    //~ Constructors ///////////////////////////////////////////////////////////

    private PathLocationEnum(String name) {
        myName = name;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public String toString() {
        return myName;
    }
}
