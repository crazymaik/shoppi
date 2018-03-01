package org.bitbrothers.shoppi.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.ShoppiApplication;
import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.presenter.AllShoppingItemsPresenter;
import org.bitbrothers.shoppi.ui.adapter.AllShoppingItemsAdapter;

import java.util.List;

public class AllShoppingItemsFragment
        extends BaseFragment<AllShoppingItemsPresenter>
        implements AllShoppingItemsPresenter.View {

    private AllShoppingItemsAdapter shoppingItemsAdapter;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_shopping_items, container, false);

        RecyclerView recyclerView = view.findViewById(android.R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(shoppingItemsAdapter);

        final FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> AddShoppingItemDialogFragment.newInstance().show(getFragmentManager(), null));

        return view;
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
    protected AllShoppingItemsPresenter createPresenter() {
        return ShoppiApplication.from(getContext()).getAllShoppingItemsPresenter();
    }

}
