/*
 * Copyright (c) 2002-2005 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.conversion.annotations;

import java.lang.annotation.*;

/**
 * <code>TypeConversion</code>
 *
 * This annotations is used for class and application wide conversion rules.
 *
 * <p>
 * Class wide conversion:<br/>
 * The conversion rules will be assembled in a file called <code>XXXAction-conversion.properties</code>
 * within the same package as the related action class.
 * Set type to: <code>type = ConversionType.CLASS</code>
 * </p>
 * <p>
 * Allication wide conversion:<br/>
 * The conversion rules will be assembled within the <code>xwork-conversion.properties</code> file within the classpath root.
 * Set type to: <code>type = ConversionType.APPLICATION</code>
 * <p/>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeConversion {

    /**
     * The optional property name used within TYPE or PACKAGE level annotations.
     */
    String property() default "";

    /**
     * The ConversionType can be either APPLICATION or CLASS.
     * Defaults to APPLICATION.
     */
    ConversionType type() default ConversionType.CLASS;

    /**
     * The ConversionRule can be a property, a Collection or a Map.
     * Note: Collection and Map vonversion rules can be determined via com.opensymphony.xwork.util.GenericsObjectTypeDeterminer.
     *
     * @see com.opensymphony.xwork.util.GenericsObjectTypeDeterminer
     */
    ConversionRule rule() default ConversionRule.PROPERTY;

    /**
     * The class of the TypeConverter to be used as converter.
     */
    Class converter();

}
