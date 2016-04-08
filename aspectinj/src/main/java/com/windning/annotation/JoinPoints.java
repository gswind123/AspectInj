package com.windning.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for classes containing join points
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface JoinPoints {
}
