/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.apt;

import com.opensymphony.xwork.conversion.metadata.ConversionDescription;
import com.sun.mirror.apt.Filer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * <code>ConversionGenerator</code>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
public class ConversionGenerator extends Generator {

    Map<String, List<ConversionDescription>> conversionsByType = null;

    public ConversionGenerator(Map<String, List<ConversionDescription>> conversionsByType) {
        this.conversionsByType = conversionsByType;

    }

    /**
     * Generate the configuration file.
     * Concrete implementations must override this method.
     */
    public void generate(Filer filer) {
        if (conversionsByType == null) {
            return;
        }

        // write property files
        for (Map.Entry<String, List<ConversionDescription>> descriptionMapping : conversionsByType.entrySet()) {

            String key = descriptionMapping.getKey();
            String fileName;
            if (key.equals("")) {
                fileName = "xwork-conversion.properties";
            } else {
                fileName = key.replace('.', File.separatorChar) + "-conversion.properties";

            }
            final PrintWriter writer;
            try {
                writer = filer.createTextFile(Filer.Location.SOURCE_TREE, "", new File(fileName), null);
                writer.println("# Generated automatically. Do not edit!");

                for (ConversionDescription component : descriptionMapping.getValue()) {
                    writer.println(component.asProperty());
                }
                writer.flush();
                writer.close();

            } catch (IOException ioe) {
                throw new RuntimeException("could not generate conversion.properties: " + ioe, ioe);
            }
        }
    }
}
