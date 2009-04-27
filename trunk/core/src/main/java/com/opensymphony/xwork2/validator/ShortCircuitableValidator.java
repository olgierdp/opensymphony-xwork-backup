/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator;


/**
 * This interface should be implemented by validators that can short-circuit the validator queue
 * that it is in.
 *
 * @author Mark Woon
 */
public interface ShortCircuitableValidator {

    /**
     * Sets whether this field validator should short circuit the validator queue
     * it's in if validation fails.
     *
     * @param shortcircuit <tt>true</tt> if this field validator should short circuit on
     *                     failure, <tt>false</tt> otherwise
     */
    public void setShortCircuit(boolean shortcircuit);

    /**
     * Gets whether this field validator should short circuit the validator queue
     * it's in if validation fails.
     *
     * @return <tt>true</tt> if this field validator should short circuit on failure,
     *         <tt>false</tt> otherwise
     */
    public boolean isShortCircuit();
}
