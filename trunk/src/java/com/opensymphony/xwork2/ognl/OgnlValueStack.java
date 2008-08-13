/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.accessor.CompoundRootAccessor;
import com.opensymphony.xwork2.util.ClearableValueStack;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.MemberAccessValueStack;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.logging.LoggerUtils;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.PropertyAccessor;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Ognl implementation of a value stack that allows for dynamic Ognl expressions to be evaluated against it. When
 * evaluating an expression, the stack will be searched down the stack, from the latest objects pushed in to the
 * earliest, looking for a bean with a getter or setter for the given property or a method of the given name (depending
 * on the expression being evaluated).
 *
 * @author Patrick Lightbody
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class OgnlValueStack implements Serializable, ValueStack, ClearableValueStack, MemberAccessValueStack {

    private static final long serialVersionUID = 370737852934925530L;

    private static Logger LOG = LoggerFactory.getLogger(OgnlValueStack.class);
    private boolean devMode;

    public static void link(Map<String, Object> context, Class clazz, String name) {
        context.put("__link", new Object[]{clazz, name});
    }


    CompoundRoot root;
    transient Map<String, Object> context;
    Class defaultType;
    Map<Object, Object> overrides;
    transient OgnlUtil ognlUtil;

    transient SecurityMemberAccess securityMemberAccess;

    protected OgnlValueStack(XWorkConverter xworkConverter, CompoundRootAccessor accessor, TextProvider prov, boolean allowStaticAccess) {
        setRoot(xworkConverter, accessor, new CompoundRoot(), allowStaticAccess);
        push(prov);
    }


    protected OgnlValueStack(ValueStack vs, XWorkConverter xworkConverter, CompoundRootAccessor accessor, boolean allowStaticAccess) {
        setRoot(xworkConverter, accessor, new CompoundRoot(vs.getRoot()), allowStaticAccess);
    }

    @Inject
    public void setOgnlUtil(OgnlUtil ognlUtil) {
        this.ognlUtil = ognlUtil;
    }

    protected void setRoot(XWorkConverter xworkConverter,
                           CompoundRootAccessor accessor, CompoundRoot compoundRoot, boolean allowStaticMethodAccess) {
        this.root = compoundRoot;
        this.securityMemberAccess =  new SecurityMemberAccess(allowStaticMethodAccess);
        this.context = Ognl.createDefaultContext(this.root, accessor, new OgnlTypeConverterWrapper(xworkConverter),
               securityMemberAccess);
        context.put(VALUE_STACK, this);
        Ognl.setClassResolver(context, accessor);
        ((OgnlContext) context).setTraceEvaluations(false);
        ((OgnlContext) context).setKeepLastEvaluation(false);
    }

    @Inject("devMode")
    public void setDevMode(String mode) {
        devMode = "true".equalsIgnoreCase(mode);
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#getContext()
     */
    public Map<String, Object> getContext() {
        return context;
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#setDefaultType(java.lang.Class)
     */
    public void setDefaultType(Class defaultType) {
        this.defaultType = defaultType;
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#setExprOverrides(java.util.Map)
     */
    public void setExprOverrides(Map<Object, Object> overrides) {
        if (this.overrides == null) {
            this.overrides = overrides;
        } else {
            this.overrides.putAll(overrides);
        }
    }

    /* (non-Javadoc)
    * @see com.opensymphony.xwork2.util.ValueStack#getExprOverrides()
    */
    public Map<Object, Object> getExprOverrides() {
        return this.overrides;
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#getRoot()
     */
    public CompoundRoot getRoot() {
        return root;
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#setValue(java.lang.String, java.lang.Object)
     */
    public void setValue(String expr, Object value) {
        setValue(expr, value, devMode);
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#setValue(java.lang.String, java.lang.Object, boolean)
     */
    public void setValue(String expr, Object value, boolean throwExceptionOnFailure) {
        Map<String, Object> context = getContext();

        try {
            context.put(XWorkConverter.CONVERSION_PROPERTY_FULLNAME, expr);
            context.put(REPORT_ERRORS_ON_NO_PROP, (throwExceptionOnFailure) ? Boolean.TRUE : Boolean.FALSE);
            ognlUtil.setValue(expr, context, root, value);
        } catch (OgnlException e) {
            if (throwExceptionOnFailure) {
                e.printStackTrace(System.out);
                System.out.println("expr: " + expr + " val: " + value + " context: " + context + " root:" + root + " value: " + value);
                String msg = "Error setting expression '" + expr + "' with value '" + value + "'";
                throw new XWorkException(msg, e);
            } else {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Error setting value", e);
                }
            }
        } catch (RuntimeException re) { //XW-281
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

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#findString(java.lang.String)
     */
    public String findString(String expr) {
        return (String) findValue(expr, String.class);
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#findValue(java.lang.String)
     */
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

            Object value = ognlUtil.getValue(expr, context, root);
            if (value != null) {
                return value;
            } else {
                checkForInvalidProperties(expr);
                return findInContext(expr);
            }
        } catch (OgnlException e) {
            checkForInvalidProperties(expr);
            return findInContext(expr);
        } catch (Exception e) {
            logLookupFailure(expr, e);

            return findInContext(expr);
        } finally {
            ReflectionContextState.clear(context);
        }
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#findValue(java.lang.String, java.lang.Class)
     */
    public Object findValue(String expr, Class asType) {
        try {
            if (expr == null) {
                return null;
            }

            if ((overrides != null) && overrides.containsKey(expr)) {
                expr = (String) overrides.get(expr);
            }

            Object value = ognlUtil.getValue(expr, context, root, asType);
            if (value != null) {
                return value;
            } else {
                return findInContext(expr);
            }
        } catch (OgnlException e) {
            return findInContext(expr);
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


    /**
     * This method looks for matching methods/properties in an action to warn the user if
     * they specified a property that doesn't exist.
     *
     * @param expr the property expression
     */
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

    /**
     * Look for available properties on an existing class.
     *
     * @param c                   the class to search on
     * @param expr                the property expression
     * @param availableProperties a set of properties found
     * @param parent              a parent property
     * @throws IntrospectionException when Ognl can't get property descriptors
     */
    private void findAvailableProperties(Class c, String expr, Set<String> availableProperties, String parent) throws IntrospectionException {
        PropertyDescriptor[] descriptors = ognlUtil.getPropertyDescriptors(c);
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
                    String methodToLookFor = "get" + rawProperty.substring(0, 1).toUpperCase() + rawProperty.substring(1);
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

    /**
     * Log a failed lookup, being more verbose when devMode=true.
     *
     * @param expr The failed expression
     * @param e    The thrown exception.
     */
    private void logLookupFailure(String expr, Exception e) {
        String msg = LoggerUtils.format("Caught an exception while evaluating expression '#0' against value stack", expr);
        if (devMode && LOG.isWarnEnabled()) {
            LOG.warn(msg, e);
            LOG.warn("NOTE: Previous warning message was issued due to devMode set to true.");
        } else if (LOG.isDebugEnabled()) {
            LOG.debug(msg, e);
        }
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#peek()
     */
    public Object peek() {
        return root.peek();
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#pop()
     */
    public Object pop() {
        return root.pop();
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#push(java.lang.Object)
     */
    public void push(Object o) {
        root.push(o);
    }

    /* (non-Javadoc)
    * @see com.opensymphony.xwork2.util.ValueStack#set(java.lang.String, java.lang.Object)
    */
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

    /* (non-Javadoc)
    * @see com.opensymphony.xwork2.util.ValueStack#size()
    */
    public int size() {
        return root.size();
    }

    private Object readResolve() {
        // TODO: this should be done better
        ActionContext ac = ActionContext.getContext();
        Container cont = ac.getContainer();
        XWorkConverter xworkConverter = cont.getInstance(XWorkConverter.class);
        CompoundRootAccessor accessor = (CompoundRootAccessor) cont.getInstance(PropertyAccessor.class, CompoundRoot.class.getName());
        TextProvider prov = cont.getInstance(TextProvider.class, "system");
        boolean allow = "true".equals(cont.getInstance(String.class, "allowStaticMethodAccess"));
        OgnlValueStack aStack = new OgnlValueStack(xworkConverter, accessor, prov, allow);
        aStack.setOgnlUtil(cont.getInstance(OgnlUtil.class));
        aStack.setRoot(xworkConverter, accessor, this.root, allow);

        return aStack;
    }


    public void clearContextValues() {
        //this is an OGNL ValueStack so the context will be an OgnlContext
        //it would be better to make context of type OgnlContext
       ((OgnlContext)context).getValues().clear();
    }

    public void setAcceptedProperties(Set<Pattern> acceptedProperties) {
        securityMemberAccess.setAcceptedProperties(acceptedProperties);
    }

    public void setExcludeProperties(Set<Pattern> excludeProperties) {
       securityMemberAccess.setExcludeProperties(excludeProperties);
    }
}
