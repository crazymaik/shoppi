package org.bitbrothers.shoppi;

import android.app.Application;
import android.content.Context;

import org.bitbrothers.shoppi.presenter.AddShoppingItemPresenter;
import org.bitbrothers.shoppi.presenter.BasePresenter;
import org.bitbrothers.shoppi.store.SQLiteOpenHelper;
import org.bitbrothers.shoppi.store.SQLiteShoppingItemRepository;
import org.bitbrothers.shoppi.store.ShoppingItemRepository;

import java.util.HashMap;
import java.util.Map;

public class ShoppiApplication extends Application {

    private ShoppingItemRepository shoppingItemRepository;
    private Map<String, BasePresenter> presenterCache;

    public static ShoppiApplication from(Context context) {
        return (ShoppiApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SQLiteOpenHelper sqliteOpenHelper = new SQLiteOpenHelper(this, "main");
        shoppingItemRepository = new SQLiteShoppingItemRepository(sqliteOpenHelper);

        presenterCache = new HashMap<>();
    }

    public ShoppingItemRepository getShoppingItemRepository() {
        return shoppingItemRepository;
    }

    public <T extends BasePresenter> T getPresenterFromCache(String cacheKey) {
        return (T) presenterCache.remove(cacheKey);
    }

    public void cachePresenter(String cacheKey, BasePresenter presenter) {
        presenterCache.put(cacheKey, presenter);
    }

    public AddShoppingItemPresenter getAddShoppingItemPresenter() {
        return new AddShoppingItemPresenter(shoppingItemRepository);
    }
}
