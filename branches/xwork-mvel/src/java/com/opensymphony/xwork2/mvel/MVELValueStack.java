/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.mvel;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.SecurityMemberAccess;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClearableValueStack;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.MemberAccessValueStack;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.logging.LoggerUtils;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;


import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.mvel2.MVEL;
import org.mvel2.util.ReflectionUtil;

/**
 * Ognl implementation of a value stack that allows for dynamic Ognl expressions to be evaluated against it. When
 * evaluating an expression, the stack will be searched down the stack, from the latest objects pushed in to the
 * earliest, looking for a bean with a getter or setter for the given property or a method of the given name (depending
 * on the expression being evaluated).
 */
public class MVELValueStack implements Serializable, ValueStack, ClearableValueStack, MemberAccessValueStack {

    private static Logger LOG = LoggerFactory.getLogger(MVELValueStack.class);
    protected boolean devMode;

    
    protected CompoundRoot root;
    protected transient MVELContext context;
    protected Class defaultType;
    protected Map<Object, Object> overrides;

    protected transient MVELUtil mvelUtil;
    protected transient SecurityMemberAccess securityMemberAccess;
    protected transient CompoundRootAccessor compoundRootAccessor;

    protected MVELValueStack(XWorkConverter xworkConverter, CompoundRootAccessor accessor, TextProvider prov, boolean allowStaticAccess) {
        setRoot(xworkConverter, accessor, new CompoundRoot(), allowStaticAccess);
        push(prov);
    }


    protected MVELValueStack(ValueStack vs, XWorkConverter xworkConverter, CompoundRootAccessor accessor, boolean allowStaticAccess) {
        setRoot(xworkConverter, accessor, new CompoundRoot(vs.getRoot()), allowStaticAccess);
    }

    protected void setRoot(XWorkConverter xworkConverter,
                           CompoundRootAccessor accessor, CompoundRoot compoundRoot, boolean allowStaticMethodAccess) {
        this.root = compoundRoot;
        this.securityMemberAccess = new SecurityMemberAccess(allowStaticMethodAccess);
        this.context = new MVELContext();
        this.compoundRootAccessor = accessor;
        context.put(VALUE_STACK, this);
    }

    @Inject("devMode")
    public void setDevMode(String mode) {
        devMode = "true".equalsIgnoreCase(mode);
    }

    @Inject
    public void setMVELUtil(MVELUtil mvelUtil) {
        this.mvelUtil = mvelUtil;
    }

    public Map<String, Object> getContext() {
        return context;
    }


    public void setDefaultType(Class defaultType) {
        this.defaultType = defaultType;
    }


    public void setExprOverrides(Map<Object, Object> overrides) {
        if (this.overrides == null) {
            this.overrides = overrides;
        } else {
            this.overrides.putAll(overrides);
        }
    }

    public Map<Object, Object> getExprOverrides() {
        return this.overrides;
    }


    public CompoundRoot getRoot() {
        return root;
    }


    public void setValue(String expr, Object value) {
        setValue(expr, value, devMode);
    }


