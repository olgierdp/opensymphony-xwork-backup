/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.impl;

import java.util.Map;

/**
 * Represents a match from a namespace pattern matching.
 *
 * @Since 2.1
 */
public class NamespaceMatch {
    private String pattern;
    private Map<String,String> variables;

    public NamespaceMatch(String pattern, Map<String, String> variables) {
        this.pattern = pattern;
        this.variables = variables;
    }

    /**
     * @return The pattern that was matched
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * @return The variables containing the matched values
     */
    public Map<String, String> getVariables() {
        return variables;
    }
}
