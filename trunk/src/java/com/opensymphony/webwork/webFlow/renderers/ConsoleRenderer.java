/*
 * Created on Aug 12, 2004 by mgreer
 */
package com.opensymphony.webwork.webFlow.renderers;

import com.opensymphony.webwork.webFlow.XWorkConfigRetriever;
import com.opensymphony.webwork.webFlow.entities.View;
import com.opensymphony.xwork.config.entities.ActionConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.Set;

/**
 * Renders flow diagram to the console at info level
 */
public class ConsoleRenderer implements Renderer {

    private static final Log LOG = LogFactory.getLog(ConsoleRenderer.class);

    /**
     * Default constructor
     */
    public ConsoleRenderer() {
    }

    public void render() {
        Set namespaces = XWorkConfigRetriever.getNamespaces();
        for (Iterator iter = namespaces.iterator(); iter.hasNext();) {
            String namespace = (String) iter.next();
            if (LOG.isDebugEnabled())
                LOG.debug(namespace);
            Set actionNames = XWorkConfigRetriever.getActionNames(namespace);
            for (Iterator iterator = actionNames.iterator(); iterator.hasNext();) {
                String actionName = (String) iterator.next();
                ActionConfig actionConfig = XWorkConfigRetriever.getActionConfig(namespace,
                        actionName);
                if (LOG.isDebugEnabled())
                    LOG.debug("\t- " + actionName);
                Set resultNames = actionConfig.getResults().keySet();
                for (Iterator iterator2 = resultNames.iterator(); iterator2.hasNext();) {
                    String resultName = (String) iterator2.next();
                    View view = XWorkConfigRetriever.getView(namespace, actionName, resultName);
                    if (view != null) {
                        if (LOG.isDebugEnabled())
                            LOG.debug("\t\t-> " + resultName + "\t-> " + view + "\t-> " + view.getTargets());
                    }
                }
            }
        }
    }

}