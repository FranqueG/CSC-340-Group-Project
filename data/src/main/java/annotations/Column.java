package annotations;
/*
 * Last updated: 4/27/2021
 * This Annotation marks a field as representing a column in the database
 * All fields this is applied to must inherit from Object, eg int is not valid, Integer is.
 * Authors: Joshua Millikan
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Column {
    boolean notNull() default false;
    boolean unique() default false;
    String name() default "";
    boolean primaryKey() default false;
    Class<?> containsType() default Object.class;
}
