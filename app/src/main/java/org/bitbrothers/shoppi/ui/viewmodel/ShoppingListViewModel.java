package org.bitbrothers.shoppi.ui.viewmodel;

import android.databinding.ObservableArrayList;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.logging.Logger;
import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.store.CategoryRepository;
import org.bitbrothers.shoppi.store.ShoppingItemRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ShoppingListViewModel extends BaseViewModel<ShoppingListViewModel.View> {

    public interface View extends BaseViewModel.BaseView {

        void showMarkBoughtFailed(ShoppingItem shoppingItem);
    }

    public final ObservableArrayList<ShoppingItem> shoppingItems = new ObservableArrayList<>();

    private final ShoppingItemRepository shoppingItemRepository;
    private final CategoryRepository categoryRepository;

    public ShoppingListViewModel(Logger logger, ShoppingItemRepository shoppingItemRepository, CategoryRepository categoryRepository) {
        super(logger);
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
                    logError("shopping_list_shopping_item_added", error);
                }));

        addViewDisposable(shoppingItemRepository.getOnItemUpdatedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    retrieveShoppingItems();
                }, error -> {
                    logError("shopping_list_shopping_item_updated", error);
                }));

        addViewDisposable(shoppingItemRepository.getOnItemRemovedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> {
                    removeShoppingItemFromList(id);
                }, error -> {
                    logError("shopping_list_shopping_item_removed", error);
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
                    logError("shopping_list_shopping_item_bought_changed", error);
                }));

        addViewDisposable(categoryRepository.getOnItemUpdatedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    retrieveShoppingItems();
                }, error -> {
                    logError("shopping_list_category_updated", error);
                }));

        addViewDisposable(categoryRepository.getOnItemRemovedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    retrieveShoppingItems();
                }, error -> {
                    logError("shopping_list_category_removed", error);
                }));

        retrieveShoppingItems();
    }

    public void markBought(ShoppingItem shoppingItem) {
        shoppingItemRepository.markBought(shoppingItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItems -> {
                }, error -> {
                    logError("shopping_list_marking_bought", error);
                    withView(view -> view.showMarkBoughtFailed(shoppingItem));
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
                    logError("shopping_list_retrieving_shopping_items", error);
                    withView(view -> view.showErrorToast(R.string.shopping_list_error_retrieving_shopping_items));
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
