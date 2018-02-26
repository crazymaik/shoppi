package org.bitbrothers.shoppi.presenter;

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

    public BasePresenter() {

    }

    public void attach(View view) {
        this.view = view;
        this.state.onAttach();
    }

    public void detach(boolean isFinishing) {
        // TODO set up caching when isFinishing is false
        this.view = null;
    }

    protected void transition(State newState) {
        this.state = newState;
        if (view != null) {
            this.state.apply();
        }
    }
}
