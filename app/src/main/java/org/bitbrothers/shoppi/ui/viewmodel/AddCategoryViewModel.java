package org.bitbrothers.shoppi.ui.viewmodel;

import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.logging.Logger;
import org.bitbrothers.shoppi.model.Category;
import org.bitbrothers.shoppi.store.CategoryRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddCategoryViewModel extends BaseViewModel<AddCategoryViewModel.View> {

    public interface View extends BaseViewModel.BaseView {
        void makeColorItemVisible(int position);
    }

    public final ObservableBoolean formFieldsEnabled = new ObservableBoolean(true);

    public final ObservableInt selectedColorPosition = new ObservableInt(0);

    public final List<Integer> colorValues = new ArrayList<Integer>() {{
        add(0xffcc5151);
        add(0xffe51616);
        add(0xffcc8e51);
        add(0xffe57e16);
        add(0xffcccc51);
        add(0xffe5e516);
        add(0xff8ecc51);
        add(0xff7ee516);
        add(0xff51cc51);
        add(0xff16e516);
        add(0xff51cc8e);
        add(0xff16e57e);
        add(0xff51cccc);
        add(0xff16e5e5);
        add(0xff518ecc);
        add(0xff167ee5);
        add(0xff5151cc);
        add(0xff1616e5);
        add(0xff8e51cc);
        add(0xff7e16e5);
        add(0xffcc51cc);
        add(0xffe516e5);
        add(0xffcc518e);
        add(0xffe5167e);
    }};

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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(category -> {
                    categoryName.set(category.getName());
                    int position = colorValues.indexOf(category.getColor());
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

        operation.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
        return colorValues.get(selectedColorPosition.get());
    }
}
