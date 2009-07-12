/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util.classloader;

/**
 * *interface taken from Apache JCI
 */
public interface ResourceStore {

    void write(final String pResourceName, final byte[] pResourceData);

    byte[] read(final String pResourceName);
}

