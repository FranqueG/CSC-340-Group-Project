package annotations;
/*
 * Last updated: 4/27/2021
 * This Annotation marks a class as representing a table in the database
 * Authors: Joshua Millikan
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
    /**
     * Sets the name of the table. will use
     * the class name if left blank
     * @apiNote classes in modules will have their class name as
     * "moduleName"."className" and the "." is not valid for SQLite, so
     * a name must be set manually for them.
     */
    String name() default "";
}
