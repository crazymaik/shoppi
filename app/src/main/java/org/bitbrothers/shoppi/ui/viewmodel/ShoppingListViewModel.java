package org.bitbrothers.shoppi.ui.viewmodel;

import android.databinding.ObservableArrayList;

import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.store.CategoryRepository;
import org.bitbrothers.shoppi.store.ShoppingItemRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ShoppingListViewModel extends BaseViewModel<ShoppingListViewModel.View> {

    public interface View extends BaseViewModel.BaseView {
    }

    public final ObservableArrayList<ShoppingItem> shoppingItems = new ObservableArrayList<>();

    private final ShoppingItemRepository shoppingItemRepository;
    private final CategoryRepository categoryRepository;

    public ShoppingListViewModel(ShoppingItemRepository shoppingItemRepository, CategoryRepository categoryRepository) {
        this.shoppingItemRepository = shoppingItemRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void attach(View view) {
        super.attach(view);

        addViewDisposable(shoppingItemRepository.getOnItemAddedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    retrieveShoppingItems();
                }, error -> {

                }));

        addViewDisposable(shoppingItemRepository.getOnItemRemovedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> {
                    removeShoppingItemFromList(id);
                }, error -> {

                }));

        addViewDisposable(shoppingItemRepository.getOnItemBoughtStateChangedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    if (shoppingItem.isBought()) {
                        removeShoppingItemFromList(shoppingItem.getId());
                    } else {
                        retrieveShoppingItems();
                    }
                }, error -> {

                }));

        addViewDisposable(categoryRepository.getOnItemUpdatedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    retrieveShoppingItems();
                }, error -> {

                }));

        addViewDisposable(categoryRepository.getOnItemRemovedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    retrieveShoppingItems();
                }, error -> {

                }));

        retrieveShoppingItems();
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

    private void removeShoppingItemFromList(long id) {
        for (int i = 0; i < shoppingItems.size(); ++i) {
            if (shoppingItems.get(i).getId() == id) {
                shoppingItems.remove(i);
                break;
            }
        }
    }
}
