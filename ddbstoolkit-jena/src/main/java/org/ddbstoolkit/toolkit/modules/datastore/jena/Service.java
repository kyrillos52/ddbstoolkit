package org.ddbstoolkit.toolkit.modules.datastore.jena;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



/**
 * Remote Endpoint URL associated with an object
 * User: Cyril GRANDJEAN
 * Date: 19/06/2012
 * Time: 09:40
 *
 * @version Creation of the class
 */
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
    String url() default "";
}
