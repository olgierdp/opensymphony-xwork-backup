/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.util.ClassLoaderUtil;

import com.opensymphony.xwork.util.OgnlUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;


/**
 * ValidatorFactory
 *
 * Created : Jan 20, 2003 12:11:11 PM
 *
 * @author Jason Carreira
 */
public class ValidatorFactory {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static Map validators = new HashMap();
    private static Log LOG = LogFactory.getLog(ValidatorFactory.class);

    static {
        parseValidators();
    }

    //~ Constructors ///////////////////////////////////////////////////////////

    private ValidatorFactory() {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Validator getValidator(String type, Map params) {
        Class clazz = (Class) validators.get(type);

        if (clazz == null) {
            throw new IllegalArgumentException("There is no validator class mapped to the name " + type);
        }

        Validator validator;

        try {
            validator = (Validator) clazz.newInstance();
            OgnlUtil.setProperties(params, validator);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("There was a problem creating a FieldValidator of type " + clazz.getName());
        }

        return validator;
    }

    public static void registerValidator(String name, Class clazz) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Registering validator of class " + clazz.getName() + " with name " + name);
        }

        validators.put(name, clazz);
    }

    private static void parseValidators() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Loading validator definitions.");
        }

        InputStream is = ClassLoaderUtil.getResourceAsStream("validators.xml", ValidatorFactory.class);

        if (is != null) {
            ValidatorFileParser.parseValidatorDefinitions(is);
        }
    }
}
