package org.bitbrothers.shoppi.ui.viewmodel;

import android.content.Context;
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

public class EditShoppingItemViewModel extends BaseViewModel<EditShoppingItemViewModel.View> {

    public interface View extends BaseViewModel.BaseView {
    }

    public final ObservableBoolean formFieldsEnabled = new ObservableBoolean(false);
    public final ObservableField<String> shoppingItemName = new ObservableField<>("");
    public final ObservableInt shoppingItemCategoryPosition = new ObservableInt(-1);
    public final ObservableBoolean saveButtonEnabled = new ObservableBoolean(false);
    public final ObservableArrayList<Category> categories = new ObservableArrayList<>();
    public final ObservableBoolean saveErrorVisible = new ObservableBoolean(false);
    public final ObservableBoolean close = new ObservableBoolean(false);

    private final Context context;
    private final ShoppingItemRepository shoppingItemRepository;
    private final CategoryRepository categoryRepository;
    private ShoppingItem shoppingItem;

    public EditShoppingItemViewModel(Context context, Logger logger, ShoppingItemRepository shoppingItemRepository, CategoryRepository categoryRepository) {
        super(logger);
        this.context = context;
        this.shoppingItemRepository = shoppingItemRepository;
        this.categoryRepository = categoryRepository;
    }

    public void setEditMode(long shoppingItemId) {
        formFieldsEnabled.set(false);
        saveButtonEnabled.set(false);

        Single.zip(categoryRepository.getAll(), shoppingItemRepository.get(shoppingItemId), (categories, shoppingItem) -> Pair.create(categories, shoppingItem))
                .compose(applySingleSchedulers())
                .subscribe(result -> {
                    this.shoppingItem = result.second;
                    this.categories.clear();
                    this.categories.add(new Category(0l, context.getString(R.string.category_unassigned_name), 0xff000000));
                    this.categories.addAll(result.first);

                    int position = 0;
                    for (int i = 1; i < this.categories.size(); ++i) {
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
                    logError("edit_shopping_item_retrieving", error);
                    withView(view -> view.showErrorToast(R.string.edit_shopping_item_error_retrieving));
                    close.set(true);
                });
    }

    public void save() {
        formFieldsEnabled.set(false);
        saveButtonEnabled.set(false);
        saveErrorVisible.set(false);

        Category category = getSelectedCategory();

        shoppingItemRepository.update(new ShoppingItem(shoppingItem.getId(), shoppingItemName.get(), shoppingItem.isBought(), category.getId(), category.getColor()))
                .compose(applySingleSchedulers())
                .subscribe(shoppingItem -> {
                    close.set(true);
                }, error -> {
                    logError("edit_shopping_item_saving", error);
                    saveErrorVisible.set(true);
                    formFieldsEnabled.set(true);
                    saveButtonEnabled.set(true);
                });
    }

    public void cancel() {
        close.set(true);
    }

    private Category getSelectedCategory() {
        int position = shoppingItemCategoryPosition.get();
        if (position <= 0) {
            return new Category(null, "", 0);
        }
        return categories.get(position);
    }
}
