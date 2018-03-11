package org.bitbrothers.shoppi.ui.fragment;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.ShoppiApplication;
import org.bitbrothers.shoppi.model.Category;
import org.bitbrothers.shoppi.ui.adapter.AllCategoriesAdapter;
import org.bitbrothers.shoppi.ui.adapter.WeakOnListChangedCallbackRecyclerViewAdapter;
import org.bitbrothers.shoppi.ui.viewmodel.AllCategoriesViewModel;

public class AllCategoriesFragment
        extends Fragment
        implements AllCategoriesViewModel.View, AlertDialogFragment.OnSingleItemSelectedListener, AlertDialogFragment.OnButtonClickListener {

    private static final String TAG_CATEGORY_OPTIONS = "category_options_tag";
    private static final String KEY_CATEGORY_ID = "category_id";
    private static final String TAG_PROMPT_DELETE_CATEGORY = "prompt_delete_category_tag";

    private AllCategoriesAdapter categoriesAdapter;
    private AllCategoriesViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, ShoppiApplication.from(getContext()).getViewModelFactory()).get(AllCategoriesViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_categories, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            AddCategoryDialogFragment.newInstance().show(getFragmentManager(), null);
        });

        categoriesAdapter = new AllCategoriesAdapter(new AllCategoriesAdapter.Callback() {
            @Override
            public void onCategoryLongClicked(Category category) {
                showCategoryOptionsDialog(category);
            }
        });

        categoriesAdapter.setCategories(viewModel.categories);
        viewModel.categories.addOnListChangedCallback(new WeakOnListChangedCallbackRecyclerViewAdapter(categoriesAdapter));

        RecyclerView recyclerView = view.findViewById(android.R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(categoriesAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.attach(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        AlertDialogFragment categoryOptionsFragment = (AlertDialogFragment) getFragmentManager().findFragmentByTag(TAG_CATEGORY_OPTIONS);
        if (categoryOptionsFragment != null) {
            categoryOptionsFragment.setOnSingleItemSelectedListener(this);
        }
        AlertDialogFragment promptDeleteFragment = (AlertDialogFragment) getFragmentManager().findFragmentByTag(TAG_PROMPT_DELETE_CATEGORY);
        if (promptDeleteFragment != null) {
            promptDeleteFragment.setOnButtonClickListener(this);
        }
    }

    @Override
    public void onStop() {
        viewModel.detach();
        super.onStop();
    }

    @Override
    public void promptDeleteCategory(long categoryId, int itemCount) {
        AlertDialogFragment.Builder builder = new AlertDialogFragment.Builder(getActivity());
        builder.setMessage(getContext().getResources().getQuantityString(R.plurals.prompt_category_delete, itemCount, itemCount));
        builder.setPositiveButton(R.string.yes);
        builder.setNegativeButton(R.string.cancel);
        Bundle customBundle = new Bundle();
        customBundle.putLong(KEY_CATEGORY_ID, categoryId);
        builder.setCustom(customBundle);
        AlertDialogFragment fragment = builder.create();
        fragment.setOnButtonClickListener(this);
        fragment.show(getFragmentManager(), TAG_PROMPT_DELETE_CATEGORY);
    }

    @Override
    public void onSingleItemSelected(Bundle custom, int itemPosition) {
        long categoryId = custom.getLong(KEY_CATEGORY_ID);

        switch (itemPosition) {
            case 0:
                AddCategoryDialogFragment.newInstance(categoryId).show(getFragmentManager(), null);
                break;
            case 1:
                viewModel.safeDeleteCategory(categoryId);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void onButtonClick(AlertDialogFragment fragment, int whichButton) {
        switch (whichButton) {
            case AlertDialog.BUTTON_POSITIVE:
                long categoryId = fragment.getCustomBundle().getLong(KEY_CATEGORY_ID);
                viewModel.deleteCategory(categoryId);
                fragment.dismiss();
                break;
            case AlertDialog.BUTTON_NEGATIVE:
                fragment.dismiss();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    private void showCategoryOptionsDialog(Category category) {
        Bundle customBundle = new Bundle();
        customBundle.putLong(KEY_CATEGORY_ID, category.getId());
        AlertDialogFragment.Builder builder = new AlertDialogFragment.Builder(getActivity());

        builder.setCancelable(true);
        builder.setTitle(category.getName());
        builder.setItems(R.array.category_options);
        builder.setCustom(customBundle);

        AlertDialogFragment fragment = builder.create();
        fragment.setOnSingleItemSelectedListener(this);
        fragment.show(getFragmentManager(), TAG_CATEGORY_OPTIONS);
    }
}
