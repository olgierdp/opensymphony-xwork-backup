/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.TestBean;
import com.opensymphony.xwork.XWorkTestCase;
import com.opensymphony.xwork.SimpleAnnotationAction;
import com.opensymphony.xwork.test.AnnotationDataAware2;
import com.opensymphony.xwork.test.SimpleAnnotationAction2;
import com.opensymphony.xwork.test.SimpleAnnotationAction3;
import com.opensymphony.xwork.test.AnnotationUser;
import com.opensymphony.xwork.validator.validators.*;

import java.util.List;
import java.util.Collections;


/**
 * AnnotationActionValidatorManagerTest
 *
 * @author Rainer Hermanns
 * @author Jason Carreira
 * @author tm_jee ( tm_jee (at) yahoo.co.uk )
 *         Created Jun 9, 2003 11:03:01 AM
 */
public class AnnotationActionValidatorManagerTest extends XWorkTestCase {

    protected final String alias = "validationAlias";

    AnnotationActionValidatorManager annotationActionValidatorManager;

    protected void setUp() throws Exception {
        annotationActionValidatorManager = new AnnotationActionValidatorManager();
        super.setUp();
    }

    protected void tearDown() throws Exception {
        annotationActionValidatorManager = null;
        super.tearDown();
    }

    public void testBuildValidatorKey() {
        String validatorKey = AnnotationActionValidatorManager.buildValidatorKey(SimpleAnnotationAction.class, alias);
        assertEquals(SimpleAnnotationAction.class.getName() + "/" + alias, validatorKey);
    }

    public void testBuildsValidatorsForAlias() {
        List validatorList = annotationActionValidatorManager.getValidators(SimpleAnnotationAction.class, alias);

        // 17 in the class level + 0 in the alias
        // TODO: add alias tests
        assertEquals(17, validatorList.size());
    }

    public void testDefaultMessageInterpolation() {
        // get validators
        List validatorList = annotationActionValidatorManager.getValidators(TestBean.class, "beanMessageBundle");
        assertEquals(3, validatorList.size());

        try {
            TestBean bean = new TestBean();
            bean.setName("foo");
            bean.setCount(99);

            ValidatorContext context = new GenericValidatorContext(bean);
            annotationActionValidatorManager.validate(bean, "beanMessageBundle", context);
            assertTrue(context.hasErrors());
            assertTrue(context.hasFieldErrors());

            List l = (List) context.getFieldErrors().get("count");
            assertNotNull(l);
            assertEquals(1, l.size());
            assertEquals("Smaller Invalid Count: 99", l.get(0));
        } catch (ValidationException ex) {
            ex.printStackTrace();
            fail("Validation error: " + ex.getMessage());
        }
    }

    public void testGetValidatorsForInterface() {
        List validatorList = annotationActionValidatorManager.getValidators(AnnotationDataAware2.class, alias);

        // 1 in interface hierarchy, 2 from parent interface (1 default + 1 context)
        assertEquals(3, validatorList.size());

        final FieldValidator dataValidator1 = (FieldValidator) validatorList.get(0);
        assertEquals("data", dataValidator1.getFieldName());
        assertTrue(dataValidator1 instanceof RequiredFieldValidator);

        final FieldValidator dataValidator2 = (FieldValidator) validatorList.get(1);
        assertEquals("data", dataValidator2.getFieldName());
        assertTrue(dataValidator2 instanceof RequiredStringValidator);

        final FieldValidator blingValidator = (FieldValidator) validatorList.get(2);
        assertEquals("bling", blingValidator.getFieldName());
        assertTrue(blingValidator instanceof RequiredStringValidator);
    }

