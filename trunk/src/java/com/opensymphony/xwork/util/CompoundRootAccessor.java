/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import ognl.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.IntrospectionException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 *
 *
 * @author $Author$
 * @version $Revision$
 */
public class CompoundRootAccessor implements PropertyAccessor, MethodAccessor, ClassResolver {
    //~ Static fields/initializers /////////////////////////////////////////////

    private final static Log log = LogFactory.getLog(CompoundRootAccessor.class);
    private static Map invalidMethods = new HashMap();

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        CompoundRoot root = (CompoundRoot) target;
        OgnlContext ognlContext = (OgnlContext) context;

        for (Iterator iterator = root.iterator(); iterator.hasNext();) {
            Object o = iterator.next();

            if (o == null) {
                continue;
            }

            try {
                if (OgnlRuntime.hasSetProperty(ognlContext, o, name)) {
                    OgnlRuntime.setProperty(ognlContext, o, name, value);

                    return;
                }
            } catch (OgnlException e) {
                if (e.getReason() != null) {
                    final String msg = "Caught an Ognl exception while setting property " + name;
                    log.error(msg, e);
                    throw new RuntimeException(msg);
                }
            } catch (IntrospectionException e) {
            }
        }

        final String msg = "No object in the CompoundRoot has a property named '" + name + "'.";
        log.error(msg);
        throw new RuntimeException(msg);
    }

    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        CompoundRoot root = (CompoundRoot) target;
        OgnlContext ognlContext = (OgnlContext) context;

        if (name instanceof Integer) {
            Integer index = (Integer) name;

            return root.cutStack(index.intValue());
        } else if (name instanceof String) {
            if ("top".equals(name)) {
                if (root.size() > 0) {
                    return root.get(0);
                } else {
                    return null;
                }
            }

            for (Iterator iterator = root.iterator(); iterator.hasNext();) {
                Object o = iterator.next();

                if (o == null) {
                    continue;
                }

                try {
                    if ((OgnlRuntime.hasGetProperty(ognlContext, o, name)) || ((o instanceof Map) && ((Map) o).containsKey(name))) {
                        Object value = OgnlRuntime.getProperty(ognlContext, o, name);

                        //Ognl.getValue(OgnlUtil.compile((String) name), context, o);
                        Evaluation currentEvaluation = ognlContext.getCurrentEvaluation();

                        SimpleNode node = currentEvaluation.getNode();
                        currentEvaluation.setSource(o);
                        ognlContext.pushEvaluation(new Evaluation(node, o));

                        return value;
                    }
                } catch (OgnlException e) {
                    if (e.getReason() != null) {
                        final String msg = "Caught an Ognl exception while getting property " + name;
                        log.error(msg, e);
                        throw new RuntimeException(msg);
                    }
                } catch (IntrospectionException e) {
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public Object callMethod(Map context, Object target, String name, Object[] objects) throws MethodFailedException {
        CompoundRoot root = (CompoundRoot) target;

        for (Iterator iterator = root.iterator(); iterator.hasNext();) {
            Object o = iterator.next();

            Class clazz = o.getClass();
            Class[] argTypes = getArgTypes(objects);

            CompoundRootAccessor.MethodCall mc = null;

            if (argTypes != null) {
                mc = new CompoundRootAccessor.MethodCall(clazz, name, argTypes);
            }

            if ((argTypes == null) || !invalidMethods.containsKey(mc)) {
                try {
                    Object value = OgnlRuntime.callMethod((OgnlContext) context, o, name, name, objects);

                    if (value != null) {
                        return value;
                    }
                } catch (OgnlException e) {
                    // try the next one
                    Throwable reason = e.getReason();

                    if ((mc != null) && (reason != null) && (reason.getClass() == NoSuchMethodException.class)) {
                        invalidMethods.put(mc, Boolean.TRUE);
                    } else if (reason != null) {
                        throw new MethodFailedException(o, name, e.getReason());
                    }
                }
            }
        }

        return null;
    }

    public Object callStaticMethod(Map transientVars, Class aClass, String s, Object[] objects) throws MethodFailedException {
        return null;
    }

    public Class classForName(String className, Map context) throws ClassNotFoundException {
        Object root = Ognl.getRoot(context);

        try {
            if (root instanceof CompoundRoot) {
                if (className.startsWith("vs")) {
                    CompoundRoot compoundRoot = (CompoundRoot) root;

                    if (className.equals("vs")) {
                        return compoundRoot.peek().getClass();
                    }

                    int index = Integer.parseInt(className.substring(2));

                    return compoundRoot.get(index - 1).getClass();
                }
            }
        } catch (Exception e) {
            // just try the old fashioned way
        }

        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }

    private Class[] getArgTypes(Object[] args) {
        if (args == null) {
            return new Class[0];
        }

        Class[] classes = new Class[args.length];

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            classes[i] = (arg != null) ? arg.getClass() : Object.class;
        }

        return classes;
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    static class MethodCall {
        Class clazz;
        String name;
        Class[] args;
        int hash;

        public MethodCall(Class clazz, String name, Class[] args) {
            this.clazz = clazz;
            this.name = name;
            this.args = args;
            this.hash = clazz.hashCode() + name.hashCode();

            for (int i = 0; i < args.length; i++) {
                Class arg = args[i];
                hash += arg.hashCode();
            }
        }

        public boolean equals(Object obj) {
            MethodCall mc = (CompoundRootAccessor.MethodCall) obj;

            return (mc.clazz.equals(clazz) && mc.name.equals(name) && Arrays.equals(mc.args, args));
        }

        public int hashCode() {
            return hash;
        }
    }
}
