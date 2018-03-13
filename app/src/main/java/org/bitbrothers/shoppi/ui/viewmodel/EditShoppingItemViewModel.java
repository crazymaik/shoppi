package org.bitbrothers.shoppi.ui.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.util.Pair;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.logging.Logger;
import org.bitbrothers.shoppi.model.Category;
import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.store.CategoryRepository;
import org.bitbrothers.shoppi.store.ShoppingItemRepository;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class EditShoppingItemViewModel extends BaseViewModel<BaseViewModel.BaseView> {

    public final ObservableBoolean formFieldsEnabled = new ObservableBoolean(true);
    public final ObservableField<String> shoppingItemName = new ObservableField<>("");
    public final ObservableInt shoppingItemCategoryPosition = new ObservableInt(-1);
    public final ObservableBoolean saveButtonEnabled = new ObservableBoolean(false);
    public final ObservableArrayList<Category> categories = new ObservableArrayList<>();
    public final ObservableInt saveErrorVisibility = new ObservableInt(android.view.View.GONE);
    public final ObservableBoolean close = new ObservableBoolean(false);
    private final ShoppingItemRepository shoppingItemRepository;
    private final CategoryRepository categoryRepository;
    private ShoppingItem shoppingItem;

    public EditShoppingItemViewModel(Logger logger, ShoppingItemRepository shoppingItemRepository, CategoryRepository categoryRepository) {
        super(logger);
        this.shoppingItemRepository = shoppingItemRepository;
        this.categoryRepository = categoryRepository;
    }

    public void setEditMode(long shoppingItemId) {
        formFieldsEnabled.set(false);
        saveButtonEnabled.set(false);

        Single.zip(categoryRepository.getAll(), shoppingItemRepository.get(shoppingItemId), (categories, shoppingItem) -> Pair.create(categories, shoppingItem))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    this.shoppingItem = result.second;
                    this.categories.clear();
                    this.categories.addAll(result.first);

                    int position = -1;
                    for (int i = 0; i < this.categories.size(); ++i) {
                        if (this.categories.get(i).getId().equals(this.shoppingItem.getCategoryId())) {
                            position = i;
                            break;
                        }
                    }

                    shoppingItemName.set(shoppingItem.getName());
                    shoppingItemCategoryPosition.set(position);

                    formFieldsEnabled.set(true);
                    saveButtonEnabled.set(true);
                }, error -> {
                    logError("edit_shopping_item_retrieving_shopping_item", error);
                    withView(view -> view.showErrorToast(R.string.edit_shopping_item_error_retrieving));
                    close.set(true);
                });
    }

    public void save() {
        formFieldsEnabled.set(false);
        saveButtonEnabled.set(false);
        saveErrorVisibility.set(android.view.View.GONE);

        Category category = getSelectedCategory();

        shoppingItemRepository.update(new ShoppingItem(shoppingItem.getId(), shoppingItemName.get(), shoppingItem.isBought(), category.getId(), category.getColor()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    close.set(true);
                }, error -> {
                    logError("edit_shopping_item_saving", error);
                    saveErrorVisibility.set(android.view.View.VISIBLE);
                    formFieldsEnabled.set(true);
                    saveButtonEnabled.set(true);
                });
    }

    public void cancel() {
        close.set(true);
    }

    private Category getSelectedCategory() {
        int position = shoppingItemCategoryPosition.get();
        if (position == -1) {
            return new Category(null, "", 0);
        }
        return categories.get(position);
    }
}
