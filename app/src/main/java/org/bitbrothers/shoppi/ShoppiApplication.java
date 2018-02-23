package org.bitbrothers.shoppi;

import android.app.Application;
import android.content.Context;

import org.bitbrothers.shoppi.store.SQLiteOpenHelper;
import org.bitbrothers.shoppi.store.SQLiteShoppingItemRepository;
import org.bitbrothers.shoppi.store.ShoppingItemRepository;

public class ShoppiApplication extends Application {

    private ShoppingItemRepository shoppingItemRepository;

    public static ShoppiApplication from(Context context) {
        return (ShoppiApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SQLiteOpenHelper sqliteOpenHelper = new SQLiteOpenHelper(this, "main");
        shoppingItemRepository = new SQLiteShoppingItemRepository(sqliteOpenHelper);
    }

    public ShoppingItemRepository getShoppingItemRepository() {
        return shoppingItemRepository;
    }
}
