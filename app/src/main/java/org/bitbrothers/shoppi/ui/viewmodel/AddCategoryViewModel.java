package org.bitbrothers.shoppi.ui.viewmodel;

import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.logging.Logger;
import org.bitbrothers.shoppi.model.Category;
import org.bitbrothers.shoppi.model.Colors;
import org.bitbrothers.shoppi.store.CategoryRepository;

import io.reactivex.Single;

public class AddCategoryViewModel extends BaseViewModel<AddCategoryViewModel.View> {

    public interface View extends BaseViewModel.BaseView {
        void makeColorItemVisible(int position);
    }

    public final ObservableBoolean formFieldsEnabled = new ObservableBoolean(true);

    public final ObservableInt selectedColorPosition = new ObservableInt(0);

    public final ObservableField<String> categoryName = new ObservableField<>("");

    public final ObservableBoolean saveErrorVisible = new ObservableBoolean(false);

    public final ObservableBoolean saveButtonEnabled = new ObservableBoolean(false);

    public final ObservableBoolean close = new ObservableBoolean(false);

    private final CategoryRepository categoryRepository;

    private Long categoryId;

    public AddCategoryViewModel(Logger logger, CategoryRepository categoryRepository) {
        super(logger);
        this.categoryRepository = categoryRepository;

        categoryName.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                saveButtonEnabled.set(formFieldsEnabled.get() && !categoryName.get().isEmpty());
            }
        });
    }

    public void setEditMode(long categoryId) {
        this.categoryId = categoryId;
        formFieldsEnabled.set(false);
        saveButtonEnabled.set(false);

        categoryRepository.get(categoryId)
                .compose(applySingleSchedulers())
                .subscribe(category -> {
                    categoryName.set(category.getName());
                    int position = Colors.RGB_COLOR_VALUES.indexOf(category.getColor());
                    withView(view -> view.makeColorItemVisible(position));
                    selectedColorPosition.set(position);
                    formFieldsEnabled.set(true);
                    saveButtonEnabled.set(true);
                }, error -> {
                    logError("add_category_retrieving", error);
                    withView(view -> view.showErrorToast(R.string.add_category_error_retrieving_category));
                    close.set(true);
                });
    }

    public boolean isEditMode() {
        return this.categoryId != null;
    }

    public void save() {
        saveErrorVisible.set(false);
        formFieldsEnabled.set(false);
        saveButtonEnabled.set(false);

        Single<Category> operation;

        if (categoryId != null) {
            operation = categoryRepository.update(new Category(categoryId, categoryName.get(), getSelectedColorValue()));
        } else {
            operation = categoryRepository.create(new Category(categoryName.get(), getSelectedColorValue()));
        }

        operation.compose(applySingleSchedulers())
                .subscribe(category -> {
                    close.set(true);
                }, error -> {
                    logError("add_category_saving", error);
                    saveErrorVisible.set(true);
                    formFieldsEnabled.set(true);
                    saveButtonEnabled.set(true);
                });
    }

    public void cancel() {
        close.set(true);
    }

    private int getSelectedColorValue() {
        return Colors.RGB_COLOR_VALUES.get(selectedColorPosition.get());
    }
}
