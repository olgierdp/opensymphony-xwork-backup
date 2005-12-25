/*
 * Created on Dec 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.opensymphony.xwork.interceptor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.opensymphony.xwork.ActionInvocation;

/**
 * @author Gabe
 *
 * Filters parameters
 */
public class ParameterFilterInterceptor implements Interceptor {

    private Collection allowed;
    
    private Collection blocked;
    
    private TreeMap includesExcludesMap;
    
    private boolean defaultBlock=false;
    
    /* (non-Javadoc)
     * @see com.opensymphony.xwork.interceptor.Interceptor#destroy()
     */
    public void destroy() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork.interceptor.Interceptor#init()
     */
    public void init() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork.interceptor.Interceptor#intercept(com.opensymphony.xwork.ActionInvocation)
     */
    public String intercept(ActionInvocation invocation) throws Exception {
        
        Map parameters=invocation.getInvocationContext().getParameters();
        HashSet paramsToRemove=new HashSet();
        
        TreeMap includesExcludesMap=getIncludesExcludesMap();
        
        for (Iterator i=parameters.keySet().iterator();i.hasNext(); ) {
            
            String param=(String)i.next();
            
            boolean currentAllowed=!isDefaultBlock();
            
            boolean foundApplicableRule=false;
            for (Iterator j=includesExcludesMap.keySet().iterator(); j.hasNext(); ) {
                String currRule=(String)j.next();
                
                if (param.startsWith(currRule)
                        && (param.length()==currRule.length()
                                || isPropSeperator(param.charAt(currRule.length())))) {
                    currentAllowed=((Boolean)includesExcludesMap.get(currRule)).booleanValue();
                }	else {
                    if (foundApplicableRule) {
                        foundApplicableRule=false;
                        break;
                    }
                }
            }
            if (!currentAllowed) {
                paramsToRemove.add(param);
            }
        }
        for (Iterator i=paramsToRemove.iterator(); i.hasNext();) {
            parameters.remove(i.next()); 
        }
        
        return invocation.invoke();
    }
    
    /**
     * @param c
     * @return
     */
    private boolean isPropSeperator(char c) {
        // TODO Auto-generated method stub
        return c=='.' || c=='(' || c=='[';
    }

    private TreeMap getIncludesExcludesMap() {
        if (this.includesExcludesMap==null) {
            this.includesExcludesMap=new TreeMap();
            
            if (getAllowed()!=null) {
	            for (Iterator i=getAllowed().iterator();i.hasNext(); ) {
	                this.includesExcludesMap.put(i.next(), Boolean.TRUE);
	            }
            }
            if (getBlocked()!=null) {
	            for (Iterator i=getBlocked().iterator();i.hasNext(); ) {
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
     * @param defaultBlock The defaultBlock to set.
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
        if (commaDelim==null || commaDelim.trim().length()==0) {
            return null;
        }
        String [] splitString=commaDelim.split(",");
        
        HashSet set=new HashSet();
        for (int i=0; i<splitString.length;i++) {
            set.add(splitString[i].trim());
        }
        return set;
    }
}
