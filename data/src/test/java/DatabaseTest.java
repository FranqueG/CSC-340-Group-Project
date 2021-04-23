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
        private String name = "bob";

        @Column(unique = true)
        private String uuid;

        @Table(name="nestedTable")
        private static class NestedTable {

            @Column(unique = true)
            private String uuid;

            @Column
            private String name = "bla";

        }

        @Column
        private NestedTable nested;

        @Column(containsType = NestedTable.class)
        private ArrayList<NestedTable> ls = new ArrayList<>();
    }



    @BeforeAll
    public static void setup() {
        //db = new SqliteDatabase(":memory:");
        db = new SqliteDatabase(System.getProperty("user.home") + "/test_db.sqlite3");
    }

    @Test
    @DisplayName("Bulk save test")
    public void bulkSaveTest() {
        List<TestTable> testTables = new ArrayList<>();
        for (int i=0;i<10;i++) {
            var table = new TestTable();
            table.nested = new TestTable.NestedTable();
            table.ls.add(new TestTable.NestedTable());
            table.ls.add(new TestTable.NestedTable());
            table.ls.add(new TestTable.NestedTable());
            testTables.add(table);
        }
        db.saveObjects(testTables);
    }

}
