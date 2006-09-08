/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.*;
import com.opensymphony.xwork.test.TestBean2;
import com.opensymphony.xwork.util.OgnlValueStack;

import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Unit test for OgnlValueStack.
 */
public class OgnlValueStackTest extends XWorkTestCase {

    public static Integer staticNullMethod() {
        return null;
    }

    public void testExpOverridesCanStackExpUp() throws Exception {
    	Map expr1 = new LinkedHashMap();
    	expr1.put("expr1", "'expr1value'");
    	
    	OgnlValueStack vs = new OgnlValueStack();
    	vs.setExprOverrides(expr1);
    	
    	assertEquals(vs.findValue("expr1"), "expr1value");
    	
    	Map expr2 = new LinkedHashMap();
    	expr2.put("expr2", "'expr2value'");
    	expr2.put("expr3", "'expr3value'");
    	vs.setExprOverrides(expr2);
    	
    	assertEquals(vs.findValue("expr2"), "expr2value");
    	assertEquals(vs.findValue("expr3"), "expr3value");
    }
    
    public void testArrayAsString() {
        OgnlValueStack vs = new OgnlValueStack();

        Dog dog = new Dog();
        dog.setAge(12);
        dog.setName("Rover");
        dog.setChildAges(new int[]{1, 2});

        vs.push(dog);
        assertEquals("1, 2", vs.findValue("childAges", String.class));
    }

    public void testBasic() {
        OgnlValueStack vs = new OgnlValueStack();

        Dog dog = new Dog();
        dog.setAge(12);
        dog.setName("Rover");

        vs.push(dog);
        assertEquals("Rover", vs.findValue("name", String.class));
    }

    public void testCallMethodOnNullObject() {
        OgnlValueStack stack = new OgnlValueStack();
        assertNull(stack.findValue("foo.size()"));
    }

    public void testCallMethodThatThrowsExceptionTwice() {
        SimpleAction action = new SimpleAction();
        OgnlValueStack stack = new OgnlValueStack();
        stack.push(action);

        action.setThrowException(true);
        assertNull(stack.findValue("exceptionMethod1()"));
        action.setThrowException(false);
        assertEquals("OK", stack.findValue("exceptionMethod()"));
    }

    public void testCallMethodWithNullArg() {
        SimpleAction action = new SimpleAction();
        OgnlValueStack stack = new OgnlValueStack();
        stack.push(action);

        stack.findValue("setName(blah)");
        assertNull(action.getName());

        action.setBlah("blah");
        stack.findValue("setName(blah)");
        assertEquals("blah", action.getName());
    }

    public void testConvertStringArrayToList() {
        Foo foo = new Foo();
        OgnlValueStack vs = new OgnlValueStack();
        vs.push(foo);

        vs.setValue("strings", new String[]{"one", "two"});

        assertNotNull(foo.getStrings());
        assertEquals("one", foo.getStrings().get(0));
        assertEquals("two", foo.getStrings().get(1));
    }

    public void testFindValueWithConversion() {

        // register converter
        TestBean2 tb2 = new TestBean2();

        OgnlValueStack stack = new OgnlValueStack();
        stack.push(tb2);
        Map myContext = stack.getContext();

        Map props = new HashMap();
        props.put("cat", "Kitty");
        OgnlUtil.setProperties(props, tb2, myContext);
        // expect String to be converted into a Cat
        assertEquals("Kitty", tb2.getCat().getName());

        // findValue should be able to access the name
        Object value = stack.findValue("cat.name == 'Kitty'", Boolean.class);
        assertNotNull(value);
        assertEquals(Boolean.class, value.getClass());
        assertEquals(Boolean.TRUE, value);

        value = stack.findValue("cat == null", Boolean.class);
        assertNotNull(value);
        assertEquals(Boolean.class, value.getClass());
        assertEquals(Boolean.FALSE, value);
    }


    public void testDeepProperties() {
        OgnlValueStack vs = new OgnlValueStack();

        Cat cat = new Cat();
        cat.setName("Smokey");

        Dog dog = new Dog();
        dog.setAge(12);
        dog.setName("Rover");
        dog.setChildAges(new int[]{1, 2});
        dog.setHates(cat);

        vs.push(dog);
        assertEquals("Smokey", vs.findValue("hates.name", String.class));
    }

    public void testFooBarAsString() {
        OgnlValueStack vs = new OgnlValueStack();
        Foo foo = new Foo();
        Bar bar = new Bar();
        bar.setTitle("blah");
        bar.setSomethingElse(123);
        foo.setBar(bar);

        vs.push(foo);
        assertEquals("blah:123", vs.findValue("bar", String.class));
    }

