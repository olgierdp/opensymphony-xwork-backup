/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.util.FileManager;
import com.opensymphony.xwork.conversion.annotations.Conversion;
import com.opensymphony.xwork.conversion.annotations.ConversionRule;
import com.opensymphony.xwork.conversion.annotations.ConversionType;
import com.opensymphony.xwork.conversion.annotations.TypeConversion;
import ognl.TypeConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;


/**
 * <!-- START SNIPPET: javadoc -->
 * <p/>
 * Type conversion is great for situations where you need to turn a String in to a more complex object. Because the web
 * is type-agnostic (everything is a string in HTTP), WebWork's type conversion features are very useful. For instance,
 * if you were prompting a user to enter in coordinates in the form of a string (such as "3, 22"), you could have
 * WebWork do the conversion both from String to Point and from Point to String.
 * <p/>
 * <p/> Using this "point" example, if your action (or another compound object in which you are setting properties on)
 * has a corresponding ClassName-conversion.properties file, WebWork will use the configured type converters for
 * conversion to and from strings. So turning "3, 22" in to new Point(3, 22) is done by merely adding the following
 * entry to <b>ClassName-conversion.properties</b> (Note that the PointConverter should impl the ognl.TypeConverter
 * interface):
 * <p/>
 * <p/><b>point = com.acme.PointConverter</b>
 * <p/>
 * <p/> Your type converter should be sure to check what class type it is being requested to convert. Because it is used
 * for both to and from strings, you will need to split the conversion method in to two parts: one that turns Strings in
 * to Points, and one that turns Points in to Strings.
 * <p/>
 * <p/> After this is done, you can now reference your point (using &lt;ww:property value="post"/&gt; in JSP or ${point}
 * in FreeMarker) and it will be printed as "3, 22" again. As such, if you submit this back to an action, it will be
 * converted back to a Point once again.
 * <p/>
 * <p/> In some situations you may wish to apply a type converter globally. This can be done by editing the file
 * <b>xwork-conversion.properties</b> in the root of your class path (typically WEB-INF/classes) and providing a
 * property in the form of the class name of the object you wish to convert on the left hand side and the class name of
 * the type converter on the right hand side. For example, providing a type converter for all Point objects would mean
 * adding the following entry:
 * <p/>
 * <p/><b>com.acme.Point = com.acme.PointConverter</b>
 * <p/>
 * <!-- END SNIPPET: javadoc -->
 * <p/>
 * <p/>
 * <p/>
 * <!-- START SNIPPET: i18n-note -->
 * <p/>
 * Type conversion should not be used as a substitute for i18n. It is not recommended to use this feature to print out
 * properly formatted dates. Rather, you should use the i18n features of WebWork (and consult the JavaDocs for JDK's
 * MessageFormat object) to see how a properly formatted date should be displayed.
 * <p/>
 * <!-- END SNIPPET: i18n-note -->
 * <p/>
 * <p/>
 * <p/>
 * <!-- START SNIPPET: error-reporting -->
 * <p/>
 * Any error that occurs during type conversion may or may not wish to be reported. For example, reporting that the
 * input "abc" could not be converted to a number might be important. On the other hand, reporting that an empty string,
 * "", cannot be converted to a number might not be important - especially in a web environment where it is hard to
 * distinguish between a user not entering a value vs. entering a blank value.
 * <p/>
 * <p/> By default, all conversion errors are reported using the generic i18n key <b>xwork.default.invalid.fieldvalue</b>,
 * which you can override (the default text is <i>Invalid field value for field "xxx"</i>, where xxx is the field name)
 * in your global i18n resource bundle.
 * <p/>
 * <p/>However, sometimes you may wish to override this message on a per-field basis. You can do this by adding an i18n
 * key associated with just your action (Action.properties) using the pattern <b>invalid.fieldvalue.xxx</b>, where xxx
 * is the field name.
 * <p/>
 * <p/>It is important to know that none of these errors are actually reported directly. Rather, they are added to a map
 * called <i>conversionErrors</i> in the ActionContext. There are several ways this map can then be accessed and the
 * errors can be reported accordingly.
 * <p/>
 * <!-- END SNIPPET: error-reporting -->
 *
 * @author <a href="mailto:plightbo@gmail.com">Pat Lightbody</a>
 * @author Rainer Hermanns
 * @see com.opensymphony.xwork.util.XWorkConverter
 */
