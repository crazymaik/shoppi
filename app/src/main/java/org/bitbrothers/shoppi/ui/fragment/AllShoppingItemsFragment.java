package org.bitbrothers.shoppi.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.ShoppiApplication;
import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.presenter.AllShoppingItemsPresenter;
import org.bitbrothers.shoppi.ui.adapter.ShoppingItemsAdapter;

import java.util.List;

public class AllShoppingItemsFragment
        extends BaseFragment<AllShoppingItemsPresenter>
        implements AllShoppingItemsPresenter.View {

    private ShoppingItemsAdapter shoppingItemsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shoppingItemsAdapter = new ShoppingItemsAdapter(getLayoutInflater());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_shopping_items, container, false);

        ListView listView = view.findViewById(android.R.id.list);
        listView.setAdapter(shoppingItemsAdapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            ShoppingItem shoppingItem = shoppingItemsAdapter.getItem(position);
            if (shoppingItem.isBought()) {
                presenter.unmarkBought(shoppingItem);
            } else {
                presenter.markBought(shoppingItem);
            }
        });

        return view;
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
    protected AllShoppingItemsPresenter createPresenter() {
        return ShoppiApplication.from(getContext()).getAllShoppingItemsPresenter();
    }

}
