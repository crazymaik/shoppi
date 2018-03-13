package org.bitbrothers.shoppi.ui.fragment;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.ShoppiApplication;
import org.bitbrothers.shoppi.databinding.FragmentEditShoppingItemBinding;
import org.bitbrothers.shoppi.ui.adapter.CategoriesSpinnerAdapter;
import org.bitbrothers.shoppi.ui.adapter.WeakOnListChangedCallbackBaseAdapter;
import org.bitbrothers.shoppi.ui.viewmodel.EditShoppingItemViewModel;

public class EditShoppingItemDialogFragment extends BaseDialogFragment<EditShoppingItemViewModel> {

    private static final String KEY_SHOPPING_ITEM_ID = "shopping_item_id";

    private Button positiveButton;
    private Button negativeButton;

    private final Observable.OnPropertyChangedCallback closePropertyChanged = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            if (viewModel.close.get()) {
                dismiss();
            }
        }
    };

    private final Observable.OnPropertyChangedCallback saveButtonEnabledPropertyChanged = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            if (positiveButton != null) {
                positiveButton.setEnabled(viewModel.saveButtonEnabled.get());
            }
        }
    };

    public static EditShoppingItemDialogFragment newInstance(long shoppingItemId) {
        Bundle args = new Bundle();
        args.putLong(KEY_SHOPPING_ITEM_ID, shoppingItemId);
        EditShoppingItemDialogFragment fragment = new EditShoppingItemDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        viewModel = ViewModelProviders.of(this, ShoppiApplication.from(getContext()).getViewModelFactory()).get(EditShoppingItemViewModel.class);
        if (savedInstanceState == null) {
            viewModel.setEditMode(getArguments().getLong(KEY_SHOPPING_ITEM_ID));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        FragmentEditShoppingItemBinding binding = FragmentEditShoppingItemBinding.inflate(getActivity().getLayoutInflater(), null, false);
        View rootView = binding.getRoot();

        builder.setTitle(R.string.edit_shopping_item_title);
        builder.setPositiveButton(R.string.save, null);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setView(rootView);

        binding.setVm(viewModel);

        CategoriesSpinnerAdapter categoriesSpinnerAdapter = new CategoriesSpinnerAdapter();
        categoriesSpinnerAdapter.setCategories(viewModel.categories);
        viewModel.categories.addOnListChangedCallback(new WeakOnListChangedCallbackBaseAdapter(categoriesSpinnerAdapter));

        Spinner editShoppingItemSpinner = rootView.findViewById(R.id.edit_shopping_item_spinner);
        editShoppingItemSpinner.setAdapter(categoriesSpinnerAdapter);

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog d = (AlertDialog) getDialog();
        positiveButton = d.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(viewModel.saveButtonEnabled.get());
        positiveButton.setOnClickListener(v -> viewModel.save());
        negativeButton = d.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setOnClickListener(v -> viewModel.cancel());

        viewModel.saveButtonEnabled.addOnPropertyChangedCallback(saveButtonEnabledPropertyChanged);
        viewModel.close.addOnPropertyChangedCallback(closePropertyChanged);

        if (viewModel.close.get()) {
            dismiss();
        }
    }

    @Override
    public void onStop() {
        viewModel.saveButtonEnabled.removeOnPropertyChangedCallback(saveButtonEnabledPropertyChanged);
        viewModel.close.removeOnPropertyChangedCallback(closePropertyChanged);
        super.onStop();
    }
}
