package org.ddbstoolkit.toolkit.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation representing an identifier of an IEntity
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
@Target(value = {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {

	/**
	 * Name of the field
	 * @return Name of the field
	 */
    String name() default "";
    
    /**
     * Indicates if the field is auto incrementing
     * @return
     */
    boolean autoincrement() default true;
}