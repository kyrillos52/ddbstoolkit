package org.ddbstoolkit.toolkit.core.reflexion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate the name of the property inside a database or data source
 * User: Cyril GRANDJEAN
 * Date: 26/06/2012
 * Time: 10:20
 *
 * @version Creation of the class
 */
@Target(value = {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyName {

    String name() default "";
}
