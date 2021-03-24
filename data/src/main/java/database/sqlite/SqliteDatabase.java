package database.sqlite;
/*
Last updated: 3/23/2021
This class represents a connection to a SQLite database and is responsible for
saving and loading data to the disk through it.
Authors: Joshua Millikan
 */

import database.Database;
import exported.DatabaseError;
import exported.Table;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SqliteDatabase extends Database {
    private final Connection connection;
    private static Map<String, String> queryCache = new HashMap<>();

    public SqliteDatabase(String _filepath) {
        super();
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:"+_filepath);
        } catch (SQLException e) {
            throw new DatabaseError("Failed to establish connection to database! cause: "+e.getMessage());
        }
    }

    @Override
    public void Insert(Object _table) {
        var annotation = _table.getClass().getAnnotation(Table.class);
        if (annotation == null)
            throw new DatabaseError("Attempted to use class that is not a table!");
        try {
            var fields = Database.getColumns(_table);
            var tableName = annotation.name().equals("") ? _table.getClass().getName() : annotation.name();
            createTable(tableName,fields);

        } catch (IllegalAccessException | SQLException e) {
            throw new DatabaseError("Unable to read field from table object! cause: "+e.getMessage());
        }
    }

    private void createTable(String _name, Map<String, ColumnData> _fields) throws SQLException {
        String query;
        if (queryCache.containsKey(_name))
            query = queryCache.get(_name);
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
            builder.append(");");
            query = builder.toString();
            queryCache.put(_name, query);
        }
        var statement = connection.createStatement();
        statement.execute(query);
    }


    private static String convertToSqliteType(Type _type) {
        //TODO
        return null;
    }

}