package org.bitbrothers.shoppi.ui.adapter;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.model.ShoppingItem;

import java.util.ArrayList;
import java.util.List;

public class AllShoppingItemsAdapter extends RecyclerView.Adapter<AllShoppingItemsAdapter.ViewHolder> {

    public interface Callback {

        void onClick(ShoppingItem shoppingItem);

        void onLongClick(ShoppingItem shoppingItem);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameField;
        private final ImageView colorField;

        public ViewHolder(View itemView) {
            super(itemView);
            this.nameField = itemView.findViewById(R.id.shopping_item_name);
            this.colorField = itemView.findViewById(R.id.shopping_item_color);

            itemView.setOnClickListener(v -> {
                callback.onClick(shoppingItems.get(getAdapterPosition()));
            });

            itemView.setOnLongClickListener(v -> {
                callback.onLongClick(shoppingItems.get(getAdapterPosition()));
                return true;
            });
        }
    }

    private final Callback callback;
    private List<ShoppingItem> shoppingItems;

    public AllShoppingItemsAdapter(Callback callback) {
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
        if (shoppingItem.getColor() != null) {
            holder.colorField.setImageDrawable(new ColorDrawable(shoppingItem.getColor()));
        } else {
            holder.colorField.setImageResource(R.drawable.unknown_category);
        }
        holder.nameField.setText(shoppingItem.getName());
        holder.nameField.setTextAppearance(shoppingItem.isBought() ? R.style.BoughtShoppingItemTextAppearance : R.style.TextAppearance_AppCompat_Medium);
    }

    @Override
    public int getItemCount() {
        return shoppingItems.size();
    }

    public void setShoppingItems(List<ShoppingItem> shoppingItems) {
        this.shoppingItems = shoppingItems;
        notifyDataSetChanged();
    }
}
