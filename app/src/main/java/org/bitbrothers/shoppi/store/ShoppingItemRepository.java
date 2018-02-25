package org.bitbrothers.shoppi.store;

import org.bitbrothers.shoppi.model.ShoppingItem;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface ShoppingItemRepository {

    Single<ShoppingItem> create(ShoppingItem shoppingItem);

    Observable<Void> delete(long id);

    Single<ShoppingItem> get(long id);

    Single<List<ShoppingItem>> getAll();
}
