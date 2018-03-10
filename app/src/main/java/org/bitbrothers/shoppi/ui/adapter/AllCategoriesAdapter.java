package org.bitbrothers.shoppi.ui.adapter;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.model.Category;

import java.util.ArrayList;
import java.util.List;

public class AllCategoriesAdapter extends RecyclerView.Adapter<AllCategoriesAdapter.ViewHolder> {

    public interface Callback {

        void onCategoryLongClicked(Category category);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView colorField;
        private final TextView nameField;

        public ViewHolder(View itemView) {
            super(itemView);
            this.colorField = itemView.findViewById(R.id.category_item_color);
            this.nameField = itemView.findViewById(R.id.category_item_name);

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
        holder.colorField.setImageDrawable(new ColorDrawable(category.getColor()));
        holder.nameField.setText(category.getName());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    public void removeCategory(long id) {
        int position = findPosition(id);
        if (position != -1) {
            categories.remove(position);
            this.notifyItemRemoved(position);
        }
    }

    private int findPosition(long id) {
        for (int i = 0; i < categories.size(); ++i) {
            if (categories.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }
}
