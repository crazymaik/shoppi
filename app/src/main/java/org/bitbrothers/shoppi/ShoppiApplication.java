package org.bitbrothers.shoppi;

import android.app.Application;
import android.content.Context;

import org.bitbrothers.shoppi.presenter.AddShoppingItemPresenter;
import org.bitbrothers.shoppi.store.SQLiteOpenHelper;
import org.bitbrothers.shoppi.store.SQLiteShoppingItemRepository;
import org.bitbrothers.shoppi.store.ShoppingItemRepository;

public class ShoppiApplication extends Application {

    private ShoppingItemRepository shoppingItemRepository;
    private AddShoppingItemPresenter addShoppingItemPresenter;

    public static ShoppiApplication from(Context context) {
        return (ShoppiApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SQLiteOpenHelper sqliteOpenHelper = new SQLiteOpenHelper(this, "main");
        shoppingItemRepository = new SQLiteShoppingItemRepository(sqliteOpenHelper);
        addShoppingItemPresenter = new AddShoppingItemPresenter(this, shoppingItemRepository);
    }

    public ShoppingItemRepository getShoppingItemRepository() {
        return shoppingItemRepository;
    }

    public AddShoppingItemPresenter getAddShoppingItemPresenter() {
        return addShoppingItemPresenter;
    }
}
