/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.util.TextUtils;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.XworkException;

import ognl.DefaultTypeConverter;
import ognl.Ognl;
import ognl.TypeConverter;

import java.lang.reflect.Array;
import java.lang.reflect.Member;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.*;


/**
 * Default type converter for XWork.
 *
 * @author <a href="mailto:plightbo@cisco.com">Pat Lightbody</a>
 */
public class XWorkBasicConverter extends DefaultTypeConverter {
    //~ Methods ////////////////////////////////////////////////////////////////

    public Object convertValue(Map context, Object o, Member member, String s, Object value, Class toType) {
        Object result = null;

        if (toType == String.class) {
            result = doConvertToString(context, value);
        } else if (toType == boolean.class) {
            result = doConvertToBoolean(value);
        } else if (toType == Boolean.class) {
            result = doConvertToBoolean(value);
        } else if (toType.isArray()) {
            result = doConvertToArray(context, o, member, s, value, toType);
        } else if (toType == Date.class) {
            result = doConvertToDate(context, value);
        } else if (Collection.class.isAssignableFrom(toType)) {
            result = doConvertToCollection(context, o, member, s, value, toType);
        } else if (toType == Class.class) {
            result = doConvertToClass(value);
        }

        if (result == null) {
            if (value instanceof Object[]) {
                Object[] array = (Object[]) value;

                if (array.length >= 1) {
                    value = array[0];
                }

                // let's try to convert the first element only
                result = this.convertValue(context, o, member, s, value, toType);
            } else {
                result = super.convertValue(context, value, toType);
            }
        }

        return result;
    }

    private Locale getLocale(Map context) {
        Locale locale = (Locale) context.get(ActionContext.LOCALE);

        if (locale == null) {
            locale = Locale.getDefault();
        }

        return locale;
    }

    private Object doConvertToArray(Map context, Object o, Member member, String s, Object value, Class toType) {
        Object result = null;
        Class componentType = toType.getComponentType();

        if (componentType != null) {
            TypeConverter converter = Ognl.getTypeConverter(context);

            if (value.getClass().isArray()) {
                int length = Array.getLength(value);
                result = Array.newInstance(componentType, length);

                for (int i = 0; i < length; i++) {
                    Object valueItem = Array.get(value, i);
                    Array.set(result, i, converter.convertValue(context, o, member, s, valueItem, componentType));
                }
            } else {
                result = Array.newInstance(componentType, 1);
                Array.set(result, 0, converter.convertValue(context, o, member, s, value, componentType));
            }
        }

        return result;
    }

    private Object doConvertToBoolean(Object value) {
        if (value instanceof String) {
            String bStr = (String) value;

            return Boolean.valueOf(bStr);
        }

        return null;
    }

    private Class doConvertToClass(Object value) {
        Class clazz = null;

        if (value instanceof String) {
            try {
                clazz = Class.forName((String) value);
            } catch (ClassNotFoundException e) {
                throw new XworkException(e.getLocalizedMessage(), e);
            }
        }

        return clazz;
    }

    private Collection doConvertToCollection(Map context, Object o, Member member, String prop, Object value, Class toType) {
        Collection result = null;
        Class memberType = String.class;

        if (o != null) {
            memberType = (Class) XWorkConverter.getInstance().getConverter(o.getClass(), "Collection_" + prop);

            if (memberType == null) {
                memberType = String.class;
            }
        }

        if (value instanceof String[]) {
            String[] sa = (String[]) value;

            if (toType == Set.class) {
                result = new HashSet(sa.length);
            } else if (toType == SortedSet.class) {
                result = new TreeSet();
            } else {
                result = new XWorkList(memberType, sa.length);
            }

            TypeConverter converter = Ognl.getTypeConverter(context);

            for (int i = 0; i < sa.length; i++) {
                result.add(converter.convertValue(context, o, member, prop, sa[i], memberType));
            }
        } else if (toType.isAssignableFrom(value.getClass())) {
            result = (Collection) value;
        } else if (Collection.class.isAssignableFrom(value.getClass())) {
            Collection col = (Collection) value;

            if (toType == Set.class) {
                result = new HashSet(col.size());
            } else if (toType == SortedSet.class) {
                result = new TreeSet();
            } else {
                result = new XWorkList(memberType, col.size());
            }

            TypeConverter converter = Ognl.getTypeConverter(context);

            for (Iterator it = col.iterator(); it.hasNext();) {
                result.add(converter.convertValue(context, o, member, prop, it.next(), memberType));
            }
        }

        return result;
    }

    private Object doConvertToDate(Map context, Object value) {
        Date result = null;

        if (value instanceof String) {
            String sa = (String) value;
            Locale locale = getLocale(context);

            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);

            try {
                result = df.parse(sa);
            } catch (ParseException e) {
                throw new XworkException("Could not parse date", e);
            }
        } else if (Date.class.isAssignableFrom(value.getClass())) {
            result = (Date) value;
        }

        return result;
    }

    private String doConvertToString(Map context, Object value) {
        String result = null;

        if (value instanceof int[]) {
            int[] x = (int[]) value;
            List intArray = new ArrayList(x.length);

            for (int i = 0; i < x.length; i++) {
                intArray.add(new Integer(x[i]));
            }

            result = TextUtils.join(", ", intArray);
        } else if (value instanceof long[]) {
            long[] x = (long[]) value;
            List intArray = new ArrayList(x.length);

            for (int i = 0; i < x.length; i++) {
                intArray.add(new Long(x[i]));
            }

            result = TextUtils.join(", ", intArray);
        } else if (value instanceof double[]) {
            double[] x = (double[]) value;
            List intArray = new ArrayList(x.length);

            for (int i = 0; i < x.length; i++) {
                intArray.add(new Double(x[i]));
            }

            result = TextUtils.join(", ", intArray);
        } else if (value instanceof boolean[]) {
            boolean[] x = (boolean[]) value;
            List intArray = new ArrayList(x.length);

            for (int i = 0; i < x.length; i++) {
                intArray.add(Boolean.valueOf(x[i]));
            }

            result = TextUtils.join(", ", intArray);
        } else if (value instanceof Date) {
            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, getLocale(context));
            result = df.format(value);
        } else if (value instanceof String[]) {
            result = TextUtils.join(", ", (String[]) value);
        }

        return result;
    }
}
