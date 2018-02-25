package org.bitbrothers.shoppi.presenter;

import android.content.Context;
import android.os.Handler;

import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.store.ShoppingItemRepository;

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

    public void onAttach(View view) {
        this.view = view;
        this.state.apply();
    }

    public void onDetach() {
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

    public void save(final String name) {
        transition(new SavingState());
        new Thread(() -> {
            shoppingItemRepository.create(new ShoppingItem(name));
            Handler handler = new Handler(context.getMainLooper());
            handler.post(() -> transition(new SaveCompletedState()));
        }).start();
    }

    private void transition(State state) {
        this.state = state;
        if (view != null) {
            this.state.apply();
        }
    }

    private class State {
        protected void apply() {
        }
    }

    private class EditingState extends State {
        @Override
        protected void apply() {
            view.setAddButtonEnabled(!name.isEmpty());
            view.setNameFieldEnabled(true);
            view.setNameFieldText(name);
        }
    }

    private class SavingState extends State {
        @Override
        protected void apply() {
            view.setAddButtonEnabled(false);
            view.setNameFieldEnabled(false);
            view.setNameFieldText(name);
        }
    }

    private class SaveCompletedState extends State {
        @Override
        protected void apply() {
            view.setAddButtonEnabled(false);
            view.setNameFieldEnabled(false);
            view.close();
        }
    }
}
