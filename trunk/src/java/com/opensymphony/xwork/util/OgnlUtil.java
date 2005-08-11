/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import ognl.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Utility class that provides common access to the Ognl APIs for
 * setting and getting properties from objects (usually Actions).
 *
 * @author Jason Carreira
 */
public class OgnlUtil {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(OgnlUtil.class);
    private static HashMap expressions = new HashMap();
    private static HashMap beanInfoCache = new HashMap();

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Sets the object's properties using the default type converter, defaulting to not throw
     * exceptions for problems setting the properties.
     *
     * @param props   the properties being set
     * @param o       the object
     * @param context the action context
     */
    public static void setProperties(Map props, Object o, Map context) {
        setProperties(props, o, context, false);
    }

    /**
     * Sets the object's properties using the default type converter.
     *
     * @param props                   the properties being set
     * @param o                       the object
     * @param context                 the action context
     * @param throwPropertyExceptions boolean which tells whether it should throw exceptions for
     *                                problems setting the properties
     */
    public static void setProperties(Map props, Object o, Map context, boolean throwPropertyExceptions) {
        if (props == null) {
            return;
        }

        Ognl.setTypeConverter(context, XWorkConverter.getInstance());

        Object oldRoot = Ognl.getRoot(context);
        Ognl.setRoot(context, o);

        for (Iterator iterator = props.entrySet().iterator();
             iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String expression = (String) entry.getKey();

            internalSetProperty(expression, entry.getValue(), o, context, throwPropertyExceptions);
        }

        Ognl.setRoot(context, oldRoot);
    }

    /**
     * Sets the properties on the object using the default context, defaulting to not throwing
     * exceptions for problems setting the properties.
     *
     * @param properties
     * @param o
     */
    public static void setProperties(Map properties, Object o) {
        setProperties(properties, o, false);
    }

    /**
     * Sets the properties on the object using the default context.
     *
     * @param properties              the property map to set on the object
     * @param o                       the object to set the properties into
     * @param throwPropertyExceptions boolean which tells whether it should throw exceptions for
     *                                problems setting the properties
     */
    public static void setProperties(Map properties, Object o, boolean throwPropertyExceptions) {
        Map context = Ognl.createDefaultContext(o);
        setProperties(properties, o, context, throwPropertyExceptions);
    }

    /**
     * Sets the named property to the supplied value on the Object, defaults to not throwing
     * property exceptions.
     *
     * @param name    the name of the property to be set
     * @param value   the value to set into the named property
     * @param o       the object upon which to set the property
     * @param context the context which may include the TypeConverter
     */
    public static void setProperty(String name, Object value, Object o, Map context) {
        setProperty(name, value, o, context, false);
    }

    /**
     * Sets the named property to the supplied value on the Object.
     *
     * @param name                    the name of the property to be set
     * @param value                   the value to set into the named property
     * @param o                       the object upon which to set the property
     * @param context                 the context which may include the TypeConverter
     * @param throwPropertyExceptions boolean which tells whether it should throw exceptions for
     *                                problems setting the property
     */
    public static void setProperty(String name, Object value, Object o, Map context, boolean throwPropertyExceptions) {
        Ognl.setTypeConverter(context, XWorkConverter.getInstance());

        Object oldRoot = Ognl.getRoot(context);
        Ognl.setRoot(context, o);

        internalSetProperty(name, value, o, context, throwPropertyExceptions);

        Ognl.setRoot(context, oldRoot);
    }

