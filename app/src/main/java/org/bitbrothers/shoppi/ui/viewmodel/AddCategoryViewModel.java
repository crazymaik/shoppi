package org.bitbrothers.shoppi.ui.viewmodel;

import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.logging.Logger;
import org.bitbrothers.shoppi.model.Category;
import org.bitbrothers.shoppi.store.CategoryRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddCategoryViewModel extends BaseViewModel<BaseViewModel.BaseView> {

    public final ObservableBoolean formFieldsEnabled = new ObservableBoolean(true);

    public final ObservableInt selectedColorPosition = new ObservableInt(0);

    public final List<Integer> colorValues = new ArrayList<Integer>() {{
        add(0xFFDC0D0D);
        add(0xFFDC6318);
        add(0xFFDCD61A);
        add(0xFF7BDC1A);
        add(0xFF1ADC61);
        add(0xFF18DCD2);
        add(0xFF188BDC);
        add(0xFF1644DC);
        add(0xFF7718DC);
        add(0xFFD618DC);
        add(0xFFDC1898);
        add(0xFFDC185D);
    }};

    public final ObservableField<String> categoryName = new ObservableField<>("");

    public final ObservableInt saveErrorVisibility = new ObservableInt(View.GONE);

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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(category -> {
                    categoryName.set(category.getName());
                    selectedColorPosition.set(colorValues.indexOf(category.getColor()));
                    formFieldsEnabled.set(true);
                    saveButtonEnabled.set(true);
                }, error -> {
                    logError("add_category_retrieving_category", error);
                    withView(view -> view.showErrorToast(R.string.add_category_error_retrieving_category));
                    close.set(true);
                });
    }

    public void save() {
        hideSaveErrorMessage();
        formFieldsEnabled.set(false);
        saveButtonEnabled.set(false);

        Single<Category> operation;

        if (categoryId != null) {
            operation = categoryRepository.update(new Category(categoryId, categoryName.get(), getSelectedColorValue()));
        } else {
            operation = categoryRepository.create(new Category(categoryName.get(), getSelectedColorValue()));
        }

        operation.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(category -> {
                    close.set(true);
                }, error -> {
                    logError("add_category_saving_category", error);
                    showSaveErrorMessage();
                    formFieldsEnabled.set(true);
                    saveButtonEnabled.set(true);
                });
    }

    public void cancel() {
        close.set(true);
    }

    private int getSelectedColorValue() {
        return colorValues.get(selectedColorPosition.get());
    }

    private void hideSaveErrorMessage() {
        saveErrorVisibility.set(View.GONE);
    }

    private void showSaveErrorMessage() {
        saveErrorVisibility.set(View.VISIBLE);
    }

}
