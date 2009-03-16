/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created on 6/10/2003
 *
 */
package com.opensymphony.xwork.util;

import junit.framework.TestCase;
import ognl.Ognl;

import java.util.*;

import com.opensymphony.xwork.mock.MockObjectTypeDeterminer;


/**
 * @author CameronBraid and Gabe
 */
public class SetPropertiesTest extends TestCase {

    public void testOgnlUtilEmptyStringAsLong() {
        Bar bar = new Bar();
        Map context = Ognl.createDefaultContext(bar);
        context.put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        bar.setId(null);

        HashMap props = new HashMap();
        props.put("id", "");

        OgnlUtil.setProperties(props, bar, context);
        assertNull(bar.getId());
        assertEquals(0, bar.getFieldErrors().size());

        props.put("id", new String[]{""});

        bar.setId(null);
        OgnlUtil.setProperties(props, bar, context);
        assertNull(bar.getId());
        assertEquals(0, bar.getFieldErrors().size());
    }

    public void testSetCollectionByConverterFromArray() {
        Foo foo = new Foo();
        OgnlValueStack vs = new OgnlValueStack();
        vs.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);

        XWorkConverter c = (XWorkConverter) Ognl.getTypeConverter(vs.getContext());
        c.registerConverter(Cat.class.getName(), new FooBarConverter());
        vs.push(foo);

