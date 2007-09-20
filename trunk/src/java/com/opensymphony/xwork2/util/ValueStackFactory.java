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
public interface ValueStackFactory {

    /**
     * Get a new instance of {@link com.opensymphony.xwork2.util.ValueStack}
     *
     * @return  a new {@link com.opensymphony.xwork2.util.ValueStack}.
     */
    ValueStack createValueStack();
    
    /**
     * Get a new instance of {@link com.opensymphony.xwork2.util.ValueStack}
     *
     * @param stack an existing stack to include.
     * @return  a new {@link com.opensymphony.xwork2.util.ValueStack}.
     */
    ValueStack createValueStack(ValueStack stack);
    
}