public class AnnotationXWorkConverter extends XWorkConverter {

    private static final Log _log = LogFactory.getLog(AnnotationXWorkConverter.class);  
  
    protected AnnotationXWorkConverter() {
        try {
            // note: this file is deprecated
            loadConversionProperties("xwork-default-conversion.properties");
        } catch (Exception e) {
        }

        try {
            loadConversionProperties("xwork-conversion.properties");
        } catch (Exception e) {
        }
    }


    /**
     * Looks for converter mappings for the specified class and adds it to an existing map.  Only new converters are
     * added.  If a converter is defined on a key that already exists, the converter is ignored.
     *
     * @param mapping an existing map to add new converter mappings to
     * @param clazz   class to look for converter mappings for
     */
    void addConverterMapping(Map mapping, Class clazz) {
        try {
            InputStream is = FileManager.loadFile(buildConverterFilename(clazz), clazz);

            if (is != null) {
                Properties prop = new Properties();
                prop.load(is);

                Iterator it = prop.entrySet().iterator();

                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    String key = (String) entry.getKey();

                    if (mapping.containsKey(key)) {
                        break;
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(key + ":" + entry.getValue());
                    }

                    if (key.startsWith(DefaultObjectTypeDeterminer.KEY_PROPERTY_PREFIX)
                            || key.startsWith(DefaultObjectTypeDeterminer.CREATE_IF_NULL_PREFIX)) {
                        mapping.put(key, entry.getValue());
                    }
                    //for properties of classes
                    else if (!(key.startsWith(DefaultObjectTypeDeterminer.ELEMENT_PREFIX) ||
                            key.startsWith(DefaultObjectTypeDeterminer.KEY_PREFIX) ||
                            key.startsWith(DefaultObjectTypeDeterminer.DEPRECATED_ELEMENT_PREFIX))
                            ) {
                        mapping.put(key, createTypeConverter((String) entry.getValue()));
                    }
                    //for keys of Maps
                    else if (key.startsWith(DefaultObjectTypeDeterminer.KEY_PREFIX)) {

                        Class converterClass = Thread.currentThread().getContextClassLoader().loadClass((String) entry.getValue());
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Converter class: " + converterClass);
                        }
                        //check if the converter is a type converter if it is one
                        //then just put it in the map as is. Otherwise
                        //put a value in for the type converter of the class
                        if (converterClass.isAssignableFrom(TypeConverter.class)) {

                            mapping.put(key, createTypeConverter((String) entry.getValue()));


                        } else {

                            mapping.put(key, converterClass);
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Object placed in mapping for key "
                                        + key
                                        + " is "
                                        + mapping.get(key));
                            }

                        }


                    }
                    //elements(values) of maps / lists
                    else {
                        mapping.put(key, Thread.currentThread().getContextClassLoader().loadClass((String) entry.getValue()));
                    }
                }
            }
        } catch (Exception ex) {
            LOG.error("Problem loading properties for " + clazz.getName(), ex);
        }

        // Process annotations
        Annotation[] annotations = clazz.getAnnotations();

        for (Annotation annotation : annotations) {
            if (annotation instanceof Conversion) {
                Conversion conversion = (Conversion) annotation;

                for (TypeConversion tc : conversion.conversions()) {

                    String key = tc.key();

                    if (mapping.containsKey(key)) {
                        break;
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(key + ":" + key);
                    }

                    if (key != null) {
                        try {
                            //if (tc.rule().equals(ConversionType.APPLICATION)) {
                        	if (tc.type()  == ConversionType.APPLICATION) {
                                defaultMappings.put(key, createTypeConverter(tc.converter()));
                            } else {
                                if (tc.rule().toString().equals(ConversionRule.KEY_PROPERTY) || tc.rule().toString().equals(ConversionRule.CREATE_IF_NULL)) {
                                    mapping.put(key, tc.value());
                                }
                                //for properties of classes
                                else if (!(tc.rule().toString().equals(ConversionRule.ELEMENT.toString())) ||
                                        tc.rule().toString().equals(ConversionRule.KEY.toString()) ||
                                        tc.rule().toString().equals(ConversionRule.COLLECTION.toString())
                                        ) {
                                    mapping.put(key, createTypeConverter(tc.converter()));
                                }
                                //for keys of Maps
                                else if (tc.rule().toString().equals(ConversionRule.KEY.toString())) {
                                    Class converterClass = Thread.currentThread().getContextClassLoader().loadClass(tc.converter());
                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug("Converter class: " + converterClass);
                                    }
                                    //check if the converter is a type converter if it is one
                                    //then just put it in the map as is. Otherwise
                                    //put a value in for the type converter of the class
                                    if (converterClass.isAssignableFrom(TypeConverter.class)) {
                                        mapping.put(key, createTypeConverter(tc.converter()));
                                    } else {
                                        mapping.put(key, converterClass);
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("Object placed in mapping for key "
                                                    + key
                                                    + " is "
                                                    + mapping.get(key));
                                        }

                                    }

                                }
                                //elements(values) of maps / lists
                                else {
                                    mapping.put(key, Thread.currentThread().getContextClassLoader().loadClass(tc.converter()));
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }

        Method[] methods = clazz.getMethods();

        for (Method method : methods) {

            annotations = method.getAnnotations();

            for (Annotation annotation : annotations) {
                if (annotation instanceof TypeConversion) {
                    TypeConversion tc = (TypeConversion) annotation;

                    String key = tc.key();
                    if (mapping.containsKey(key)) {
                        break;
                    }
                    // Default to the property name
                    if ( key != null && key.length() == 0) {
                        key = AnnotationUtils.resolvePropertyName(method);
                        _log.debug("key from method name... " + key + " - " + method.getName());
                    }


                    if (LOG.isDebugEnabled()) {
                        LOG.debug(key + ":" + key);
                    }

                    if (key != null) {
                    	try {
                            //if (tc.rule().equals(ConversionType.APPLICATION)) {
                        	if (tc.type() == ConversionType.APPLICATION) {
                                defaultMappings.put(key, createTypeConverter(tc.converter()));
                            } else {
                                if (tc.rule().toString().equals(ConversionRule.KEY_PROPERTY)) {
                                    mapping.put(key, tc.value());
                                }
                                //for properties of classes
                                else if (!(tc.rule().toString().equals(ConversionRule.ELEMENT.toString())) ||
                                        tc.rule().toString().equals(ConversionRule.KEY.toString()) ||
                                        tc.rule().toString().equals(ConversionRule.COLLECTION.toString())
                                        ) {
                                    mapping.put(key, createTypeConverter(tc.converter()));
                                }
                                //for keys of Maps
                                else if (tc.rule().toString().equals(ConversionRule.KEY.toString())) {
                                    Class converterClass = Thread.currentThread().getContextClassLoader().loadClass(tc.converter());
                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug("Converter class: " + converterClass);
                                    }
                                    //check if the converter is a type converter if it is one
                                    //then just put it in the map as is. Otherwise
                                    //put a value in for the type converter of the class
                                    if (converterClass.isAssignableFrom(TypeConverter.class)) {
                                        mapping.put(key, createTypeConverter(tc.converter()));
                                    } else {
                                        mapping.put(key, converterClass);
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("Object placed in mapping for key "
                                                    + key
                                                    + " is "
                                                    + mapping.get(key));
                                        }

                                    }

                                }
                                //elements(values) of maps / lists
                                else {
                                    mapping.put(key, Thread.currentThread().getContextClassLoader().loadClass(tc.converter()));
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }

        }
    }
}
