/*
 * Created on Aug 12, 2004 by mgreer
 */
package com.opensymphony.webwork.webFlow.renderers;

import com.opensymphony.webwork.webFlow.XWorkConfigRetriever;
import com.opensymphony.webwork.webFlow.entities.View;
import com.opensymphony.xwork.config.entities.ActionConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

/**
 * Renders flow diagram to an ASCII text file
 */
public class TextRenderer implements Renderer {

    private static final Log LOG = LogFactory.getLog(TextRenderer.class);

    public TextRenderer() {
    }

    public void render() {
        Set namespaces = XWorkConfigRetriever.getNamespaces();
        for (Iterator iter = namespaces.iterator(); iter.hasNext();) {
            String namespace = (String) iter.next();
            String fileName = "default.txt";
            if (namespace.length() > 0)
                fileName = namespace.substring(1) + ".txt";
            LOG.info("fileName=" + fileName);
            File namespaceFile = new File(fileName);
            FileWriter out = null;
            try {
                out = new FileWriter(namespaceFile);
                out.write(namespace + "\n");
                Set actionNames = XWorkConfigRetriever.getActionNames(namespace);
                for (Iterator iterator = actionNames.iterator(); iterator.hasNext();) {
                    String actionName = (String) iterator.next();
                    ActionConfig actionConfig = XWorkConfigRetriever.getActionConfig(namespace,
                            actionName);
                    out.write("\t- " + actionName + "\n");
                    Set resultNames = actionConfig.getResults().keySet();
                    for (Iterator iterator2 = resultNames.iterator(); iterator2.hasNext();) {
                        String resultName = (String) iterator2.next();
                        View view = XWorkConfigRetriever
                                .getView(namespace, actionName, resultName);
                        if (view != null) {
                            out.write("\t\t-> " + resultName + "\t-> " + view + "\t-> "
                                    + view.getTargets() + "\n");
                        }
                    }
                }
                out.close();
            } catch (IOException e) {
                LOG.error("Error writing to " + namespace, e);
            }
        }
    }
}