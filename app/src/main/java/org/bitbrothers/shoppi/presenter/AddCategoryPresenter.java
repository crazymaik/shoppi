package org.bitbrothers.shoppi.presenter;

import org.bitbrothers.shoppi.model.Category;
import org.bitbrothers.shoppi.store.CategoryRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddCategoryPresenter extends BasePresenter<AddCategoryPresenter.View> {

    public interface View extends BasePresenter.BaseView {

        void setColors(List<Integer> colors);

        void setSelectedColorPosition(int position);

        void setAddButtonEnabled(boolean enabled);

        void setFieldsEnabled(boolean enabled);

        void setNameFieldText(String name);

        void showSaveFailedErrorMessage();

        void close();
    }

    private final CategoryRepository categoryRepository;

    private final List<Integer> colors = new ArrayList() {{
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

    private String name;
    private int color;

    public AddCategoryPresenter(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void init() {
        super.init();
        name = "";
        color = colors.get(0);
        transition(new EditingState());
    }

    @Override
    public void attach(View view) {
        super.attach(view);
    }

    public void setName(String name) {
        if (!this.name.equals(name)) {
            this.name = name;
            this.state.apply();
        }
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void save() {
        transition(new SavingState());
        categoryRepository.create(new Category(name, color))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(category -> {
                    transition(new SaveCompletedState());
                }, error -> {
                    transition(new SaveErrorState());
                });
    }

    public void cancel() {
        view.close();
    }

    private class EditingState extends State {
        @Override
        protected void onAttach() {
            view.setColors(colors);
            view.setSelectedColorPosition(colors.indexOf(color));
            view.setAddButtonEnabled(!name.isEmpty());
            view.setFieldsEnabled(true);
            view.setNameFieldText(name);
        }

        @Override
        protected void apply() {
            view.setAddButtonEnabled(!name.isEmpty());
            view.setFieldsEnabled(true);
        }
    }

    private class SavingState extends SimpleState {
        @Override
        protected void apply() {
            view.setAddButtonEnabled(false);
            view.setFieldsEnabled(false);
            view.setNameFieldText(name);
        }
    }

    private class SaveErrorState extends SimpleState {
        @Override
        protected void apply() {
            view.showSaveFailedErrorMessage();
            transition(new EditingState());
        }
    }

    private class SaveCompletedState extends SimpleState {
        @Override
        protected void apply() {
            view.close();
        }
    }
}
