/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.validators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import ognl.OgnlException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork.XworkException;
import com.opensymphony.xwork.util.OgnlUtil;
import com.opensymphony.xwork.validator.ActionValidatorManagerFactory;
import com.opensymphony.xwork.validator.FieldValidator;
import com.opensymphony.xwork.validator.ShortCircuitableValidator;
import com.opensymphony.xwork.validator.ValidationException;
import com.opensymphony.xwork.validator.Validator;
import com.opensymphony.xwork.validator.ValidatorConfig;
import com.opensymphony.xwork.validator.ValidatorFactory;

/**
 * <!-- START SNIPPET: javadoc -->
 * 
 * Validate a property available in the object of a collection. This validator will not work
 * when the 'nested' validator is an 'fieldexpression' or 'expression' validator.. If we want to use
 * 'fieldexpression' or 'expression' validator we might want to use eg. the following 
 * expression for validation collections (using OGNL's projection and filtering capabilities.
 * <p/>
 * For example if we have a collection of <code>person</code>s objects with 'name'
 * properties we might want to consider the following OGNL expression.
 * <pre>
 * persons.{#this.name.length() > 4}.{? #this == false }.size() <= 0 
 * </pre>
 * 
 * The idea is 
 * <ul>
 * 	<li>- project across the collection of <code>person</code> objects 
 *              and make sure the conditions are valid (eg. name is more than 4 characters</li>
 *      <li>- filter out the result of previous projection that are not valid (eg. false)</li>
 *      <li>- check the size of the filtered collection, if its greater than zero, 
 *               we have some validation that fails</li>
 * </ul>
 * 
 * <!-- END SNIPPET: javadoc -->
 * 
 * 
 * <!-- START SNIPPET: parameters -->
 * 
 * <ul>
 * 	<li>property - the full property name that this validator should validate. 
 * 							eg. if "persons" is a collection of Persons object with a property
 *                             called name the property would be "persons.name" </li>
 * 	<li>validatorRef - validator name of an existing validator (could not be "collection" validator)
 * 							     eg. required, requiredstring, int etc.</li>
 * 	<li>validatorParams - the parameters to be passed into the validator referenced
 * 										by the "validatorParams" attribute above.</li>
 * </ul>
 * 
 * <!-- END SNIPPET: parameters -->
 * 
 * <pre>
 * <!-- START SNIPPET: examples -->
 * 
 * public class MyAction extends ActionSupport {
 * 	private List persons = new ArrayList();
 *     ....
 *     public List getPersons() { return this.persons; }
 *     public void setPersons(List persons) { this.persons = persons; } 
 * }
 * 
 * 
 * public class Person {
 *    private String name;
 *    private Integer age;
 *    
 *    public String getName() { return name; }
 *    public void setName(String name) { this.name = name; }
 *    
 *    public Integer getAge() { return age; }
 *    public void setAge(Integer age) { this.age = age; }
 * }
 * 
 * 
 * &lt;validators&gt;
 *    &lt;field name="persons"&gt;
 *        &lt;field-validator type="collection"&gt;
 *        		&lt;param name="property"&gt;persons.name&lt;/param&gt;
 *        		&lt;param name="validatorRef"&gt;requiredstring&lt;/param&gt;
 *             &lt;param name="validatorParams['defaultMessage']"&gt;Must be String&lt;/param&gt;
 *             &lt;param name="validatorParams['trim']"&gt;true&lt;/param&gt;
 *             &lt;message&gt; ... &lt;/message&gt;
 *        &lt;/field-validator&gt;
 *        &lt;field-validator type="collection"&gt;
 *            &lt;param name="property"&gt;persons.age&lt;/param&gt;
 *            &lt;param name="validatorRef"&gt;required&lt;/param&gt;
 *            &lt;param name="validatorParams['defaultMessage']"&gt;Must be filled in&lt;/param&gt;
 *            &lt;message&gt; ... &lt;/message&gt;
 *        &lt;/field-validator&gt;
 *        &lt;field-validator type="collection"&gt;
 *        		&lt;param name="property"&gt;persons.age&lt;/param&gt;
 *             &lt;param name="validatorRef"&gt;int&lt;/param&gt;
 *             &lt;param name="validatorParams['defaultMessage']"&gt;Needs to be an integer&lt;/param&gt;
 *             &lt;message&gt; ... &lt;/message&gt;
 *        &lt;/field-validator&gt;
 *    &lt;/field&gt;
 * &lt;/validators&gt;
 * 
 * <!-- END SNIPPET: examples -->
 * </pre>
 * 
 * @author tmjee
 * @version $Date$ $Id$
 */
