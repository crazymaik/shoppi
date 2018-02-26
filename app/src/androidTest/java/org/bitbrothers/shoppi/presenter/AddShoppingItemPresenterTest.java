package org.bitbrothers.shoppi.presenter;

import android.support.test.InstrumentationRegistry;

import org.bitbrothers.shoppi.store.SQLiteShoppingItemRepository;
import org.bitbrothers.shoppi.store.ShoppingItemRepository;
import org.bitbrothers.shoppi.support.DatabaseRule;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

public class AddShoppingItemPresenterTest {

    @Rule
    public final DatabaseRule databaseRule = new DatabaseRule();

    private ShoppingItemRepository shoppingItemRepository;
    private AddShoppingItemPresenter presenter;

    @Before
    public void setUp() {
        shoppingItemRepository = new SQLiteShoppingItemRepository(databaseRule.getSQLiteOpenHelper());
        presenter = new AddShoppingItemPresenter(InstrumentationRegistry.getTargetContext(), shoppingItemRepository);
    }

    @Test
    public void initialViewSetup() {
        presenter.init();
        AddShoppingItemPresenter.View view = mock(AddShoppingItemPresenter.View.class);
        presenter.attach(view);
        verify(view).setAddButtonEnabled(false);
        verify(view).setNameFieldEnabled(true);
    }

    @Test
    public void textInNameField_shouldControlAddButtonEnabledState() {
        presenter.init();
        AddShoppingItemPresenter.View view = mock(AddShoppingItemPresenter.View.class);
        presenter.attach(view);
        reset(view);
        presenter.setName("a");
        verify(view).setAddButtonEnabled(true);
        reset(view);
        presenter.setName("");
        verify(view).setAddButtonEnabled(false);
    }

    @Test
    @Ignore
    public void saveShoppingItem_shouldCreateItemInRepository() {
        presenter.init();
        AddShoppingItemPresenter.View view = mock(AddShoppingItemPresenter.View.class);
        presenter.attach(view);
        presenter.setName("a");
        presenter.save();
        assertThat(shoppingItemRepository.getAll().test().values().size(), is(1));
    }
}