    public void testGetValidatorsFromInterface() {
        List validatorList = annotationActionValidatorManager.getValidators(SimpleAnnotationAction3.class, alias);

        // 17 in the class hierarchy + 1 in the interface + 1 in interface alias
        assertEquals(19, validatorList.size());

        final FieldValidator v = (FieldValidator) validatorList.get(0);
        assertEquals("bar", v.getFieldName());
        assertTrue(v instanceof RequiredFieldValidator);

        final FieldValidator v1 = (FieldValidator) validatorList.get(1);
        assertEquals("bar", v1.getFieldName());
        assertTrue(v1 instanceof IntRangeFieldValidator);

        final FieldValidator v2 = (FieldValidator) validatorList.get(2);
        assertEquals("baz", v2.getFieldName());
        assertTrue(v2 instanceof IntRangeFieldValidator);

        final FieldValidator vdouble = (FieldValidator) validatorList.get(3);
        assertEquals("percentage", vdouble.getFieldName());
        assertTrue(vdouble instanceof DoubleRangeFieldValidator);

        final FieldValidator v3 = (FieldValidator) validatorList.get(4);
        assertEquals("date", v3.getFieldName());
        assertTrue(v3 instanceof DateRangeFieldValidator);

        // action-level validator comes first
        final Validator v4 = (Validator) validatorList.get(5);
        assertTrue(v4 instanceof ExpressionValidator);

        // action-level validator comes first
        final Validator v5 = (Validator) validatorList.get(6);
        assertTrue(v5 instanceof ExpressionValidator);

        // action-level validator comes first
        final Validator v6 = (Validator) validatorList.get(7);
        assertTrue(v6 instanceof ExpressionValidator);

        // action-level validator comes first
        final Validator v7 = (Validator) validatorList.get(8);
        assertTrue(v7 instanceof ExpressionValidator);

        // action-level validator comes first
        final Validator v8 = (Validator) validatorList.get(9);
        assertTrue(v8 instanceof ExpressionValidator);

        final FieldValidator v9 = (FieldValidator) validatorList.get(10);
        assertEquals("datefield", v9.getFieldName());
        assertTrue(v9 instanceof DateRangeFieldValidator);

        final FieldValidator v10 = (FieldValidator) validatorList.get(11);
        assertEquals("emailaddress", v10.getFieldName());
        assertTrue(v10 instanceof EmailValidator);

        final FieldValidator v11 = (FieldValidator) validatorList.get(12);
        assertEquals("intfield", v11.getFieldName());
        assertTrue(v11 instanceof IntRangeFieldValidator);

        final FieldValidator v12 = (FieldValidator) validatorList.get(13);
        assertEquals("customfield", v12.getFieldName());
        assertTrue(v12 instanceof RequiredFieldValidator);

        final FieldValidator v13 = (FieldValidator) validatorList.get(14);
        assertEquals("stringisrequired", v13.getFieldName());
        assertTrue(v13 instanceof RequiredStringValidator);

        final FieldValidator v14 = (FieldValidator) validatorList.get(15);
        assertEquals("needstringlength", v14.getFieldName());
        assertTrue(v14 instanceof StringLengthFieldValidator);

        final FieldValidator v15 = (FieldValidator) validatorList.get(16);
        assertEquals("hreflocation", v15.getFieldName());
        assertTrue(v15 instanceof URLValidator);

        final FieldValidator v16 = (FieldValidator) validatorList.get(17);
        assertEquals("data", v16.getFieldName());
        assertTrue(v16 instanceof RequiredFieldValidator);

        final FieldValidator v17 = (FieldValidator) validatorList.get(18);
        assertEquals("data", v17.getFieldName());
        assertTrue(v17 instanceof RequiredStringValidator);

    }

    public void testMessageInterpolation() {
        // get validators
        List validatorList = annotationActionValidatorManager.getValidators(TestBean.class, "beanMessageBundle");
        assertEquals(3, validatorList.size());

        try {
            TestBean bean = new TestBean();
            bean.setName("foo");
            bean.setCount(150);

            ValidatorContext context = new GenericValidatorContext(bean);
            annotationActionValidatorManager.validate(bean, "beanMessageBundle", context);
            assertTrue(context.hasErrors());
            assertTrue(context.hasFieldErrors());

            List l = (List) context.getFieldErrors().get("count");
            assertNotNull(l);
            assertEquals(1, l.size());
            assertEquals("Count must be between 1 and 100, current value is 150.", l.get(0));
        } catch (ValidationException ex) {
            ex.printStackTrace();
            fail("Validation error: " + ex.getMessage());
        }
    }

    public void testSameAliasWithDifferentClass() {
        List validatorList = annotationActionValidatorManager.getValidators(SimpleAnnotationAction.class, alias);
        List validatorList2 = annotationActionValidatorManager.getValidators(SimpleAnnotationAction2.class, alias);
        assertFalse(validatorList.size() == validatorList2.size());
    }

