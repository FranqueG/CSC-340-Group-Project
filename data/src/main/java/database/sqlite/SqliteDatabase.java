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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SqliteDatabase extends Database {
    private final Connection connection;
    private static final Map<String, String> queryCache = new HashMap<>();

    /**
     * Opens a SQLite database connection
     * @param _filepath the path to the database file
     */
    public SqliteDatabase(String _filepath) {
        super();
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:"+_filepath);
        } catch (SQLException e) {
            throw new DatabaseError("Failed to establish connection to database! cause: "+e.getMessage());
        }
    }

    @Override
    public void insert(Object _table) {
        var annotation = _table.getClass().getAnnotation(Table.class);
        if (annotation == null)
            throw new DatabaseError("Attempted to use class that is not a table!");
        try {
            var fields = Database.getColumns(_table);
            var tableName = annotation.name().equals("") ? _table.getClass().getName() : annotation.name();
            var createSting = createTableString(tableName,fields);
            var statement = connection.createStatement();
            statement.execute(createSting);

        } catch (IllegalAccessException | SQLException e) {
            throw new DatabaseError("Unable to read field from table object! cause: "+e.getMessage());
        }
    }

    /**
     * Creates a table in the database based on a class
     * @param _name the name of the table
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
            builder.append(");");
            var query = builder.toString();
            queryCache.put(_name, query);
            return query;
        }
    }

    public static String testCreateQuery(Object _table) {
        var annotation = _table.getClass().getAnnotation(Table.class);
        if (annotation == null)
            throw new DatabaseError("Attempted to use class that is not a table!");
        try {
            var fields = Database.getColumns(_table);
            var tableName = annotation.name().equals("") ? _table.getClass().getName() : annotation.name();
            return createTableString(tableName,fields);
        } catch (IllegalAccessException | SQLException e) {
            throw new DatabaseError("Unable to read field from table object! cause: "+e.getMessage());
        }
    }


    private static String convertToSqliteType(Type _type) {
        if (_type.equals(Integer.class) || _type.equals(int.class))
            return "INTEGER";
        if (_type.equals(String.class))
            return "TEXT";
        if (_type.equals(Double.class) || _type.equals(Float.class))
            return "REAL";
        return null;

        //todo handle complex things
    }

}
