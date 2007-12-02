package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.xwork.XWorkTestCase;
import com.opensymphony.xwork.validator.validators.FieldExpressionValidator;
import com.opensymphony.xwork.validator.validators.RequiredFieldValidator;
import com.opensymphony.xwork.validator.validators.RequiredStringValidator;
import com.opensymphony.xwork.validator.validators.VisitorFieldValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A test case for ActionValidatorManager. 
 *
 * @author tmjee
 * @version $Date$ $Id$
 */
public class ActionValidatorManagerTest extends XWorkTestCase {



    public void testValidate() throws Exception {
        /* MockAction.class */
        // reference number 
        final RequiredStringValidator referenceNumberRequiredStringValidator = new RequiredStringValidator();
        referenceNumberRequiredStringValidator.setFieldName("referenceNumber");
        referenceNumberRequiredStringValidator.setDefaultMessage("Reference number is required");

        // order
        final RequiredFieldValidator orderRequiredValidator = new RequiredFieldValidator();
        orderRequiredValidator.setFieldName("order");
        orderRequiredValidator.setDefaultMessage("Order is required");

        // customer
        final RequiredFieldValidator customerRequiredValidator = new RequiredFieldValidator();
        customerRequiredValidator.setFieldName("customer");
        customerRequiredValidator.setDefaultMessage("Customer is required");
        final VisitorFieldValidator customerVisitorValidator = new VisitorFieldValidator();
        customerVisitorValidator.setAppendPrefix(true);
        customerVisitorValidator.setFieldName("customer");

        /* Customer.class */
        // customer -> name
        final RequiredStringValidator customerNameRequiredStringValidator = new RequiredStringValidator();
        customerNameRequiredStringValidator.setFieldName("name");
        customerNameRequiredStringValidator.setDefaultMessage("Name is required");

        // customer -> age
        final RequiredFieldValidator customerAgeRequiredValidator = new RequiredFieldValidator();
        customerAgeRequiredValidator.setFieldName("age");
        customerAgeRequiredValidator.setDefaultMessage("Age is required");

        // customer -> Address
        final RequiredFieldValidator customerAddressRequiredFieldValidator = new RequiredFieldValidator();
        customerAddressRequiredFieldValidator.setFieldName("address");
        customerAddressRequiredFieldValidator.setDefaultMessage("Address is required");
        final VisitorFieldValidator customerAddressVisitorFieldValidator = new VisitorFieldValidator();
        customerAddressVisitorFieldValidator.setFieldName("address");
        customerAddressVisitorFieldValidator.setAppendPrefix(true);
        //customerAddressVisitorFieldValidator.setDefaultMessage("");


        /* Address.class */
        // customer -> Address -> street
        final RequiredStringValidator customerAddressStreetRequiredFieldValidator = new RequiredStringValidator();
        customerAddressStreetRequiredFieldValidator.setFieldName("street");
        customerAddressStreetRequiredFieldValidator.setDefaultMessage("Street is required");
        customerAddressStreetRequiredFieldValidator.setShortCircuit(true);
        final RequiredStringValidator customerAddressStreetRequiredFieldValidator2 = new RequiredStringValidator();
        customerAddressStreetRequiredFieldValidator2.setFieldName("street");
        customerAddressStreetRequiredFieldValidator2.setDefaultMessage("Street is required 2");
        customerAddressStreetRequiredFieldValidator2.setShortCircuit(true);


        // customer -> Address -> pobox
        final RequiredStringValidator customerAddressPoboxRequiredFieldValidator = new RequiredStringValidator();
        customerAddressPoboxRequiredFieldValidator.setFieldName("pobox");
        customerAddressPoboxRequiredFieldValidator.setDefaultMessage("PO Box is required");
        customerAddressPoboxRequiredFieldValidator.setShortCircuit(false);
        final RequiredStringValidator customerAddressPoboxRequiredFieldValidator2 = new RequiredStringValidator();
        customerAddressPoboxRequiredFieldValidator2.setFieldName("pobox");
        customerAddressPoboxRequiredFieldValidator2.setDefaultMessage("PO Box is required 2");
        customerAddressPoboxRequiredFieldValidator2.setShortCircuit(false);



        final List validatorsForMockAction = new ArrayList() {
            {
                add(referenceNumberRequiredStringValidator);
                add(orderRequiredValidator);
                add(customerRequiredValidator);
                add(customerVisitorValidator);
            }
        };

        final List validatorsForCustomer = new ArrayList() {
            {
                add(customerNameRequiredStringValidator);
                add(customerAgeRequiredValidator);
                add(customerAddressRequiredFieldValidator);
                add(customerAddressVisitorFieldValidator);
            }
        };

        final List validatorsForAddress = new ArrayList() {
            {
                add(customerAddressStreetRequiredFieldValidator);
                add(customerAddressStreetRequiredFieldValidator2);
                add(customerAddressPoboxRequiredFieldValidator);
                add(customerAddressPoboxRequiredFieldValidator2);
            }
        };


        ActionValidatorManager validatorManager = new AbstractActionValidatorManager() {
            public List getValidators(Class clazz, String context) {
                if (clazz.isAssignableFrom(MockAction.class)) {
                    return validatorsForMockAction;
                }
                else if (clazz.isAssignableFrom(Customer.class)) {
                    return validatorsForCustomer;
                }
                else if (clazz.isAssignableFrom(Address.class)) {
                    return validatorsForAddress;
                }
                return Collections.EMPTY_LIST;
            }
        };
        ActionValidatorManagerFactory.setInstance(validatorManager);
        MockAction action = new MockAction();
        validatorManager.validate(action, "ctx");
        ActionValidatorManagerFactory.setInstance(new DefaultActionValidatorManager());

        assertFalse(action.hasActionErrors());
        assertFalse(action.hasActionMessages());
        assertTrue(action.hasFieldErrors());
        assertTrue(action.getFieldErrors().containsKey("referenceNumber"));
        assertEquals(1, ((List)action.getFieldErrors().get("referenceNumber")).size());
        assertTrue(action.getFieldErrors().containsKey("order"));
        assertEquals(1, ((List)action.getFieldErrors().get("order")).size());
        assertTrue(action.getFieldErrors().containsKey("customer.name"));
        assertEquals(1, ((List)action.getFieldErrors().get("customer.name")).size());
        assertTrue(action.getFieldErrors().containsKey("customer.age"));
        assertEquals(1, ((List)action.getFieldErrors().get("customer.age")).size());
        assertTrue(action.getFieldErrors().containsKey("customer.address.street"));
        assertEquals(1, ((List)action.getFieldErrors().get("customer.address.street")).size());
        assertTrue(action.getFieldErrors().containsKey("customer.address.pobox"));
        assertEquals(2, ((List)action.getFieldErrors().get("customer.address.pobox")).size());

        for (Iterator i = action.getFieldErrors().keySet().iterator(); i.hasNext(); ) {
            System.out.println(i.next());   
        }


        for (Iterator i = ((List)action.getFieldErrors().get("customer.address.street")).iterator(); i.hasNext();) {
            System.out.println("**"+i.next());
        }
        for (Iterator i = ((List)action.getFieldErrors().get("customer.address.pobox")).iterator(); i.hasNext();) {
            System.out.println("**"+i.next());    
        }

    }

    private class MockAction extends ActionSupport {
        
        private String referenceNumber;
        private Integer order;
        private Customer customer = new Customer();


        public String getReferenceNumber() { return referenceNumber; }
        public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

        public Integer getOrder() { return order; }
        public void setOrder(Integer order) { this.order = order; }

        public Customer getCustomer() { return customer; }
        public void setCustomer(Customer customer) { this.customer = customer; }
    }


    private class Customer {
        private String name;
        private Integer age;
        private Address address = new Address();

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }

        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }
    }

    private class Address {
        private String street;
        private String pobox;

        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }

        public String getPobox() { return pobox; }
        public void setPobox(String pobox) { this.pobox = pobox; }
    }
}
