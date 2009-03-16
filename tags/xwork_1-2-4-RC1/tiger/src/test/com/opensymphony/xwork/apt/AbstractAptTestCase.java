/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.apt;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for tests that deal with the apt tool
 * 
 * @author Nils Hartmann <nils@nilshartmann.net>
 * @author Rainer Hermanns
 */
public class AbstractAptTestCase extends TestCase {

    protected static final Log log = LogFactory.getLog(AbstractAptTestCase.class);

    public final static String SOURCE_DIR="tiger/src/test";
    public final static String DEST_DIR="tiger/build/test_dest";

    public AbstractAptTestCase(String name) {
        super(name);
    }

    /**
     * Runs the apt-processor on all java-Sourcefiles in the
     * defaultSourceDir.
     *
     * <p><b>Attention:</b> The destdir will be removed before
     * running apt
     */
    protected AptRunnerResult runApt() throws Exception {
        return runApt(getDefaultSourceDir());
    }

    /**
     * Runs the apt-processor on all java-files in sourceDir
     *
     * <b>Attention:</b> The destdir will be removed before
     * running apt
     */
    protected AptRunnerResult runApt(File sourceDir) throws Exception {

        final File destDir = getDefaultDestDir();
        assertNotNull(destDir);

        if (getDefaultDestDir().exists()) {
            removeDirectoryTree(getDefaultDestDir().getAbsolutePath());
        }

        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        assertTrue(destDir.exists());
        assertTrue(destDir.isDirectory());

        AptRunner aptRunner = new AptRunner();
        aptRunner.setDestDir(destDir);
        aptRunner.addSourceDir(sourceDir);
        aptRunner.setExecutable(getDefaultExcecutable().getAbsolutePath());
        aptRunner.setFactoryName(getDefaultFactoryName());
        return aptRunner.run();
    }

    protected File getDefaultBaseDir() {
        return new File(System.getProperty("user.dir"));
    }

    protected File getDefaultSourceDir() {
        return new File(getDefaultBaseDir(), SOURCE_DIR);
    }

    /**
     * Returns the destination directory for the apt process
     * <p><b>Warning:</b> This directory will be removed before
     * starting apt !
     * @return
     */
    protected File getDefaultDestDir() {
        return new File(getDefaultBaseDir(), DEST_DIR);
    }

    protected File getDefaultExcecutable() {
        String javaHome  = System.getProperty("java.home");
        if (javaHome.endsWith("jre")) {
            // javaHome points "only" to jre, which doesn't contain
            // the apt tool
            // check if it's a jre installation inside a complete jdk
            javaHome = javaHome.substring(0, javaHome.length()-4);
        }
        File apt = new File(javaHome, File.separator + "bin" + File.separator + "apt");
        assertTrue("apt executable not found. Plase set java.home to a jdk5 installation directory", apt.isFile());
        return apt;
    }

    protected String getDefaultFactoryName() {
        return "com.opensymphony.xwork.apt.XWorkProcessorFactory";
    }

    protected String getPackageDirectoryName() {
        final String className = getClass().getName();
        String packageName = className.substring(0,className.lastIndexOf('.'));
        return packageName.replace('.', '/');
    }

    protected InputStream readFromFolder(File file) throws Exception {
        assertNotNull(file);
        assertTrue(file.isFile());
        FileInputStream fis = new FileInputStream(file);
        return fis;
    }

    protected InputStream readFromSourceFolder(String relativeFileName) throws Exception {
        return readFromFolder(new File(getDefaultSourceDir(), relativeFileName));
    }

    protected InputStream readFromDestFolder(String relativeFileName) throws Exception {
        return readFromFolder(new File(getDefaultDestDir(), relativeFileName));
    }

    protected Document readDocumentFromSourceFolder(String relativeFileName) throws Exception {
        SAXReader reader = new SAXReader();
        Document document = reader.read(readFromSourceFolder(relativeFileName));
        return document;
    }

    protected Document readDocumentFromDestFolder(String relativeFileName) throws Exception {
        SAXReader reader = new SAXReader();
        Document document = reader.read(readFromDestFolder(relativeFileName));
        return document;
    }

    /**
     * Delete a directory, his subdirectory and all files.
     *
     * @param directoryName
     *          the name of the directory to remove.
     * @throws Exception
     *           if an error occurs.
     */
    public static void removeDirectoryTree(String directoryName) throws Exception {

        File directory = new File(directoryName);

        if (!directory.exists()) {
            return;
        }

        if (!directory.isDirectory()) {
            throw new Exception("'" + directory + "' is not a directory");
        }

        String[] fileList = directory.list();
        int numFile = fileList.length;
        boolean fileDeleted = false;
        File f = null;
        for (int i = 0; i < numFile; i++) {
            f = new File(directoryName + File.separator + fileList[i]);
            if (f.isDirectory()) {
                removeDirectoryTree(f.getPath());
            } else {
                fileDeleted = f.delete();
            }
        }
        fileDeleted = directory.delete();
    }
}
