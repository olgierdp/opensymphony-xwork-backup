package com.opensymphony.xwork2.util;

import junit.framework.*;

/**
 * ObjectTypeDeterminerFactoryTest test for given class.
 *
 * @author Rene Gielen
 */
public class ObjectTypeDeterminerFactoryTest extends TestCase {


    public void testDefaultInstanceTypeIsGenericsObjectTypeDeterminer() throws Exception {
        assertEquals(ObjectTypeDeterminerFactory.getInstance().getClass(),DefaultObjectTypeDeterminer.class);
    }

    public void testSetInstance() throws Exception {
        ObjectTypeDeterminer objectTypeDeterminer = ObjectTypeDeterminerFactory.getInstance();
        try {
            ObjectTypeDeterminerFactory.setInstance(null);
            assertEquals(ObjectTypeDeterminerFactory.getInstance(), objectTypeDeterminer);
            ObjectTypeDeterminerFactory.setInstance(new DefaultObjectTypeDeterminer());
            assertTrue(ObjectTypeDeterminerFactory.getInstance().getClass().equals(objectTypeDeterminer.getClass()));
        } finally {
            ObjectTypeDeterminerFactory.setInstance(objectTypeDeterminer);
        }
    }
}