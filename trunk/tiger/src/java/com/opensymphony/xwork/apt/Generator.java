/*
 * Copyright (c) 2002-2005 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.apt;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Filer;
import com.sun.mirror.declaration.MethodDeclaration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <code>Generator</code>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
public abstract class Generator {


    private static final Pattern SETTER_PATTERN = Pattern.compile("set([A-Z][A-Za-z0-9]*)$");
    private static final Pattern GETTER_PATTERN = Pattern.compile("(get|is|has)([A-Z][A-Za-z0-9]*)$");

    protected AnnotationProcessorEnvironment env;


    /**
     * Sets the AnnotationProcessorEnvironment.
     *
     * @param env the AnnotationProcessorEnvironment.
     */
    public void setEnv(AnnotationProcessorEnvironment env) {
        this.env = env;
    }

    /**
     * Generate the configuration file.
     * Concrete implementations must override this method.
     */
    public abstract void generate(Filer filer);

    /**
     * Returns the property name for a method.
     * This method is independant from property fields.
     *
     * @param method The method to get the property name for.
     * @return the property name for given method; null if non could be resolved.
     */
    public static String resolvePropertyName(MethodDeclaration method) {

        Matcher matcher = SETTER_PATTERN.matcher(method.getSimpleName());
        if (matcher.matches() && method.getParameters().size() == 1) {
            String raw = matcher.group(1);
            return raw.substring(0, 1).toLowerCase() + raw.substring(1);
        }

        matcher = GETTER_PATTERN.matcher(method.getSimpleName());
        if (matcher.matches() && method.getParameters().size() == 0) {
            String raw = matcher.group(2);
            return raw.substring(0, 1).toLowerCase() + raw.substring(1);
        }

        return null;
    }

}
