package org.bitbrothers.shoppi.store;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteOpenHelper extends android.database.sqlite.SQLiteOpenHelper {

    public SQLiteOpenHelper(Context context, String name) {
        super(context, name, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table categories (id integer primary key, name text not null, color integer not null);");
        db.execSQL("create table shopping_items (id integer primary key, name text not null, bought integer not null, category_id integer references categories(id) on delete set null);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // We assume that newVersion is always higher than oldVersion

        if (oldVersion < 2) {
            db.execSQL("create table categories (id integer primary key, name text not null, color integer not null);");
            db.execSQL("alter table shopping_items add category_id integer references categories(id) on delete set null;");
        }
    }
}
