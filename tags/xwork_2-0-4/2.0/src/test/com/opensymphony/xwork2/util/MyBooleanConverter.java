package com.opensymphony.xwork2.util;

import ognl.DefaultTypeConverter;

import java.util.Map;
import java.util.Date;
import java.lang.reflect.Member;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>MyBooleanConverter</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id: $
 */
public class MyBooleanConverter extends DefaultTypeConverter {

    /**
     * The static jakarta-commons-logging reference.
     */
    private static final Log log = LogFactory.getLog(MyBooleanConverter.class);

    @Override
    public Object convertValue(Map context, Object value, Class toType) {
        try {
            if (toType == String.class) {
                Boolean bool = (Boolean) value;
                log.error("to String type...");
                return bool.toString();
            } else if (toType == Boolean.class) {
                String valueStr = (String) value;

                if ( valueStr!= null && (valueStr.equalsIgnoreCase("y") || valueStr.equalsIgnoreCase("yes"))) {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            } else {
                log.error("Don't know how to convert between " + value.getClass().getName() +
                        " and " + toType.getName());
            }

        } catch (Exception e) {
            log.error("Error while converting", e);
        }

        return null;

    }

    @Override
    public Object convertValue(Map context, Object source, Member member, String property, Object value, Class toClass) {
        return convertValue(context, value, toClass);
    }
}
