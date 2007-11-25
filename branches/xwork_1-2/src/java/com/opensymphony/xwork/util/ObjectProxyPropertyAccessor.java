/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork.util;

import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;
import ognl.enhance.ExpressionCompiler;
import ognl.enhance.UnsupportedCompilationException;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Is able to access (set/get) properties on a given object.
 * <p/>
 * Uses Ognl internal.
 *
 * @author Gabe
 * @author tmjee
 */
public class ObjectProxyPropertyAccessor implements PropertyAccessor {


    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        ObjectProxy proxy = (ObjectProxy) target;
        setupContext(context, proxy);

        return OgnlRuntime.getPropertyAccessor(proxy.getValue().getClass()).getProperty(context, target, name);

    }

    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        ObjectProxy proxy = (ObjectProxy) target;
        setupContext(context, proxy);

        OgnlRuntime.getPropertyAccessor(proxy.getValue().getClass()).setProperty(context, target, name, value);
    }

    public Class getPropertyClass(OgnlContext ognlcontext, Object target, Object name) {
        ObjectProxy proxy = (ObjectProxy) target;

        Object tmp = proxy.getValue();
        if (tmp != null) {
            return tmp.getClass();
        }
        return Object.class;
    }

    public String getSourceAccessor(OgnlContext ognlcontext, Object target, Object name) {
        ObjectProxy proxy = (ObjectProxy) target;
        Object tmp = proxy.getValue();
        String beanName = ((String)name).replaceAll("\"", "");
        
        if (tmp != null) {
            Method m = OgnlRuntime.getReadMethod(tmp.getClass(), beanName);

            Class type = OgnlRuntime.getCompiler().getSuperOrInterfaceClass(m, proxy.getValue().getClass());

            ExpressionCompiler.addCastString(ognlcontext, "((" + type.getName() + ")");

            ognlcontext.setCurrentType(type);
            ognlcontext.setCurrentAccessor(ObjectProxy.class);

            return "."+m.getName()+"()";
        }
        return "";
    }

    public String getSourceSetter(OgnlContext ognlcontext, Object target, Object name) {
        ObjectProxy proxy = (ObjectProxy) target;
        Object tmp = proxy.getValue();
        String beanName = ((String)name).replaceAll("\"", "");

        if (tmp != null) {
            Method m = OgnlRuntime.getWriteMethod(tmp.getClass(), beanName);

            Class type = OgnlRuntime.getCompiler().getSuperOrInterfaceClass(m, proxy.getValue().getClass());

            if (m.getParameterTypes().length != 1) {
                throw new UnsupportedCompilationException("Object property accessors can only support single parameter setters.");
            }

            Class param = m.getParameterTypes()[0];
            String conversion = null;

            if (param.isPrimitive()) {
                Class wrapClass = OgnlRuntime.getPrimitiveWrapperClass(param);
                conversion = OgnlRuntime.getCompiler().createLocalReference(ognlcontext,
                                      "((" + wrapClass.getName() + ")ognl.OgnlOps#convertValue($3," + wrapClass.getName()
                                      + ".class, true))." + OgnlTools.getPrimitiveValueGetter(wrapClass),
                                      param);

            } else if (param.isArray()) {
                                      conversion = OgnlRuntime.getCompiler().createLocalReference(ognlcontext,
                                      "(" + ExpressionCompiler.getCastString(param) + ")ognl.OgnlOps#toArray($3,"
                                      + param.getComponentType().getName() + ".class)",
                                      param);
            } else {
                                      conversion = OgnlRuntime.getCompiler().createLocalReference(ognlcontext,
                                      "(" + param.getName()+ ")ognl.OgnlOps#convertValue($3,"
                                      + param.getName()
                                      + ".class)",
                                      param);
            }


            ExpressionCompiler.addCastString(ognlcontext, "((" + type.getName() + ")");

            ognlcontext.setCurrentType(type);
            ognlcontext.setCurrentAccessor(ObjectProxy.class);

            return "."+m.getName()+"("+conversion+")";
        }
        return "";
    }

    /**
     * Sets up the context with the last property and last class
     * accessed.
     *
     * @param context
     * @param proxy
     */
    private void setupContext(Map context, ObjectProxy proxy) {
        OgnlContextState.setLastBeanClassAccessed(context, proxy.getLastClassAccessed());
        OgnlContextState.setLastBeanPropertyAccessed(context, proxy.getLastPropertyAccessed());
    }
}
