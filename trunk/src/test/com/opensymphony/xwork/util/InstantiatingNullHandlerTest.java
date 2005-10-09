/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import junit.framework.TestCase;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class InstantiatingNullHandlerTest extends TestCase {

    public void testBlank() {

    }
    /*public void testInheritance() {
        Tiger t = new Tiger();
        CompoundRoot root = new CompoundRoot();
        root.add(t);

        Map context = new OgnlContext();
        context.put(InstantiatingNullHandler.CREATE_NULL_OBJECTS, Boolean.TRUE);

        InstantiatingNullHandler nh = new InstantiatingNullHandler();

        Object dogList = nh.nullPropertyValue(context, root, "dogs");
        Class clazz = nh.getCollectionType(Tiger.class, "dogs");
        assertEquals(Dog.class, clazz);
        assertNotNull(dogList);
        assertTrue(dogList instanceof List);

        Object kittenList = nh.nullPropertyValue(context, root, "kittens");
        clazz = nh.getCollectionType(Tiger.class, "kittens");
        assertEquals(Cat.class, clazz);
        assertNotNull(kittenList);
        assertTrue(kittenList instanceof List);
    }*/
}
