/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.mvel.accessors;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.mvel.MVELContext;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.accessor.XWorkCollectionPropertyAccessor;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import com.opensymphony.xwork2.util.TextUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.mvel2.integration.PropertyHandler;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.optimizers.impl.asm.ProducesBytecode;
import org.mvel2.asm.MethodVisitor;

public class MVELListPropertyAccessor implements PropertyHandler, ProducesBytecode {

    private XWorkCollectionPropertyAccessor _sAcc = new XWorkCollectionPropertyAccessor();

    private XWorkConverter xworkConverter;
    private ObjectFactory objectFactory;
    private ObjectTypeDeterminer objectTypeDeterminer;
    private OgnlUtil ognlUtil;

    @Inject
    public void setXWorkConverter(XWorkConverter conv) {
        this.xworkConverter = conv;
    }

    @Inject
    public void setObjectFactory(ObjectFactory fac) {
        this.objectFactory = fac;
    }

    @Inject
    public void setObjectTypeDeterminer(ObjectTypeDeterminer ot) {
        this.objectTypeDeterminer = ot;
    }

    public Object getProperty(String name, Object contextObj, VariableResolverFactory variableFactory) {
        DefaultVariableResolverFactory defaultFactory = (DefaultVariableResolverFactory) variableFactory;
        MVELContext context = defaultFactory.getContext();
        List list = (List) contextObj;

        Class lastClass = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
        String lastProperty = (String) context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);

        Long numericValue = null;
        try {
            numericValue = Long.valueOf(name);
        } catch (NumberFormatException e) {
            //ignore
        }
        
        if (numericValue != null
                && ReflectionContextState.isCreatingNullObjects(context)
                && objectTypeDeterminer.shouldCreateIfNew(lastClass, lastProperty, contextObj, null, true)) {
            int index = numericValue.intValue();
            int listSize = list.size();

            /*if (lastClass == null || lastProperty == null) {
                return super.getProperty(context, target, name);
            }*/
            Class beanClass = objectTypeDeterminer.getElementClass(lastClass, lastProperty, name);
            if (listSize <= index) {
                Object result = null;

                for (int i = listSize; i < index; i++) {
                    list.add(null);
                }
                try {
                    list.add(index, result = objectFactory.buildBean(beanClass, context));
                } catch (Exception exc) {
                    throw new XWorkException(exc);
                }
                return result;
            } else if (list.get(index) == null) {
                Object result = null;
                try {
                    list.set(index, result = objectFactory.buildBean(beanClass, context));
                } catch (Exception exc) {
                    throw new XWorkException(exc);
                }
                return result;
            } else {
                return list.get(index);
            }
        }

        return null;
    }

    public Object setProperty(String name, Object target, VariableResolverFactory variableFactory, Object value) {
        DefaultVariableResolverFactory defaultFactory = (DefaultVariableResolverFactory) variableFactory;
        MVELContext context = defaultFactory.getContext();

        Class lastClass = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
        String lastProperty = (String) context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);
        Class convertToClass = objectTypeDeterminer.getElementClass(lastClass, lastProperty, name);

        if (name instanceof String && value.getClass().isArray()) {
            // looks like the input game in the form of "someList.foo" and
            // we are expected to define the index values ourselves.
            // So let's do it:

            Collection c = (Collection) value;
            Object[] values = (Object[]) value;
            for (Object v : values) {
                try {
                    Object o = objectFactory.buildBean(convertToClass, context);
                    ognlUtil.setValue((String) name, context, o, v);
                    c.add(o);
                } catch (Exception e) {
                    throw new XWorkException("Error converting given String values for Collection.", e);
                }
            }

            // we don't want to do the normal list property setting now, since we've already done the work
            // just return instead
            return null;
        }

        Object realValue = getRealValue(context, value, convertToClass);

        Long numericValue = null;
        try {
            numericValue = Long.valueOf(name);
        } catch (NumberFormatException e) {
            //ignore
        }

        if (target instanceof List && numericValue != null) {
            //make sure there are enough spaces in the List to set
            List list = (List) target;
            int listSize = list.size();
            int count = numericValue.intValue();
            if (count >= listSize) {
                for (int i = listSize; i <= count; i++) {
                    list.add(null);
                }
            }
        }

        return null;
    }

    private Object getRealValue(Map context, Object value, Class convertToClass) {
        if (value == null || convertToClass == null) {
            return value;
        }
        return xworkConverter.convertValue(context, value, convertToClass);
    }

    public void produceBytecodeGet(MethodVisitor mv, String propertyName, VariableResolverFactory factory) {
    }

    public void produceBytecodePut(MethodVisitor mv, String propertyName, VariableResolverFactory factory) {
    }
}
