package org.ddbstoolkit.toolkit.modules.datastore.jena.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation corresponding to the default namespace of a class
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultNamespace {
	
    String name() default "";
    String url() default "";
}