/*
 * Created on Jun 17, 2005
 *
 */
package com.opensymphony.xwork.util;

/**
 * @author Gabe
 *         <p/>
 *         An Object to use within OGNL to proxy other Objects
 *         usually Collections that you set in a different place
 *         on the ValueStack but want to retain the context information
 *         about where they previously were.
 */
public class ObjectProxy {
    Object value;
    Class lastClassAccessed;
    String lastPropertyAccessed;


    /**
     * @return Returns the lastClassAccessed.
     */
    public Class getLastClassAccessed() {
        return lastClassAccessed;
    }

    /**
     * @param lastClassAccessed The lastClassAccessed to set.
     */
    public void setLastClassAccessed(Class lastClassAccessed) {
        this.lastClassAccessed = lastClassAccessed;
    }

    /**
     * @return Returns the lastPropertyAccessed.
     */
    public String getLastPropertyAccessed() {
        return lastPropertyAccessed;
    }

    /**
     * @param lastPropertyAccessed The lastPropertyAccessed to set.
     */
    public void setLastPropertyAccessed(String lastPropertyAccessed) {
        this.lastPropertyAccessed = lastPropertyAccessed;
    }

    /**
     * @return Returns the value.
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value The value to set.
     */
    public void setValue(Object value) {
        this.value = value;
    }
}
