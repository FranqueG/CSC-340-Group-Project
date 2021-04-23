import annotations.Table;
import manager.DatabaseManager;
import org.junit.jupiter.api.BeforeAll;

public class ManagerTest {


    @Table(name = "TestTable")
    private static class TestTable {

    }

    private static class TestListElement {

    }


    @BeforeAll
    public static void setup() {
        DatabaseManager.connectToDatabase(":memory:");
    }


}
