package org.bitbrothers.shoppi.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import org.bitbrothers.shoppi.ShoppiApplication;
import org.bitbrothers.shoppi.ui.viewmodel.AddCategoryViewModel;
import org.bitbrothers.shoppi.ui.viewmodel.AllCategoriesViewModel;

public class DependencyAwareViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public DependencyAwareViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.equals(AddCategoryViewModel.class)) {
            return (T) new AddCategoryViewModel(ShoppiApplication.from(context).getCategoryRepository());
        } else if (modelClass.equals(AllCategoriesViewModel.class)) {
            return (T) new AllCategoriesViewModel(ShoppiApplication.from(context).getCategoryRepository());
        } else {
            throw new IllegalArgumentException();
        }
    }
}
