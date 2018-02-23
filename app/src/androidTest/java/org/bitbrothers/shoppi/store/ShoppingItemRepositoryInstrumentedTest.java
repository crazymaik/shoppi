package org.bitbrothers.shoppi.store;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.bitbrothers.shoppi.model.ShoppingItem;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class ShoppingItemRepositoryInstrumentedTest {

    @Test
    public void createAndGet_shouldReturnCreatedShoppingItem() {
        SQLiteOpenHelper helper = getSqLiteOpenHelper();
        ShoppingItemRepository repository = new SQLiteShoppingItemRepository(helper);

        ShoppingItem createdItem = repository.create(new ShoppingItem("a"));
        assertNotNull(createdItem.getId());

        ShoppingItem retrievedItem = repository.get(createdItem.getId());
        assertEquals("a", retrievedItem.getName());
        assertEquals(createdItem, retrievedItem);
    }

    @Test
    public void getAll_shouldReturnPreviouslyCreatedItems() {
        SQLiteOpenHelper helper = getSqLiteOpenHelper();
        ShoppingItemRepository repository = new SQLiteShoppingItemRepository(helper);

        repository.create(new ShoppingItem("a"));
        repository.create(new ShoppingItem("b"));

        List<ShoppingItem> items = repository.getAll();
        assertEquals(2, items.size());

        helper.close();
    }

    @NonNull
    private SQLiteOpenHelper getSqLiteOpenHelper() {
        Context context = InstrumentationRegistry.getTargetContext();
        context.deleteDatabase("testdb");
        return new SQLiteOpenHelper(context, "testdb", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE shopping_items ( id integer primary key, name next not null);");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            }
        };
    }
}
