package org.bitbrothers.shoppi.ui.viewmodel;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.bitbrothers.shoppi.logging.NullLogger;
import org.bitbrothers.shoppi.model.Category;
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

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(AndroidJUnit4.class)
public class EditShoppingItemViewModelInstrumentedTest {

    @Rule
    public final DatabaseRule databaseRule = new DatabaseRule();

    private CategoryRepository categoryRepository;
    private ShoppingItemRepository shoppingItemRepository;
    private EditShoppingItemViewModel viewModel;
    private EditShoppingItemViewModel.View view;

    @Before
    public void setup() {
        categoryRepository = new SQLiteCategoryRepository(databaseRule.getSQLiteOpenHelper());
        shoppingItemRepository = new SQLiteShoppingItemRepository(databaseRule.getSQLiteOpenHelper());
        viewModel = new EditShoppingItemViewModel(InstrumentationRegistry.getTargetContext(), new NullLogger(), shoppingItemRepository, categoryRepository);
        viewModel.setSchedulers(ImmediateThinScheduler.INSTANCE, ImmediateThinScheduler.INSTANCE);
        view = mock(EditShoppingItemViewModel.View.class);
    }

    @Test
    public void setEditMode_shoppingItemWithCategory_shouldSetNameFieldAndSelectedCategory() {
        Category otherCategory = categoryRepository.create(new Category("a", 0x12341234)).test().values().get(0);
        Category expectedCategory = categoryRepository.create(new Category("b", 0x12341234)).test().values().get(0);
        ShoppingItem shoppingItem = shoppingItemRepository.create(new ShoppingItem("apples", expectedCategory)).test().values().get(0);

        assertThat(viewModel.formFieldsEnabled.get(), is(false));
        assertThat(viewModel.saveButtonEnabled.get(), is(false));

        viewModel.setEditMode(shoppingItem.getId());

        assertThat(viewModel.formFieldsEnabled.get(), is(true));
        assertThat(viewModel.saveButtonEnabled.get(), is(true));
        assertThat(viewModel.shoppingItemName.get(), is("apples"));
        assertThat(viewModel.shoppingItemCategoryPosition.get(), greaterThan(0));
    }

    @Test
    public void setEditMode_errorRetrievingShoppingItem_shouldNotAllowEditingOrSaving() {
        viewModel.setEditMode(1);

        assertThat(viewModel.formFieldsEnabled.get(), is(false));
        assertThat(viewModel.saveButtonEnabled.get(), is(false));
        assertThat(viewModel.close.get(), is(true));
    }

    @Test
    public void save_validShoppingItem_shouldCloseView() {
        ShoppingItem shoppingItem = shoppingItemRepository.create(new ShoppingItem("apples")).test().values().get(0);

        viewModel.setEditMode(shoppingItem.getId());
        viewModel.save();

        assertThat(viewModel.formFieldsEnabled.get(), is(false));
        assertThat(viewModel.saveButtonEnabled.get(), is(false));
        assertThat(viewModel.close.get(), is(true));
    }
}
