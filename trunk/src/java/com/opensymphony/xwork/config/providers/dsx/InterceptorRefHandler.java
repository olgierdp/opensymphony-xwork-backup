/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers.dsx;

import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.config.entities.InterceptorListHolder;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.config.entities.Parameterizable;
import com.opensymphony.xwork.config.providers.InterceptorBuilder;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.xml.DefaultDelegatingHandler;
import com.opensymphony.xwork.xml.Path;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * InterceptorRefHandler
 * @author Jason Carreira
 * Created May 31, 2003 11:28:51 PM
 */
public class InterceptorRefHandler extends DefaultDelegatingHandler implements Parameterizable {
    //~ Instance fields ////////////////////////////////////////////////////////

    Map params = new HashMap();
    String name;

    //~ Constructors ///////////////////////////////////////////////////////////

    public InterceptorRefHandler(Path path) {
        super(path);
        registerHandlers();
    }

    public InterceptorRefHandler(String pathString) {
        super(pathString);
        registerHandlers();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setParams(Map params) {
        this.params = params;
    }

    public Map getParams() {
        return params;
    }

    public void addParam(String name, Object value) {
        params.put(name, value);
    }

    /**
    * Subclasses should override this method if they want to receive an event at the ending point of the path
    */
    public void endingPath(Path path) {
        super.endingPath(path);

        final OgnlValueStack valueStack = getValueStack();

        //pop this
        valueStack.pop();

        InterceptorListHolder interceptorListHolder = (InterceptorListHolder) valueStack.pop();
        PackageConfig config = (PackageConfig) valueStack.peek();
        valueStack.push(interceptorListHolder);

        try {
            List interceptors = InterceptorBuilder.constructInterceptorReference(config, name, params);
            interceptorListHolder.addInterceptors(interceptors);
        } catch (ConfigurationException e) {
            addError(e);
        }
    }

    /**
    * Subclasses should override this method if they want to receive an event at the starting point of the path
    */
    public void startingPath(Path path, Map attributeMap) {
        // add this to the stack to get the params
        getValueStack().push(this);
        super.startingPath(path, attributeMap);
        name = (String) attributeMap.get("name");
    }

    private void registerHandlers() {
        new ParamHandler("").registerWith(this);
    }
}
