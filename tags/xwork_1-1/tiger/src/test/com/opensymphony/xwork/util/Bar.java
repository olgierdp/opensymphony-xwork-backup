/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.ActionSupport;


/**
 * @author <a href="mailto:plightbo@cisco.com">Pat Lightbody</a>
 * @author Rainer Hermanns
 * @version $Revision$
 */
public class Bar extends ActionSupport {

    Long id;
    String title;
    int somethingElse;


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setSomethingElse(int somethingElse) {
        this.somethingElse = somethingElse;
    }

    public int getSomethingElse() {
        return somethingElse;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String toString() {
        return getTitle() + ":" + getSomethingElse();
    }
}
