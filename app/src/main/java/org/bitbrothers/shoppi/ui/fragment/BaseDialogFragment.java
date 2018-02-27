package org.bitbrothers.shoppi.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;

import org.bitbrothers.shoppi.ShoppiApplication;
import org.bitbrothers.shoppi.presenter.BasePresenter;

import java.util.UUID;

public abstract class BaseDialogFragment<Presenter extends BasePresenter>
        extends AppCompatDialogFragment {

    public static final String KEY_PRESENTER_CACHE = "KEY_PRESENTER_CACHE";

    protected Presenter presenter;
    private String presenterCacheKey;
    private boolean destroyedBySystem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            presenterCacheKey = savedInstanceState.getString(KEY_PRESENTER_CACHE);
        }
        if (presenterCacheKey == null) {
            presenterCacheKey = UUID.randomUUID().toString();
        }
        presenter = ShoppiApplication.from(getContext()).getPresenterFromCache(presenterCacheKey);
        if (presenter == null || savedInstanceState == null) {
            presenter = createPresenter();
            presenter.init();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.attach((BasePresenter.BaseView) this);
    }

    @Override
    public void onPause() {
        presenter.detach();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_PRESENTER_CACHE, presenterCacheKey);
        destroyedBySystem = true;
    }

    @Override
    public void onDestroy() {
        if (destroyedBySystem) {
            ShoppiApplication.from(getContext()).cachePresenter(presenterCacheKey, presenter);
        } else {
            presenter.destroy();
        }
        presenter = null;
        super.onDestroy();
    }

    protected abstract Presenter createPresenter();
}
