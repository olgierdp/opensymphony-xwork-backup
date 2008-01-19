/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.TextProvider;
import com.opensymphony.xwork.util.OgnlValueStack;

import java.util.*;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * This is a composite {@link TextProvider} that takes in an array or {@link List} of {@link TextProvider}s, it will
 * consult each of them in order to get a composite result. To know how each method behaves, please refer to the
 * javadoc for each methods.
 *
 * @author tmjee
 * @version $Date$ $Id$
 */
public class CompositeTextProvider implements TextProvider {

    private static final Log LOG = LogFactory.getLog(CompositeTextProvider.class);

    private List textProviders = new ArrayList();

    /**
     * Instantiates a {@link CompositeTextProvider} with some predefined <code>textProviders</code>.
     * @param textProviders
     */
    public CompositeTextProvider(List textProviders) {
        for (Iterator i = textProviders.iterator(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof TextProvider) {
                this.textProviders.add(obj);
            }
            else {
                LOG.warn("object ["+obj+"] provided in list ["+textProviders+"] is not a TextProvider instance, ignoring it");
            }
        }
    }

    /**
     * Instantiates a {@link CompositeTextProvider} with some predefined <code>textProviders</code>.
     * @param textProviders
     */
    public CompositeTextProvider(TextProvider[] textProviders) {
        this(Arrays.asList(textProviders));
    }

