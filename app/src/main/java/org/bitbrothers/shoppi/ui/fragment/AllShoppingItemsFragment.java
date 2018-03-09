package org.bitbrothers.shoppi.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Spinner;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.ShoppiApplication;
import org.bitbrothers.shoppi.model.Category;
import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.presenter.AllShoppingItemsPresenter;
import org.bitbrothers.shoppi.ui.adapter.AllShoppingItemsAdapter;
import org.bitbrothers.shoppi.ui.adapter.CategoriesSpinnerAdapter;
import org.bitbrothers.shoppi.ui.view.TextWatcherAdapter;
import org.bitbrothers.shoppi.ui.widget.BetterEditText;

import java.util.List;

public class AllShoppingItemsFragment
        extends BaseFragment<AllShoppingItemsPresenter>
        implements AllShoppingItemsPresenter.View {

    private static final String KEY_ADD_CONTAINER_VISIBLE = "add_container_visible";

    private AllShoppingItemsAdapter shoppingItemsAdapter;
    private CategoriesSpinnerAdapter categoriesSpinnerAdapter;
    private ViewGroup addShoppingItemContainer;
    private Spinner addShoppingItemSpinner;
    private BetterEditText addShoppingItemEditText;
    private Button addShoppingItemButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shoppingItemsAdapter = new AllShoppingItemsAdapter(new AllShoppingItemsAdapter.Callback() {
            @Override
            public void removeShoppingItem(ShoppingItem shoppingItem) {
                presenter.deleteShoppingItem(shoppingItem);
            }

            @Override
            public void toggleMark(ShoppingItem shoppingItem) {
                if (shoppingItem.isBought()) {
                    presenter.unmarkBought(shoppingItem);
                } else {
                    presenter.markBought(shoppingItem);
                }
            }
        });
        categoriesSpinnerAdapter = new CategoriesSpinnerAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_shopping_items, container, false);
        FloatingActionButton fab = view.findViewById(R.id.fab);

        addShoppingItemContainer = view.findViewById(R.id.add_container);
        addShoppingItemSpinner = view.findViewById(R.id.add_shopping_item_spinner);
        addShoppingItemEditText = view.findViewById(R.id.add_shopping_item_edit);
        addShoppingItemButton = view.findViewById(R.id.add_shopping_item_button);

        addShoppingItemSpinner.setAdapter(categoriesSpinnerAdapter);

        Runnable addShoppingItemAndReset = () -> {
            Category selectedCategory = (Category) addShoppingItemSpinner.getSelectedItem();
            String selectedName = addShoppingItemEditText.getText().toString();
            presenter.addShoppingItem(selectedName, selectedCategory);
            // TODO this should prevent further input and only reset after adding the
            // shopping item actually completed
            addShoppingItemEditText.setText("");
        };

        addShoppingItemEditText.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                addShoppingItemButton.setEnabled(!s.toString().isEmpty());
            }
        });

        addShoppingItemEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == getResources().getInteger(R.integer.ime_action_done)) {
                addShoppingItemAndReset.run();
                return true;
            }
            return false;
        });

        addShoppingItemEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.post(() -> {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(v, 0);
                    }
                });
            }
        });

        addShoppingItemEditText.setOnImeBackListener(v -> {
            addShoppingItemContainer.animate().translationY(-addShoppingItemContainer.getHeight()).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    addShoppingItemContainer.setVisibility(View.GONE);
                    addShoppingItemContainer.setTranslationY(0);
                }
            }).start();
        });

        addShoppingItemButton.setOnClickListener(v -> {
            addShoppingItemAndReset.run();
        });

        fab.setOnClickListener(v -> {
            presenter.openAddShoppingItemView();
        });

        RecyclerView recyclerView = view.findViewById(android.R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(shoppingItemsAdapter);

        if (savedInstanceState != null) {
            addShoppingItemContainer.setVisibility(savedInstanceState.getBoolean(KEY_ADD_CONTAINER_VISIBLE, false) ? View.VISIBLE : View.GONE);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_ADD_CONTAINER_VISIBLE, addShoppingItemContainer.getVisibility() == View.VISIBLE);
    }

    @Override
    public void removeShoppingItem(long id) {
        shoppingItemsAdapter.removeShoppingItem(id);
    }

    @Override
    public void showShoppingItems(List<ShoppingItem> shoppingItems) {
        shoppingItemsAdapter.setShoppingItems(shoppingItems);
    }

    @Override
    public void updateShoppingItem(ShoppingItem shoppingItem) {
        shoppingItemsAdapter.updateShoppingItem(shoppingItem);
    }

    @Override
    public void setCategories(List<Category> categories) {
        categoriesSpinnerAdapter.setCategories(categories);
    }

    @Override
    public void showAddShoppingItemView() {
        addShoppingItemSpinner.setSelection(0);
        addShoppingItemEditText.setText("");
        addShoppingItemContainer.setVisibility(View.VISIBLE);
        addShoppingItemEditText.requestFocus();
    }

    @Override
    protected AllShoppingItemsPresenter createPresenter() {
        return ShoppiApplication.from(getContext()).getAllShoppingItemsPresenter();
    }

}
