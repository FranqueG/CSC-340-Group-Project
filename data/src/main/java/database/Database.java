package database;

import exported.Column;
import exported.Table;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Database {



    protected static HashMap<String, List<ColumnInfo>> getTableData() {
        var configBuilder = new ConfigurationBuilder();
        configBuilder.setUrls(ClasspathHelper.forJavaClassPath());
        configBuilder.setScanners(new TypeAnnotationsScanner());
        configBuilder.useParallelExecutor();

        var reflections = new Reflections(configBuilder);
        var classes = reflections.getTypesAnnotatedWith(Table.class);

        var classesFieldsMap = new HashMap<String, List<ColumnInfo>>();
        for (var tableClass : classes) {
            var fields = tableClass.getDeclaredFields();
            var list = new ArrayList<ColumnInfo>();
            for (var field : fields) {
                if (field.isAnnotationPresent(Column.class)) {
                    var annotation = field.getAnnotation(Column.class);
                    list.add(new ColumnInfo(
                            field.getName(),
                            field.getType(),
                            annotation.notNull(),
                            annotation.notNull()
                    ));
                }
            }
            classesFieldsMap.put(tableClass.getName(), list);
        }
        return classesFieldsMap;
    }

    protected static class ColumnInfo {
        private final String name;
        private final Type type;
        private final boolean notNull;
        private final boolean unique;

        protected ColumnInfo(String _name, Type _type, boolean _notNull, boolean _unique) {
            this.name = _name;
            this.type = _type;
            this.notNull = _notNull;
            this.unique = _unique;
        }

        //=================  GETTERS ===============
        public String getName() {
            return name;
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
    }
}
