/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers.dsx;

import com.opensymphony.xwork.xml.Path;
import com.opensymphony.xwork.xml.handlers.ElementValueHandler;

import java.util.List;


/**
 * ArgumentHandler
 * @author Jason Carreira
 * Created May 24, 2003 10:59:53 PM
 */
public class ArgumentHandler extends ElementValueHandler {
    //~ Instance fields ////////////////////////////////////////////////////////

    private int index;

    //~ Constructors ///////////////////////////////////////////////////////////

    public ArgumentHandler(Path path, int index) {
        super(path);
        this.index = index;
    }

    public ArgumentHandler(String pathString, int index) {
        super(pathString);
        this.index = index;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void handleContent(String content) {
        List argList = (List) getRootHandler().getValueStack().peek();
        argList.add(index, content);
    }
}
