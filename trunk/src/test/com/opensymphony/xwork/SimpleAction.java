/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class SimpleAction extends ActionSupport {
    //~ Static fields/initializers /////////////////////////////////////////////

    public static final String COMMAND_RETURN_CODE = "com.opensymphony.xwork.SimpleAction.CommandInvoked";

    public SimpleAction() {
        System.out.println("hi");
    }

    //~ Instance fields ////////////////////////////////////////////////////////

    private ArrayList someList = new ArrayList();
    private Date date = new Date();
    private Properties settings = new Properties();
    private String blah;
    private String name;
    private TestBean bean = new TestBean();
    private int bar;
    private int baz;
    private int foo;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setBar(int bar) {
        this.bar = bar;
    }

    public int getBar() {
        return bar;
    }

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

    public void setSomeList(ArrayList someList) {
        this.someList = someList;
    }

    public ArrayList getSomeList() {
        return someList;
    }

    public String commandMethod() throws Exception {
        return COMMAND_RETURN_CODE;
    }

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
