/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.ognl;

import java.lang.reflect.Member;
import java.util.Map;

import com.opensymphony.xwork2.conversion.TypeConverter;

/**
 * Wraps an OGNL TypeConverter as an XWork TypeConverter
 */
public class XWorkTypeConverterWrapper implements TypeConverter {

    private ognl.TypeConverter typeConverter;
    
    public XWorkTypeConverterWrapper(ognl.TypeConverter conv) {
        this.typeConverter = conv;
    }
    
    public Object convertValue(Map context, Object target, Member member,
            String propertyName, Object value, Class toType) {
        return typeConverter.convertValue(context, target, member, propertyName, value, toType);
    }
}
