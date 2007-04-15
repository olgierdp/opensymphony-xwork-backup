/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

/**
 * Factory that creates a value stack, defaulting to the OgnlValueStackFactory
 */
public abstract class ValueStackFactory {

    private static ValueStackFactory factory = new OgnlValueStackFactory();

    /**
     * Set a new factory to use.
     *
     * @param factory the new factory
     */
    public static void setFactory(ValueStackFactory factory) {
        ValueStackFactory.factory = factory;
    }

    /**
     * Gets the facatory to use for getting instances of {@link com.opensymphony.xwork2.util.ValueStack}
     *
     * @return the factory
     */
    public static ValueStackFactory getFactory() {
        return factory;
    }

    /**
     * Get a new instance of {@link com.opensymphony.xwork2.util.ValueStack}
     *
     * @return  a new {@link com.opensymphony.xwork2.util.ValueStack}.
     */
    public abstract ValueStack createValueStack();
    
    /**
     * Get a new instance of {@link com.opensymphony.xwork2.util.ValueStack}
     *
     * @param stack an existing stack to include.
     * @return  a new {@link com.opensymphony.xwork2.util.ValueStack}.
     */
    public abstract ValueStack createValueStack(ValueStack stack);
    
}
