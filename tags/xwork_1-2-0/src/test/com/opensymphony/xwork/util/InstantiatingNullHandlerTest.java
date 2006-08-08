/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import java.util.Map;

import junit.framework.TestCase;
import ognl.OgnlContext;

import com.opensymphony.xwork.test.TestBean2;


/**
 * DOCUMENT ME!
 *
 * @author <a href='mailto:the_mindstorm[at]evolva[dot]ro'>Alexandru Popescu</a>
 * @version $Revision$
 */
public class InstantiatingNullHandlerTest extends TestCase {

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
    
    public void testNullPropertyValueWithoutCreateNullObjects() {
        Map context = new OgnlContext();
        context.put(InstantiatingNullHandler.CREATE_NULL_OBJECTS, Boolean.FALSE);
        
        CompoundRoot root = new CompoundRoot();
        TestBean2 testBean =  new TestBean2();
        root.add(testBean);
        
        InstantiatingNullHandler nh = new InstantiatingNullHandler();
        
        Object cat = nh.nullPropertyValue(context, root, "cat");
        
        assertNull(cat); 
    }
    
    public void testNullPropertyValue() {
        Map context = new OgnlContext();
        context.put(InstantiatingNullHandler.CREATE_NULL_OBJECTS, Boolean.TRUE);
        
        CompoundRoot root = new CompoundRoot();
        TestBean2 testBean =  new TestBean2();
        root.add(testBean);
        
        InstantiatingNullHandler nh = new InstantiatingNullHandler();
        
        Object cat = nh.nullPropertyValue(context, root, "cat");
        
        assertNotNull(cat);
        assertEquals(Cat.class, cat.getClass()); 
    }
 
    public void testNullPropertyValueWithWrongInnerBean() {
        Map context = new OgnlContext();
        context.put(InstantiatingNullHandler.CREATE_NULL_OBJECTS, Boolean.TRUE);
        
        CompoundRoot root = new CompoundRoot();
        TestBean3 testBean =  new TestBean3();
        root.add(testBean);
        
        InstantiatingNullHandler nh = new InstantiatingNullHandler();
        
        Object cat = nh.nullPropertyValue(context, root, "cat");
        
        assertNotNull(cat);
        assertEquals(Cat.class, cat.getClass()); 
        
        Object anotherBean = nh.nullPropertyValue(context, root, "anotherBean");

        assertNull(anotherBean);
    }
    
    private static class TestBean3 extends TestBean2 {
        private AnotherBean anotherBean;
        
        public void setAnotherBean(AnotherBean bean) {
            this.anotherBean = bean;
        }
        
        public AnotherBean getAnotherBean() {
            return this.anotherBean;
        }
    }
    
    private static class AnotherBean {
        public AnotherBean(int dummyCtorParam) {
        }
    }
}
