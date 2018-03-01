package org.bitbrothers.shoppi.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.ShoppiApplication;
import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.presenter.ShoppingListPresenter;
import org.bitbrothers.shoppi.ui.adapter.ShoppingListAdapter;

import java.util.List;

public class ShoppingListFragment
        extends BaseFragment<ShoppingListPresenter>
        implements ShoppingListPresenter.View {

    private ShoppingListAdapter shoppingListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shoppingListAdapter = new ShoppingListAdapter(new ShoppingListAdapter.Callback() {
            @Override
            public void markBought(ShoppingItem shoppingItem) {
                presenter.markBought(shoppingItem);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        RecyclerView listView = view.findViewById(android.R.id.list);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setAdapter(shoppingListAdapter);

        return view;
    }

    @Override
    public void showShoppingItems(List<ShoppingItem> shoppingItems) {
        shoppingListAdapter.setShoppingItems(shoppingItems);
    }

    @Override
    public void removeShoppingItem(long id) {
        shoppingListAdapter.removeShoppingItem(id);
    }

    @Override
    protected ShoppingListPresenter createPresenter() {
        return ShoppiApplication.from(getContext()).getShoppingListPresenter();
    }
}
