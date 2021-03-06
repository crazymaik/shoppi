package org.bitbrothers.shoppi.store;

import org.bitbrothers.shoppi.model.Category;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface CategoryRepository {

    Single<Category> create(Category category);

    Single<Category> update(Category category);

    Completable delete(long id);

    Single<Category> get(long id);

    Single<List<Category>> getAll();

    Single<Integer> getAssignedShoppingItemsCount(long id);

    Observable<Category> getOnItemAddedObservable();

    Observable<Category> getOnItemUpdatedObservable();

    Observable<Long> getOnItemRemovedObservable();
}
