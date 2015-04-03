package org.ddbstoolkit.toolkit.modules.datastore.jena;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate if the field is optional
 * User: Cyril GRANDJEAN
 * Date: 20/06/2012
 * Time: 14:33
 *
 * @version Creation of the class
 */
@Target(value = {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Optional {
}
