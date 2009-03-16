/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import java.util.List;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class Tiger extends Cat {

    List dogs;


    public void setDogs(List dogs) {
        this.dogs = dogs;
    }

    public List getDogs() {
        return dogs;
    }
}
