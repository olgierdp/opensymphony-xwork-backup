/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.xml.handlers;

import com.opensymphony.xwork.util.OgnlUtil;
import com.opensymphony.xwork.xml.DefaultDelegatingHandler;
import com.opensymphony.xwork.xml.Path;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * ObjectCreateHandler
 * @author Jason Carreira
 * Created May 16, 2003 6:50:04 PM
 */
public class ObjectCreateHandler extends DefaultDelegatingHandler {
    //~ Instance fields ////////////////////////////////////////////////////////

    private Class clazz;
    private Map attributeMappings = new HashMap();

    //~ Constructors ///////////////////////////////////////////////////////////

    public ObjectCreateHandler(Path path, Class clazz) {
        super(path);
        this.clazz = clazz;
    }

    public ObjectCreateHandler(String pathString, Class clazz) {
        super(pathString);
        this.clazz = clazz;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void addAttributeMapping(String attributeName, String propertyName) {
        attributeMappings.put(attributeName, propertyName);
    }

    /**
    * Subclasses should override this method if they want to receive an event at the starting point of the path
    */
    public void startingPath(Path path, Map attributeMap) {
        super.startingPath(path, attributeMap);

        try {
            Object instance = clazz.newInstance();
            OgnlUtil.setProperties(buildPropertyMap(attributeMap), instance);
            getValueStack().push(instance);
        } catch (Exception e) {
            getRootHandler().addError(e);
        }
    }

    protected Map buildPropertyMap(Map attributeMap) {
        Map propertyMap = new HashMap(attributeMap);

        for (Iterator iterator = attributeMappings.entrySet().iterator();
                iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object value = propertyMap.remove(entry.getKey());

            if (value != null) {
                propertyMap.put(entry.getValue(), value);
            }
        }

        //        for (Iterator iterator = attributeMap.entrySet().iterator();
        //                iterator.hasNext();) {
        //            Map.Entry entry = (Map.Entry) iterator.next();
        //            String attributeName = (String) entry.getKey();
        //            String propertyName = (String) (attributeMappings.containsKey(attributeName) ? attributeMappings.get(attributeName) : attributeName);
        //            propertyMap.put(propertyName, entry.getValue());
        //        }
        return propertyMap;
    }
}
