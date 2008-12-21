/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.XWorkTestCase;

import java.util.Arrays;

/**
 * Unit test for {@link com.opensymphony.xwork2.util.TextUtils}.
 */
public class TextUtilsTest extends XWorkTestCase {

	public void testJavaScriptEscapeNoEscapes() {
        assertEquals("", TextUtils.escapeJavaScript(null));
        assertEquals("", TextUtils.escapeJavaScript(""));
        assertEquals("   ", TextUtils.escapeJavaScript("   "));
        assertEquals("Hello World", TextUtils.escapeJavaScript("Hello World"));
	}
	
	public void testJavaScriptEscape() {
		assertEquals("\\t", TextUtils.escapeJavaScript("\t"));
		assertEquals("\\b", TextUtils.escapeJavaScript("\b"));
		assertEquals("\\n", TextUtils.escapeJavaScript("\n"));
		assertEquals("\\f", TextUtils.escapeJavaScript("\f"));
		assertEquals("\\r", TextUtils.escapeJavaScript("\r"));
		assertEquals("\\\\", TextUtils.escapeJavaScript("\\"));
		assertEquals("\\\"", TextUtils.escapeJavaScript("\""));
		assertEquals("\\'", TextUtils.escapeJavaScript("'"));
		assertEquals("<\\/script", TextUtils.escapeJavaScript("</script"));
	}
	
    public void testHtmlEncodeNoHTML() {
        assertEquals("", TextUtils.htmlEncode(null));
        assertEquals("", TextUtils.htmlEncode(""));
        assertEquals("   ", TextUtils.htmlEncode("   "));
        assertEquals("Hello World", TextUtils.htmlEncode("Hello World"));
    }

    public void testHtmlEncodeNoHTMLNoSpecial() {
        assertEquals("", TextUtils.htmlEncode(null, false));
        assertEquals("", TextUtils.htmlEncode("", false));
        assertEquals("   ", TextUtils.htmlEncode("   ", false));
        assertEquals("Hello World", TextUtils.htmlEncode("Hello World", false));
    }

    public void testHtmlEncodeSimple() {
        assertEquals("Me &amp; My", TextUtils.htmlEncode("Me & My"));
        assertEquals("Me &amp; My &amp; You", TextUtils.htmlEncode("Me & My & You"));
        assertEquals("Me &amp; &quot;Special one&quot;", TextUtils.htmlEncode("Me & \"Special one\""));
        assertEquals("100 &lt; 200", TextUtils.htmlEncode("100 < 200"));
        assertEquals("200 &gt; 100", TextUtils.htmlEncode("200 > 100"));
    }

    public void testHtmlEncodeSimpleNoSpecial() {
        assertEquals("Me &amp; My", TextUtils.htmlEncode("Me & My", false));
        assertEquals("Me &amp; My &amp; You", TextUtils.htmlEncode("Me & My & You", false));
        assertEquals("Me &amp; &quot;Special one&quot;", TextUtils.htmlEncode("Me & \"Special one\"", false));
        assertEquals("100 &lt; 200", TextUtils.htmlEncode("100 < 200", false));
        assertEquals("200 &gt; 100", TextUtils.htmlEncode("200 > 100", false));
    }

    public void testSpecialChars() {
        assertEquals("Sp&#xE9;cial", TextUtils.htmlEncode("Sp\u00e9cial"));
    }

    public void testVerifyUrl() {
        assertEquals(false, TextUtils.verifyUrl(null));
        assertEquals(false, TextUtils.verifyUrl(""));
        assertEquals(false, TextUtils.verifyUrl("   "));
        assertEquals(false, TextUtils.verifyUrl("no url"));

        assertEquals(true, TextUtils.verifyUrl("http://www.opensymphony.com"));
        assertEquals(true, TextUtils.verifyUrl("https://www.opensymphony.com"));
        assertEquals(true, TextUtils.verifyUrl("https://www.opensymphony.com:443/login"));
        assertEquals(true, TextUtils.verifyUrl("http://localhost:8080/myapp"));
    }

    public void testJoin() {
        assertEquals("abc-def-hij", TextUtils.join("-", new String[]{"abc", "def", "hij"}));
        assertEquals("--", TextUtils.join("-", new String[]{"", "", ""}));

        assertEquals("abc-def-hij", TextUtils.join("-", Arrays.asList("abc", "def", "hij")));
        assertEquals("--", TextUtils.join("-", Arrays.asList("", "", "")));

        assertEquals("abc-def-hij", TextUtils.join("-", Arrays.asList("abc", "def", "hij").iterator()));
        assertEquals("--", TextUtils.join("-", Arrays.asList("", "", "").iterator()));
    }

    public void testNotNull() {
        assertEquals("Hello World", TextUtils.noNull("Hello World"));
        assertEquals("   Hey   ", TextUtils.noNull("   Hey   "));
        assertEquals(" ", TextUtils.noNull(" "));
        assertEquals("", TextUtils.noNull(null));

        assertEquals("Hello World", TextUtils.noNull("Hello World", "Santa"));
        assertEquals("   Hey   ", TextUtils.noNull("   Hey   ", "Santa"));
        assertEquals(" ", TextUtils.noNull(" ", "Santa"));
        assertEquals("Santa", TextUtils.noNull(null, "Santa"));
    }

    public void testStringSet() {
        assertEquals(true, TextUtils.stringSet("Hello World"));
        assertEquals(true, TextUtils.stringSet("   Hey   "));
        assertEquals(true, TextUtils.stringSet(" "));
        assertEquals(false, TextUtils.stringSet(""));
        assertEquals(false, TextUtils.stringSet(null));
    }

}
