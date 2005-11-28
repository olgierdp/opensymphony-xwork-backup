/*
 * Copyright (c) 2005 aixcept (Rainer Hermanns). All Rights Reserved.
 */
package com.opensymphony.xwork.conversion;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.util.XWorkBasicConverter;
import com.opensymphony.xwork.conversion.annotations.*;

import java.util.List;
import java.util.HashMap;
import java.math.BigInteger;

/**
 * <code>ConversionTestAction</code>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
@Conversion()
public class ConversionTestAction implements Action {

    private String convertInt;

    private String convertDouble;

    private List users = null;


    private HashMap keyValues = null;


    public String getConvertInt() {
        return convertInt;
    }

    @TypeConversion(type = ConversionType.APPLICATION, converter = XWorkBasicConverter.class)
    public void setConvertInt( String convertInt ) {
        this.convertInt = convertInt;
    }

    public String getConvertDouble() {
        return convertDouble;
    }

    @TypeConversion(converter = XWorkBasicConverter.class)
    public void setConvertDouble( String convertDouble ) {
        this.convertDouble = convertDouble;
    }

    public List getUsers() {
        return users;
    }

    @TypeConversion(rule = ConversionRule.COLLECTION, converter = String.class)
    public void setUsers( List users ) {
        this.users = users;
    }

    public HashMap getKeyValues() {
        return keyValues;
    }

    @TypeConversion(rule = ConversionRule.MAP, converter = BigInteger.class)
    public void setKeyValues( HashMap keyValues ) {
        this.keyValues = keyValues;
    }

    /**
     * Where the logic of the action is executed.
     *
     * @return a string representing the logical result of the execution.
     *         See constants in this interface for a list of standard result values.
     * @throws Exception thrown if a system level exception occurs.
     *                   Application level exceptions should be handled by returning
     *                   an error value, such as Action.ERROR.
     */
    @TypeConversion(type = ConversionType.APPLICATION, property = "java.util.Date", converter = XWorkBasicConverter.class)
    public String execute() throws Exception {
        return SUCCESS;
    }
}
