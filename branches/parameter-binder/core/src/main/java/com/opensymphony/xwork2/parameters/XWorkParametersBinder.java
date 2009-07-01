/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.opensymphony.xwork2.parameters;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.parameters.nodes.Node;
import com.opensymphony.xwork2.parameters.nodes.IdentifierNode;
import com.opensymphony.xwork2.parameters.nodes.IndexedNode;
import com.opensymphony.xwork2.parameters.nodes.CollectionNode;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.conversion.NullHandler;
import com.opensymphony.xwork2.ognl.accessor.CompoundRootAccessor;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import ognl.PropertyAccessor;
import ognl.OgnlException;
import ognl.OgnlContext;

public class XWorkParametersBinder {
    protected ReflectionProvider reflectionProvider;
    protected NullHandler nullHandler;
    protected Container container;
    private XWorkParametersMapPropertyAccessor mapAccessor;

    private static final Map<String, List<Node>> nodesCache = new WeakHashMap<String, List<Node>>();

    public void setProperty(Map<String, Object> context, Object action, String paramName, Object paramValue) {
        try {
            OgnlContext ognlContext = new OgnlContext(context);

            List<Node> nodes = nodesCache.get(paramName);
            if (nodes == null) {
                XWorkParameterParser parser = new XWorkParameterParser(paramName);
                nodes = parser.expression();
                nodesCache.put(paramName, nodes);
            }

            Object lastObject = action;
            Object lastProperty = null;


            Iterator<Node> itt = nodes.iterator();
            while (itt.hasNext()) {
                //iterate over the nodes and create objects if needed
                Node node = itt.next();
                boolean lastNode = !itt.hasNext();

                if (node instanceof IdentifierNode) {
                    //A.B
                    String id = ((IdentifierNode) node).getIdentifier();

                    if (StringUtils.isBlank(id))
                        throw new ParseException("Expression '" + paramName + "' is invalid");

                    lastProperty = id;

                    //if this is not the last expression, create the object if it doesn't exist
                    PropertyAccessor accessor = getPropertyAccessor(lastObject);
                    Object value = accessor.getProperty(ognlContext, lastObject, id);
                    if (!lastNode) {
                        if (value == null) {
                            //create it
                            value = create(ognlContext, action, id);
                        }
                        lastObject = value;
                    }
                } else if (node instanceof IndexedNode) {
                    //A[B]
                    IndexedNode indexedNode = (IndexedNode) node;
                    String id = indexedNode.getIdentifier();
                    Object index = indexedNode.getIndex();

                    lastProperty = index;
                    PropertyAccessor accessor = getPropertyAccessor(lastObject);
                    //the list or map
                    Object container = accessor.getProperty(ognlContext, lastObject, id);

                    if (container == null) {
                        //create the list or map
                        container = create(ognlContext, lastObject, id);
                    }

                    lastObject = container;

                    if (!lastNode) {
                        //the expression goes on like A[B].C, so now create A[B]
                        accessor = getPropertyAccessor(lastObject);
                        lastObject = getAndCreate(ognlContext, lastObject, index, accessor);
                    }
                } else if (node instanceof CollectionNode) {
                    //A(B)
                    CollectionNode indexedNode = (CollectionNode) node;
                    String id = indexedNode.getIdentifier();
                    Object index = indexedNode.getIndex();

                    lastProperty = index;
                    PropertyAccessor accessor = getPropertyAccessor(lastObject);
                    lastObject = accessor.getProperty(ognlContext, lastObject, id);

                    //create the lastObject
                    if (lastObject == null) {
                        //create it
                        lastObject = create(ognlContext, action, id);
                    }
                }
            }

            PropertyAccessor accessor = getPropertyAccessor(lastObject);
            accessor.setProperty(ognlContext, lastObject, lastProperty, paramValue);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    protected PropertyAccessor getPropertyAccessor(Object object) {
        if (object instanceof CompoundRoot)
            return container.getInstance(PropertyAccessor.class, CompoundRoot.class.getName());
        if (object instanceof Map)
            return mapAccessor;
        else if (object instanceof List)
            return container.getInstance(PropertyAccessor.class, List.class.getName());
        else if (object instanceof Collection)
            return container.getInstance(PropertyAccessor.class, Collection.class.getName());
        else if (object instanceof Enumeration)
            return container.getInstance(PropertyAccessor.class, Enumeration.class.getName());
        else if (object instanceof Iterator)
            return container.getInstance(PropertyAccessor.class, Iterator.class.getName());
        else
            return container.getInstance(PropertyAccessor.class, Object.class.getName());

    }

    /**
     * Uses the NullHandler to create and set a field on an object
     */
    protected Object create(Map<String, Object> context, Object root, String property) {
        boolean originalValue = ReflectionContextState.isCreatingNullObjects(context);
        try {
            ReflectionContextState.setCreatingNullObjects(context, true);
            return nullHandler.nullPropertyValue(context, root, property);
        } finally {
            ReflectionContextState.setCreatingNullObjects(context, originalValue);
        }
    }

    protected Object getAndCreate(Map<String, Object> context, Object root, Object property, PropertyAccessor accessor) throws OgnlException {
        boolean originalValue = ReflectionContextState.isCreatingNullObjects(context);
        try {
            ReflectionContextState.setCreatingNullObjects(context, true);
            return accessor.getProperty(context, root, property);
        } finally {
            ReflectionContextState.setCreatingNullObjects(context, originalValue);
        }
    }

    @Inject
    public void setReflectionProvider(ReflectionProvider reflectionProvider) {
        this.reflectionProvider = reflectionProvider;
    }

    @Inject("java.lang.Object")
    public void setNullHandler(NullHandler nullHandler) {
        this.nullHandler = nullHandler;
    }

    @Inject
    public void setContainer(Container container) {
        this.container = container;
        this.mapAccessor = new XWorkParametersMapPropertyAccessor();
        container.inject(mapAccessor);
    }
}
