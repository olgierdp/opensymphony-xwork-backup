/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.apt;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Filer;

/**
 * <code>Generator</code>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
public abstract class Generator {


    protected AnnotationProcessorEnvironment env;


    /**
     * Sets the AnnotationProcessorEnvironment.
     *
     * @param env the AnnotationProcessorEnvironment.
     */
    public void setEnv(AnnotationProcessorEnvironment env) {
        this.env = env;
    }

    /**
     * Generate the configuration file.
     * Concrete implementations must override this method.
     */
    public abstract void generate(Filer filer);

}
