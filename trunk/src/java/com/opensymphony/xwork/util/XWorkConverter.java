/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.ActionContext;

import ognl.*;

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

    public Object convertValue(Map context, Object target, Member member, String property, Object value, Class toClass) {
        //
        // Process the conversion using the default mappings, if one exists
        //
        TypeConverter tc = null;

        // allow this method to be called without any context
        // i.e. it can be called with as little as "Object value" and "Class toClass"
        if (target != null) {
            Class clazz = null;

            clazz = target.getClass();

            // this is to handle weird issues with setValue with a different type
            if ((target instanceof CompoundRoot) && (context != null)) {
                OgnlContext ognlContext = (OgnlContext) context;
                Evaluation eval = ognlContext.getCurrentEvaluation();
                if (eval == null) {
                    eval = ognlContext.getLastEvaluation();
                }

                if (eval != null && eval.getLastChild() != null) {
                    // since we changed what the source was (tricked Ognl essentially)
                    if (eval.getLastChild().getLastChild() != null
                            && eval.getLastChild().getLastChild().getSource() != null
                            && eval.getLastChild().getLastChild().getSource().getClass() != CompoundRoot.class) {
                        clazz = eval.getLastChild().getLastChild().getSource().getClass();
                    } else {
                        clazz = eval.getLastChild().getSource().getClass();
                    }

                    // ugly hack getting the property, but it works
                    property = eval.getNode().jjtGetChild(eval.getNode().jjtGetNumChildren() - 1).toString();

                    if (property.startsWith("\"") && property.endsWith("\"")) {
                        property = property.substring(1, property.length() - 1);
                    }
                }
            }

            if (!noMapping.contains(clazz)) {
                try {
                    Map mapping = (Map) mappings.get(clazz);

                    if (mapping == null) {
                        mapping = new HashMap();
                        mappings.put(clazz, mapping);

                        String className = clazz.getName();
                        String resource = className.replace('.', '/') + "-conversion.properties";
                        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);

                        if (is != null) {
                            Properties props = new Properties();
                            props.load(is);
                            mapping.putAll(props);

                            for (Iterator iterator = mapping.entrySet().iterator();
                                    iterator.hasNext();) {
                                Map.Entry entry = (Map.Entry) iterator.next();
                                entry.setValue(createTypeConverter((String) entry.getValue()));
                            }
                        } else {
                            noMapping.add(clazz);
                        }
                    }

                    tc = (TypeConverter) mapping.get(property);
                } catch (Throwable t) {
                    noMapping.add(clazz);
                }
            }
        }

        if (tc == null) {
            if (toClass.equals(String.class) && value != null && !(value.getClass().equals(String.class) || value.getClass().equals(String[].class))) {
                // when converting to a string, use the source target's class's converter
                if (defaultMappings.containsKey(value.getClass().getName())) {
                    tc = (TypeConverter) defaultMappings.get(value.getClass().getName());
                }
            } else {
                // when converting from a string, use the toClass's converter
                if (defaultMappings.containsKey(toClass.getName())) {
                    //	converting from String
                    tc = (TypeConverter) defaultMappings.get(toClass.getName());
                }
            }
        }

        if (tc != null) {
            try {
                return tc.convertValue(context, target, member, property, value, toClass);
            } catch (Exception e) {
                handleConversionException(property, value, target);

                return null;
            }
        }

        if (defaultTypeConverter != null) {
            try {
                return defaultTypeConverter.convertValue(context, target, member, property, value, toClass);
            } catch (Exception e) {
                handleConversionException(property, value, target);

                return null;
            }
        } else {
            try {
                return super.convertValue(context, target, member, property, value, toClass);
            } catch (Exception e) {
                handleConversionException(property, value, target);

                return null;
            }
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

    protected void handleConversionException(String property, Object value, Object object) {
        if (ActionContext.getContext().get("report.conversion.errors") == Boolean.TRUE) {
            String defaultMessage = "Invalid field value for field \"" + property + "\".";
            OgnlValueStack stack = ActionContext.getContext().getValueStack();

            String getTextExpression = "getText('invalid.fieldvalue." + property + "','" + defaultMessage + "')";
            String message = (String) stack.findValue(getTextExpression);

            if (message == null) {
                message = defaultMessage;
            }

            String addFieldErrorExpression = "addFieldError('" + property + "','" + message + "')";
            stack.findValue(addFieldErrorExpression);
        }
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
