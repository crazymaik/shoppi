package org.bitbrothers.shoppi.ui.viewmodel;

import android.databinding.ObservableArrayList;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.logging.Logger;
import org.bitbrothers.shoppi.model.Category;
import org.bitbrothers.shoppi.store.CategoryRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AllCategoriesViewModel extends BaseViewModel<AllCategoriesViewModel.View> {

    public interface View extends BaseViewModel.BaseView {

        void promptDeleteCategory(long categoryId, int itemCount);
    }

    public final ObservableArrayList<Category> categories = new ObservableArrayList<>();

    private final CategoryRepository categoryRepository;

    public AllCategoriesViewModel(Logger logger, CategoryRepository categoryRepository) {
        super(logger);
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void attach(View view) {
        super.attach(view);

        addViewDisposable(categoryRepository.getOnItemAddedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    retrieveCategories();
                }, error -> {
                    logError("all_categories_shopping_item_added", error);
                }));

        addViewDisposable(categoryRepository.getOnItemUpdatedObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    retrieveCategories();
                }, error -> {
                    logError("all_categories_shopping_item_updated", error);
                }));

        addViewDisposable(categoryRepository.getOnItemRemovedObservable()
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
                    logError("all_categories_shopping_item_removed", error);
                }));

        retrieveCategories();
    }

    private void retrieveCategories() {
        categoryRepository.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categories -> {
                    this.categories.clear();
                    this.categories.addAll(categories);
                }, error -> {
                    logError("all_categories_retrieving_categories", error);
                    withView(view -> view.showErrorToast(R.string.all_categories_error_retrieving_categories));
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
                        withView(view -> view.promptDeleteCategory(categoryId, itemCount));
                    }
                }, error -> {
                    logError("all_categories_retrieving_category_usage", error);
                    withView(view -> view.showErrorToast(R.string.all_categories_error_retrieving_category_usage));
                });
    }

    public void deleteCategory(long categoryId) {
        categoryRepository.delete(categoryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                }, error -> {
                    logError("all_categories_deleting_category", error);
                    withView(view -> view.showErrorToast(R.string.all_categories_error_deleting_category));
                });
    }
}
