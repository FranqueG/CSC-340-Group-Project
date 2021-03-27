package manager;
/*
Last updated: 3/23/2021
This class manages access to the database
 */

import database.Database;
import database.sqlite.SqliteDatabase;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class DatabaseManager {
    private static Database database;
    private static final Object monitor = new Object();
    private static final ConcurrentLinkedDeque<Object> objectQueue;


    public static void connectToDatabase(String _filepath) {
        database = new SqliteDatabase(_filepath);
    }

    public static void insert(List<Object> _objects) {
        objectQueue.addAll(_objects);
        synchronized (monitor) {monitor.notify();}
    }

    public static void insert(Object _obj) {
        insert(Collections.singletonList(_obj));
    }

    static {
        objectQueue = new ConcurrentLinkedDeque<>();
        Thread writerThread = new Thread(() -> {
            try {
                synchronized (monitor) {monitor.wait();}
                for (var obj : objectQueue)
                    database.insert(obj);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        writerThread.start();
    }

}
