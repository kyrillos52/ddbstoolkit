package org.ddbstoolkit.toolkit.modules.datastore.jena;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



/**
 * Define the namespace of a property
 * User: Cyril GRANDJEAN
 * Date: 19/06/2012
 * Time: 09:39
 *
 * @version Creation of the class
 */
@Target(value = {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Namespace {
    String name() default "";
    String url() default "";
}
