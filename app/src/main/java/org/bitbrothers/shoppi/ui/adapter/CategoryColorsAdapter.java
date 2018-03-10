package org.bitbrothers.shoppi.ui.adapter;


import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.bitbrothers.shoppi.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryColorsAdapter extends RecyclerView.Adapter<CategoryColorsAdapter.ViewHolder> {

    public interface Callback {

        void onColorSelected(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.category_color);
            itemView.setOnClickListener(v -> switchSelectedTo(getAdapterPosition()));
        }
    }

    private final Callback callback;
    private List<Integer> colors;
    private int selectedPosition;

    public CategoryColorsAdapter(Callback callback) {
        this.callback = callback;
        this.colors = new ArrayList<>();
        this.selectedPosition = 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.li_category_color_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imageView.setImageDrawable(new ColorDrawable(colors.get(position)));
        holder.itemView.setSelected(position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    public void setColors(List<Integer> colors) {
        this.colors = colors;
        notifyDataSetChanged();
    }

    public void setSelectedColorPosition(int position) {
        if (this.selectedPosition != position) {
            this.selectedPosition = position;
            notifyDataSetChanged();
        }
    }

    private void switchSelectedTo(int position) {
        if (position != selectedPosition) {
            notifyItemChanged(selectedPosition);
            selectedPosition = position;
            notifyItemChanged(position);
            callback.onColorSelected(selectedPosition);
        }
    }
}
