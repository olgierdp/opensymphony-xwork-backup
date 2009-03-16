/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.test;

import com.opensymphony.xwork.util.Bar;


/**
 * Implemented by SimpleAction3 and TestBean2 to test class hierarchy traversal.
 *
 * @author Mark Woon
 */
public interface DataAware {

    void setBarObj(Bar b);

    Bar getBarObj();

    void setData(String data);

    String getData();
}
