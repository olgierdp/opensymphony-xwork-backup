/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import ognl.DefaultTypeConverter;

import java.lang.reflect.Member;

import java.util.Map;


/**
 *
 *
 * @author <a href="mailto:plightbo@cisco.com">Pat Lightbody</a>
 * @author $Author$
 * @version $Revision$
 */
public class FooBarConverter extends DefaultTypeConverter {
    //~ Methods ////////////////////////////////////////////////////////////////

    public Object convertValue(Map context, Object value, Class toType) {
        if (toType == String.class) {
            Bar bar = (Bar) value;

            return bar.getTitle() + ":" + bar.getSomethingElse();
        } else if (toType == Bar.class) {
            String valueStr = (String) value;
            int loc = valueStr.indexOf(":");
            String title = valueStr.substring(0, loc);
            String rest = valueStr.substring(loc + 1);

            Bar bar = new Bar();
            bar.setTitle(title);
            bar.setSomethingElse(Integer.parseInt(rest));

            return bar;
        }

        return null;
    }

    /* (non-Javadoc)
 * @see ognl.TypeConverter#convertValue(java.util.Map, java.lang.Object, java.lang.reflect.Member, java.lang.String, java.lang.Object, java.lang.Class)
 */
    public Object convertValue(Map context, Object source, Member member, String property, Object value, Class toClass) {
        return convertValue(context, value, toClass);
    }
}
