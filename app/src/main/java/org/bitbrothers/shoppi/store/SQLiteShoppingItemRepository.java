package org.bitbrothers.shoppi.store;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.bitbrothers.shoppi.model.ShoppingItem;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;

public class SQLiteShoppingItemRepository implements ShoppingItemRepository {

    private final SQLiteOpenHelper sqliteOpenHelper;

    public SQLiteShoppingItemRepository(SQLiteOpenHelper sqLiteOpenHelper) {
        this.sqliteOpenHelper = sqLiteOpenHelper;
    }

    @Override
    public Single<ShoppingItem> create(ShoppingItem shoppingItem) {
        return Single.create((SingleOnSubscribe<Long>) emitter -> {
            long id;

            try (SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase()) {
                ContentValues values = new ContentValues();
                values.put("name", shoppingItem.getName());

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
                 Cursor cursor = db.query("shopping_items", new String[]{"id", "name"}, "id = ?", new String[]{"" + id}, null, null, null)) {

                if (!cursor.moveToNext()) {
                    emitter.onError(new RuntimeException());
                }

                emitter.onSuccess(new ShoppingItem(cursor.getLong(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("name"))));
            }
        });
    }

    @Override
    public Single<List<ShoppingItem>> getAll() {
        return Single.create(emitter -> {
            List<ShoppingItem> items = new ArrayList<>();
            try (SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
                 Cursor cursor = db.query("shopping_items", new String[]{"id", "name"}, null, null, null, null, "name collate nocase asc")) {

                if (!cursor.isBeforeFirst()) {
                    emitter.onError(new RuntimeException());
                }

                while (cursor.moveToNext()) {
                    items.add(new ShoppingItem(cursor.getLong(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("name"))));
                }
            }
            emitter.onSuccess(items);
        });
    }
}
