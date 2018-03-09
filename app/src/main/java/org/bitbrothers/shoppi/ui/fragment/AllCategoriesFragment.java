package org.bitbrothers.shoppi.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.ShoppiApplication;
import org.bitbrothers.shoppi.model.Category;
import org.bitbrothers.shoppi.presenter.AllCategoriesPresenter;
import org.bitbrothers.shoppi.ui.adapter.AllCategoriesAdapter;

import java.util.List;

public class AllCategoriesFragment
        extends BaseFragment<AllCategoriesPresenter>
        implements AllCategoriesPresenter.View {

    private AllCategoriesAdapter categoriesAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoriesAdapter = new AllCategoriesAdapter(new AllCategoriesAdapter.Callback() {
            @Override
            public void deleteCategory(Category category) {
                presenter.deleteCategory(category);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_categories, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            AddCategoryDialogFragment.newInstance().show(getFragmentManager(), null);
        });

        RecyclerView recyclerView = view.findViewById(android.R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(categoriesAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void showCategories(List<Category> categories) {
        categoriesAdapter.setCategories(categories);
    }

    @Override
    public void removeCategory(long id) {
        categoriesAdapter.removeCategory(id);
    }

    @Override
    protected AllCategoriesPresenter createPresenter() {
        return ShoppiApplication.from(getContext()).getAllCategoriesPresenter();
    }
}
