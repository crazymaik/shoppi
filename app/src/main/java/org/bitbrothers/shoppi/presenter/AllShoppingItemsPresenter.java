package org.bitbrothers.shoppi.presenter;


import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.store.ShoppingItemRepository;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AllShoppingItemsPresenter extends BasePresenter<AllShoppingItemsPresenter.View> {

    public interface View extends BasePresenter.BaseView {

        void removeShoppingItem(ShoppingItem shoppingItem);

        void showShoppingItems(List<ShoppingItem> shoppingItems);

        void updateShoppingItem(ShoppingItem shoppingItem);
    }

    private final ShoppingItemRepository shoppingItemRepository;

    public AllShoppingItemsPresenter(ShoppingItemRepository shoppingItemRepository) {
        this.shoppingItemRepository = shoppingItemRepository;
    }

    @Override
    public void attach(View view) {
        super.attach(view);
        retrieveShoppingItems();
    }

    public void deleteShoppingItem(ShoppingItem shoppingItem) {
        shoppingItemRepository.delete(shoppingItem.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    if (view != null) {
                        view.removeShoppingItem(shoppingItem);
                    }
                }, error -> {

                });
    }

    public void markBought(ShoppingItem shoppingItem) {
        shoppingItemRepository.markBought(shoppingItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updatedShoppingItem -> {
                    if (view != null) {
                        view.updateShoppingItem(updatedShoppingItem);
                    }
                }, error -> {
                    if (view != null) {
                        view.updateShoppingItem(shoppingItem);
                    }
                });
    }

    public void unmarkBought(ShoppingItem shoppingItem) {
        shoppingItemRepository.unmarkBought(shoppingItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updatedShoppingItem -> {
                    if (view != null) {
                        view.updateShoppingItem(updatedShoppingItem);
                    }
                }, error -> {
                    if (view != null) {
                        view.updateShoppingItem(shoppingItem);
                    }
                });
    }

    private void retrieveShoppingItems() {
        shoppingItemRepository.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItems -> {
                    if (view != null) {
                        view.showShoppingItems(shoppingItems);
                    }
                }, error -> {
                    // TODO
                });
    }
}
