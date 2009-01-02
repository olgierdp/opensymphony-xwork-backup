package com.opensymphony.xwork2.mvel.accessors;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.mvel.MVELContext;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import org.mvel2.integration.Listener;
import org.mvel2.integration.VariableResolverFactory;

public class MVELGetListener implements Listener {
    public void onEvent(Object target, String property, VariableResolverFactory variableFactory, Object value) {
        DefaultVariableResolverFactory factory = (DefaultVariableResolverFactory) variableFactory;
        MVELContext context = factory.getContext();
        context.put(XWorkConverter.LAST_BEAN_CLASS_ACCESSED, target.getClass());
        context.put(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED, property);
        ReflectionContextState.updateCurrentPropertyPath(context, property);
    }
}
