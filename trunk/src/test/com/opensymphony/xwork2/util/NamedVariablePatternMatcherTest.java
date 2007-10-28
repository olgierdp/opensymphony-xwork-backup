/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import junit.framework.TestCase;

import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import com.opensymphony.xwork2.util.NamedVariablePatternMatcher.CompiledPattern;

public class NamedVariablePatternMatcherTest extends TestCase {

    public void testCompile() {
        NamedVariablePatternMatcher matcher = new NamedVariablePatternMatcher();

        assertNull(matcher.compilePattern(null));
        assertNull(matcher.compilePattern(""));

        CompiledPattern pattern = matcher.compilePattern("foo");
        assertEquals("foo", pattern.getPattern().pattern());

        pattern = matcher.compilePattern("foo{jim}");
        assertEquals("foo([^/]+)", pattern.getPattern().pattern());
        assertEquals("jim", pattern.getVariableNames().get(0));

        pattern = matcher.compilePattern("foo{jim}/{bob}");
        assertEquals("foo([^/]+)/([^/]+)", pattern.getPattern().pattern());
        assertEquals("jim", pattern.getVariableNames().get(0));
        assertEquals("bob", pattern.getVariableNames().get(1));
        assertTrue(pattern.getPattern().matcher("foostar/jie").matches());
        assertFalse(pattern.getPattern().matcher("foo/star/jie").matches());
    }

    public void testMatch() {
        NamedVariablePatternMatcher matcher = new NamedVariablePatternMatcher();

        Map<String,String> vars = new HashMap<String,String>();
        CompiledPattern pattern = new CompiledPattern(Pattern.compile("foo([^/]+)"), Arrays.asList("bar"));

        assertTrue(matcher.match(vars, "foobaz", pattern));
        assertEquals("baz", vars.get("bar"));
    }

    public void testIsLiteral() {
        NamedVariablePatternMatcher matcher = new NamedVariablePatternMatcher();

        assertTrue(matcher.isLiteral("bob"));
        assertFalse(matcher.isLiteral("bob{jim}"));
    }
}
