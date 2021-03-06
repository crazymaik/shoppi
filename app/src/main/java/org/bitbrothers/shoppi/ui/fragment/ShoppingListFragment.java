package org.bitbrothers.shoppi.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.ShoppiApplication;
import org.bitbrothers.shoppi.databinding.FragmentShoppingListBinding;
import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.ui.adapter.ShoppingListAdapter;
import org.bitbrothers.shoppi.ui.adapter.WeakOnListChangedCallbackRecyclerViewAdapter;
import org.bitbrothers.shoppi.ui.viewmodel.ShoppingListViewModel;

public class ShoppingListFragment
        extends BaseFragment<ShoppingListViewModel>
        implements ShoppingListViewModel.View {

    private ShoppingListAdapter shoppingListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, ShoppiApplication.from(getContext()).getViewModelFactory()).get(ShoppingListViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentShoppingListBinding binding = FragmentShoppingListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.setVm(viewModel);

        shoppingListAdapter = new ShoppingListAdapter(getActivity(), new ShoppingListAdapter.Callback() {
            @Override
            public void markBought(ShoppingItem shoppingItem) {
                viewModel.markBought(shoppingItem);
            }
        });

        viewModel.shoppingItems.addOnListChangedCallback(new WeakOnListChangedCallbackRecyclerViewAdapter(shoppingListAdapter));
        shoppingListAdapter.setShoppingItems(viewModel.shoppingItems);

        RecyclerView listView = view.findViewById(android.R.id.list);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setAdapter(shoppingListAdapter);

        return view;
    }

    @Override
    public void showMarkBoughtFailed(ShoppingItem shoppingItem) {
        Toast.makeText(getActivity(), getString(R.string.shopping_list_error_mark_bought, shoppingItem.getName()), Toast.LENGTH_LONG).show();
    }
}
