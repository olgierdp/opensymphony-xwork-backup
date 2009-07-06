package com.opensymphony.xwork2.parameters.accessor;


import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;

import java.util.Map;


public class ParametersObjectPropertyAccessor implements ParametersPropertyAccessor {
    protected ReflectionProvider reflectionProvider;

    @Override
    public Object getProperty(Map context, Object target, Object property) throws Exception {
        Object obj = reflectionProvider.getValue(property.toString(), context, target);

        context.put(XWorkConverter.LAST_BEAN_CLASS_ACCESSED, target.getClass());
        context.put(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED, property.toString());
        ReflectionContextState.updateCurrentPropertyPath(context, property);
        return obj;
    }

    @Override
    public void setProperty(Map context, Object target, Object property, Object value) throws Exception {
        reflectionProvider.setProperty(property.toString(), value, target, context);
    }

    @Inject
    public void setReflectionProvider(ReflectionProvider reflectionProvider) {
        this.reflectionProvider = reflectionProvider;
    }
}
