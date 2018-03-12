package org.bitbrothers.shoppi.ui.fragment;

import android.support.v4.app.Fragment;

import org.bitbrothers.shoppi.ui.viewmodel.BaseViewModel;

public class BaseFragment<ViewModel extends BaseViewModel> extends Fragment implements BaseViewModel.BaseView {

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
