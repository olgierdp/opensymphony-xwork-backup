/*
 * Created on Aug 13, 2004 by mgreer
 */
package com.opensymphony.webwork.webFlow.entities;

import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.ResultConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Delegates to an XWork ActionConfig to match Command interface
 */
public class XWorkCommand implements Command {

    private static final Log LOG = LogFactory.getLog(XWorkCommand.class);
    private ActionConfig actionConfig = null;
    private String baseString = "";
    private String name = "";
    private String namespace = "";

    public XWorkCommand(ActionConfig actionConfig, String baseString) {
        this.actionConfig = actionConfig;
        this.baseString = baseString;
        this.namespace = this.actionConfig.getPackageName();
    }

    public String getClassName() {
        return this.actionConfig.getClassName();
    }

    public List getInterceptors() {
        return this.actionConfig.getInterceptors();
    }

    public Collection getResults() {
        return this.actionConfig.getResults().keySet();
    }

    public Map getViews() {
        Map views = new HashMap();
        Collection resultNames = getResults();
        for (Iterator iter = resultNames.iterator(); iter.hasNext();) {
            String resultName = (String) iter.next();
            ResultConfig result = (ResultConfig) this.actionConfig.getResults().get(resultName);
            String location = (String) result.getParams().get("location");
            //TODO make sure to follow chaining and redirection to other actions
            if (location != null && !location.matches(".*action.*")) {
                StringBuffer filePath = new StringBuffer(baseString);
                if (!location.startsWith("/"))
                    filePath.append(this.namespace + "/");
                filePath.append(location);
                //views.put( resultName, new XworkView( filePath.toString() ) );
            }
        }
        return views;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String toString() {
        return this.getName() + "->" + this.getResults() + "->" + this.getViews();
    }
}