public class CollectionFieldValidator extends FieldValidatorSupport {
	
	private static final Log LOG = LogFactory.getLog(CollectionFieldValidator.class);

	private String property;
	public String getProperty() { return this.property; }
	public void setProperty(String collection) { this.property = collection; }
	
	private String validatorRef;
	public String getValidatorRef() { return this.validatorRef; }
	public void setValidatorRef(String validatorRef) { this.validatorRef = validatorRef; }
	
	private Map validatorParams = new LinkedHashMap();
	public void setValidatorParams(Map validatorParams) { this.validatorParams = validatorParams; }
	public Map getValidatorParams() { return validatorParams; }
	
	
	/**
	 * Validate the <code>object</code>.
	 * 
	 * @see {@link Validator#validate(Object)}
	 */
	public void validate(Object object) throws ValidationException {
		if (property == null || property.trim().length() <= 0) {
			throw new XworkException("collection property cannot be null or empty, it is needed to specify a property that doesn't return back a Collection");
		}
		
		// will throw IllegalArgumentException if validatorRef is bad (not registered etc)
		ValidatorFactory.lookupRegisteredValidatorType(validatorRef);
		Object obj = getFieldValue(getFieldName(), object);
		if (obj != null) {
			List result = new ArrayList();
			PropertySpliter spliter = new PropertySpliter(property);
			try {
				String overallProperty = "";
				populateValue(object, spliter.iterator(), result, overallProperty);
			}
			catch(Exception e) {
				throw new XworkException(e.toString(), e);
			}
			
			// validate 
			Validator validator = ValidatorFactory.getValidator(new ValidatorConfig(validatorRef, validatorParams));
			validator.setValidatorContext(getValidatorContext());
			if (validatorParams.containsKey("defaultMessage")) {
				validator.setDefaultMessage((String) validatorParams.get("defaultMessage"));
			}
			if (validatorParams.containsKey("messageKey")) {
				validator.setDefaultMessage((String) validatorParams.get("messageKey"));
			}
			if (validatorParams.containsKey("shortCircuit") && (validator instanceof ShortCircuitableValidator))  {
				((ShortCircuitableValidator)validator).setShortCircuit(validatorParams.get("shortCircuit").equals("true"));
			}
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("validatorRef ["+validatorRef+"] found to be referencing to validator ["+validator+"]");
				LOG.debug("injecting parameters ["+validatorParams+"] into validator ["+validator+"]");
			}
				
			for(Iterator i = result.iterator(); i.hasNext(); ) {
				List validators = new ArrayList();
				String overallPropertyName = (String) i.next();
				if (validator instanceof FieldValidator)  {
					((FieldValidator)validator).setFieldName(overallPropertyName);
				}
				validators.add(validator);
				ActionValidatorManagerFactory.getInstance().validate(object, validators, getValidatorContext());
			}
		}
		else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("valued obtained from field name ["+getFieldName()+"] is null, skiping this validator");
			}
		}
	}
	
	
	/**
	 * Populate <code>result</code> with a list of property name, eg.
	 * if we have a list of Person object with a property called "name". If we have 
	 * 2 persons in the list the result would be :-
	 * 
	 *  <ul>
	 *  	<li>persons[0].name</li>
	 *     <li>persons[1].name</li>
	 *  </ul>
	 * 
	 * @param obj
	 * @param iterator
	 * @param result
	 * @param overallPropertyName
	 * @throws OgnlException
	 * @throws CloneNotSupportedException
	 */
	protected void populateValue(Object obj, CloneableIterator iterator, List result, String overallPropertyName) throws OgnlException, CloneNotSupportedException {
		if (iterator.hasNext()) {
			String expression = (String) iterator.next();
		
			if (overallPropertyName.trim().length() <= 0) {
				overallPropertyName = overallPropertyName + expression;
			}
			else {
				overallPropertyName = overallPropertyName+"."+expression;
			}
		
			Object val = OgnlUtil.getValue(expression, Collections.EMPTY_MAP, obj);
			if (val instanceof Collection) {
				if (! iterator.hasNext()) {
					throw new XworkException("collection property ["+property+"] ends with a collection, it should end with an object not collection");
				}
			
				int a=0;
				for (Iterator i = ((Collection)val).iterator(); i.hasNext(); ) {
					CloneableIterator ii = (CloneableIterator) iterator.clone();
					// strip out [*] eg. [1], [2] etc.
					if (overallPropertyName.endsWith("]")) {
						int index = overallPropertyName.lastIndexOf("[");
						overallPropertyName = overallPropertyName.substring(0, index);
					}
					overallPropertyName = overallPropertyName + "["+a+"]";
					populateValue(i.next(), ii, result, overallPropertyName);
					a = a + 1;
				}
			}
			else {
				if (! iterator.hasNext()) {
					result.add(overallPropertyName);
				}
				else {
					populateValue(val, iterator, result, overallPropertyName);
				}
			}
		}
	}
	
	/**
	 * Splits up the <code>properties</code> supplied, eg. if the <code>properties</code> 
	 * is "persons.addresses.name", the {@link #iterator()} method would return a
	 * {@link CloneableIterator} with the following entries
	 * 
	 * <ul>
	 *     <li>persons</li>
	 *     <li>addresses</li>
	 *     <li>name</li>
	 * </ul>
	 * 
	 * in order.
	 * 
	 * @author tmjee
	 * @version $Date$ $Id$
	 */
	protected class PropertySpliter {
		private String properties;
		public PropertySpliter(String properties) {
			this.properties = properties;
		}
		
		/**
		 * Return an iterator of the splited <code>properties</code>
		 * @return
		 */
		CloneableIterator iterator() {
			List propertyList = new ArrayList();
			int tmpPrevIndex = 0;
			int tmpIndex = properties.indexOf(".", 0);
			while (tmpIndex > 0) {
				String property = properties.substring(tmpPrevIndex, tmpIndex);
				propertyList.add(property);
				tmpPrevIndex = tmpIndex + 1;
				tmpIndex = properties.indexOf(".", tmpIndex + 1);
			}
			propertyList.add(properties.substring(tmpPrevIndex));
			return new CloneableIterator(propertyList); 
		}
	}
	
	/**
	 * A cloneable iterator, when the iterator is cloned, its state is preserved, 
	 * eg. when this iterator is iterated to its 2nd element and the iterator is 
	 * cloned, the cloned iterator will start at its 3rd element as well when its
	 * {{@link #next()} method is called.
	 * 
	 * @author tmjee
	 * @version $Date$ $Id$
	 */
	protected class CloneableIterator implements Iterator, Cloneable {
		private List delegate;
		private ListIterator delegateIterator;
		public int index = -1; // current element index in list
		
		/**
		 * Create a {@link CloneableIterator} based on the <code>list</code>
		 * supplied.
		 * 
		 * @param list
		 */
		public CloneableIterator(List list) {
			this.delegate = list;
			this.delegateIterator = list.listIterator();
		}
		
		/**
		 * An internal constructor used when cloning, its private so not to be 
		 * seen by outer class.
		 * 
		 * @param list
		 * @param index index of original iterator's current iterated element
		 */
		private CloneableIterator(List list, int index) {
			this.delegate = list;
			this.index = index;
			this.delegateIterator = list.listIterator(index+1);
		}
		
		/**
		 * See if there's a next element pending.
		 */
		public boolean hasNext() {
			return delegateIterator.hasNext();
		}

		/**
		 * Go get the next element.
		 */
		public Object next() {
			index = index + 1;
			return delegateIterator.next();
		}

		/**
		 * This operation is NOT SUPPORTED, will throw {@link UnsupportedOperationException}
		 * if its invoked.
		 */
		public void remove() {
			throw new UnsupportedOperationException("remove() is not supported");
		}
		
		/**
		 * Clone this iterator, when the iterator is cloned, its state is preserved, 
		 * eg. when this iterator is iterated to its 2nd element and the iterator is 
		 * cloned, the cloned iterator will start at its 3rd element as well when its
		 * {{@link #next()} method is called.
		 */
		protected Object clone() throws CloneNotSupportedException {
			return new CloneableIterator(delegate, index);
		}
	}
}
