/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.xwork.util.LocalizedTextUtil;
import com.opensymphony.xwork.util.OgnlValueStack;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * Default TextProvider implementation.
 *
 * @author Jason Carreira
 */
public class TextProviderSupport implements TextProvider {
    //~ Instance fields ////////////////////////////////////////////////////////

    private Class clazz;
    private LocaleProvider localeProvider;
    private ResourceBundle bundle;

    //~ Constructors ///////////////////////////////////////////////////////////

    public TextProviderSupport(Class clazz, LocaleProvider provider) {
        this.clazz = clazz;
        this.localeProvider = provider;
    }

    public TextProviderSupport(ResourceBundle bundle, LocaleProvider provider) {
        this.bundle = bundle;
        this.localeProvider = provider;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
    * Get a text from the resource bundles associated with this action.
    * The resource bundles are searched, starting with the one associated
    * with this particular action, and testing all its superclasses' bundles.
    * It will stop once a bundle is found that contains the given text. This gives
    * a cascading style that allow global texts to be defined for an application base
    * class.
    *
    * @param   aTextName  name of text to be found
    * @return     value of named text
    */
    public String getText(String aTextName) {
        return getText(aTextName,aTextName,null);
    }

    /**
     * Get a text from the resource bundles associated with this action.
     * The resource bundles are searched, starting with the one associated
     * with this particular action, and testing all its superclasses' bundles.
     * It will stop once a bundle is found that contains the given text. This gives
     * a cascading style that allow global texts to be defined for an application base
     * class. If no text is found for this text name, the default value is returned.
     *
     * @param   aTextName  name of text to be found
     * @param   defaultValue the default value which will be returned if no text is found
     * @return     value of named text
     */
    public String getText(String aTextName, String defaultValue) {
        return getText(aTextName,defaultValue,null);
    }

    /**
     * Get a text from the resource bundles associated with this action.
     * The resource bundles are searched, starting with the one associated
     * with this particular action, and testing all its superclasses' bundles.
     * It will stop once a bundle is found that contains the given text. This gives
     * a cascading style that allow global texts to be defined for an application base
     * class. If no text is found for this text name, the default value is returned.
     *
     * @param   aTextName  name of text to be found
     * @param   args a List of args to be used in a MessageFormat message
     * @return     value of named text
     */
    public String getText(String aTextName, List args) {
        return getText(aTextName,aTextName,args);
    }

    /**
     * Get a text from the resource bundles associated with this action.
     * The resource bundles are searched, starting with the one associated
     * with this particular action, and testing all its superclasses' bundles.
     * It will stop once a bundle is found that contains the given text. This gives
     * a cascading style that allow global texts to be defined for an application base
     * class. If no text is found for this text name, the default value is returned.
     *
     * @param   aTextName  name of text to be found
     * @param   defaultValue the default value which will be returned if no text is found
     * @param   args a List of args to be used in a MessageFormat message
     * @return     value of named text
     */
    public String getText(String aTextName, String defaultValue, List args) {
        Object[] argsArray = ((args != null) ? args.toArray() : null);
        if (clazz != null) {
            return LocalizedTextUtil.findText(clazz,aTextName, getLocale(), defaultValue, argsArray);
        } else {
            return LocalizedTextUtil.findText(bundle, aTextName, getLocale(), defaultValue, argsArray);
        }
    }

    /**
     * Gets a message based on a key using the supplied args, as defined in
     * {@link java.text.MessageFormat}, or, if the message is not found, a supplied
     * default value is returned. Instead of using the value stack in the ActionContext
     * this version of the getText() method uses the provided value stack.
     *
     * @param aTextName   the resource bundle key that is to be searched for
     * @param defaultValue the default value which will be returned if no message is found
     * @param args         a list args to be used in a {@link java.text.MessageFormat} message
     * @param stack        the value stack to use for finding the text
     * @return the message as found in the resource bundle, or defaultValue if none is found
     */
    public String getText(String aTextName, String defaultValue, List args, OgnlValueStack stack) {
        Object[] argsArray = ((args != null) ? args.toArray() : null);
        Locale locale = (Locale) stack.getContext().get(ActionContext.LOCALE);
        if (locale == null) {
            locale = getLocale();
        }
        if (clazz != null) {
            return LocalizedTextUtil.findText(clazz, aTextName, locale, defaultValue, argsArray, stack);
        } else {
            return LocalizedTextUtil.findText(bundle, aTextName, locale, defaultValue, argsArray, stack);
        }
    }

    /**
    * Get the named bundle.
    *
    * You can override the getLocale() methodName to change the behaviour of how
    * to choose locale for the bundles that are returned. Typically you would
    * use the TextProvider interface to get the users configured locale, or use
    * your own methodName to allow the user to select the locale and store it in
    * the session (by using the SessionAware interface).
    *
    * @param   aBundleName  bundle name
    * @return     a resource bundle
    */
    public ResourceBundle getTexts(String aBundleName) {
        return LocalizedTextUtil.findResourceBundle(aBundleName, getLocale());
    }

    /**
    * Get the resource bundle associated with this action.
    * This will be based on the actual subclass that is used.
    *
    * @return     resouce bundle
    */
    public ResourceBundle getTexts() {
        return getTexts(clazz.getName());
    }

    private Locale getLocale() {
        return localeProvider.getLocale();
    }
}
