package com.opensymphony.xwork.util;

import junit.framework.TestCase;

public class InstantiatingNullHandlerTest extends TestCase {
    public void testInheritance() {
        Tiger t = new Tiger();
        InstantiatingNullHandler nh = new InstantiatingNullHandler();

        Class clazz = nh.getCollectionType(Tiger.class, "dogs");
        assertEquals(Dog.class, clazz);

        clazz = nh.getCollectionType(Tiger.class, "kittens");
        assertEquals(Cat.class, clazz);
    }
}
