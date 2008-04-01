/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.impl;

import com.opensymphony.xwork2.util.PatternMatcher;

import java.util.Map;
import java.util.Set;

/**
 * Matches namespace strings against a wildcard pattern matcher
 *
 * @Since 2.1
 */
public class NamespaceMatcher extends AbstractMatcher<NamespaceMatch> {
     public NamespaceMatcher(PatternMatcher<?> patternMatcher,
            Set<String> namespaces) {
        super(patternMatcher);
        for (String name : namespaces) {
            if (!patternMatcher.isLiteral(name)) {
                addPattern(name, new NamespaceMatch(name, null), false);
            }
        }
    }

    protected NamespaceMatch convert(String path, NamespaceMatch orig, Map vars) {
        /*Map<String,String> origVars = (Map<String,String>)vars;
        Map<String,String> map = new HashMap<String,String>();
        for (Map.Entry<String,String> entry : origVars.entrySet()) {
            if (entry.getKey().length() == 1) {
                map.put("ns"+entry.getKey(), entry.getValue());
            }
        }
        */
        return new NamespaceMatch(orig.getPattern(), vars);
    }
}
