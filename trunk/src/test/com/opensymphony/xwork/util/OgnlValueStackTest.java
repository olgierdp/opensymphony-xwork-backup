/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import junit.framework.TestCase;

import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


/**
 *
 *
 * @author $Author$
 * @version $Revision$
 */
public class OgnlValueStackTest extends TestCase {
    //~ Methods ////////////////////////////////////////////////////////////////

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

    public void testExceptionThrown() {
        OgnlValueStack vs = new OgnlValueStack();

        Dog dog1 = new Dog();
        dog1.setAge(12);
        dog1.setName("Rover");

        vs.push(dog1);

        try {
            vs.findValue("exception");
            fail("An exception was expected");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            vs.setValue("exception", "blah");
            fail("An exception was expected");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            vs.findValue("this");
        } catch (Exception e) {
            fail("an exception was not expected");
        }
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

    public void testThatNull() {
        OgnlValueStack vs = new OgnlValueStack();

        assertNull(vs.findValue("top"));
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

    public void testNullEntry() {
        OgnlValueStack vs = new OgnlValueStack();

        Dog dog = new Dog();
        dog.setName("Rover");

        vs.push(dog);
        assertEquals("Rover", vs.findValue("name", String.class));

        vs.push(null);
        assertEquals("Rover", vs.findValue("name", String.class));
    }

}
