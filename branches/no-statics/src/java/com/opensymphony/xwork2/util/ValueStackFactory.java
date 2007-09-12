/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.OgnlValueStackFactory;

/**
 * Factory that creates a value stack, defaulting to the OgnlValueStackFactory
 */
public abstract class ValueStackFactory {

    /**
     * Gets the facatory to use for getting instances of {@link com.opensymphony.xwork2.util.ValueStack}
     *
     * @return the factory
     */
    public static ValueStackFactory getFactory() {
        return ActionContext.getContext().getInstance(ValueStackFactory.class);
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
