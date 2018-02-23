package org.bitbrothers.shoppi.store;

import org.bitbrothers.shoppi.model.ShoppingItem;

import java.util.List;

public interface ShoppingItemRepository {

    ShoppingItem create(ShoppingItem shoppingItem);

    void delete(long id);

    ShoppingItem get(long id);

    List<ShoppingItem> getAll();
}
