/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import ognl.Ognl;
import ognl.OgnlException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Utility class that provides common access to the Ognl APIs for
 * setting and getting properties from objects (usually Actions).
 *
 * @author $Author$
 * @version $Revision$
 */
public class OgnlUtil {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(OgnlUtil.class);
    private static HashMap expressions = new HashMap();

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
    * Sets the object's properties using the default type converter.
    *
    * @param props the properties being set
    * @param o the object
    * @param context the action context
    */
    public static void setProperties(Map props, Object o, Map context) {
        if (props == null) {
            return;
        }

        Ognl.setTypeConverter(context, DefaultConverter.getInstance());

        Object oldRoot = Ognl.getRoot(context);
        Ognl.setRoot(context, o);

        for (Iterator iterator = props.entrySet().iterator();
                iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String expression = (String) entry.getKey();

            try {
                Ognl.setValue(compile(expression), context, o, entry.getValue());
            } catch (OgnlException e) {
                // ignore, this happens a lot
            }
        }

        Ognl.setRoot(context, oldRoot);
    }

    /**
    * Sets the properties on the object using the default context
    * @param properties
    * @param o
    */
    public static void setProperties(Map properties, Object o) {
        Map context = Ognl.createDefaultContext(o);
        Ognl.setTypeConverter(context, DefaultConverter.getInstance());
        setProperties(properties, o, context);
    }

    /**
    * Sets the named property to the supplied value on the Object
    * @param name the name of the property to be set
    * @param value the value to set into the named property
    * @param o the object upon which to set the property
    * @param context the context which may include the TypeConverter
    */
    public static void setProperty(String name, Object value, Object o, Map context) {
        Ognl.setTypeConverter(context, DefaultConverter.getInstance());

        Object oldRoot = Ognl.getRoot(context);
        Ognl.setRoot(context, o);

        try {
            Ognl.setValue(compile(name), context, o, value);
        } catch (OgnlException e) {
            // ignore, this happens a lot
        }

        Ognl.setRoot(context, oldRoot);
    }

    public static Object compile(String expression) throws OgnlException {
        Object o = null;

        o = expressions.get(expression);

        if (o == null) {
            synchronized (expressions) {
                o = expressions.get(expression);

                if (o == null) {
                    o = Ognl.parseExpression(expression);
                    expressions.put(expression, o);
                }
            }
        }

        return o;
    }

    /**
    * Copies the properties in the object "from" and sets them in the object "to"
    * using specified type converter, or {@link com.opensymphony.xwork.util.DefaultConverter} if none is specified.
    *
    * @param from the source object
    * @param to the target object
    * @param context the action context we're running under
    */
    public static void copy(Object from, Object to, Map context) {
        Map contextFrom = Ognl.createDefaultContext(from);
        Ognl.setTypeConverter(contextFrom, DefaultConverter.getInstance());

        Map contextTo = Ognl.createDefaultContext(to);
        Ognl.setTypeConverter(contextTo, DefaultConverter.getInstance());

        BeanInfo beanInfoFrom = null;

        try {
            beanInfoFrom = Introspector.getBeanInfo(from.getClass(), Object.class);
        } catch (IntrospectionException e) {
            log.error("An error occured", e);

            return;
        }

        PropertyDescriptor[] pds = beanInfoFrom.getPropertyDescriptors();

        for (int i = 0; i < pds.length; i++) {
            PropertyDescriptor pd = pds[i];

            try {
                Object expr = compile(pd.getName());
                Object value = Ognl.getValue(expr, contextFrom, from);
                Ognl.setValue(expr, contextTo, to, value);
            } catch (OgnlException e) {
                // ignore, this is OK
            }
        }
    }
}
