/*
 * Copyright (c) 2005, Your Corporation. All Rights Reserved.
 */

package com.opensymphony.xwork.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.opensymphony.util.ClassLoaderUtil;

/**
 * <code>ActionValidatorManagerFactory</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id$
 */
public class ActionValidatorManagerFactory {

    private static final Log LOG = LogFactory.getLog(ActionValidatorManagerFactory.class);

    private static ActionValidatorManager instance = new DefaultActionValidatorManager();

    static {
        try {
            Class c = ClassLoaderUtil.loadClass("com.opensymphony.xwork.validator.AnnotationActionValidatorManager", ActionValidatorManagerFactory.class);

            LOG.info("Detected AnnotationActionValidatorManager, initializing it...");
            instance = (ActionValidatorManager) c.newInstance();
        } catch (ClassNotFoundException e) {
            // this is fine, just fall back to the default object type determiner
        } catch (Exception e) {
            LOG.error("Exception when trying to create new AnnotationActionValidatorManager", e);
        }
    }

    public static void setInstance(ActionValidatorManager instance) {
        ActionValidatorManagerFactory.instance = instance;
    }

    public static ActionValidatorManager getInstance() {
        return instance;
    }
}
