/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

/**
 * @author tmjee
 * @version $Date$ $Id$
 */
public class Address {
    private String street;
    private String pobox;
    private MyPrimitiveArrayObject myArrayObject;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPobox() {
        return pobox;
    }

    public void setPobox(String pobox) {
        this.pobox = pobox;
    }

    public MyPrimitiveArrayObject getMyArrayObject() {
        return myArrayObject;
    }

    public void setMyArrayObject(MyPrimitiveArrayObject myArrayObject) {
        this.myArrayObject = myArrayObject;
    }
}
