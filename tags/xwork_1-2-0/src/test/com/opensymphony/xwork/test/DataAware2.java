/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.test;


/**
 * Used to test hierarchy traversal for interfaces.
 *
 * @author Mark Woon
 */
public interface DataAware2 extends DataAware {

    public void setBling(String bling);

    public String getBling();
}
