package org.bitbrothers.shoppi.ui.fragment;

import android.support.v7.app.AppCompatDialogFragment;
import android.widget.Toast;

import org.bitbrothers.shoppi.ui.viewmodel.BaseViewModel;

public class BaseDialogFragment<ViewModel extends BaseViewModel> extends AppCompatDialogFragment implements BaseViewModel.BaseView {

    protected ViewModel viewModel;

    @Override
    public void onStart() {
        super.onStart();
        viewModel.attach(this);
    }

    @Override
    public void onStop() {
        viewModel.detach();
        super.onStop();
    }

    @Override
    public void showErrorToast(int messageResId) {
        Toast.makeText(getContext(), messageResId, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showErrorToast(int formatMessageResId, Object... args) {
        Toast.makeText(getContext(), getString(formatMessageResId, args), Toast.LENGTH_LONG).show();
    }
}
