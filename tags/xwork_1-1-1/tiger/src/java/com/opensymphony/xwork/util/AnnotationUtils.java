/*
 * Copyright (c) 2002-2005 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.File;
import java.net.URL;

/**
 * <code>AnnotationUtils</code>
 *
 * Various utility methods dealing with annotations
 *
 * @author Rainer Hermanns
 * @author Zsolt Szasz, zsolt at lorecraft dot com
 * @version $Id$
 */
public class AnnotationUtils {

    /**
     * Adds all fields with the specified Annotation of class clazz and its superclasses to allFields
     *
     * @param annotationClass
     * @param clazz
     * @param allFields
     */
    public static void addAllFields(Class annotationClass, Class clazz, List<Field> allFields) {

        if (clazz == null) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            Annotation ann = field.getAnnotation(annotationClass);
            if (ann!=null) {
                allFields.add(field);
            }
        }
        addAllFields(annotationClass, clazz.getSuperclass(), allFields);
    }

    /**
     * Adds all methods with the specified Annotation of class clazz and its superclasses to allFields
     *
     * @param annotationClass
     * @param clazz
     * @param allMethods
     */
    public static void addAllMethods(Class annotationClass, Class clazz, List<Method> allMethods) {

        if (clazz == null) {
            return;
        }

        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            Annotation ann = method.getAnnotation(annotationClass);
            if (ann!=null) {
                allMethods.add(method);
            }
        }
        addAllMethods(annotationClass, clazz.getSuperclass(), allMethods);
    }

    /**
     *
     * @param clazz
     * @param allInterfaces
     */
    public static void addAllInterfaces(Class clazz, List allInterfaces) {
        if (clazz == null) {
            return;
        }

        Class[] interfaces = clazz.getInterfaces();
        allInterfaces.addAll(Arrays.asList(interfaces));
        addAllInterfaces(clazz.getSuperclass(), allInterfaces);
    }

    public static List<Method> findAnnotatedMethods(Class clazz, Class<? extends Annotation> annotationClass) {
        List<Method> methods = new ArrayList<Method>();
        findRecursively(clazz, annotationClass, methods);
        return methods;
    }

    public static void findRecursively(Class clazz, Class<? extends Annotation> annotationClass, List<Method> methods) {
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getAnnotation(annotationClass) != null) { methods.add(0, m); }
        }
        if (clazz.getSuperclass() != Object.class) {
            findRecursively(clazz.getSuperclass(), annotationClass, methods);
        }
    }


    /**
     * Retrieves all classes within a packages.
     * TODO: this currently does not work with jars.
     *
     * @param pckgname
     * @return Array of full qualified class names from this package.
     */
    public static String[] find(Class clazz, final String pckgname) {

        List<String> classes = new ArrayList<String>();
        String name = new String(pckgname);
        if (!name.startsWith("/")) {
            name = "/" + name;
        }

        name = name.replace('.', File.separatorChar);

        final URL url = clazz.getResource(name);
        final File directory = new File(url.getFile());

        if (directory.exists()) {
            final String[] files = directory.list();
            for (int i = 0; i < files.length; i++) {
                if (files[i].endsWith(".class")) {
                    classes.add(pckgname + "." + files[i].substring(0, files[i].length() - 6));
                }
            }
        }
        return classes.toArray(new String[classes.size()]);
    }
}
