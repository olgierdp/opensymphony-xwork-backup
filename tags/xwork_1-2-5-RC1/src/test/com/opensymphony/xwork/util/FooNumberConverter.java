package com.opensymphony.xwork.util;

import ognl.DefaultTypeConverter;

import java.util.Map;

public class FooNumberConverter extends DefaultTypeConverter {
    public Object convertValue(Map map, Object object, Class aClass) {
        String s = (String) object;

        int length = s.length();
        StringBuffer r = new StringBuffer();
        for (int i = length; i > 0; i--) {
            r.append(s.charAt(i - 1));
        }

        return super.convertValue(map, r.toString(), aClass);
    }
}
