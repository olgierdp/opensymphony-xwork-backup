package com.opensymphony.xwork2.parameters.accessor;

import java.util.Map;

public interface ParametersPropertyAccessor {
    Object getProperty(Map context, Object lastObject, Object name) throws Exception;

    void setProperty(Map context, Object target, Object property, Object value) throws Exception;
}