    public void testGetBarAsString() {
        Foo foo = new Foo();
        Bar bar = new Bar();
        bar.setTitle("bar");
        bar.setSomethingElse(123);
        foo.setBar(bar);

        OgnlValueStack vs = new OgnlValueStack();
        vs.push(foo);

        String output = (String) vs.findValue("bar", String.class);
        assertEquals("bar:123", output);
    }

    public void testGetComplexBarAsString() {
        // children foo->foo->foo
        Foo foo = new Foo();
        Foo foo2 = new Foo();
        foo.setChild(foo2);

        Foo foo3 = new Foo();
        foo2.setChild(foo3);

        // relatives
        Foo fooA = new Foo();
        foo.setRelatives(new Foo[]{fooA});

        Foo fooB = new Foo();
        foo2.setRelatives(new Foo[]{fooB});

        Foo fooC = new Foo();
        foo3.setRelatives(new Foo[]{fooC});

        // the bar
        Bar bar = new Bar();
        bar.setTitle("bar");
        bar.setSomethingElse(123);

        // now place the bar all over
        foo.setBar(bar);
        foo2.setBar(bar);
        foo3.setBar(bar);
        fooA.setBar(bar);
        fooB.setBar(bar);
        fooC.setBar(bar);

        OgnlValueStack vs = new OgnlValueStack();
        vs.push(foo);

        vs.getContext().put("foo", foo);

        assertEquals("bar:123", vs.findValue("#foo.bar", String.class));
        assertEquals("bar:123", vs.findValue("bar", String.class));
        assertEquals("bar:123", vs.findValue("child.bar", String.class));
        assertEquals("bar:123", vs.findValue("child.child.bar", String.class));
        assertEquals("bar:123", vs.findValue("relatives[0].bar", String.class));
        assertEquals("bar:123", vs.findValue("child.relatives[0].bar", String.class));
        assertEquals("bar:123", vs.findValue("child.child.relatives[0].bar", String.class));

        vs.push(vs.findValue("child"));
        assertEquals("bar:123", vs.findValue("bar", String.class));
        assertEquals("bar:123", vs.findValue("child.bar", String.class));
        assertEquals("bar:123", vs.findValue("relatives[0].bar", String.class));
        assertEquals("bar:123", vs.findValue("child.relatives[0].bar", String.class));
    }

    public void testGetNullValue() {
        Dog dog = new Dog();
        OgnlValueStack stack = new OgnlValueStack();
        stack.push(dog);
        assertNull(stack.findValue("name"));
    }

    public void testMapEntriesAvailableByKey() {
        Foo foo = new Foo();
        String title = "a title";
        foo.setTitle(title);

        OgnlValueStack vs = new OgnlValueStack();
        vs.push(foo);

        Map map = new HashMap();
        String a_key = "a";
        String a_value = "A";
        map.put(a_key, a_value);

        String b_key = "b";
        String b_value = "B";
        map.put(b_key, b_value);

        vs.push(map);

        assertEquals(title, vs.findValue("title"));
        assertEquals(a_value, vs.findValue(a_key));
        assertEquals(b_value, vs.findValue(b_key));
    }

    public void testMethodCalls() {
        OgnlValueStack vs = new OgnlValueStack();

        Dog dog1 = new Dog();
        dog1.setAge(12);
        dog1.setName("Rover");

        Dog dog2 = new Dog();
        dog2.setAge(1);
        dog2.setName("Jack");
        vs.push(dog1);
        vs.push(dog2);

        //assertEquals(new Boolean(false), vs.findValue("'Rover'.endsWith('Jack')"));
        //assertEquals(new Boolean(false), vs.findValue("'Rover'.endsWith(name)"));
        //assertEquals("RoverJack", vs.findValue("[1].name + name"));
        assertEquals(new Boolean(false), vs.findValue("[1].name.endsWith(name)"));

        assertEquals(new Integer(1 * 7), vs.findValue("computeDogYears()"));
        assertEquals(new Integer(1 * 2), vs.findValue("multiplyAge(2)"));
        assertEquals(new Integer(12 * 7), vs.findValue("[1].computeDogYears()"));
        assertEquals(new Integer(12 * 5), vs.findValue("[1].multiplyAge(5)"));
        assertNull(vs.findValue("thisMethodIsBunk()"));
        assertEquals(new Integer(12 * 1), vs.findValue("[1].multiplyAge(age)"));

        assertEquals("Jack", vs.findValue("name"));
        assertEquals("Rover", vs.findValue("[1].name"));

        //hates will be null
        assertEquals(Boolean.TRUE, vs.findValue("nullSafeMethod(hates)"));
    }

