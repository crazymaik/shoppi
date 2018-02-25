package org.bitbrothers.shoppi.support;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.bitbrothers.shoppi.store.SQLiteOpenHelper;
import org.junit.rules.ExternalResource;

/**
 * Rule to remove and always retrieve a fresh test database file.
 */
public class DatabaseRule extends ExternalResource {

    public static final String DATABASE_NAME = "test.db";

    private SQLiteOpenHelper sqliteOpenHelper;

    /**
     * Returns the SQLiteOpenHelper using a test database file.
     */
    public SQLiteOpenHelper getSQLiteOpenHelper() {
        return sqliteOpenHelper;
    }

    @Override
    protected void before() throws Throwable {
        deleteDatabaseFiles();
        sqliteOpenHelper = new SQLiteOpenHelper(InstrumentationRegistry.getTargetContext(), DATABASE_NAME);
    }

    @Override
    protected void after() {
        sqliteOpenHelper.close();
        deleteDatabaseFiles();
    }

    private void deleteDatabaseFiles() {
        Context context = InstrumentationRegistry.getTargetContext();
        context.deleteDatabase(DATABASE_NAME);
    }
}
