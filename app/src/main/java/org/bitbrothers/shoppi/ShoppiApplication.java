package org.bitbrothers.shoppi;

import android.app.Application;
import android.content.Context;

import org.bitbrothers.shoppi.presenter.AllCategoriesPresenter;
import org.bitbrothers.shoppi.presenter.AllShoppingItemsPresenter;
import org.bitbrothers.shoppi.presenter.BasePresenter;
import org.bitbrothers.shoppi.presenter.MainPresenter;
import org.bitbrothers.shoppi.presenter.ShoppingListPresenter;
import org.bitbrothers.shoppi.store.CategoryRepository;
import org.bitbrothers.shoppi.store.SQLiteCategoryRepository;
import org.bitbrothers.shoppi.store.SQLiteOpenHelper;
import org.bitbrothers.shoppi.store.SQLiteShoppingItemRepository;
import org.bitbrothers.shoppi.store.ShoppingItemRepository;
import org.bitbrothers.shoppi.ui.DependencyAwareViewModelFactory;

import java.util.HashMap;
import java.util.Map;

public class ShoppiApplication extends Application {

    private CategoryRepository categoryRepository;
    private ShoppingItemRepository shoppingItemRepository;
    private DependencyAwareViewModelFactory viewModelFactory;
    private Map<String, BasePresenter> presenterCache;

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

        presenterCache = new HashMap<>();
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

    public <T extends BasePresenter> T getPresenterFromCache(String cacheKey) {
        return (T) presenterCache.remove(cacheKey);
    }

    public void cachePresenter(String cacheKey, BasePresenter presenter) {
        presenterCache.put(cacheKey, presenter);
    }

    public AllShoppingItemsPresenter getAllShoppingItemsPresenter() {
        return new AllShoppingItemsPresenter(shoppingItemRepository, categoryRepository);
    }

    public MainPresenter getMainPresenter() {
        return new MainPresenter();
    }

    public ShoppingListPresenter getShoppingListPresenter() {
        return new ShoppingListPresenter(shoppingItemRepository, categoryRepository);
    }

    public AllCategoriesPresenter getAllCategoriesPresenter() {
        return new AllCategoriesPresenter(categoryRepository);
    }
}
