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
package com.opensymphony.xwork2.mvel;

import org.mvel2.ConversionHandler;
import com.opensymphony.xwork2.conversion.TypeConverter;

import java.util.Map;
import java.lang.reflect.Member;

public class MVELTypeConverterWrapper implements ConversionHandler {
    private TypeConverter typeConverter;

    public MVELTypeConverterWrapper(TypeConverter conv) {
        if (conv == null) {
            throw new IllegalArgumentException("Wrapped type converter cannot be null");
        }
        this.typeConverter = conv;
    }

    public Object convertValue(Map context, Object target, Member member,
            String propertyName, Object value, Class toType) {
        return typeConverter.convertValue(context, target, member, propertyName, value, toType);
    }

    public TypeConverter getTarget() {
        return typeConverter;
    }
    public Object convertFrom(Object in) {
        return typeConverter.convertValue(null, )
    }

    public boolean canConvertFrom(Class cls) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
