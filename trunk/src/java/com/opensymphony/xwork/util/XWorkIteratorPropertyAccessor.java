package com.opensymphony.xwork.util;

import ognl.IteratorPropertyAccessor;
import ognl.OgnlException;
import ognl.ObjectPropertyAccessor;

import java.util.Map;

/**
 * User: plightbo
 * Date: Nov 13, 2003
 * Time: 7:12:22 AM
 */
public class XWorkIteratorPropertyAccessor extends IteratorPropertyAccessor {
    ObjectPropertyAccessor opa = new ObjectPropertyAccessor();

    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        opa.setProperty(context, target, name, value);
    }
}
