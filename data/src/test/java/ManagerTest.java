import annotations.Column;
import annotations.Table;
import errors.DatabaseError;
import manager.DatabaseManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ManagerTest {

    /**
     * Connects to the database
     * @throws IOException if the database file cannot be found/created
     */
    @BeforeAll
    public static void setup() throws IOException {
        DatabaseManager.connectToDatabase();
        DatabaseManager.testClearDatabase(TestTable.class,TestListElement.class,BasicTable.class);
    }

    /**
     * Cleans up the database
     */
    @AfterAll
    public static void cleanup() {
        DatabaseManager.shutdownDatabase();
    }

    /**
     * Performs the most basic tests of saving and loading simple objects
     * @throws ExecutionException if loading fails
     * @throws InterruptedException if loading is interrupted
     */
    @Test
    public void basicTest() throws ExecutionException, InterruptedException {
        var table = new BasicTable();
        table.num = 34;
        table.notSaved = "bla";
        DatabaseManager.saveObject(table);
        var result = DatabaseManager.loadObject(new BasicTable()).get().get(0);
        assert(result.num.equals(table.num));
        assert(!table.notSaved.equals(result.notSaved));
    }

    /**
     * This test tests the primary functionality of the database, saving and loading complex objects
     * @throws ExecutionException if loading fails
     * @throws InterruptedException if loading is interrupted
     */
    @Test
    public void primaryTest() throws ExecutionException, InterruptedException {
        var table = new TestTable();
        var ls = new ArrayList<TestListElement>();
        ls.add(new TestListElement("first",3412));
        ls.add(new TestListElement("Bla",1234));
        ls.add(new TestListElement("testing", 9876));
        table.list = ls;
        table.name = "This is a test";
        table.notSaved = "not important";

        DatabaseManager.saveObject(table);

        var searchTable = new TestTable();
        var future = DatabaseManager.loadObject(searchTable);

        //test the object loaded has the same column fields as the original
        var result = future.get().get(0);
        assert (result.equals(table));
        //test that non-columns are not saved
        assert(!table.notSaved.equals(result.notSaved));

        //test updating behavior
        table.list.clear();
        table.list.add(new TestListElement("bla",12));
        DatabaseManager.saveObject(table);

        //confirm that the update has worked
        future = DatabaseManager.loadObject(searchTable);
        result = future.get().get(0);
        assert (result.equals(table));

        table.list.clear();
        table.name = "bulk";
        searchTable.name = "bulk";
        //bulk list test
        for(int i=0;i<100;i++)
            table.list.add(new TestListElement("bla",i));
        DatabaseManager.saveObject(table);
        result = DatabaseManager.loadObject(searchTable).get().get(0);
        var secondCopy = DatabaseManager.loadObject(searchTable).get().get(0);
        assert (result.equals(table));
        assert (result.equals(secondCopy));
    }

    /**
     * This tests that deleting something does actually deactivate the record
     * and that loading will not return it
     */
    @Test
    public void deleteTest() throws ExecutionException, InterruptedException {
        var table = new BasicTable();
        table.num = 42;
        DatabaseManager.saveObject(table);
        DatabaseManager.deleteObject(table);
        var result = DatabaseManager.loadObject(table).get();
        assert(result.size() == 0);
    }



    /**
     * This tests that the database will throw the expected errors in case
     * of invalid input
     */
    @Test
    public void errorTest() {
        //ensure the database rejects null objects
        assertThrows(DatabaseError.class, ()-> DatabaseManager.saveObject(null));
        //ensure the database rejects non-table objects
        assertThrows(DatabaseError.class, ()-> DatabaseManager.saveObject(new NotATable()));
        //ensure that a malformed table object will be rejected
        assertThrows(DatabaseError.class, ()->DatabaseManager.saveObject(new MalformedTable()));
    }

    @Table(name = "TestTable")
    public static class TestTable {
        @Column(name = "username", unique = true)
        public String name = null;
        @Column(containsType = TestListElement.class)
        public ArrayList<TestListElement> list = null;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestTable testTable = (TestTable) o;
            return Objects.equals(name, testTable.name) && Objects.equals(list, testTable.list);
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append(name)
                    .append("\n")
                    .append("list: ");
            b.append(list);
            return b.toString();
        }

        public String notSaved;

    }

    @Table(name = "ListElement")
    public static class TestListElement {
        @Column
        public String name;
        @Column
        public Integer foo;

        public TestListElement(String name, int foo) {
            this.name = name;
            this.foo = foo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestListElement that = (TestListElement) o;
            return foo.equals(that.foo) && Objects.equals(name, that.name);
        }

        public TestListElement() {
            name = null;
            foo = 0;
        }

        @Override
        public String toString() {
            return "TestListElement{" +
                    "name='" + name + '\'' +
                    ", foo=" + foo +
                    '}';
        }
    }

    @Table(name="BasicTable")
    public static class BasicTable {
        @Column
        public Integer num;

        public String notSaved;
    }

    @Table(name = "MalformedTable")
    public static class MalformedTable {
        @Column
        public int bad;
        @Column
        public String foo;
    }

    private static class NotATable { }

}
