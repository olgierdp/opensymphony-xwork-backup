/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import ognl.DefaultTypeConverter;
import ognl.Evaluation;
import ognl.OgnlContext;
import ognl.TypeConverter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Member;
import java.util.*;


/**
 * OGNL TypeConverter for WebWork.
 *
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 */
public class XWorkConverter extends DefaultTypeConverter {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static XWorkConverter instance;

    //~ Instance fields ////////////////////////////////////////////////////////

    HashMap mappings = new HashMap();
    HashSet noMapping = new HashSet();
    HashMap defaultMappings = new HashMap();

    //~ Constructors ///////////////////////////////////////////////////////////

    private XWorkConverter() {
        try {
            loadConversionProps("xwork-default-conversion.properties");
        } catch (Exception e) {
        }

        try {
            loadConversionProps("xwork-conversion.properties");
        } catch (Exception e) {
        }
    }

    private void loadConversionProps(String propsName) throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(propsName);
        Properties props = new Properties();
        props.load(is);

        for (Iterator iterator = props.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();

            defaultMappings.put(key, createTypeConverter((String) entry.getValue()));
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static XWorkConverter getInstance() {
        if (instance == null) {
            instance = new XWorkConverter();
        }

        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    public Object convertValue(Map context, Object value, Member member, String s, Object o1, Class aClass) {
        if (value == null) {
            return null;
        }

        Class clazz = null;
        try {
            OgnlContext ognlContext = (OgnlContext) context;
            Evaluation eval = ognlContext.getCurrentEvaluation();
            String property;

            if (eval == null) {
                eval = ognlContext.getLastEvaluation();
                clazz = eval.getResult().getClass();
                property = (String) eval.getLastChild().getResult();
            } else {
                clazz = eval.getLastChild().getSource().getClass();
                property = (String) eval.getFirstChild().getResult();
            }

            if (!noMapping.contains(clazz)) {
                Map mapping = (Map) mappings.get(clazz);

                if (mapping == null) {
                    mapping = new HashMap();
                    mappings.put(clazz, mapping);

                    String className = clazz.getName();
                    String resource = className.replace('.', '/') + "-conversion.properties";
                    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);

                    Properties props = new Properties();
                    props.load(is);
                    mapping.putAll(props);

                    for (Iterator iterator = mapping.entrySet().iterator();
                         iterator.hasNext();) {
                        Map.Entry entry = (Map.Entry) iterator.next();
                        entry.setValue(createTypeConverter((String) entry.getValue()));
                    }
                }

                TypeConverter converter = (TypeConverter) mapping.get(property);

                if (converter != null) {
                    return converter.convertValue(context, value, member, s, o1, aClass);
                }
            }
        } catch (Throwable t) {
            if (clazz != null) {
                noMapping.add(clazz);
            }
        }

        if (defaultMappings.containsKey(aClass.getName())) {
            try {
                TypeConverter tc = (TypeConverter) defaultMappings.get(aClass.getName());
                return tc.convertValue(context, value, member, s, o1, aClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return super.convertValue(context, value, member, s, o1, aClass);
    }

    private TypeConverter createTypeConverter(String className) throws Exception, InstantiationException {
        Class conversionClass = Thread.currentThread().getContextClassLoader().loadClass(className);

        return (TypeConverter) conversionClass.newInstance();
    }

}
