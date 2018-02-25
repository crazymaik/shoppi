package org.bitbrothers.shoppi.store;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.bitbrothers.shoppi.model.ShoppingItem;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ShoppingItemRepositoryInstrumentedTest {

    @Test
    public void createAndGet_shouldReturnCreatedShoppingItem() {
        SQLiteOpenHelper helper = getSqLiteOpenHelper();
        ShoppingItemRepository repository = new SQLiteShoppingItemRepository(helper);

        ShoppingItem createdItem = repository.create(new ShoppingItem("a"));
        assertNotNull(createdItem.getId());

        ShoppingItem retrievedItem = repository.get(createdItem.getId());
        assertThat(retrievedItem.getName(), is("a"));
        assertThat(retrievedItem, is(createdItem));
    }

    @Test
    public void getAll_shouldReturnPreviouslyCreatedItems() {
        SQLiteOpenHelper helper = getSqLiteOpenHelper();
        ShoppingItemRepository repository = new SQLiteShoppingItemRepository(helper);

        repository.create(new ShoppingItem("a"));
        repository.create(new ShoppingItem("b"));

        List<ShoppingItem> items = repository.getAll();
        assertThat(items.size(), is(2));

        helper.close();
    }

    @NonNull
    private SQLiteOpenHelper getSqLiteOpenHelper() {
        Context context = InstrumentationRegistry.getTargetContext();
        context.deleteDatabase("testdb");
        return new SQLiteOpenHelper(context, "testdb");
    }
}
