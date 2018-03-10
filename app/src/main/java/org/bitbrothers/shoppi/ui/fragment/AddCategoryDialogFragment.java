package org.bitbrothers.shoppi.ui.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.ShoppiApplication;
import org.bitbrothers.shoppi.presenter.AddCategoryPresenter;
import org.bitbrothers.shoppi.ui.adapter.CategoryColorsAdapter;
import org.bitbrothers.shoppi.ui.view.TextWatcherAdapter;
import org.bitbrothers.shoppi.ui.widget.BetterEditText;

import java.util.List;

public class AddCategoryDialogFragment
        extends BaseDialogFragment<AddCategoryPresenter>
        implements AddCategoryPresenter.View {

    public static AddCategoryDialogFragment newInstance() {
        return new AddCategoryDialogFragment();
    }

    public static AddCategoryDialogFragment newInstance(long categoryId) {
        return new AddCategoryDialogFragment();
    }

    private CategoryColorsAdapter categoryColorsAdapter;
    private RecyclerView colorsView;
    private BetterEditText nameField;
    private Button positiveButton;
    private Button negativeButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        categoryColorsAdapter = new CategoryColorsAdapter(new CategoryColorsAdapter.Callback() {
            @Override
            public void onColorSelected(int color) {
                presenter.setColor(color);
            }
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Add Category");
        builder.setPositiveButton("Save", null);
        builder.setNegativeButton("Cancel", null);

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_category, null);

        builder.setView(view);

        nameField = view.findViewById(R.id.add_category_edit);
        nameField.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                presenter.setName(s.toString());
            }
        });

        colorsView = view.findViewById(R.id.add_category_colors);
        colorsView.setHasFixedSize(true);
        colorsView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        colorsView.setAdapter(categoryColorsAdapter);

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog d = (AlertDialog) getDialog();
        positiveButton = d.getButton(AlertDialog.BUTTON_POSITIVE);
        negativeButton = d.getButton(AlertDialog.BUTTON_NEGATIVE);

        negativeButton.setOnClickListener(v -> presenter.cancel());

        positiveButton.setOnClickListener(v -> presenter.save());
    }

    @Override
    public void setColors(List<Integer> colors) {
        categoryColorsAdapter.setColors(colors);
    }

    @Override
    public void setSelectedColorPosition(int position) {
        categoryColorsAdapter.setSelectedColorPosition(position);
    }

    @Override
    public void setAddButtonEnabled(boolean enabled) {
        positiveButton.setEnabled(enabled);
    }

    @Override
    public void setFieldsEnabled(boolean enabled) {
        nameField.setEnabled(enabled);
        colorsView.setEnabled(enabled);
    }

    @Override
    public void setNameFieldText(String name) {
        if (!nameField.getText().equals(name)) {
            nameField.setText(name);
            nameField.setSelection(name.length());
        }
    }

    @Override
    public void showSaveFailedErrorMessage() {
        Toast.makeText(getActivity(), R.string.add_category_save_error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void close() {
        dismiss();
    }

    @Override
    protected AddCategoryPresenter createPresenter() {
        return ShoppiApplication.from(getContext()).getAddCategoryPresenter();
    }
}
