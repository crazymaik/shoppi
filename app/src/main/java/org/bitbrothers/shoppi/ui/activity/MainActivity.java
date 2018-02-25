package org.bitbrothers.shoppi.ui.activity;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.ShoppiApplication;
import org.bitbrothers.shoppi.store.ShoppingItemRepository;
import org.bitbrothers.shoppi.ui.adapter.ShoppingItemsAdapter;
import org.bitbrothers.shoppi.ui.fragment.AddShoppingItemDialogFragment;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ShoppingItemRepository repository;
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

        repository = ShoppiApplication.from(this).getShoppingItemRepository();

        setContentView(R.layout.activity_main);

        shoppingItemsAdapter = new ShoppingItemsAdapter(getLayoutInflater());

        ListView listView = findViewById(android.R.id.list);
        listView.setAdapter(shoppingItemsAdapter);

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> AddShoppingItemDialogFragment.newInstance().show(getSupportFragmentManager(), null));
    }

    @Override
    protected void onStart() {
        super.onStart();
        getAllShoppingItems();
    }

    @Override
    protected void onDestroy() {
        cancelGetAllShoppingItems();
        super.onDestroy();
    }

    private void getAllShoppingItems() {
        cancelGetAllShoppingItems();
        getAllDisposable = repository.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItems -> {
                    shoppingItemsAdapter.setShoppingItems(shoppingItems);
                }, error -> {
                    // TODO
                });
    }

    private void cancelGetAllShoppingItems() {
        if (getAllDisposable != null) {
            getAllDisposable.dispose();
            getAllDisposable = null;
        }
    }
}
