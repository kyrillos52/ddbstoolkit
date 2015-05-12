package org.ddbstoolkit.toolkit.modules.datastore.jena.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



/**
 * Annotation indicating the URI of an object
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
@Target(value = {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface URI {
}