    public void testMismatchedGettersAndSettersCauseExceptionInSet() {
        OgnlValueStack vs = new OgnlValueStack();

        BadJavaBean bean = new BadJavaBean();
        vs.push(bean);

        try {
            vs.setValue("count", "1", true);
            fail("Expected an exception for mismatched getter and setter");
        } catch (XworkException e) {
            //expected
        }

        try {
            vs.setValue("count2", "a", true);
            fail("Expected an exception for mismatched getter and setter");
        } catch (XworkException e) {
            //expected
        }
    }

    public void testNoExceptionInSetForDefault() {
        OgnlValueStack vs = new OgnlValueStack();

        BadJavaBean bean = new BadJavaBean();
        vs.push(bean);

        try {
            vs.setValue("count", "1");
        } catch (XworkException e) {
            fail("Unexpected exception for mismatched getter and setter");
        }

        try {
            vs.setValue("count2", "a");
        } catch (XworkException e) {
            fail("Unexpected exception for mismatched getter and setter");
        }
    }

    public void testNullEntry() {
        OgnlValueStack vs = new OgnlValueStack();

        Dog dog = new Dog();
        dog.setName("Rover");

        vs.push(dog);
        assertEquals("Rover", vs.findValue("name", String.class));

        vs.push(null);
        assertEquals("Rover", vs.findValue("name", String.class));
    }

    public void testNullMethod() {
        Dog dog = new Dog();
        OgnlValueStack stack = new OgnlValueStack();
        stack.push(dog);
        assertNull(stack.findValue("nullMethod()"));
        assertNull(stack.findValue("@com.opensymphony.xwork.util.OgnlValueStackTest@staticNullMethod()"));
    }

    public void testPetSoarBug() {
        Cat cat = new Cat();
        cat.setFoo(new Foo());

        Bar bar = new Bar();
        bar.setTitle("bar");
        bar.setSomethingElse(123);
        cat.getFoo().setBar(bar);

        OgnlValueStack vs = new OgnlValueStack();
        vs.push(cat);

        assertEquals("bar:123", vs.findValue("foo.bar", String.class));
    }

    public void testPrimitiveSettingWithInvalidValueAddsFieldErrorInDevMode() {
        SimpleAction action = new SimpleAction();
        OgnlValueStack stack = new OgnlValueStack();
        stack.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        stack.getContext().put(ActionContext.DEV_MODE, Boolean.TRUE);
        stack.push(action);
        
        try {
            stack.setValue("bar", "3x");
            fail("Attempt to set 'bar' int property to '3x' should result in XworkException");
        }
        catch(XworkException re) {
            assertTrue(true);
        }

        Map conversionErrors = (Map) stack.getContext().get(ActionContext.CONVERSION_ERRORS);
        assertTrue(conversionErrors.containsKey("bar"));
    }

    public void testPrimitiveSettingWithInvalidValueAddsFieldErrorInNonDevMode() {
        SimpleAction action = new SimpleAction();
        OgnlValueStack stack = new OgnlValueStack();
        stack.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        stack.getContext().put(ActionContext.DEV_MODE, Boolean.FALSE);
        stack.push(action);
        stack.setValue("bar", "3x");

        Map conversionErrors = (Map) stack.getContext().get(ActionContext.CONVERSION_ERRORS);
        assertTrue(conversionErrors.containsKey("bar"));
    }
    

    public void testObjectSettingWithInvalidValueDoesNotCauseSetCalledWithNull() {
        SimpleAction action = new SimpleAction();
        action.setBean(new TestBean());
        OgnlValueStack stack = new OgnlValueStack();
        stack.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        stack.push(action);
        stack.setValue("bean", "foobar");

        Map conversionErrors = (Map) stack.getContext().get(ActionContext.CONVERSION_ERRORS);
        assertTrue(conversionErrors.containsKey("bean"));
        assertNotNull(action.getBean());
    }


    public void testSerializable() throws IOException, ClassNotFoundException {
        OgnlValueStack vs = new OgnlValueStack();

        Dog dog = new Dog();
        dog.setAge(12);
        dog.setName("Rover");

        vs.push(dog);
        assertEquals("Rover", vs.findValue("name", String.class));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(vs);
        oos.flush();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);

