package com.opensymphony.xwork.util;

import ognl.EnumerationPropertyAccessor;
import ognl.OgnlException;
import ognl.ObjectPropertyAccessor;

import java.util.Map;

/**
 * User: plightbo
 * Date: Nov 13, 2003
 * Time: 7:13:37 AM
 */
public class XWorkEnumerationAcccessor extends EnumerationPropertyAccessor {
    ObjectPropertyAccessor opa = new ObjectPropertyAccessor();

    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        opa.setProperty(context, target, name, value);
    }
}