        vs.setValue("cats", new String[]{"1", "2"});
        assertNotNull(foo.getCats());
        assertEquals(2, foo.getCats().size());
        assertEquals(Cat.class, foo.getCats().get(0).getClass());
        assertEquals(Cat.class, foo.getCats().get(1).getClass());
    }

    public void testSetCollectionByConverterFromCollection() {
        Foo foo = new Foo();
        OgnlValueStack vs = new OgnlValueStack();
        vs.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);

        XWorkConverter c = (XWorkConverter) Ognl.getTypeConverter(vs.getContext());
        c.registerConverter(Cat.class.getName(), new FooBarConverter());
        vs.push(foo);

        HashSet s = new HashSet();
        s.add("1");
        s.add("2");
        vs.setValue("cats", s);
        assertNotNull(foo.getCats());
        assertEquals(2, foo.getCats().size());
        assertEquals(Cat.class, foo.getCats().get(0).getClass());
        assertEquals(Cat.class, foo.getCats().get(1).getClass());
    }

    public void testValueStackSetValueEmptyStringAsLong() {
        Bar bar = new Bar();
        OgnlValueStack vs = new OgnlValueStack();
        vs.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        vs.push(bar);

        vs.setValue("id", "");
        assertNull(bar.getId());
        assertEquals(0, bar.getFieldErrors().size());

        bar.setId(null);

        vs.setValue("id", new String[]{""});
        assertNull(bar.getId());
        assertEquals(0, bar.getFieldErrors().size());
    }
    public void testAddingToListsWithObjects() {
        doTestAddingToListsWithObjects(true);
        doTestAddingToListsWithObjects(false);

    }
    public void doTestAddingToListsWithObjects(boolean allowAdditions) {

        MockObjectTypeDeterminer determiner
        =new MockObjectTypeDeterminer(null,Cat.class,null,allowAdditions);
        XWorkConverter.getInstance().setObjectTypeDeterminer(determiner);

        Foo foo = new Foo();
        foo.setMoreCats(new ArrayList());
        String spielname = "Spielen";
        OgnlValueStack vs = new OgnlValueStack();
        vs.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        vs.getContext().put(InstantiatingNullHandler.CREATE_NULL_OBJECTS, Boolean.TRUE);
        vs.push(foo);
        try {
        vs.setValue("moreCats[2].name", spielname);
        } catch (IndexOutOfBoundsException e) {
            if (allowAdditions) {
                throw e;
            }
        }
        Object setCat = null;
        if (allowAdditions) {
             setCat = foo.getMoreCats().get(2);


            assertNotNull(setCat);
            assertTrue(setCat instanceof Cat);
            assertTrue(((Cat) setCat).getName().equals(spielname));
        }	else {
            assertTrue(foo.getMoreCats()==null || foo.getMoreCats().size()==0);
        }

        //now try to set a lower number
        //to test setting after a higher one
        //has been created
        if (allowAdditions) {
            spielname = "paws";
            vs.setValue("moreCats[0].name", spielname);
            setCat = foo.getMoreCats().get(0);
            assertNotNull(setCat);
            assertTrue(setCat instanceof Cat);
            assertTrue(((Cat) setCat).getName().equals(spielname));
        }

    }

    public void testAddingToMapsWithObjects() {
        doTestAddingToMapsWithObjects(true);
        doTestAddingToMapsWithObjects(false);

    }

    public void doTestAddingToMapsWithObjects(boolean allowAdditions) {

        MockObjectTypeDeterminer determiner
        =new MockObjectTypeDeterminer(Long.class,Cat.class,null,allowAdditions);
        XWorkConverter.getInstance().setObjectTypeDeterminer(determiner);

        Foo foo = new Foo();
        foo.setAnotherCatMap(new HashMap());
        String spielname = "Spielen";
        OgnlValueStack vs = new OgnlValueStack();
        vs.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        vs.getContext().put(InstantiatingNullHandler.CREATE_NULL_OBJECTS, Boolean.TRUE);
        vs.push(foo);
        vs.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
        vs.setValue("anotherCatMap[\"3\"].name", spielname);
        Object setCat = foo.getAnotherCatMap().get(new Long(3));
        if (allowAdditions) {
            assertNotNull(setCat);
            assertTrue(setCat instanceof Cat);
            assertTrue(((Cat) setCat).getName().equals(spielname));
        }	else {
            assertNull(setCat);
        }


    }


    public void testAddingAndModifyingCollectionWithObjects() {
        doTestAddingAndModifyingCollectionWithObjects(new HashSet());
        doTestAddingAndModifyingCollectionWithObjects(new ArrayList());

    }
    public void doTestAddingAndModifyingCollectionWithObjects(Collection barColl) {

        XWorkConverter.getInstance().setObjectTypeDeterminer(new DefaultObjectTypeDeterminer());
        OgnlValueStack vs = new OgnlValueStack();
        Foo foo = new Foo();

        foo.setBarCollection(barColl);
        Bar bar1 = new Bar();
        bar1.setId(new Long(11));
        barColl.add(bar1);
        Bar bar2 = new Bar();
        bar2.setId(new Long(22));
        barColl.add(bar2);
        //try modifying bar1 and bar2
        //check the logs here to make sure
        //the Map is being created
        OgnlContextState.setCreatingNullObjects(vs.getContext(), true);
        OgnlContextState.setReportingConversionErrors(vs.getContext(), true);
        vs.push(foo);
        String bar1Title = "The Phantom Menace";
        String bar2Title = "The Clone Wars";
        vs.setValue("barCollection(22).title", bar2Title);
        vs.setValue("barCollection(11).title", bar1Title);
        Iterator barSetIter = barColl.iterator();
        while (barSetIter.hasNext()) {
            Bar next = (Bar) barSetIter.next();
            if (next.getId().intValue() == 22) {
                assertEquals(bar2Title, next.getTitle());
            } else {
                assertEquals(bar1Title, next.getTitle());
            }
        }
        //now test adding to a collection
        String bar3Title = "Revenge of the Sith";
        String bar4Title = "A New Hope";
        vs.setValue("barCollection.makeNew[4].title", bar4Title, true);
        vs.setValue("barCollection.makeNew[0].title", bar3Title, true);

        assertEquals(4, barColl.size());
        barSetIter = barColl.iterator();

        while (barSetIter.hasNext()) {
            Bar next = (Bar) barSetIter.next();
            if (next.getId() == null) {
                assertNotNull(next.getTitle());
                assertTrue(next.getTitle().equals(bar4Title)
                        || next.getTitle().equals(bar3Title));
            }
        }

    }

    public void testAddingToCollectionBasedOnPermission() {

        MockObjectTypeDeterminer determiner
        =new MockObjectTypeDeterminer(Long.class,Bar.class,"id",true);
        XWorkConverter.getInstance().setObjectTypeDeterminer(determiner);

        Collection barColl=new HashSet();

        OgnlValueStack vs = new OgnlValueStack();
        OgnlContextState.setCreatingNullObjects(vs.getContext(), true);
        OgnlContextState.setReportingConversionErrors(vs.getContext(), true);
        Foo foo = new Foo();

        foo.setBarCollection(barColl);

        vs.push(foo);

        String bar1Title="title";
        vs.setValue("barCollection(11).title", bar1Title);

        assertEquals(1, barColl.size());
        Object bar=barColl.iterator().next();
        assertTrue(bar instanceof Bar);
        assertEquals(((Bar)bar).getTitle(), bar1Title);
        assertEquals(((Bar)bar).getId(), new Long(11));

        //now test where there is no permission
        determiner.setShouldCreateIfNew(false);

        String bar2Title="another title";
        vs.setValue("barCollection(22).title", bar1Title);

        assertEquals(1, barColl.size());
        bar=barColl.iterator().next();
        assertTrue(bar instanceof Bar);
        assertEquals(((Bar)bar).getTitle(), bar1Title);
        assertEquals(((Bar)bar).getId(), new Long(11));


    }


}
