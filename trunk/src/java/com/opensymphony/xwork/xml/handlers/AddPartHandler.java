/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.xml.handlers;

import com.opensymphony.xwork.util.OgnlUtil;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.xml.DefaultSubHandler;
import com.opensymphony.xwork.xml.Path;

import java.beans.Introspector;

import java.lang.reflect.Method;


/**
 * AddPartHandler
 * @author Jason Carreira
 * Created May 19, 2003 8:18:36 AM
 */
public class AddPartHandler extends DefaultSubHandler {
    //~ Instance fields ////////////////////////////////////////////////////////

    String methodName;

    //~ Constructors ///////////////////////////////////////////////////////////

    public AddPartHandler(Path path, String methodName) {
        super(path);
        this.methodName = methodName;
    }

    public AddPartHandler(String pathString, String methodName) {
        super(pathString);
        this.methodName = methodName;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
    * Subclasses should override this method if they want to receive an event at the ending point of the path
    */
    public void endingPath(Path path) {
        final OgnlValueStack valueStack = getRootHandler().getValueStack();
        Object part = valueStack.pop();
        Object parent = valueStack.peek();

        try {
            //            OgnlUtil.setProperty(methodName,part,parent,valueStack.getContext());
            Method method = parent.getClass().getMethod(methodName, new Class[] {
                    part.getClass()
                });
            method.invoke(parent, new Object[] {part});
        } catch (Exception e) {
            getRootHandler().addFatal(e);
        }
    }
}
