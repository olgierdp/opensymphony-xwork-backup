/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * This file is copyright (c) 2001-2004, Board of Trustees of Stanford
 * University.  Created in the laboratory of Professor Russ B. Altman
 * (russ.altman@stanford.edu), Stanford University, Department of
 * Medicine, as part of the NIH PharmGKB knowledge base development
 * effort.  This work is supported by NIH U01GM61374.  Contact
 * help@pharmgkb.org for assistance, questions or suggestions.
 */
package com.opensymphony.xwork.test;

import com.opensymphony.xwork.util.Bar;


/**
 * Implemented by SimpleAction3 and TestBean2 to test class hierarchy traversal.
 *
 * @author Mark Woon
 */
public interface DataAware {
    //~ Methods ////////////////////////////////////////////////////////////////

    void setBarObj(Bar b);

    Bar getBarObj();

    void setData(String data);

    String getData();
}