    public void setValue(String expr, Object value, boolean throwExceptionOnFailure) {
        try {
            context.put(XWorkConverter.CONVERSION_PROPERTY_FULLNAME, expr);
            context.put(REPORT_ERRORS_ON_NO_PROP, (throwExceptionOnFailure) ? Boolean.TRUE : Boolean.FALSE);
            compoundRootAccessor.setProperty(context, root, expr, value);
        } catch (Exception re) { //XW-281
            if (throwExceptionOnFailure) {
                StringBuilder msg = new StringBuilder();
                msg.append("Error setting expression '");
                msg.append(expr);
                msg.append("' with value ");

                if (value instanceof Object[]) {
                    Object[] valueArray = (Object[]) value;
                    msg.append("[");
                    for (int index = 0; index < valueArray.length; index++) {
                        msg.append("'");
                        msg.append(valueArray[index]);
                        msg.append("'");

                        if (index < (valueArray.length + 1))
                            msg.append(", ");
                    }
                    msg.append("]");
                } else {
                    msg.append("'");
                    msg.append(value);
                    msg.append("'");
                }

                throw new XWorkException(msg.toString(), re);
            } else {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Error setting value", re);
                }
            }
        } finally {
            ReflectionContextState.clear(context);
            context.remove(XWorkConverter.CONVERSION_PROPERTY_FULLNAME);
            context.remove(REPORT_ERRORS_ON_NO_PROP);
        }
    }


    public String findString(String expr) {
        return (String) findValue(expr, String.class);
    }


    public Object findValue(String expr) {
        try {
            if (expr == null) {
                return null;
            }

            if ((overrides != null) && overrides.containsKey(expr)) {
                expr = (String) overrides.get(expr);
            }

            if (defaultType != null) {
                return findValue(expr, defaultType);
            }

            Object value = compoundRootAccessor.getProperty(context, root, expr);
            if (value != null) {
                return value;
            } else {
                checkForInvalidProperties(expr);
                return findInContext(expr);
            }
        } catch (Exception e) {
            logLookupFailure(expr, e);

            return findInContext(expr);
        } finally {
            ReflectionContextState.clear(context);
        }
    }


    public Object findValue(String expr, Class asType) {
        try {
            if (expr == null) {
                return null;
            }

            if ((overrides != null) && overrides.containsKey(expr)) {
                expr = (String) overrides.get(expr);
            }

            Object value =  compoundRootAccessor.getProperty(context, root, expr, asType);
            if (value != null) {
                return value;
            } else {
                return findInContext(expr);
            }
        } catch (Exception e) {
            logLookupFailure(expr, e);

            return findInContext(expr);
        } finally {
            ReflectionContextState.clear(context);
        }
    }

    private Object findInContext(String name) {
        return getContext().get(name);
    }


    private void checkForInvalidProperties(String expr) {
        if (expr.contains("(") && expr.contains(")")) {
            if (devMode) {
                LOG.warn("Could not find method [" + expr + "]");
            }
        } else if (findInContext(expr) == null) {
            // find objects with Action in them and inspect matching getters
            Set<String> availableProperties = new LinkedHashSet<String>();
            for (Object o : root) {
                if (o instanceof ActionSupport || o.getClass().getSimpleName().endsWith("Action")) {
                    try {
                        findAvailableProperties(o.getClass(), expr, availableProperties, null);
                    } catch (IntrospectionException ise) {
                        // ignore
                    }
                }
            }
            if (!availableProperties.contains(expr)) {
                if (devMode) {
                    LOG.warn("Could not find property [" + expr + "]");
                }
            }
        }
    }


    private void findAvailableProperties(Class c, String expr, Set<String> availableProperties, String parent) throws IntrospectionException {
        PropertyDescriptor[] descriptors = mvelUtil.loadPropertyDescriptors(c);
        for (PropertyDescriptor pd : descriptors) {
            String name = pd.getDisplayName();
            if (parent != null && expr.contains(".")) {
                name = expr.substring(0, expr.indexOf(".") + 1) + name;
            }
            if (expr.startsWith(name)) {
                availableProperties.add((parent != null) ? parent + "." + name : name);
                if (expr.equals(name)) break; // no need to go any further
                if (expr.contains(".")) {
                    String property = expr.substring(expr.indexOf(".") + 1);
                    // if there is a nested property (indicated by a dot), chop it off so we can look for method name
                    String rawProperty = (property.contains(".")) ? property.substring(0, property.indexOf(".")) : property;
                    String methodToLookFor = ReflectionUtil.getGetter(rawProperty);
                    Method[] methods = pd.getPropertyType().getDeclaredMethods();
                    for (Method method : methods) {
                        if (method.getName().equals(methodToLookFor)) {
                            availableProperties.add(name + "." + rawProperty);
                            Class returnType = method.getReturnType();
                            findAvailableProperties(returnType, property, availableProperties, name);
                        }
                    }

                }
            }
        }
    }


    private void logLookupFailure(String expr, Exception e) {
        String msg = LoggerUtils.format("Caught an exception while evaluating expression '#0' against value stack", expr);
        if (devMode && LOG.isWarnEnabled()) {
            LOG.warn(msg, e);
            LOG.warn("NOTE: Previous warning message was issued due to devMode set to true.");
        } else if (LOG.isDebugEnabled()) {
            LOG.debug(msg, e);
        }
    }


    public Object peek() {
        return root.peek();
    }


    public Object pop() {
        return root.pop();
    }


    public void push(Object o) {
        root.push(o);
    }


    public void set(String key, Object o) {
        //set basically is backed by a Map
        //pushed on the stack with a key
        //being put on the map and the
        //Object being the value

        Map setMap = null;

        //check if this is a Map
        //put on the stack  for setting
        //if so just use the old map (reduces waste)
        Object topObj = peek();
        if (topObj instanceof Map
                && ((Map) topObj).get(MAP_IDENTIFIER_KEY) != null) {

            setMap = (Map) topObj;
        } else {
            setMap = new HashMap();
            //the map identifier key ensures
            //that this map was put there
            //for set purposes and not by a user
            //whose data we don't want to touch
            setMap.put(MAP_IDENTIFIER_KEY, "");
            push(setMap);
        }
        setMap.put(key, o);

    }


    private static final String MAP_IDENTIFIER_KEY = "com.opensymphony.xwork2.util.OgnlValueStack.MAP_IDENTIFIER_KEY";


    public int size() {
        return root.size();
    }

   /* private Object readResolve() {
        // TODO: this should be done better
        ActionContext ac = ActionContext.getContext();
        Container cont = ac.getContainer();
        XWorkConverter xworkConverter = cont.getInstance(XWorkConverter.class);
        CompoundRootAccessor accessor = (CompoundRootAccessor) cont.getInstance(PropertyAccessor.class, CompoundRoot.class.getName());
        TextProvider prov = cont.getInstance(TextProvider.class, "system");
        boolean allow = "true".equals(cont.getInstance(String.class, "allowStaticMethodAccess"));
        MVELlValueStack aStack = new MVELlValueStack(xworkConverter, accessor, prov, allow);
        aStack.setOgnlUtil(cont.getInstance(OgnlUtil.class));
        aStack.setRoot(xworkConverter, accessor, this.root, allow);

        return aStack;
    }
*/

    public void clearContextValues() {
        context.clear();
    }

    public void setAcceptProperties(Set<Pattern> acceptedProperties) {
        securityMemberAccess.setAcceptProperties(acceptedProperties);
    }

    public void setExcludeProperties(Set<Pattern> excludeProperties) {
        securityMemberAccess.setExcludeProperties(excludeProperties);
    }
}
