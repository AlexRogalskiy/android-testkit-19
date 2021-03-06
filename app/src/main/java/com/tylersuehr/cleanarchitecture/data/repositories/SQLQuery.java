package com.tylersuehr.cleanarchitecture.data.repositories;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tylersuehr.cleanarchitecture.data.mappers.IEntityMapper;
import com.tylersuehr.cleanarchitecture.data.models.Entity;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2017 Tyler Suehr
 *
 * Utility to help with querying the local SQLite database.
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public final class SQLQuery {
    /**
     * Queries the SQLite database for a single object.
     * @param db {@link SQLiteDatabase}
     * @param mapper {@link IEntityMapper}
     * @param table Table name
     * @param where Where clause
     * @param callback {@link Callbacks.ISingle}
     */
    public static <T extends Entity> void query(
            SQLiteDatabase db,
            IEntityMapper<T> mapper,
            String table,
            String where,
            Callbacks.ISingle<T> callback) {
        Cursor c = null;
        try {
            c = db.query(table, null, where, null, null, null, null);
            c.moveToFirst();

            T object = mapper.map(c);
            callback.onSingleLoaded(object);
        } catch (Exception ex) {
            callback.onNotAvailable(ex);
        } finally {
            if (c != null) { c.close(); }
        }
    }

    /**
     * Queries the SQLite database for multiple objects.
     * @param db {@link SQLiteDatabase}
     * @param mapper {@link IEntityMapper}
     * @param table Table name
     * @param where Where clause
     * @param order OrderBy clause
     * @param limit Limit clause
     * @param callback {@link Callbacks.IList}
     */
    public static <T extends Entity> void query(
            SQLiteDatabase db,
            IEntityMapper<T> mapper,
            String table,
            String where,
            String order,
            String limit,
            Callbacks.IList<T> callback) {
        Cursor c = null;
        try {
            c = db.query(table, null, where, null, null, null, order, limit);
            if (!c.moveToFirst()) {
                throw new EmptyQueryException(table);
            }

            List<T> objects = new ArrayList<>(c.getCount());
            for (int i = 0; i < c.getCount(); i++) {
                objects.add(mapper.map(c));
                c.moveToNext();
            }

            callback.onListLoaded(objects);
        } catch (Exception ex) {
            callback.onNotAvailable(ex);
        } finally {
            if (c != null) { c.close(); }
        }
    }


    /**
     * Subclass of {@link RuntimeException} to be thrown when any query
     * in the SQLite database has no results.
     */
    public static final class EmptyQueryException extends RuntimeException {
        EmptyQueryException(String table) {
            super("Query in " + table + " yields no results!");
        }
    }
}