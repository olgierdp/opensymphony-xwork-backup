/*
 * Copyright (c) 2005 Opensymphony. All Rights Reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.util.LocalizedTextUtil;

import java.util.List;
import java.util.ResourceBundle;
import java.io.Serializable;
import java.io.ObjectStreamException;

/**
 * DefaultTextProvider gets texts from only the default resource bundles associated with the
 * LocalizedTextUtil.
 *
 * @see LocalizedTextUtil#addDefaultResourceBundle(String)
 *
 * @author Jason Carreira <jcarreira@gmail.com>
 */
public class DefaultTextProvider implements TextProvider, Serializable, Unchainable {

    public static final DefaultTextProvider INSTANCE = new DefaultTextProvider();

    private DefaultTextProvider() {
    }

    public String getText(String key) {
        return LocalizedTextUtil.findDefaultText(key,ActionContext.getContext().getLocale());
    }

    public String getText(String key, String defaultValue) {
        String text = getText(key);
        if (text == null) {
            return defaultValue;
        }
        return text;
    }

    public String getText(String key, List args) {
        return LocalizedTextUtil.findDefaultText(key,ActionContext.getContext().getLocale(),args.toArray());
    }

    public String getText(String key, String defaultValue, List args) {
        String text = getText(key,args);
        if (text == null) {
            return defaultValue;
        }
        return text;
    }

    public String getText(String key, String defaultValue, List args, OgnlValueStack stack) {
        //we're not using the value stack here
        return getText(key,defaultValue,args);
    }

    public ResourceBundle getTexts(String bundleName) {
        return LocalizedTextUtil.findResourceBundle(bundleName,ActionContext.getContext().getLocale());
    }

    public ResourceBundle getTexts() {
        return null;
    }

    private Object readResolve() throws ObjectStreamException {
        return INSTANCE;
    }
}
