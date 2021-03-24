package exported;

import database.Database;
import database.sqlite.SqliteDatabase;

public class DatabaseManager {
    private static Database database;


    public static void connectToDatabase(String _filepath) {
        database = new SqliteDatabase(_filepath);
    }
}
