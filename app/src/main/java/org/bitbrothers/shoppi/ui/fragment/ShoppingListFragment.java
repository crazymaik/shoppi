package org.bitbrothers.shoppi.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.ShoppiApplication;
import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.ui.adapter.ShoppingListAdapter;
import org.bitbrothers.shoppi.ui.adapter.WeakOnListChangedCallback;
import org.bitbrothers.shoppi.ui.viewmodel.ShoppingListViewModel;

public class ShoppingListFragment extends Fragment {

    private ShoppingListViewModel viewModel;
    private ShoppingListAdapter shoppingListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, ShoppiApplication.from(getContext()).getViewModelFactory()).get(ShoppingListViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        shoppingListAdapter = new ShoppingListAdapter(new ShoppingListAdapter.Callback() {
            @Override
            public void markBought(ShoppingItem shoppingItem) {
                viewModel.markBought(shoppingItem);
            }
        });

        viewModel.shoppingItems.addOnListChangedCallback(new WeakOnListChangedCallback(shoppingListAdapter));
        shoppingListAdapter.setShoppingItems(viewModel.shoppingItems);

        RecyclerView listView = view.findViewById(android.R.id.list);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setAdapter(shoppingListAdapter);

        return view;
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
}
