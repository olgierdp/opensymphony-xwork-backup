/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import ognl.DefaultTypeConverter;
import ognl.Evaluation;
import ognl.OgnlContext;
import ognl.TypeConverter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;

import java.lang.reflect.Member;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;


/**
 * OGNL TypeConverter for WebWork.
 *
 * @author <a href="mailto:plightbo@hotmail.com">Pat Lightbody</a>
 */
public class XWorkConverter extends DefaultTypeConverter {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static XWorkConverter instance;
    private static final Log LOG = LogFactory.getLog(XWorkConverter.class);

    //~ Instance fields ////////////////////////////////////////////////////////

    HashMap defaultMappings = new HashMap();
    HashMap mappings = new HashMap();
    HashSet noMapping = new HashSet();
    TypeConverter defaultTypeConverter = null;

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

    public void setDefaultConverter(TypeConverter defaultTypeConverter) {
        this.defaultTypeConverter = defaultTypeConverter;
    }

    public Object convertValue(Map context, Object object, Member member, String property, Object value, Class toClass) {
        if (value == null) {
            return null;
        }

        // allow this method to be called without any context
        // i.e. it can be called with as little as "Object value" and "Class toClass"
        if ((context != null) && (object != null)) {
            Class clazz = null;

            OgnlContext ognlContext = (OgnlContext) context;
            Evaluation eval = ognlContext.getCurrentEvaluation();

            if (eval == null) {
                eval = ognlContext.getLastEvaluation();

                // since the upgrade to ognl-2.6.3.jar, eval is null here
                // and this null check was being caoucht by an outer try/catch which ignored it !
                if (eval != null) {
                    clazz = eval.getResult().getClass();
                    property = (String) eval.getLastChild().getResult();
                }
            } else {
                clazz = eval.getLastChild().getSource().getClass();
                property = (String) eval.getFirstChild().getResult();
            }

            //	
            //
            // I refactored this bit, as when runtime exceptions were occuring within a custom TypeConverter
            // the clazz was being added to noMappings.
            //
            if (!noMapping.contains(clazz)) {
                TypeConverter tc = null;

                try {
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

                    tc = (TypeConverter) mapping.get(property);
                } catch (Throwable t) {
                    noMapping.add(clazz);
                }

                if (tc != null) {
                    return tc.convertValue(context, object, member, property, value, toClass);
                }
            }
        }

        //
        // Process the conversion using the default mappings, if one exists
        //
        if (defaultMappings.containsKey(toClass.getName())) {
            TypeConverter tc = null;

            if (toClass.equals(String.class) && !(value.getClass().equals(String.class) || value.getClass().equals(String[].class))) {
                // converting ToString from NON String 
                tc = (TypeConverter) defaultMappings.get(value.getClass().getName());
            } else {
                //	converting from String
                tc = (TypeConverter) defaultMappings.get(toClass.getName());
            }

            if (tc != null) {
                try {
                    return tc.convertValue(context, object, member, property, value, toClass);
                } catch (Exception e) {
                    if (LOG.isDebugEnabled()) {
                        LOG.error("Conversion failed", e);
                    }

                    return null;
                }
            }
        }

        if (defaultTypeConverter != null) {
            return defaultTypeConverter.convertValue(context, object, member, property, value, toClass);
        } else {
            return super.convertValue(context, object, member, property, value, toClass);
        }
    }

    public TypeConverter lookup(String className) {
        return (TypeConverter) defaultMappings.get(className);
    }

    public TypeConverter lookup(Class clazz) {
        return lookup(clazz.getName());
    }

    public void registerConverter(String className, TypeConverter converter) {
        defaultMappings.put(className, converter);
    }

    private TypeConverter createTypeConverter(String className) throws Exception, InstantiationException {
        Class conversionClass = Thread.currentThread().getContextClassLoader().loadClass(className);

        return (TypeConverter) conversionClass.newInstance();
    }

    private void loadConversionProps(String propsName) throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(propsName);
        Properties props = new Properties();
        props.load(is);

        for (Iterator iterator = props.entrySet().iterator();
                iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();

            try {
                defaultMappings.put(key, createTypeConverter((String) entry.getValue()));
            } catch (Exception e) {
                LOG.error("Conversion registration error", e);
            }
        }
    }
}
