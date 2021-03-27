import annotations.Column;
import annotations.Table;
import manager.DatabaseManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ManagerTest {

    @Table(name = "TestTable")
    private static class TestTable {
        @Column(primaryKey = true)
        private int id;

        @Column(name = "username")
        private String name;
    }

    @BeforeAll
    public static void setup() {
        DatabaseManager.connectToDatabase(":memory:");
    }

    @Test
    @DisplayName("Test using insert through manager class")
    public void TestInsertUsage() {
        var testObj = new TestTable();
        DatabaseManager.insert(testObj);
        //todo
    }
}
