package org.bitbrothers.shoppi.ui.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableInt;

import org.bitbrothers.shoppi.model.Category;
import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.store.CategoryRepository;
import org.bitbrothers.shoppi.store.ShoppingItemRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AllShoppingItemsViewModel extends ViewModel {

    public final ObservableArrayList<ShoppingItem> shoppingItems = new ObservableArrayList<>();
    public final ObservableArrayList<Category> categories = new ObservableArrayList<>();
    public final ObservableInt addContainerVisibility = new ObservableInt(android.view.View.GONE);

    private final ShoppingItemRepository shoppingItemRepository;
    private final CategoryRepository categoryRepository;
    private Disposable onShoppingItemAddedDisposable;
    private Disposable onShoppingItemRemovedDisposable;
    private Disposable onShoppingItemBoughtStateChangedDisposable;
    private Disposable onCategoryUpdatedDisposable;
    private Disposable onCategoryRemovedDisposable;

    public AllShoppingItemsViewModel(ShoppingItemRepository shoppingItemRepository, CategoryRepository categoryRepository) {
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
                    removeShoppingItemFromList(id);
                }, error -> {

                });

        onShoppingItemBoughtStateChangedDisposable = shoppingItemRepository.getOnItemBoughtStateChangedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    updateShoppingItemInList(shoppingItem);
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

    public void openAddShoppingItemView() {
        categoryRepository.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categories -> {
                    this.categories.clear();
                    this.categories.addAll(categories);
                    addContainerVisibility.set(android.view.View.VISIBLE);
                }, error -> {
                });
    }

    public void addShoppingItem(String name, Category category) {
        shoppingItemRepository.create(new ShoppingItem(name, category))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {

                }, error -> {

                });
    }

    public void deleteShoppingItem(ShoppingItem shoppingItem) {
        shoppingItemRepository.delete(shoppingItem.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                }, error -> {
                });
    }

    public void markBought(ShoppingItem shoppingItem) {
        shoppingItemRepository.markBought(shoppingItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updatedShoppingItem -> {
                }, error -> {
                });
    }

    public void unmarkBought(ShoppingItem shoppingItem) {
        shoppingItemRepository.unmarkBought(shoppingItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updatedShoppingItem -> {
                }, error -> {
                });
    }

    private void retrieveShoppingItems() {
        shoppingItemRepository.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItems -> {
                    this.shoppingItems.clear();
                    this.shoppingItems.addAll(shoppingItems);
                }, error -> {
                    // TODO
                });
    }

    private void removeShoppingItemFromList(long id) {
        for (int i = 0; i < shoppingItems.size(); ++i) {
            if (shoppingItems.get(i).getId() == id) {
                shoppingItems.remove(i);
                break;
            }
        }
    }

    private void updateShoppingItemInList(ShoppingItem shoppingItem) {
        for (int i = 0; i < shoppingItems.size(); ++i) {
            if (shoppingItems.get(i).getId().equals(shoppingItem.getId())) {
                shoppingItems.set(i, shoppingItem);
                break;
            }
        }
    }
}
