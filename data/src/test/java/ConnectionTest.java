import exported.DatabaseManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ConnectionTest {

    @Test
    @DisplayName("Testing ability to create and connect to a database")
    public void connectionTest() {
        DatabaseManager.connectToDatabase(":memory:");
    }
}
