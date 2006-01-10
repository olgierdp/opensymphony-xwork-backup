package com.opensymphony.xwork.util;

import com.opensymphony.util.ClassLoaderUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: plightbo
 * Date: Sep 22, 2005
 * Time: 8:26:17 PM
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
