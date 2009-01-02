package com.opensymphony.xwork2.mvel;

import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.mvel.accessors.DefaultVariableResolverFactory;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.mvel2.CompileException;
import org.mvel2.MVEL;

import java.io.Serializable;
import java.util.Map;


public class CompoundRootAccessor {

    private final static Logger LOG = LoggerFactory.getLogger(CompoundRootAccessor.class);
    static boolean devMode = false;
    protected XWorkConverter converter;
    protected MVELUtil mvelUtil;

    @Inject("devMode")
    public static void setDevMode(String mode) {
        devMode = "true".equals(mode);
    }

    @Inject
    public void setMvelUtil(MVELUtil mvelUtil) {
        this.mvelUtil = mvelUtil;
    }

    @Inject
    public void setConverter(XWorkConverter converter) {
        this.converter = converter;
    }

    public void setProperty(MVELContext context, CompoundRoot root, String property, Object value) {
        //set "top"
        if (root.size() > 0)
            context.put("top", root.get(0));

        for (Object object : root) {
            if (object == null) {
                continue;
            }

            try {
                if (object instanceof Map) {
                    Map<Object, Object> map = (Map) object;
                    map.put(property, value);
                    return;
                }

                //TODO: add cache of expr
                mvelUtil.setProperty(object, property, context, value);
                return;
            } catch (CompileException e) {
                // this is OK if this happens, we'll just keep trying the next one
                if (LOG.isTraceEnabled())
                    LOG.trace("Failed to set property [#0] in class [#1]", e, property, object.getClass().getName());
            } catch (Exception e) {
                throw new XWorkException("unable to set '" + property + "' with value '" + value + "'", e);
            }
        }

        Boolean reportError = (Boolean) context.get(ValueStack.REPORT_ERRORS_ON_NO_PROP);

        final String msg = "No object in the CompoundRoot has a publicly accessible property named '" + property + "' (no setter could be found).";

        if ((reportError != null) && (reportError.booleanValue())) {
            throw new XWorkException(msg);
        } else if (devMode) {
            LOG.warn(msg);
        }
    }

    public Object getProperty(MVELContext context, CompoundRoot target, String name) {
        return getProperty(context, target, name, null);
    }

    public Object getProperty(MVELContext context, CompoundRoot root, String property, Class asType) {
        //TODO: add cache of expr
        Serializable expr = MVEL.compileExpression(property);

        //set "top"
        if (root.size() > 0)
            context.put("top", root.get(0));

        try {
            Object result = mvelUtil.getProperty(root, expr, context);

            return result;
        } catch (Exception e) {
            //ignore and move on
        }

        //try to get the property on the root itself, so [0].x.y
        if ("top".equals(property)) {
            if (root.size() > 0) {
                return root.get(0);
            } else {
                return null;
            }
        }

        for (Object object : root) {
            try {
                Object result =  mvelUtil.getProperty(object, expr, context);

                if (result == null)
                    return null;

                if (asType != null)
                    return converter.convertValue(context, result, asType);


                return result;
            } catch (CompileException e) {
                //property was not found, ignore and move on
            }
        }

        return null;
    }

    public Object callMethod(Map context, Object target, String name, Object[] objects) {
        return null;
    }

    public Object callStaticMethod(Map transientVars, Class aClass, String s, Object[] objects) {
        return null;
    }
}