    /**
     * @see {@link com.opensymphony.xwork.TextProvider#hasKey(String)}
     * It will consult each individual {@link TextProvider}s and return true if either one of the
     * {@link TextProvider} has such a <code>key></code> else false.
     *
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        // if there's a key in either text providers we are ok, else try the next text provider
        for (Iterator i = textProviders.iterator(); i.hasNext(); ) {
            TextProvider _textProvider = (TextProvider) i.next();
            if (_textProvider.hasKey(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code>
     * @see {@link com.opensymphony.xwork.TextProvider#getText(String)}
     * @param key
     * @return
     */
    public String getText(String key) {
        return getText(key, key, Collections.EMPTY_LIST);
    }

    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code> before returning <code>defaultValue</code> if every else fails.
     * @see {@link com.opensymphony.xwork.TextProvider#getText(String, String)} 
     * @param key
     * @param defaultValue
     * @return
     */
    public String getText(String key, String defaultValue) {
        return getText(key, defaultValue, Collections.EMPTY_LIST);
    }

    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code>, before returining <code>defaultValue</code>
     * if every else fails.
     * @see {@link com.opensymphony.xwork.TextProvider#getText(String, String, String)}
     * @param key
     * @param defaultValue
     * @param obj
     * @return
     */
    public String getText(String key, String defaultValue, final String obj) {
        return getText(key, defaultValue, new ArrayList() {
            {
                add(obj);
            }
        });
    }

    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code>.
     * @see {@link com.opensymphony.xwork.TextProvider#getText(String, java.util.List)}
     * @param key
     * @param args
     * @return
     */
    public String getText(String key, List args) {
        return getText(key, key, args);
    }

    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code>.
     * @see {@link com.opensymphony.xwork.TextProvider#getText(String, String[])}
     * @param key
     * @param args
     * @return
     */
    public String getText(String key, String[] args) {
        return getText(key, key, args);
    }


    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code>, before returining <code>defaultValue</code>
     * @see {@link com.opensymphony.xwork.TextProvider#getText#getText(String, String, java.util.List)}
     * @param key
     * @param defaultValue
     * @param args
     * @return
     */
    public String getText(String key, String defaultValue, List args) {
        // if there's one text provider that gives us a msg not the same as defaultValue
        // for this key, we are ok, else try the next
        // text provider
        for (Iterator i = textProviders.iterator(); i.hasNext(); ) {
            TextProvider _textProvider = (TextProvider) i.next();
            String msg = _textProvider.getText(key, defaultValue, args);
            if (msg != null && (!msg.equals(defaultValue))) {
                return msg;
            }
        }
        return defaultValue;
    }


    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code>, before returining <code>defaultValue</code>.
     * @see {@link com.opensymphony.xwork.TextProvider#getText(String, String, String[])}
     * @param key
     * @param defaultValue
     * @param args
     * @return
     */
    public String getText(String key, String defaultValue, String[] args) {
        // if there's one text provider that gives us a msg not the same as defaultValue
        // for this key, we are ok, else try the next
        // text provider
        for (Iterator i = textProviders.iterator(); i.hasNext(); ) {
            TextProvider _textProvider = (TextProvider) i.next();
            String msg = _textProvider.getText(key, defaultValue, args);
            if (msg != null && (!msg.equals(defaultValue))) {
                return msg;
            }
        }
        return defaultValue;
    }


    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code>, before returining <code>defaultValue</code>
     *
     * @see {@link com.opensymphony.xwork.TextProvider#getText(String, String, java.util.List, com.opensymphony.xwork.util.OgnlValueStack)}
     * @param key
     * @param defaultValue
     * @param args
     * @param stack
     * @return
     */
    public String getText(String key, String defaultValue, List args, OgnlValueStack stack) {
        // if there's one text provider that gives us a msg not the same as defaultValue
        // for this key, we are ok, else try the next
        // text provider
        for (Iterator i = textProviders.iterator(); i.hasNext(); ) {
            TextProvider _textProvider = (TextProvider) i.next();
            String msg = _textProvider.getText(key, defaultValue, args, stack);
            if (msg != null && (!msg.equals(defaultValue))) {
                return msg;
            }
        }
        return defaultValue;
    }

    /**
     * It will consult each {@link TextProvider}s and return the first valid message for this
     * <code>key</code>, before returining <code>defaultValue</code>
     * @see {@link com.opensymphony.xwork.TextProvider#getText(String, String, String[], com.opensymphony.xwork.util.OgnlValueStack)}
     * @param key
     * @param defaultValue
     * @param args
     * @param stack
     * @return
     */
    public String getText(String key, String defaultValue, String[] args, OgnlValueStack stack) {
        // if there's one text provider that gives us a msg not the same as defaultValue
        // for this key, we are ok, else try the next
        // text provider
        for (Iterator i = textProviders.iterator(); i.hasNext(); ) {
            TextProvider _textProvider = (TextProvider) i.next();
            String msg = _textProvider.getText(key, defaultValue, args, stack);
            if (msg != null && (!msg.equals(defaultValue))) {
                return msg;
            }
        }
        return defaultValue;
    }


    /**
     * It will consult each {@link TextProvider}s and return the first non-null {@link ResourceBundle}.
     * @see {@link TextProvider#getTexts(String)} 
     * @param bundleName
     * @return
     */
    public ResourceBundle getTexts(String bundleName) {
        // if there's one text provider that gives us a non-null resource bunlde for this bundleName, we are ok, else try the next
        // text provider
        for (Iterator i = textProviders.iterator(); i.hasNext(); ) {
            TextProvider _textProvider = (TextProvider) i.next();
            ResourceBundle bundle  = _textProvider.getTexts(bundleName);
            if (bundle != null) {
                return bundle;
            }
        }
        return null;
    }

    /**
     * It will consult each {@link com.opensymphony.xwork.TextProvider}s and return the first non-null {@link ResourceBundle}.
     * @see {@link TextProvider#getTexts()} 
     * @return
     */
    public ResourceBundle getTexts() {
        // if there's one text provider that gives us a non-null resource bundle, we are ok, else try the next
        // text provider
        for (Iterator i = textProviders.iterator(); i.hasNext(); ) {
            TextProvider _textProvider = (TextProvider) i.next();
            ResourceBundle bundle = _textProvider.getTexts();
            if (bundle != null) {
                return bundle;
            }
        }
        return null;
    }
}
