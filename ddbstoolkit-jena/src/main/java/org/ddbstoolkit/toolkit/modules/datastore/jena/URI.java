package org.ddbstoolkit.toolkit.modules.datastore.jena;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



/**
 * Annotation indicating the URI of an object
 * User: Cyril GRANDJEAN
 * Date: 19/06/2012
 * Time: 09:41
 *
 * @version Creation of the class
 */
@Target(value = {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface URI {
}
