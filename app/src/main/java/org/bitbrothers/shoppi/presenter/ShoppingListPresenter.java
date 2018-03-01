package org.bitbrothers.shoppi.presenter;


import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.store.ShoppingItemRepository;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ShoppingListPresenter extends BasePresenter<ShoppingListPresenter.View> {

    public interface View extends BasePresenter.BaseView {

        void showShoppingItems(List<ShoppingItem> shoppingItems);

        void removeShoppingItem(long id);
    }

    private final ShoppingItemRepository shoppingItemRepository;
    private Disposable onItemAddedDisposable;
    private Disposable onItemRemovedDisposable;
    private Disposable onItemBoughtStateChangedDisposable;

    public ShoppingListPresenter(ShoppingItemRepository shoppingItemRepository) {
        this.shoppingItemRepository = shoppingItemRepository;
    }

    @Override
    public void attach(View view) {
        super.attach(view);

        retrieveShoppingItems();

        onItemAddedDisposable = shoppingItemRepository.getOnItemAddedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    retrieveShoppingItems();
                }, error -> {

                });

        onItemRemovedDisposable = shoppingItemRepository.getOnItemRemovedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> {
                    if (view != null) {
                        view.removeShoppingItem(id);
                    }
                }, error -> {

                });

        onItemBoughtStateChangedDisposable = shoppingItemRepository.getOnItemBoughtStateChangedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    if (shoppingItem.isBought()) {
                        view.removeShoppingItem(shoppingItem.getId());
                    } else {
                        retrieveShoppingItems();
                    }
                }, error -> {

                });
    }

    @Override
    public void detach() {
        onItemAddedDisposable.dispose();
        onItemRemovedDisposable.dispose();
        onItemBoughtStateChangedDisposable.dispose();
        super.detach();
    }

    public void markBought(ShoppingItem shoppingItem) {
        shoppingItemRepository.markBought(shoppingItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItems -> {
                }, error -> {

                });
    }

    private void retrieveShoppingItems() {
        shoppingItemRepository.getUnbought()
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
