package database;
/*
 * Last updated: 4/29/2021
 * This class represents an abstract database connection
 * Authors: Joshua Millikan
 */

import annotations.Column;
import annotations.Table;
import errors.DatabaseError;

import java.io.InvalidClassException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class Database {
    private static final ExecutorService pool = Executors.newSingleThreadExecutor();

    /**
     * Save a collection of objects to the database
     * @param _objects collection of objects
     * @param <T> object type
     */
    public final <T> void saveObjects(Collection<T> _objects) {
            for(Object obj : _objects) {
                validate(obj);
                pool.submit(() -> updateInsert(obj));
            }
    }

    /**
     * Save an object to the database
     * @param _obj object to save
     */
    public final void saveObject(Object _obj) {
        saveObjects(Collections.singletonList(_obj));
    }

    /**
     * load a list of objects from the database
     * @param _objs objects to search for
     * @param <T> the object type
     * @return array list of futures that will contain the objects found
     */
    public final <T> ArrayList<Future<ArrayList<T>>> loadObjects(Collection<T> _objs) {
        var ls = new ArrayList<Future<ArrayList<T>>>();
        for (T object : _objs) {
            validate(object);
            var future = pool.submit(() -> selectFromDatabase(object));
            ls.add(future);
        }
        return ls;
    }

    /**
     * search for a object in the database
     * @param _obj the object to search for
     * @return future that will contain the list of results
     */
    public final <T> Future<ArrayList<T>> loadObject(T _obj) {
        validate(_obj);
        return pool.submit(() ->
                selectFromDatabase(_obj));
    }

    /**
     * Remove a object from the database
     * @param _obj the object to use as a filter
     */
    public final void delete(Object _obj) {
        validate(_obj);
        pool.submit(()-> deactivate(_obj));
    }

    /**
     * Shutdown the database thread pool.
     * Must be called once before exiting the program
     */
    public void shutdown() {
        pool.shutdown();
    }

    /**
     * Return the name of a @Table class
     * @param annotated the class
     * @return the table name in the annotation or the class name if the annotation was empty
     * @throws InvalidClassException if the class is not actually annotated @Table
     */
    protected static String nameFromAnnotation(Class<?> annotated) throws InvalidClassException {
        var annotation = annotated.getAnnotation(Table.class);
        if(annotation == null)
            throw new InvalidClassException("Class is not a table");
        return annotation.name().equals("") ? annotated.getName() : annotation.name();
    }

    /**
     * Return the name of a @Column field
     * @param annotated the field
     * @return the field name in the annotation or the field name if the annotation was empty
     * @throws InvalidClassException if the field is not actually annotated @Table
     */
    protected static String nameFromAnnotation(Field annotated) throws InvalidClassException {
        var annotation = annotated.getAnnotation(Column.class);
        if(annotation == null)
            throw new InvalidClassException("Field is not a Column");
        return annotation.name().equals("") ? annotated.getName() : annotation.name();
    }

    /**
     * Retrieves data from the database
     * @param _obj object to use as a template for the request, each non-null field will be used in the select statement
     * @return all objects in the database the matched the request
     */
    protected abstract <T> ArrayList<T> selectFromDatabase(T _obj);

    /**
     * Inserts a object into the table
     *
     * @param _table object representing a table, it's class must be annotated with @Table
     */
    protected abstract long updateInsert(Object _table);

    /**
     * Deactivates a record corresponding to the object given
     * @param _table the object to disable the record for
     */
    protected abstract void deactivate(Object _table);


    /**
     * drop a class's table from the database
     * @param _c class to drop
     * @throws InvalidClassException if class is not a table
     */
    public abstract void drop(Class<?> _c) throws InvalidClassException;

    /**
     * Reads a object as a table using reflection
     * the object read must be annotated @Table
     *
     * @param _table the object representing a table
     * @return a map of field names and corresponding column data
     * @throws IllegalAccessException if something went wrong with reflection
     */
    protected static Map<String, ColumnData> getColumns(Object _table) throws IllegalAccessException {
        var fields = _table.getClass().getDeclaredFields();
        var fieldMap = new HashMap<String, ColumnData>();

        for (var field : fields) {
            var fieldAnnotation = field.getAnnotation(Column.class);
            if (fieldAnnotation != null) {
                field.setAccessible(true);
                var fieldName = fieldAnnotation.name();
                if (fieldName.equals(""))
                    fieldName = field.getName();

                boolean nestedTable = field.getType().getAnnotation(Table.class) != null;
                var obj = field.get(_table);
                boolean list = fieldAnnotation.containsType() != Object.class;
                var data = new ColumnData(
                        obj,
                        field.getType(),
                        fieldAnnotation.notNull(),
                        fieldAnnotation.unique(),
                        fieldAnnotation.primaryKey(),
                        nestedTable,
                        list
                );
                fieldMap.put(fieldName, data);
            }
        }
        return fieldMap;
    }

    /**
     * Validates that an object can be saved/loaded in the database. Will
     * throw an error if it's not
     * @param _obj the object to validate
     */
    private void validate(Object _obj) {
        if(_obj == null)
            throw new DatabaseError("A input object was null!");
        if(_obj.getClass().getAnnotation(Table.class) == null)
            throw new DatabaseError("Class "+ _obj.getClass() +" is not a table!");
        for(Field field : _obj.getClass().getDeclaredFields()) {
            if(field.getAnnotation(Column.class) != null) {
                if(!Object.class.isAssignableFrom(field.getType())) {
                    try {
                        throw new DatabaseError("Malformed column "+nameFromAnnotation(field)+" is of a invalid type: "+field.getType().getName());
                    } catch (InvalidClassException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * This class holds data about a column in the table
     */
    protected static class ColumnData {
        private final Object data;
        private final Type type;
        private final boolean notNull, unique, primaryKey, nestedTable, list;

        public ColumnData(Object _data, Type _type, boolean _notNull, boolean _unique, boolean _primaryKey, boolean _nestedTable, boolean _list) {
            this.data = _data;
            this.type = _type;
            this.notNull = _notNull;
            this.unique = _unique;
            this.primaryKey = _primaryKey;
            this.nestedTable = _nestedTable;
            this.list = _list;
        }

        //=================  GETTERS ===============
        public Object getData() {
            return data;
        }

        public Type getType() {
            return type;
        }

        public boolean isNotNull() {
            return notNull;
        }

        public boolean isUnique() {
            return unique;
        }

        public boolean isPrimaryKey() {
            return primaryKey;
        }

        public boolean isNestedTable() {
            return nestedTable;
        }

        public boolean isList() {
            return list;
        }
    }
}
