package org.bitbrothers.shoppi.ui.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.ShoppiApplication;
import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.store.ShoppingItemRepository;
import org.bitbrothers.shoppi.ui.adapter.ShoppingItemsAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ShoppingItemsAdapter shoppingItemsAdapter;
    private GetAllShoppingItems getAllShoppingItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        shoppingItemsAdapter = new ShoppingItemsAdapter(getLayoutInflater());

        ListView listView = findViewById(android.R.id.list);
        listView.setAdapter(shoppingItemsAdapter);

        ShoppingItemRepository repository = ShoppiApplication.from(this).getShoppingItemRepository();

        getAllShoppingItems = new GetAllShoppingItems(repository, new GetAllShoppingItems.Callback() {
            @Override
            public void onResult(List<ShoppingItem> shoppingItems) {
                shoppingItemsAdapter.setShoppingItems(shoppingItems);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getAllShoppingItems.execute();
    }

    @Override
    protected void onDestroy() {
        getAllShoppingItems.destroy();
        super.onDestroy();
    }

    private static class GetAllShoppingItems extends AsyncTask<Void, Void, List<ShoppingItem>> {

        private final ShoppingItemRepository repository;
        private Callback callback;

        interface Callback {
            void onResult(List<ShoppingItem> shoppingItems);
        }

        public GetAllShoppingItems(ShoppingItemRepository repository, Callback callback) {
            this.repository = repository;
            this.callback = callback;
        }

        protected void destroy() {
            callback = null;
            cancel(false);
        }

        @Override
        protected List<ShoppingItem> doInBackground(Void... voids) {
            return repository.getAll();
        }

        @Override
        protected void onCancelled() {
            callback = null;
        }

        @Override
        protected void onPostExecute(List<ShoppingItem> shoppingItems) {
            callback.onResult(shoppingItems);
        }
    }
}
