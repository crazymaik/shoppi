package org.bitbrothers.shoppi.ui.viewmodel;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.logging.Logger;
import org.bitbrothers.shoppi.model.Category;
import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.store.CategoryRepository;
import org.bitbrothers.shoppi.store.ShoppingItemRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AllShoppingItemsViewModel extends BaseViewModel<AllShoppingItemsViewModel.View> {

    public interface View extends BaseViewModel.BaseView {
    }

    public final ObservableArrayList<ShoppingItem> shoppingItems = new ObservableArrayList<>();
    public final ObservableArrayList<Category> categories = new ObservableArrayList<>();
    public final ObservableBoolean addContainerButtonEnabled = new ObservableBoolean(false);
    public final ObservableField<String> addContainerName = new ObservableField<>("");
    public final ObservableInt addContainerCategoryPosition = new ObservableInt(-1);
    public final ObservableBoolean addContainerVisible = new ObservableBoolean(false);

    private final Context context;
    private final ShoppingItemRepository shoppingItemRepository;
    private final CategoryRepository categoryRepository;

    public AllShoppingItemsViewModel(Context context, Logger logger, ShoppingItemRepository shoppingItemRepository, CategoryRepository categoryRepository) {
        super(logger);
        this.context = context;
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
                    logError("all_shopping_items_shopping_item_added", error);
                }));

        addViewDisposable(shoppingItemRepository.getOnItemUpdatedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    retrieveShoppingItems();
                }, error -> {
                    logError("all_shopping_items_shopping_item_updated", error);
                }));

        addViewDisposable(shoppingItemRepository.getOnItemRemovedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> {
                    removeShoppingItemFromList(id);
                }, error -> {
                    logError("all_shopping_items_shopping_item_removed", error);
                }));

        addViewDisposable(shoppingItemRepository.getOnItemBoughtStateChangedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    updateShoppingItemInList(shoppingItem);
                }, error -> {
                    logError("all_shopping_items_shopping_item_bought_changed", error);
                }));

        addViewDisposable(categoryRepository.getOnItemUpdatedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    retrieveShoppingItems();
                }, error -> {
                    logError("all_shopping_items_category_updated", error);
                }));

        addViewDisposable(categoryRepository.getOnItemRemovedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    retrieveShoppingItems();
                }, error -> {
                    logError("all_shopping_items_category_removed", error);
                }));

        retrieveShoppingItems();
    }

    public void openAddShoppingItemView() {
        addContainerButtonEnabled.set(false);
        categoryRepository.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categories -> {
                    this.categories.clear();
                    this.categories.add(new Category(0l, context.getString(R.string.category_unassigned_name), 0xff000000));
                    this.categories.addAll(categories);
                    addContainerButtonEnabled.set(true);
                    addContainerName.set("");
                    addContainerCategoryPosition.set(categories.isEmpty() ? -1 : 0);
                    addContainerVisible.set(true);
                }, error -> {
                    logError("all_shopping_items_getall", error);
                    withView(view -> view.showErrorToast(R.string.all_shopping_items_error_retrieving_categories_for_add));
                });
    }

    public void addShoppingItem() {
        String name = addContainerName.get();
        Category category = getSelectedCategory();
        addContainerButtonEnabled.set(false);
        addContainerName.set("");
        shoppingItemRepository.create(new ShoppingItem(name, category))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    addContainerButtonEnabled.set(true);
                }, error -> {
                    logError("all_shopping_items_create", error);
                    withView(view -> view.showErrorToast(R.string.all_shopping_items_error_adding_item));
                    addContainerButtonEnabled.set(true);
                });
    }

    public void deleteShoppingItem(long shoppingItemId) {
        shoppingItemRepository.delete(shoppingItemId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                }, error -> {
                    logError("all_shopping_items_delete", error);
                    withView(view -> view.showErrorToast(R.string.all_shopping_items_error_deleting_item));
                });
    }

    public void markBought(ShoppingItem shoppingItem) {
        shoppingItemRepository.markBought(shoppingItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updatedShoppingItem -> {
                }, error -> {
                    logError("all_shopping_items_markbought", error);
                    withView(view -> view.showErrorToast(R.string.all_shopping_items_error_mark_bought));
                });
    }

    public void unmarkBought(ShoppingItem shoppingItem) {
        shoppingItemRepository.unmarkBought(shoppingItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updatedShoppingItem -> {
                }, error -> {
                    logError("all_shopping_items_unmarkbought", error);
                    withView(view -> view.showErrorToast(R.string.all_shopping_items_error_unmark_bought));
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
                    logError("all_shopping_items_retrieving", error);
                    withView(view -> view.showErrorToast(R.string.all_shopping_items_error_retrieving_shopping_items));
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

    private Category getSelectedCategory() {
        int position = addContainerCategoryPosition.get();
        if (position <= 0) {
            return new Category(null, "", 0);
        }
        return categories.get(position);
    }
}
