package org.bitbrothers.shoppi.store;


import android.support.test.runner.AndroidJUnit4;

import org.bitbrothers.shoppi.model.Category;
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
public class CategoryRepositoryInstrumentedTest {

    @Rule
    public final DatabaseRule databaseRule = new DatabaseRule();

    @Test
    public void createAndGet_shouldReturnCreatedCategory() {
        CategoryRepository repository = new SQLiteCategoryRepository(databaseRule.getSQLiteOpenHelper());

        TestObserver<Category> createObserver = repository.create(new Category("a", 0)).test();
        assertThat(createObserver.valueCount(), is(1));

        Category createdCategory = createObserver.values().get(0);
        assertThat(createdCategory.getId(), notNullValue());
        assertThat(createdCategory.getName(), is("a"));

        Category retrievedCategory = repository.get(createdCategory.getId()).test().values().get(0);
        assertThat(retrievedCategory.getName(), is("a"));
        assertThat(retrievedCategory, is(createdCategory));
    }

    @Test
    public void getAll_shouldReturnPreviouslyCreatedCategories() throws InterruptedException {
        CategoryRepository repository = new SQLiteCategoryRepository(databaseRule.getSQLiteOpenHelper());

        repository.create(new Category("a", 0)).test();
        repository.create(new Category("b", 0)).test();

        List<Category> items = repository.getAll().test().values().get(0);
        assertThat(items.size(), is(2));
    }
}
