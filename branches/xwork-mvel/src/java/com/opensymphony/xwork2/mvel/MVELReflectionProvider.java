package com.opensymphony.xwork2.mvel;

import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import com.opensymphony.xwork2.util.reflection.ReflectionException;
import com.opensymphony.xwork2.inject.Inject;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.Collection;

import org.mvel2.MVEL;
import org.mvel2.util.PropertyTools;

public class MVELReflectionProvider implements ReflectionProvider {
    private MVELUtil mvelUtil;

    @Inject
    public void setMvelUtil(MVELUtil mvelUtil) {
        this.mvelUtil = mvelUtil;
    }

    public Method getGetMethod(Class targetClass, String propertyName) throws IntrospectionException, ReflectionException {
        return mvelUtil.getGetMethod(targetClass, propertyName);
    }

    public Method getSetMethod(Class targetClass, String propertyName) throws IntrospectionException, ReflectionException {
        return mvelUtil.getSetMethod(targetClass, propertyName);
    }

    public Field getField(Class inClass, String name) {
        Member member = PropertyTools.getFieldOrAccessor(inClass, name);
        return (member instanceof Field) ? (Field) member : null;
    }

    public void setProperties(Map<String, String> props, Object o, Map<String, Object> context) {
        throw new UnsupportedOperationException();
    }

    public void setProperties(Map<String, String> props, Object o, Map<String, Object> context, boolean throwPropertyExceptions) throws ReflectionException {
        throw new UnsupportedOperationException();
    }

    public void setProperties(Map<String, String> properties, Object o) {
        throw new UnsupportedOperationException();
    }

    public PropertyDescriptor getPropertyDescriptor(Class targetClass, String propertyName) throws IntrospectionException, ReflectionException {
        return mvelUtil.getPropertyDescriptor(targetClass, propertyName);
    }

    public void copy(Object from, Object to, Map<String, Object> context, Collection<String> exclusions, Collection<String> inclusions) {
        throw new UnsupportedOperationException();
    }

    public Object getRealTarget(String property, Map<String, Object> context, Object root) throws ReflectionException {
        return root;
    }

    public void setProperty(String name, Object value, Object o, Map<String, Object> context, boolean throwPropertyExceptions) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setProperty(String name, Object value, Object o, Map<String, Object> context) {
        throw new UnsupportedOperationException();
    }

    public Map<String, Object> getBeanMap(Object source) throws IntrospectionException, ReflectionException {
        throw new UnsupportedOperationException();
    }

    public Object getValue(String expression, Map<String, Object> context, Object root) throws ReflectionException {
        throw new UnsupportedOperationException();
    }

    public void setValue(String expression, Map<String, Object> context, Object root, Object value) throws ReflectionException {
        mvelUtil.setProperty(root, expression, context, value);
    }

    public PropertyDescriptor[] getPropertyDescriptors(Object source) throws IntrospectionException {
        return mvelUtil.loadPropertyDescriptors(source.getClass());
    }
}
