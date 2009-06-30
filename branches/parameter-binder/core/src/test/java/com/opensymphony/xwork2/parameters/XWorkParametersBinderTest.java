/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.opensymphony.xwork2.parameters;

import com.opensymphony.xwork2.SimpleAction;
import com.opensymphony.xwork2.XWorkTestCase;

import java.util.HashMap;
import java.util.Map;

import ognl.OgnlException;

public class XWorkParametersBinderTest extends XWorkTestCase {
    private XWorkParametersBinder binder;

    public void testSimple() throws ParseException, OgnlException {
        String expr = "name";
        SimpleAction action = new SimpleAction();

        assertNull(action.getName());

        Map<String, Object> context = new HashMap<String, Object>();
        binder.setProperty(context, action, expr, "Lex Luthor");

        assertEquals("Lex Luthor", action.getName());
    }

    public void testNestedNotNull() throws ParseException, OgnlException {
        String expr = "bean.name";
        SimpleAction action = new SimpleAction();

        assertNotNull(action.getBean());
        assertNull(action.getBean().getName());

        Map<String, Object> context = new HashMap<String, Object>();
        binder.setProperty(context, action, expr, "Lex Luthor");

        assertEquals("Lex Luthor", action.getBean().getName());
    }

    public void testNestedNull() throws ParseException, OgnlException {
        String expr = "bean.name";
        SimpleAction action = new SimpleAction();
        action.setBean(null);

        assertNull(action.getBean());

        Map<String, Object> context = new HashMap<String, Object>();
        binder.setProperty(context, action, expr, "Lex Luthor");

        assertEquals("Lex Luthor", action.getBean().getName());
    }

    //Maps

    public void testSimpleMap() throws ParseException, OgnlException {
        String expr = "theExistingMap['Name']";
        SimpleAction action = new SimpleAction();

        assertNull(action.getTheExistingMap().get("Name"));

        Map<String, Object> context = new HashMap<String, Object>();
        binder.setProperty(context, action, expr, "Lex Luthor");

        assertEquals("Lex Luthor", action.getTheExistingMap().get("Name"));
    }

    public void testSimpleMapNull() throws ParseException, OgnlException {
        String expr = "someMap['Name']";
        SimpleAction action = new SimpleAction();
        action.setExistingMap(null);

        assertNull(action.getSomeMap());

        Map<String, Object> context = new HashMap<String, Object>();
        binder.setProperty(context, action, expr, "Lex Luthor");

        assertEquals("Lex Luthor", action.getSomeMap().get("Name"));
    }

    //Lists
    public void testSimpleList() throws ParseException, OgnlException {
           String expr = "someList[0]";
           SimpleAction action = new SimpleAction();

           assertEquals(0, action.getSomeList().size());

           Map<String, Object> context = new HashMap<String, Object>();
           binder.setProperty(context, action, expr, "Lex Luthor");

           assertEquals("Lex Luthor", action.getSomeList().get(0));
       }



    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.binder = container.getInstance(XWorkParametersBinder.class);
    }
}
