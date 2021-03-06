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
    private final Subject<ShoppingItem> onItemUpdatedSubject;
    private final Subject<Long> onItemRemovedSubject;
    private final Subject<ShoppingItem> onItemBoughtStateChangedSubject;

    public SQLiteShoppingItemRepository(SQLiteOpenHelper sqLiteOpenHelper) {
        this.sqliteOpenHelper = sqLiteOpenHelper;
        this.onItemAddedSubject = PublishSubject.<ShoppingItem>create().toSerialized();
        this.onItemUpdatedSubject = PublishSubject.<ShoppingItem>create().toSerialized();
        this.onItemRemovedSubject = PublishSubject.<Long>create().toSerialized();
        this.onItemBoughtStateChangedSubject = PublishSubject.<ShoppingItem>create().toSerialized();
    }

    @Override
    public Single<ShoppingItem> create(ShoppingItem shoppingItem) {
        return Single.create((SingleEmitter<Long> emitter) -> {
            ContentValues values = new ContentValues();
            values.put("name", shoppingItem.getName());
            values.put("bought", shoppingItem.isBought() ? 1 : 0);
            values.put("category_id", shoppingItem.getCategoryId());

            SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
            long id = db.insert("shopping_items", null, values);

            emitter.onSuccess(id);
        }).flatMap((Long id) -> get(id)).doOnSuccess(onItemAddedSubject::onNext);
    }

    @Override
    public Single<ShoppingItem> update(ShoppingItem shoppingItem) {
        return Single.create((SingleEmitter<Long> emitter) -> {
            ContentValues values = new ContentValues();
            values.put("name", shoppingItem.getName());
            values.put("bought", shoppingItem.isBought() ? 1 : 0);
            values.put("category_id", shoppingItem.getCategoryId());

            SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
            int rowCount = db.update("shopping_items", values, "id = ?", new String[]{"" + shoppingItem.getId()});

            if (rowCount != 1) {
                throw new RuntimeException();
            }

            emitter.onSuccess(shoppingItem.getId());
        }).flatMap((Long id) -> get(id)).doOnSuccess(onItemUpdatedSubject::onNext);
    }

    @Override
    public Completable delete(long id) {
        return Completable.create(emitter -> {
            SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
            int deleteCount = db.delete("shopping_items", "id = ?", new String[]{"" + id});

            if (deleteCount != 1) {
                throw new RuntimeException();
            }

            emitter.onComplete();
        }).doOnComplete(() -> onItemRemovedSubject.onNext(id));
    }

    @Override
    public Single<ShoppingItem> get(long id) {
        return Single.create(emitter -> {
            SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();

            try (Cursor cursor = db.rawQuery("select s.id, s.name, s.bought, s.category_id, c.color from shopping_items s left outer join categories c on s.category_id = c.id where s.id = ?", new String[]{"" + id})) {
                if (!cursor.moveToFirst()) {
                    throw new RuntimeException();
                }

                int columnColor = cursor.getColumnIndex("color");

                emitter.onSuccess(new ShoppingItem(cursor.getLong(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getInt(cursor.getColumnIndex("bought")) == 1,
                        cursor.getLong(cursor.getColumnIndex("category_id")),
                        cursor.isNull(columnColor) ? null : cursor.getInt(columnColor)));
            }
        });
    }

    @Override
    public Single<List<ShoppingItem>> getAll() {
        return Single.create(emitter -> {
            SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
            try (Cursor cursor = db.rawQuery("select s.id, s.name, s.bought, s.category_id, c.color from shopping_items s left outer join categories c on s.category_id = c.id order by s.name collate nocase asc", null)) {
                List<ShoppingItem> items = cursorToList(cursor);
                emitter.onSuccess(items);
            }
        });
    }

    @Override
    public Single<List<ShoppingItem>> getUnboughtOrderedByCategories() {
        return Single.create(emitter -> {
            SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
            try (Cursor cursor = db.rawQuery("select s.id, s.name, s.bought, s.category_id, c.color from shopping_items s left outer join categories c on s.category_id = c.id where s.bought = 0 order by c.name collate nocase asc", null)) {
                List<ShoppingItem> items = cursorToList(cursor);
                emitter.onSuccess(items);
            }
        });
    }

    @Override
    public Single<ShoppingItem> markBought(ShoppingItem shoppingItem) {
        return Single.create((SingleEmitter<Long> emitter) -> {
            ContentValues values = new ContentValues();
            values.put("bought", true);

            SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
            int updateCount = db.update("shopping_items", values, "id = ?", new String[]{"" + shoppingItem.getId()});

            if (updateCount != 1) {
                throw new RuntimeException();
            }

            emitter.onSuccess(shoppingItem.getId());
        }).flatMap(id -> get(id)).doOnSuccess(onItemBoughtStateChangedSubject::onNext);
    }

    @Override
    public Single<ShoppingItem> unmarkBought(ShoppingItem shoppingItem) {
        return Single.create((SingleEmitter<Long> emitter) -> {
            ContentValues values = new ContentValues();
            values.put("bought", false);

            SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
            int updateCount = db.update("shopping_items", values, "id = ?", new String[]{"" + shoppingItem.getId()});

            if (updateCount != 1) {
                throw new RuntimeException();
            }

            emitter.onSuccess(shoppingItem.getId());
        }).flatMap(id -> get(id)).doOnSuccess(onItemBoughtStateChangedSubject::onNext);
    }

    @Override
    public Observable<ShoppingItem> getOnItemAddedObservable() {
        return onItemAddedSubject;
    }

    @Override
    public Observable<ShoppingItem> getOnItemUpdatedObservable() {
        return onItemUpdatedSubject;
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
        int columnCategoryId = cursor.getColumnIndex("category_id");
        int columnColor = cursor.getColumnIndex("color");

        while (cursor.moveToNext()) {
            boolean hasCategory = !cursor.isNull(columnCategoryId);
            items.add(new ShoppingItem(cursor.getLong(columnId),
                    cursor.getString(columnName),
                    cursor.getInt(columnBought) == 1,
                    hasCategory ? cursor.getLong(columnCategoryId) : null,
                    hasCategory ? cursor.getInt(columnColor) : null));
        }

        return items;
    }
}
