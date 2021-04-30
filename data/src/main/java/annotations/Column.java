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
    /**
     * if true forces a column to be non-null in the database
     */
    boolean notNull() default false;

    /**
     * If true forces a column to make all values unique in the database
     */
    boolean unique() default false;

    /**
     * sets the name of a column.
     * if left blank uses the field name
     */
    String name() default "";

    /**
     * If true makes this column the primary key.
     */
    boolean primaryKey() default false;

    /**
     * Only used for lists, sets the type of object
     *  that list contains, since it cannot be deduced when loading otherwise
     */
    Class<?> containsType() default Object.class;
}
