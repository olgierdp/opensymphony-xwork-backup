/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.validators;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.xwork.XWorkTestCase;
import com.opensymphony.xwork.validator.DelegatingValidatorContext;
import com.opensymphony.xwork.validator.validators.CollectionFieldValidator.PropertySpliter;

/**
 * @author tmjee
 * @version $Date$ $Id$
 */
public class CollectionFieldValidatorTest extends XWorkTestCase {

	public void testValidator1() throws Exception {
		TestAction action = new TestAction();
		
		CollectionFieldValidator validator = new CollectionFieldValidator();
		validator.setValidatorContext(new DelegatingValidatorContext(action));
		validator.setFieldName("collection");
		validator.setProperty("collection.element.element2s.element3s.element4s.name");
		validator.setValidatorRef("requiredstring");
		validator.setValidatorParams(new LinkedHashMap() {
			private static final long serialVersionUID = 0L;
			{put("trim", "true");
			  put("defaultMessage", "error name required");}
		});
		validator.validate(action);
		assertEquals(action.getFieldErrors().size(), 0);
	}
	
	public void testValidator2() throws Exception {
		TestAction action = new TestAction();
		
		CollectionFieldValidator validator = new CollectionFieldValidator();
		validator.setValidatorContext(new DelegatingValidatorContext(action));
		validator.setFieldName("collection");
		validator.setProperty("collection.element.element2s.element3s.element4s.address");
		validator.setValidatorRef("requiredstring");
		validator.setValidatorParams(new LinkedHashMap() {
			private static final long serialVersionUID = 0L;
			{put("trim", "true");
			  put("defaultMessage", "error address required");}
		});
		validator.validate(action);
		assertEquals(action.getFieldErrors().size(), 18);
		
		
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[0].element3s[0].element4s[0].address")).get(0), "error address required");
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[0].element3s[0].element4s[0].address")).size(), 1);
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[0].element3s[0].element4s[1].address")).get(0), "error address required");
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[0].element3s[0].element4s[1].address")).size(), 1);
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[0].element3s[0].element4s[2].address")).get(0), "error address required");
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[0].element3s[0].element4s[2].address")).size(), 1);
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[0].element3s[1].element4s[0].address")).get(0), "error address required");
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[0].element3s[1].element4s[0].address")).size(), 1);
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[0].element3s[1].element4s[1].address")).get(0), "error address required");
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[0].element3s[1].element4s[1].address")).size(), 1);
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[0].element3s[1].element4s[2].address")).get(0), "error address required");
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[0].element3s[1].element4s[2].address")).size(), 1);
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[0].element3s[2].element4s[0].address")).get(0), "error address required");
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[0].element3s[2].element4s[0].address")).size(), 1);
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[0].element3s[2].element4s[1].address")).get(0), "error address required");
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[0].element3s[2].element4s[1].address")).size(), 1);
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[0].element3s[2].element4s[2].address")).get(0), "error address required");
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[0].element3s[2].element4s[2].address")).size(), 1);
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[1].element3s[0].element4s[0].address")).get(0), "error address required");
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[1].element3s[0].element4s[0].address")).size(), 1);
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[1].element3s[0].element4s[1].address")).get(0), "error address required");
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[1].element3s[0].element4s[1].address")).size(), 1);
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[1].element3s[0].element4s[2].address")).get(0), "error address required");
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[1].element3s[0].element4s[2].address")).size(), 1);
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[1].element3s[1].element4s[0].address")).get(0), "error address required");
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[1].element3s[1].element4s[0].address")).size(), 1);
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[1].element3s[1].element4s[1].address")).get(0), "error address required");
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[1].element3s[1].element4s[1].address")).size(), 1);
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[1].element3s[1].element4s[2].address")).get(0), "error address required");
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[1].element3s[1].element4s[2].address")).size(), 1);
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[1].element3s[2].element4s[0].address")).get(0), "error address required");
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[1].element3s[2].element4s[0].address")).size(), 1);
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[1].element3s[2].element4s[1].address")).get(0), "error address required");
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[1].element3s[2].element4s[1].address")).size(), 1);
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[1].element3s[2].element4s[2].address")).get(0), "error address required");
		assertEquals(((List)action.getFieldErrors().get("collection[0].element.element2s[1].element3s[2].element4s[2].address")).size(), 1);
	}
	
	
	public void testPropertySpliter() throws Exception {
		CollectionFieldValidator validator = new CollectionFieldValidator();
		PropertySpliter propertySpliter = validator.new PropertySpliter("aaa.bbb.ccc.ddd.eee");
		CollectionFieldValidator.CloneableIterator i = propertySpliter.iterator();

		String element = null;
		
		assertTrue(i.hasNext());
		element = (String) i.next();
		assertNotNull(element);
		assertEquals("aaa", element);
		
		assertTrue(i.hasNext());
		element = (String) i.next();
		assertNotNull(element);
		assertEquals("bbb", element);
		
		assertTrue(i.hasNext());
		element = (String) i.next();
		assertNotNull(element);
		assertEquals("ccc", element);
		
		assertTrue(i.hasNext());
		element = (String) i.next();
		assertNotNull(element);
		assertEquals("ddd", element);
		
		assertTrue(i.hasNext());
		element = (String) i.next();
		assertNotNull(element);
		assertEquals("eee", element);
		
		assertFalse(i.hasNext());
	}
	
