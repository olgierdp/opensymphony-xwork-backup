/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.ObjectFactory;

import ognl.NullHandler;
import ognl.Ognl;
import ognl.OgnlRuntime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Normally does nothing, but if {@link #CREATE_NULL_OBJECTS} is in the action context
 * with a value of true, then this class will attempt to create null objects when Ognl
 * requests null objects be created.
 * <p/>
 * The following rules are used:
 * <ul>
 * <li>If the null property is a simple bean with a no-arg constructor, it will simply be
 * created using ObjectFactory's {@link ObjectFactory#buildBean(java.lang.Class) buildBean} method.</li>
 * <li>If the property is declared <i>exactly</i> as a {@link Collection} or {@link List}, then this class
 * will look in the conversion property file (see {@link XWorkConverter}) for an entry
 * with a key of the form "Collection_[propertyName]". Using the value of this key as
 * the class type in which the collection will be holding, an {@link XWorkList} will be
 * created, allowing simple dynamic insertion.</li>
 * <li>If the property is declared as a {@link Map}, then the same rules are applied for
 * list, except that an {@link XWorkMap} will be created instead.</li>
 * </ul>
 *
 * @author Matt Ho
 * @author Patrick Lightbody
 */
public class InstantiatingNullHandler implements NullHandler {
    //~ Static fields/initializers /////////////////////////////////////////////

    public static final String CREATE_NULL_OBJECTS = "xwork.NullHandler.createNullObjects";
    private static final Log LOG = LogFactory.getLog(InstantiatingNullHandler.class);

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object nullMethodResult(Map context, Object target, String methodName, Object[] args) {
        return null;
    }

    public Object nullPropertyValue(Map context, Object target, Object property) {
        Boolean create = (Boolean) context.get(CREATE_NULL_OBJECTS);
        boolean c = ((create == null) ? false : create.booleanValue());

        if (!c) {
            return null;
        }

        if ((target == null) || (property == null)) {
            return null;
        }

        try {
            String propName = property.toString();
            Object realTarget = OgnlUtil.getRealTarget(propName, context, target);
            Class clazz = null;

            if (realTarget != null) {
                clazz = OgnlRuntime.getPropertyDescriptor(realTarget.getClass(), propName).getPropertyType();
            }

            if (clazz == null) {
                // can't do much here!
                return null;
            }

            Object param = createObject(clazz, realTarget, propName);

            Ognl.setValue(propName, context, realTarget, param);

            return param;
        } catch (Exception e) {
            LOG.error("Could not create and/or set value back on to object", e);
        }

        return null;
    }


    private Object createObject(Class clazz, Object target, String property) throws Exception {
        if (Collection.class.isAssignableFrom(clazz)) {
            /*Class collectionType = getCollectionType(target.getClass(), property);

            if (collectionType == null) {
                return null;
            }

            return new XWorkList(collectionType);*/
        	return new ArrayList();
        } else if (clazz == Map.class) {
            /*Class collectionType = getCollectionType(target.getClass(), property);

            if (collectionType == null) {
                return null;
            }

            return new XWorkMap(collectionType);*/
        	return new HashMap();
        }

        return ObjectFactory.getObjectFactory().buildBean(clazz);
    }
}
