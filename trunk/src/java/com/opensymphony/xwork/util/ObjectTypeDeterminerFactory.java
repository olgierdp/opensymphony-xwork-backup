/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.opensymphony.util.ClassLoaderUtil;

/**
 * Factory for getting an instance of {@link ObjectTypeDeterminer}.
 * <p/>
 * In WebWork this factory is used to instantiate a JDK5 generics compatible factory.
 * <br/>
 * Will use <code>com.opensymphony.xwork.util.GenericsObjectTypeDeterminer</code> if running on JDK5 or higher.
 * If not <code>com.opensymphony.xwork.util.ObjectTypeDeterminer</code> is used.
 *
 * @author plightbo
 * @author Rainer Hermanns
 */
public class ObjectTypeDeterminerFactory {
    private static final Log LOG = LogFactory.getLog(ObjectTypeDeterminerFactory.class);

    private static ObjectTypeDeterminer instance = new DefaultObjectTypeDeterminer();

    static {
        try {
            Class c = ClassLoaderUtil.loadClass("com.opensymphony.xwork.util.GenericsObjectTypeDeterminer",
                    ObjectTypeDeterminerFactory.class);

            LOG.info("Detected GenericsObjectTypeDeterminer, initializing it...");
            instance = (ObjectTypeDeterminer) c.newInstance();
        } catch (ClassNotFoundException e) {
            // this is fine, just fall back to the default object type determiner
        } catch (Exception e) {
            LOG.error("Exception when trying to create new GenericsObjectTypeDeterminer", e);
        }
    }

    public static void setInstance(ObjectTypeDeterminer instance) {
        ObjectTypeDeterminerFactory.instance = instance;
    }

    public static ObjectTypeDeterminer getInstance() {
        return instance;
    }
}
