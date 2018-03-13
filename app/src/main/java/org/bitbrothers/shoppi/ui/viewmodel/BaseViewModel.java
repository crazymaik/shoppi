package org.bitbrothers.shoppi.ui.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.StringRes;

import org.bitbrothers.shoppi.logging.Logger;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class BaseViewModel<ViewType extends BaseViewModel.BaseView> extends ViewModel {

    public interface BaseView {

        void showErrorToast(@StringRes int messageResId);

        void showErrorToast(@StringRes int formatMessageResId, Object... args);
    }

    private final List<Consumer<ViewType>> onViewActions = new ArrayList<>();
    protected final Logger logger;
    private boolean isCleared;
    private CompositeDisposable viewDisposables;
    private ViewType view;

    public BaseViewModel(Logger logger) {
        this.logger = logger;
    }

    public void attach(ViewType view) {
        this.view = view;
        viewDisposables = new CompositeDisposable();
        for (Consumer<ViewType> action : onViewActions) {
            runAction(action);
        }
    }

    public void detach() {
        viewDisposables.dispose();
        viewDisposables = null;
        view = null;
    }

    @Override
    protected void onCleared() {
        isCleared = true;
        onViewActions.clear();
    }

    /**
     * Adds the given Disposable to the list of disposables disposed when {@link #detach()} is called.
     *
     * @param disposable The Disposable to dispose on detach
     */
    protected void addViewDisposable(Disposable disposable) {
        if (isCleared) {
            return;
        }

        this.viewDisposables.add(disposable);
    }

    /**
     * Calls the consumer with the current view instance, if view is not null.
     * Otherwise saves and executes the consumer the next time the view attaches.
     *
     * @param action The consumer to execute with the current or next view.
     */
    protected void withView(Consumer<ViewType> action) {
        if (isCleared) {
            return;
        }

        if (Looper.getMainLooper() != Looper.myLooper()) {
            logError("with_view_wrong_looper");
        }

        if (view != null) {
            runAction(action);
        } else {
            onViewActions.add(action);
        }
    }

    private void runAction(Consumer<ViewType> action) {
        try {
            action.accept(view);
        } catch (Exception e) {
            logError("run_action", e);
        }
    }

    protected void logError(String name) {
        Bundle params = new Bundle();
        params.putString("class", getClass().getSimpleName());
        logger.logError(name, params);
    }

    protected void logError(String name, Throwable ex) {
        Bundle params = new Bundle();
        params.putString("class", getClass().getSimpleName());
        logger.logError(name, params, ex);
    }
}
