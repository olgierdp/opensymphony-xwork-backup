package com.opensymphony.xwork2.interceptor.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.opensymphony.xwork2.Action;

/**
 * Declares that by default fields on the {@link Action} class
 * are NOT permitted to be set from HttpRequest parameters.
 * To allow access to a field it must be annotated with {@link Allowed}
 *
 * @author martin.gilday
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BlockByDefault {

}
