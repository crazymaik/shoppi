package org.bitbrothers.shoppi.presenter;

import android.content.Context;

import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.store.ShoppingItemRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddShoppingItemPresenter {

    public interface View {

        void setAddButtonEnabled(boolean enabled);

        void setNameFieldEnabled(boolean enabled);

        void setNameFieldText(String text);

        void close();
    }

    private final Context context;
    private final ShoppingItemRepository shoppingItemRepository;
    private State state;
    private String name;
    private View view;

    public AddShoppingItemPresenter(Context context, ShoppingItemRepository shoppingItemRepository) {
        this.context = context;
        this.shoppingItemRepository = shoppingItemRepository;
    }

    public void init() {
        this.state = new EditingState();
        this.name = "";
    }

    public void attach(View view) {
        this.view = view;
        this.state.onAttach();
    }

    public void detach() {
        this.view = null;
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
        shoppingItemRepository.create(new ShoppingItem(name))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItem -> {
                    transition(new SaveCompletedState());
                }, error -> {
                    //transition(new SaveErrorState());
                });
    }

    private void transition(State state) {
        this.state = state;
        if (view != null) {
            this.state.apply();
        }
    }

    private abstract class State {

        protected abstract void onAttach();

        protected abstract void apply();
    }

    private abstract class SimpleState extends State {

        @Override
        protected void onAttach() {
            apply();
        }
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
