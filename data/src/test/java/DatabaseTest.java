import annotations.Column;
import annotations.Table;
import database.Database;
import database.sqlite.SqliteDatabase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class DatabaseTest {
    private static Database db;

    @Table(name = "TestTable")
    private static class TestTable {
        @Column(primaryKey = true)
        private int id;

        @Column(name = "username")
        private String name;
    }

    @BeforeAll
    public static void setup() {
        db = new SqliteDatabase(":memory:");
    }

    @Test
    @DisplayName("Bulk save test")
    public void bulkSaveTest() {
        List<TestTable> testTables = new ArrayList<>();
        for (int i=0;i<100;i++) {
            testTables.add(new TestTable());
        }
        db.saveObjects(testTables);
    }

}
