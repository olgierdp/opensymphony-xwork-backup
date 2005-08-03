package com.opensymphony.xwork.util;

import com.opensymphony.xwork.XWorkTestCase;

/**
 * User: plightbo
 * Date: Aug 3, 2005
 * Time: 5:41:36 AM
 */
public class TextParseUtilTest extends XWorkTestCase {
    public void testTranslateVariables() {
        OgnlValueStack stack = new OgnlValueStack();

        String s = TextParseUtil.translateVariables("foo: ${{1, 2, 3}}, bar: ${1}", stack);
        assertEquals("foo: [1, 2, 3], bar: 1", s);

        s = TextParseUtil.translateVariables("foo: ${#{1 : 2, 3 : 4}}, bar: ${1}", stack);
        assertEquals("foo: {1=2, 3=4}, bar: 1", s);

        s = TextParseUtil.translateVariables("foo: 1}", stack);
        assertEquals("foo: 1}", s);

        s = TextParseUtil.translateVariables("foo: {1}", stack);
        assertEquals("foo: {1}", s);

        s = TextParseUtil.translateVariables("foo: ${1", stack);
        assertEquals("foo: ${1", s);

    }
}
