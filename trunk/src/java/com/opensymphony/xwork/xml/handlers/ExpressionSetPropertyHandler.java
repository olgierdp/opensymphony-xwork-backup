/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.xml.handlers;

import com.opensymphony.xwork.util.CompoundRootAccessor;
import com.opensymphony.xwork.util.OgnlUtil;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.xml.DefaultSubHandler;
import com.opensymphony.xwork.xml.Path;

import ognl.MethodFailedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * ExpressionSetPropertyHandler
 * @author Jason Carreira
 * Created May 29, 2003 12:45:13 PM
 */
public class ExpressionSetPropertyHandler extends DefaultSubHandler {
    //~ Instance fields ////////////////////////////////////////////////////////

    private PathLocationEnum pathLocation;
    private String attributeFlag;
    private String property;
    private String value;
    private boolean shouldSet = true;

    //~ Constructors ///////////////////////////////////////////////////////////

    public ExpressionSetPropertyHandler(Path path, String property, String value, PathLocationEnum pathLocation) {
        super(path);
        this.property = property;
        this.value = value;
        this.pathLocation = pathLocation;
    }

    public ExpressionSetPropertyHandler(String pathString, String property, String value, PathLocationEnum pathLocation) {
        super(pathString);
        this.property = property;
        this.value = value;
        this.pathLocation = pathLocation;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setAttributeFlag(String attributeFlag) {
        this.attributeFlag = attributeFlag;
        shouldSet = false;
    }

    public void endingPath(Path path) {
        if (pathLocation.equals(PathLocationEnum.END) || pathLocation.equals(PathLocationEnum.BOTH)) {
            setProperty();
        }
    }

    /**
    * Subclasses should override this method if they want to receive an event at the starting point of the path
    */
    public void startingPath(Path path, Map attributeMap) {
        if (attributeFlag != null) {
            String strVal = (String) attributeMap.get(attributeFlag);
            Boolean booleanVal = Boolean.valueOf(strVal);
            this.shouldSet = booleanVal.booleanValue();
        }

        if (pathLocation.equals(PathLocationEnum.START) || pathLocation.equals(PathLocationEnum.BOTH)) {
            setProperty();
        }
    }

    private void setProperty() {
        if (shouldSet) {
            OgnlValueStack valueStack = getRootHandler().getValueStack();
            Object val = valueStack.findValue(value);
            valueStack.setValue(property, val);
        }
    }
}
