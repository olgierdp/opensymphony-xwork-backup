/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.dispatcher;


/**
 * A mapping of data between a POJO and an XWork action
 * @author <a href="mailto:yellek@dev.java.net">Peter Kelley</a>
 */
public class Mapping {
    //~ Instance fields ////////////////////////////////////////////////////////

    /** The OGNL used to set/get values on the action/value stack */
    private String actionExpression;

    /** The OGNL used to set/get values on the POJO */
    private String pojoExpression;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Default constructor
     * @param actionOGNL The OGNL used to set/get values on the action/value stack
     * @param pojoOGNL The OGNL used to set/get values on the POJO
     */
    public Mapping(String actionOGNL, String pojoOGNL) {
        setActionExpression(actionOGNL);
        setPojoExpression(pojoOGNL);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Setter for property actionExpression.
     * @param newActionExpression the new value for field actionExpression
     */
    public void setActionExpression(String newActionExpression) {
        actionExpression = newActionExpression;
    }

    /**
     * Getter for property actionExpression.
     * @return the value of field actionExpression
     */
    public String getActionExpression() {
        return actionExpression;
    }

    /**
     * Setter for property pojoExpression.
     * @param newPojoExpression the new value for field pojoExpression
     */
    public void setPojoExpression(String newPojoExpression) {
        pojoExpression = newPojoExpression;
    }

    /**
     * Getter for property pojoExpression.
     * @return the value of field pojoExpression
     */
    public String getPojoExpression() {
        return pojoExpression;
    }
}
