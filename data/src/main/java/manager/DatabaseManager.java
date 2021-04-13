package manager;
/*
Last updated: 3/23/2021
This class manages access to the database
 */

import database.Database;
import database.sqlite.SqliteDatabase;

import java.util.List;
import java.util.concurrent.Future;

public class DatabaseManager {
    private static Database database;


    public static void connectToDatabase(String _filepath) {
        database = new SqliteDatabase(_filepath);
    }

    public static void saveObject(Object _obj) {
        database.saveObject(_obj);
    }

    public static <T> Future<List<T>> loadObject(T _obj) {
        return database.loadObject(_obj);
    }
}
