package org.bitbrothers.shoppi.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import org.bitbrothers.shoppi.ShoppiApplication;
import org.bitbrothers.shoppi.ui.viewmodel.AddCategoryViewModel;
import org.bitbrothers.shoppi.ui.viewmodel.AllCategoriesViewModel;
import org.bitbrothers.shoppi.ui.viewmodel.AllShoppingItemsViewModel;
import org.bitbrothers.shoppi.ui.viewmodel.ShoppingListViewModel;

public class DependencyAwareViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public DependencyAwareViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        ShoppiApplication app = ShoppiApplication.from(context);
        if (modelClass.equals(AddCategoryViewModel.class)) {
            return (T) new AddCategoryViewModel(app.getCategoryRepository());
        } else if (modelClass.equals(AllCategoriesViewModel.class)) {
            return (T) new AllCategoriesViewModel(app.getCategoryRepository());
        } else if (modelClass.equals(ShoppingListViewModel.class)) {
            return (T) new ShoppingListViewModel(app.getShoppingItemRepository(), app.getCategoryRepository());
        } else if (modelClass.equals(AllShoppingItemsViewModel.class)) {
            return (T) new AllShoppingItemsViewModel(app.getShoppingItemRepository(), app.getCategoryRepository());
        } else {
            throw new IllegalArgumentException();
        }
    }
}