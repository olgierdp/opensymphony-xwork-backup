/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.XWorkException;

/**
 * Factory that determines what {@link com.opensymphony.xwork2.validator.ActionValidatorManager} to be used.
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 */
public class ActionValidatorManagerFactory {

    private static final Log LOG = LogFactory.getLog(ActionValidatorManagerFactory.class);

    private static ActionValidatorManager instance = new DefaultActionValidatorManager();

    static {
        try {
            Class c = ClassLoaderUtil.loadClass("com.opensymphony.xwork2.validator.AnnotationActionValidatorManager", ActionValidatorManagerFactory.class);

            LOG.info("Detected AnnotationActionValidatorManager, initializing it...");
            instance = (ActionValidatorManager) c.newInstance();
        } catch (ClassNotFoundException e) {
            // this is fine, just fall back to the default object type determiner
        } catch (Exception e) {
            throw new XWorkException(e);
        }
    }

    /**
     * Sets the ActionValidationManager to be used
     *
     * @param instance  the manager to use
     */
    public static void setInstance(ActionValidatorManager instance) {
        ActionValidatorManagerFactory.instance = instance;
    }

    /**
     * Gets the ActionValidatorManager to use
     *
     * @return  the ActionValidatorManager to use
     */
    public static ActionValidatorManager getInstance() {
        return instance;
    }

}
