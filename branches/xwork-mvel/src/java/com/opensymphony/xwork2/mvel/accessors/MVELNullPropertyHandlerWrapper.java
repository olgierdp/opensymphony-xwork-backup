package com.opensymphony.xwork2.mvel.accessors;

import com.opensymphony.xwork2.conversion.NullHandler;
import com.opensymphony.xwork2.mvel.accessors.DefaultVariableResolverFactory;
import com.opensymphony.xwork2.mvel.MVELContext;
import com.opensymphony.xwork2.inject.Inject;

import java.util.Map;
import java.util.Collections;

import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.PropertyHandler;

public class MVELNullPropertyHandlerWrapper implements PropertyHandler{
    private NullHandler wrapped;

    @Inject("java.lang.Object")
    public void setWrapped(NullHandler wrapped) {
        this.wrapped = wrapped;
    }

    public Object getProperty(String property, Object target, VariableResolverFactory variableFactory) {
        String propertyName = property.startsWith("get") ? property.substring(3, 5).toLowerCase() + property.substring(5) : property;
        MVELContext contex = ((DefaultVariableResolverFactory)variableFactory).getContext();
        return wrapped.nullPropertyValue(contex, target, propertyName);
    }

    public Object setProperty(String method, Object target, VariableResolverFactory variableFactory, Object value) {
        String methodName = method.startsWith("set") ? method.substring(3, 5).toLowerCase() + method.substring(5) : method;
        MVELContext contex = ((DefaultVariableResolverFactory)variableFactory).getContext();
        return wrapped.nullMethodResult(contex, target, methodName, new Object[] {value});
    }
}
