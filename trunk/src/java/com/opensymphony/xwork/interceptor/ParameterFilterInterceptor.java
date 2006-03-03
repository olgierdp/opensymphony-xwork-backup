/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.ActionInvocation;

import java.util.*;

/**
 * @author Gabe
 * 
 * <!-- START SNIPPET: description -->
 *
 * <p>The Parameter Filter Interceptor blocks parameters from getting
 * to the rest of the stack or your action. You can use multiple 
 * parameter filter interceptors for a given action, so, for example,
 * you could use one in your default stack that filtered parameters
 * you wanted blocked from every action and those you wanted blocked 
 * from an individual action you could add an additional interceptor
 * for each action.</p>
 * 
 * <!-- END SNIPPET: description -->
 * 
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>allowed - a comma delimited list of parameter prefixes
 *  that are allowed to pass to the action</li>
 * <li>blocked - a comma delimited list of parameter prefixes 
 * that are not allowed to pass to the action</li>
 * <li>defaultBlock - boolean (default to false) whether by
 * default a given parameter is blocked. If true, then a parameter
 * must have a prefix in the allowed list in order to be able 
 * to pass to the action
 * </ul>
 * 
 * <p>The way parameters are filtered for the least configuration is that
 * if a string is in the allowed or blocked lists, then any parameter
 * that is a member of the object represented by the parameter is allowed
 * or blocked respectively.</p>
 * 
 * <p>For example, if the parameters are:
 * <ul>
 * <li>blocked: person,person.address.createDate,personDao</li>
 * <li>allowed: person.address</li>
 * <li>defaultBlock: false</li>
 * </ul>
 * <br>
 * The parameters person.name, person.phoneNum etc would be blocked 
 * because 'person' is in the blocked list. However, person.address.street
 * and person.address.city would be allowed because person.address is
 * in the allowed list (the longer string determines permissions).</p> 
 * 
 *
 * <!-- END SNIPPET: parameters -->
 */
public class ParameterFilterInterceptor implements Interceptor {

    private Collection allowed;

    private Collection blocked;

    private Map includesExcludesMap;

    private boolean defaultBlock = false;

    /* (non-Javadoc)
    * @see com.opensymphony.xwork.interceptor.Interceptor#destroy()
    */
    public void destroy() {
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork.interceptor.Interceptor#init()
     */
    public void init() {
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork.interceptor.Interceptor#intercept(com.opensymphony.xwork.ActionInvocation)
     */
    public String intercept(ActionInvocation invocation) throws Exception {

        Map parameters = invocation.getInvocationContext().getParameters();
        HashSet paramsToRemove = new HashSet();

        Map includesExcludesMap = getIncludesExcludesMap();

        for (Iterator i = parameters.keySet().iterator(); i.hasNext();) {

            String param = (String) i.next();

            boolean currentAllowed = !isDefaultBlock();

            boolean foundApplicableRule = false;
            for (Iterator j = includesExcludesMap.keySet().iterator(); j.hasNext();) {
                String currRule = (String) j.next();

                if (param.startsWith(currRule)
                        && (param.length() == currRule.length()
                        || isPropSeperator(param.charAt(currRule.length())))) {
                    currentAllowed = ((Boolean) includesExcludesMap.get(currRule)).booleanValue();
                } else {
                    if (foundApplicableRule) {
                        foundApplicableRule = false;
                        break;
                    }
                }
            }
            if (!currentAllowed) {
                paramsToRemove.add(param);
            }
        }
        for (Iterator i = paramsToRemove.iterator(); i.hasNext();) {
            parameters.remove(i.next());
        }

        return invocation.invoke();
    }

    /**
     * @param c
     * @return <tt>true</tt>, if char is property separator, <tt>false</tt> otherwise.
     */
    private boolean isPropSeperator(char c) {
        return c == '.' || c == '(' || c == '[';
    }

    private Map getIncludesExcludesMap() {
        if (this.includesExcludesMap == null) {
            this.includesExcludesMap = new TreeMap();

            if (getAllowed() != null) {
                for (Iterator i = getAllowed().iterator(); i.hasNext();) {
                    this.includesExcludesMap.put(i.next(), Boolean.TRUE);
                }
            }
            if (getBlocked() != null) {
                for (Iterator i = getBlocked().iterator(); i.hasNext();) {
                    this.includesExcludesMap.put(i.next(), Boolean.FALSE);
                }
            }
        }

        return this.includesExcludesMap;
    }

    /**
     * @return Returns the defaultBlock.
     */
    public boolean isDefaultBlock() {
        return defaultBlock;
    }

    /**
     * @param defaultExclude The defaultExclude to set.
     */
    public void setDefaultBlock(boolean defaultExclude) {
        this.defaultBlock = defaultExclude;
    }

    /**
     * @return Returns the blocked.
     */
    public Collection getBlocked() {
        return blocked;
    }

    /**
     * @param blocked The blocked to set.
     */
    public void setBlocked(Collection blocked) {
        this.blocked = blocked;
    }

    public void setBlocked(String blocked) {
        setBlocked(asCollection(blocked));
    }

    /**
     * @return Returns the allowed.
     */
    public Collection getAllowed() {
        return allowed;
    }

    /**
     * @param allowed The allowed to set.
     */
    public void setAllowed(Collection allowed) {
        this.allowed = allowed;
    }

    public void setAllowed(String allowed) {

        setAllowed(asCollection(allowed));
    }

    private Collection asCollection(String commaDelim) {
        if (commaDelim == null || commaDelim.trim().length() == 0) {
            return null;
        }
        String [] splitString = commaDelim.split(",");

        HashSet set = new HashSet();
        for (int i = 0; i < splitString.length; i++) {
            set.add(splitString[i].trim());
        }
        return set;
    }
}
