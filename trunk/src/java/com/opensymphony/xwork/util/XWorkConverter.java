/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.util.FileManager;
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

    public static final String REPORT_CONVERSION_ERRORS = "report.conversion.errors";
    public static final String CONVERSION_PROPERTY_FULLNAME = "conversion.property.fullName";

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
                        mapping = buildConverterMapping(clazz);

                    } else {
                        mapping = conditionalReload(clazz, mapping);
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
                tc = lookup(value.getClass());
            } else {
                // when converting from a string, use the toClass's converter
                tc = lookup(toClass);
            }
        }

        if (tc != null) {
            try {
                return tc.convertValue(context, target, member, property, value, toClass);
            } catch (Exception e) {
                handleConversionException(context, property, value, target);

                return acceptableErrorValue(toClass);
            }
        }

        if (defaultTypeConverter != null) {
            try {
                return defaultTypeConverter.convertValue(context, target, member, property, value, toClass);
            } catch (Exception e) {
                handleConversionException(context, property, value, target);

                return acceptableErrorValue(toClass);
            }
        } else {
            try {
                return super.convertValue(context, target, member, property, value, toClass);
            } catch (Exception e) {
                handleConversionException(context, property, value, target);

                return acceptableErrorValue(toClass);
            }
        }
    }

    private Object acceptableErrorValue(Class toClass) {
        if (!toClass.isPrimitive()) {
            return null;
        }

        if (toClass == int.class) {
            return new Integer(0);
        } else if (toClass == double.class) {
            return new Double(0);
        } else if (toClass == long.class) {
            return new Long(0);
        } else if (toClass == boolean.class) {
            return Boolean.FALSE;
        } else if (toClass == short.class) {
            return new Short((short) 0);
        } else if (toClass == float.class) {
            return new Float(0);
        } else if (toClass == byte.class) {
            return new Byte((byte) 0);
        } else if (toClass == char.class) {
            return new Character((char) 0);
        }

        return null;
    }

    private Map buildConverterMapping(Class clazz) throws Exception {
        Map mapping = new HashMap();

        String resource = buildConverterFilename(clazz);
        InputStream is = FileManager.loadFile(resource,clazz);

        if (is != null) {
            Properties props = new Properties();
            props.load(is);
            mapping.putAll(props);

            for (Iterator iterator = mapping.entrySet().iterator();
                 iterator.hasNext();) {
                Map.Entry entry = (Map.Entry) iterator.next();
                entry.setValue(createTypeConverter((String) entry.getValue()));
            }
            mappings.put(clazz, mapping);
        } else {
            noMapping.add(clazz);
        }
        return mapping;
    }

    private String buildConverterFilename(Class clazz) {
        String className = clazz.getName();
        String resource = className.replace('.', '/') + "-conversion.properties";
        return resource;
    }

    private Map conditionalReload(Class clazz, Map oldValues) throws Exception {
        Map mapping = oldValues;
        if (FileManager.isReloadingConfigs()) {
            if (FileManager.fileNeedsReloading(buildConverterFilename(clazz))) {
                mapping = buildConverterMapping(clazz);
            }
        }
        return mapping;
    }

    public TypeConverter lookup(String className) {
        TypeConverter result = (TypeConverter) defaultMappings.get(className);

        //Looks for super classes
        if (result == null) {
            Class clazz = null;
            try {
                clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            } catch (ClassNotFoundException cnfe) {
            }

            result = lookupSuper(clazz);

            if (result != null) {
                //Register now, the next lookup will be faster
                registerConverter(className, result);
            }
        }
        return result;
    }

    private TypeConverter lookupSuper(Class clazz) {

        TypeConverter result = null;

        if (clazz != null) {
            result = (TypeConverter) defaultMappings.get(clazz.getName());

            if (result == null) {
                // Looks for direct interfaces (depth = 1 )
                Class[] interfaces = clazz.getInterfaces();
                for (int i = 0; i < interfaces.length; i++) {
                    if (defaultMappings.containsKey(interfaces[i].getName())) {
                        result = (TypeConverter) defaultMappings.get(interfaces[i].getName());
                    }
                    break;
                }

                if (result == null) {
                    // Looks for the superclass
                    // If 'clazz' is the Object class, an interface, a primitive type or void then clazz.getSuperClass() returns null 
                    result = lookupSuper(clazz.getSuperclass());
                }
            }

        }

        return result;
    }

    public TypeConverter lookup(Class clazz) {
        return lookup(clazz.getName());
    }

    public void registerConverter(String className, TypeConverter converter) {
        defaultMappings.put(className, converter);
    }

    protected void handleConversionException(Map context, String property, Object value, Object object) {
        if (context.get(REPORT_CONVERSION_ERRORS) == Boolean.TRUE) {
            String realProperty = property;
            String fullName = (String) context.get(CONVERSION_PROPERTY_FULLNAME);
            if (fullName != null) {
                realProperty = fullName;
            }

            String defaultMessage = "Invalid field value for field \"" + realProperty + "\".";
            OgnlValueStack stack = (OgnlValueStack) context.get(ActionContext.VALUE_STACK);

            String getTextExpression = "getText('invalid.fieldvalue." + realProperty + "','" + defaultMessage + "')";
            String message = (String) stack.findValue(getTextExpression);

            if (message == null) {
                message = defaultMessage;
            }

            String addFieldErrorExpression = "addFieldError('" + realProperty + "','" + message + "')";
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
