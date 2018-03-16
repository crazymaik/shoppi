package org.bitbrothers.shoppi.store;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.bitbrothers.shoppi.BuildConfig;

public class SQLiteOpenHelper extends android.database.sqlite.SQLiteOpenHelper {

    public SQLiteOpenHelper(Context context, String name) {
        super(context, name, null, 2);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table categories (id integer primary key, name text not null, color integer not null);");
        db.execSQL("create table shopping_items (id integer primary key, name text not null, bought integer not null, category_id integer references categories(id) on delete set null);");

        if (BuildConfig.FEATURE_PREFILL_CATEGORIES) {
            db.execSQL("insert into categories (name, color) values ('Beverages', 0xff1616e5), ('Bread & Bakery', 0xffe57e16), ('Candy', 0xffe516e5), ('Canned Goods', 0xff8ecc51), ('Cleaning & Home', 0xff7e16e5), ('Dairy', 0xffe5e516), ('Frozen Foods', 0xff5151cc), ('Fruits & Vegetables', 0xff16e516), ('Meat & Seafood', 0xffe51616), ('Personal Care', 0xff16e5e5);");
        }
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
