/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.xml;

import com.opensymphony.xwork.xml.DefaultSubHandler;
import com.opensymphony.xwork.xml.Path;


/**
 * TestSubHandler
 * @author Jason Carreira
 * Created May 16, 2003 10:27:42 PM
 */
public class TestSubHandler extends DefaultSubHandler {
    //~ Constructors ///////////////////////////////////////////////////////////

    public TestSubHandler(Path path) {
        super(path);
    }

    public TestSubHandler(String pathString) {
        super(pathString);
    }
}
