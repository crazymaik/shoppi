package org.bitbrothers.shoppi.ui.fragment;

import org.bitbrothers.shoppi.ShoppiApplication;
import org.bitbrothers.shoppi.presenter.AllShoppingItemsPresenter;

public class AllShoppingItemsFragment
        extends BaseFragment<AllShoppingItemsPresenter>
        implements AllShoppingItemsPresenter.View {

    @Override
    protected AllShoppingItemsPresenter createPresenter() {
        return ShoppiApplication.from(getContext()).getAllShoppingItemsPresenter();
    }
}
