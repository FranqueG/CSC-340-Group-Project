package database;
/*
 * This class represents an abstract database connection
 * Authors: Joshua Millikan
 */

import annotations.Column;
import annotations.Table;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class Database {
    private static final ExecutorService pool = Executors.newWorkStealingPool();

    /**
     * Save a collection of objects to the database
     * @param _objects collection of objects
     * @param <T> object type
     */
    public <T> void saveObjects(Collection<T> _objects) {
        pool.execute(() -> {
            for(Object obj : _objects)
                updateInsert(obj);
        });
    }

    /**
     * Save an object to the database
     * @param _obj object to save
     */
    public void saveObject(Object _obj) {
        saveObjects(Collections.singletonList(_obj));
    }

    /**
     * load a list of objects from the database
     * @param _objs objects to search for
     * @param <T> the object type
     * @return array list of futures that will contain the objects found
     */
    public <T> ArrayList<Future<List<T>>> loadObjects(Collection<T> _objs) {
        var ls = new ArrayList<Future<List<T>>>();
        for (T object : _objs) {
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
    public <T> Future<List<T>> loadObject(T _obj) {
        return pool.submit(() -> selectFromDatabase(_obj));
    }

    /**
     * Retrieves data from the database
     * @param _obj object to use as a template for the request, each non-null field will be used in the select statement
     * @return all objects in the database the matched the request
     */
    protected abstract <T> List<T> selectFromDatabase(T _obj);

    /**
     * Inserts a object into the table
     *
     * @param _table object representing a table, it's class must be annotated with @Table
     */
    protected abstract int updateInsert(Object _table);

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
                boolean list = field.getType().isAssignableFrom(List.class);

                var data = new ColumnData(
                        field.get(_table),
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
