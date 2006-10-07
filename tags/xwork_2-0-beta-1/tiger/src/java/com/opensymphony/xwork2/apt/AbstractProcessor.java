/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.apt;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Filer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <code>AbstractProcessor</code>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
public abstract class AbstractProcessor implements AnnotationProcessor {

    protected Log log = null;

    protected AnnotationProcessorEnvironment env;


    public AbstractProcessor() {
        log = LogFactory.getLog(this.getClass());
    }


    /**
     * Sets the AnnotationProcessorEnvironment.
     *
     * @param env the AnnotationProcessorEnvironment.
     */
    public void setEnv(AnnotationProcessorEnvironment env) {
        this.env = env;
    }

    /**
     * Initializes all annotations types required for processing.
     */
    public void init() {
    }

    /**
     * Process all program elements supported by this annotations processor.
     */
    public abstract void process();


    /**
     * Returns a PrintWriter that writes to META-INF directory within the Filer.Location.SOURCE_TREE.
     *
     * @param filename The filename of the file to be written.
     * @return PrintWriter that writes to META-INF directory within the Filer.Location.SOURCE_TREE.
     * @throws IOException
     */
    protected PrintWriter getSourceMetaInfWriter(String filename) throws IOException {
        return env.getFiler().createTextFile(Filer.Location.SOURCE_TREE, "", new File("META-INF" + File.separator + filename), "UTF-8");
    }

    /**
     * Returns a PrintWriter that writes to META-INF directory within the Filer.Location.CLASS_TREE.
     *
     * @param filename The filename of the file to be written.
     * @return PrintWriter that writes to META-INF directory within the Filer.Location.CLASS_TREE.
     * @throws IOException
     */
    protected PrintWriter getClassesMetaInfWriter(String filename) throws IOException {
        return env.getFiler().createTextFile(Filer.Location.CLASS_TREE, "", new File("META-INF" + File.separator + filename), "UTF-8");
    }

    /**
     * Returns a PrintWriter that writes to WEB-INF directory within the Filer.Location.SOURCE_TREE.
     *
     * @param filename The filename of the file to be written.
     * @return PrintWriter that writes to WEB-INF directory within the Filer.Location.SOURCE_TREE.
     * @throws IOException
     */
    protected PrintWriter getWebInfWriter(String filename) throws IOException {
        return env.getFiler().createTextFile(Filer.Location.SOURCE_TREE, "", new File("WEB-INF" + File.separator + filename), "UTF-8");
    }
}
