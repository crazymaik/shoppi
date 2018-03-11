package org.bitbrothers.shoppi.ui.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableArrayList;
import android.util.Log;

import org.bitbrothers.shoppi.model.Category;
import org.bitbrothers.shoppi.store.CategoryRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AllCategoriesViewModel extends ViewModel {

    public interface View {

        void promptDeleteCategory(long categoryId, int itemCount);
    }

    public final ObservableArrayList<Category> categories = new ObservableArrayList<>();

    private final CategoryRepository categoryRepository;
    private View view;
    private Disposable onItemAddedDisposable;
    private Disposable onItemUpdatedDisposable;
    private Disposable onItemRemovedDisposable;

    public AllCategoriesViewModel(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void attach(View view) {
        this.view = view;

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
                    for (int i = 0; i < categories.size(); ++i) {
                        if (categories.get(i).getId() == id) {
                            categories.remove(i);
                            break;
                        }
                    }
                }, error -> {

                });

        retrieveCategories();
    }

    public void detach() {
        onItemAddedDisposable.dispose();
        onItemUpdatedDisposable.dispose();
        onItemRemovedDisposable.dispose();
    }

    private void retrieveCategories() {
        categoryRepository.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categories -> {
                    this.categories.clear();
                    this.categories.addAll(categories);
                }, error -> {
                    Log.e("AllCategoriesPresenter", "Retrieving categories failed", error);
                });
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
}
