/*
 * Copyright (c) 2005 Opensymphony. All Rights Reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.xwork.util.LocalizedTextUtil;
import com.opensymphony.xwork.util.OgnlValueStack;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;

/**
 * DefaultTextProvider gets texts from only the default resource bundles associated with the
 * LocalizedTextUtil.
 *
 * @author Jason Carreira <jcarreira@gmail.com>
 * @see LocalizedTextUtil#addDefaultResourceBundle(String)
 */
public class DefaultTextProvider implements TextProvider, Serializable, Unchainable {

    public static final DefaultTextProvider INSTANCE = new DefaultTextProvider();

    private DefaultTextProvider() {
    }

    public String getText(String key) {
        return LocalizedTextUtil.findDefaultText(key, ActionContext.getContext().getLocale());
    }

    public String getText(String key, String defaultValue) {
        String text = getText(key);
        if (text == null) {
            return defaultValue;
        }
        return text;
    }

    public String getText(String key, List args) {
        Object[] params;
        if (args != null) {
            params = args.toArray();
        } else {
            params = new Object[0];
        }

        return LocalizedTextUtil.findDefaultText(key, ActionContext.getContext().getLocale(), params);
    }

    public String getText(String key, String defaultValue, List args) {
        String text = getText(key, args);
        if (text == null) {
            return defaultValue;
        }
        return text;
    }

    public String getText(String key, String defaultValue, List args, OgnlValueStack stack) {
        //we're not using the value stack here
        return getText(key, defaultValue, args);
    }

    public ResourceBundle getTexts(String bundleName) {
        return LocalizedTextUtil.findResourceBundle(bundleName, ActionContext.getContext().getLocale());
    }

    public ResourceBundle getTexts() {
        return null;
    }

    private Object readResolve() throws ObjectStreamException {
        return INSTANCE;
    }
}
