/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util.finder;

/**
 * This is the testing interface that is used to accept or reject resources.
 */
public interface Test<T> {
    /**
     * The test method.
     *
     * @param   t The resource object to test.
     * @return  True if the resource should be accepted, false otherwise.
     */
    public boolean test(T t);
}