package org.bitbrothers.shoppi.presenter;

import io.reactivex.disposables.CompositeDisposable;

public class BasePresenter<View extends BasePresenter.BaseView> {

    public interface BaseView {
    }

    protected abstract class State {

        protected abstract void onAttach();

        protected abstract void apply();
    }

    protected abstract class SimpleState extends State {

        @Override
        protected void onAttach() {
            apply();
        }
    }

    protected View view;
    protected State state;
    protected CompositeDisposable disposables;

    public BasePresenter() {
        this.disposables = new CompositeDisposable();
    }

    public void init() {
    }

    public void attach(View view) {
        this.view = view;
        this.state.onAttach();
    }

    public void detach() {
        this.view = null;
    }

    public void destroy() {
        disposables.dispose();
    }

    protected void transition(State newState) {
        this.state = newState;
        if (view != null) {
            this.state.apply();
        }
    }
}
