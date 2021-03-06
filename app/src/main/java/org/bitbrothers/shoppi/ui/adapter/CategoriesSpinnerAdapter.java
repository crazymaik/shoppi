package org.bitbrothers.shoppi.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.model.Category;
import org.bitbrothers.shoppi.ui.widget.CheckedColorView;

import java.util.ArrayList;
import java.util.List;

public class CategoriesSpinnerAdapter extends BaseAdapter {

    public static class ViewHolder {

        private final TextView nameField;
        private final CheckedColorView colorField;

        public ViewHolder(View itemView) {
            this.nameField = itemView.findViewById(R.id.category_item_name);
            this.colorField = itemView.findViewById(R.id.category_item_color);
            this.colorField.setFilled(true);
        }
    }

    private List<Category> categories;

    public CategoriesSpinnerAdapter() {
        this.categories = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Category getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return categories.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_category_item, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        Category category = categories.get(position);
        vh.nameField.setText(category.getName());
        vh.colorField.setColor(category.getColor());

        return convertView;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }
}
