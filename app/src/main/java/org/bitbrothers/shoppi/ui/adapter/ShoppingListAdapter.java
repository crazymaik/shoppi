package org.bitbrothers.shoppi.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.ui.view.Dimensions;
import org.bitbrothers.shoppi.ui.widget.CheckedColorView;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

    public interface Callback {

        void markBought(ShoppingItem shoppingItem);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameField;
        private final CheckedColorView colorField;

        public ViewHolder(View itemView) {
            super(itemView);
            this.nameField = itemView.findViewById(R.id.shopping_item_name);
            this.colorField = itemView.findViewById(R.id.shopping_item_color);
            this.colorField.setFilled(true);

            itemView.setOnLongClickListener(v -> {
                callback.markBought(shoppingItems.get(getAdapterPosition()));
                return true;
            });
        }
    }

    private final Callback callback;
    private List<ShoppingItem> shoppingItems;
    private final int strokeWidth;

    public ShoppingListAdapter(Context context, Callback callback) {
        this.callback = callback;
        this.shoppingItems = new ArrayList<>();
        this.strokeWidth = Dimensions.dpToPixels(context, 2);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.li_shopping_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ShoppingItem shoppingItem = shoppingItems.get(position);
        int color = shoppingItem.getColor() != null ? shoppingItem.getColor() : 0xff000000;

        holder.nameField.setText(shoppingItem.getName());
        holder.colorField.setColor(color);
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