	public void testCloningPropertySpliter1() throws Exception {
		CollectionFieldValidator validator = new CollectionFieldValidator();
		PropertySpliter propertySpliter = validator.new PropertySpliter("aaa.bbb.ccc.ddd.eee");
		CollectionFieldValidator.CloneableIterator i = propertySpliter.iterator();
		i = (CollectionFieldValidator.CloneableIterator) i.clone();

		String element = null;
		
		assertTrue(i.hasNext());
		element = (String) i.next();
		assertNotNull(element);
		assertEquals("aaa", element);
		
		assertTrue(i.hasNext());
		element = (String) i.next();
		assertNotNull(element);
		assertEquals("bbb", element);
		
		{
			CollectionFieldValidator.CloneableIterator ii = (CollectionFieldValidator.CloneableIterator) i.clone();
			
			assertTrue(ii.hasNext());
			element = (String) ii.next();
			assertNotNull(element);
			assertEquals("ccc", element);
			
			{
				CollectionFieldValidator.CloneableIterator iii = (CollectionFieldValidator.CloneableIterator) ii.clone();
				
				assertTrue(iii.hasNext());
				element = (String) iii.next();
				assertNotNull(element);
				assertEquals("ddd", element);
				
				assertTrue(iii.hasNext());
				element = (String) iii.next();
				assertNotNull(element);
				assertEquals("eee", element);
			}
			
			assertTrue(ii.hasNext());
			element = (String) ii.next();
			assertNotNull(element);
			assertEquals("ddd", element);
			
			assertTrue(ii.hasNext());
			element = (String) ii.next();
			assertNotNull(element);
			assertEquals("eee", element);
		}
		
		
		assertTrue(i.hasNext());
		element = (String) i.next();
		assertNotNull(element);
		assertEquals("ccc", element);
		
		assertTrue(i.hasNext());
		element = (String) i.next();
		assertNotNull(element);
		assertEquals("ddd", element);
		
		assertTrue(i.hasNext());
		element = (String) i.next();
		assertNotNull(element);
		assertEquals("eee", element);
		
		assertFalse(i.hasNext());
	}
	
	
	public void testCloningPropertySpliter2() throws Exception {
		CollectionFieldValidator validator = new CollectionFieldValidator();
		CollectionFieldValidator.PropertySpliter spliter = validator.new PropertySpliter("element.element2s.element3s.element4s.name");
		CollectionFieldValidator.CloneableIterator i = (CollectionFieldValidator.CloneableIterator) spliter.iterator();
		
		assertEquals("element", i.next());
			CollectionFieldValidator.CloneableIterator ii1 = (CollectionFieldValidator.CloneableIterator) i.clone();
			assertEquals("element2s", ii1.next());
				CollectionFieldValidator.CloneableIterator iii1 = (CollectionFieldValidator.CloneableIterator) ii1.clone();
				assertEquals("element3s", iii1.next());
					CollectionFieldValidator.CloneableIterator iiii1 = (CollectionFieldValidator.CloneableIterator) iii1.clone();
					assertEquals("element4s", iiii1.next());
					assertEquals("name", iiii1.next());
				assertEquals("element4s", iii1.next());
				assertEquals("name", iii1.next());
			assertEquals("element3s", ii1.next());
			assertEquals("element4s", ii1.next());
			assertEquals("name", ii1.next());
		assertEquals("element2s", i.next());
		assertEquals("element3s", i.next());
			CollectionFieldValidator.CloneableIterator ii2 = (CollectionFieldValidator.CloneableIterator) i.clone();
			assertEquals("element4s", ii2.next());
			assertEquals("name", ii2.next());
		assertEquals("element4s", i.next());
		assertEquals("name", i.next());
	}
	
	
	public void testPopulateValue() throws Exception {
		Element4.count = 0;
		
		List result = new ArrayList();
		TestObject testObject = new TestObject();
		CollectionFieldValidator validator = new CollectionFieldValidator();
		CollectionFieldValidator.PropertySpliter spliter = validator.new PropertySpliter("element.element2s.element3s.element4s.name");
		String e = "";
		validator.populateValue(testObject, spliter.iterator(), result, e);
		
		/*
		 *  TestObject
		 *      + Element
		 *             + Element2
		 *                   + Element3
		 *                   		+ Element4
		 *                                + name (tmjee1)
		 *                         + Element4
		 *                                + name (phil1)
		 *                         + Element4
		 *                                + name (pat1)
		 *                   + Element3
		 *                         + Element4
		 *                                + name (tmjee2)
		 *                         + Element4
		 *                                + name (phil2)
		 *                         + Element4
		 *                                + name (pat2)
		 *                   + Element3
		 *                         + Element4
		 *                                + name (tmjee3)
		 *                         + Element4
		 *                                + name (phil3)
		 *                         + Element4
		 *                                + name (pat3)
		 *             + Element2
		 *                   + Element3
		 *                         + Element4 
		 *                               + name (tmjee4)
		 *                         + Element4
		 *                               + name (phil4)
		 *                         + Element4
		 *                               + name (pat4)
		 *                   + Element3
		 *                         + Element4
		 *                               + name (tmjee5)
		 *                         + Element4
		 *                               + name (phil5)
		 *                         + Element4
		 *                               + name (pat5)
		 *                   + Element3
		 *                         + Element4
		 *                               + name (tmjee6)
		 *                         + Element4
		 *                               + name (phil6)
		 *                         + Element4
		 *                               + name (pat6)
		 */
		assertEquals(result.size(), 18);
		assertEquals("element.element2s[0].element3s[0].element4s[0].name", result.get(0).toString());
		assertEquals("element.element2s[0].element3s[0].element4s[1].name", result.get(1).toString());
		assertEquals("element.element2s[0].element3s[0].element4s[2].name", result.get(2).toString());
		assertEquals("element.element2s[0].element3s[1].element4s[0].name", result.get(3).toString());
		assertEquals("element.element2s[0].element3s[1].element4s[1].name", result.get(4).toString());
		assertEquals("element.element2s[0].element3s[1].element4s[2].name", result.get(5).toString());
		assertEquals("element.element2s[0].element3s[2].element4s[0].name", result.get(6).toString());
		assertEquals("element.element2s[0].element3s[2].element4s[1].name", result.get(7).toString());
		assertEquals("element.element2s[0].element3s[2].element4s[2].name", result.get(8).toString());
		assertEquals("element.element2s[1].element3s[0].element4s[0].name", result.get(9).toString());
		assertEquals("element.element2s[1].element3s[0].element4s[1].name", result.get(10).toString());
		assertEquals("element.element2s[1].element3s[0].element4s[2].name", result.get(11).toString());
		assertEquals("element.element2s[1].element3s[1].element4s[0].name", result.get(12).toString());
		assertEquals("element.element2s[1].element3s[1].element4s[1].name", result.get(13).toString());
		assertEquals("element.element2s[1].element3s[1].element4s[2].name", result.get(14).toString());
		assertEquals("element.element2s[1].element3s[2].element4s[0].name", result.get(15).toString());
		assertEquals("element.element2s[1].element3s[2].element4s[1].name", result.get(16).toString());
		assertEquals("element.element2s[1].element3s[2].element4s[2].name", result.get(17).toString());
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static class TestAction extends ActionSupport {
		private static final long serialVersionUID = 7815309144526028220L;
		public List getCollection() {
			List collection = new ArrayList();
			collection.add(new TestObject());
			return collection;
		}
	}
	
	
	public static class TestObject {
		private Element element;
		
		public TestObject() {
			element = new Element();
		}
		
		public Element getElement() { return this.element; }
		public void setElement(Element element) { this.element = element; }
	}
	
	public static class Element {
		private List elements; 
		
		public Element() {
			elements = new ArrayList();
			elements.add(new Element2());
			elements.add(new Element2());
		}
		
		public List getElement2s() { return this.elements; }
		public void setElement2s(List elements) { this.elements = elements; }
	}
	
	
	public static class Element2 {
		private List elements;
		
		public Element2() {
			elements = new ArrayList();
			elements.add(new Element3());
			elements.add(new Element3());
			elements.add(new Element3());
		}
		
		public List getElement3s() { return this.elements; }
		public void setElement3s(List elements) { this.elements = elements; }
	}
	
	public static class Element3 {
		private List elements; 
		
		public Element3() {
			elements = new ArrayList();
			elements.add(new Element4("tmjee"));
			elements.add(new Element4("phil"));
			elements.add(new Element4("pat"));
		}
		
		public List getElement4s() { return this.elements; }
		public void setElement4s(List elements) { this.elements = elements; }
	}
	
	public static class Element4 {
		private static int count = 0;
		private String name;
		private String address;
		
		public Element4(String name) {
			this.name = name+count;
			count = count +1;
		}
		
		public String getName() { return this.name; }
		public void setName(String name) { this.name = name; }
		
		public String getAddress() { return this.address; }
		public void setAddress(String address) { this.address = address; }
	}
}
