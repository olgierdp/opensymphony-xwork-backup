/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator;

import java.util.Map;
import java.util.Collections;
import java.util.LinkedHashMap;

import com.opensymphony.xwork2.util.location.Located;
import com.opensymphony.xwork2.util.location.Location;

/**
 * Holds the necessary information for configuring an instance of a Validator.
 * 
 * 
 * @author James House
 * @author Rainer Hermanns
 * @author tm_jee
 * @author Martin Gilday 
 */
public class ValidatorConfig extends Located {

    private String type;
    private Map params;
    private String defaultMessage;
    private String messageKey;
    private boolean shortCircuit;
    private String[] messageParams;
    
    /**
     * @param validatorType
     */
    protected ValidatorConfig(String validatorType) {
        this.type = validatorType;
        params = new LinkedHashMap();
    }

    protected ValidatorConfig(ValidatorConfig orig) {
        this.type = orig.type;
        this.params = new LinkedHashMap<String,String>(orig.params);
        this.defaultMessage = orig.defaultMessage;
        this.messageKey = orig.messageKey;
        this.shortCircuit = orig.shortCircuit;
    }
    
    /**
     * @return Returns the defaultMessage for the validator.
     */
    public String getDefaultMessage() {
        return defaultMessage;
    }
    
    /**
     * @return Returns the messageKey for the validator.
     */
    public String getMessageKey() {
        return messageKey;
    }
    
    /**
     * @return Returns wether the shortCircuit flag should be set on the 
     * validator.
     */
    public boolean isShortCircuit() {
        return shortCircuit;
    }
    
    /**
     * @return Returns the configured params to set on the validator. 
     */
    public Map getParams() {
        return params;
    }
    
    /**
     * @return Returns the type of validator to configure.
     */
    public String getType() {
        return type;
    }

    /**
     * @return The i18n message parameters/arguments to be used.
     */
    public String[] getMessageParams() {
        return messageParams;
    }

    /**
     * Builds a ValidatorConfig
     */
    public static final class Builder {
        private ValidatorConfig target;

        public Builder(String validatorType) {
            target = new ValidatorConfig(validatorType);
        }

        public Builder(ValidatorConfig config) {
            target = new ValidatorConfig(config);
        }

        public Builder shortCircuit(boolean shortCircuit) {
            target.shortCircuit = shortCircuit;
            return this;
        }

        public Builder defaultMessage(String msg) {
            target.defaultMessage = msg;
            return this;
        }

        public Builder messageParams(String[] msgParams) {
            target.messageParams = msgParams;
            return this;
        }

        public Builder messageKey(String key) {
            if ((key != null) && (key.trim().length() > 0)) {
                target.messageKey = key;
            }
            return this;
        }

        public Builder addParam(String name, String value) {
            if (value != null && name != null) {
                target.params.put(name, value);
            }
            return this;
        }

        public Builder addParams(Map<String,String> params) {
            target.params.putAll(params);
            return this;
        }

        public Builder location(Location loc) {
            target.location = loc;
            return this;
        }

        public ValidatorConfig build() {
            target.params = Collections.unmodifiableMap(target.params);
            ValidatorConfig result = target;
            target = new ValidatorConfig(target);
            return result;
        }

        public Builder removeParam(String key) {
            target.params.remove(key);
            return this;
        }
    }
}
