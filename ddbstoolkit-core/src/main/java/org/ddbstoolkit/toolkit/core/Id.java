package org.ddbstoolkit.toolkit.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation representing the identifier of an element
 * User: Cyril GRANDJEAN
 * Date: 18/06/2012
 * Time: 10:03
 *
 * @version Creation of the class
 */
@Target(value = {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {

    String name() default "";
    
    boolean autoincrement() default true;
}