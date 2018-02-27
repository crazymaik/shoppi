package org.bitbrothers.shoppi.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.ShoppiApplication;
import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.presenter.ShoppingListPresenter;
import org.bitbrothers.shoppi.ui.adapter.ShoppingItemsAdapter;

import java.util.List;

public class ShoppingListFragment
        extends BaseFragment<ShoppingListPresenter>
        implements ShoppingListPresenter.View {

    private ShoppingItemsAdapter shoppingItemsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shoppingItemsAdapter = new ShoppingItemsAdapter(getLayoutInflater());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        ListView listView = view.findViewById(android.R.id.list);
        listView.setAdapter(shoppingItemsAdapter);

        final FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> AddShoppingItemDialogFragment.newInstance().show(getFragmentManager(), null));

        return view;
    }

    @Override
    public void showShoppingItems(List<ShoppingItem> shoppingItems) {
        shoppingItemsAdapter.setShoppingItems(shoppingItems);
    }

    @Override
    protected ShoppingListPresenter createPresenter() {
        return ShoppiApplication.from(getContext()).getShoppingListPresenter();
    }
}
