/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.XworkException;
import ognl.*;
import ognl.enhance.ExpressionCompiler;
import ognl.enhance.UnsupportedCompilationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.*;


/**
 * An OGNL property accessor that is able to call methods on objects in the {@link CompoundRoot}.
 *
 * @author Rainer Hermanns
 * @author tmjee
 * @version $Date$ $Id$
 */
public class CompoundRootAccessor implements PropertyAccessor, MethodAccessor, ClassResolver {

    private final static Log log = LogFactory.getLog(CompoundRootAccessor.class);
    private static Map invalidMethods = new HashMap();

    public Class getPropertyClass(OgnlContext ognlcontext, Object target, Object name) {
        //System.out.println("getPropertyClass "+target+"\t"+name);
        CompoundRoot root = (CompoundRoot) target;

        if (name instanceof Integer) {
           // this means user probably 'cut' the stack
            return CompoundRoot.class;
        }
        else if (name instanceof String) {
            String beanName = ((String)name).replaceAll("\"", "");

            try {
                Integer.valueOf(beanName);
                return CompoundRoot.class;
            }
            catch(NumberFormatException e) { /* ignore, its not a number */ }

            if ("top".equals(beanName)) {
                if (root.size() > 0 && (root.get(0) != null)) {
                    return root.get(0).getClass();
                }
            }
            else {
                // let's go through the root
                for (Iterator i = root.iterator(); i.hasNext(); ) {
                    try {
                        Object tmp = i.next();
                        if (tmp != null) {
                            PropertyDescriptor pd = OgnlRuntime.getPropertyDescriptor(tmp.getClass(), beanName);
                            if (pd != null) {
                                if (Map.class.isAssignableFrom(pd.getClass())) {
                                    return Map.class;
                                }
                                return pd.getPropertyType();
                            }
                        }
                    }
                    catch(IntrospectionException e) {
                        // we'll just try the next one
                    }
                    catch(OgnlException e) {
                        // we'll just try the next one
                    }
                }
            }
        }
        return Object.class;  // always fall back to Object.class
    }

    public String getSourceAccessor(OgnlContext ognlcontext, Object target, Object name) {
        //System.out.println("getSourceAccessor "+target+"\t"+name);
        CompoundRoot root = (CompoundRoot) target;

        if (name instanceof Integer) {
            return ".cutStack("+name+")";
        }
        else if (name instanceof String) {
            String beanName = ((String)name).replaceAll("\"", "");

            try {
                Integer.valueOf(beanName);
                return ".cutStack("+name+")";
            }
            catch(NumberFormatException e) {
                // ignore, its not a number
            }

            if ("top".equals(beanName)) {
                return ".get(0)";
            }


            int a = 0;
            for (Iterator i = root.iterator(); i.hasNext(); a++) {
                try {
                Object tmp = i.next();
                    if (tmp != null) {
                        PropertyDescriptor pd = OgnlRuntime.getPropertyDescriptor(tmp.getClass(), beanName);
                        if (pd != null) {
                            if (Map.class.isAssignableFrom(tmp.getClass())) {

                                ExpressionCompiler.addCastString(ognlcontext, "(("+Map.class.getName()+")");

                                ognlcontext.setCurrentType(Map.class);
                                ognlcontext.setCurrentAccessor(CompoundRoot.class);

                                return ".get("+a+")).get(\""+beanName+"\")";
                            }

                            Class type = OgnlRuntime.getCompiler().getSuperOrInterfaceClass(pd.getReadMethod(), tmp.getClass());


                            if (pd.getPropertyType().isPrimitive()) {
                                Class wrapClass = OgnlRuntime.getPrimitiveWrapperClass(pd.getPropertyType());

                                ExpressionCompiler.addCastString(ognlcontext, "(ognl.OgnlOps.convertValue(((" + type.getName() + ")");
                                                    
                                ognlcontext.setCurrentType(type);
                                ognlcontext.setCurrentAccessor(CompoundRoot.class);

                                return ".get("+a+"))."+pd.getReadMethod().getName()+"(),"+wrapClass.getName()+".class, false))";
                            }

                            ExpressionCompiler.addCastString(ognlcontext, "((" + type.getName() + ")");

                            ognlcontext.setCurrentType(type);
                            ognlcontext.setCurrentAccessor(CompoundRoot.class);

                            return ".get("+a+"))."+pd.getReadMethod().getName()+"()";
                        }
                    }
                }
                catch(IntrospectionException e) {
                    // ignore, continue with next element
                }
                catch(OgnlException e) {
                    // ignore, continue with next element
                }
            }
        }
        return "";
    }

