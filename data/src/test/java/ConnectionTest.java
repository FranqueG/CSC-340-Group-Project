import manager.DatabaseManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ConnectionTest {

    @Test
    @DisplayName("Testing ability to create and connect to a database")
    public void connectionTest() throws IOException {
        DatabaseManager.connectToDatabase(":memory:");
    }
}
