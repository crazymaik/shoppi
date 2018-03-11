package org.bitbrothers.shoppi;

import android.app.Application;
import android.content.Context;

import org.bitbrothers.shoppi.store.CategoryRepository;
import org.bitbrothers.shoppi.store.SQLiteCategoryRepository;
import org.bitbrothers.shoppi.store.SQLiteOpenHelper;
import org.bitbrothers.shoppi.store.SQLiteShoppingItemRepository;
import org.bitbrothers.shoppi.store.ShoppingItemRepository;
import org.bitbrothers.shoppi.ui.DependencyAwareViewModelFactory;

public class ShoppiApplication extends Application {

    private CategoryRepository categoryRepository;
    private ShoppingItemRepository shoppingItemRepository;
    private DependencyAwareViewModelFactory viewModelFactory;

    public static ShoppiApplication from(Context context) {
        return (ShoppiApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SQLiteOpenHelper sqliteOpenHelper = new SQLiteOpenHelper(this, "main");
        shoppingItemRepository = new SQLiteShoppingItemRepository(sqliteOpenHelper);
        categoryRepository = new SQLiteCategoryRepository(sqliteOpenHelper);

        viewModelFactory = new DependencyAwareViewModelFactory(this);
    }

    public ShoppingItemRepository getShoppingItemRepository() {
        return shoppingItemRepository;
    }

    public CategoryRepository getCategoryRepository() {
        return categoryRepository;
    }

    public DependencyAwareViewModelFactory getViewModelFactory() {
        return viewModelFactory;
    }
}
