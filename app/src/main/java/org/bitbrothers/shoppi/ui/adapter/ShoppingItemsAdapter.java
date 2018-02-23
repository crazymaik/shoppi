package org.bitbrothers.shoppi.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.model.ShoppingItem;

import java.util.Collections;
import java.util.List;

public class ShoppingItemsAdapter extends BaseAdapter {

    private final LayoutInflater layoutInflater;
    private List<ShoppingItem> shoppingItems;

    public ShoppingItemsAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
        this.shoppingItems = Collections.emptyList();
    }

    @Override
    public int getCount() {
        return shoppingItems.size();
    }

    @Override
    public ShoppingItem getItem(int position) {
        return shoppingItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;

        if (convertView == null) {
            listItem = layoutInflater.inflate(R.layout.li_shopping_item, parent, false);
        }

        TextView textView = listItem.findViewById(android.R.id.text1);
        textView.setText(getItem(position).getName());

        return listItem;
    }

    public void setShoppingItems(List<ShoppingItem> shoppingItems) {
        this.shoppingItems = shoppingItems;
        notifyDataSetChanged();
    }
}
