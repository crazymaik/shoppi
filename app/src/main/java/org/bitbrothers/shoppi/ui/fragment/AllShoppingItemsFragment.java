package org.bitbrothers.shoppi.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import org.bitbrothers.shoppi.databinding.FragmentAllShoppingItemsBinding;
import org.bitbrothers.shoppi.model.Category;
import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.ui.adapter.AllShoppingItemsAdapter;
import org.bitbrothers.shoppi.ui.adapter.CategoriesSpinnerAdapter;
import org.bitbrothers.shoppi.ui.adapter.WeakOnListChangedCallbackBaseAdapter;
import org.bitbrothers.shoppi.ui.adapter.WeakOnListChangedCallbackRecyclerViewAdapter;
import org.bitbrothers.shoppi.ui.view.TextWatcherAdapter;
import org.bitbrothers.shoppi.ui.viewmodel.AllShoppingItemsViewModel;
import org.bitbrothers.shoppi.ui.widget.BetterEditText;

public class AllShoppingItemsFragment
        extends Fragment {

    private AllShoppingItemsViewModel viewModel;
    private AllShoppingItemsAdapter shoppingItemsAdapter;
    private CategoriesSpinnerAdapter categoriesSpinnerAdapter;
    private ViewGroup addShoppingItemContainer;
    private Spinner addShoppingItemSpinner;
    private BetterEditText addShoppingItemEditText;
    private Button addShoppingItemButton;

    private final Observable.OnPropertyChangedCallback onAddContainerVisibilityPropertyChanged = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            if (viewModel.addContainerVisibility.get() == View.VISIBLE) {
                addShoppingItemContainer.setTranslationY(0);
                addShoppingItemSpinner.setSelection(-1);
                addShoppingItemEditText.setText("");
                addShoppingItemEditText.requestFocus();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, ShoppiApplication.from(getContext()).getViewModelFactory()).get(AllShoppingItemsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentAllShoppingItemsBinding binding = FragmentAllShoppingItemsBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        binding.setVm(viewModel);

        FloatingActionButton fab = rootView.findViewById(R.id.fab);

        shoppingItemsAdapter = new AllShoppingItemsAdapter(new AllShoppingItemsAdapter.Callback() {
            @Override
            public void removeShoppingItem(ShoppingItem shoppingItem) {
                viewModel.deleteShoppingItem(shoppingItem);
            }

            @Override
            public void toggleMark(ShoppingItem shoppingItem) {
                if (shoppingItem.isBought()) {
                    viewModel.unmarkBought(shoppingItem);
                } else {
                    viewModel.markBought(shoppingItem);
                }
            }
        });

        shoppingItemsAdapter.setShoppingItems(viewModel.shoppingItems);
        viewModel.shoppingItems.addOnListChangedCallback(new WeakOnListChangedCallbackRecyclerViewAdapter(shoppingItemsAdapter));

        categoriesSpinnerAdapter = new CategoriesSpinnerAdapter();
        categoriesSpinnerAdapter.setCategories(viewModel.categories);
        viewModel.categories.addOnListChangedCallback(new WeakOnListChangedCallbackBaseAdapter(categoriesSpinnerAdapter));

        addShoppingItemContainer = rootView.findViewById(R.id.add_container);
        addShoppingItemSpinner = rootView.findViewById(R.id.add_shopping_item_spinner);
        addShoppingItemEditText = rootView.findViewById(R.id.add_shopping_item_edit);
        addShoppingItemButton = rootView.findViewById(R.id.add_shopping_item_button);

        addShoppingItemSpinner.setAdapter(categoriesSpinnerAdapter);

        Runnable addShoppingItemAndReset = () -> {
            Category selectedCategory = (Category) addShoppingItemSpinner.getSelectedItem();
            String selectedName = addShoppingItemEditText.getText().toString();
            viewModel.addShoppingItem(selectedName, selectedCategory);
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
                    viewModel.addContainerVisibility.set(View.GONE);
                }
            }).start();
        });

        addShoppingItemButton.setOnClickListener(v -> {
            addShoppingItemAndReset.run();
        });

        viewModel.addContainerVisibility.addOnPropertyChangedCallback(onAddContainerVisibilityPropertyChanged);

        fab.setOnClickListener(v -> {
            viewModel.openAddShoppingItemView();
        });

        RecyclerView recyclerView = rootView.findViewById(android.R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(shoppingItemsAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.attach();
    }

    @Override
    public void onStop() {
        viewModel.detach();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        viewModel.addContainerVisibility.removeOnPropertyChangedCallback(onAddContainerVisibilityPropertyChanged);
        super.onDestroyView();
    }
}
