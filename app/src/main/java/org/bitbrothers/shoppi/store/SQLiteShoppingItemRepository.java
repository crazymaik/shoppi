package org.bitbrothers.shoppi.store;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.bitbrothers.shoppi.model.ShoppingItem;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;

public class SQLiteShoppingItemRepository implements ShoppingItemRepository {

    private final SQLiteOpenHelper sqliteOpenHelper;

    public SQLiteShoppingItemRepository(SQLiteOpenHelper sqLiteOpenHelper) {
        this.sqliteOpenHelper = sqLiteOpenHelper;
    }

    @Override
    public Single<ShoppingItem> create(ShoppingItem shoppingItem) {
        return Single.create((SingleEmitter<Long> emitter) -> {
            long id;

            try (SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase()) {
                ContentValues values = new ContentValues();
                values.put("name", shoppingItem.getName());
                values.put("bought", shoppingItem.isBought() ? 1 : 0);

                id = db.insert("shopping_items", null, values);

            }

            emitter.onSuccess(id);
        }).flatMap((Long id) -> get(id));
    }

    @Override
    public Completable delete(long id) {
        return Completable.create(emitter -> {
            try (SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase()) {
                int deleteCount = db.delete("shopping_items", "id = ?", new String[]{"" + id});

                if (deleteCount != 1) {
                    emitter.onError(new RuntimeException());
                }
            }
            emitter.onComplete();
        });
    }

    @Override
    public Single<ShoppingItem> get(long id) {
        return Single.create(emitter -> {
            try (SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
                 Cursor cursor = db.query("shopping_items", new String[]{"id", "name", "bought"}, "id = ?", new String[]{"" + id}, null, null, null)) {

                if (!cursor.moveToNext()) {
                    emitter.onError(new RuntimeException());
                }

                emitter.onSuccess(new ShoppingItem(cursor.getLong(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getInt(cursor.getColumnIndex("bought")) == 1));
            }
        });
    }

    @Override
    public Single<List<ShoppingItem>> getAll() {
        return Single.create(emitter -> {
            try (SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
                 Cursor cursor = db.query("shopping_items", new String[]{"id", "name", "bought"}, null, null, null, null, "name collate nocase asc")) {
                List<ShoppingItem> items = cursorToList(cursor);
                emitter.onSuccess(items);
            }
        });
    }

    @Override
    public Single<List<ShoppingItem>> getUnbought() {
        return Single.create(emitter -> {
            try (SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
                 Cursor cursor = db.query("shopping_items", new String[]{"id", "name", "bought"}, "bought = 0", null, null, null, "name collate nocase asc")) {
                List<ShoppingItem> items = cursorToList(cursor);
                emitter.onSuccess(items);
            }
        });
    }

    @Override
    public Single<ShoppingItem> markBought(ShoppingItem shoppingItem) {
        return Single.create((SingleEmitter<Long> emitter) -> {
            try (SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase()) {
                ContentValues values = new ContentValues();
                values.put("bought", true);
                int updateCount = db.update("shopping_items", values, "id = ?", new String[]{"" + shoppingItem.getId()});
                if (updateCount != 1) {
                    emitter.onError(new RuntimeException());
                }
                emitter.onSuccess(shoppingItem.getId());
            }
        }).flatMap(id -> get(id));
    }

    @Override
    public Single<ShoppingItem> unmarkBought(ShoppingItem shoppingItem) {
        return Single.create((SingleEmitter<Long> emitter) -> {
            try (SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase()) {
                ContentValues values = new ContentValues();
                values.put("bought", false);
                int updateCount = db.update("shopping_items", values, "id = ?", new String[]{"" + shoppingItem.getId()});
                if (updateCount != 1) {
                    emitter.onError(new RuntimeException());
                }
                emitter.onSuccess(shoppingItem.getId());
            }
        }).flatMap(id -> get(id));
    }

    private List<ShoppingItem> cursorToList(Cursor cursor) {
        List<ShoppingItem> items = new ArrayList<>();

        if (!cursor.isBeforeFirst()) {
            throw new RuntimeException();
        }

        int columnId = cursor.getColumnIndex("id");
        int columnName = cursor.getColumnIndex("name");
        int columnBought = cursor.getColumnIndex("bought");

        while (cursor.moveToNext()) {
            items.add(new ShoppingItem(cursor.getLong(columnId),
                    cursor.getString(columnName),
                    cursor.getInt(columnBought) == 1));
        }

        return items;
    }
}
