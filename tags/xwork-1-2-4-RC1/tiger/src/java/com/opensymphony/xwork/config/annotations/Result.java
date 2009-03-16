package com.opensymphony.xwork.config.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Result {
  String name() default "";

  String type() default "";

  String value() default "";

  Param[] params() default {};
}
