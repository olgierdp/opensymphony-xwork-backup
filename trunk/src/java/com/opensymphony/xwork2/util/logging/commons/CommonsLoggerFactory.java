/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util.logging.commons;

import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * Creates commons-logging-backed loggers
 */
public class CommonsLoggerFactory extends LoggerFactory {

    @Override
    protected Logger getLoggerImpl(Class<?> cls) {
        return new CommonsLogger(LogFactory.getLog(cls));
    }
    
    @Override
    protected Logger getLoggerImpl(String name) {
        return new CommonsLogger(LogFactory.getLog(name));
    }

}
