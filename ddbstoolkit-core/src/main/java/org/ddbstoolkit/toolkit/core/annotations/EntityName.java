package org.ddbstoolkit.toolkit.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Name of the entity inside a database or data source
 * @author Cyril Grandjean
 * @version Creation of the class
 */
@Target(value = {ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityName {

    String name() default "";
}
