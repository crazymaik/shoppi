package org.bitbrothers.shoppi.ui.viewmodel;

import android.arch.lifecycle.ViewModel;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BaseViewModel extends ViewModel {

    private CompositeDisposable viewDisposables;

    public void attach() {
        viewDisposables = new CompositeDisposable();
    }

    public void detach() {
        viewDisposables.dispose();
        viewDisposables = null;
    }

    /**
     * Adds the given Disposable to the list of disposables disposed when {@link #detach()} is called.
     *
     * @param disposable The Disposable to dispose on detach
     */
    protected void addViewDisposable(Disposable disposable) {
        this.viewDisposables.add(disposable);
    }
}
