/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ModelDriven;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.text.MessageFormat;

import java.util.*;


/**
 * LocalizedTextUtil
 *
 * @author Jason Carreira
 *         Created Apr 19, 2003 11:02:26 PM
 */
public class LocalizedTextUtil {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final List DEFAULT_RESOURCE_BUNDLES = Collections.synchronizedList(new ArrayList());
    private static final Log LOG = LogFactory.getLog(LocalizedTextUtil.class);
    private static boolean reloadBundles = false;

    static {
        DEFAULT_RESOURCE_BUNDLES.add("com/opensymphony/xwork/xwork-messages");
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static void setReloadBundles(boolean reloadBundles) {
        LocalizedTextUtil.reloadBundles = reloadBundles;
    }

    public static void addDefaultResourceBundle(String resourceBundleName) {
        //make sure this doesn't get added more than once
        DEFAULT_RESOURCE_BUNDLES.remove(resourceBundleName);
        DEFAULT_RESOURCE_BUNDLES.add(0, resourceBundleName);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Added default resource bundle " + resourceBundleName + ", default resource bundles = " + DEFAULT_RESOURCE_BUNDLES);
        }
    }

    public static String findDefaultText(String aTextName, Locale locale) throws MissingResourceException {
        MissingResourceException e = null;
        List localList = new ArrayList(DEFAULT_RESOURCE_BUNDLES);

        for (Iterator iterator = localList.iterator(); iterator.hasNext();) {
            String bundleName = (String) iterator.next();

            try {
                ResourceBundle bundle = findResourceBundle(bundleName, locale);
                reloadBundles(bundle);

                return bundle.getString(aTextName);
            } catch (MissingResourceException ex) {
                e = ex;
            }
        }

        if (e == null) {
            e = new MissingResourceException("Unable to find text for key " + aTextName, LocalizedTextUtil.class.getName(), aTextName);
        }

        throw e;
    }

    /**
     * Returns a localized message for the specified text key, aTextName, substituting variables from the
     * array of params into the message.
     *
     * @param aTextName The message key
     * @param params    An array of objects to be substituted into the message text
     * @return A formatted message based on the key specified
     * @throws MissingResourceException
     */
    public static String findDefaultText(String aTextName, Locale locale, Object[] params) throws MissingResourceException {
        return MessageFormat.format(findDefaultText(aTextName, locale), params);
    }

    public static ResourceBundle findResourceBundle(String aBundleName, Locale locale) {
        return ResourceBundle.getBundle(aBundleName, locale, Thread.currentThread().getContextClassLoader());
    }

    /**
     * Calls {@link #findText(Class aClass, String aTextName, Locale locale, String defaultMessage, Object[] args)} with
     * aTextName as the default message.
     *
     * @see #findText(Class aClass, String aTextName, Locale locale, String defaultMessage, Object[] args)
     */
    public static String findText(Class aClass, String aTextName, Locale locale) {
        return findText(aClass, aTextName, locale, aTextName, new Object[0]);
    }

    public static String findText(ResourceBundle bundle, String aTextName, Locale locale) {
        return findText(bundle, aTextName, locale, aTextName, new Object[0]);
    }

