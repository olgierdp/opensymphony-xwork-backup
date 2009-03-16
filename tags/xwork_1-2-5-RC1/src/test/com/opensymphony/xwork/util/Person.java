/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

/**
 * @author tmjee
 * @version $Date$ $Id$
 */
public class Person {

    private String name;
    private Integer age;
    private Address address;
    private MyPrimitivesObject myPrimitiveObject;


    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public MyPrimitivesObject getMyPrimitiveObject() {
        return myPrimitiveObject;
    }

    public void setMyPrimitiveObject(MyPrimitivesObject myPrimitiveObject) {
        this.myPrimitiveObject = myPrimitiveObject;
    }
}