    /**
     * Looks for the real target with the specified property given a root Object which may be a
     * CompoundRoot.
     *
     * @return the real target or null if no object can be found with the specified property
     */
    public static Object getRealTarget(String property, Map context, Object root) throws OgnlException {
        //special keyword, they must be cutting the stack
        if ("top".equals(property)) {
            return root;
        }

        if (root instanceof CompoundRoot) {
            // find real target
            CompoundRoot cr = (CompoundRoot) root;

            try {
                for (Iterator iterator = cr.iterator(); iterator.hasNext();) {
                    Object target = iterator.next();

                    if (
                            OgnlRuntime.hasSetProperty((OgnlContext) context, target, property)
                            ||
                            OgnlRuntime.hasGetProperty((OgnlContext) context, target, property)
                            ||
                            OgnlRuntime.getIndexedPropertyType((OgnlContext) context, target.getClass(), property) != OgnlRuntime.INDEXED_PROPERTY_NONE
                            ) {
                        return target;
                    }
                }
            } catch (IntrospectionException ex) {
                throw new OgnlException("Cannot figure out real target class", ex);
            }

            return null;
        }

        return root;
    }

    private static Object translateCompoundRoot(String name, Map context,
                                                Object root, String hintProperty, Object hintTarget)
            throws OgnlException {

        if (!(root instanceof CompoundRoot))
            return root;

        int length = name.length();
        int bracketPosition = -1;

        // This loop tests if it's potential candidate for indexed property
        for (int i = 0; i < length; i++) {
            if (i > 0 && name.charAt(i) == '[') {
                bracketPosition = i;
                break;
            }
            if (!Character.isJavaIdentifierPart(name.charAt(i)))
                break;

        }

        if (bracketPosition > 0) {
            String property = name.substring(0, bracketPosition);

            //reduce cost of real target lookup if we already know it
            if (hintProperty != null && property.equals(hintProperty))
                return hintTarget;

            return getRealTarget(property, context, root);
        }

        return root;
    }


    /**
     * Wrapper around Ognl.setValue() to handle type conversion for collection elements.
     * Ideally, this should be handled by OGNL directly.
     */
    public static void setValue(String name, Map context, Object root, Object value) throws OgnlException {
    	OgnlContextState.clearCurrentPropertyPath(context);
        String property = null;
        Object target = null;
        if (name.endsWith("]")) {
            property = name.substring(0, name.lastIndexOf("["));
            target = getRealTarget(property, context, root);

            if (target != null) {
                Class memberType = (Class) XWorkConverter.getInstance().getObjectTypeDeterminer().getElementClass(target.getClass(), property, null);

                if (memberType != null) {
                    TypeConverter converter = Ognl.getTypeConverter(context);
                    value = converter.convertValue(context, target, null, property, value, memberType);
                }
            }
        }
        //translate compound root is to extract roots object when it's probable that it deals with
        //indexed properties, cause Ognl cannot detected indexed properties on Compound object

        Ognl.setValue(compile(name), context, translateCompoundRoot(name, context, root, property, target), value);

    }

    public static Object getValue(String name, Map context, Object root) throws OgnlException {
        return Ognl.getValue(compile(name), context, translateCompoundRoot(name, context, root, null, null));
    }

    public static Object getValue(String name, Map context, Object root, Class resultType) throws OgnlException {
        return Ognl.getValue(compile(name), context, translateCompoundRoot(name, context, root, null, null), resultType);
    }


    public static Object compile(String expression) throws OgnlException {
        synchronized(expressions) {
            Object o = expressions.get(expression);

            if (o == null) {
                o = Ognl.parseExpression(expression);
                expressions.put(expression, o);
            }

            return o;
        }
    }

