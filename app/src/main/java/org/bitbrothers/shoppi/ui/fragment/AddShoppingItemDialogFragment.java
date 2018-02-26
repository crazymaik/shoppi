package org.bitbrothers.shoppi.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.ShoppiApplication;
import org.bitbrothers.shoppi.presenter.AddShoppingItemPresenter;

public class AddShoppingItemDialogFragment
        extends BaseDialogFragment<AddShoppingItemPresenter>
        implements AddShoppingItemPresenter.View {

    public static AddShoppingItemDialogFragment newInstance() {
        final AddShoppingItemDialogFragment fragment = new AddShoppingItemDialogFragment();
        return fragment;
    }

    private EditText nameField;
    private Button positiveButton;
    private Button negativeButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(R.string.add_shopping_item_title);
        builder.setPositiveButton(R.string.add_shopping_item_positive_button, null);
        builder.setNegativeButton(R.string.add_shopping_item_negative_button, null);

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_shopping_item, null);

        builder.setView(view);

        nameField = view.findViewById(android.R.id.edit);
        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This gets called twice nested due to the way 2 way binding is handled here
                presenter.setName(s.toString());
            }
        });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        configureButtonBehavior((AlertDialog) getDialog());
    }

    @Override
    public void setAddButtonEnabled(boolean enabled) {
        positiveButton.setEnabled(enabled);
    }

    @Override
    public void setNameFieldEnabled(boolean enabled) {
        nameField.setEnabled(enabled);
    }

    @Override
    public void setNameFieldText(String text) {
        if (!nameField.getText().equals(text)) {
            nameField.setText(text);
            nameField.setSelection(text.length());
        }
    }

    @Override
    public void close() {
        dismiss();
    }

    @Override
    protected AddShoppingItemPresenter createPresenter() {
        return ShoppiApplication.from(getContext()).getAddShoppingItemPresenter();
    }

    private void configureButtonBehavior(final AlertDialog dialog) {
        positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setOnClickListener(v -> presenter.save());

        negativeButton.setOnClickListener(v -> presenter.cancel());
    }
}
