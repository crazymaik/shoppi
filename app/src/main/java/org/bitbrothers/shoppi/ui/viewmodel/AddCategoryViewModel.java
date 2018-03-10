package org.bitbrothers.shoppi.ui.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;

import org.bitbrothers.shoppi.model.Category;
import org.bitbrothers.shoppi.store.CategoryRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddCategoryViewModel extends ViewModel {

    public final ObservableBoolean formFieldsEnabled = new ObservableBoolean(true);

    public final ObservableInt selectedColorPosition = new ObservableInt(0);

    public final List<Integer> colorValues = new ArrayList<Integer>() {{
        add(0xDC0D0D);
        add(0xDC6318);
        add(0xDCD61A);
        add(0x7BDC1A);
        add(0x1ADC61);
        add(0x18DCD2);
        add(0x188BDC);
        add(0x1644DC);
        add(0x7718DC);
        add(0xD618DC);
        add(0xDC1898);
        add(0xDC185D);
    }};

    public final ObservableField<String> categoryName = new ObservableField<>("");

    public final ObservableInt saveErrorVisibility = new ObservableInt(View.GONE);

    public final ObservableBoolean saveButtonEnabled = new ObservableBoolean(false);

    public final ObservableBoolean close = new ObservableBoolean(false);

    private final CategoryRepository categoryRepository;

    public AddCategoryViewModel(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;

        categoryName.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                saveButtonEnabled.set(formFieldsEnabled.get() && !categoryName.get().isEmpty());
            }
        });
    }

    public void save() {
        hideSaveErrorMessage();
        formFieldsEnabled.set(false);
        saveButtonEnabled.set(false);

        categoryRepository.create(new Category(categoryName.get(), getSelectedColorValue()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(category -> {
                    close.set(true);
                }, error -> {
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
