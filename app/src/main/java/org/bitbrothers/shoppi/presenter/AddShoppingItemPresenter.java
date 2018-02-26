package org.bitbrothers.shoppi.presenter;

import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.store.ShoppingItemRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddShoppingItemPresenter extends BasePresenter<AddShoppingItemPresenter.View> {

    public interface View extends BasePresenter.BaseView {

        void setAddButtonEnabled(boolean enabled);

        void setNameFieldEnabled(boolean enabled);

        void setNameFieldText(String text);

        void close();
    }

    private final ShoppingItemRepository shoppingItemRepository;
    private String name;

    public AddShoppingItemPresenter(ShoppingItemRepository shoppingItemRepository) {
        this.shoppingItemRepository = shoppingItemRepository;
    }

    @Override
    public void init() {
        this.name = "";
        transition(new EditingState());
    }

    public void setName(String name) {
        if (!this.name.equals(name)) {
            this.name = name;
            this.state.apply();
        }
    }

    public void cancel() {
        this.view.close();
    }

    public void save() {
        transition(new SavingState());
        disposables.add(shoppingItemRepository.create(new ShoppingItem(name))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    transition(new SaveCompletedState());
                }, error -> {
                    //transition(new SaveErrorState());
                }));
    }

    private class EditingState extends State {

        @Override
        protected void onAttach() {
            view.setAddButtonEnabled(!name.isEmpty());
            view.setNameFieldEnabled(true);
            view.setNameFieldText(name);
        }

        @Override
        protected void apply() {
            view.setAddButtonEnabled(!name.isEmpty());
            view.setNameFieldEnabled(true);
        }
    }

    private class SavingState extends SimpleState {
        @Override
        protected void apply() {
            view.setAddButtonEnabled(false);
            view.setNameFieldEnabled(false);
            view.setNameFieldText(name);
        }
    }

    private class SaveCompletedState extends SimpleState {
        @Override
        protected void apply() {
            view.setAddButtonEnabled(false);
            view.setNameFieldEnabled(false);
            view.close();
        }
    }
}
