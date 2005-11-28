/*
 * Copyright (c) 2002-2005 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.apt;

import com.opensymphony.xwork.conversion.annotations.Conversion;
import com.opensymphony.xwork.validator.annotations.Validation;
import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessors;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;

import java.util.*;
import java.util.prefs.Preferences;

/**
 * <code>XWorkProcessorFactory</code>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
public class XWorkProcessorFactory implements AnnotationProcessorFactory {

    private static Preferences preferences = Preferences.userNodeForPackage(XWorkProcessorFactory.class);


    /**
     * Returns the options recognized by this factory or by any of the processors it may create.
     *
     * @return the options recognized by this factory or by any of the processors it may create,
     *         or an empty collection if none.
     */
    public Collection<String> supportedOptions() {
        return Collections.emptySet();
    }

    /**
     * Returns the names of the annotations types supported by this factory.
     *
     * @return the names of the annotations types supported by this factory.
     */
    public Collection<String> supportedAnnotationTypes() {
        return createCollection(
                "com.opensymphony.xwork.conversion.annotations.*" //,
                //"com.opensymphony.xwork.validator.annotations.*"
        );
    }


    /**
     * Returns an annotations processor for a set of annotations types.
     *
     * @param declarations The type declarations for the annotations types to be processed.
     * @param env          The environment to use during processing.
     * @return an annotations processor for the given annotations types, or <tt>null</tt>
     *         if the types are not supported or the processor cannot be created.
     */
    public AnnotationProcessor getProcessorFor(Set<AnnotationTypeDeclaration> declarations, AnnotationProcessorEnvironment env) {
        List<AnnotationProcessor> processors = new LinkedList<AnnotationProcessor>();

        AnnotationTypeDeclaration conversionType = (AnnotationTypeDeclaration) env.getTypeDeclaration(Conversion.class.getName());
        //AnnotationTypeDeclaration validationType = (AnnotationTypeDeclaration) env.getTypeDeclaration(Validation.class.getName());

        if (declarations.contains(conversionType)) {
            ConversionProcessor conversion = new ConversionProcessor();

            conversion.setEnv(env);
            conversion.init();
            processors.add(conversion);
        }

        // TODO: Temporary disabled...
        /*
        if (declarations.contains(validationType)) {
            ValidationProcessor validation = new ValidationProcessor();
            validation.setEnv(env);
            validation.init();
            processors.add(validation);
        }
        */

        if (processors.isEmpty()) {
            return AnnotationProcessors.NO_OP;
        }

        if (processors.size() == 1) {
            return processors.get(0);
        }

        return AnnotationProcessors.getCompositeAnnotationProcessor(processors);
    }

    public static <T> Collection<T> createCollection(T... args) {
        return Arrays.asList(args);
    }
}
