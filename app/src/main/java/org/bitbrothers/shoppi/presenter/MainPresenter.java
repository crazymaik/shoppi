package org.bitbrothers.shoppi.presenter;

import org.bitbrothers.shoppi.model.ShoppingItem;
import org.bitbrothers.shoppi.store.ShoppingItemRepository;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter extends BasePresenter<MainPresenter.View> {

    public interface View extends BasePresenter.BaseView {

        void showShoppingItems(List<ShoppingItem> shoppingItems);
    }

    private final ShoppingItemRepository shoppingItemRepository;
    private Disposable getAllDisposable;

    public MainPresenter(ShoppingItemRepository shoppingItemRepository) {
        this.shoppingItemRepository = shoppingItemRepository;
    }

    @Override
    public void attach(View view) {
        super.attach(view);
        retrieveShoppingItems();
    }

    @Override
    public void detach() {
        if (getAllDisposable != null) {
            getAllDisposable.dispose();
            getAllDisposable = null;
        }
        super.detach();
    }

    private void retrieveShoppingItems() {
        getAllDisposable = shoppingItemRepository.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shoppingItems -> {
                    view.showShoppingItems(shoppingItems);
                }, error -> {
                    // TODO
                });
    }
}
