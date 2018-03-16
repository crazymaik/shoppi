package org.bitbrothers.shoppi.ui.viewmodel;

import android.databinding.ObservableArrayList;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.logging.Logger;
import org.bitbrothers.shoppi.model.Category;
import org.bitbrothers.shoppi.store.CategoryRepository;

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
                .compose(applyObservableSchedulers())
                .subscribe(shoppingItem -> {
                    retrieveCategories();
                }, error -> {
                    logError("all_categories_item_added", error);
                }));

        addViewDisposable(categoryRepository.getOnItemUpdatedObservable()
                .compose(applyObservableSchedulers())
                .subscribe(shoppingItem -> {
                    retrieveCategories();
                }, error -> {
                    logError("all_categories_item_updated", error);
                }));

        addViewDisposable(categoryRepository.getOnItemRemovedObservable()
                .compose(applyObservableSchedulers())
                .subscribe(id -> {
                    for (int i = 0; i < categories.size(); ++i) {
                        if (categories.get(i).getId() == id) {
                            categories.remove(i);
                            break;
                        }
                    }
                }, error -> {
                    logError("all_categories_item_removed", error);
                }));

        retrieveCategories();
    }

    private void retrieveCategories() {
        categoryRepository.getAll()
                .compose(applySingleSchedulers())
                .subscribe(categories -> {
                    this.categories.clear();
                    this.categories.addAll(categories);
                }, error -> {
                    logError("all_categories_retrieving", error);
                    withView(view -> view.showErrorToast(R.string.all_categories_error_retrieving_categories));
                });
    }

    public void safeDeleteCategory(long categoryId) {
        categoryRepository.getAssignedShoppingItemsCount(categoryId)
                .compose(applySingleSchedulers())
                .subscribe(itemCount -> {
                    if (itemCount == 0) {
                        deleteCategory(categoryId);
                    } else {
                        withView(view -> view.promptDeleteCategory(categoryId, itemCount));
                    }
                }, error -> {
                    logError("all_categories_retrieving_usage", error);
                    withView(view -> view.showErrorToast(R.string.all_categories_error_retrieving_category_usage));
                });
    }

    public void deleteCategory(long categoryId) {
        categoryRepository.delete(categoryId)
                .compose(applyCompletableSchedulers())
                .subscribe(() -> {
                }, error -> {
                    logError("all_categories_deleting", error);
                    withView(view -> view.showErrorToast(R.string.all_categories_error_deleting_category));
                });
    }
}
