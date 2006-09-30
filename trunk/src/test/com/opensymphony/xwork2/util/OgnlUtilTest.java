/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.interceptor.ChainingInterceptor;
import com.opensymphony.xwork2.test.User;

import ognl.*;

import java.lang.reflect.Method;
import java.util.*;


/**
 * Unit test of {@link OgnlUtil}.
 */
public class OgnlUtilTest extends XWorkTestCase {

    public void testCanSetADependentObject() throws Exception {
        String dogName = "fido";

        OgnlRuntime.setNullHandler(Owner.class, new NullHandler() {
            public Object nullMethodResult(Map map, Object o, String s, Object[] objects) {
                return null;
            }

            public Object nullPropertyValue(Map map, Object o, Object o1) {
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
                            methods[i].invoke(o, new Object[]{param});

                            return param;
                        } catch (Exception e) {
                        	throw new RuntimeException(e);
                        }
                    }
                }

                return null;
            }
        });

        Owner owner = new Owner();
        Map context = Ognl.createDefaultContext(owner);
        Map props = new HashMap();
        props.put("dog.name", dogName);

        OgnlUtil.setProperties(props, owner, context);
        assertNotNull("expected Ognl to create an instance of Dog", owner.getDog());
        assertEquals(dogName, owner.getDog().getName());
    }

    public void testCanSetDependentObjectArray() {
        EmailAction action = new EmailAction();
        Map context = Ognl.createDefaultContext(action);

        Map props = new HashMap();
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
        foo1.setPoints(new long[]{1, 2, 3});
        foo1.setBirthday(cal.getTime());
        foo1.setUseful(false);

        OgnlUtil.copy(foo1, foo2, context);

        assertEquals(foo1.getTitle(), foo2.getTitle());
        assertEquals(foo1.getNumber(), foo2.getNumber());
        assertEquals(foo1.getPoints(), foo2.getPoints());
        assertEquals(foo1.getBirthday(), foo2.getBirthday());
        assertEquals(foo1.isUseful(), foo2.isUseful());
    }


    public void testIncudeExcludes() {

        Foo foo1 = new Foo();
        Foo foo2 = new Foo();

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal.set(Calendar.DAY_OF_MONTH, 12);
        cal.set(Calendar.YEAR, 1982);

        foo1.setPoints(new long[]{1, 2, 3});
        foo1.setBirthday(cal.getTime());
        foo1.setUseful(false);


        foo1.setTitle("foo1 title");
        foo1.setNumber(1);

        foo2.setTitle("foo2 title");
        foo2.setNumber(2);

        Map context = Ognl.createDefaultContext(foo1);

        List excludes = new ArrayList();
        excludes.add("title");
        excludes.add("number");

        OgnlUtil.copy(foo1, foo2, context, excludes, null);
        // these values should remain unchanged in foo2
        assertEquals(foo2.getTitle(), "foo2 title");
        assertEquals(foo2.getNumber(), 2);

        // these values should be changed/copied
        assertEquals(foo1.getPoints(), foo2.getPoints());
        assertEquals(foo1.getBirthday(), foo2.getBirthday());
        assertEquals(foo1.isUseful(), foo2.isUseful());


        Bar b1 = new Bar();
        Bar b2 = new Bar();

        b1.setTitle("bar1 title");
        b1.setSomethingElse(10);


        b1.setId(new Long(1));

        b2.setTitle("");
        b2.setId(new Long(2));

        context = Ognl.createDefaultContext(b1);
        List includes = new ArrayList();
        includes.add("title");
        includes.add("somethingElse");

        OgnlUtil.copy(b1, b2, context, null, includes);
        // includes properties got copied
        assertEquals(b1.getTitle(), b2.getTitle());
        assertEquals(b1.getSomethingElse(), b2.getSomethingElse());

        // id properties did not
        assertEquals(b2.getId(), new Long(2));

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
        foo.setPoints(new long[]{1, 2, 3});
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

        Map props = new HashMap();
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

        Map props = new HashMap();
        props.put("aLong", "123a");

        OgnlUtil.setProperties(props, foo, context);
        assertEquals(0, foo.getALong());
    }

    /**
     * Test that type conversion is performed on indexed collection properties.
     */
    public void testSetIndexedValue() {
        ValueStack stack = ValueStackFactory.getFactory().createValueStack();
        Map stackContext = stack.getContext();
        stackContext.put(InstantiatingNullHandler.CREATE_NULL_OBJECTS, Boolean.TRUE);
        stackContext.put(XWorkMethodAccessor.DENY_METHOD_EXECUTION, Boolean.TRUE);
        stackContext.put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);

        User user = new User();
        stack.push(user);

        // indexed string w/ existing array
        user.setList(new ArrayList());
        user.getList().add("");

        String[] foo = new String[]{"asdf"};
        stack.setValue("list[0]", foo);
        assertNotNull(user.getList());
        assertEquals(1, user.getList().size());
        assertEquals(String.class, user.getList().get(0).getClass());
        assertEquals("asdf", user.getList().get(0));
    }

    public void testSetPropertiesBoolean() {
        Foo foo = new Foo();

        Map context = Ognl.createDefaultContext(foo);

        Map props = new HashMap();
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

        Map props = new HashMap();
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

        Map props = new HashMap();
        props.put("number", "2");
        OgnlUtil.setProperties(props, foo, context);

        assertEquals(2, foo.getNumber());
    }

    public void testSetPropertiesLongArray() {
        Foo foo = new Foo();

        Map context = Ognl.createDefaultContext(foo);

        Map props = new HashMap();
        props.put("points", new String[]{"1", "2"});
        OgnlUtil.setProperties(props, foo, context);

        assertNotNull(foo.getPoints());
        assertEquals(2, foo.getPoints().length);
        assertEquals(1, foo.getPoints()[0]);
        assertEquals(2, foo.getPoints()[1]);
    }

    public void testSetPropertiesString() {
        Foo foo = new Foo();

        Map context = Ognl.createDefaultContext(foo);

        Map props = new HashMap();
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


    public void testSetList() throws Exception {
        ChainingInterceptor foo = new ChainingInterceptor();
        ChainingInterceptor foo2 = new ChainingInterceptor();

        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null);
        SimpleNode expression = (SimpleNode) Ognl.parseExpression("{'a','ruby','b','tom'}");


        Ognl.getValue(expression, context, "aksdj");

        final ValueStack stack = ActionContext.getContext().getValueStack();

        Object result = Ognl.getValue(OgnlUtil.compile("{\"foo\",'ruby','b','tom'}"), context, foo);
        foo.setIncludes((Collection) result);

        assertEquals(4, foo.getIncludes().size());
        assertEquals("foo", foo.getIncludes().toArray()[0]);
        assertEquals("ruby", foo.getIncludes().toArray()[1]);
        assertEquals("b", "" + foo.getIncludes().toArray()[2]);
        assertEquals("tom", foo.getIncludes().toArray()[3]);

        Object result2 = Ognl.getValue(OgnlUtil.compile("{\"foo\",'ruby','b','tom'}"), context, foo2);
        OgnlUtil.setProperty("includes", result2, foo2, context);

        assertEquals(4, foo.getIncludes().size());
        assertEquals("foo", foo.getIncludes().toArray()[0]);
        assertEquals("ruby", foo.getIncludes().toArray()[1]);
        assertEquals("b", "" + foo.getIncludes().toArray()[2]);
        assertEquals("tom", foo.getIncludes().toArray()[3]);

        result = ActionContext.getContext().getValueStack().findValue("{\"foo\",'ruby','b','tom'}");

        foo.setIncludes((Collection) result);
        assertEquals(ArrayList.class, result.getClass());

        assertEquals(4, foo.getIncludes().size());
        assertEquals("foo", foo.getIncludes().toArray()[0]);
        assertEquals("ruby", foo.getIncludes().toArray()[1]);
        assertEquals("b", "" + foo.getIncludes().toArray()[2]);
        assertEquals("tom", foo.getIncludes().toArray()[3]);
    }


    public void testStringToLong() {
        Foo foo = new Foo();

        Map context = Ognl.createDefaultContext(foo);

        Map props = new HashMap();
        props.put("aLong", "123");

        OgnlUtil.setProperties(props, foo, context);
        assertEquals(123, foo.getALong());

        props.put("aLong", new String[]{"123"});

        foo.setALong(0);
        OgnlUtil.setProperties(props, foo, context);
        assertEquals(123, foo.getALong());
    }

    public void testNullProperties() {
        Foo foo = new Foo();
        foo.setALong(88);

        Map context = Ognl.createDefaultContext(foo);

        OgnlUtil.setProperties(null, foo, context);
        assertEquals(88, foo.getALong());

        Map props = new HashMap();
        props.put("aLong", "99");
        OgnlUtil.setProperties(props, foo, context);
        assertEquals(99, foo.getALong());
    }
    
    public void testCopyNull() {
        Foo foo = new Foo();
        Map context = Ognl.createDefaultContext(foo);
   		OgnlUtil.copy(null, null, context);

   		OgnlUtil.copy(foo, null, context);
   		OgnlUtil.copy(null, foo, context);
    }
    
    public void testGetTopTarget() throws Exception {
        Foo foo = new Foo();
        Map context = Ognl.createDefaultContext(foo);

        CompoundRoot root = new CompoundRoot();
        Object top = OgnlUtil.getRealTarget("top", context, root);
        assertEquals(root, top); // top should be root
        
        root.push(foo);
        Object val = OgnlUtil.getRealTarget("unknown", context, root);
        assertNull(val); // not found
    }
    
    public void testGetBeanMap() throws Exception {
    	Bar bar = new Bar();
    	bar.setTitle("I have beer");
        
    	Foo foo = new Foo();
        foo.setALong(123);
        foo.setNumber(44);
        foo.setBar(bar);
        foo.setTitle("Hello Santa");
        foo.setUseful(true);
        
        // just do some of the 15 tests
        Map beans = OgnlUtil.getBeanMap(foo);
        assertNotNull(beans);
        assertEquals(15, beans.size());
        assertEquals("Hello Santa", beans.get("title"));
        assertEquals(new Long("123"), beans.get("ALong"));
        assertEquals(new Integer("44"), beans.get("number"));
        assertEquals(bar, beans.get("bar"));
        assertEquals(Boolean.TRUE, beans.get("useful"));
    }

    public void testGetBeanMapNoReadMethod() throws Exception {
    	MyWriteBar bar = new MyWriteBar();
    	bar.setBar("Sams");
    	
    	Map beans = OgnlUtil.getBeanMap(bar);
    	assertEquals(2, beans.size());
    	assertEquals(new Integer("1"), beans.get("id"));
    	assertEquals("There is no read method for bar", beans.get("bar"));
    }

    /**
	 * XW-281
	 */
    public void testSetBigIndexedValue() {
        ValueStack stack = ValueStackFactory.getFactory().createValueStack();
        Map stackContext = stack.getContext();
        stackContext.put(InstantiatingNullHandler.CREATE_NULL_OBJECTS, Boolean.FALSE);
        stackContext.put(XWorkMethodAccessor.DENY_METHOD_EXECUTION, Boolean.TRUE);
        stackContext.put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);

        User user = new User();
        stack.push(user);

        // indexed string w/ existing array
        user.setList(new ArrayList());

        String[] foo = new String[]{"asdf"};
        stackContext.put(ActionContext.DEV_MODE, Boolean.TRUE);
        try {
            stack.setValue("list.1114778947765", foo);
            fail("non-valid expression: list.1114778947765"); 
        }
        catch(RuntimeException ex) {
            ; // it's oke
        }
        
        try {
            stack.setValue("1114778947765", foo);
            fail("non-valid expression: 1114778947765"); 
        }
        catch(RuntimeException ex) {
            ;
        }
        
        try {
            stack.setValue("1234", foo);
            fail("non-valid expression: 1114778947765"); 
        }
        catch(RuntimeException ex) {
            ;
        }
        
        stackContext.put(ActionContext.DEV_MODE, Boolean.FALSE);
        stack.setValue("list.1114778947765", foo);
        stack.setValue("1114778947765", foo);
        stack.setValue("1234", foo);
    }
    

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
                    throw new XWorkException(e);
                }
            }

            return super.get(index);
        }
    }
    
    private class MyWriteBar {
    	private int id;
    	
    	public int getId() {
    		return id;
    	}
    	
    	public void setBar(String name) {
    		if ("Sams".equals(name))
    			id = 1;
    		else
    			id = 999;
    	}
    	
    }
}
