package manager;
/*
Last updated: 3/23/2021
This class manages access to the database
 */

import database.Database;
import database.sqlite.SqliteDatabase;

public class DatabaseManager {
    private static Database database;


    public static void connectToDatabase(String _filepath) {
        database = new SqliteDatabase(_filepath);
    }
}
