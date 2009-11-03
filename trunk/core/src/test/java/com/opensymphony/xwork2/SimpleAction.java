/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.inject.Inject;

import java.util.*;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class SimpleAction extends ActionSupport {

    public static final String COMMAND_RETURN_CODE = "com.opensymphony.xwork2.SimpleAction.CommandInvoked";


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
    private long longFoo;
    private short shortFoo;
    private double percentage;
    private Map<Integer,String> indexedProps = new HashMap<Integer,String>();

    private String aliasSource;
    private String aliasDest;
    private Map<String,String> protectedMap = new HashMap<String,String>();
    private Map<String,String> existingMap = new HashMap<String,String>();
    private Map<String,String> someMap;
    private String[] stringArray = new String[5];
    private int[] intArray = new int[5];
    private Collection<SimpleAction> someCollection = new ArrayList<SimpleAction>();

    private Map<String,TestBean> otherMap = new HashMap<String,TestBean>();
    private List<TestBean> otherList = new ArrayList<TestBean>();

    public static boolean resultCalled;
    private SimpleAction nestedAction;

    public SimpleAction() {
        resultCalled = false;
        existingMap.put("existingKey", "value");
    }

    public List<TestBean> getOtherList() {
        return otherList;
    }

    public void setOtherList(List<TestBean> otherList) {
        this.otherList = otherList;
    }

    public void setOtherMap(Map<String, TestBean> otherMap) {
        this.otherMap = otherMap;
    }

    public Map<String, TestBean> getOtherMap() {
        return otherMap;
    }

    public Collection<SimpleAction> getSomeCollection() {
        return someCollection;
    }

    public void setSomeCollection(Collection<SimpleAction> someCollection) {
        this.someCollection = someCollection;
    }

    public SimpleAction getNestedAction() {
        return nestedAction;
    }

    public void setNestedAction(SimpleAction nestedAction) {
        this.nestedAction = nestedAction;
    }

    public String[] getStringArray() {
        return stringArray;
    }

    public void setStringArray(String[] stringArray) {
        this.stringArray = stringArray;
    }

    public int[] getIntArray() {
        return intArray;
    }

    public void setIntArray(int[] intArray) {
        this.intArray = intArray;
    }

    public Map<String, String> getSomeMap() {
        return someMap;
    }

    public void setSomeMap(Map<String, String> someMap) {
        this.someMap = someMap;
    }

    public Map<String,String> getTheProtectedMap() {
        return protectedMap;
    }
    
    protected Map<String,String> getTheSemiProtectedMap() {
        return protectedMap;
    }

    public void setExistingMap(Map<String,String> map) {
        this.existingMap = map;
    }

    public Map<String,String> getTheExistingMap() {
        return existingMap;
    }


    public void setBar(int bar) {
        this.bar = bar;
    }

    public int getBar() {
        return bar;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
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
        boolean[] b = new boolean[]{true, false, false, true};

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
    
    public String getIndexedProp(int index) {
    	return indexedProps.get(index);
    }
    
    public void setIndexedProp(int index, String val) {
    	indexedProps.put(index, val);
    }
    

    public void setThrowException(boolean   throwException) {
        this.throwException = throwException;
    }

    public String commandMethod() throws Exception {
        return COMMAND_RETURN_CODE;
    }
    
    public Result resultAction() throws Exception {
    	return new Result() {
            public Configuration configuration;

            @Inject
            public void setConfiguration(Configuration config) {
                this.configuration = config;
            }
            public void execute(ActionInvocation invocation) throws Exception {
                if (configuration != null)
                    resultCalled = true;
            }
    	    
    	};
    }

    public String exceptionMethod() throws Exception {
        if (throwException) {
            throw new Exception("We're supposed to throw this");
        }

        return "OK";
    }

    @Override
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
    
    public String doInput() throws Exception {
        return INPUT;
    }


    public long getLongFoo() {
        return longFoo;
    }


    public void setLongFoo(long longFoo) {
        this.longFoo = longFoo;
    }


    public short getShortFoo() {
        return shortFoo;
    }


    public void setShortFoo(short shortFoo) {
        this.shortFoo = shortFoo;
    }
}
