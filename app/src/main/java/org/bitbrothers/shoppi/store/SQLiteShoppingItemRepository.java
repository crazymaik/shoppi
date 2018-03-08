package org.bitbrothers.shoppi.store;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.bitbrothers.shoppi.model.ShoppingItem;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class SQLiteShoppingItemRepository implements ShoppingItemRepository {

    private final SQLiteOpenHelper sqliteOpenHelper;

    private final Subject<ShoppingItem> onItemAddedSubject;
    private final Subject<Long> onItemRemovedSubject;
    private final Subject<ShoppingItem> onItemBoughtStateChangedSubject;

    public SQLiteShoppingItemRepository(SQLiteOpenHelper sqLiteOpenHelper) {
        this.sqliteOpenHelper = sqLiteOpenHelper;
        this.onItemAddedSubject = PublishSubject.<ShoppingItem>create().toSerialized();
        this.onItemRemovedSubject = PublishSubject.<Long>create().toSerialized();
        this.onItemBoughtStateChangedSubject = PublishSubject.<ShoppingItem>create().toSerialized();
    }

    @Override
    public Single<ShoppingItem> create(ShoppingItem shoppingItem) {
        return Single.create((SingleEmitter<Long> emitter) -> {
            long id;

            try (SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase()) {
                ContentValues values = new ContentValues();
                values.put("name", shoppingItem.getName());
                values.put("bought", shoppingItem.isBought() ? 1 : 0);
                values.put("category_id", (Long) null);

                id = db.insert("shopping_items", null, values);
            }

            emitter.onSuccess(id);
        }).flatMap((Long id) -> get(id)).doOnSuccess(onItemAddedSubject::onNext);
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
        }).doOnComplete(() -> onItemRemovedSubject.onNext(id));
    }

    @Override
    public Single<ShoppingItem> get(long id) {
        return Single.create(emitter -> {
            try (SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
                 Cursor cursor = db.query("shopping_items", new String[]{"id", "name", "bought", "category_id"}, "id = ?", new String[]{"" + id}, null, null, null)) {

                if (!cursor.moveToNext()) {
                    emitter.onError(new RuntimeException());
                }

                emitter.onSuccess(new ShoppingItem(cursor.getLong(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getInt(cursor.getColumnIndex("bought")) == 1,
                        cursor.getLong(cursor.getColumnIndex("category_id"))));
            }
        });
    }

    @Override
    public Single<List<ShoppingItem>> getAll() {
        return Single.create(emitter -> {
            try (SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
                 Cursor cursor = db.query("shopping_items", new String[]{"id", "name", "bought", "category_id"}, null, null, null, null, "name collate nocase asc")) {
                List<ShoppingItem> items = cursorToList(cursor);
                emitter.onSuccess(items);
            }
        });
    }

    @Override
    public Single<List<ShoppingItem>> getUnbought() {
        return Single.create(emitter -> {
            try (SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
                 Cursor cursor = db.query("shopping_items", new String[]{"id", "name", "bought", "category_id"}, "bought = 0", null, null, null, "name collate nocase asc")) {
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
        }).flatMap(id -> get(id)).doOnSuccess(onItemBoughtStateChangedSubject::onNext);
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
        }).flatMap(id -> get(id)).doOnSuccess(onItemBoughtStateChangedSubject::onNext);
    }

    @Override
    public Observable<ShoppingItem> getOnItemAddedObservable() {
        return onItemAddedSubject;
    }

    @Override
    public Observable<Long> getOnItemRemovedObservable() {
        return onItemRemovedSubject;
    }

    @Override
    public Observable<ShoppingItem> getOnItemBoughtStateChangedObservable() {
        return onItemBoughtStateChangedSubject;
    }

    private List<ShoppingItem> cursorToList(Cursor cursor) {
        List<ShoppingItem> items = new ArrayList<>();

        if (!cursor.isBeforeFirst()) {
            throw new RuntimeException();
        }

        int columnId = cursor.getColumnIndex("id");
        int columnName = cursor.getColumnIndex("name");
        int columnBought = cursor.getColumnIndex("bought");
        int columntCategoryId = cursor.getColumnIndex("category_id");

        while (cursor.moveToNext()) {
            items.add(new ShoppingItem(cursor.getLong(columnId),
                    cursor.getString(columnName),
                    cursor.getInt(columnBought) == 1,
                    cursor.getLong(columntCategoryId)));
        }

        return items;
    }
}
