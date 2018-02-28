package org.bitbrothers.shoppi.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.model.ShoppingItem;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

    public interface Callback {

        void markBought(ShoppingItem shoppingItem);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameField;

        public ViewHolder(View itemView) {
            super(itemView);

            this.nameField = itemView.findViewById(android.R.id.text1);

            itemView.setOnLongClickListener(v -> {
                callback.markBought(shoppingItems.get(getAdapterPosition()));
                return true;
            });
        }
    }

    private final Callback callback;
    private List<ShoppingItem> shoppingItems;

    public ShoppingListAdapter(Callback callback) {
        this.callback = callback;
        shoppingItems = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.li_shopping_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ShoppingItem shoppingItem = shoppingItems.get(position);
        holder.nameField.setText(shoppingItem.getName());
    }

    @Override
    public int getItemCount() {
        return shoppingItems.size();
    }

    public void setShoppingItems(List<ShoppingItem> shoppingItems) {
        this.shoppingItems = shoppingItems;
        notifyDataSetChanged();
    }

    public void removeShoppingItem(ShoppingItem shoppingItem) {
        int position = findPosition(shoppingItem);
        if (position != -1) {
            shoppingItems.remove(position);
            this.notifyItemRemoved(position);
        }
    }

    private int findPosition(ShoppingItem shoppingItem) {
        for (int i = 0; i < shoppingItems.size(); ++i) {
            if (shoppingItems.get(i).getId() == shoppingItem.getId()) {
                return i;
            }
        }
        return -1;
    }

}
