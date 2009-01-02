package com.opensymphony.xwork2.mvel;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.reflection.ReflectionException;
import com.opensymphony.xwork2.mvel.accessors.DefaultVariableResolverFactory;

import java.beans.*;
import java.util.WeakHashMap;
import java.util.Map;
import java.util.List;
import java.util.Collection;
import java.util.HashMap;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.Serializable;

import org.mvel2.integration.PropertyHandler;
import org.mvel2.integration.PropertyHandlerFactory;
import org.mvel2.MVEL;
import org.mvel2.PropertyAccessor;
import org.mvel2.util.PropertyTools;

public class MVELUtil {
    protected WeakHashMap<Class, HashMap<String, PropertyDescriptor>> descriptorsCache = new WeakHashMap<Class, HashMap<String, PropertyDescriptor>>();

    public Method getSetMethod(Class targetClass, String propertyName) throws IntrospectionException, ReflectionException {
        //TODO : this stinks
        if (Map.class.isAssignableFrom(targetClass))
            return getMethod(targetClass, "put", Object.class, Object.class);
        else if (List.class.isAssignableFrom(targetClass))
            return getMethod(targetClass, "add", int.class, Object.class);
        else if (Collection.class.isAssignableFrom(targetClass))
            return getMethod(targetClass, "add", Object.class);

        return PropertyTools.getSetter(targetClass, propertyName);
    }

    public Method getGetMethod(Class targetClass, String propertyName) throws IntrospectionException, ReflectionException {
        //TODO : this stinks
        if (Map.class.isAssignableFrom(targetClass))
            return getMethod(targetClass, "get", Object.class);
        else if (List.class.isAssignableFrom(targetClass))
            return getMethod(targetClass, "get", int.class);

        return PropertyTools.getGetter(targetClass, propertyName);
    }

    private Method getMethod(Class clazz, String name, Class... args) {
        try {
            return clazz.getMethod(name, args);
        } catch (NoSuchMethodException e) {
            //cannot happen
        }
        return null;
    }

    public void setProperty(Object target, String expr, Map<String, Object> context, Object value) {
        MVELContext realContex = (context instanceof MVELContext) ? (MVELContext) context : new MVELContext(context);
        MVEL.executeSetExpression(MVEL.compileSetExpression(expr), target, new DefaultVariableResolverFactory(realContex), value);
    }

    public Object getProperty(Object target, Serializable expr, Map<String, Object> context) {
        MVELContext realContex = (context instanceof MVELContext) ? (MVELContext) context : new MVELContext(context);
        return MVEL.executeExpression(expr, target, new DefaultVariableResolverFactory(realContex));
    }

    public PropertyDescriptor[] loadPropertyDescriptors(Class beanClass) {
        // Look up any cached descriptors for this bean class
        HashMap<String, PropertyDescriptor> descriptors = descriptorsCache.get(beanClass);
        if (descriptors != null)
            return (PropertyDescriptor[]) descriptors.values().toArray(new PropertyDescriptor[0]);

        // Introspect the bean and cache the generated descriptors
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(beanClass);
        } catch (IntrospectionException e) {
            return (new PropertyDescriptor[0]);
        }
        PropertyDescriptor[] descriptorsArray = beanInfo.getPropertyDescriptors();
        if (descriptorsArray != null) {
            descriptors = new HashMap<String, PropertyDescriptor>();
            for (PropertyDescriptor propertyDescriptor : descriptorsArray)
                descriptors.put(propertyDescriptor.getName(), propertyDescriptor);
            descriptorsCache.put(beanClass, descriptors);
        }

        return descriptorsArray;
    }

    public PropertyDescriptor getPropertyDescriptor(Class beanClass, String name) {
        //make sure properties descriptors are loaded
        loadPropertyDescriptors(beanClass);
        HashMap<String, PropertyDescriptor> descriptors = descriptorsCache.get(beanClass);
        return descriptors != null ? descriptors.get(name) : null;
    }
}
