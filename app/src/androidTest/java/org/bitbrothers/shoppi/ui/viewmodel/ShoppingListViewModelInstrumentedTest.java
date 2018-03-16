package org.bitbrothers.shoppi.ui.viewmodel;


import android.support.test.runner.AndroidJUnit4;

import org.bitbrothers.shoppi.logging.NullLogger;
import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.store.CategoryRepository;
import org.bitbrothers.shoppi.store.SQLiteCategoryRepository;
import org.bitbrothers.shoppi.store.SQLiteShoppingItemRepository;
import org.bitbrothers.shoppi.store.ShoppingItemRepository;
import org.bitbrothers.shoppi.support.DatabaseRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.internal.schedulers.ImmediateThinScheduler;
import io.reactivex.observers.TestObserver;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(AndroidJUnit4.class)
public class ShoppingListViewModelInstrumentedTest {

    @Rule
    public final DatabaseRule databaseRule = new DatabaseRule();

    private CategoryRepository categoryRepository;
    private ShoppingItemRepository shoppingItemRepository;
    private ShoppingListViewModel viewModel;
    private ShoppingListViewModel.View view;

    @Before
    public void setup() {
        categoryRepository = new SQLiteCategoryRepository(databaseRule.getSQLiteOpenHelper());
        shoppingItemRepository = new SQLiteShoppingItemRepository(databaseRule.getSQLiteOpenHelper());
        viewModel = new ShoppingListViewModel(new NullLogger(), shoppingItemRepository, categoryRepository);
        viewModel.setSchedulers(ImmediateThinScheduler.INSTANCE, ImmediateThinScheduler.INSTANCE);
        view = mock(ShoppingListViewModel.View.class);
    }

    @Test
    public void attach_shouldRefreshShoppingItems() throws InterruptedException {
        shoppingItemRepository.create(new ShoppingItem("a")).test().assertComplete().assertNoErrors();

        assertThat(viewModel.shoppingItems.size(), is(0));
        assertThat(viewModel.showEmptyListMessage.get(), is(false));

        viewModel.attach(view);

        assertThat(viewModel.shoppingItems.size(), is(1));
        assertThat(viewModel.showEmptyListMessage.get(), is(false));
    }

    @Test
    public void attach_emptyRepository_shouldShowEmptyMessage() {
        viewModel.attach(view);

        assertThat(viewModel.shoppingItems.size(), is(0));
        assertThat(viewModel.showEmptyListMessage.get(), is(true));
    }

    @Test
    public void markBought_oneItemInList_shouldRefreshShoppingItemsAndEmptyMessage() {
        TestObserver<ShoppingItem> createObserver = shoppingItemRepository.create(new ShoppingItem("a")).test();
        ShoppingItem shoppingItem = createObserver.values().get(0);

        viewModel.attach(view);

        assertThat(viewModel.shoppingItems.size(), is(1));
        assertThat(viewModel.showEmptyListMessage.get(), is(false));

        viewModel.markBought(shoppingItem);

        assertThat(viewModel.shoppingItems.size(), is(0));
        assertThat(viewModel.showEmptyListMessage.get(), is(true));
    }
}
