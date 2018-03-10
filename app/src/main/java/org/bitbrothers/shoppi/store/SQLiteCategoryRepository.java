package org.bitbrothers.shoppi.store;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.bitbrothers.shoppi.model.Category;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class SQLiteCategoryRepository implements CategoryRepository {

    private final SQLiteOpenHelper sqliteOpenHelper;

    private final Subject<Category> onItemAddedSubject;
    private final Subject<Category> onItemUpdatedSubject;
    private final Subject<Long> onItemRemovedSubject;

    public SQLiteCategoryRepository(SQLiteOpenHelper sqLiteOpenHelper) {
        this.sqliteOpenHelper = sqLiteOpenHelper;
        this.onItemAddedSubject = PublishSubject.<Category>create().toSerialized();
        this.onItemUpdatedSubject = PublishSubject.<Category>create().toSerialized();
        this.onItemRemovedSubject = PublishSubject.<Long>create().toSerialized();
    }

    @Override
    public Single<Category> create(Category category) {
        return Single.create((SingleEmitter<Long> emitter) -> {
            long id;

            try (SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase()) {
                ContentValues values = new ContentValues();
                values.put("name", category.getName());
                values.put("color", category.getColor());

                id = db.insert("categories", null, values);
            }

            emitter.onSuccess(id);
        }).flatMap((Long id) -> get(id)).doOnSuccess(onItemAddedSubject::onNext);
    }

    @Override
    public Single<Category> update(Category category) {
        return Single.create((SingleEmitter<Long> emitter) -> {
            try (SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase()) {
                ContentValues values = new ContentValues();
                values.put("name", category.getName());
                values.put("color", category.getColor());

                int rowCount = db.update("categories", values, "id = ?", new String[]{"" + category.getId()});

                if (rowCount != 1) {
                    throw new RuntimeException();
                }
            }
            emitter.onSuccess(category.getId());
        }).flatMap((Long id) -> get(id)).doOnSuccess(onItemUpdatedSubject::onNext);
    }

    @Override
    public Completable delete(long id) {
        return Completable.create(emitter -> {
            try (SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase()) {
                int deleteCount = db.delete("categories", "id = ?", new String[]{"" + id});

                if (deleteCount != 1) {
                    emitter.onError(new RuntimeException());
                }
            }
            emitter.onComplete();
        }).doOnComplete(() -> onItemRemovedSubject.onNext(id));
    }

    @Override
    public Single<Category> get(long id) {
        return Single.create(emitter -> {
            try (SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
                 Cursor cursor = db.query("categories", new String[]{"id", "name", "color"}, "id = ?", new String[]{"" + id}, null, null, null)) {

                if (!cursor.moveToNext()) {
                    emitter.onError(new RuntimeException());
                }

                emitter.onSuccess(new Category(cursor.getLong(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getInt(cursor.getColumnIndex("color"))));
            }
        });
    }

    @Override
    public Single<List<Category>> getAll() {
        return Single.create(emitter -> {
            try (SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
                 Cursor cursor = db.query("categories", new String[]{"id", "name", "color"}, null, null, null, null, "name collate nocase asc")) {
                List<Category> items = cursorToList(cursor);
                emitter.onSuccess(items);
            }
        });
    }

    @Override
    public Single<Integer> getAssignedShoppingItemsCount(long categoryId) {
        return Single.create(emitter -> {
            try (SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
                 Cursor cursor = db.query("shopping_items", new String[]{"count(*)"}, "category_id = ?", new String[]{"" + categoryId}, null, null, null)) {

                if (!cursor.moveToNext()) {
                    emitter.onError(new RuntimeException());
                }

                emitter.onSuccess(cursor.getInt(0));
            }
        });
    }

    @Override
    public Observable<Category> getOnItemAddedObservable() {
        return onItemAddedSubject;
    }

    @Override
    public Observable<Category> getOnItemUpdatedObservable() {
        return onItemUpdatedSubject;
    }

    @Override
    public Observable<Long> getOnItemRemovedObservable() {
        return onItemRemovedSubject;
    }

    private List<Category> cursorToList(Cursor cursor) {
        List<Category> items = new ArrayList<>();

        if (!cursor.isBeforeFirst()) {
            throw new RuntimeException();
        }

        int columnId = cursor.getColumnIndex("id");
        int columnName = cursor.getColumnIndex("name");
        int columnColor = cursor.getColumnIndex("color");

        while (cursor.moveToNext()) {
            items.add(new Category(cursor.getLong(columnId),
                    cursor.getString(columnName),
                    cursor.getInt(columnColor)));
        }

        return items;
    }
}
