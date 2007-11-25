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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * There's an issue with OGNL checkout in  opensymphony svn at http://svn.opensymphony.com/svn/ognl/trunk
 * - first OgnlRuntime
 *          public static Object callMethod(OgnlContext context, Object target,
 *                                   String methodName, String propertyName, Object[] args)  throws OgnlException
 *    doesn't exists anymore but instead replaced by
 *          public static Object callMethod(OgnlContext context, Object target,
 *                                   String methodName, Object[] args)
 *
 *     See http://forums.opensymphony.com/thread.jspa?threadID=167692&tstart=0 for more info
 *
 * - ListPropertyAccessor, ArrayPropertyAccessor uses
 *    OgnlRuntime.getNumericValueGetter(....) which doesn't take into account primitive characters etc. 
 *
 *   See http://forums.opensymphony.com/thread.jspa?threadID=167693&tstart=0 for more info
 *
 * @author tmjee
 * @version $Date$ $Id$
 */
public class CompoundRootPropertyAccessorTest extends XWorkTestCase {


    public void testGetterBasic() throws Exception {
        
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


    public void testGetterArray() throws Exception {
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


    public void testGetterObjects() throws Exception {
        
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


    public void testGetterSpecialExpression() throws Exception {

        MyPrimitivesObject myPrimitiveObject1 = new MyPrimitivesObject();
        myPrimitiveObject1.setMyLong(2l);
        myPrimitiveObject1.setMyLongObject(new Long(3l));

        MyPrimitiveArrayObject myPrimitiveArrayObject1 = new MyPrimitiveArrayObject();
        myPrimitiveArrayObject1.setMyFloat(new float[] { 1.1f, 2.2f, 3.3f });
        myPrimitiveArrayObject1.setMyFloatObject(new Float[] { new Float(4.4f), new Float(5.5f), new Float(6.6f) });

        Address a1 = new Address();
        a1.setStreet("High Street");
        a1.setPobox("1111");
        a1.setMyArrayObject(myPrimitiveArrayObject1);

        Person p1 = new Person();
        p1.setName("tmjee");
        p1.setAge(new Integer(28));
        p1.setAddress(a1);
        p1.setMyPrimitiveObject(myPrimitiveObject1);

        MyPrimitivesObject myPrimitiveObject2 = new MyPrimitivesObject();
        myPrimitiveObject2.setMyLong(2l);
        myPrimitiveObject2.setMyLongObject(new Long(3l));

        MyPrimitiveArrayObject myPrimitiveArrayObject2 = new MyPrimitiveArrayObject();
        myPrimitiveArrayObject2.setMyFloat(new float[] { 1.1f, 2.2f, 3.3f });
        myPrimitiveArrayObject2.setMyFloatObject(new Float[] { new Float(4.4f), new Float(5.5f), new Float(6.6f) });

        Address a2 = new Address();
        a2.setStreet("Melbourne Street");
        a2.setPobox("222");
        a2.setMyArrayObject(myPrimitiveArrayObject2);

        Person p2 = new Person();
        p2.setName("phil");
        p2.setAge(new Integer(40));
        p2.setAddress(a2);
        p2.setMyPrimitiveObject(myPrimitiveObject2);


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

        {
            Node node = Ognl.compileExpression(ognlContext, root, "top.name");
            assertEquals(node.getAccessor().get(ognlContext, root), "tmjee");
        }

        {
            Node node= Ognl.compileExpression(ognlContext, root, "top.address.street");
            assertEquals(node.getAccessor().get(ognlContext, root), "High Street");
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "top.myPrimitiveObject.myLong");
            assertEquals(node.getAccessor().get(ognlContext, root), new Long(2l));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "top.myPrimitiveObject.myLongObject");
            assertEquals(node.getAccessor().get(ognlContext, root), new Long(3l));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "top.address.myArrayObject.myFloat[0]");
            assertEquals(node.getAccessor().get(ognlContext, root), new Float(1.1f));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "top.address.myArrayObject.myFloat[1]");
            assertEquals(node.getAccessor().get(ognlContext, root), new Float(2.2f));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "top.address.myArrayObject.myFloat[2]");
            assertEquals(node.getAccessor().get(ognlContext, root), new Float(3.3f));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "top.address.myArrayObject.myFloatObject[0]");
            assertEquals(node.getAccessor().get(ognlContext, root), new Float(4.4f));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "top.address.myArrayObject.myFloatObject[1]");
            assertEquals(node.getAccessor().get(ognlContext, root), new Float(5.5f));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "top.address.myArrayObject.myFloatObject[2]");
            assertEquals(node.getAccessor().get(ognlContext, root), new Float(6.6f));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "[2].name");
            assertEquals(node.getAccessor().get(ognlContext, root), "phil");
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "[2].address.street");
            assertEquals(node.getAccessor().get(ognlContext, root), "Melbourne Street");
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "[2].myPrimitiveObject.myLong");
            assertEquals(node.getAccessor().get(ognlContext, root), new Long(2l));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "[2].myPrimitiveObject.myLongObject");
            assertEquals(node.getAccessor().get(ognlContext, root), new Long(3l));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "[2].address.myArrayObject.myFloat[0]");
            assertEquals(node.getAccessor().get(ognlContext, root), new Float(1.1f));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "[2].address.myArrayObject.myFloat[1]");
            assertEquals(node.getAccessor().get(ognlContext, root), new Float(2.2f));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "[2].address.myArrayObject.myFloat[2]");
            assertEquals(node.getAccessor().get(ognlContext, root), new Float(3.3f));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "[2].address.myArrayObject.myFloatObject[0]");
            assertEquals(node.getAccessor().get(ognlContext, root), new Float(4.4f));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "[2].address.myArrayObject.myFloatObject[1]");
            assertEquals(node.getAccessor().get(ognlContext, root), new Float(5.5f));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "[2].address.myArrayObject.myFloatObject[2]");
            assertEquals(node.getAccessor().get(ognlContext, root), new Float(6.6f));
        }
    }


    public void testGetterList() throws Exception {
        CompoundRootAccessor accessor = new CompoundRootAccessor();

        final Person p = new Person();
        p.setName("tmjee");

        MyPrimitiveArrayObject o = new MyPrimitiveArrayObject();
        o.setMyList(new ArrayList() {
            {
                add("a string");
                add(new Integer(1));
                add(p);
            }
        });
        Address a = new Address();
        a.setMyArrayObject(o);

        OgnlRuntime.setPropertyAccessor(CompoundRootAccessor.class, accessor);

        CompoundRoot root = new CompoundRoot();
        root.add(a);

        OgnlContext ognlContext = (OgnlContext) Ognl.createDefaultContext(root);


        {
            Node node = Ognl.compileExpression(ognlContext, root, "myArrayObject.myList.size");
            assertEquals(node.getAccessor().get(ognlContext, root), new Integer(3));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myArrayObject.myList[0]");
            assertEquals(node.getAccessor().get(ognlContext, root), "a string");
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myArrayObject.myList.get(1)");
            assertEquals(node.getAccessor().get(ognlContext, root), new Integer(1));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myArrayObject.myList[2].name");
            assertEquals(node.getAccessor().get(ognlContext, root), "tmjee");
        }

    }

    public void testGetterMap() throws Exception {
        CompoundRootAccessor accessor = new CompoundRootAccessor();

        final Person p = new Person();
        p.setName("tmjee");

        MyPrimitiveArrayObject o = new MyPrimitiveArrayObject();
        o.setMyMap(new LinkedHashMap() {
            {
                put("key1", "a string");
                put("key2", new Integer(1));
                put("key3", p);
            }
        });

        Address a = new Address();
        a.setMyArrayObject(o);

        OgnlRuntime.setPropertyAccessor(CompoundRootAccessor.class, accessor);

        CompoundRoot root = new CompoundRoot();
        root.add(a);

        OgnlContext ognlContext = (OgnlContext) Ognl.createDefaultContext(root);

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myArrayObject.myMap.size");
            assertEquals(node.getAccessor().get(ognlContext, root), new Integer(3));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myArrayObject.myMap['key1']");
            assertEquals(node.getAccessor().get(ognlContext, root), "a string");
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myArrayObject.myMap.get('key2')");
            assertEquals(node.getAccessor().get(ognlContext, root), new Integer(1));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myArrayObject.myMap['key3'].name");
            assertEquals(node.getAccessor().get(ognlContext, root), "tmjee");
        }

    }


    public void testSetterBasic() throws Exception {

        MyPrimitivesObject p = new MyPrimitivesObject();

        CompoundRoot root = new CompoundRoot();
        root.add(p);

        CompoundRootAccessor accessor = new CompoundRootAccessor();
        OgnlRuntime.setPropertyAccessor(CompoundRoot.class, accessor);

        OgnlContext ognlContext = (OgnlContext) Ognl.createDefaultContext(root);


        {  // int
           assertEquals(p.getMyInteger(), 0);
           Node node = Ognl.compileExpression(ognlContext, root, "myInteger"); 
           node.getAccessor().set(ognlContext, root, new Integer(1));
           assertEquals(p.getMyInteger(), 1);
        }

        {   // Integer
            assertEquals(p.getMyIntegerObject(), null);
            Node node = Ognl.compileExpression(ognlContext, root, "myIntegerObject");
            node.getAccessor().set(ognlContext, root, new Integer(2));
            assertEquals(p.getMyIntegerObject(), new Integer(2));
        }

        {   // byte
            assertEquals(p.getMyByte(), (byte)0);
            Node node = Ognl.compileExpression(ognlContext, root, "myByte");
            node.getAccessor().set(ognlContext, root, new Byte((byte)1));
            assertEquals(p.getMyByte(), (byte)1);
        }

        {  // Byte
            assertEquals(p.getMyByteObject(), null);
            Node node = Ognl.compileExpression(ognlContext, root, "myByteObject");
            node.getAccessor().set(ognlContext, root, new Byte((byte)2));
            assertEquals(p.getMyByteObject(), new Byte((byte)2));
        }

        {   // short
            assertEquals(p.getMyShort(), (short)0);
            Node node = Ognl.compileExpression(ognlContext, root, "myShort") ;
            node.getAccessor().set(ognlContext, root, new Short((short)1));
            assertEquals(p.getMyShort(), (short)1);
        }

        {   // Short
            assertEquals(p.getMyShortObject(), null);
            Node node = Ognl.compileExpression(ognlContext, root, "myShortObject");
            node.getAccessor().set(ognlContext, root, new Short((short)2));
            assertEquals(p.getMyShortObject(), new Short((short)2));
        }

        {   // char
            assertEquals(p.getMyCharacter(), (char)0);
            Node node = Ognl.compileExpression(ognlContext, root, "myCharacter");
            node.getAccessor().set(ognlContext, root, new Character('a'));
            assertEquals(p.getMyCharacter(), 'a');
        }

        {   // Character
            assertEquals(p.getMyCharacterObject(), null);
            Node node = Ognl.compileExpression(ognlContext, root, "myCharacterObject");
            node.getAccessor().set(ognlContext, root, new Character('b'));
            assertEquals(p.getMyCharacterObject(), new Character('b'));
        }

        {   // long
            assertEquals(p.getMyLong(), 0l);
            Node node = Ognl.compileExpression(ognlContext, root, "myLong");
            node.getAccessor().set(ognlContext, root, new Long(1));
            assertEquals(p.getMyLong(), 1l);
        }

        {   // Long
            assertEquals(p.getMyLongObject(), null);
            Node node = Ognl.compileExpression(ognlContext, root, "myLongObject");
            node.getAccessor().set(ognlContext, root, new Long(2));
            assertEquals(p.getMyLongObject(), new Long(2));
        }

        {   // float
            assertEquals(p.getMyFloat(), 0f, 2f);
            Node node = Ognl.compileExpression(ognlContext, root, "myFloat");
            node.getAccessor().set(ognlContext, root, new Float(1f));
            assertEquals(p.getMyFloat(), 1f, 2f);
        }

        {   // Float
            assertEquals(p.getMyFloatObject(), null);
            Node node = Ognl.compileExpression(ognlContext, root, "myFloatObject");
            node.getAccessor().set(ognlContext, root, new Float(2f));
            assertEquals(p.getMyFloatObject(), new Float(2f));
        }

        {   // double
            assertEquals(p.getMyDouble(), 0d, 2d);
            Node node = Ognl.compileExpression(ognlContext, root, "myDouble");
            node.getAccessor().set(ognlContext, root, new Double(1d));
            assertEquals(p.getMyDouble(), 1d, 2d);
        }

        {   // Double
            assertEquals(p.getMyDoubleObject(), null);
            Node node = Ognl.compileExpression(ognlContext, root, "myDoubleObject");
            node.getAccessor().set(ognlContext, root, new Double(2d));
            assertEquals(p.getMyDoubleObject(), new Double(2d));
        }

        {   // String
            assertEquals(p.getMyString(), null);
            Node node = Ognl.compileExpression(ognlContext, root, "myString");
            node.getAccessor().set(ognlContext, root, "some string");
            assertEquals(p.getMyString(), "some string");
        }
    }

    public void testSetterArray() throws Exception {
        MyPrimitiveArrayObject p = new MyPrimitiveArrayObject();
        p.setMyShort(new short[2]);
        p.setMyShortObject(new Short[2]);
        p.setMyByte(new byte[2]);
        p.setMyByteObject(new Byte[2]);
        p.setMyCharacter(new char[2]);
        p.setMyCharacterObject(new Character[2]);
        p.setMyInteger(new int[2]);
        p.setMyIntegerObject(new Integer[2]);
        p.setMyLong(new long[2]);
        p.setMyLongObject(new Long[2]);
        p.setMyFloat(new float[2]);
        p.setMyFloatObject(new Float[2]);
        p.setMyDouble(new double[2]);
        p.setMyDoubleObject(new Double[2]);
        p.setMyString(new String[2]);


        CompoundRoot root = new CompoundRoot();
        root.add(p);

        CompoundRootAccessor accessor = new CompoundRootAccessor();
        OgnlRuntime.setPropertyAccessor(CompoundRoot.class, accessor);

        OgnlContext ognlContext = (OgnlContext) Ognl.createDefaultContext(root);

        {
            assertEquals(p.getMyShort()[0], (short)0);
            Node node = Ognl.compileExpression(ognlContext, root, "myShort[0]");
            node.getAccessor().set(ognlContext, root, new Short((short)2));
            assertEquals(p.getMyShort()[0], (short)2);
        }

        {
            assertEquals(p.getMyShortObject()[0], null);
            Node node = Ognl.compileExpression(ognlContext, root, "myShortObject[0]");
            node.getAccessor().set(ognlContext, root, new Short((short)1));
            assertEquals(p.getMyShortObject()[0], new Short((short)1));
        }

        {
            assertEquals(p.getMyByte()[0], (byte)0);
            Node node = Ognl.compileExpression(ognlContext, root, "myByte[0]");
            node.getAccessor().set(ognlContext, root, new Byte((byte)1));
            assertEquals(p.getMyByte()[0], (byte)1);
        }

        {
            assertEquals(p.getMyByteObject()[0], null);
            Node node = Ognl.compileExpression(ognlContext, root, "myByteObject[0]");
            node.getAccessor().set(ognlContext, root, new Byte((byte)1));
            assertEquals(p.getMyByteObject()[0], new Byte((byte)1));
        }

        /*{
            // NOTE: We need to uncomment this when ognl could handle character properly.


            assertEquals(p.getMyCharacter()[0], (char)0);
            Node node = Ognl.compileExpression(ognlContext, root, "myCharacter[0]");
            node.getAccessor().set(ognlContext, root, new Character('a'));
            assertEquals(p.getMyCharacter()[0], 'a');
        }*/

        {
            assertEquals(p.getMyCharacterObject()[0], null);
            Node node = Ognl.compileExpression(ognlContext, root, "myCharacterObject[0]");
            node.getAccessor().set(ognlContext, root, new Character('a'));
            assertEquals(p.getMyCharacterObject()[0], new Character('a'));
        }

        {
            assertEquals(p.getMyInteger()[0], 0);
            Node node = Ognl.compileExpression(ognlContext, root, "myInteger[0]");
            node.getAccessor().set(ognlContext, root, new Integer(1));
            assertEquals(p.getMyInteger()[0], 1);
        }

        {
            assertEquals(p.getMyIntegerObject()[0], null);
            Node node = Ognl.compileExpression(ognlContext, root, "myIntegerObject[0]");
            node.getAccessor().set(ognlContext, root, new Integer(1));
            assertEquals(p.getMyIntegerObject()[0], new Integer(1));
        }

        {
            assertEquals(p.getMyLong()[0], 0l);
            Node node = Ognl.compileExpression(ognlContext, root, "myLong[0]");
            node.getAccessor().set(ognlContext, root, new Long(1));
            assertEquals(p.getMyLong()[0], 1l);
        }

        {
            assertEquals(p.getMyLongObject()[0], null);
            Node node=  Ognl.compileExpression(ognlContext, root, "myLongObject[0]");
            node.getAccessor().set(ognlContext, root, new Long(1));
            assertEquals(p.getMyLongObject()[0], new Long(1));
        }

        {
            assertEquals(p.getMyFloat()[0], 0f, 2f);
            Node node = Ognl.compileExpression(ognlContext, root, "myFloat[0]");
            node.getAccessor().set(ognlContext, root, new Float(1f));
            assertEquals(p.getMyFloat()[0], 1f, 2f);
        }

        {
            assertEquals(p.getMyFloatObject()[0], null);
            Node node = Ognl.compileExpression(ognlContext, root, "myFloatObject[0]");
            node.getAccessor().set(ognlContext, root, new Float(1f));
            assertEquals(p.getMyFloatObject()[0], new Float(1f));    
        }

        {
            assertEquals(p.getMyDouble()[0], 0d, 2d);
            Node node = Ognl.compileExpression(ognlContext, root, "myDouble[0]");
            node.getAccessor().set(ognlContext, root, new Double(1d));
            assertEquals(p.getMyDouble()[0], 1d, 2d);
        }

        {
            assertEquals(p.getMyDoubleObject()[0], null);
            Node node = Ognl.compileExpression(ognlContext, root, "myDoubleObject[0]");
            node.getAccessor().set(ognlContext, root, new Double(1d));
            assertEquals(p.getMyDoubleObject()[0], new Double(1d));
        }

        {
            assertEquals(p.getMyString()[0], null);
            Node node = Ognl.compileExpression(ognlContext, root, "myString[0]");
            node.getAccessor().set(ognlContext, root, "hello tmjee");
            assertEquals(p.getMyString()[0], "hello tmjee");
        }
    }


    public void testSetterObjectsTest() throws Exception {

        MyPrimitivesObject po = new MyPrimitivesObject();

        MyPrimitiveArrayObject pao = new MyPrimitiveArrayObject();
        pao.setMyInteger(new int[2]);
        pao.setMyIntegerObject(new Integer[2]);
        pao.setMyList(new ArrayList(){
            {
                add("xxx");
            }
        });
        pao.setMyMap(new LinkedHashMap());

        Address a = new Address();
        a.setMyArrayObject(pao);

        Person p = new Person();
        p.setAddress(a);
        p.setMyPrimitiveObject(po);

        CompoundRootAccessor accessor = new CompoundRootAccessor();
        OgnlRuntime.setPropertyAccessor(CompoundRoot.class, accessor);
        CompoundRoot root = new CompoundRoot();
        root.add(p);

        OgnlContext ognlContext = (OgnlContext) Ognl.createDefaultContext(root);

        {
            Node node = Ognl.compileExpression(ognlContext, root, "name");
            node.getAccessor().set(ognlContext, root, "tmjee");
            assertEquals(p.getName(), "tmjee");
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "address.street");
            node.getAccessor().set(ognlContext, root, "High Street");
            assertEquals(p.getAddress().getStreet(), "High Street");
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myPrimitiveObject.myInteger");
            node.getAccessor().set(ognlContext, root, new Integer(2));
            assertEquals(p.getMyPrimitiveObject().getMyInteger(), 2);
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "myPrimitiveObject.myIntegerObject");
            node.getAccessor().set(ognlContext, root, new Integer(1));
            assertEquals(p.getMyPrimitiveObject().getMyIntegerObject(), new Integer(1));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "address.myArrayObject.myInteger[0]");
            node.getAccessor().set(ognlContext, root, new Integer(3));
            assertEquals(p.getAddress().getMyArrayObject().getMyInteger()[0], 3);
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "address.myArrayObject.myIntegerObject[1]");
            node.getAccessor().set(ognlContext, root, new Integer(4));
            assertEquals(p.getAddress().getMyArrayObject().getMyIntegerObject()[1], new Integer(4));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "address.myArrayObject.myList[0]");
            node.getAccessor().set(ognlContext, root, "happy birthday");
            assertEquals(p.getAddress().getMyArrayObject().getMyList().get(0), "happy birthday");
        }

       {
            Node node = Ognl.compileExpression(ognlContext, root, "address.myArrayObject.myMap['Key1']");
            node.getAccessor().set(ognlContext, root, "happy valentine");
            assertEquals(p.getAddress().getMyArrayObject().getMyMap().get("Key1"), "happy valentine");
        }
    }


    public void testGetterMapInCompoundRoot() throws Exception {

        CompoundRootAccessor accessor = new CompoundRootAccessor();

        MyPrimitivesObject po = new MyPrimitivesObject();
        po.setMyLong(20l);

        MyPrimitiveArrayObject pao = new MyPrimitiveArrayObject();
         pao.setMyInteger(new int[] { 1, 2 });
         pao.setMyIntegerObject(new Integer[] { new Integer(1), new Integer(2) });

        Address a = new Address();
        a.setMyArrayObject(pao);

        Person p = new Person();
        p.setName("phil");
        p.setMyPrimitiveObject(po);
        p.setAddress(a);

        Person p2 = new Person();
        p2.setName("George");

        Map map = new LinkedHashMap();
        map.put("key1", "tmjee");
        map.put("key2", new Integer(10));
        map.put("key3", p);

        CompoundRoot root=  new CompoundRoot();
        root.add(map);
        root.add(p2);

        OgnlContext ognlContext = (OgnlContext) Ognl.createDefaultContext(root);

        {
            Node node = Ognl.compileExpression(ognlContext, root, "key1");
            assertEquals(node.getAccessor().get(ognlContext, root), "tmjee");
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "name");
            assertEquals(node.getAccessor().get(ognlContext, root), "George");
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "key2");
            assertEquals(node.getAccessor().get(ognlContext, root), new Integer(10));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "key3.myPrimitiveObject.myLong");
            assertEquals(node.getAccessor().get(ognlContext, root), new Long(20l));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "key3.name");
            assertEquals(node.getAccessor().get(ognlContext, root), "phil");
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "key3.address.myArrayObject.myInteger[0]");
            assertEquals(node.getAccessor().get(ognlContext, root), new Integer(1));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "key3.address.myArrayObject.myInteger[1]");
            assertEquals(node.getAccessor().get(ognlContext, root), new Integer(2));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "key3.address.myArrayObject.myIntegerObject[0]");
            assertEquals(node.getAccessor().get(ognlContext, root), new Integer(1));
        }

        {
            Node node = Ognl.compileExpression(ognlContext, root, "key3.address.myArrayObject.myIntegerObject[1]");
            assertEquals(node.getAccessor().get(ognlContext, root), new Integer(2));
        }
    }

     public void testSetterMapInCompoundRoot() throws Exception {

        CompoundRootAccessor accessor = new CompoundRootAccessor();

        MyPrimitivesObject po = new MyPrimitivesObject();

        MyPrimitiveArrayObject pao = new MyPrimitiveArrayObject();
         pao.setMyInteger(new int[2]);
         pao.setMyIntegerObject(new Integer[2]);

        Address a = new Address();
        a.setMyArrayObject(pao);         


        Person p = new Person();
        p.setMyPrimitiveObject(po);
        p.setAddress(a);

        Map map = new LinkedHashMap();

        CompoundRoot root=  new CompoundRoot();
        root.add(p);
        root.add(map);

        OgnlContext ognlContext = (OgnlContext) Ognl.createDefaultContext(root);

         {
             Node node = Ognl.compileExpression(ognlContext, root, "key1");
             node.getAccessor().set(ognlContext, root, "testing 123");
             assertEquals(map.get("key1"), "testing 123");
         }

         {
             Node node = Ognl.compileExpression(ognlContext, root, "name");
             node.getAccessor().set(ognlContext, root, "tmjee");
             assertEquals(p.getName(), "tmjee");
         }

         {
             Node node = Ognl.compileExpression(ognlContext, root, "myPrimitiveObject.myInteger");
             node.getAccessor().set(ognlContext, root, new Integer(22));
             assertEquals(p.getMyPrimitiveObject().getMyInteger(), 22);
         }

         {
             Node node = Ognl.compileExpression(ognlContext, root, "address.myArrayObject.myInteger[0]");
             node.getAccessor().set(ognlContext, root, new Integer(22));
             assertEquals(p.getAddress().getMyArrayObject().getMyInteger()[0], 22);
         }

         {
             Node node = Ognl.compileExpression(ognlContext, root, "address.myArrayObject.myIntegerObject[0]");
             node.getAccessor().set(ognlContext, root, new Integer(22));
             assertEquals(p.getAddress().getMyArrayObject().getMyIntegerObject()[0], new Integer(22));
         }

     }

}
