package org.bitbrothers.shoppi.ui.adapter;

import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;

import java.lang.ref.WeakReference;

public class WeakOnListChangedCallbackRecyclerViewAdapter extends ObservableList.OnListChangedCallback<ObservableList> {

    private final WeakReference<RecyclerView.Adapter> adapter;

    public WeakOnListChangedCallbackRecyclerViewAdapter(RecyclerView.Adapter<?> adapter) {
        this.adapter = new WeakReference<>(adapter);
    }

    @Override
    public void onChanged(ObservableList sender) {
        RecyclerView.Adapter<?> adapter = this.adapter.get();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {
        RecyclerView.Adapter<?> adapter = this.adapter.get();
        if (adapter != null) {
            adapter.notifyItemRangeChanged(positionStart, itemCount);
        }
    }

    @Override
    public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
        RecyclerView.Adapter<?> adapter = this.adapter.get();
        if (adapter != null) {
            adapter.notifyItemRangeInserted(positionStart, itemCount);
        }
    }

    @Override
    public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {
        RecyclerView.Adapter<?> adapter = this.adapter.get();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {
        RecyclerView.Adapter<?> adapter = this.adapter.get();
        if (adapter != null) {
            adapter.notifyItemRangeRemoved(positionStart, itemCount);
        }
    }
}
