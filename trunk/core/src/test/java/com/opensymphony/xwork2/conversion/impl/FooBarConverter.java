/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.util.AnnotatedCat;
import com.opensymphony.xwork2.util.Bar;
import com.opensymphony.xwork2.util.Cat;

import java.lang.reflect.Member;
import java.util.Map;


/**
 * @author <a href="mailto:plightbo@cisco.com">Pat Lightbody</a>
 * @author $Author$
 * @version $Revision$
 */
public class FooBarConverter extends DefaultTypeConverter {

    @Override
    public Object convertValue(Map<String, Object> context, Object value, Class toType) {
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
        } else if (toType == Cat.class) {
            Cat cat = new Cat();
            cat.setName((String) value);

            return cat;
        } else if (toType == AnnotatedCat.class) {
            AnnotatedCat cat = new AnnotatedCat();
            cat.setName((String) value);

            return cat;
        } else {
            System.out.println("Don't know how to convert between " + value.getClass().getName() +
                    " and " + toType.getName());
        }

        return null;
    }

    @Override
    public Object convertValue(Map<String, Object> context, Object source, Member member, String property, Object value, Class toClass) {
        return convertValue(context, value, toClass);
    }
}
