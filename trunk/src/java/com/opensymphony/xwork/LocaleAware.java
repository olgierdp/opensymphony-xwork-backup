/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.List;


/**
 * LocaleAware
 * @author Jason Carreira
 * Created Feb 10, 2003 9:55:48 AM
 */
public interface LocaleAware {
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Get the locale for this action.
     *
     * Applications may customize how locale is chosen by
     * subclassing ActionSupport and override this methodName.
     *
     * @return     the locale to use
     */
    Locale getLocale();

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
    String getText(String aTextName);

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
    String getText(String aTextName, String defaultValue);

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
    String getText(String aTextName, List args);

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
    String getText(String aTextName, String defaultValue, List args);

    /**
    * Get the named bundle.
    *
    * You can override the getLocale() methodName to change the behaviour of how
    * to choose locale for the bundles that are returned. Typically you would
    * use the LocaleAware interface to get the users configured locale, or use
    * your own methodName to allow the user to select the locale and store it in
    * the session (by using the SessionAware interface).
    *
    * @param   aBundleName  bundle name
    * @return     a resource bundle
    */
    ResourceBundle getTexts(String aBundleName);

    /**
    * Get the resource bundle associated with this action.
    * This will be based on the actual subclass that is used.
    *
    * @return     resouce bundle
    */
    ResourceBundle getTexts();
}
