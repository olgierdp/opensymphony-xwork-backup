package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminerFactory;
import com.opensymphony.xwork2.conversion.impl.DefaultObjectTypeDeterminer;

import junit.framework.*;

/**
 * ObjectTypeDeterminerFactoryTest test for given class.
 *
 * @author Rene Gielen
 */
public class ObjectTypeDeterminerFactoryTest extends TestCase {


    public void testDefaultInstanceTypeIsDefaultObjectTypeDeterminer() throws Exception {
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