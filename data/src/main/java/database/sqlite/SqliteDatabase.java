package database.sqlite;
/*
Last updated: 3/23/2021
This class represents a connection to a SQLite database and is responsible for
saving and loading data to the disk through it.
Authors: Joshua Millikan
 */
import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import database.Database;

public class SqliteDatabase extends Database {
    private Connection connection;

    private static List<String> baseTableCreateStrings;

    static {
        baseTableCreateStrings = new ArrayList<>();
        var tableData = Database.getTableData();
        for (var table : tableData.entrySet()) {
            var builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
            builder.append(table.getKey()).append(" (");
            for (var column : table.getValue()) {
                builder.append(column.getName())
                        .append(" ")
                        .append(convertToSqliteType(column.getType()))
                        .append(" ");
                if (column.isNotNull())
                    builder.append("NOT NULL ");
                if (column.isUnique())
                    builder.append("UNIQUE ");
                builder.append(',');
            }
            builder.append(");");
            baseTableCreateStrings.add(builder.toString());
        }
    }

    private static String convertToSqliteType(Type _type) {
        //TODO
        return null;
    }

    public SqliteDatabase(String _filepath) {
        super();
    }


}
