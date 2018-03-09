package org.bitbrothers.shoppi.presenter;


import android.util.Log;

import org.bitbrothers.shoppi.model.Category;
import org.bitbrothers.shoppi.store.CategoryRepository;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AllCategoriesPresenter extends BasePresenter<AllCategoriesPresenter.View> {

    public interface View extends BasePresenter.BaseView {
        void showCategories(List<Category> categories);
    }

    private final CategoryRepository categoryRepository;

    public AllCategoriesPresenter(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void attach(View view) {
        super.attach(view);

        retrieveCategories();
    }

    private void retrieveCategories() {
        categoryRepository.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categories -> {
                    if (view != null) {
                        view.showCategories(categories);
                    }
                }, error -> {
                    Log.e("AllCategoriesPresenter", "Retrieving categories failed", error);
                });
    }
}
