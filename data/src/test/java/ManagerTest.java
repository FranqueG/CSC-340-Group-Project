import annotations.Column;
import annotations.Table;
import manager.DatabaseManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class ManagerTest {


    @Table(name = "TestTable")
    public static class TestTable {
        @Column(name = "username")
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


    @BeforeAll
    public static void setup() throws IOException {
        DatabaseManager.connectToDatabase();
    }

    @Test
    public void RunTest() throws ExecutionException, InterruptedException {
        var table = new TestTable();
        var ls = new ArrayList<TestListElement>();
        ls.add(new TestListElement("first",3412));
        ls.add(new TestListElement("Bla",1234));
        ls.add(new TestListElement("testing", 9876));
        table.list = ls;
        table.name = "This is a test";

        DatabaseManager.saveObject(table);

        var searchTable = new TestTable();
        var future = DatabaseManager.loadObject(searchTable);

        var result = future.get().get(0);
        System.out.println(result.toString());
        assert (result.equals(table));
    }


}
