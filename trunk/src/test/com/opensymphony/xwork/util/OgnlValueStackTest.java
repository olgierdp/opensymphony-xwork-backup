/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.math.BigDecimal;


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
        dog.setChildAges(new int[] {1, 2});

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
        dog.setChildAges(new int[] {1, 2});
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

    public void testThat() {
        OgnlValueStack vs = new OgnlValueStack();

        Dog dog1 = new Dog();
        dog1.setAge(12);
        dog1.setName("Rover");

        Dog dog2 = new Dog();
        dog2.setAge(1);
        dog2.setName("Jack");
        vs.push(dog1);
        vs.push(dog2);

        assertEquals(dog2, vs.findValue("that"));
        assertEquals("Jack", vs.findValue("that.name"));
    }

    public void testThatNull() {
        OgnlValueStack vs = new OgnlValueStack();

        assertNull(vs.findValue("that"));
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
}
