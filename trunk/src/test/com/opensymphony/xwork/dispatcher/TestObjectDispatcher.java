/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.dispatcher;

import com.opensymphony.xwork.util.OgnlValueStack;

import junit.framework.*;

import java.util.ArrayList;
import java.util.List;


/**
 * test the object dispatcher
 * @authorPeter Kelley
 */
public class TestObjectDispatcher extends TestCase implements MappingFactory, ResultObjectFactory {
    //~ Instance fields ////////////////////////////////////////////////////////

    private ObjectDispatcher objectDispatcher = null;
    private String bar = "bar";
    private String foo = "foo";
    private String foobar = "something else";

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * default constructor
     * @param name The name of the test fixture
     */
    public TestObjectDispatcher(String name) {
        super(name);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Setter for property bar.
     * @param newBar the new value for field bar
     */
    public void setBar(String newBar) {
        bar = newBar;
    }

    /**
     * Getter for property bar.
     * @return the value of field bar
     */
    public String getBar() {
        return bar;
    }

    /**
     * Setter for property foo.
     * @param newFoo the new value for field foo
     */
    public void setFoo(String newFoo) {
        foo = newFoo;
    }

    /**
     * Getter for property foo.
     * @return the value of field foo
     */
    public String getFoo() {
        return foo;
    }

    /**
     * Setter for property foobar.
     * @param newFoobar the new value for field foobar
     */
    public void setFoobar(String newFoobar) {
        foobar = newFoobar;
    }

    /**
     * Getter for property foobar.
     * @return the value of field foobar
     */
    public String getFoobar() {
        return foobar;
    }

    /**
     * Get a mapping set with the given name.
     *
     * @param name The name of the mapping set to get
     * @return Either the mapping set with the given name or <code>null</code> if no mapping set
     *   exists with that name
     */
    public MappingSet getMappingSet(String name) {
        MappingSet mappingSet = new MappingSet(name);

        Mapping newMapping = new Mapping("blah", "foo + bar");
        List parameterList = new ArrayList();
        parameterList.add(newMapping);
        mappingSet.setParameterMappings(parameterList);

        Mapping resultMapping = new Mapping("blah", "foobar");
        List resultList = new ArrayList();
        resultList.add(resultMapping);
        mappingSet.setResultMappings(resultList);

        return mappingSet;
    }

    /**
     * Get the result object
     * @param key The key of the object to get (ignored)
     * @return this
     */
    public Object getResultObject(String key) {
        return this;
    }

    /**
     * Test dispatching from an object
     * @throws Exception
     */
    public void testDispatch() throws Exception {
        String namespace = "";
        String action = "test";
        String mappingName = "dispatch1";
        ResultObjectFactory resultObjectFactory = this;
        OgnlValueStack result = objectDispatcher.dispatch(this, namespace, action, mappingName, resultObjectFactory);
        assertEquals("Value returned was incorrect", getFoo() + getBar(), result.findValue("blah"));
        assertEquals("Value on result not set correctly", getFoo() + getBar(), getFoobar());
    }

    /**
     * set up for a test
     * @throws Exception if something bad happens
     */
    protected void setUp() throws Exception {
        super.setUp();
        objectDispatcher = new ObjectDispatcher(this);
    }

    /**
     * clean up after a test
     * @throws Exception if something bad happens
     */
    protected void tearDown() throws Exception {
        objectDispatcher = null;
        super.tearDown();
    }
}
