package org.bitbrothers.shoppi.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.bitbrothers.shoppi.ShoppiApplication;
import org.bitbrothers.shoppi.presenter.BasePresenter;

import java.util.UUID;

public abstract class BaseAppCompatActivity<Presenter extends BasePresenter>
        extends AppCompatActivity {

    public static final String KEY_PRESENTER_CACHE = "KEY_PRESENTER_CACHE";

    protected Presenter presenter;
    private String presenterCacheKey;
    private boolean destroyedBySystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            presenterCacheKey = savedInstanceState.getString(KEY_PRESENTER_CACHE);
        }
        if (presenterCacheKey == null) {
            presenterCacheKey = UUID.randomUUID().toString();
        }
        presenter = ShoppiApplication.from(this).getPresenterFromCache(presenterCacheKey);
        if (presenter == null || savedInstanceState == null) {
            presenter = createPresenter();
            presenter.init();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.attach((BasePresenter.BaseView) this);
    }

    @Override
    protected void onPause() {
        presenter.detach();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_PRESENTER_CACHE, presenterCacheKey);
        destroyedBySystem = true;
    }

    @Override
    protected void onDestroy() {
        if (destroyedBySystem) {
            ShoppiApplication.from(this).cachePresenter(presenterCacheKey, presenter);
        } else {
            presenter.destroy();
        }
        presenter = null;
        super.onDestroy();
    }

    protected abstract Presenter createPresenter();
}
