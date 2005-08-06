/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.util.ClassLoaderUtil;
import com.opensymphony.xwork.ObjectFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * ValidatorFactory
 * <p/>
 * Created : Jan 20, 2003 12:11:11 PM
 *
 * @author Jason Carreira
 * @author James House
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

    /**
     * Get a Validator that matches the given configuration.
     */
    public static Validator getValidator(ValidatorConfig cfg) {

        String className = lookupRegisteredValidatorType(cfg.getType());

        Validator validator;

        try {
            // instantiate the validator, and set configured parameters
            validator = ObjectFactory.getObjectFactory().buildValidator(className, cfg.getParams());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("There was a problem creating a Validator of type " + className);
        }

        // set other configured properties
        validator.setMessageKey(cfg.getMessageKey());
        validator.setDefaultMessage(cfg.getDefaultMessage());
        if (validator instanceof ShortCircuitableValidator) {
            ((ShortCircuitableValidator) validator).setShortCircuit(cfg.isShortCircuit());
        }

        return validator;
    }

    public static void registerValidator(String name, String className) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Registering validator of class " + className + " with name " + name);
        }

        validators.put(name, className);
    }

    public static String lookupRegisteredValidatorType(String name) {
        // lookup the validator class mapped to the type name
        String className = (String) validators.get(name);

        if (className == null) {
            throw new IllegalArgumentException("There is no validator class mapped to the name " + name);
        }

        return className;
    }

    private static void parseValidators() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Loading validator definitions.");
        }

        InputStream is = ClassLoaderUtil.getResourceAsStream("validators.xml", ValidatorFactory.class);
        if (is == null) {
            is = ClassLoaderUtil.getResourceAsStream("com/opensymphony/xwork/validator/validators/default.xml",
                    ValidatorFactory.class);
        }

        if (is != null) {
            ValidatorFileParser.parseValidatorDefinitions(is);
        }
    }
}
