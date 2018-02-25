package org.bitbrothers.shoppi.store;

import android.support.test.runner.AndroidJUnit4;

import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.support.DatabaseRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.reactivex.observers.TestObserver;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ShoppingItemRepositoryInstrumentedTest {

    @Rule
    public final DatabaseRule databaseRule = new DatabaseRule();

    @Test
    public void createAndGet_shouldReturnCreatedShoppingItem() {
        ShoppingItemRepository repository = new SQLiteShoppingItemRepository(databaseRule.getSQLiteOpenHelper());

        TestObserver<ShoppingItem> createObserver = repository.create(new ShoppingItem("a")).test();
        assertThat(createObserver.valueCount(), is(1));

        ShoppingItem createdItem = createObserver.values().get(0);
        assertThat(createdItem.getId(), notNullValue());

        ShoppingItem retrievedItem = repository.get(createdItem.getId()).test().values().get(0);
        assertThat(retrievedItem.getName(), is("a"));
        assertThat(retrievedItem, is(createdItem));
    }

    @Test
    public void getAll_shouldReturnPreviouslyCreatedItems() throws InterruptedException {
        ShoppingItemRepository repository = new SQLiteShoppingItemRepository(databaseRule.getSQLiteOpenHelper());

        repository.create(new ShoppingItem("a")).test();
        repository.create(new ShoppingItem("b")).test();

        List<ShoppingItem> items = repository.getAll().test().values().get(0);
        assertThat(items.size(), is(2));
    }
}
