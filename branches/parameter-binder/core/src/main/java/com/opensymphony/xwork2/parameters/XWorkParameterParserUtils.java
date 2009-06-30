package com.opensymphony.xwork2.parameters;

import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import com.opensymphony.xwork2.conversion.NullHandler;
import com.opensymphony.xwork2.inject.Inject;

import java.util.Map;

public class XWorkParameterParserUtils {
    private NullHandler nullHandler;
    private ReflectionProvider reflectionProvider;

    public Object get(String expression, Map<String, Object> context, Object root) {
        Object value = reflectionProvider.getValue(expression, context, root);
        if (value == null) {
            //instantiate it and set it
            boolean previousValue = ReflectionContextState.isCreatingNullObjects(context);
            ReflectionContextState.setCreatingNullObjects(context, true);

            try {
                return nullHandler.nullPropertyValue(context, root, expression);
            } finally {
                ReflectionContextState.setCreatingNullObjects(context, previousValue);
            }
        }

        return value;
    }

    @Inject
    public void setReflectionProvider(ReflectionProvider reflectionProvider) {
        this.reflectionProvider = reflectionProvider;
    }

    @Inject("java.lang.Object")
    public void setNullHandler(NullHandler nullHandler) {
        this.nullHandler = nullHandler;
    }
}
