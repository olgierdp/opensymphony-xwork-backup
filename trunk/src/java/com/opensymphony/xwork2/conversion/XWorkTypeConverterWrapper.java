package com.opensymphony.xwork2.conversion;

import java.lang.reflect.Member;
import java.util.Map;

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
