/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.validator.annotations.*;
import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.xwork.TestBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;


/**
 * Simple Test Action for annotaton processing.
 *
 * @author $author$
 * @version $Revision$
 */
@Validation()
public class SimpleAnnotationAction extends ActionSupport {
    //~ Static fields/initializers /////////////////////////////////////////////

    public static final String COMMAND_RETURN_CODE = "com.opensymphony.xwork.validator.SimpleAnnotationAction.CommandInvoked";

    //~ Instance fields ////////////////////////////////////////////////////////

    private ArrayList someList = new ArrayList();
    private Date date = new Date();
    private Properties settings = new Properties();
    private String blah;
    private String name;
    private TestBean bean = new TestBean();
    private boolean throwException;
    private int bar;
    private int baz;
    private int foo;
    
    private String aliasSource;
    private String aliasDest;
    
    

    //~ Constructors ///////////////////////////////////////////////////////////

    public SimpleAnnotationAction() {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /*
        <field name="bar">
            <field-validator type="required">
                <message>You must enter a value for bar.</message>
            </field-validator>
            <field-validator type="int">
                <param name="min">6</param>
                <param name="max">10</param>
                <message>bar must be between ${min} and ${max}, current value is ${bar}.</message>
            </field-validator>
        </field>
    */
    @RequiredFieldValidator(type = ValidatorType.FIELD, message = "You must enter a value for bar.")
    @IntRangeFieldValidator(type = ValidatorType.FIELD, min = "6", max = "10", message = "bar must be between ${min} and ${max}, current value is ${bar}.")
    public void setBar(int bar) {
        this.bar = bar;
    }

    public int getBar() {
        return bar;
    }

    /*
        <field name="baz">
            <field-validator type="int">
                <param name="min">0</param>
                <message key="baz.range">Could not find baz.range!</message>
            </field-validator>
        </field>
    */
    @IntRangeFieldValidator(min = "0", key = "baz.range", message = "Could not find baz.range!")
    public void setBaz(int baz) {
        this.baz = baz;
    }

    public int getBaz() {
        return baz;
    }

    public void setBean(TestBean bean) {
        this.bean = bean;
    }

    public TestBean getBean() {
        return bean;
    }

    public void setBlah(String blah) {
        this.blah = blah;
    }

    public String getBlah() {
        return blah;
    }

    public Boolean getBool(String b) {
        return new Boolean(b);
    }

    public boolean[] getBools() {
        boolean[] b = new boolean[] {true, false, false, true};

        return b;
    }

    /*
        <field name="date">
            <field-validator type="date">
                <param name="min">12/22/2002</param>
                <param name="max">12/25/2002</param>
                <message>The date must be between 12-22-2002 and 12-25-2002.</message>
            </field-validator>
        </field>
    */
    @DateRangeFieldValidator(min = "12/22/2002", max = "12/25/2002", message = "The date must be between 12-22-2002 and 12-25-2002.")
    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setFoo(int foo) {
        this.foo = foo;
    }

    public int getFoo() {
        return foo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSettings(Properties settings) {
        this.settings = settings;
    }

    public Properties getSettings() {
        return settings;
    }


    public String getAliasDest() {
        return aliasDest;
    }

    public void setAliasDest(String aliasDest) {
        this.aliasDest = aliasDest;
    }

    public String getAliasSource() {
        return aliasSource;
    }

    public void setAliasSource(String aliasSource) {
        this.aliasSource = aliasSource;
    }

    
    public void setSomeList(ArrayList someList) {
        this.someList = someList;
    }

    public ArrayList getSomeList() {
        return someList;
    }

    public void setThrowException(boolean throwException) {
        this.throwException = throwException;
    }

    public String commandMethod() throws Exception {
        return COMMAND_RETURN_CODE;
    }

    public String exceptionMethod() throws Exception {
        if (throwException) {
            throw new Exception("We're supposed to throw this");
        }

        return "OK";
    }

    @Validations(
            requiredFields =
                    {@RequiredFieldValidator(type = ValidatorType.SIMPLE, fieldName = "customfield", message = "You must enter a value for field.")},
            requiredStrings =
                    {@RequiredStringValidator(type = ValidatorType.SIMPLE, fieldName = "stringisrequired", message = "You must enter a value for string.")},
            emails =
                    { @EmailValidator(type = ValidatorType.SIMPLE, fieldName = "emailaddress", message = "You must enter a value for email.")},
            urls =
                    { @UrlValidator(type = ValidatorType.SIMPLE, fieldName = "hreflocation", message = "You must enter a value for email.")},
            stringLengthFields =
                    {@StringLengthFieldValidator(type = ValidatorType.SIMPLE, trim = true, minLength="10" , maxLength = "12", fieldName = "needstringlength", message = "You must enter a stringlength.")},
            intRangeFields =
                    { @IntRangeFieldValidator(type = ValidatorType.SIMPLE, fieldName = "intfield", min = "6", max = "10", message = "bar must be between ${min} and ${max}, current value is ${bar}.")},
            dateRangeFields =
                    {@DateRangeFieldValidator(type = ValidatorType.SIMPLE, fieldName = "datefield", min = "-1", max = "99", message = "bar must be between ${min} and ${max}, current value is ${bar}.")},
            expressions = {
                @ExpressionValidator(expression = "foo &gt; 1", message = "Foo must be greater than Bar 1. Foo = ${foo}, Bar = ${bar}."),
                @ExpressionValidator(expression = "foo &gt; 2", message = "Foo must be greater than Bar 2. Foo = ${foo}, Bar = ${bar}."),
                @ExpressionValidator(expression = "foo &gt; 3", message = "Foo must be greater than Bar 3. Foo = ${foo}, Bar = ${bar}."),
                @ExpressionValidator(expression = "foo &gt; 4", message = "Foo must be greater than Bar 4. Foo = ${foo}, Bar = ${bar}."),
                @ExpressionValidator(expression = "foo &gt; 5", message = "Foo must be greater than Bar 5. Foo = ${foo}, Bar = ${bar}.")
    }
    )
    public String execute() throws Exception {
        if (foo == bar) {
            return ERROR;
        }

        baz = foo + bar;

        name = "HelloWorld";
        settings.put("foo", "bar");
        settings.put("black", "white");

        someList.add("jack");
        someList.add("bill");
        someList.add("kerry");

        return SUCCESS;
    }
}
