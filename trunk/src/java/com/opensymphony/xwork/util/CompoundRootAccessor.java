/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import ognl.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.IntrospectionException;
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

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        CompoundRoot root = (CompoundRoot) target;
        OgnlContext ognlContext = (OgnlContext) context;

        for (Iterator iterator = root.iterator(); iterator.hasNext();) {
            Object o = iterator.next();

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
    }

    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        CompoundRoot root = (CompoundRoot) target;
        OgnlContext ognlContext = (OgnlContext) context;

        if (name instanceof Integer) {
            Integer index = (Integer) name;

            return root.cutStack(index.intValue());
        } else if (name instanceof String) {
            if ("that".equals(name)) {
                if (root.size() > 0) {
                    return root.get(0);
                } else {
                    return null;
                }
            }

            for (Iterator iterator = root.iterator(); iterator.hasNext();) {
                Object o = iterator.next();

                try {
                    if (OgnlRuntime.hasGetProperty(ognlContext, o, name)) {
                        Object value = OgnlRuntime.getProperty(ognlContext, o, name);

                        //Ognl.getValue(OgnlUtil.compile((String) name), context, o);
                        if (value != null) {
                            ognlContext.pushEvaluation(new Evaluation(ognlContext.getCurrentEvaluation().getNode(), o));

                            return value;
                        }
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

            try {
                Object value = OgnlRuntime.callMethod((OgnlContext) context, o, name, name, objects);

                if (value != null) {
                    return value;
                }
            } catch (OgnlException e) {
                // try the next one
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
}
