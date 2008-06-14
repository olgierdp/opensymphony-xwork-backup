package com.opensymphony.xwork2.interceptor.annotations;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.interceptor.ParameterFilterInterceptor;
import com.opensymphony.xwork2.interceptor.ParametersInterceptor;
import com.opensymphony.xwork2.util.AnnotationUtils;

/**
 * Annotation based version of {@link ParameterFilterInterceptor}.
 * <p/>
 * This {@link Interceptor} must be placed in the stack before the {@link ParametersInterceptor}
 * When a parameter matches a field that is marked {@link Blocked} then it is removed from
 * the parameter map.
 * <p/>
 * If an {@link Action} class is marked with {@link BlockByDefault} then all parameters are
 * removed unless a field on the Action exists and is marked with {@link Allowed}
 *
 * @author martin.gilday
 */
public class AnnotationParameterFilterIntereptor extends AbstractInterceptor {

    /* (non-Javadoc)
      * @see com.opensymphony.xwork2.interceptor.AbstractInterceptor#intercept(com.opensymphony.xwork2.ActionInvocation)
      */
    public String intercept(ActionInvocation invocation) throws Exception {

        final Object action = invocation.getAction();
        Map parameters = invocation.getInvocationContext().getParameters();

        boolean blockByDefault = action.getClass().isAnnotationPresent(BlockByDefault.class);
        List<Field> annotatedFields = new ArrayList<Field>();
        HashSet paramsToRemove = new HashSet();

        if (blockByDefault) {
            AnnotationUtils.addAllFields(Allowed.class, action.getClass(), annotatedFields);

            for (Iterator i = parameters.keySet().iterator(); i.hasNext();) {
                boolean allowed = false;
                String paramName = (String) i.next();

                for (Field field : annotatedFields) {
                    //TODO only matches exact field names.  need to change to it matches start of ognl expression
                    //i.e take param name up to first . (period) and match against that
                    if (field.getName().equals(paramName)) {
                        allowed = true;
                    }
                }

                if (!allowed) {
                    paramsToRemove.add(paramName);
                }
            }
        } else {
            AnnotationUtils.addAllFields(Blocked.class, action.getClass(), annotatedFields);

            for (Iterator i = parameters.keySet().iterator(); i.hasNext();) {
                String paramName = (String) i.next();

                for (Field field : annotatedFields) {
                    //TODO only matches exact field names.  need to change to it matches start of ognl expression
                    //i.e take param name up to first . (period) and match against that
                    if (field.getName().equals(paramName)) {
                        paramsToRemove.add(paramName);
                    }
                }
            }
        }

        for (Iterator i = paramsToRemove.iterator(); i.hasNext();) {
            parameters.remove(i.next());
        }

        return invocation.invoke();
    }

}
