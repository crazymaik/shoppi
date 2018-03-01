package org.bitbrothers.shoppi.store;

import org.bitbrothers.shoppi.model.ShoppingItem;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface ShoppingItemRepository {

    Single<ShoppingItem> create(ShoppingItem shoppingItem);

    Completable delete(long id);

    Single<ShoppingItem> get(long id);

    Single<List<ShoppingItem>> getAll();

    Single<List<ShoppingItem>> getUnbought();

    Single<ShoppingItem> markBought(ShoppingItem shoppingItem);

    Single<ShoppingItem> unmarkBought(ShoppingItem shoppingItem);

    Observable<ShoppingItem> getOnItemAddedObservable();

    Observable<Long> getOnItemRemovedObservable();
}