    public void testSkipUserMarkerActionLevelShortCircuit() {
        // get validators
        List validatorList = annotationActionValidatorManager.getValidators(AnnotationUser.class, null);
        assertEquals(10, validatorList.size());

        try {
            AnnotationUser user = new AnnotationUser();
            user.setName("Mark");
            user.setEmail("bad_email");
            user.setEmail2("bad_email");

            ValidatorContext context = new GenericValidatorContext(user);
            annotationActionValidatorManager.validate(user, "", context);
            assertTrue(context.hasFieldErrors());

            // check field errors
            List l = (List) context.getFieldErrors().get("email");
            assertNotNull(l);
            assertEquals(1, l.size());
            assertEquals("Not a valid e-mail.", l.get(0));
            l = (List) context.getFieldErrors().get("email2");
            assertNotNull(l);
            assertEquals(2, l.size());
            assertEquals("Not a valid e-mail2.", l.get(0));
            assertEquals("Email2 not from the right company.", l.get(1));

            // check action errors
            assertTrue(context.hasActionErrors());
            l = (List) context.getActionErrors();
            assertNotNull(l);
            assertEquals(2, l.size()); // both expression test failed see AnnotationUser-validation.xml
            assertEquals("Email does not start with mark", l.get(0));
        } catch (ValidationException ex) {
            ex.printStackTrace();
            fail("Validation error: " + ex.getMessage());
        }
    }

    public void testSkipAllActionLevelShortCircuit2() {
        // get validators
        List validatorList = annotationActionValidatorManager.getValidators(AnnotationUser.class, null);
        assertEquals(10, validatorList.size());

        try {
            AnnotationUser user = new AnnotationUser();
            user.setName("Mark");
            // * mark both email to starts with mark to get pass the action-level validator,
            // so we could concentrate on testing the field-level validators (AnnotationUser-validation.xml)
            // * make both email the same to pass the action-level validator at 
            // AnnotationUserMarker-validation.xml
            user.setEmail("mark_bad_email_for_field_val@foo.com");
            user.setEmail2("mark_bad_email_for_field_val@foo.com");

            ValidatorContext context = new GenericValidatorContext(user);
            annotationActionValidatorManager.validate(user, "", context);
            assertTrue(context.hasFieldErrors());

            // check field errors
            // we have an error in this field level, email does not ends with mycompany.com
            List l = (List) context.getFieldErrors().get("email");
            assertNotNull(l);
            assertEquals(1, l.size()); // because email-field-val is short-circuit
            assertEquals("Email not from the right company.", l.get(0));

            
            // check action errors
            l = (List) context.getActionErrors();
            assertFalse(context.hasActionErrors());
            assertEquals(0, l.size());
            
            
        } catch (ValidationException ex) {
            ex.printStackTrace();
            fail("Validation error: " + ex.getMessage());
        }
    }

    
    public void testActionLevelShortCircuit() throws Exception {
    	
    	List validatorList = annotationActionValidatorManager.getValidators(AnnotationUser.class, null);
        assertEquals(10, validatorList.size());
        
        AnnotationUser user = new AnnotationUser();
        // all fields will trigger error, but sc of action-level, cause it to not appear
        user.setName(null);		

        user.setEmail("rainerh(at)example.com");
        user.setEmail("rainer_h(at)example.com");


        ValidatorContext context = new GenericValidatorContext(user);
        annotationActionValidatorManager.validate(user, "", context);
    	
    	// check field level errors
        // shouldn't have any because action error prevents validation of anything else
        List l = (List) context.getFieldErrors().get("email2");
        assertNull(l);
    	
    	
        // check action errors
        assertTrue(context.hasActionErrors());
        l = (List) context.getActionErrors();
        assertNotNull(l);
        // we only get one, because AnnotationUserMarker-validation.xml action-level validator
        // already sc it   :-)
        assertEquals(1, l.size()); 
        assertEquals("Email not the same as email2", l.get(0));
    }
    
    
    public void testShortCircuitNoErrors() {
        // get validators
        List validatorList = annotationActionValidatorManager.getValidators(AnnotationUser.class, null);
        assertEquals(10, validatorList.size());

        try {
            AnnotationUser user = new AnnotationUser();
            user.setName("Mark");
            user.setEmail("mark@mycompany.com");
            user.setEmail2("mark@mycompany.com");

            ValidatorContext context = new GenericValidatorContext(user);
            annotationActionValidatorManager.validate(user, "", context);
            assertFalse(context.hasErrors());
        } catch (ValidationException ex) {
            ex.printStackTrace();
            fail("Validation error: " + ex.getMessage());
        }
    }
}
