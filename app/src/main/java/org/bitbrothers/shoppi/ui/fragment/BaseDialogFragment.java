package org.bitbrothers.shoppi.ui.fragment;

import android.support.v7.app.AppCompatDialogFragment;

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
}
