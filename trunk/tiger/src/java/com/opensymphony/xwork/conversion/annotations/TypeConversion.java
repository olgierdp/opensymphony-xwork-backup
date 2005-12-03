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
     * The optional key name used within TYPE level annotations.
     * Defaults to the property name.
     */
    String key() default "";

    /**
     * The ConversionType can be either APPLICATION or CLASS.
     * Defaults to CLASS.
     *
     * Note: If you use ConversionType.APPLICATION, you can not set a value!
     */
    ConversionType type() default ConversionType.CLASS;

    /**
     * The ConversionRule can be a PROPERTY, KEY, KEY_PROPERTY, ELEMENT, COLLECTION (deprecated) or a MAP.
     * Note: Collection and Map vonversion rules can be determined via com.opensymphony.xwork.util.GenericsObjectTypeDeterminer.
     *
     * @see com.opensymphony.xwork.util.GenericsObjectTypeDeterminer
     */
    ConversionRule rule() default ConversionRule.PROPERTY;

    /**
     * The class of the TypeConverter to be used as converter.
     *
     * Note: This can not be used with ConversionRule.KEY_PROPERTY! 
     */
    String converter() default "";

    /**
     * If used with ConversionRule.KEY_PROPERTY specify a value here!
     *
     * Note: If you use ConversionType.APPLICATION, you can not set a value!
     */
    String value() default "";

}
