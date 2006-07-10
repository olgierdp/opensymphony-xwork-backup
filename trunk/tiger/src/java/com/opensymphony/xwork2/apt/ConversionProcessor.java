/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.apt;

import com.opensymphony.xwork2.conversion.annotations.Conversion;
import com.opensymphony.xwork2.conversion.annotations.ConversionType;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;
import com.opensymphony.xwork2.conversion.metadata.ConversionDescription;
import com.opensymphony.xwork2.util.AnnotationUtils;
import com.sun.mirror.declaration.*;
import com.sun.mirror.type.ClassType;
import com.sun.mirror.type.InterfaceType;

import java.util.*;

/**
 * <code>ConversionProcessor</code>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
public class ConversionProcessor extends AbstractProcessor {

    private AnnotationTypeDeclaration conversionAnnotation;
    private AnnotationTypeDeclaration typeConversionAnnotation;

    /**
     * Initializes all annotations types required for processing.
     */
    public void init() {
        super.init();
        this.conversionAnnotation = (AnnotationTypeDeclaration) env.getTypeDeclaration(Conversion.class.getName());
        this.typeConversionAnnotation = (AnnotationTypeDeclaration) env.getTypeDeclaration(TypeConversion.class.getName());
    }


    /**
     * Process all program elements supported by this annotations processor.
     */
    public void process() {

        final Map<String, List<ConversionDescription>> conversionsByType = new Hashtable<String, List<ConversionDescription>>();

        for (Declaration compDecl : env.getDeclarationsAnnotatedWith(conversionAnnotation)) {

            /*
            * Get all fields with TypeConversion-Annotation of component
            */
            List<ConversionDescription> applicationFields = new ArrayList<ConversionDescription>();
            List<ConversionDescription> classFields = new ArrayList<ConversionDescription>();

            if ( compDecl instanceof InterfaceDeclaration) {

                final InterfaceDeclaration component = (InterfaceDeclaration) compDecl;

                addConversionFields(component, applicationFields, classFields);

                conversionsByType.put("", applicationFields);
                conversionsByType.put(component.getQualifiedName(), classFields);
            } else if ( compDecl instanceof ClassDeclaration) {
                final ClassDeclaration component = (ClassDeclaration) compDecl;

                addConversionFields(component, applicationFields, classFields);

                conversionsByType.put("", applicationFields);
                conversionsByType.put(component.getQualifiedName(), classFields);

            }

        }

        /**
         * Holds all components without usage (="default usage")
         */
        List<ConversionDescription> defaultDescriptions = conversionsByType.get("");

        // Add components with default usage to specific components
        /*
        for (Map.Entry<String, List<ConversionDescription>> descriptionMapping : conversionsByType.entrySet()) {
            if (!"".equals(descriptionMapping.getKey())) {
                addDefaultConversions(defaultDescriptions, descriptionMapping.getValue());
            }
        }
        */

        new ConversionGenerator(conversionsByType).generate(env.getFiler());
    }

    private void addDefaultConversions(List<ConversionDescription> defaultComponents, List<ConversionDescription> specialComponents) {
        for (ConversionDescription description : defaultComponents) {
            if (!specialComponents.contains(description)) {
                specialComponents.add(description);
            }
        }
    }

    private ConversionDescription createConversionDescription(MethodDeclaration method, AnnotationMirror annotation) {

        Map<AnnotationTypeElementDeclaration, AnnotationValue> values = annotation.getElementValues();

        final ConversionDescription result;

        String property = AnnotationUtils.resolvePropertyName(method);

        if (typeConversionAnnotation.equals(annotation.getAnnotationType().getDeclaration())) {


            result = new ConversionDescription(property);

            for (AnnotationTypeElementDeclaration element : typeConversionAnnotation.getMethods()) {
                AnnotationValue value = values.get(element);
                if (value == null) {
                    value = element.getDefaultValue();
                }

                String name = element.getSimpleName();
                if ("rule".equals(name)) {
                    result.setRule(value.getValue().toString());
                } else if ("converter".equals(name)) {
                    result.setTypeConverter(value.getValue().toString());
                } else if ("message".equals(name)) {
                    result.setValue(value.getValue().toString());
                } else if ("key".equals(name)) {
                    String s = value.getValue().toString();
                    if ( s != null && s.length() > 0 ) {
                        result.setProperty(s);
                    }
                } else if ("type".equals(name)) {
                    result.setType(value.getValue().toString());
                }
            }
            return result;
        }
        return null;
    }

    /**
     * Adds all fields with TypeConversion Annotation of class clazz and
     * its superclasses to allFields
     */
    private void addConversionFields(ClassDeclaration clazz, List<ConversionDescription> allApplicationFields, List<ConversionDescription> allClassFields) {
        if (clazz == null) {
            return;
        }

        processConversionFields(clazz, allApplicationFields, allClassFields);

        ClassType superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            addConversionFields(superClazz.getDeclaration(), allApplicationFields, allClassFields);
        }
    }

    /**
     * Adds all fields with TypeConversion Annotation of class clazz and
     * its superclasses to allFields
     */
    private void addConversionFields(InterfaceDeclaration clazz, List<ConversionDescription> allApplicationFields, List<ConversionDescription> allClassFields) {
        if (clazz == null) {
            return;
        }

        processConversionFields(clazz, allApplicationFields, allClassFields);
        Collection<InterfaceType> ifaces = clazz.getSuperinterfaces();

        if ( ifaces != null ) {
            for ( InterfaceType it : ifaces) {
                addConversionFields(it.getDeclaration(), allApplicationFields, allClassFields);
            }
        }
    }

    private void processConversionFields(TypeDeclaration clazz, List<ConversionDescription> allApplicationFields, List<ConversionDescription> allClassFields) {
        Collection<? extends MethodDeclaration> methods = clazz.getMethods();

        for (MethodDeclaration method : methods) {

            Collection<AnnotationMirror> annos = method.getAnnotationMirrors();
            for (AnnotationMirror am : annos) {

                Map<AnnotationTypeElementDeclaration, AnnotationValue> values = am.getElementValues();
                for ( AnnotationTypeElementDeclaration element : typeConversionAnnotation.getMethods()) {
                    AnnotationValue value = values.get(element);
                    if (value == null) {
                        value = element.getDefaultValue();
                    }
                    if ( "type".equals(element.getSimpleName()) ) {
                        ConversionDescription desc = createConversionDescription(method, am);
                        if ( desc != null ) {
                            if ( value.getValue().toString().equals(ConversionType.APPLICATION.toString())) {
                                allApplicationFields.add(desc);
                            } else {
                                allClassFields.add(desc);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}
