package database.sqlite;
/*
Last updated: 3/23/2021
This class represents a connection to a SQLite database and is responsible for
saving and loading data to the disk through it.
Authors: Joshua Millikan
 */

import annotations.Table;
import database.Database;
import errors.DatabaseError;

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
    protected <T> List<T> selectFromDatabase(T _obj) {
        var annotation = _obj.getClass().getAnnotation(Table.class);
        if (annotation == null)
            throw new DatabaseError("Attempted to use class that is not a table!");
        try {
            Map<String, ColumnData> searchFields = new HashMap<>();
            List<String> resultFields = new ArrayList<>();
            for (var field : Database.getColumns(_obj).entrySet()) {
                if (field.getValue() == null)
                    resultFields.add(field.getKey());
                else
                    searchFields.put(field.getKey(), field.getValue());
            }
            var statement = connection.prepareStatement(createSelectString(annotation.name(), searchFields, resultFields));
            int i = 0;
            for (var field : searchFields.values())
                statement.setObject(i++, field.getData());
            ResultSet result = statement.executeQuery();
            return buildFromResults(result);
        } catch (IllegalAccessException | SQLException e) {
            throw new DatabaseError("Unable to read field from table object! cause: " + e.getMessage());
        }
    }

    @Override
    protected int updateInsert(Object _table) {
        var annotation = _table.getClass().getAnnotation(Table.class);
        if (annotation == null)
            throw new DatabaseError("Attempted to use class that is not a table!");
        try {
            var fields = Database.getColumns(_table);

            // create the table if it doesn't already exist
            String tableName = annotation.name().equals("") ? _table.getClass().getName() : annotation.name();
            String createSting = createTableString(tableName, fields);
            Statement statement = connection.createStatement();
            statement.execute(createSting);

            // handle nested tables
            for (var field : fields.entrySet()) {
                if (field.getValue().isNestedTable()) {
                    Integer id = updateInsert(field.getValue().getData());
                    field.setValue(new ColumnData(
                            id,
                            Integer.TYPE,
                            true,
                            false,
                            false,
                            false,
                            false));
                }
            }

            // Perform insertion statement
            String insertString = createInsertString(tableName, fields);
            PreparedStatement preparedStatement = connection.prepareStatement(insertString);
            int i = 1;
            for (var field : fields.values()) {
                preparedStatement.setObject(i, field.getData());
                i++;
            }
            int rowId = preparedStatement.executeUpdate();
            fields.values().forEach((field) -> createList(field, rowId));
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
    private void createList(ColumnData _listData, int _owningTable) {
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
                Statement statement = connection.createStatement();
                statement.execute(tableString);
                statement.execute("DELETE FROM " + tableName + "_join_table WHERE parent=" + _owningTable);

                //create join table linking list and the elements
                String createString = "CREATE TABLE IF NOT EXISTS " + tableName +
                        "_join_table (" +
                        "parent INTEGER NOT NULL," +
                        " INTEGER child NOT NULL," +
                        " INTEGER count)";
                statement.execute(createString);

                for (Object element : ls) {

                    // insert the actual record
                    int child = updateInsert(element);

                    //update the join tables for the list
                    var preparedStatement = connection.prepareStatement(createListInsertStatement(tableName + "_join_table"));
                    preparedStatement.setInt(0, _owningTable);
                    preparedStatement.setInt(1, child);
                    preparedStatement.setInt(0, _owningTable);
                    preparedStatement.setInt(1, child);
                    preparedStatement.setInt(0, _owningTable);
                    preparedStatement.setInt(1, child);
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
     * @param _tableName the join table name
     * @return the query string
     */
    private static String createListInsertStatement(String _tableName) {
        return "CASE WHEN EXISTS(" +
                "SELECT * FROM  " +
                _tableName +
                "WHERE parent=? AND child=?)\n" +
                "THEN\n UPDATE " +
                _tableName +
                " SET count=count+1 WHERE parent=? AND child=?\n" +
                "ELSE\n INSERT INTO " +
                _tableName +
                " (parent,child,count) VALUES (?, ?, 1)";
    }

    /**
     * Create the SQL select statement
     *
     * @param _tableName    name of the table to search
     * @param _searchFields fields to use as search parameters
     * @param _resultFields fields to get from the result
     * @return the SQL query
     */
    private static String createSelectString(String _tableName, Map<String, ColumnData> _searchFields, List<String> _resultFields) {
        var builder = new StringBuilder("SELECT * FROM").append(_tableName);
        StringBuilder tail = new StringBuilder();
        if (_searchFields.size() > 0) {
            builder.append(" WHERE ");
            for (var field : _searchFields.entrySet()) {
                if (field.getValue().isList()) {
                    tail.append(createSelectListString(_tableName, field.getValue()));
                } else
                    builder.append(field.getKey())
                            .append("=? AND ");
            }
            builder.replace(builder.lastIndexOf("AND"), builder.lastIndexOf("AND") + 3, " ");
        }
        builder.append(tail);
        return builder.toString();
    }

    private static String createSelectListString(String _tableName, ColumnData _field) {
        List<?> ls = (List<?>) _field.getData();
        var annotation = ls.get(0).getClass().getAnnotation(Table.class);
        String listDataName = annotation.name().equals("") ? ls.get(0).getClass().getName() : annotation.name();
        return "INNER JOIN " + listDataName + "_join_table on " + listDataName + "_join_table.parent = " + _tableName + ".rowid\n"
                + "INNER JOIN " + listDataName + " on " + listDataName + ".rowid = " + listDataName + "_join_table.child\n";
    }

    /**
     * Constructs java objects from SQL results
     *
     * @param _result SQLite query result data
     * @return list of objects constructed
     */
    private static <T> List<T> buildFromResults(ResultSet _result) {
        return null;//todo
    }

    /**
     * Create a SQLite insert query string
     *
     * @param _tableName the name of the table to insert into
     * @param _fields    the fields to insert
     * @return the query string
     */
    private static String createInsertString(String _tableName, Map<String, ColumnData> _fields) {
        var builder = new StringBuilder("INSERT INTO ")
                .append(_tableName)
                .append(" (");
        for (String fieldName : _fields.keySet())
            builder.append(fieldName)
                    .append(',');

        builder.deleteCharAt(builder.lastIndexOf(","));
        builder.append(") VALUES (");
        for (var ignored : _fields.keySet())
            builder.append("?,");
        builder.deleteCharAt(builder.lastIndexOf(","));
        builder.append(')');
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
            builder.deleteCharAt(builder.lastIndexOf(","));
            builder.append("); ");
            var query = builder.toString();
            queryCache.put(_name, query);
            return query;
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
