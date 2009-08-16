/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util.classloader;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Reads a class from disk
 *  class taken from Apache JCI
 */
public final class FileResourceStore implements ResourceStore {
    private static final Logger LOG = LoggerFactory.getLogger(FileResourceStore.class);
    private final File root;

    public FileResourceStore(final File pFile) {
        root = pFile;
    }

    public byte[] read(final String pResourceName) {
        FileInputStream fis = null;
        try {
            File file = getFile(pResourceName);
            byte[] data = new byte[(int) file.length()];
            fis = new FileInputStream(file);
            fis.read(data);

            return data;
        } catch (Exception e) {
            if (LOG.isDebugEnabled())
                LOG.debug("Unable to read file [#0]", e, pResourceName);
            return null;
        } finally {
            closeQuietly(fis);
        }
    }

    public void write(final String pResourceName, final byte[] pData) {

    }

    private void closeQuietly(InputStream is) {
        try {
            if (is != null)
                is.close();
        } catch (IOException e) {
            if (LOG.isErrorEnabled())
                LOG.error("Unable to close file input stream", e);
        }
    }

    private File getFile(final String pResourceName) {
        final String fileName = pResourceName.replace('/', File.separatorChar);
        return new File(root, fileName);
    }

    public String toString() {
        return this.getClass().getName() + root.toString();
    }
}
