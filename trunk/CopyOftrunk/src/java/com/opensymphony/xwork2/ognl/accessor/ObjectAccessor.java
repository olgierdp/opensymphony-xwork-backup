/**
 * 
 */
package com.opensymphony.xwork2.ognl.accessor;

import java.util.Map;

import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.ognl.OgnlValueStack;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;

public class ObjectAccessor extends ObjectPropertyAccessor {
    public Object getProperty(Map map, Object o, Object o1) throws OgnlException {
        Object obj = super.getProperty(map, o, o1);
        OgnlValueStack.link(map, o.getClass(), (String) o1);

        map.put(XWorkConverter.LAST_BEAN_CLASS_ACCESSED, o.getClass());
        map.put(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED, o1.toString());
        ReflectionContextState.updateCurrentPropertyPath(map, o1);
        return obj;
    }

    public void setProperty(Map map, Object o, Object o1, Object o2) throws OgnlException {
        super.setProperty(map, o, o1, o2);
    }
}