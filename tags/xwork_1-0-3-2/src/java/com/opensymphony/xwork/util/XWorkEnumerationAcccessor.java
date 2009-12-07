/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import ognl.EnumerationPropertyAccessor;
import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;

import java.util.Map;


/**
 * User: plightbo
 * Date: Nov 13, 2003
 * Time: 7:13:37 AM
 */
public class XWorkEnumerationAcccessor extends EnumerationPropertyAccessor {
    //~ Instance fields ////////////////////////////////////////////////////////

    ObjectPropertyAccessor opa = new ObjectPropertyAccessor();

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        opa.setProperty(context, target, name, value);
    }
}
