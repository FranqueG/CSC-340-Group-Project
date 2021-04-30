import annotations.Column;
import annotations.Table;
import database.Database;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InvalidClassException;
import java.util.ArrayList;

public class ReflectionTest extends Database {

    @SuppressWarnings("FieldMayBeFinal")
    @Table
    private static class TestExample {
        @Column(name = "Number")
        private int something = 42;

        @Column
        private String someString = "Test";

        @Column(primaryKey = true)
        private int primaryKeyColumn = 1;

        private int notColumn = 0;
    }

    @Override
    protected ArrayList<Object> selectFromDatabase(Object _obj) {
        return null;
    }

    @Override
    public long updateInsert(Object _table) { /* Do nothing */return 0;}

    @Override
    public void deactivate(Object _table) {
        // do nothing
    }

    @Override
    public void drop(Class<?> _c) throws InvalidClassException {
        // do nothing
    }

    /**
     * Basic test the shows that the database
     * reflection system works
     * @throws IllegalAccessException if reflection access fails
      */
    @Test
    @DisplayName("Testing ability to read fields in a table class")
    public void reflectionTest() throws IllegalAccessException {
        var exampleTable = new TestExample();
        var columns = Database.getColumns(exampleTable);

        assert (columns.containsKey("Number"));
        assert (columns.containsKey("someString"));
        assert (columns.containsKey("primaryKeyColumn"));
        assert (!columns.containsKey("notColumn"));

        assert (columns.get("Number").getData().equals(42));
        assert (columns.get("someString").getData().equals("Test"));
        assert (columns.get("primaryKeyColumn").isPrimaryKey());

    }
}
