/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.xml.handlers;

import com.opensymphony.xwork.util.OgnlUtil;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.xml.Path;
import com.opensymphony.xwork.xml.handlers.ElementValueHandler;

import ognl.Ognl;

import java.util.Map;


/**
 * SetPropertyHandler
 * @author Jason Carreira
 * Created May 16, 2003 8:11:25 PM
 */
public class SetPropertyHandler extends ElementValueHandler {
    //~ Instance fields ////////////////////////////////////////////////////////

    private String propertyName;

    //~ Constructors ///////////////////////////////////////////////////////////

    public SetPropertyHandler(Path path, String propertyName) {
        super(path);
        this.propertyName = propertyName;
    }

    public SetPropertyHandler(String pathString, String propertyName) {
        super(pathString);
        this.propertyName = propertyName;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void handleContent(String content) {
        OgnlValueStack stack = getRootHandler().getValueStack();

        if (stack.size() > 0) {
            Object target = stack.peek();
            OgnlUtil.setProperty(propertyName, content, target, stack.getContext());
        } else {
            getRootHandler().addError(new IllegalArgumentException("There was no object on the stack to set the property '" + propertyName + "' on."));
        }
    }
}
