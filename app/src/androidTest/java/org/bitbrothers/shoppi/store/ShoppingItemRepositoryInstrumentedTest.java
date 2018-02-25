package org.bitbrothers.shoppi.store;

import android.support.test.runner.AndroidJUnit4;

import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.support.DatabaseRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ShoppingItemRepositoryInstrumentedTest {

    @Rule
    public final DatabaseRule databaseRule = new DatabaseRule();

    @Test
    public void createAndGet_shouldReturnCreatedShoppingItem() {
        ShoppingItemRepository repository = new SQLiteShoppingItemRepository(databaseRule.getSQLiteOpenHelper());

        ShoppingItem createdItem = repository.create(new ShoppingItem("a"));
        assertNotNull(createdItem.getId());

        ShoppingItem retrievedItem = repository.get(createdItem.getId());
        assertThat(retrievedItem.getName(), is("a"));
        assertThat(retrievedItem, is(createdItem));
    }

    @Test
    public void getAll_shouldReturnPreviouslyCreatedItems() {
        ShoppingItemRepository repository = new SQLiteShoppingItemRepository(databaseRule.getSQLiteOpenHelper());

        repository.create(new ShoppingItem("a"));
        repository.create(new ShoppingItem("b"));

        List<ShoppingItem> items = repository.getAll();
        assertThat(items.size(), is(2));
    }
}
