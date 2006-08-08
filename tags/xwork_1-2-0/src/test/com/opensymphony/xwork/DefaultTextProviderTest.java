/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import junit.framework.TestCase;

import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.opensymphony.xwork.util.LocalizedTextUtil;
import com.opensymphony.xwork.util.OgnlValueStack;

/**
 * Unit test for {@link DefaultTextProvider}.
 *
 * @author Claus Ibsen
 */
public class DefaultTextProviderTest extends TestCase {

    private DefaultTextProvider tp;

    public void testSimpleGetTexts() throws Exception {
        assertEquals("Hello World", tp.getText("hello"));
        assertEquals(null, tp.getText("not.in.bundle"));

        assertEquals("Hello World", tp.getText("hello", "this is default"));
        assertEquals("this is default", tp.getText("not.in.bundle", "this is default"));

        List nullList = null;
        assertEquals("Hello World", tp.getText("hello", nullList));

        String[] nullStrings = null;
        assertEquals("Hello World", tp.getText("hello", nullStrings));
    }

   public void testGetTextsWithArgs() throws Exception {
        assertEquals("Hello World", tp.getText("hello", "this is default", "from me")); // no args in bundle
        assertEquals("Hello World from me", tp.getText("hello.0", "this is default", "from me"));
        assertEquals("this is default", tp.getText("not.in.bundle", "this is default", "from me"));
        assertEquals("this is default from me", tp.getText("not.in.bundle", "this is default {0}", "from me"));

        assertEquals(null, tp.getText("not.in.bundle"));
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

        assertEquals(null, tp.getText("not.in.bundle", args));

        assertEquals("Hello World", tp.getText("hello", "this is default", (List) null));
        assertEquals("this is default", tp.getText("not.in.bundle", "this is default", (List) null));
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

        assertEquals(null, tp.getText("not.in.bundle", args));

        assertEquals("Hello World", tp.getText("hello", "this is default", (String[]) null));
        assertEquals("this is default", tp.getText("not.in.bundle", "this is default", (String[]) null));
    }

    public void testGetTextsWithListAndStack() throws Exception {
        OgnlValueStack stack = new OgnlValueStack();

        List args = new ArrayList();
        args.add("Santa");
        args.add("loud");
        assertEquals("Hello World", tp.getText("hello", "this is default", args, stack)); // no args in bundle
        assertEquals("Hello World Santa", tp.getText("hello.0", "this is default", args, stack)); // only 1 arg in bundle
        assertEquals("Hello World. This is Santa speaking loud", tp.getText("hello.1", "this is default", args, stack));

        assertEquals("this is default", tp.getText("not.in.bundle", "this is default", args, stack));
        assertEquals("this is default Santa", tp.getText("not.in.bundle", "this is default {0}", args, stack));
        assertEquals("this is default Santa speaking loud", tp.getText("not.in.bundle", "this is default {0} speaking {1}", args, stack));
    }

    public void testGetTextsWithArrayAndStack() throws Exception {
        OgnlValueStack stack = new OgnlValueStack();

        String[] args = { "Santa", "loud" };
        assertEquals("Hello World", tp.getText("hello", "this is default", args, stack)); // no args in bundle
        assertEquals("Hello World Santa", tp.getText("hello.0", "this is default", args, stack)); // only 1 arg in bundle
        assertEquals("Hello World. This is Santa speaking loud", tp.getText("hello.1", "this is default", args, stack));

        assertEquals("this is default", tp.getText("not.in.bundle", "this is default", args, stack));
        assertEquals("this is default Santa", tp.getText("not.in.bundle", "this is default {0}", args, stack));
        assertEquals("this is default Santa speaking loud", tp.getText("not.in.bundle", "this is default {0} speaking {1}", args, stack));
    }

    public void testGetBundle() throws Exception {
        assertNull(tp.getTexts()); // always returns null

        ResourceBundle rb = ResourceBundle.getBundle(TextProviderSupportTest.class.getName(), Locale.CANADA);
        assertEquals(rb, tp.getTexts(TextProviderSupportTest.class.getName()));
    }

    protected void setUp() throws Exception {
        ActionContext.getContext().setLocale(Locale.CANADA);

        LocalizedTextUtil.clearDefaultResourceBundles();
        LocalizedTextUtil.addDefaultResourceBundle(DefaultTextProviderTest.class.getName());

        tp = DefaultTextProvider.INSTANCE;
    }

    protected void tearDown() throws Exception {
        ActionContext.getContext().setLocale(null);
        tp = null;
    }


}
