package manager;
/*
Last updated: 3/23/2021
This class manages access to the database
 */

import database.Database;
import database.sqlite.SqliteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

public final class DatabaseManager {
    private static Database database;

    /**
     * Open a connection to the database file at the given filepath
     * @param _filepath path to the database file
     */
    public static void connectToDatabase(String _filepath) {
        database = new SqliteDatabase(_filepath);
    }

    /**
     * Open a connection to the database with the default path of
     * (user home directory)/MtgDeckBuilder/database.sqlite3
     */
    public static void connectToDatabase() {
        database = new SqliteDatabase(System.getProperty("user.home")+"/MtgDeckBuilder/database.sqlite3");
    }

    /**
     * Save a object to the database.
     *
     * The object must be of a class annotated @Table,
     * and only fields annotated @Column will be saved.
     *
     * @see annotations.Table
     * @see annotations.Column
     * @param _obj the object to save to the database
     */
    public static void saveObject(Object _obj) {
        database.saveObject(_obj);
    }

    /**
     * Save a collection of objects to the database.
     *
     * All objects in the collection must be of classes annotated @Table,
     * and only fields annotated @Column will be saved.
     *
     * @see annotations.Table
     * @see annotations.Column
     * @param _objs the collection of objects to save
     */
    public static void saveObjects(Collection<?> _objs) {
        database.saveObjects(_objs);
    }

    /**
     * Load an object from the database.
     * <p>
     *  All non-null fields that are annotated @Column of the object passed as a parameter
     *  will be used as parameters to search the database with, and all records in the database
     *  that have matching fields will be constructed into java objects and returned
     *  as part of the list returned by this method. The parameter itself must be
     *  non-null, but all fields can be null if you wish to select all records that
     *  match that type.
     * </p>
     *
     * @see annotations.Table
     * @see annotations.Column
     * @param _obj the object to use a filter
     * @param <T> the type of the object returned
     * @return A future promise that will contain the list when the database operation completes
     */
    public static <T> Future<List<T>> loadObject(final T _obj) {
        return database.loadObject(_obj);
    }

    /**
     * Load multiple objects from the database
     * <p>
     *  Each object in the collection parameter will be used to search the database,
     *  each object's non-null fields that are annotated @Column will be used a search
     *  parameters in the database. A list of objects will constructed for each object in
     *  in the collection containing new java objects representing those records that
     *  matched the object. A list of futures is returned that will contain these lists once
     *  the database operation is completed.
     * </p>
     *
     * @see annotations.Table
     * @see annotations.Column
     * @param _objs objects to use a filters
     * @param <T> the type of the objects returned
     * @return A array list of futures which will contain the list of objects for each returned
     * for each object passed as a parameter parameter once the operation is completes.
     */
    @SafeVarargs//TODO make sure the varargs is a actually safe
    public static <T>ArrayList<Future<List<T>>> loadObjects(final T... _objs) {
        return database.loadObjects(Arrays.asList(_objs));
    }

    private DatabaseManager() {throw new UnsupportedOperationException("This class should not be instantiated");}
}
