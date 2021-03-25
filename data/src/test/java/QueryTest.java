import annotations.Column;
import annotations.Table;
import database.sqlite.SqliteDatabase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class QueryTest {

    @Table
    private static class TestTable {
        @Column(primaryKey = true)
        private int id;

        @Column(name = "username")
        private String name;
    }

    @Test
    @DisplayName("create table query string creation test")
    public void testQuery() {
        var tableObject = new TestTable();
        var query = SqliteDatabase.testCreateQuery(tableObject);
        var expected = "CREATE TABLE IF NOT EXISTS QueryTest$TestTable (\n" +
                           "id null PRIMARY KEY,\n" +
                           "username TEXT ,\n" +
                           ");";
        assert(query.equals(expected));
    }


}
