package database.sqlite;
/*
 * Last updated: 4/23/2021
 * This class represents a connection to a SQLite database and is responsible for
 * saving and loading data to the disk through it.
 * Authors: Joshua Millikan
 */

import annotations.Column;
import annotations.Table;
import database.Database;
import errors.DatabaseError;

import java.io.InvalidClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqliteDatabase extends Database {
    private final Connection connection;
    private static final Map<String, String> queryCache = new HashMap<>();

    /**
     * Opens a SQLite database connection
     *
     * @param _filepath the path to the database file
     */
    public SqliteDatabase(String _filepath) {
        super();
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + _filepath);
        } catch (SQLException e) {
            throw new DatabaseError("Failed to establish connection to database! cause: " + e.getMessage());
        }
    }

    @Override
    protected <T> ArrayList<T> selectFromDatabase(T _obj) {
        var annotation = _obj.getClass().getAnnotation(Table.class);
        if (annotation == null)
            throw new DatabaseError("Attempted to use class that is not a table!");
        try {
            Map<String, ColumnData> searchFields = new HashMap<>();
            for (var field : Database.getColumns(_obj).entrySet()) {
                if (field.getValue().getData() != null)
                    searchFields.put(field.getKey(), field.getValue());
            }
            var statement = this.connection.prepareStatement(createSelectString(annotation.name(), searchFields));
            int i = 0;
            for (var field : searchFields.values()) {
                try {
                    statement.setObject(++i, field.getData());
                } catch (ArrayIndexOutOfBoundsException e){
                    break;
                }
            }
            ResultSet result = statement.executeQuery();
            return buildFromResults(result, (Class<T>) _obj.getClass());
        } catch (IllegalAccessException | SQLException e) {
            throw new DatabaseError("Unable to read field from table object! cause: " + e.getMessage());
        }
    }

    @Override
    protected long updateInsert(Object _table) {
        var annotation = _table.getClass().getAnnotation(Table.class);
        if (annotation == null)
            throw new DatabaseError("Attempted to use class that is not a table!");
        try {
            var fields = Database.getColumns(_table);

            // create the table if it doesn't already exist
            String tableName = annotation.name().equals("") ? _table.getClass().getName() : annotation.name();
            String createSting = createTableString(tableName, fields);
            Statement statement = this.connection.createStatement();
            statement.execute(createSting);
            // handle nested tables
            for (var field : fields.entrySet()) {
                if (field.getValue().isNestedTable()) {
                    Long id = updateInsert(field.getValue().getData());
                    field.setValue(new ColumnData(
                            id,
                            Long.TYPE,
                            true,
                            false,
                            false,
                            false,
                            false));
                }
            }

            // Perform insertion statement
            String insertString = createInsertString(tableName, fields);
            PreparedStatement preparedStatement = this.connection.prepareStatement(insertString, Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            for (var field : fields.values()) {
                if (!field.isList()) {
                    preparedStatement.setObject(i, field.getData());
                    i++;
                }
            }
            preparedStatement.executeUpdate();
            long rowId = preparedStatement.getGeneratedKeys().getLong(1);
            fields.values().stream().filter(ColumnData::isList).forEach((field) -> createList(field, rowId, tableName));
            return rowId;
        } catch (IllegalAccessException | SQLException e) {
            throw new DatabaseError("Unable to read field from table object! cause: " + e.getMessage());
        }
    }

    /**
     * Create a list of objects in the database
     *
     * @param _listData    column containing the list
     * @param _owningTable owning table rowid
     */
    private void createList(ColumnData _listData, long _owningTable, String _owningName) {
        if (_listData.getData() instanceof List) {
            List<?> ls = (List<?>) _listData.getData();
            var firstElement = ls.get(0);
            var annotation = firstElement.getClass().getAnnotation(Table.class);
            if (annotation == null)
                throw new DatabaseError("List of non-table objects used as column");
            try {
                //create list objects table
                var fields = getColumns(firstElement);
                String tableName = annotation.name().equals("") ? firstElement.getClass().getName() : annotation.name();
                String tableString = createTableString(tableName, fields);
                Statement statement = this.connection.createStatement();

                statement.execute(tableString);

                //create join table linking list and the elements
                String createString = "CREATE TABLE IF NOT EXISTS " + tableName + "_" + _owningName +
                        "_join_table (" +
                        "parent INTEGER NOT NULL," +
                        " child INTEGER UNIQUE NOT NULL," +
                        " count INTEGER )";
                statement.execute(createString);

                for (Object element : ls) {

                    // insert the actual record
                    long child = updateInsert(element);

                    //update the join tables for the list
                    var query = createListInsertStatement(tableName + "_" + _owningName + "_join_table");
                    var preparedStatement = this.connection.prepareStatement(query);
                    preparedStatement.setLong(1, _owningTable);
                    preparedStatement.setLong(2, child);
                    preparedStatement.execute();
                }
            } catch (SQLException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the string to insert a join table for a list
     *
     * @param _joinTableName the join table name
     * @return the query string
     */
    private static String createListInsertStatement(String _joinTableName) {
        return "INSERT INTO "
                + _joinTableName
                + " (parent,child,count) VALUES (?, ?, 1)\n"
                + " ON CONFLICT(child) DO UPDATE SET count=count+1";
    }

    /**
     * Create the SQL select statement
     *
     * @param _tableName    name of the table to search
     * @param _searchFields fields to use as search parameters
     * @return the SQL query
     */
    private static String createSelectString(String _tableName, Map<String, ColumnData> _searchFields) {
        var builder = new StringBuilder("SELECT rowid, * FROM ").append(_tableName);
        StringBuilder tail = new StringBuilder();
        if (_searchFields.size() > 0) {
            builder.append(" WHERE ");
            for (var field : _searchFields.entrySet()) {
                if (!field.getValue().isList())
                    builder.append(field.getKey())
                            .append("=? AND ");
            }
            builder.append("active=1 ");
        }
        builder.append(tail);
        return builder.toString();
    }

    /**
     * Constructs java objects from SQL results
     *
     * @param _result SQLite query result data
     * @return list of objects constructed
     */
    private <T> ArrayList<T> buildFromResults(ResultSet _result, Class<T> _class) {
        var ls = new ArrayList<T>();
        try {
            while (_result.next()) {
                try {
                    var obj = _class.getDeclaredConstructor().newInstance();
                    for (var field : obj.getClass().getDeclaredFields()) {
                        var annotation = field.getAnnotation(Column.class);
                        if (annotation != null) {
                            String fieldName = nameFromAnnotation(field);
                            field.setAccessible(true);
                            if (List.class.isAssignableFrom(field.getType())) {
                                String tableName = nameFromAnnotation(obj.getClass());
                                field.set(obj, field.getType().cast(
                                        buildListFromResults(
                                                tableName,
                                                nameFromAnnotation(annotation.containsType()),
                                                _result.getLong("rowid"),
                                                annotation.containsType())
                                        )
                                );
                            }
                            else {
                                try {
                                    field.set(obj, field.getType().cast(_result.getObject(fieldName)));
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    ls.add(obj);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InvalidClassException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ls;
    }

    private <T> ArrayList<T> buildListFromResults(String _parentTable, String _childName, long _parentId, Class<T> _listField) throws SQLException {
        var joinTableName = _childName + "_" + _parentTable + "_join_table";
        String query = "SELECT * FROM " + joinTableName  + " INNER JOIN " + _childName + " ON " + _childName + ".rowid = " + joinTableName + ".child " + " WHERE parent=? ;";
        var statement = connection.prepareStatement(query);
        statement.setLong(1, _parentId);
        var lsResult = statement.executeQuery();

        var ls = new ArrayList<T>();
        try {
            while (lsResult.next()) {
                try {
                    var obj = _listField.getDeclaredConstructor().newInstance();
                    for (var field : _listField.getDeclaredFields()) {
                        var annotation = field.getAnnotation(Column.class);
                        if(annotation != null) {
                            String fieldName = annotation.name().equals("") ? field.getName() : annotation.name();
                            field.setAccessible(true);
                            var value = lsResult.getObject(fieldName);
                            field.set(obj, value);
                        }
                    }
                    ls.add(_listField.cast(obj));
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ls;
    }

    /**
     * Create a SQLite insert query string
     *
     * @param _tableName the name of the table to insert into
     * @param _fields    the fields to insert
     * @return the query string
     */
    private static String createInsertString(String _tableName, Map<String, ColumnData> _fields) {
        var builder = new StringBuilder("INSERT OR REPLACE INTO ")
                .append(_tableName)
                .append(" (");
        for (var field : _fields.entrySet()) {
            if (!field.getValue().isList())
                builder.append(field.getKey())
                        .append(',');
        }
        builder.deleteCharAt(builder.lastIndexOf(","));
        builder.append(") VALUES (");
        _fields.entrySet().stream().filter((a) -> !a.getValue().isList()).forEach((a) -> {
            builder.append("?,");
        });
        builder.deleteCharAt(builder.lastIndexOf(","));
        builder.append("); ");
        return builder.toString();
    }

    /**
     * Creates a table in the database based on a class
     *
     * @param _name   the name of the table
     * @param _fields the columns in the table
     * @throws SQLException if it fails to create the table
     */
    private static String createTableString(String _name, Map<String, ColumnData> _fields) throws SQLException {
        if (queryCache.containsKey(_name))
            return queryCache.get(_name);
        else {
            var builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
                    .append(_name)
                    .append(" (\n");
            for (var field : _fields.entrySet()) {
                var columnData = field.getValue();
                if (!columnData.isList()) {
                    builder.append(field.getKey())
                            .append(" ")
                            .append(convertToSqliteType(columnData.getType()))
                            .append(" ");
                    if (columnData.isNotNull())
                        builder.append("NOT NULL ");
                    if (columnData.isUnique())
                        builder.append("UNIQUE ");
                    if (columnData.isPrimaryKey())
                        builder.append("PRIMARY KEY");
                    builder.append(",\n");
                }
            }
            builder.append("active INTEGER DEFAULT 1\n");
            builder.append("); ");
            var query = builder.toString();
            queryCache.put(_name, query);
            return query;
        }
    }

    @Override
    public void deactivate(Object _table) {
        var annotation = _table.getClass().getAnnotation(Table.class);
        if (annotation == null)
            throw new DatabaseError("attempted to deactivate non-table object");
        String tableName = annotation.name().equals("") ? _table.getClass().getName() : annotation.name();
        var builder = new StringBuilder("UPDATE " + tableName + " SET active=0 WHERE ");
        try {
            // create query string
            var columns = getColumns(_table);
            for (var column : columns.entrySet()) {
                if (column.getValue().getData() != null)
                    builder.append(column.getKey())
                            .append("=? AND ");
            }
            builder.append("active=1");

            // execute statement
            var preparedStatement = this.connection.prepareStatement(builder.toString());
            int i = 0;
            for (var data : columns.values()) {
                if (data.getData() != null)
                    preparedStatement.setObject(i++, data.getData());
            }
            preparedStatement.execute();
        } catch (IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
    }

    // test function to make sure query creation works
    public static String testCreateQuery(Object _table) {
        var annotation = _table.getClass().getAnnotation(Table.class);
        if (annotation == null)
            throw new DatabaseError("Attempted to use class that is not a table!");
        try {
            var fields = Database.getColumns(_table);
            var tableName = annotation.name().equals("") ? _table.getClass().getName() : annotation.name();
            return createTableString(tableName, fields);
        } catch (IllegalAccessException | SQLException e) {
            throw new DatabaseError("Unable to read field from table object! cause: " + e.getMessage());
        }
    }


    /**
     * Convert a java type to the SQLite data type that will represent it in the database
     *
     * @param _type the java type to convert
     * @return string representing the SQLite type
     */
    private static String convertToSqliteType(Type _type) {
        if (_type.equals(String.class))
            return "TEXT";
        if (_type.equals(Double.class)
                || _type.equals(Float.class)
                || _type.equals(float.class)
                || _type.equals(double.class))
            return "REAL";
        if (_type.equals(Integer.class)
                || _type.equals(int.class)
                || _type.equals(Long.class)
                || _type.equals(long.class)
                || _type.equals(Short.class)
                || _type.equals(short.class)
                || _type.equals(Byte.class)
                || _type.equals(byte.class)
                || _type.equals(Boolean.class)
                || _type.equals(boolean.class))
            return "INTEGER";

        return "";
    }

}
