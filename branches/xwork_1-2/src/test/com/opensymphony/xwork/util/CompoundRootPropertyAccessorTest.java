/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.XWorkTestCase;
import ognl.Node;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlRuntime;

/**
 * @author tmjee
 * @version $Date$ $Id$
 */
public class CompoundRootPropertyAccessorTest extends XWorkTestCase {


    public void testBasic() throws Exception {
        
        MyPrimitivesObject o = new MyPrimitivesObject();
        o.setMyShort((short)9);
        o.setMyShortObject(new Short((short)10));
        o.setMyByte((byte)111);
        o.setMyByteObject(new Byte((byte)112));
        o.setMyCharacter('a');
        o.setMyCharacterObject(new Character('b'));
        o.setMyDouble(1.1d);
        o.setMyDoubleObject(new Double(1.2d));
        o.setMyFloat(2.1f);
        o.setMyFloatObject(new Float(2.2));
        o.setMyLong(1);
        o.setMyLongObject(new Long(2));
        o.setMyInteger(3);
        o.setMyIntegerObject(new Integer(4));
        o.setMyString("hello tmjee");
        o.setMyBoolean(true);
        o.setMyBooleanObject(Boolean.TRUE);


        CompoundRootAccessor accessor = new CompoundRootAccessor();

        CompoundRoot root = new CompoundRoot();
        root.add(o);

        OgnlRuntime.setPropertyAccessor(CompoundRoot.class, accessor);

        OgnlContext ognlContext = (OgnlContext) Ognl.createDefaultContext(root, accessor);

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myInteger");
            assertEquals(node.getAccessor().get(ognlContext, root), new Integer(3));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myIntegerObject");
            assertEquals(node.getAccessor().get(ognlContext, root), new Integer(4));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myByte");
            assertEquals(node.getAccessor().get(ognlContext, root), new Byte((byte)111));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myByteObject");
            assertEquals(node.getAccessor().get(ognlContext, root), new Byte((byte)112));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myCharacter");
            assertEquals(node.getAccessor().get(ognlContext, root), new Character('a'));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myCharacterObject");
            assertEquals(node.getAccessor().get(ognlContext, root), new Character('b'));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myDouble");
            assertEquals(node.getAccessor().get(ognlContext, root), new Double(1.1d));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myDoubleObject");
            assertEquals(node.getAccessor().get(ognlContext, root), new Double(1.2d));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myFloat");
            assertEquals(node.getAccessor().get(ognlContext, root), new Float(2.1f));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myFloatObject");
            assertEquals(node.getAccessor().get(ognlContext, root), new Float(2.2f));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myLong");
            assertEquals(node.getAccessor().get(ognlContext, root), new Long(1));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myLongObject");
            assertEquals(node.getAccessor().get(ognlContext, root), new Long(2));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myString");
            assertEquals(node.getAccessor().get(ognlContext, root), "hello tmjee");
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myShort");
            assertEquals(node.getAccessor().get(ognlContext, root), new Short((short)9));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myShortObject");
            assertEquals(node.getAccessor().get(ognlContext, root), new Short((short)10));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myBoolean");
            assertEquals(node.getAccessor().get(ognlContext, root), Boolean.TRUE);
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myBooleanObject");
            assertEquals(node.getAccessor().get(ognlContext, root), Boolean.TRUE);
        }
    }


    public void testArray() throws Exception {
        CompoundRootAccessor accessor = new CompoundRootAccessor();

        MyPrimitiveArrayObject o = new MyPrimitiveArrayObject();
        o.setMyByte(new byte[] { (byte)110, (byte)111, (byte)112 });
        o.setMyByteObject(new Byte[] { new Byte((byte)98), new Byte((byte)99), new Byte((byte)100) });
        o.setMyShort(new short[] { (short)1, (short)2, (short)3 });
        o.setMyShortObject(new Short[] { new Short((short)4), new Short((short)5), new Short((short)6) });
        o.setMyCharacter(new char[] { 'a', 'b', 'c' });
        o.setMyCharacterObject(new Character[] { new Character('d'), new Character('e'), new Character('f') } );
        o.setMyString(new String[] { "one", "two", "three" });
        o.setMyInteger(new int[] { 1, 2, 3 });
        o.setMyIntegerObject(new Integer[] { new Integer(4), new Integer(5), new Integer(6) });
        o.setMyLong(new long[] { 1l, 2l, 3l });
        o.setMyLongObject(new Long[] { new Long(4l), new Long(5l), new Long(6l) });
        o.setMyFloat(new float[] { 1.1f, 2.2f, 3.3f });
        o.setMyFloatObject(new Float[] { new Float(4.4f), new Float(5.5f), new Float(6.6f) });
        o.setMyDouble(new double[] { 1.1d, 2.2d, 3.3d });
        o.setMyDoubleObject(new Double[] { new Double(4.4d), new Double(5.5d), new Double(6.6d)  });
        o.setMyBoolean(new boolean[] { true, false });
        o.setMyBooleanObject(new Boolean[] { Boolean.TRUE, Boolean.FALSE });


        CompoundRoot root = new CompoundRoot();
        root.add(o);

        OgnlContext ognlContext = (OgnlContext) Ognl.createDefaultContext(root, accessor);

        {   // byte[]
            Node node = Ognl.compileExpression(ognlContext, root, "myByte");
            assertTrue(node.getAccessor().get(ognlContext, root).getClass().isArray());
            assertEquals(node.getAccessor().get(ognlContext, root), o.getMyByte());
            assertEquals(((byte[])node.getAccessor().get(ognlContext, root)).length, 3);
            assertEquals(((byte[])node.getAccessor().get(ognlContext, root))[0], (byte)110);
            assertEquals(((byte[])node.getAccessor().get(ognlContext, root))[1], (byte)111);
            assertEquals(((byte[])node.getAccessor().get(ognlContext, root))[2], (byte)112);
        }

        {   // Byte[]
            Node node = Ognl.compileExpression(ognlContext, root, "myByteObject");
            assertTrue(node.getAccessor().get(ognlContext, root).getClass().isArray());
            assertEquals(node.getAccessor().get(ognlContext, root), o.getMyByteObject());
            assertEquals(((Byte[])node.getAccessor().get(ognlContext, root)).length, 3);
            assertEquals(((Byte[])node.getAccessor().get(ognlContext, root))[0], new Byte((byte)98));
            assertEquals(((Byte[])node.getAccessor().get(ognlContext, root))[1], new Byte((byte)99));
            assertEquals(((Byte[])node.getAccessor().get(ognlContext, root))[2], new Byte((byte)100));
        }


        {   // short[]
            Node node = Ognl.compileExpression(ognlContext, root, "myShort");
            assertTrue(node.getAccessor().get(ognlContext, root).getClass().isArray());
            assertEquals(node.getAccessor().get(ognlContext, root), o.getMyShort());
            assertEquals(((short[])node.getAccessor().get(ognlContext, root)).length, 3);
            assertEquals(((short[])node.getAccessor().get(ognlContext, root))[0], (short)1);
            assertEquals(((short[])node.getAccessor().get(ognlContext, root))[1], (short)2);
            assertEquals(((short[])node.getAccessor().get(ognlContext, root))[2], (short)3);
        }

        {   // Short[]
            Node node = Ognl.compileExpression(ognlContext, root, "myShortObject");
            assertTrue(node.getAccessor().get(ognlContext, root).getClass().isArray());
            assertEquals(node.getAccessor().get(ognlContext, root), o.getMyShortObject());
            assertEquals(((Short[])node.getAccessor().get(ognlContext, root)).length, 3);
            assertEquals(((Short[])node.getAccessor().get(ognlContext, root))[0], new Short((short)4));
            assertEquals(((Short[])node.getAccessor().get(ognlContext, root))[1], new Short((short)5));
            assertEquals(((Short[])node.getAccessor().get(ognlContext, root))[2], new Short((short)6));
        }

        {   // char[]
            Node node = Ognl.compileExpression(ognlContext, root, "myCharacter");
            assertTrue(node.getAccessor().get(ognlContext, root).getClass().isArray());
            assertEquals(node.getAccessor().get(ognlContext, root), o.getMyCharacter());
            assertEquals(((char[])node.getAccessor().get(ognlContext, root)).length, 3);
            assertEquals(((char[])node.getAccessor().get(ognlContext, root))[0], 'a');
            assertEquals(((char[])node.getAccessor().get(ognlContext, root))[1], 'b');
            assertEquals(((char[])node.getAccessor().get(ognlContext, root))[2], 'c');
        }

        {   // Character[]
            Node node = Ognl.compileExpression(ognlContext, root, "myCharacterObject");
            assertTrue(node.getAccessor().get(ognlContext, root).getClass().isArray());
            assertEquals(((Character[])node.getAccessor().get(ognlContext, root)).length, 3);
            assertEquals(((Character[])node.getAccessor().get(ognlContext, root))[0], new Character('d'));
            assertEquals(((Character[])node.getAccessor().get(ognlContext, root))[1], new Character('e'));
            assertEquals(((Character[])node.getAccessor().get(ognlContext, root))[2], new Character('f'));
        }

        {   // String[]
            Node node = Ognl.compileExpression(ognlContext, root, "myString");
            assertTrue(node.getAccessor().get(ognlContext, root).getClass().isArray());
            assertEquals(node.getAccessor().get(ognlContext, root), o.getMyString());
            assertEquals(((String[])node.getAccessor().get(ognlContext, root)).length, 3);
            assertEquals(((String[])node.getAccessor().get(ognlContext, root))[0], "one");
            assertEquals(((String[])node.getAccessor().get(ognlContext, root))[1], "two");
            assertEquals(((String[])node.getAccessor().get(ognlContext, root))[2], "three");
        }

        {   // int[]
            Node node = Ognl.compileExpression(ognlContext, root, "myInteger");
            assertTrue(node.getAccessor().get(ognlContext, root).getClass().isArray());
            assertEquals(node.getAccessor().get(ognlContext, root), o.getMyInteger());
            assertEquals(((int[])node.getAccessor().get(ognlContext, root)).length, 3);
            assertEquals(((int[])node.getAccessor().get(ognlContext, root))[0], 1);
            assertEquals(((int[])node.getAccessor().get(ognlContext, root))[1], 2);
            assertEquals(((int[])node.getAccessor().get(ognlContext, root))[2], 3);
        }

        {   // Integer[]
            Node node = Ognl.compileExpression(ognlContext, root, "myIntegerObject");
            assertTrue(node.getAccessor().get(ognlContext, root).getClass().isArray());
            assertEquals(((Integer[])node.getAccessor().get(ognlContext, root)).length, 3);
            assertEquals(((Integer[])node.getAccessor().get(ognlContext, root))[0], new Integer(4));
            assertEquals(((Integer[])node.getAccessor().get(ognlContext, root))[1], new Integer(5));
            assertEquals(((Integer[])node.getAccessor().get(ognlContext, root))[2], new Integer(6));
        }

        {   // long[]
            Node node = Ognl.compileExpression(ognlContext, root, "myLong");
            assertTrue(node.getAccessor().get(ognlContext, root).getClass().isArray());
            assertEquals(node.getAccessor().get(ognlContext, root), o.getMyLong());
            assertEquals(((long[])node.getAccessor().get(ognlContext, root))[0], 1l);
            assertEquals(((long[])node.getAccessor().get(ognlContext, root))[1], 2l);
            assertEquals(((long[])node.getAccessor().get(ognlContext, root))[2], 3l);
        }

        {   // Long[]
            Node node = Ognl.compileExpression(ognlContext, root, "myLongObject");
            assertTrue(node.getAccessor().get(ognlContext, root).getClass().isArray());
            assertEquals(node.getAccessor().get(ognlContext, root), o.getMyLongObject());
            assertEquals(((Long[])node.getAccessor().get(ognlContext, root))[0], new Long(4l));
            assertEquals(((Long[])node.getAccessor().get(ognlContext, root))[1], new Long(5l));
            assertEquals(((Long[])node.getAccessor().get(ognlContext, root))[2], new Long(6l));
        }

        {   // float[]
            Node node = Ognl.compileExpression(ognlContext, root, "myFloat");
            assertTrue(node.getAccessor().get(ognlContext, root).getClass().isArray());
            assertEquals(node.getAccessor().get(ognlContext, root), o.getMyFloat());
            assertEquals(((float[])node.getAccessor().get(ognlContext, root))[0], 1.1f, 2f);
            assertEquals(((float[])node.getAccessor().get(ognlContext, root))[1], 2.2f, 2f);
            assertEquals(((float[])node.getAccessor().get(ognlContext, root))[2], 3.3f, 2f);
        }

        {   // Float[]
            Node node = Ognl.compileExpression(ognlContext, root, "myFloatObject");
            assertTrue(node.getAccessor().get(ognlContext, root).getClass().isArray());
            assertEquals(node.getAccessor().get(ognlContext, root), o.getMyFloatObject());
            assertEquals(((Float[])node.getAccessor().get(ognlContext, root))[0], new Float(4.4f));
            assertEquals(((Float[])node.getAccessor().get(ognlContext, root))[1], new Float(5.5f));
            assertEquals(((Float[])node.getAccessor().get(ognlContext, root))[2], new Float(6.6f));
        }

        {   // double[]
            Node node = Ognl.compileExpression(ognlContext, root, "myDouble");
            assertTrue(node.getAccessor().get(ognlContext, root).getClass().isArray());
            assertEquals(node.getAccessor().get(ognlContext, root), o.getMyDouble());
            assertEquals(((double[])node.getAccessor().get(ognlContext, root))[0], 1.1d, 2d);
            assertEquals(((double[])node.getAccessor().get(ognlContext, root))[1], 2.2d, 2d);
            assertEquals(((double[])node.getAccessor().get(ognlContext, root))[2], 3.3d, 2d);
        }

        {   // Double[]
            Node node = Ognl.compileExpression(ognlContext, root, "myDoubleObject");
            assertTrue(node.getAccessor().get(ognlContext, root).getClass().isArray());
            assertEquals(node.getAccessor().get(ognlContext, root), o.getMyDoubleObject());
            assertEquals(((Double[])node.getAccessor().get(ognlContext, root))[0], new Double(4.4d));
            assertEquals(((Double[])node.getAccessor().get(ognlContext, root))[1], new Double(5.5d));
            assertEquals(((Double[])node.getAccessor().get(ognlContext, root))[2], new Double(6.6d));
        }

        {   // boolean[]
            Node node = Ognl.compileExpression(ognlContext, root, "myBoolean");
            assertTrue(node.getAccessor().get(ognlContext, root).getClass().isArray());
            assertEquals(node.getAccessor().get(ognlContext, root), o.getMyBoolean());
            assertEquals(((boolean[])node.getAccessor().get(ognlContext, root))[0], true);
            assertEquals(((boolean[])node.getAccessor().get(ognlContext, root))[1], false);
        }

        {   // Boolean[]
            Node node = Ognl.compileExpression(ognlContext, root, "myBooleanObject");
            assertTrue(node.getAccessor().get(ognlContext, root).getClass().isArray());
            assertEquals(node.getAccessor().get(ognlContext, root), o.getMyBooleanObject());
            assertEquals(((Boolean[])node.getAccessor().get(ognlContext, root))[0], Boolean.TRUE);
            assertEquals(((Boolean[])node.getAccessor().get(ognlContext, root))[1], Boolean.FALSE);
        }
    }


    public void testObjects() throws Exception {
        
        MyPrimitivesObject myPrimitiveObject = new MyPrimitivesObject();
        myPrimitiveObject.setMyLong(2l);
        myPrimitiveObject.setMyLongObject(new Long(3l));

        MyPrimitiveArrayObject myPrimitiveArrayObject = new MyPrimitiveArrayObject();
        myPrimitiveArrayObject.setMyFloat(new float[] { 1.1f, 2.2f, 3.3f });
        myPrimitiveArrayObject.setMyFloatObject(new Float[] { new Float(4.4f), new Float(5.5f), new Float(6.6f) });

        Address a = new Address();
        a.setStreet("High Street");
        a.setPobox("hd1 5pr");
        a.setMyArrayObject(myPrimitiveArrayObject);

        Person p = new Person();
        p.setName("tmjee");
        p.setAge(new Integer(28));
        p.setMyPrimitiveObject(myPrimitiveObject);
        p.setAddress(a);

        CompoundRoot root = new CompoundRoot();
        root.add(p);

        CompoundRootAccessor accessor = new CompoundRootAccessor();

        OgnlRuntime.setPropertyAccessor(CompoundRoot.class, accessor);

        OgnlContext ognlContext = (OgnlContext) Ognl.createDefaultContext(root);

        {
            Node node = Ognl.compileExpression(ognlContext, root, "name");
            assertEquals(node.getAccessor().get(ognlContext, root), "tmjee");
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myPrimitiveObject.myLong");
            assertEquals(node.getAccessor().get(ognlContext, root), new Long(2l));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myPrimitiveObject.myLongObject");
            assertEquals(node.getAccessor().get(ognlContext, root), new Long(3l));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "address.street");
            assertEquals(node.getAccessor().get(ognlContext, root), "High Street");
        }


        {
            Node node = Ognl.compileExpression(ognlContext, root, "address.myArrayObject.myFloat[0]");
            assertEquals(node.getAccessor().get(ognlContext, root), new Float(1.1f));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "address.myArrayObject.myFloat[1]");
            assertEquals(node.getAccessor().get(ognlContext, root), new Float(2.2f));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "address.myArrayObject.myFloat[2]");
            assertEquals(node.getAccessor().get(ognlContext, root), new Float(3.3f));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "address.myArrayObject.myFloatObject[0]");
            assertEquals(node.getAccessor().get(ognlContext, root), new Float(4.4f));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "address.myArrayObject.myFloatObject[1]");
            assertEquals(node.getAccessor().get(ognlContext, root), new Float(5.5f));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "address.myArrayObject.myFloatObject[2]");
            assertEquals(node.getAccessor().get(ognlContext, root), new Float(6.6f));
        }
    }


    public void testSpecialExpression() throws Exception {
        Person p1 = new Person();
        p1.setName("tmjee");
        p1.setAge(new Integer(28));

        Address a1 = new Address();
        a1.setStreet("High Street");
        a1.setPobox("1111");

        Person p2 = new Person();
        p2.setName("phil");
        p2.setAge(new Integer(40));

        Address a2 = new Address();
        a2.setStreet("Melbourne Street");
        a2.setPobox("222");


        CompoundRoot root = new CompoundRoot();
        root.add(p1);
        root.add(a1);
        root.add(p2);
        root.add(a2);

        OgnlContext ognlContext = (OgnlContext) Ognl.createDefaultContext(root);

        CompoundRootAccessor accessor = new CompoundRootAccessor();

        OgnlRuntime.setPropertyAccessor(CompoundRoot.class, accessor);

        {
            Node node = Ognl.compileExpression(ognlContext, root, "top");
            assertEquals(node.getAccessor().get(ognlContext, root), p1);
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "[1]");
            assertEquals(node.getAccessor().get(ognlContext, root).getClass(), CompoundRoot.class);
            assertEquals(((CompoundRoot)node.getAccessor().get(ognlContext, root)).size(), 3);
            assertEquals(((CompoundRoot)node.getAccessor().get(ognlContext, root)).get(0), a1);
            assertEquals(((CompoundRoot)node.getAccessor().get(ognlContext, root)).get(1), p2);
            assertEquals(((CompoundRoot)node.getAccessor().get(ognlContext, root)).get(2), a2);
        }


    }

}
