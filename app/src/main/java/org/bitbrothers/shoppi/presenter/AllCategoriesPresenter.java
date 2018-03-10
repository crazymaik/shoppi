package org.bitbrothers.shoppi.presenter;


import android.util.Log;

import org.bitbrothers.shoppi.model.Category;
import org.bitbrothers.shoppi.store.CategoryRepository;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AllCategoriesPresenter extends BasePresenter<AllCategoriesPresenter.View> {

    public interface View extends BasePresenter.BaseView {

        void showCategories(List<Category> categories);

        void removeCategory(long id);

        void promptDeleteCategory(long categoryId, int itemCount);
    }

    private final CategoryRepository categoryRepository;
    private Disposable onItemAddedDisposable;
    private Disposable onItemUpdatedDisposable;
    private Disposable onItemRemovedDisposable;

    public AllCategoriesPresenter(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void attach(View view) {
        super.attach(view);

        onItemAddedDisposable = categoryRepository.getOnItemAddedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    retrieveCategories();
                }, error -> {

                });

        onItemUpdatedDisposable = categoryRepository.getOnItemUpdatedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    retrieveCategories();
                }, error -> {

                });

        onItemRemovedDisposable = categoryRepository.getOnItemRemovedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> {
                    if (view != null) {
                        view.removeCategory(id);
                    }
                }, error -> {

                });

        retrieveCategories();
    }

    @Override
    public void detach() {
        onItemAddedDisposable.dispose();
        onItemUpdatedDisposable.dispose();
        onItemRemovedDisposable.dispose();
        super.detach();
    }

    public void safeDeleteCategory(long categoryId) {
        categoryRepository.getAssignedShoppingItemsCount(categoryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(itemCount -> {
                    if (itemCount == 0) {
                        deleteCategory(categoryId);
                    } else {
                        if (view != null) {
                            view.promptDeleteCategory(categoryId, itemCount);
                        }
                    }
                }, error -> {
                });
    }

    public void deleteCategory(long categoryId) {
        categoryRepository.delete(categoryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                }, error -> {
                });
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
