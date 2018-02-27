package org.bitbrothers.shoppi.presenter;


import org.bitbrothers.shoppi.store.ShoppingItemRepository;

public class AllShoppingItemsPresenter extends BasePresenter<AllShoppingItemsPresenter.View> {

    public interface View extends BasePresenter.BaseView {

    }

    private final ShoppingItemRepository shoppingItemRepository;

    public AllShoppingItemsPresenter(ShoppingItemRepository shoppingItemRepository) {
        this.shoppingItemRepository = shoppingItemRepository;
    }
}
