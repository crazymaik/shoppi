package org.bitbrothers.shoppi.ui.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableArrayList;

import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.store.CategoryRepository;
import org.bitbrothers.shoppi.store.ShoppingItemRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ShoppingListViewModel extends ViewModel {

    public final ObservableArrayList<ShoppingItem> shoppingItems = new ObservableArrayList<>();

    private final ShoppingItemRepository shoppingItemRepository;
    private final CategoryRepository categoryRepository;
    private Disposable onShoppingItemAddedDisposable;
    private Disposable onShoppingItemRemovedDisposable;
    private Disposable onShoppingItemBoughtStateChangedDisposable;
    private Disposable onCategoryUpdatedDisposable;
    private Disposable onCategoryRemovedDisposable;

    public ShoppingListViewModel(ShoppingItemRepository shoppingItemRepository, CategoryRepository categoryRepository) {
        this.shoppingItemRepository = shoppingItemRepository;
        this.categoryRepository = categoryRepository;
    }

    public void attach() {
        onShoppingItemAddedDisposable = shoppingItemRepository.getOnItemAddedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    retrieveShoppingItems();
                }, error -> {

                });

        onShoppingItemRemovedDisposable = shoppingItemRepository.getOnItemRemovedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> {
                    removeShoppingItem(id);
                }, error -> {

                });

        onShoppingItemBoughtStateChangedDisposable = shoppingItemRepository.getOnItemBoughtStateChangedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    if (shoppingItem.isBought()) {
                        removeShoppingItem(shoppingItem.getId());
                    } else {
                        retrieveShoppingItems();
                    }
                }, error -> {

                });

        onCategoryUpdatedDisposable = categoryRepository.getOnItemUpdatedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    retrieveShoppingItems();
                }, error -> {

                });

        onCategoryRemovedDisposable = categoryRepository.getOnItemRemovedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    retrieveShoppingItems();
                }, error -> {

                });

        retrieveShoppingItems();
    }

    public void detach() {
        onShoppingItemAddedDisposable.dispose();
        onShoppingItemRemovedDisposable.dispose();
        onShoppingItemBoughtStateChangedDisposable.dispose();
        onCategoryUpdatedDisposable.dispose();
        onCategoryRemovedDisposable.dispose();
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
        shoppingItemRepository.getUnboughtOrderedByCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItems -> {
                    this.shoppingItems.clear();
                    this.shoppingItems.addAll(shoppingItems);
                }, error -> {
                    // TODO
                });
    }

    private void removeShoppingItem(long id) {
        for (int i = 0; i < shoppingItems.size(); ++i) {
            if (shoppingItems.get(i).getId() == id) {
                shoppingItems.remove(i);
                break;
            }
        }
    }

}
