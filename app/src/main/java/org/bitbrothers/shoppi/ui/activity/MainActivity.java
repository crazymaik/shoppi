package org.bitbrothers.shoppi.ui.activity;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.widget.ListView;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.ShoppiApplication;
import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.presenter.BasePresenter;
import org.bitbrothers.shoppi.presenter.MainPresenter;
import org.bitbrothers.shoppi.ui.adapter.ShoppingItemsAdapter;
import org.bitbrothers.shoppi.ui.fragment.AddShoppingItemDialogFragment;

import java.util.List;

import io.reactivex.disposables.Disposable;

public class MainActivity
        extends BaseAppCompatActivity
        implements MainPresenter.View {

    private ShoppingItemsAdapter shoppingItemsAdapter;
    private Disposable getAllDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectAll()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());

        setContentView(R.layout.activity_main);

        shoppingItemsAdapter = new ShoppingItemsAdapter(getLayoutInflater());

        ListView listView = findViewById(android.R.id.list);
        listView.setAdapter(shoppingItemsAdapter);

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> AddShoppingItemDialogFragment.newInstance().show(getSupportFragmentManager(), null));
    }

    @Override
    protected BasePresenter createPresenter() {
        return ShoppiApplication.from(this).getMainPresenter();
    }

    @Override
    public void showShoppingItems(List<ShoppingItem> shoppingItems) {
        shoppingItemsAdapter.setShoppingItems(shoppingItems);
    }
}
