/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.validators;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.PreResultListener;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.validator.ValidationException;

/**
 * <!-- START SNIPPET: javadoc -->
 * 
 * An abstract base class that adds in the capability to populate the stack with
 * a fake parameter map when a conversion error has occurred and the 'repopulateField'
 * property is set to "true".
 * 
 * <p/>
 * 
 * This is typically usefull when one wants to repopulate the field with the original value 
 * when a conversion error occurred. Eg. with a textfield that only allows an Integer 
 * (the action class have an Integer field declared), upon conversion error, the incorrectly
 * entered integer (maybe a text 'one') will not appear when dispatched back. With 'repopulateField'
 * porperty set to true, it will, meaning the textfield will have 'one' as its value 
 * upon conversion error.
 * 
 * <!-- END SNIPPET: javadoc -->
 * 
 * <p/>
 * 
 * <pre>
 * <!-- START SNIPPET: example -->
 * 
 * <!-- myJspPage.jsp -->
 * <ww:form action="someAction" method="POST">
 *   ....
 *   <ww:textfield 
 *       label="My Integer Field"
 *       name="myIntegerField" />
 *   ....
 *   <ww:submit />       
 * </ww:form>
 * 
 * 
 * <!-- xwork.xml -->
 * <xwork>
 * <include file="webwork-default.xml" />
 * ....
 * <package name="myPackage" extends="webwork-default">
 *   ....
 *   <action name="someAction" class="example.MyActionSupport.java">
 *      <result name="input">myJspPage.jsp</result>
 *      <result>success.jsp</result>
 *   </action>
 *   ....
 * </package>
 * ....
 * </xwork>
 * 
 * <!-- MyActionSupport.java -->
 * public class MyActionSupport extends ActionSupport {
 *    private Integer myIntegerField;
 *    
 *    public Integer getMyIntegerField() { return this.myIntegerField; }
 *    public void setMyIntegerField(Integer myIntegerField) { 
 *       this.myIntegerField = myIntegerField; 
 *    }
 * }
 * 
 * 
 * <!-- MyActionSupport-someAction-validation.xml -->
 * <validators>
 *   ...
 *   <field name="myIntegerField">
 *      <field-validator type="conversion">
 *         <param name="repopulateField">true</param>
 *         <message>Conversion Error (Integer Wanted)</message>
 *      </field-validator>
 *   </field>
 *   ...
 * </validators>
 * 
 * <!-- END EXAMPLE: example -->
 * </pre>
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public abstract class RepopulateConversionErrorFieldValidatorSupport extends FieldValidatorSupport {
	
	private static final Log _log = LogFactory.getLog(RepopulateConversionErrorFieldValidatorSupport.class);
	
	private String repopulateFieldAsString = "false";
	private boolean repopulateFieldAsBoolean = false;
	
	public String getRepopulateField() { 
		return repopulateFieldAsString;
	}
	
	public void setRepopulateField(String repopulateField) {
		this.repopulateFieldAsString = repopulateField == null ? repopulateField : repopulateField.trim();
		this.repopulateFieldAsBoolean = "true".equalsIgnoreCase(this.repopulateFieldAsString) ? (true) : (false);
	}

	public void validate(Object object) throws ValidationException {
		doValidate(object);
		if (repopulateFieldAsBoolean) {
			repopulateField(object);
		}
	}
	
	public void repopulateField(Object object) throws ValidationException {
		
		ActionInvocation invocation = ActionContext.getContext().getActionInvocation();
		Map conversionErrors = ActionContext.getContext().getConversionErrors();
		
		String fieldName = getFieldName();
		String fullFieldName = getValidatorContext().getFullFieldName(fieldName);
		Object value = conversionErrors.get(fullFieldName);
		
		final Map fakeParams = new LinkedHashMap();
		boolean doExprOverride = false;
		
		if (value instanceof String[]) {
			// take the first element, if possible
			String[] tmpValue = (String[]) value;
			if (tmpValue != null && (tmpValue.length > 0) ) {
				doExprOverride = true;
				fakeParams.put(fieldName, "'"+tmpValue[0]+"'");
			}
			else {
				_log.warn("value is an empty array of String or with first element in it as null ["+value+"], will not repopulate conversion error ");
			}
		}
		else if (value instanceof String) {
			String tmpValue = (String) value;
			doExprOverride = true;
			fakeParams.put(fieldName, "'"+tmpValue+"'");
		}
		else {
			// opps... it should be 
			_log.warn("conversion error value is not a String or array of String but instead is ["+value+"], will not repopulate conversion error");
		}
		
		if (doExprOverride) {
			invocation.addPreResultListener(new PreResultListener() {
				public void beforeResult(ActionInvocation invocation, String resultCode) {
					OgnlValueStack stack = ActionContext.getContext().getValueStack();
					stack.setExprOverrides(fakeParams);
				}
			});
		}
	}
	
	protected abstract void doValidate(Object object) throws ValidationException;
}
