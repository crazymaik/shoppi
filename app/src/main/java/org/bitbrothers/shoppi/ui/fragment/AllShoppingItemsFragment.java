package org.bitbrothers.shoppi.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.Observable;
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
import org.bitbrothers.shoppi.databinding.FragmentAllShoppingItemsBinding;
import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.ui.activity.MainActivity;
import org.bitbrothers.shoppi.ui.adapter.AllShoppingItemsAdapter;
import org.bitbrothers.shoppi.ui.adapter.CategoriesSpinnerAdapter;
import org.bitbrothers.shoppi.ui.adapter.WeakOnListChangedCallbackBaseAdapter;
import org.bitbrothers.shoppi.ui.adapter.WeakOnListChangedCallbackRecyclerViewAdapter;
import org.bitbrothers.shoppi.ui.view.TextWatcherAdapter;
import org.bitbrothers.shoppi.ui.viewmodel.AllShoppingItemsViewModel;
import org.bitbrothers.shoppi.ui.widget.BetterEditText;

public class AllShoppingItemsFragment
        extends BaseFragment<AllShoppingItemsViewModel>
        implements AllShoppingItemsViewModel.View, AlertDialogFragment.OnSingleItemSelectedListener {

    private static final String TAG_SHOPPING_ITEM_OPTIONS = "shopping_item_options_tag";
    private static final String KEY_SHOPPING_ITEM_ID = "shopping_item_id";

    private ViewGroup addShoppingItemContainer;
    private BetterEditText addShoppingItemEditText;

    private final Observable.OnPropertyChangedCallback onAddContainerVisibilityPropertyChanged = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            if (viewModel.addContainerVisible.get()) {
                addShoppingItemContainer.setTranslationY(0);
                addShoppingItemEditText.requestFocus();
            }
        }
    };

    private final MainActivity.OnBackPressedListener onBackPressedListener = () -> {
        if (viewModel.addContainerVisible.get() && getUserVisibleHint()) {
            hideAddShoppingItemContainer();
            return true;
        }
        return false;
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && isResumed()) {
            hideAddShoppingItemContainer();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity) getActivity()).addOnBackPressedListener(onBackPressedListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, ShoppiApplication.from(getContext()).getViewModelFactory()).get(AllShoppingItemsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentAllShoppingItemsBinding binding = FragmentAllShoppingItemsBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);

        View rootView = binding.getRoot();

        AllShoppingItemsAdapter shoppingItemsAdapter = new AllShoppingItemsAdapter(new AllShoppingItemsAdapter.Callback() {
            @Override
            public void onLongClick(ShoppingItem shoppingItem) {
                showShoppingItemOptionsDialog(shoppingItem);
            }

            @Override
            public void onClick(ShoppingItem shoppingItem) {
                if (shoppingItem.isBought()) {
                    viewModel.unmarkBought(shoppingItem);
                } else {
                    viewModel.markBought(shoppingItem);
                }
            }
        });

        shoppingItemsAdapter.setShoppingItems(viewModel.shoppingItems);
        viewModel.shoppingItems.addOnListChangedCallback(new WeakOnListChangedCallbackRecyclerViewAdapter(shoppingItemsAdapter));

        CategoriesSpinnerAdapter categoriesSpinnerAdapter = new CategoriesSpinnerAdapter();
        categoriesSpinnerAdapter.setCategories(viewModel.categories);
        viewModel.categories.addOnListChangedCallback(new WeakOnListChangedCallbackBaseAdapter(categoriesSpinnerAdapter));

        addShoppingItemContainer = rootView.findViewById(R.id.add_container);
        Spinner addShoppingItemSpinner = rootView.findViewById(R.id.add_shopping_item_spinner);
        addShoppingItemEditText = rootView.findViewById(R.id.add_shopping_item_edit);
        Button addShoppingItemButton = rootView.findViewById(R.id.add_shopping_item_button);

        addShoppingItemSpinner.setAdapter(categoriesSpinnerAdapter);

        Runnable addShoppingItemAndReset = () -> {
            viewModel.addShoppingItem();
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
            hideAddShoppingItemContainer();
        });

        addShoppingItemButton.setOnClickListener(v -> {
            addShoppingItemAndReset.run();
        });

        viewModel.addContainerVisible.addOnPropertyChangedCallback(onAddContainerVisibilityPropertyChanged);

        FloatingActionButton fab = rootView.findViewById(R.id.fab);
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
    public void onResume() {
        super.onResume();
        AlertDialogFragment shoppingItemOptionsFragment = (AlertDialogFragment) getFragmentManager().findFragmentByTag(TAG_SHOPPING_ITEM_OPTIONS);
        if (shoppingItemOptionsFragment != null) {
            shoppingItemOptionsFragment.setOnSingleItemSelectedListener(this);
        }
    }

    @Override
    public void onDestroyView() {
        viewModel.addContainerVisible.removeOnPropertyChangedCallback(onAddContainerVisibilityPropertyChanged);
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        ((MainActivity) getActivity()).removeOnBackPressedListener(onBackPressedListener);
        super.onDetach();
    }

    @Override
    public void onSingleItemSelected(Bundle custom, int itemPosition) {
        long shoppingItemId = custom.getLong(KEY_SHOPPING_ITEM_ID);

        switch (itemPosition) {
            case 0:
                EditShoppingItemDialogFragment.newInstance(shoppingItemId).show(getFragmentManager(), null);
                break;
            case 1:
                viewModel.deleteShoppingItem(shoppingItemId);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    private void showShoppingItemOptionsDialog(ShoppingItem shoppingItem) {
        Bundle customBundle = new Bundle();

        customBundle.putLong(KEY_SHOPPING_ITEM_ID, shoppingItem.getId());
        AlertDialogFragment.Builder builder = new AlertDialogFragment.Builder(getActivity());

        builder.setCancelable(true);
        builder.setTitle(shoppingItem.getName());
        builder.setItems(R.array.category_options);
        builder.setCustom(customBundle);

        AlertDialogFragment fragment = builder.create();
        fragment.setOnSingleItemSelectedListener(this);
        fragment.show(getFragmentManager(), TAG_SHOPPING_ITEM_OPTIONS);
    }

    private void hideAddShoppingItemContainer() {
        addShoppingItemContainer.animate().translationY(-addShoppingItemContainer.getHeight()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                viewModel.addContainerVisible.set(false);
            }
        }).start();
    }

}
