/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers.dsx;

import com.opensymphony.xwork.config.entities.Parameterizable;
import com.opensymphony.xwork.xml.Path;
import com.opensymphony.xwork.xml.handlers.ElementValueHandler;

import java.util.Map;


/**
 * ParamHandler
 * @author Jason Carreira
 * Created May 24, 2003 12:56:20 AM
 */
public class ParamHandler extends ElementValueHandler {
    //~ Instance fields ////////////////////////////////////////////////////////

    String name;

    //~ Constructors ///////////////////////////////////////////////////////////

    public ParamHandler(Path path) {
        super(path);
        this.path.add("param");
    }

    public ParamHandler(String pathString) {
        super(pathString);
        this.path.add("param");
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void handleContent(String content) {
        Parameterizable parameterizable = (Parameterizable) getRootHandler().getValueStack().peek();
        parameterizable.addParam(name, content);
    }

    /**
    * Subclasses should override this method if they want to receive an event at the starting point of the path
    */
    public void startingPath(Path path, Map attributeMap) {
        super.startingPath(path, attributeMap);
        name = (String) attributeMap.get("name");
    }
}
