/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers.dsx;

import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.config.entities.ResultConfig;
import com.opensymphony.xwork.config.entities.ResultTypeConfig;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.xml.DefaultDelegatingHandler;
import com.opensymphony.xwork.xml.Path;
import com.opensymphony.xwork.xml.handlers.ElementValueHandler;

import java.lang.reflect.Method;

import java.util.Map;


/**
 * ResultHandler
 * @author Jason Carreira
 * Created Jun 3, 2003 12:13:58 AM
 */
public class ResultHandler extends DefaultDelegatingHandler {
    //~ Instance fields ////////////////////////////////////////////////////////

    private String bodyText = null;
    private String methodName = "addResultConfig";

    //~ Constructors ///////////////////////////////////////////////////////////

    public ResultHandler(Path path) {
        super(path);
        registerHandlers();
    }

    public ResultHandler(String pathString) {
        super(pathString);
        registerHandlers();
    }

    public ResultHandler(Path path, String methodName) {
        super(path);
        this.methodName = methodName;
    }

    public ResultHandler(String pathString, String methodName) {
        super(pathString);
        this.methodName = methodName;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
    * Subclasses should override this method if they want to receive an event at the ending point of the path
    */
    public void endingPath(Path path) {
        super.endingPath(path);

        OgnlValueStack valueStack = getValueStack();
        Object top = valueStack.peek();

        if (top instanceof ResultConfig) {
            ResultConfig config = (ResultConfig) valueStack.pop();
            handleParams(config);

            Object parent = valueStack.peek();

            try {
                Method method = parent.getClass().getMethod(methodName, new Class[] {
                        ResultConfig.class
                    });
                method.invoke(parent, new Object[] {config});
            } catch (Exception e) {
                getRootHandler().addFatal(e);
            }
        }
    }

    /**
    * Subclasses should override this method if they want to receive an event at the starting point of the path
    */
    public void startingPath(Path path, Map attributeMap) {
        OgnlValueStack valueStack = getValueStack();
        String name = (String) attributeMap.get("name");
        String type = (String) attributeMap.get("type");

        if (type == null) {
            type = (String) valueStack.findValue("defaultResultType");
        }

        Map resultTypes = (Map) valueStack.findValue("allResultTypeConfigs");
        ResultTypeConfig resultTypeConfig = (ResultTypeConfig) resultTypes.get(type);

        if (resultTypeConfig == null) {
            addError(new ConfigurationException("There is no result type " + type + " for the reference " + name));

            return;
        }

        ResultConfig resultConfig = new ResultConfig(name, resultTypeConfig.getClazz());
        valueStack.push(resultConfig);
        super.startingPath(path, attributeMap);
    }

    private void handleParams(ResultConfig config) {
        if ((config.getParams().size() <= 0) && (bodyText != null)) {
            String locationValue = bodyText.trim();

            if (!locationValue.equals("")) {
                config.addParam("location", locationValue);
            }
        }
    }

    private void registerHandlers() {
        new ParamHandler("").registerWith(this);
        new ElementValueHandler("") {
                public void handleContent(String content) {
                    bodyText = content;
                }
            }.registerWith(this);
    }
}