    /**
     * Copies the properties in the object "from" and sets them in the object "to"
     * using specified type converter, or {@link com.opensymphony.xwork.util.XWorkConverter} if none
     * is specified.
     *
     * @param from       the source object
     * @param to         the target object
     * @param context    the action context we're running under
     * @param exclusions collection of method names to excluded from copying ( can be null)
     * @param inclusions collection of method names to included copying  (can be null)
     *                   note if exclusions AND inclusions are supplied and not null nothing will get copied.
     */
    public static void copy(Object from, Object to, Map context,  Collection exclusions, Collection inclusions) {
        Map contextFrom = Ognl.createDefaultContext(from);
        Ognl.setTypeConverter(contextFrom, XWorkConverter.getInstance());
        Map contextTo = Ognl.createDefaultContext(to);
        Ognl.setTypeConverter(contextTo, XWorkConverter.getInstance());

        PropertyDescriptor[] fromPds;
        PropertyDescriptor[] toPds;

        try {
            fromPds = getPropertyDescriptors(from);
            toPds = getPropertyDescriptors(to);
        } catch (IntrospectionException e) {
            log.error("An error occured", e);

            return;
        }

        Map toPdHash = new HashMap();

        for (int i = 0; i < toPds.length; i++) {
            PropertyDescriptor toPd = toPds[i];
            toPdHash.put(toPd.getName(), toPd);
        }

        for (int i = 0; i < fromPds.length; i++) {
            PropertyDescriptor fromPd = fromPds[i];
            if (fromPd.getReadMethod() != null) {
                boolean copy = true;
                if (exclusions != null && exclusions.contains(fromPd.getName())) {
                    copy = false;
                } else if (inclusions != null && !inclusions.contains(fromPd.getName())) {
                    copy = false;
                }

                if (copy == true) {
                    PropertyDescriptor toPd = (PropertyDescriptor) toPdHash.get(fromPd.getName());
                    if ((toPd != null) && (toPd.getWriteMethod() != null)) {
                        try {
                            Object expr = OgnlUtil.compile(fromPd.getName());
                            Object value = Ognl.getValue(expr, contextFrom, from);
                            Ognl.setValue(expr, contextTo, to, value);
                        } catch (OgnlException e) {
                            // ignore, this is OK
                        }
                    }

                }

            }

        }
    }


    /**
     * Copies the properties in the object "from" and sets them in the object "to"
     * using specified type converter, or {@link com.opensymphony.xwork.util.XWorkConverter} if none
     * is specified.
     *
     * @param from    the source object
     * @param to      the target object
     * @param context the action context we're running under
     */
    public static void copy(Object from, Object to, Map context) {
        OgnlUtil.copy(from, to, context, null, null);
    }

    public static PropertyDescriptor[] getPropertyDescriptors(Object source) throws IntrospectionException {
        BeanInfo beanInfo = getBeanInfo(source);
        return beanInfo.getPropertyDescriptors();
    }

    public static Map getBeanMap(Object source) throws IntrospectionException, OgnlException {
        Map beanMap = new HashMap();
        Map sourceMap = Ognl.createDefaultContext(source);
        PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(source);
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
            String propertyName = propertyDescriptor.getDisplayName();
            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod != null) {
                Object expr = OgnlUtil.compile(propertyName);
                Object value = Ognl.getValue(expr, sourceMap, source);
                beanMap.put(propertyName, value);
            } else {
                beanMap.put(propertyName, "There is no read method for " + propertyName);
            }
        }
        return beanMap;
    }

    public static BeanInfo getBeanInfo(Object from) throws IntrospectionException {
        synchronized(beanInfoCache) {
            BeanInfo beanInfo;
            beanInfo = (BeanInfo) beanInfoCache.get(from.getClass());
            if (beanInfo == null) {
                beanInfo = Introspector.getBeanInfo(from.getClass(), Object.class);
                beanInfoCache.put(from.getClass(), beanInfo);
            }
            return beanInfo;
        }
    }

    static void internalSetProperty(String name, Object value, Object o, Map context, boolean throwPropertyExceptions) {
        try {
            setValue(name, context, o, value);
        } catch (OgnlException e) {
            Throwable reason = e.getReason();
            String msg = "Caught OgnlException while setting property '" + name + "' on type '" + o.getClass().getName() + "'.";
            Throwable exception = (reason == null) ? e : reason;

            if (throwPropertyExceptions) {
                log.error(msg, exception);
                throw new RuntimeException(msg);
            } else {
                log.warn(msg, exception);
            }
        }
    }
}
