/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers.dsx;

import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.xml.Path;
import com.opensymphony.xwork.xml.handlers.ObjectCreateHandler;

import java.util.Map;


/**
 * ActionConfigHandler
 * @author Jason Carreira
 * Created Jun 3, 2003 11:34:13 PM
 */
public class ActionConfigHandler extends ObjectCreateHandler {
    //~ Instance fields ////////////////////////////////////////////////////////

    private String name;

    //~ Constructors ///////////////////////////////////////////////////////////

    public ActionConfigHandler(Path path) {
        super(path, ActionConfig.class);
        registerHandlers();
    }

    public ActionConfigHandler(String pathString) {
        super(pathString, ActionConfig.class);
        registerHandlers();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
    * Subclasses should override this method if they want to receive an event at the ending point of the path
    */
    public void endingPath(Path path) {
        super.endingPath(path);

        OgnlValueStack valueStack = getValueStack();
        ActionConfig actionConfig = (ActionConfig) valueStack.pop();
        PackageConfig packageConfig = (PackageConfig) valueStack.peek();
        packageConfig.addActionConfig(name, actionConfig);
    }

    /**
    * Subclasses should override this method if they want to receive an event at the starting point of the path
    */
    public void startingPath(Path path, Map attributeMap) {
        this.name = (String) attributeMap.get("name");
        super.startingPath(path, attributeMap);
    }

    protected void registerHandlers() {
        this.addAttributeMapping("class", "clazz");
        new ParamHandler("").registerWith(this);
        new ResultHandler("result").registerWith(this);
        new InterceptorRefHandler("interceptor-ref").registerWith(this);
    }
}
