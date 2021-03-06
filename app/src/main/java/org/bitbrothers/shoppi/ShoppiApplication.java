package org.bitbrothers.shoppi;

import android.app.Application;
import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.bitbrothers.shoppi.logging.Logger;
import org.bitbrothers.shoppi.logging.ProdLogger;
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
    private Logger logger;

    public static ShoppiApplication from(Context context) {
        return (ShoppiApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        analytics.setAnalyticsCollectionEnabled(BuildConfig.FEATURE_ANALYTICS);

        logger = new ProdLogger(analytics);

        SQLiteOpenHelper sqliteOpenHelper = new SQLiteOpenHelper(this, "main");
        shoppingItemRepository = new SQLiteShoppingItemRepository(sqliteOpenHelper);
        categoryRepository = new SQLiteCategoryRepository(sqliteOpenHelper);

        viewModelFactory = new DependencyAwareViewModelFactory(this);
    }

    public Logger getLogger() {
        return logger;
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