    /**
     * Finds a localized text message for the given text key, aTextName. findText uses a system of
     * defaults for finding resource bundle property files for searching for the message text.
     * <ol>
     * <li>The class name is used with the call <br>
     * ResourceBundle.getBundle(aBundleName, locale, Thread.currentThread().getContextClassLoader())<br>
     * </li>
     * <li>If the message text is not found, each parent class of the action is used as above until
     * java.lang.Object is found.<li>
     * <li>If the message text has still not been found and the action is {@link ModelDriven},
     * the class hierarchy of the model will be traversed (interface and parent classes) and
     * used as above until java.lang.Object is found.</li>
     * <li>If the message text has still not been found, findDefaultText(aTextName, locale) is called to
     * search the default message bundles</li>
     * <li>If the text has still not been found, the provided defaultMessage is returned.<li>
     * </ol>
     *
     * @param aClass         the class whose name to use as the start point for the search
     * @param aTextName      the text key to find the text message for
     * @param locale         the locale to use to find the correct ResourceBundle
     * @param defaultMessage the message to be returned if no text message can be found in a resource bundle
     * @return
     */
    public static String findText(Class aClass, String aTextName, Locale locale, String defaultMessage, Object[] args) {
        ActionContext context = ActionContext.getContext();
        OgnlValueStack valueStack = context.getValueStack();

        // search up class hierarchy
        Class clazz = aClass;

        do {
            try {
                ResourceBundle bundle = findResourceBundle(clazz.getName(), locale);
                reloadBundles(bundle);

                String message = TextParseUtil.translateVariables(bundle.getString(aTextName), valueStack);

                return MessageFormat.format(message, args);
            } catch (MissingResourceException ex) {
                clazz = clazz.getSuperclass();
            }
        } while (!clazz.equals(Object.class));

        if (ModelDriven.class.isAssignableFrom(aClass)) {
            Action action = context.getActionInvocation().getAction();

            // make sure action is ModelDriven
            if (action instanceof ModelDriven) {
                clazz = ((ModelDriven) action).getModel().getClass();

                while (!clazz.equals((Object.class))) {
                    // look for resource bundle for implemented interfaces
                    Class[] interfaces = clazz.getInterfaces();

                    for (int x = 0; x < interfaces.length; x++) {
                        try {
                            ResourceBundle bundle = findResourceBundle(interfaces[x].getName(), locale);
                            reloadBundles(bundle);

                            String message = TextParseUtil.translateVariables(bundle.getString(aTextName), valueStack);

                            return MessageFormat.format(message, args);
                        } catch (MissingResourceException ex) {
                        }
                    }

                    // search up model class hierarchy
                    try {
                        ResourceBundle bundle = findResourceBundle(clazz.getName(), locale);
                        reloadBundles(bundle);

                        String message = TextParseUtil.translateVariables(bundle.getString(aTextName), valueStack);

                        return MessageFormat.format(message, args);
                    } catch (MissingResourceException ex) {
                    }

                    clazz = clazz.getSuperclass();
                }
            }
        }

        // nothing still? alright, search the package hierarchy now
        for (clazz = aClass; (clazz != null) && !clazz.equals(Object.class);
                clazz = clazz.getSuperclass()) {
            if (clazz.getPackage() != null) {
                try {
                    String packageName = clazz.getPackage().getName();
                    ResourceBundle bundle = findResourceBundle(packageName + ".package", locale);
                    reloadBundles(bundle);

                    String message = TextParseUtil.translateVariables(bundle.getString(aTextName), valueStack);

                    return MessageFormat.format(message, args);
                } catch (MissingResourceException ex) {
                }
            }
        }

        return getDefaultText(aTextName, locale, valueStack, args, defaultMessage);
    }

    public static String findText(ResourceBundle bundle, String aTextName, Locale locale, String defaultMessage, Object[] args) {
        OgnlValueStack valueStack = ActionContext.getContext().getValueStack();

        try {
            reloadBundles(bundle);

            String message = TextParseUtil.translateVariables(bundle.getString(aTextName), valueStack);

            return MessageFormat.format(message, args);
        } catch (MissingResourceException ex) {
        }

        return getDefaultText(aTextName, locale, valueStack, args, defaultMessage);
    }

    private static String getDefaultText(String aTextName, Locale locale, OgnlValueStack valueStack, Object[] args, String defaultMessage) {
        try {
            String message = TextParseUtil.translateVariables(findDefaultText(aTextName, locale), valueStack);

            return MessageFormat.format(message, args);
        } catch (MissingResourceException ex) {
            //ignore
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Unable to find text for key " + aTextName);
        }

        return MessageFormat.format(TextParseUtil.translateVariables(defaultMessage, valueStack), args);
    }

    private static void reloadBundles(ResourceBundle resource) {
        if (reloadBundles) {
            try {
                Class klass = resource.getClass().getSuperclass();
                Field field = klass.getDeclaredField("cacheList");
                field.setAccessible(true);

                Object cache = field.get(null);
                Method clearMethod = cache.getClass().getMethod("clear", new Class[0]);
                clearMethod.invoke(cache, new Object[0]);
            } catch (Exception e) {
                LOG.error("Could not reload resource bundles", e);
            }
        }
    }
}
