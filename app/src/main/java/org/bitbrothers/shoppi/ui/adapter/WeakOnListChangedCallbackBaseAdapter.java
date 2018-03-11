package org.bitbrothers.shoppi.ui.adapter;

import android.databinding.ObservableList;
import android.widget.BaseAdapter;

import java.lang.ref.WeakReference;

public class WeakOnListChangedCallbackBaseAdapter extends ObservableList.OnListChangedCallback<ObservableList> {

    private final WeakReference<BaseAdapter> adapter;

    public WeakOnListChangedCallbackBaseAdapter(BaseAdapter adapter) {
        this.adapter = new WeakReference<>(adapter);
    }

    @Override
    public void onChanged(ObservableList sender) {
        notifyDataSetChanged();
    }

    @Override
    public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {
        notifyDataSetChanged();
    }

    @Override
    public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
        notifyDataSetChanged();
    }

    @Override
    public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {
        notifyDataSetChanged();
    }

    @Override
    public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {
        notifyDataSetChanged();
    }

    private void notifyDataSetChanged() {
        BaseAdapter adapter = this.adapter.get();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
