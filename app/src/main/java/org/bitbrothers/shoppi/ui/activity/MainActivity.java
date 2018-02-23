package org.bitbrothers.shoppi.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.ui.adapter.ShoppingItemAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(android.R.id.list);
        listView.setAdapter(new ShoppingItemAdapter(getLayoutInflater()));
    }
}
