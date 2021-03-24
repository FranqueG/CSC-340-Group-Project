package database;

import exported.Column;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public abstract class Database {

    public abstract void Insert(Object _table);

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
