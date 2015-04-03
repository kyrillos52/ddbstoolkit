package org.ddbstoolkit.toolkit.modules.datastore.jena;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation corresponding to the default namespace of a class
 * User: Cyril GRANDJEAN
 * Date: 19/06/2012
 * Time: 09:36
 *
 * @version Creation of the class
 */
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultNamespace {
    String name() default "";
    String url() default "";
}
