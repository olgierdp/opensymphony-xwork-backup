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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;


/**
 * @author CameronBraid
 */
public class SetPropertiesTest extends TestCase {
    //~ Methods ////////////////////////////////////////////////////////////////

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

        props.put("id", new String[] {""});

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
    public void fix_testAddingToListsWithObjects() {
		Foo foo=new Foo();
		foo.setMoreCats(new ArrayList());
		String spielname="Spielen";    	
		OgnlValueStack vs = new OgnlValueStack();
		vs.getContext().put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);
		vs.getContext().put(InstantiatingNullHandler.CREATE_NULL_OBJECTS, Boolean.TRUE);
		vs.push(foo);
		vs.setValue("moreCats[2].name", spielname);
		Object setCat=foo.getMoreCats().get(2);
		assertNotNull(setCat);
		assertTrue(setCat instanceof Cat);
		assertTrue(((Cat)setCat).getName().equals(spielname));
		//now try to set a lower number
		//to test setting after a higher one
		//has been created
		spielname="paws";
		vs.setValue("moreCats[0].name", spielname);
		setCat=foo.getMoreCats().get(0);
		assertNotNull(setCat);
		assertTrue(setCat instanceof Cat);
		assertTrue(((Cat)setCat).getName().equals(spielname));
		
    
    }

    public void fix_testAddingToMapsWithObjects() {
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
        assertNotNull(setCat);
        assertTrue(setCat instanceof Cat);
        assertTrue(((Cat) setCat).getName().equals(spielname));


    }

    public void fix_testAddingAndModifyingSetsWithObjects() {
        OgnlValueStack vs = new OgnlValueStack();
        Foo foo = new Foo();
        HashSet barSet = new HashSet();
        foo.setBarSet(barSet);
        Bar bar1 = new Bar();
        bar1.setId(new Long(11));
        barSet.add(bar1);
        Bar bar2 = new Bar();
        bar2.setId(new Long(22));
        barSet.add(bar2);
        //try modifying bar1 and bar2
        //check the logs here to make sure
        //the Map is being created
        OgnlContextState.setCreatingNullObjects(vs.getContext(), true);
        OgnlContextState.setReportingConversionErrors(vs.getContext(), true);
        vs.push(foo);
        String bar1Title = "The Phantom Menace";
        String bar2Title = "The Clone Wars";
        vs.setValue("barSet['22'].title", bar2Title);
        vs.setValue("barSet['11'].title", bar1Title);
        Iterator barSetIter = barSet.iterator();
        while (barSetIter.hasNext()) {
            Bar next = (Bar) barSetIter.next();
            if (next.getId().intValue() == 22) {
                assertEquals(bar2Title, next.getTitle());
            } else {
                assertEquals(bar1Title, next.getTitle());
            }
        }
        //now test adding
        String bar3Title = "Revenge of the Sith";
        String bar4Title = "A New Hope";
        vs.setValue("barSet.makeNew[4].title", bar4Title, true);
        vs.setValue("barSet.makeNew[0].title", bar3Title, true);
        assertEquals(4, barSet.size());
        barSetIter = barSet.iterator();

        while (barSetIter.hasNext()) {
            Bar next = (Bar) barSetIter.next();
            if (next.getId() == null) {
                assertNotNull(next.getTitle());
                assertTrue(next.getTitle().equals(bar4Title)
                        || next.getTitle().equals(bar3Title));
            }
        }

    }
    
    
}
