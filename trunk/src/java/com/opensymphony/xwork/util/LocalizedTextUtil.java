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
 * Provides support for localization in XWork.
 *
 * @author Jason Carreira
 * @author Mark Woon
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

    /**
     * Returns a localized message for the specified key, aTextName.  Neither the key nor the
     * message is evaluated.
     *
     * @param aTextName the message key
     * @param locale    the locale the message should be for
     * @return a localized message based on the specified key
     * @throws MissingResourceException if no message can be found for the specified key
     */
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
     * Returns a localized message for the specified key, aTextName, substituting variables from the
     * array of params into the message.  Neither the key nor the message is evaluated.
     *
     * @param aTextName the message key
     * @param locale    the locale the message should be for
     * @param params    an array of objects to be substituted into the message text
     * @return A formatted message based on the specified key
     * @throws MissingResourceException if no message can be found for the specified key
     */
    public static String findDefaultText(String aTextName, Locale locale, Object[] params) throws MissingResourceException {
        MessageFormat mf = buildMessageFormat(findDefaultText(aTextName, locale), locale);

        return mf.format(params);
    }

    public static ResourceBundle findResourceBundle(String aBundleName, Locale locale) {
        return ResourceBundle.getBundle(aBundleName, locale, Thread.currentThread().getContextClassLoader());
    }

    /**
     * Calls {@link #findText(Class aClass, String aTextName, Locale locale, String defaultMessage, Object[] args)}
     * with aTextName as the default message.
     *
     * @see #findText(Class aClass, String aTextName, Locale locale, String defaultMessage, Object[] args)
     */
    public static String findText(Class aClass, String aTextName, Locale locale) {
        return findText(aClass, aTextName, locale, aTextName, new Object[0]);
    }

    /**
     * Finds a localized text message for the given key, aTextName. Both the key and the message
     * itself is evaluated as required.  The following algorithm is used to find the requested
     * message:
     * <p />
     * <ol>
     * <li>Look for message in aClass' class hierarchy.
     * <ol>
     * <li>Look for the message in a resource bundle for aClass</li>
     * <li>If not found, look for the message in a resource bundle for any implemented interface</li>
     * <li>If not found, traverse up the Class' hierarchy and repeat from the first sub-step</li>
     * </ol></li>
     * <li>If not found and aClass is a {@link ModelDriven} Action, then look for message in
     * the model's class hierarchy (repeat sub-steps listed above).</li>
     * <li>If not found, look for message in child property.  This is determined by evaluating
     * the message key as an OGNL expression.  For example, if the key is
     * <i>user.address.state</i>, then it will attempt to see if "user" can be resolved into an
     * object.  If so, repeat the entire process fromthe beginning with the object's class as
     * aClass and "address.state" as the message key.</li>
     * <li>If not found, look for the message in aClass' package hierarchy.</li>
     * <li>If still not found, look for the message in the default resource bundles.</li>
     * <li>Return defaultMessage</li>
     * </ol>
     * <p />
     * When looking for the message, if the key indexes a collection (e.g. user.phone[0]) and a
     * message for that specific key cannot be found, the general form will also be looked up
     * (i.e. user.phone[*]).
     * <p />
     * If a message is found, it will also be interpolated.  Anything within <code>${...}</code>
     * will be treated as an OGNL expression and evaluated as such.
     *
     * @param aClass         the class whose name to use as the start point for the search
     * @param aTextName      the key to find the text message for
     * @param locale         the locale the message should be for
     * @param defaultMessage the message to be returned if no text message can be found in any
     *                       resource bundle
     * @return the localized text, or null if none can be found and no defaultMessage is provided
     */
    public static String findText(Class aClass, String aTextName, Locale locale, String defaultMessage, Object[] args) {
        ActionContext context = ActionContext.getContext();
        OgnlValueStack valueStack = context.getValueStack();
        String indexedTextName = null;

        // calculate indexedTextName (collection[*]) if applicable
        if (aTextName.indexOf("[") != -1) {
            int i = -1;

            indexedTextName = aTextName;

            while ((i = indexedTextName.indexOf("[", i + 1)) != -1) {
                int j = indexedTextName.indexOf("]", i);
                String a = indexedTextName.substring(0, i);
                String b = indexedTextName.substring(j);
                indexedTextName = a + "[*" + b;
            }
        }

        // search up class hierarchy
        String msg = findMessage(aClass, aTextName, indexedTextName, locale, args, null, valueStack);

        if (msg != null) {
            return msg;
        }

        if (ModelDriven.class.isAssignableFrom(aClass)) {
            // search up model's class hierarchy
            Action action = context.getActionInvocation().getAction();
            msg = findMessage(((ModelDriven) action).getModel().getClass(), aTextName, indexedTextName, locale, args, null, valueStack);

            if (msg != null) {
                return msg;
            }
        }

        // nothing still? alright, search the package hierarchy now
        for (Class clazz = aClass;
             (clazz != null) && !clazz.equals(Object.class);
             clazz = clazz.getSuperclass()) {
            if (clazz.getPackage() != null) {
                String packageName = clazz.getPackage().getName() + ".package";
                msg = getMessage(packageName, locale, aTextName, valueStack, args);

                if (msg != null) {
                    return msg;
                }

                if (indexedTextName != null) {
                    msg = getMessage(packageName, locale, indexedTextName, valueStack, args);

                    if (msg != null) {
                        return msg;
                    }
                }
            }
        }

        // see if it's a child property
        int idx = aTextName.indexOf(".");
        if (idx != -1) {
            String newKey = null;
            String prop = null;

            if (aTextName.startsWith(XWorkConverter.CONVERSION_ERROR_PROPERTY_PREFIX)) {
                idx = aTextName.indexOf(".", XWorkConverter.CONVERSION_ERROR_PROPERTY_PREFIX.length());

                if (idx != -1) {
                    prop = aTextName.substring(XWorkConverter.CONVERSION_ERROR_PROPERTY_PREFIX.length(), idx);
                    newKey = XWorkConverter.CONVERSION_ERROR_PROPERTY_PREFIX + aTextName.substring(idx + 1);
                }
            } else {
                prop = aTextName.substring(0, idx);
                newKey = aTextName.substring(idx + 1);
            }

            if (prop != null) {
                Object obj = valueStack.findValue(prop);

                if (obj != null) {
                    Class clazz = obj.getClass();

                    if (clazz != null) {
                        valueStack.push(obj);
                        msg = findText(clazz, newKey, locale, null, args);
                        valueStack.pop();

                        if (msg != null) {
                            return msg;
                        }
                    }
                }
            }
        }

        // get default
        if (indexedTextName == null) {
            return getDefaultMessage(aTextName, locale, valueStack, args, defaultMessage);
        } else {
            msg = getDefaultMessage(aTextName, locale, valueStack, args, null);

            if (msg != null) {
                return msg;
            }

            return getDefaultMessage(indexedTextName, locale, valueStack, args, defaultMessage);
        }
    }

    /**
     * Finds a localized text message for the given key, aTextName, in the specified resource bundle
     * with aTextName as the default message.
     * <p />
     * If a message is found, it will also be interpolated.  Anything within <code>${...}</code>
     * will be treated as an OGNL expression and evaluated as such.
     *
     * @see #findText(java.util.ResourceBundle, java.lang.String, java.util.Locale, java.lang.String, java.lang.Object[])
     */
    public static String findText(ResourceBundle bundle, String aTextName, Locale locale) {
        return findText(bundle, aTextName, locale, aTextName, new Object[0]);
    }

    /**
     * Finds a localized text message for the given key, aTextName, in the specified resource
     * bundle.
     * <p />
     * If a message is found, it will also be interpolated.  Anything within <code>${...}</code>
     * will be treated as an OGNL expression and evaluated as such.
     */
    public static String findText(ResourceBundle bundle, String aTextName, Locale locale, String defaultMessage, Object[] args) {
        OgnlValueStack valueStack = ActionContext.getContext().getValueStack();

        try {
            reloadBundles(bundle);

            String message = TextParseUtil.translateVariables(bundle.getString(aTextName), valueStack);
            MessageFormat mf = buildMessageFormat(message, locale);

            return mf.format(args);
        } catch (MissingResourceException ex) {
        }

        return getDefaultMessage(aTextName, locale, valueStack, args, defaultMessage);
    }

    /**
     * Gets the default message.
     */
    private static String getDefaultMessage(String key, Locale locale, OgnlValueStack valueStack, Object[] args, String defaultMessage) {
        if (key != null) {
            String message = null;

            try {
                message = findDefaultText(key, locale);
            } catch (MissingResourceException ex) {
                //ignore
            }

            if (message == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Unable to find text for key " + key);
                }

                message = defaultMessage;
            }

            // defaultMessage may be null
            if (message != null) {
                MessageFormat mf = buildMessageFormat(TextParseUtil.translateVariables(message, valueStack), locale);

                return mf.format(args);
            }
        }

        return null;
    }

    /**
     * Gets the message from the named resource bundle.
     */
    private static String getMessage(String bundleName, Locale locale, String key, OgnlValueStack valueStack, Object[] args) {
        try {
            ResourceBundle bundle = findResourceBundle(bundleName, locale);
            reloadBundles(bundle);

            String message = TextParseUtil.translateVariables(bundle.getString(key), valueStack);

            MessageFormat mf = buildMessageFormat(message, locale);

            return mf.format(args);
        } catch (MissingResourceException ex) {
            return null;
        }
    }

    private static MessageFormat buildMessageFormat(String pattern, Locale locale) {
        MessageFormat format = new MessageFormat(pattern);
        format.setLocale(locale);

        return format;
    }

    /**
     * Traverse up class hierarchy looking for message.  Looks at class, then implemented interface,
     * before going up hierarchy.
     */
    private static String findMessage(Class clazz, String key, String indexedKey, Locale locale, Object[] args, Set checked, OgnlValueStack valueStack) {
        if (checked == null) {
            checked = new TreeSet();
        } else if (checked.contains(clazz.getName())) {
            return null;
        }

        // look in properties of this class
        String msg = getMessage(clazz.getName(), locale, key, valueStack, args);

        if (msg != null) {
            return msg;
        }

        if (indexedKey != null) {
            msg = getMessage(clazz.getName(), locale, indexedKey, valueStack, args);

            if (msg != null) {
                return msg;
            }
        }

        // look in properties of implemented interfaces
        Class[] interfaces = clazz.getInterfaces();

        for (int x = 0; x < interfaces.length; x++) {
            msg = getMessage(interfaces[x].getName(), locale, key, valueStack, args);

            if (msg != null) {
                return msg;
            }

            if (indexedKey != null) {
                msg = getMessage(interfaces[x].getName(), locale, indexedKey, valueStack, args);

                if (msg != null) {
                    return msg;
                }
            }
        }

        // traverse up hierarchy
        if (clazz.isInterface()) {
            interfaces = clazz.getInterfaces();

            for (int x = 0; x < interfaces.length; x++) {
                msg = findMessage(interfaces[x], key, indexedKey, locale, args, checked, valueStack);

                if (msg != null) {
                    return msg;
                }
            }
        } else {
            if (!clazz.equals(Object.class)) {
                return findMessage(clazz.getSuperclass(), key, indexedKey, locale, args, checked, valueStack);
            }
        }

        return null;
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
