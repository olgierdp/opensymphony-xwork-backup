/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import java.lang.reflect.Method;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.config.entities.ActionConfig;

/**
 * <!-- START SNIPPET: description -->
 *
 * An interceptor that enables scoped model-driven actions.
 *
 * <p/>This interceptor only activates on actions that implement the {@link ScopedModelDriven} interface.  If
 * detected, it will retrieve the model class from the configured scope, then provide it to the Action.
 *  
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>className - The model class name.  Defaults to the class name of the object returned by the getModel() method.</li>
 *            
 * <li>name - The key to use when storing or retrieving the instance in a scope.  Defaults to the model
 *            class name.</li>
 *
 * <li>scope - The scope to store and retrieve the model.  Defaults to 'request' but can also be 'session'.</li>
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Extending the interceptor:</u>
 *
 * <p/>
 *
 * <!-- START SNIPPET: extending -->
 *
 * There are no known extension points for this interceptor.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * 
 * &lt;-- Basic ussage --&gt;
 * lt;interceptor name="scoped-model-driven" class="com.opensymphony.interceptor.ScopedModelDrivenInterceptor" /gt;
 * 
 * &lt;-- Using all available parameters --&gt;
 * lt;interceptor name="gangsterForm" class="com.opensymphony.interceptor.ScopedModelDrivenInterceptor"gt;
 *      lt;param name="scope"gt;sessionlt;/paramgt;
 *      lt;param name="name"gt;gangsterFormlt;/paramgt;
 *      lt;param name="className"gt;com.opensymphony.example.GangsterFormlt;/paramgt;
 *  lt;/interceptorgt;
 * 
 * <!-- END SNIPPET: example -->
 * </pre>
 */
public class ScopedModelDrivenInterceptor extends AbstractInterceptor {

    private static final String GET_MODEL = "getModel";
    private String scope;
    private String name;
    private String className;
    
    protected Object resolveModel(ObjectFactory factory, Map session, String modelClassName, String modelScope, String modelName) throws Exception {
        Object model = null;
        if (modelName == null) {
            modelName = modelClassName;
        }
        if ("session".equals(modelScope)) {
            model = session.get(modelName);
            if (model == null) {
                model = factory.buildBean(modelClassName, null);
                session.put(modelName, model);
            }
        } else {
            model = factory.buildBean(modelClassName, null);
        }
        return model;
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();

        if (action instanceof ScopedModelDriven) {
            ScopedModelDriven modelDriven = (ScopedModelDriven) action;
            if (modelDriven.getModel() == null) {
                ActionContext ctx = ActionContext.getContext();
                ActionConfig config = invocation.getProxy().getConfig();
                
                String cName = className;
                if (cName == null) {
                    try {
                        Method method = action.getClass().getMethod(GET_MODEL, new Class[0]);
                        Class cls = method.getReturnType();
                        cName = cls.getName();
                    } catch (NoSuchMethodException e) {
                        throw new XWorkException("The " + GET_MODEL + "() is not defined in action " + action.getClass() + "", config);
                    }
                }
                Object model = resolveModel(ObjectFactory.getObjectFactory(), ctx.getSession(), cName, scope, name);
                modelDriven.setModel(model);
            }
        }
        return invocation.invoke();
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param scope the scope to set
     */
    public void setScope(String scope) {
        this.scope = scope;
    }    
}
