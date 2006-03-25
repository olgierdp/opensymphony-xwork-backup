/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.XWorkTestCase;

import java.util.List;
import java.util.HashSet;
import java.util.Arrays;

/**
 * Unit test of {@link TextParseUtil}.
 *
 * @author plightbo
 */
public class TextParseUtilTest extends XWorkTestCase {

    public void testTranslateVariables() {
        OgnlValueStack stack = new OgnlValueStack();

        Object s = TextParseUtil.translateVariables("foo: ${{1, 2, 3}}, bar: ${1}", stack);
        assertEquals("foo: [1, 2, 3], bar: 1", s);

        s = TextParseUtil.translateVariables("foo: ${#{1 : 2, 3 : 4}}, bar: ${1}", stack);
        assertEquals("foo: {1=2, 3=4}, bar: 1", s);

        s = TextParseUtil.translateVariables("foo: 1}", stack);
        assertEquals("foo: 1}", s);

        s = TextParseUtil.translateVariables("foo: {1}", stack);
        assertEquals("foo: {1}", s);

        s = TextParseUtil.translateVariables("foo: ${1", stack);
        assertEquals("foo: ${1", s);

        s =  TextParseUtil.translateVariables('$', "${{1, 2, 3}}", stack, Object.class);
        assertTrue("List not returned when parsing a 'pure' list", s instanceof List);

        s =  TextParseUtil.translateVariables('$', "${1} two ${3}", stack, Object.class);
        assertEquals("1 two 3", s);

        s = TextParseUtil.translateVariables('$', "count must be between ${123} and ${456}, current value is ${98765}.", stack, Object.class);
        assertEquals("count must be between 123 and 456, current value is 98765.", s);
    }

    public void testCommaDelimitedStringToSet() {
        assertEquals(0, TextParseUtil.commaDelimitedStringToSet("").size());
        assertEquals(new HashSet(Arrays.asList(new String[] { "foo", "bar", "tee" })),
                TextParseUtil.commaDelimitedStringToSet(" foo, bar,tee"));
    }

    public void testTranslateVariablesOpenChar() {
        // just a quick test to see if the open char works
        // most test are done the methods above
        OgnlValueStack stack = new OgnlValueStack();

        Object s = TextParseUtil.translateVariables('$', "foo: ${{1, 2, 3}}, bar: ${1}", stack);
        assertEquals("foo: [1, 2, 3], bar: 1", s);

        Object s2 = TextParseUtil.translateVariables('#', "foo: #{{1, 2, 3}}, bar: #{1}", stack);
        assertEquals("foo: [1, 2, 3], bar: 1", s2);
    }

    public void testTranslateNoVariables() {
        OgnlValueStack stack = new OgnlValueStack();

        Object s = TextParseUtil.translateVariables('$', "foo: ${}", stack);
        assertEquals("foo: ", s);
    }

}
