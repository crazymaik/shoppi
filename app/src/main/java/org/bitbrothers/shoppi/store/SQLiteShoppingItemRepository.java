package org.bitbrothers.shoppi.store;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.bitbrothers.shoppi.model.ShoppingItem;

import java.util.ArrayList;
import java.util.List;

public class SQLiteShoppingItemRepository implements ShoppingItemRepository {

    private final SQLiteOpenHelper sqliteOpenHelper;

    public SQLiteShoppingItemRepository(SQLiteOpenHelper sqLiteOpenHelper) {
        this.sqliteOpenHelper = sqLiteOpenHelper;
    }

    @Override
    public ShoppingItem create(ShoppingItem shoppingItem) {
        try (SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase()) {
            long id;

            ContentValues values = new ContentValues();
            values.put("name", shoppingItem.getName());

            id = db.insert("shopping_items", null, values);

            return get(id);
        }
    }

    @Override
    public void delete(long id) {
        try (SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase()) {
            int deleteCount = db.delete("shopping_items", "id = ?", new String[]{"" + id});

            if (deleteCount != 1) {
                // TODO
            }
        }
    }

    @Override
    public ShoppingItem get(long id) {
        try (SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
             Cursor cursor = db.query("shopping_items", new String[]{"id", "name"}, "id = ?", new String[]{"" + id}, null, null, null)) {

            if (!cursor.moveToNext()) {
                return null;
            }

            return new ShoppingItem(cursor.getLong(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("name")));
        }
    }

    @Override
    public List<ShoppingItem> getAll() {
        List<ShoppingItem> items = new ArrayList<>();
        try (SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
             Cursor cursor = db.query("shopping_items", new String[]{"id", "name"}, null, null, null, null, null)) {

            if (!cursor.isBeforeFirst()) {
                return null;
            }

            while (cursor.moveToNext()) {
                items.add(new ShoppingItem(cursor.getLong(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("name"))));
            }
        }
        return items;
    }
}
