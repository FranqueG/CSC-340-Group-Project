package database;

import exported.Column;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public abstract class Database {

    /**
     * Inserts a object into the table
     * @param _table object representing a table, it's class must be annotated with @Table
     */
    public abstract void Insert(Object _table);

    /**
     * Reads a object as a table using reflection
     * the object read must be annotated @Table
     * @param _table the object representing a table
     * @return a map of field names and corresponding column data
     * @throws IllegalAccessException if something went wrong with reflection
     */
    protected static Map<String, ColumnData> getColumns(Object _table) throws IllegalAccessException {
        var fields = _table.getClass().getFields();
        var fieldMap = new HashMap<String, ColumnData>();

        for(var field : fields) {
            var fieldAnnotation = field.getAnnotation(Column.class);
            if (fieldAnnotation != null) {
                var fieldName = fieldAnnotation.name();
                if (fieldName.equals(""))
                    fieldName = field.getName();
                var data = new ColumnData(
                        field.get(_table),
                        field.getType(),
                        fieldAnnotation.notNull(),
                        fieldAnnotation.unique(),
                        fieldAnnotation.primaryKey()
                        );
                fieldMap.put(fieldName,data);
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
        private final boolean notNull, unique, primaryKey;

        public ColumnData(Object _data, Type _type, boolean _notNull, boolean _unique, boolean _primaryKey) {
            this.data = _data;
            this.type = _type;
            this.notNull = _notNull;
            this.unique = _unique;
            this.primaryKey = _primaryKey;
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
    }
}
