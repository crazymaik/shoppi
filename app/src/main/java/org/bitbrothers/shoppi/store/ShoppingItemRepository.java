package org.bitbrothers.shoppi.store;

import org.bitbrothers.shoppi.model.ShoppingItem;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface ShoppingItemRepository {

    Single<ShoppingItem> create(ShoppingItem shoppingItem);

    Completable delete(long id);

    Single<ShoppingItem> get(long id);

    Single<List<ShoppingItem>> getAll();
}
