package org.bitbrothers.shoppi.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.model.Category;
import org.bitbrothers.shoppi.ui.widget.CheckedColorView;

import java.util.ArrayList;
import java.util.List;

public class AllCategoriesAdapter extends RecyclerView.Adapter<AllCategoriesAdapter.ViewHolder> {

    public interface Callback {

        void onCategoryLongClicked(Category category);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameField;
        private final CheckedColorView colorField;

        public ViewHolder(View itemView) {
            super(itemView);
            this.nameField = itemView.findViewById(R.id.category_item_name);
            this.colorField = itemView.findViewById(R.id.category_item_color);
            this.colorField.setFilled(true);

            itemView.setOnLongClickListener(v -> {
                callback.onCategoryLongClicked(categories.get(getAdapterPosition()));
                return true;
            });
        }
    }

    private final Callback callback;
    private List<Category> categories;

    public AllCategoriesAdapter(Callback callback) {
        this.callback = callback;
        this.categories = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.li_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.nameField.setText(category.getName());
        holder.colorField.setColor(category.getColor());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }
}
