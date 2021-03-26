import annotations.Column;
import annotations.Table;
import database.sqlite.SqliteDatabase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class QueryTest {
    private static Connection db;

    @Table(name = "TestTable")
    private static class TestTable {
        @Column(primaryKey = true)
        private int id;

        @Column(name = "username")
        private String name;
    }

    @BeforeAll
    static void setupTest() {
        try {
            db = DriverManager.getConnection("jdbc:sqlite::memory:");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("create table query string creation test")
    public void testQuery() throws SQLException {
        var tableObject = new TestTable();
        var query = SqliteDatabase.testCreateQuery(tableObject);
        var expected =
                "CREATE TABLE IF NOT EXISTS TestTable (\n" +
                        "id INTEGER PRIMARY KEY,\n" +
                        "username TEXT \n" +
                        ");";
        assert (query.equals(expected));
        var statement = db.createStatement();
        statement.execute(query);
        statement.close();
        statement = db.createStatement();
        var results = statement.executeQuery(
                "SELECT " +
                        "name" +
                    " FROM " +
                        "sqlite_master" +
                    " WHERE " +
                        "type='table' AND name LIKE 'TestTable'");

        assert (results.getString(1).equals("TestTable"));
    }


}