        OgnlValueStack newVs = (OgnlValueStack) ois.readObject();
        assertEquals("Rover", newVs.findValue("name", String.class));
    }

    public void testSetBarAsString() {
        Foo foo = new Foo();

        OgnlValueStack vs = new OgnlValueStack();
        vs.push(foo);

        vs.setValue("bar", "bar:123");

        assertEquals("bar", foo.getBar().getTitle());
        assertEquals(123, foo.getBar().getSomethingElse());
    }

    public void testSetDeepBarAsString() {
        Foo foo = new Foo();
        Foo foo2 = new Foo();
        foo.setChild(foo2);

        OgnlValueStack vs = new OgnlValueStack();
        vs.push(foo);

        vs.setValue("child.bar", "bar:123");

        assertEquals("bar", foo.getChild().getBar().getTitle());
        assertEquals(123, foo.getChild().getBar().getSomethingElse());
    }

    public void testSetNullList() {
        Foo foo = new Foo();
        OgnlValueStack vs = new OgnlValueStack();
        vs.getContext().put(InstantiatingNullHandler.CREATE_NULL_OBJECTS, Boolean.TRUE);
        vs.push(foo);

        vs.setValue("cats[0].name", "Cat One");
        vs.setValue("cats[1].name", "Cat Two");

        assertNotNull(foo.getCats());
        assertEquals(2, foo.getCats().size());
        assertEquals("Cat One", ((Cat) foo.getCats().get(0)).getName());
        assertEquals("Cat Two", ((Cat) foo.getCats().get(1)).getName());

        vs.setValue("cats[0].foo.cats[1].name", "Deep null cat");
        assertNotNull(((Cat) foo.getCats().get(0)).getFoo());
        assertNotNull(((Cat) foo.getCats().get(0)).getFoo().getCats());
        assertNotNull(((Cat) foo.getCats().get(0)).getFoo().getCats().get(1));
        assertEquals("Deep null cat", ((Cat) ((Cat) foo.getCats().get(0)).getFoo().getCats().get(1)).name);
    }

    public void testSetNullMap() {
        Foo foo = new Foo();
        OgnlValueStack vs = new OgnlValueStack();
        vs.getContext().put(InstantiatingNullHandler.CREATE_NULL_OBJECTS, Boolean.TRUE);
        vs.push(foo);

        vs.setValue("catMap['One'].name", "Cat One");
        vs.setValue("catMap['Two'].name", "Cat Two");

        assertNotNull(foo.getCatMap());
        assertEquals(2, foo.getCatMap().size());
        assertEquals("Cat One", ((Cat) foo.getCatMap().get("One")).getName());
        assertEquals("Cat Two", ((Cat) foo.getCatMap().get("Two")).getName());

        vs.setValue("catMap['One'].foo.catMap['Two'].name", "Deep null cat");
        assertNotNull(((Cat) foo.getCatMap().get("One")).getFoo());
        assertNotNull(((Cat) foo.getCatMap().get("One")).getFoo().getCatMap());
        assertNotNull(((Cat) foo.getCatMap().get("One")).getFoo().getCatMap().get("Two"));
        assertEquals("Deep null cat", ((Cat) ((Cat) foo.getCatMap().get("One")).getFoo().getCatMap().get("Two")).name);
    }

    public void testSetReallyDeepBarAsString() {
        Foo foo = new Foo();
        Foo foo2 = new Foo();
        foo.setChild(foo2);

        Foo foo3 = new Foo();
        foo2.setChild(foo3);

        OgnlValueStack vs = new OgnlValueStack();
        vs.push(foo);

        vs.setValue("child.child.bar", "bar:123");

        assertEquals("bar", foo.getChild().getChild().getBar().getTitle());
        assertEquals(123, foo.getChild().getChild().getBar().getSomethingElse());
    }

    public void testSettingDogGender() {
        OgnlValueStack vs = new OgnlValueStack();

        Dog dog = new Dog();
        vs.push(dog);

        vs.setValue("male", "false");

        assertEquals(false, dog.isMale());
    }

    public void testStatics() {
        OgnlValueStack vs = new OgnlValueStack();

        Cat cat = new Cat();
        vs.push(cat);

        Dog dog = new Dog();
        dog.setAge(12);
        dog.setName("Rover");
        vs.push(dog);

        assertEquals("Canine", vs.findValue("@vs@SCIENTIFIC_NAME"));
        assertEquals("Canine", vs.findValue("@vs1@SCIENTIFIC_NAME"));
        assertEquals("Feline", vs.findValue("@vs2@SCIENTIFIC_NAME"));
        assertEquals(new Integer(BigDecimal.ROUND_HALF_DOWN), vs.findValue("@java.math.BigDecimal@ROUND_HALF_DOWN"));
        assertNull(vs.findValue("@vs3@BLAH"));
        assertNull(vs.findValue("@com.nothing.here.Nothing@BLAH"));
    }

    public void testTop() {
        OgnlValueStack vs = new OgnlValueStack();

        Dog dog1 = new Dog();
        dog1.setAge(12);
        dog1.setName("Rover");

        Dog dog2 = new Dog();
        dog2.setAge(1);
        dog2.setName("Jack");
        vs.push(dog1);
        vs.push(dog2);

        assertEquals(dog2, vs.findValue("top"));
        assertEquals("Jack", vs.findValue("top.name"));
    }

    public void testTopIsDefaultTextProvider() {
        OgnlValueStack vs = new OgnlValueStack();

        assertEquals(DefaultTextProvider.INSTANCE, vs.findValue("top"));
    }

    public void testTwoDogs() {
        OgnlValueStack vs = new OgnlValueStack();

        Dog dog1 = new Dog();
        dog1.setAge(12);
        dog1.setName("Rover");

        Dog dog2 = new Dog();
        dog2.setAge(1);
        dog2.setName("Jack");
        vs.push(dog1);
        vs.push(dog2);

        assertEquals("Jack", vs.findValue("name"));
        assertEquals("Rover", vs.findValue("[1].name"));

        assertEquals(dog2, vs.pop());
        assertEquals("Rover", vs.findValue("name"));
    }

    public void testTypeConversionError() {
        TestBean bean = new TestBean();
        OgnlValueStack stack = new OgnlValueStack();
        stack.push(bean);
        stack.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        stack.setValue("count", "a");

        Map conversionErrors = (Map) stack.getContext().get(ActionContext.CONVERSION_ERRORS);
        assertTrue(conversionErrors.containsKey("count"));
    }

    public void testIsDevModeEnabled() {
        OgnlValueStack stack = new OgnlValueStack();
        assertFalse(stack.isDevModeEnabled());
        stack.getContext().put(ActionContext.DEV_MODE, Boolean.FALSE);
        assertFalse(stack.isDevModeEnabled());
        stack.getContext().put(ActionContext.DEV_MODE, Boolean.TRUE);
        assertTrue(stack.isDevModeEnabled());
    }
    
    public void testConstructorWithAStack() {
        OgnlValueStack stack = new OgnlValueStack();
        stack.push("Hello World");
        
        OgnlValueStack stack2 = new OgnlValueStack(stack);

        assertEquals(stack.getRoot(), stack2.getRoot());
        assertEquals(stack.peek(), stack2.peek());
        assertEquals("Hello World", stack2.pop());
        
        assertNotNull(OgnlValueStack.getAccessor());
    }

    public void testDefaultType() {
        OgnlValueStack stack = new OgnlValueStack();
        stack.setDefaultType(String.class);
        stack.push("Hello World");
        
        assertEquals("Hello World", stack.findValue("top"));
        assertEquals(null, stack.findValue(null));

        stack.setDefaultType(Integer.class);
        stack.push(new Integer(123));
        assertEquals(new Integer(123), stack.findValue("top"));
    }
    
    public void testFindString() {
        OgnlValueStack stack = new OgnlValueStack();
        stack.setDefaultType(Integer.class);
        stack.push("Hello World");
    	
        assertEquals("Hello World", stack.findString("top"));
        assertEquals(null, stack.findString(null));
    }

    public void testExpOverrides() {
    	Map overrides = new HashMap();
    	overrides.put("claus", "top");
    	
        OgnlValueStack stack = new OgnlValueStack();
        stack.setExprOverrides(overrides);
        stack.push("Hello World");
        
        assertEquals("Hello World", stack.findValue("claus"));
        assertEquals("Hello World", stack.findString("claus"));
        assertEquals("Hello World", stack.findValue("top"));
        assertEquals("Hello World", stack.findString("top"));

        assertEquals("Hello World", stack.findValue("claus", String.class));
        assertEquals("Hello World", stack.findValue("top", String.class));
        
        stack.getContext().put("santa", "Hello Santa");
        assertEquals("Hello Santa", stack.findValue("santa", String.class));
        assertEquals(null, stack.findValue("unknown", String.class));
    }

    class BadJavaBean {
        private int count;
        private int count2;

        public void setCount(int count) {
            this.count = count;
        }

        public String getCount() {
            return "" + count;
        }

        public void setCount2(String count2) {
            this.count2 = Integer.parseInt(count2);
        }

        public int getCount2() {
            return count2;
        }
    }
}
