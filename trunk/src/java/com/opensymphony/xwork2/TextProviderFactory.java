/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import java.util.ResourceBundle;

/**
 * <code>TextProviderFactory</code>
 *
 * @author Oleg Gorobets
 */
public class TextProviderFactory {

    private static TextProvider instance = new TextProviderSupport();

    /**
     * @param instance Text provider
     */
    public static void setInstance(TextProvider instance) {
        TextProviderFactory.instance = instance;
    }

    public static TextProvider getInstance() {
        return instance;
    }

    public static TextProvider getInstance(Class clazz, LocaleProvider provider) {
        if (instance instanceof TextProviderSupport) {
            ((TextProviderSupport) instance).setClazz(clazz);
            ((TextProviderSupport) instance).setLocaleProvider(provider);
        }
        return instance;
    }

    public static TextProvider getInstance(ResourceBundle bundle, LocaleProvider provider) {
        if (instance instanceof TextProviderSupport) {
            ((TextProviderSupport) instance).setBundle(bundle);
            ((TextProviderSupport) instance).setLocaleProvider(provider);
        }
        return instance;
    }
}
