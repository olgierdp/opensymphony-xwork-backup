/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.XworkException;

import junit.framework.TestCase;

import ognl.NullHandler;
import ognl.Ognl;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.*;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class OgnlUtilTest extends TestCase {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void testCanSetADependentObject() {
        String dogName = "fido";

        OgnlRuntime.setNullHandler(Owner.class, new NullHandler() {
                public Object nullMethodResult(Map map, Object o, String s, Object[] objects) {
                    System.out.println("nullMethodResult");

                    return null;
                }

                public Object nullPropertyValue(Map map, Object o, Object o1) {
                    System.out.println("nullPropertyValue");
                    System.out.println("map -- " + map);
                    System.out.println("o   -- " + o);
                    System.out.println("o1  -- " + o1.getClass().getName());

                    String methodName = o1.toString();
                    String getter = "set" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
                    Method[] methods = o.getClass().getDeclaredMethods();
                    System.out.println(getter);

                    for (int i = 0; i < methods.length; i++) {
                        String name = methods[i].getName();

                        if (!getter.equals(name) || (methods[i].getParameterTypes().length != 1)) {
                            continue;
                        } else {
                            Class clazz = methods[i].getParameterTypes()[0];

                            try {
                                Object param = clazz.newInstance();
                                methods[i].invoke(o, new Object[] {param});

                                return param;
                            } catch (InstantiationException e) {
                                e.printStackTrace(); //To change body of catch statement use Options | File Templates.
                            } catch (IllegalAccessException e) {
                                e.printStackTrace(); //To change body of catch statement use Options | File Templates.
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace(); //To change body of catch statement use Options | File Templates.
                            } catch (InvocationTargetException e) {
                                e.printStackTrace(); //To change body of catch statement use Options | File Templates.
                            }
                        }
                    }

                    return null;
                }
            });

        Owner owner = new Owner();
        Map context = Ognl.createDefaultContext(owner);
        HashMap props = new HashMap();
        props.put("dog.name", dogName);

        OgnlUtil.setProperties(props, owner, context);
        assertNotNull("expected Ognl to create an instance of Dog", owner.getDog());
        assertEquals(dogName, owner.getDog().getName());

        try {
            System.out.println("dog.name == " + Ognl.getValue("dog.name", new Owner()));
        } catch (OgnlException e) {
            e.printStackTrace(); //To change body of catch statement use Options | File Templates.
        }
    }

    public void testCanSetDependentObjectArray() {
        EmailAction action = new EmailAction();
        Map context = Ognl.createDefaultContext(action);

        HashMap props = new HashMap();
        props.put("email[0].address", "addr1");
        props.put("email[1].address", "addr2");
        props.put("email[2].address", "addr3");

        OgnlUtil.setProperties(props, action, context);
        assertEquals(3, action.email.size());
        assertEquals("addr1", action.email.get(0).toString());
        assertEquals("addr2", action.email.get(1).toString());
        assertEquals("addr3", action.email.get(2).toString());
    }

    public void testCopySameType() {
        Foo foo1 = new Foo();
        Foo foo2 = new Foo();

        Map context = Ognl.createDefaultContext(foo1);

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal.set(Calendar.DAY_OF_MONTH, 12);
        cal.set(Calendar.YEAR, 1982);

        foo1.setTitle("blah");
        foo1.setNumber(1);
        foo1.setPoints(new long[] {1, 2, 3});
        foo1.setBirthday(cal.getTime());
        foo1.setUseful(false);

        OgnlUtil.copy(foo1, foo2, context);

        assertEquals(foo1.getTitle(), foo2.getTitle());
        assertEquals(foo1.getNumber(), foo2.getNumber());
        assertEquals(foo1.getPoints(), foo2.getPoints());
        assertEquals(foo1.getBirthday(), foo2.getBirthday());
        assertEquals(foo1.isUseful(), foo2.isUseful());
    }

    public void testCopyUnevenObjects() {
        Foo foo = new Foo();
        Bar bar = new Bar();

        Map context = Ognl.createDefaultContext(foo);

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal.set(Calendar.DAY_OF_MONTH, 12);
        cal.set(Calendar.YEAR, 1982);

        foo.setTitle("blah");
        foo.setNumber(1);
        foo.setPoints(new long[] {1, 2, 3});
        foo.setBirthday(cal.getTime());
        foo.setUseful(false);

        OgnlUtil.copy(foo, bar, context);

        assertEquals(foo.getTitle(), bar.getTitle());
        assertEquals(0, bar.getSomethingElse());
    }

    public void testDeepSetting() {
        Foo foo = new Foo();
        foo.setBar(new Bar());

        Map context = Ognl.createDefaultContext(foo);

        HashMap props = new HashMap();
        props.put("bar.title", "i am barbaz");
        OgnlUtil.setProperties(props, foo, context);

        assertEquals(foo.getBar().getTitle(), "i am barbaz");
    }

    public void testExceptionForUnmatchedGetterAndSetterWithThrowPropertyException() {
        Map props = new HashMap();
        props.put("myIntegerProperty", new Integer(1234));

        TestObject testObject = new TestObject();

        try {
            OgnlUtil.setProperties(props, testObject, true);
            fail("should rise IllegalAccessException because of Wrong getter method");
        } catch (Exception e) {
            //expected
        }
    }

    public void testExceptionForWrongPropertyNameWithThrowPropertyException() {
        Map props = new HashMap();
        props.put("myStringProperty", "testString");

        TestObject testObject = new TestObject();

        try {
            OgnlUtil.setProperties(props, testObject, true);
            fail("Should rise NoSuchPropertyException because of wrong property name");
        } catch (Exception e) {
            //expected
        }
    }

    public void testOgnlHandlesCrapAtTheEndOfANumber() {
        Foo foo = new Foo();
        Map context = Ognl.createDefaultContext(foo);

        HashMap props = new HashMap();
        props.put("aLong", "123a");

        OgnlUtil.setProperties(props, foo, context);
        assertEquals(0, foo.getALong());
    }

    public void testSetPropertiesBoolean() {
        Foo foo = new Foo();

        Map context = Ognl.createDefaultContext(foo);

        HashMap props = new HashMap();
        props.put("useful", "true");
        OgnlUtil.setProperties(props, foo, context);

        assertEquals(true, foo.isUseful());

        props = new HashMap();
        props.put("useful", "false");
        OgnlUtil.setProperties(props, foo, context);

        assertEquals(false, foo.isUseful());
    }

    public void testSetPropertiesDate() {
        Locale orig = Locale.getDefault();
        Locale.setDefault(Locale.US);

        Foo foo = new Foo();

        Map context = Ognl.createDefaultContext(foo);

        HashMap props = new HashMap();
        props.put("birthday", "02/12/1982");
        OgnlUtil.setProperties(props, foo, context);

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal.set(Calendar.DAY_OF_MONTH, 12);
        cal.set(Calendar.YEAR, 1982);

        assertEquals(cal.getTime(), foo.getBirthday());
        Locale.setDefault(orig);
    }

    public void testSetPropertiesInt() {
        Foo foo = new Foo();

        Map context = Ognl.createDefaultContext(foo);

        HashMap props = new HashMap();
        props.put("number", "2");
        OgnlUtil.setProperties(props, foo, context);

        assertEquals(2, foo.getNumber());
    }

    public void testSetPropertiesLongArray() {
        Foo foo = new Foo();

        Map context = Ognl.createDefaultContext(foo);

        HashMap props = new HashMap();
        props.put("points", new String[] {"1", "2"});
        OgnlUtil.setProperties(props, foo, context);

        assertNotNull(foo.getPoints());
        assertEquals(2, foo.getPoints().length);
        assertEquals(1, foo.getPoints()[0]);
        assertEquals(2, foo.getPoints()[1]);
    }

    public void testSetPropertiesString() {
        Foo foo = new Foo();

        Map context = Ognl.createDefaultContext(foo);

        HashMap props = new HashMap();
        props.put("title", "this is a title");
        OgnlUtil.setProperties(props, foo, context);

        assertEquals(foo.getTitle(), "this is a title");
    }

    public void testSetProperty() {
        Foo foo = new Foo();
        Map context = Ognl.createDefaultContext(foo);
        assertFalse(123456 == foo.getNumber());
        OgnlUtil.setProperty("number", "123456", foo, context);
        assertEquals(123456, foo.getNumber());
    }

    public void testStringToLong() {
        Foo foo = new Foo();

        Map context = Ognl.createDefaultContext(foo);

        HashMap props = new HashMap();
        props.put("aLong", "123");

        OgnlUtil.setProperties(props, foo, context);
        assertEquals(123, foo.getALong());

        props.put("aLong", new String[] {"123"});

        foo.setALong(0);
        OgnlUtil.setProperties(props, foo, context);
        assertEquals(123, foo.getALong());
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    public static class Email {
        String address;

        public void setAddress(String address) {
            this.address = address;
        }

        public String toString() {
            return address;
        }
    }

    static class TestObject {
        private Integer myIntegerProperty;
        private Long myLongProperty;
        private String myStrProperty;

        public void setMyIntegerProperty(Integer myIntegerProperty) {
            this.myIntegerProperty = myIntegerProperty;
        }

        public String getMyIntegerProperty() {
            return myIntegerProperty.toString();
        }

        public void setMyLongProperty(Long myLongProperty) {
            this.myLongProperty = myLongProperty;
        }

        public Long getMyLongProperty() {
            return myLongProperty;
        }

        public void setMyStrProperty(String myStrProperty) {
            this.myStrProperty = myStrProperty;
        }

        public String getMyStrProperty() {
            return myStrProperty;
        }
    }

    class EmailAction {
        public List email = new OgnlList(Email.class);

        public List getEmail() {
            return this.email;
        }
    }

    class OgnlList extends ArrayList {
        private Class clazz;

        public OgnlList(Class clazz) {
            this.clazz = clazz;
        }

        public synchronized Object get(int index) {
            while (index >= this.size()) {
                try {
                    this.add(clazz.newInstance());
                } catch (Exception e) {
                    throw new XworkException(e);
                }
            }

            return super.get(index);
        }
    }
}
