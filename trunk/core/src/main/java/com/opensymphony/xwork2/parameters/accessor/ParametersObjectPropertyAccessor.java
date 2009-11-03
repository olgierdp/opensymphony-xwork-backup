/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.parameters.accessor;


import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import com.opensymphony.xwork2.parameters.bytecode.AccessorBytecodeUtil;
import com.opensymphony.xwork2.parameters.bytecode.Getter;
import com.opensymphony.xwork2.parameters.bytecode.Setter;
import com.opensymphony.xwork2.ObjectFactory;

import java.util.Map;

/**
 * Implementation similar to XWorkObjectPropertyAccessor, but does not depend on OGNL
 */
public class ParametersObjectPropertyAccessor implements ParametersPropertyAccessor {
    protected AccessorBytecodeUtil accessorBytecodeUtil;
    private XWorkConverter xworkConverter;
    private ReflectionProvider reflectionProvider;

    @Inject
    public void setXWorkConverter(XWorkConverter conv) {
        this.xworkConverter = conv;
    }

    @Inject
    public void setReflectionProvider(ReflectionProvider reflectionProvider) {
        this.reflectionProvider = reflectionProvider;
    }


    @Override
    public Object getProperty(Map context, Object target, Object property) throws Exception {
        Getter getter = accessorBytecodeUtil.getGetter(target.getClass(), property.toString());
        Object obj = getter.invoke(target);

        context.put(XWorkConverter.LAST_BEAN_CLASS_ACCESSED, target.getClass());
        context.put(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED, property.toString());
        return obj;
    }

    @Override
    public void setProperty(Map context, Object target, Object property, Object value) throws Exception {
        String propertyName = property.toString();
        Class targetType = target.getClass();

        Class expectedType = reflectionProvider.getPropertyDescriptor(targetType, propertyName).getWriteMethod().getParameterTypes()[0];
        Class valueType = value.getClass();

        Setter setter = accessorBytecodeUtil.getSetter(targetType, expectedType, propertyName);

        //convert value, if needed
        if (!expectedType.isAssignableFrom(valueType)) {
            value = xworkConverter.convertValue(context, value, expectedType);
        }

        setter.invoke(target, value);
    }

    @Inject
    public void setAccessorBytecodeUtil(AccessorBytecodeUtil accessorBytecodeUtil) {
        this.accessorBytecodeUtil = accessorBytecodeUtil;
    }
}
