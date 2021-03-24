import annotations.Column;
import annotations.Table;
import database.Database;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ReflectionTest extends Database {
    @Table
    private static class TestExample {
        @Column(name = "Number")
        private int something = 42;
        @Column
        private String someString = "Test";
    }

    @Override
    public void Insert(Object _table) { // Do nothing
    }

    @Test
    @DisplayName("Testing ability to read fields in a table class")
    public void reflectionTest() throws IllegalAccessException {
        var exampleTable = new TestExample();
        var columns = Database.getColumns(exampleTable);

        assert(columns.containsKey("Number"));
        assert(columns.containsKey("someString"));
        assert(columns.get("Number").getData().equals(42));
        assert(columns.get("someString").getData().equals("Test"));
    }
}
