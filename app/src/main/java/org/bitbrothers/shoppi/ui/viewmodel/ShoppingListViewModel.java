package org.bitbrothers.shoppi.ui.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.logging.Logger;
import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.store.CategoryRepository;
import org.bitbrothers.shoppi.store.ShoppingItemRepository;

public class ShoppingListViewModel extends BaseViewModel<ShoppingListViewModel.View> {

    public interface View extends BaseViewModel.BaseView {

        void showMarkBoughtFailed(ShoppingItem shoppingItem);
    }

    public final ObservableArrayList<ShoppingItem> shoppingItems = new ObservableArrayList<>();

    public final ObservableBoolean showEmptyListMessage = new ObservableBoolean(false);

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
                .compose(applyObservableSchedulers())
                .subscribe(shoppingItem -> {
                    retrieveShoppingItems();
                }, error -> {
                    logError("shopping_list_item_added", error);
                }));

        addViewDisposable(shoppingItemRepository.getOnItemUpdatedObservable()
                .compose(applyObservableSchedulers())
                .subscribe(shoppingItem -> {
                    retrieveShoppingItems();
                }, error -> {
                    logError("shopping_list_item_updated", error);
                }));

        addViewDisposable(shoppingItemRepository.getOnItemRemovedObservable()
                .compose(applyObservableSchedulers())
                .subscribe(id -> {
                    removeShoppingItemFromList(id);
                }, error -> {
                    logError("shopping_list_item_removed", error);
                }));

        addViewDisposable(shoppingItemRepository.getOnItemBoughtStateChangedObservable()
                .compose(applyObservableSchedulers())
                .subscribe(shoppingItem -> {
                    if (shoppingItem.isBought()) {
                        removeShoppingItemFromList(shoppingItem.getId());
                    } else {
                        retrieveShoppingItems();
                    }
                }, error -> {
                    logError("shopping_list_bought_changed", error);
                }));

        addViewDisposable(categoryRepository.getOnItemUpdatedObservable()
                .compose(applyObservableSchedulers())
                .subscribe(shoppingItem -> {
                    retrieveShoppingItems();
                }, error -> {
                    logError("shopping_list_category_updated", error);
                }));

        addViewDisposable(categoryRepository.getOnItemRemovedObservable()
                .compose(applyObservableSchedulers())
                .subscribe(shoppingItem -> {
                    retrieveShoppingItems();
                }, error -> {
                    logError("shopping_list_category_removed", error);
                }));

        retrieveShoppingItems();
    }

    public void markBought(ShoppingItem shoppingItem) {
        shoppingItemRepository.markBought(shoppingItem)
                .compose(applySingleSchedulers())
                .subscribe(shoppingItems -> {
                }, error -> {
                    logError("shopping_list_marking_bought", error);
                    withView(view -> view.showMarkBoughtFailed(shoppingItem));
                });
    }

    private void retrieveShoppingItems() {
        shoppingItemRepository.getUnboughtOrderedByCategories()
                .compose(applySingleSchedulers())
                .subscribe(shoppingItems -> {
                    this.shoppingItems.clear();
                    this.shoppingItems.addAll(shoppingItems);
                    this.showEmptyListMessage.set(shoppingItems.isEmpty());
                }, error -> {
                    logError("shopping_list_retrieving_items", error);
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
        this.showEmptyListMessage.set(shoppingItems.isEmpty());
    }
}
