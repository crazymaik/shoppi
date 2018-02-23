package org.bitbrothers.shoppi.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.ShoppiApplication;
import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.store.ShoppingItemRepository;
import org.bitbrothers.shoppi.ui.adapter.ShoppingItemAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ShoppingItemRepository repository = ShoppiApplication.from(this).getShoppingItemRepository();
        List<ShoppingItem> shoppingItems = repository.getAll();

        ShoppingItemAdapter adapter = new ShoppingItemAdapter(getLayoutInflater());
        adapter.setShoppingItems(shoppingItems);

        ListView listView = findViewById(android.R.id.list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
