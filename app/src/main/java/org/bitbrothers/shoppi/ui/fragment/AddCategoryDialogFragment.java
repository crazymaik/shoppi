package org.bitbrothers.shoppi.ui.fragment;


import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import org.bitbrothers.shoppi.R;
import org.bitbrothers.shoppi.ShoppiApplication;
import org.bitbrothers.shoppi.databinding.FragmentAddCategoryBinding;
import org.bitbrothers.shoppi.ui.adapter.CategoryColorsAdapter;
import org.bitbrothers.shoppi.ui.viewmodel.AddCategoryViewModel;

public class AddCategoryDialogFragment
        extends BaseDialogFragment<AddCategoryViewModel>
        implements AddCategoryViewModel.View {

    public static final String KEY_CATEGORY_ID = "category_id";

    public static AddCategoryDialogFragment newInstance() {
        return new AddCategoryDialogFragment();
    }

    public static AddCategoryDialogFragment newInstance(long categoryId) {
        Bundle args = new Bundle();
        args.putLong(KEY_CATEGORY_ID, categoryId);
        AddCategoryDialogFragment fragment = new AddCategoryDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private CategoryColorsAdapter categoryColorsAdapter;
    private RecyclerView colorsView;
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

    private final Observable.OnPropertyChangedCallback selectedColorPositionPropertyChanged = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            categoryColorsAdapter.setSelectedColorPosition(viewModel.selectedColorPosition.get());
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        viewModel = ViewModelProviders.of(this, ShoppiApplication.from(getContext()).getViewModelFactory()).get(AddCategoryViewModel.class);

        if (savedInstanceState == null) {
            if (getArguments() != null && getArguments().containsKey(KEY_CATEGORY_ID)) {
                viewModel.setEditMode(getArguments().getLong(KEY_CATEGORY_ID));
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        FragmentAddCategoryBinding binding = FragmentAddCategoryBinding.inflate(getActivity().getLayoutInflater());
        View rootView = binding.getRoot();

        builder.setTitle(viewModel.isEditMode() ? R.string.add_category_title_edit : R.string.add_category_title_add);
        builder.setPositiveButton(R.string.save, null);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setView(rootView);

        categoryColorsAdapter = new CategoryColorsAdapter(position -> viewModel.selectedColorPosition.set(position));
        categoryColorsAdapter.setColors(viewModel.colorValues);
        categoryColorsAdapter.setSelectedColorPosition(viewModel.selectedColorPosition.get());

        colorsView = rootView.findViewById(R.id.add_category_colors);
        colorsView.setHasFixedSize(true);
        colorsView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        colorsView.setAdapter(categoryColorsAdapter);

        binding.setVm(viewModel);

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

        viewModel.selectedColorPosition.addOnPropertyChangedCallback(selectedColorPositionPropertyChanged);
        viewModel.saveButtonEnabled.addOnPropertyChangedCallback(saveButtonEnabledPropertyChanged);
        viewModel.close.addOnPropertyChangedCallback(closePropertyChanged);

        if (viewModel.close.get()) {
            dismiss();
        }
    }

    @Override
    public void onStop() {
        viewModel.selectedColorPosition.removeOnPropertyChangedCallback(selectedColorPositionPropertyChanged);
        viewModel.saveButtonEnabled.removeOnPropertyChangedCallback(saveButtonEnabledPropertyChanged);
        viewModel.close.removeOnPropertyChangedCallback(closePropertyChanged);
        super.onStop();
    }

    @Override
    public void makeColorItemVisible(int position) {
        colorsView.getLayoutManager().scrollToPosition(position);
    }
}
