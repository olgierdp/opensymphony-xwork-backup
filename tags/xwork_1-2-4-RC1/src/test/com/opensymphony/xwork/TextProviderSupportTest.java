/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork;

import java.util.*;

/**
 * Unit test for {@link TextProviderSupport}.
 *
 * @author Claus Ibsen
 * @author tmjee
 */
public class TextProviderSupportTest extends XWorkTestCase {

    private TextProviderSupport tp;
    private java.util.ResourceBundle rb;

    public void testHasKey() throws Exception {
    	assertTrue(tp.hasKey("hello"));
    	assertFalse(tp.hasKey("not.in.bundle"));
    }
    
    
    public void testSimpleGetTexts() throws Exception {
        assertEquals("Hello World", tp.getText("hello"));
        assertEquals("not.in.bundle", tp.getText("not.in.bundle"));

        assertEquals("Hello World", tp.getText("hello", "this is default"));
        assertEquals("this is default", tp.getText("not.in.bundle", "this is default"));
    }

    public void testGetTextsWithArgs() throws Exception {
        assertEquals("Hello World", tp.getText("hello", "this is default", "from me")); // no args in bundle
        assertEquals("Hello World from me", tp.getText("hello.0", "this is default", "from me"));
        assertEquals("this is default", tp.getText("not.in.bundle", "this is default", "from me"));
        assertEquals("this is default from me", tp.getText("not.in.bundle", "this is default {0}", "from me"));

        assertEquals("not.in.bundle", tp.getText("not.in.bundle"));
    }

    public void testGetTextsWithListArgs() throws Exception {
        List args = new ArrayList();
        args.add("Santa");
        args.add("loud");
        assertEquals("Hello World", tp.getText("hello", "this is default", args)); // no args in bundle
        assertEquals("Hello World Santa", tp.getText("hello.0", "this is default", args)); // only 1 arg in bundle
        assertEquals("Hello World. This is Santa speaking loud", tp.getText("hello.1", "this is default", args));

        assertEquals("this is default", tp.getText("not.in.bundle", "this is default", args));
        assertEquals("this is default Santa", tp.getText("not.in.bundle", "this is default {0}", args));
        assertEquals("this is default Santa speaking loud", tp.getText("not.in.bundle", "this is default {0} speaking {1}", args));

        assertEquals("Hello World", tp.getText("hello", args)); // no args in bundle
        assertEquals("Hello World Santa", tp.getText("hello.0", args)); // only 1 arg in bundle
        assertEquals("Hello World. This is Santa speaking loud", tp.getText("hello.1", args));

        assertEquals("not.in.bundle", tp.getText("not.in.bundle", args));
    }

    public void testGetTextsWithArrayArgs() throws Exception {
        String[] args = { "Santa", "loud" };
        assertEquals("Hello World", tp.getText("hello", "this is default", args)); // no args in bundle
        assertEquals("Hello World Santa", tp.getText("hello.0", "this is default", args)); // only 1 arg in bundle
        assertEquals("Hello World. This is Santa speaking loud", tp.getText("hello.1", "this is default", args));

        assertEquals("this is default", tp.getText("not.in.bundle", "this is default", args));
        assertEquals("this is default Santa", tp.getText("not.in.bundle", "this is default {0}", args));
        assertEquals("this is default Santa speaking loud", tp.getText("not.in.bundle", "this is default {0} speaking {1}", args));

        assertEquals("Hello World", tp.getText("hello", args)); // no args in bundle
        assertEquals("Hello World Santa", tp.getText("hello.0", args)); // only 1 arg in bundle
        assertEquals("Hello World. This is Santa speaking loud", tp.getText("hello.1", args));

        assertEquals("not.in.bundle", tp.getText("not.in.bundle", args));
    }

    public void testGetBundle() throws Exception {
        assertEquals(rb, tp.getTexts());
        assertEquals(rb, tp.getTexts(TextProviderSupportTest.class.getName()));
    }

    public void testDifficultSymbols1() {
        String val= tp.getText("symbols1"); 
        assertEquals("\"=!@#$%^&*(){qwe}<>?:|}{[]\\';/.,<>`~'", val);
    }

    public void testDifficultSymbols2() {
        String val= tp.getText("symbols2"); 
        assertEquals("\"=!@#$%^&*()<>?:|[]\\';/.,<>`~'", val);
    } 
    
    protected void setUp() throws Exception {
        super.setUp();
        rb = ResourceBundle.getBundle(TextProviderSupportTest.class.getName(), Locale.ENGLISH);
        tp = new TextProviderSupport(rb, new LocaleProvider() {
            public Locale getLocale() {
                return Locale.ENGLISH;
            }
        });
    }

    protected void tearDown() throws Exception {
        rb = null;
        tp = null;
    }


}

