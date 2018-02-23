package org.bitbrothers.shoppi.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.model.ShoppingItem;

import java.util.ArrayList;
import java.util.List;

public class ShoppingItemAdapter extends BaseAdapter {

    private final List<ShoppingItem> items;
    private final LayoutInflater layoutInflater;

    public ShoppingItemAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;

        this.items = new ArrayList<ShoppingItem>() {{
            add(new ShoppingItem("Apples"));
            add(new ShoppingItem("Bananas"));
            add(new ShoppingItem("Chips"));
            add(new ShoppingItem("Water"));
            add(new ShoppingItem("Milk"));
            add(new ShoppingItem("Gummy Bears"));
            add(new ShoppingItem("Tomatoes"));
            add(new ShoppingItem("Peppers"));
            add(new ShoppingItem("Salad"));
            add(new ShoppingItem("Crackers"));
        }};
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public ShoppingItem getItem(int position) {
        return items.get(position);
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
}