    public String getSourceSetter(OgnlContext ognlcontext, Object target, Object name) {
        //System.out.println("getSourceSetter "+target+"\t"+name);
        CompoundRoot root = (CompoundRoot) target;
        if (name instanceof String) {
            String beanName = ((String)name).replaceAll("\"", "");

            int a = 0;
            for (Iterator i = root.iterator(); i.hasNext(); a++) {
                try {
                Object tmp = i.next();
                    if (tmp != null) {
                        PropertyDescriptor pd = OgnlRuntime.getPropertyDescriptor(tmp.getClass(), beanName);
                        if (pd != null) {
                            if (Map.class.isAssignableFrom(tmp.getClass())) {

                                ExpressionCompiler.addCastString(ognlcontext, "(("+Map.class.getName()+")");

                                ognlcontext.setCurrentType(Map.class);
                                ognlcontext.setCurrentAccessor(CompoundRoot.class);

                                return ".get("+a+")).get(\""+beanName+"\")";
                            }

                            if (pd.getWriteMethod().getParameterTypes().length > 1) {
                                 throw new UnsupportedCompilationException("Object property accessors can only support single parameter setters.");
                            }

                            Class param = pd.getWriteMethod().getParameterTypes()[0];
                            String conversion = null;

                            if (param.isPrimitive()) {
                                Class wrapClass = OgnlRuntime.getPrimitiveWrapperClass(param);
                                /*System.out.println("***"+"((" + wrapClass.getName() + ")ognl.OgnlOps#convertValue($3,"
                                                                            + wrapClass.getName()+ ".class, true))." + OgnlTools.getPrimitiveValueGetter(wrapClass));*/
                                conversion = OgnlRuntime.getCompiler().createLocalReference(ognlcontext,
                                                                            "((" + wrapClass.getName() + ")ognl.OgnlOps#convertValue($3,"
                                                                            + wrapClass.getName()+ ".class, true))." + OgnlTools.getPrimitiveValueGetter(wrapClass),
                                                                            param);

                            } else if (param.isArray()) {
                                conversion = OgnlRuntime.getCompiler().createLocalReference(ognlcontext,
                                                                            "(" + ExpressionCompiler.getCastString(param) + ")ognl.OgnlOps#toArray($3,"
                                                                            + param.getComponentType().getName() + ".class)",
                                                                            param);

                            } else {
                                /*System.out.println("(" + param.getName()+ ")ognl.OgnlOps#convertValue($3,"
                                                                            + param.getName()
                                                                            + ".class)");*/
                                conversion = OgnlRuntime.getCompiler().createLocalReference(ognlcontext,
                                                                            "(" + param.getName()+ ")ognl.OgnlOps#convertValue($3,"
                                                                            + param.getName()
                                                                            + ".class)",
                                                                            param);
                            }

                            Class type = OgnlRuntime.getCompiler().getSuperOrInterfaceClass(pd.getWriteMethod(), tmp.getClass());

                            ExpressionCompiler.addCastString(ognlcontext, "((" + type.getName() + ")");

                            ognlcontext.setCurrentType(type);
                            ognlcontext.setCurrentAccessor(CompoundRoot.class);

                            return ".get("+a+"))."+pd.getWriteMethod().getName()+"("+conversion+")";
                        }
                    }
                }
                catch(IntrospectionException e) {
                    // ignore, continue with next element
                }
                catch(OgnlException e) {
                    // ignore, continue with next element
                }
            }
        }
        return "";
    }

    /**
     * 
     * @param context
     * @param target
     * @param name
     * @param value
     * @throws OgnlException
     */
    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        //System.out.println("setProperty "+name+"\t"+value);
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
                } else if (o instanceof Map) {
                    Map map = (Map) o;
                    map.put(name, value);
                    return;
                }
            } catch (IntrospectionException e) {
                // this is OK if this happens, we'll just keep trying the next
            }
        }

        Boolean reportError = (Boolean) context.get(OgnlValueStack.REPORT_ERRORS_ON_NO_PROP);

        final String msg = "No object in the CompoundRoot has a publicly accessible property named '" + name + "' (no setter could be found).";

        if ((reportError != null) && (reportError.booleanValue())) {
            log.error(msg);
            throw new XworkException(msg);
        } else {
            log.debug(msg);
        }
    }

    /**
     * 
     * @param context
     * @param target
     * @param name
     * @return
     * @throws OgnlException
     */
    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        //System.out.println("getProperty "+name);
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
                    if ((OgnlRuntime.hasGetProperty(ognlContext, o, name)) || ((o instanceof Map) && ((Map) o).containsKey(name)))
                    {
                        return OgnlRuntime.getProperty(ognlContext, o, name);
                    }
                } catch (OgnlException e) {
                    if (e.getReason() != null) {
                        final String msg = "Caught an Ognl exception while getting property " + name;
                        log.error(msg, e);
                        throw new XworkException(msg, e);
                    }
                } catch (IntrospectionException e) {
                    // this is OK if this happens, we'll just keep trying the next
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public Object callMethod(Map context, Object target, String name, Object[] objects) throws MethodFailedException {
        CompoundRoot root = (CompoundRoot) target;

        if ("describe".equals(name)) {
            Object v;
            if (objects != null && objects.length == 1) {
                v = objects[0];
            } else {
                v = root.get(0);
            }


            if (v instanceof Collection || v instanceof Map || v.getClass().isArray()) {
                return v.toString();
            }

            try {
                Map descriptors = OgnlRuntime.getPropertyDescriptors(v.getClass());

                int maxSize = 0;
                for (Iterator iterator = descriptors.keySet().iterator(); iterator.hasNext();) {
                    String pdName = (String) iterator.next();
                    if (pdName.length() > maxSize) {
                        maxSize = pdName.length();
                    }
                }

                SortedSet set = new TreeSet();
                StringBuffer sb = new StringBuffer();
                for (Iterator iterator = descriptors.values().iterator(); iterator.hasNext();) {
                    PropertyDescriptor pd = (PropertyDescriptor) iterator.next();

                    sb.append(pd.getName()).append(": ");
                    int padding = maxSize - pd.getName().length();
                    for (int i = 0; i < padding; i++) {
                        sb.append(" ");
                    }
                    sb.append(pd.getPropertyType().getName());
                    set.add(sb.toString());

                    sb = new StringBuffer();
                }

                sb = new StringBuffer();
                for (Iterator iterator = set.iterator(); iterator.hasNext();) {
                    String s = (String) iterator.next();
                    sb.append(s).append("\n");
                }

                return sb.toString();
            } catch (IntrospectionException e) {
                e.printStackTrace();
            } catch (OgnlException e) {
                e.printStackTrace();
            }

            return null;
        }

        for (Iterator iterator = root.iterator(); iterator.hasNext();) {
            Object o = iterator.next();

            if (o == null) {
                continue;
            }

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
